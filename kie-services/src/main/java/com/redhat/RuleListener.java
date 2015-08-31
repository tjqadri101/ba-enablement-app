package com.redhat;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;

public class RuleListener extends DefaultAgendaEventListener {

	private List< AfterMatchFiredEvent >	ruleFiredEvents	= new ArrayList< AfterMatchFiredEvent >();

	@Override
	public void afterMatchFired( AfterMatchFiredEvent event ) {
		ruleFiredEvents.add( event );
	}

	public List< AfterMatchFiredEvent > getRuleFiredEvents() {
		return ruleFiredEvents;
	}

	public void setRuleFiredEvents( List< AfterMatchFiredEvent > ruleFiredEvents ) {
		this.ruleFiredEvents = ruleFiredEvents;
	}

	public List< String > getRuleNames() {
		List< String > names = new ArrayList< String >();
		for ( AfterMatchFiredEvent event : ruleFiredEvents ) {
			names.add( event.getMatch().getRule().getName() );
		}
		return names;
	}

}
