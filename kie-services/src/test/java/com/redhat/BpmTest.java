package com.redhat;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import bitronix.tm.resource.jdbc.PoolingDataSource;

@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@ActiveProfiles("test")
public class BpmTest extends AbstractJUnit4SpringContextTests {

	protected static final String GROUP_ID = "com.redhat";
	protected static final String ARTIFACT_ID = "knowledge";
	protected static final String VERSION = "1.0-SNAPSHOT";
	protected static final DeploymentUnit DEPLOYMENT_UNIT = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
	protected static final String PROCESS_ID = "VerifySupplier";

	@Autowired
	protected ProcessService processService;
	@Autowired
	protected RuntimeDataService runtimeDataService;
	@Autowired
	protected DeploymentService deploymentService;
	@Autowired
	protected UserTaskService userTaskService;

	@Test
	public void test() throws InterruptedException {
		deploymentService.deploy(DEPLOYMENT_UNIT);
		
		processService.startProcess(DEPLOYMENT_UNIT.getIdentifier(), PROCESS_ID);
		
		Thread.sleep( 10000l );
	}

	protected static PoolingDataSource pds;

	@BeforeClass
	public static void generalSetup() {
		TestUtils.setupPoolingDataSource();
	}

	@Before
	public void setup() {
		TestUtils.cleanupSingletonSessionId();

	}
}
