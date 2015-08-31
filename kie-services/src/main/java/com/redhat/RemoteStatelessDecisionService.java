package com.redhat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.internal.runtime.helper.BatchExecutionHelper;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.ServiceResponse.ResponseType;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class RemoteStatelessDecisionService implements StatelessDecisionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteStatelessDecisionService.class);

	private String httpUrl;
	private String containerId;
	private String userName;
	private String password;
	private int timeout = 0;

	private KieCommands commandFactory;
	private KieServicesClient client;
	private XStream xstream;

	private boolean inited = false;

	public RemoteStatelessDecisionService(String httpUrl, String userName, String password, int timeout, String containerId) {
		this.httpUrl = httpUrl;
		this.userName = userName;
		this.password = password;
		this.timeout = timeout;
		this.containerId = containerId;
		init();
	}

	/**
	 * Only use the default constructor with spring. be sure to use the
	 * init-method
	 */
	public RemoteStatelessDecisionService() {
	}

	public void init() {
		KieServicesConfiguration config;
		if (timeout == 0) {
			config = KieServicesFactory.newRestConfiguration(this.httpUrl, this.userName, this.password);
		} else {
			config = KieServicesFactory.newRestConfiguration(this.httpUrl, this.userName, this.password, this.timeout);
		}
		this.client = KieServicesFactory.newKieServicesClient(config);
		commandFactory = KieServices.Factory.get().getCommands();
		xstream = BatchExecutionHelper.newXStreamMarshaller();

		inited = true;
	}

	@Override
	public <Response> Response runRules(Collection<Object> facts, String processId, Class<Response> responseClazz) {
		if (!inited) {
			init();
		}

		String payload = getPayload(facts, processId, responseClazz);

		LOGGER.info(String.format("Remote BRMS request to %s/%s with below payload: \n %s", httpUrl, containerId, payload));

		ServiceResponse<String> reply = client.executeCommands(containerId, payload);
		if (reply.getType().equals(ResponseType.FAILURE)) {
			LOGGER.error(reply.toString());
			throw new RuntimeException(reply.getMsg());
		}

		LOGGER.info(String.format("Response from decision server:\n %s", reply.getResult()));

		ExecutionResults results = (ExecutionResults) xstream.fromXML(reply.getResult());

		Response response = ReflectiveExecutionResultsTransformer.transform(results, responseClazz);

		return response;
	}

	public BatchExecutionCommand createBatchExecutionCommand(Collection<Object> facts, Class<?> responseClazz) {
		List<Command<?>> commands = new ArrayList<Command<?>>();

		if (facts != null) {
			commands.add(commandFactory.newInsertElements(facts));
		}

		commands.add(commandFactory.newFireAllRules());

		// creates commands to run the queries at the end of process
		commands.addAll(QueryUtils.buildQueryCommands(responseClazz));

		return commandFactory.newBatchExecution(commands, "defaultStatelessKieSession");
	}

	@SuppressWarnings("rawtypes")
	private String getPayload(Collection<Object> facts, String processId, Class<?> responseClazz) {
		String payload = null;
		if (processId == null) {
			BatchExecutionCommand command = createBatchExecutionCommand(facts, responseClazz);
			payload = xstream.toXML(command);
		} else {
			String payloadTemplate = "<batch-execution lookup=\"defaultStatelessKieSession\">\n" + "  <start-process processId=\"%s\"/>\n" + "%s\n" + "  <fire-all-rules/>\n" + "%s\n"
					+ "</batch-execution>\n";
			String insertElements = "";
			if (facts != null && facts.size() > 0) {
				Command command = commandFactory.newInsertElements(facts);
				insertElements = xstream.toXML(command);
			}
			List<Command<?>> queryCommands = QueryUtils.buildQueryCommands(responseClazz);
			String queryElements = "";
			if (queryCommands.size() > 0) {
				for (Command c : queryCommands) {
					queryElements = queryElements.concat(xstream.toXML(c));
				}
				LOGGER.error(queryElements);
			}
			payload = String.format(payloadTemplate, processId, insertElements, queryElements);
		}

		return payload;
	}

	@Override
	public boolean createOrUpgradeRulesWithVersion(String group, String artifact, String version) {
		ServiceResponse<ReleaseId> response = client.updateReleaseId(this.containerId, new ReleaseId(group, artifact, version));
		if (response.getType().equals(ResponseType.FAILURE)) {
			if (response.getMsg().contains("is not instantiated")) {
				LOGGER.info(response.getMsg());
				LOGGER.info(String.format("Instantiating container %s now...", containerId));
				ServiceResponse<KieContainerResource> response2 = client.createContainer(containerId, new KieContainerResource(containerId, new ReleaseId(group, artifact, version),
						KieContainerStatus.STARTED));
				if (response2.getType().equals(ResponseType.SUCCESS)) {
					LOGGER.info(response2.getMsg());
					return true;
				}
				// else fall through to fail
			}
			LOGGER.error(response.getMsg());
			return false;
		} else {
			LOGGER.info(String.format("%sd to %s", response.getMsg().substring(0, response.getMsg().length() - 2), response.getResult()));
			return true;
		}
	}

	@Override
	public String getCurrentRulesVersion() {
		ServiceResponse<KieContainerResource> response = client.getContainerInfo(containerId);
		if (response == null || response.getResult() == null || response.getResult().getReleaseId() == null) {
			return "container is empty";
		}
		return response.getResult().getReleaseId().toString();
	}

}
