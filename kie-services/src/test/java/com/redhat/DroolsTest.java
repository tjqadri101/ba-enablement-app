package com.redhat;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

public class DroolsTest {

	private StatelessDecisionService service = BrmsHelper.newStatelessDecisionServiceBuilder().auditLogName("audit").build();

	@Test
	public void helloWorldTest() {
		// given
		Collection<Object> facts = new ArrayList<Object>();
		Business business = new Business();
		business.setName("test");
		String string = new String();
		facts.add(business);

		// when
		RuleResponse response = service.runRules(facts, "defaultPackage.Process", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertEquals("test", response.getBusiness().getName());
	}
	
	
	public void shouldFilterOutAllRequestsFromKansas(){
		// scenario: business from Kansas are handled by another system - filter them out
		// given a business from Kansas
		// when I apply the filtering rules
		// then the business should be filtered
		// and the reason message should be "business filtered from Kansas"
	}
	
	
	public void shouldProcessAllBusinessesNotFromKansas(){
		// scenario: we are responsible for all businesses not from Kansas
		// given a business from New York
		// when I apply the filtering rules
		// then the business should be not be filtered
		// and the validation rules should be applied to the business
	}
	
	public void shouldCreateValidationErrorsForAnyFieldThatAreEmptyOrNull(){
		// scenario: all fields must have values. 
		// given a business 
		// and the business' zipcode is empty
		// and the business' address line 1 is null
		// when I apply the validation rules
		// then the business should be return a validation error
		// and a message should say the zipcode is empty
		// and a message should say the address is null
	}
	
	public void shouldEnrichTheTaxIdWithZipCode(){
		// scenario: we need to enrich the taxId with the zipcode for system XYZ
		// given a business 
		// and the business' zipcode is 10002
		// and the business' taxId is 98765
		// when I apply the enrichment rules
		// then the business' taxId should be enriched to 98765-10002
	}
	
}
