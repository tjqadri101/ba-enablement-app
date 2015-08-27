package com.redhat;

import java.util.Collection;

import org.kie.api.KieServices;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatelessDecisionServiceImpl implements StatelessDecisionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(StatelessDecisionServiceImpl.class);
	
	private KieContainer kieContainer = KieServices.Factory.get().newKieClasspathContainer();
	
	public void runRules(Collection<Object> facts, String processId) {
		LOGGER.info("hi!");
		
		KieSession session = kieContainer.newKieSession("stateful");
		KieRuntimeLogger auditLogger = KieServices.Factory.get().getLoggers().newFileLogger(session, "audit");
		
		for ( Object fact : facts){
			session.insert(fact);
		}
		session.startProcess(processId);
		session.fireAllRules();
		session.dispose();
		auditLogger.close();
	}

}
