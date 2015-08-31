package com.redhat;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

public class DroolTest {

	private StatelessDecisionService service = BrmsHelper.newStatelessDecisionServiceBuilder().build();

	@Test
	public void helloWorldTest() {
		// given
		Collection<Object> facts = new ArrayList<Object>();
		Greeting greeting = new Greeting();
		facts.add(greeting);
		Assert.assertNull(greeting.getGreeting());

		// when
		service.runRules(facts, "simple.Ruleflow", null);

		// then
		Assert.assertEquals("Hello, World!", greeting.getGreeting());
	}

}
