package com.redhat;


public class StatelessDecisionServiceBuilder {

	// global properties
	private RuntimeType runtimeType = RuntimeType.EMBEDDED;

	// local properties
	private String auditLogName = null;
	private boolean isDebugLoggingEnabled = false;

	// remote
	private String userName = null;
	private String password = null;
	private String url = "http://localhost:8080/kie-server/services/rest/server";
	private String kieContainerId = "default";
	private int timeoutInSeconds = 0;  // this will use the BRMS default, 5 seconds as of 6.1.0;

	protected StatelessDecisionServiceBuilder() {
	}

	public StatelessDecisionService build() {
		if (runtimeType.equals(RuntimeType.EMBEDDED)) {
			return new EmbeddedStatelessDecisionService(auditLogName, isDebugLoggingEnabled);
		} else {
			return new RemoteStatelessDecisionService(url, userName, password, timeoutInSeconds, kieContainerId);
		}
	}
	
	public StatelessDecisionServiceBuilder url( String url ){
		assertNotNull(url, "url");
		this.url = url;
		return this;
	}
	
	public StatelessDecisionServiceBuilder kieContainerId( String id ){
		assertNotNull(url, "kie container Id");
		this.kieContainerId = id;
		return this;
	}
	
	public StatelessDecisionServiceBuilder runtimeType( RuntimeType runtimeType ){
		assertNotNull(runtimeType, "runtimeType");
		this.runtimeType = runtimeType;
		return this;
	}
	
	public StatelessDecisionServiceBuilder auditLogName(String auditLogName){
		assertNotNull(auditLogName, "assertNotNull");
		this.auditLogName = auditLogName;
		return this;
	}

	public StatelessDecisionServiceBuilder enableDebugLogging(){
		this.isDebugLoggingEnabled = true;
		return this;
	}
	
	private static void assertNotNull(Object obj, String name) {
		if (obj == null) {
			throw new IllegalArgumentException("Null " + name + " arguments are not accepted!");
		}
	}

}
