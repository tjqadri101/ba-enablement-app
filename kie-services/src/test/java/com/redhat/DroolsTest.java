package com.redhat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
		facts.add(business);

		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness()); //retracted due to validaion errors caused by unpopulated fields
		Assert.assertEquals("test", response.getBusiness().getName());
	}
	
	@Test
	public void shouldFilterOutAllRequestsFromKansas(){
		// scenario: business from Kansas are handled by another system - filter them out
		// given a business from Kansas
		// when I apply the filtering rules
		// then the business should be filtered
		// and the reason message should be "business filtered from Kansas"
		
		Collection<Object> facts = new ArrayList<Object>();
		Reason reason = new Reason();
		reason.setReasonMessage("business from kansas");
		Business business = new Business();
		business.setStateCode("KS");
		facts.add(business);

		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNull(response.getBusiness());
		Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("filtered", response.getResponseCode());
		Assert.assertNotNull(response.getReasons());
		Assert.assertTrue(response.getReasons().contains(reason));
	}
	
	
	
	@Test
	public void shouldProcessAllBusinessesNotFromKansas(){
		// scenario: we are responsible for all businesses not from Kansas
		// given a business from New York
		// when I apply the filtering rules
		// then the business should be not be filtered
		// and the validation rules should be applied to the business
		//the response code should be validation error as the business variable has unpopulated fields
		
		Collection<Object> facts = new ArrayList<Object>();
		Reason reason = new Reason();
		reason.setReasonMessage("business not from kansas so applying validation rules");
		Business business = new Business();
		business.setStateCode("NY");
		facts.add(business);

		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness()); 
		Assert.assertEquals("NY", response.getBusiness().getStateCode());
		Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("validation error", response.getResponseCode());
		Assert.assertNotNull(response.getReasons());
		Assert.assertTrue(response.getReasons().contains(reason));
		
		
	}
	
	/*
	@Test
	public void shouldCreateValidationErrorsForAnyFieldThatAreEmptyOrNull(){
		// scenario: all fields must have values. 
		// given a business 
		// and the business' zipcode is empty
		// and the business' address line 1 is null
		// when I apply the validation rules
		// then the business should be return a validation error
		// and a message should say the zipcode is empty
		// and a message should say the address is null
		shouldCreateValidationErrorsForEmptyNameField();
	}
	*/
	
	@Test
	public void shouldCreateValidationErrorsForEmptyNameField(){
		Collection<Object> facts = new ArrayList<Object>();
		Reason reason = new Reason();
		reason.setReasonMessage("the name is empty");
		Business business = new Business();
		business.setName("");
		facts.add(business);

		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertEquals("", response.getBusiness().getName());
		Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("validation error", response.getResponseCode());
		Assert.assertNotNull(response.getReasons());
		Assert.assertTrue(response.getReasons().contains(reason));
	}
	
	@Test
	public void shouldCreateValidationErrorsForNullNameField(){
		Collection<Object> facts = new ArrayList<Object>();
		Reason reason = new Reason();
		reason.setReasonMessage("the name is null");
		Business business = new Business();
		facts.add(business);
		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertNull(response.getBusiness().getName());
		Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("validation error", response.getResponseCode());
		Assert.assertNotNull(response.getReasons());
		Assert.assertTrue(response.getReasons().contains(reason));
	}
	
	@Test
	public void shouldCreateValidationErrorsForEmptyAddressLine1Field(){
		Collection<Object> facts = new ArrayList<Object>();
		Reason reason = new Reason();
		reason.setReasonMessage("the address line 1 is empty");
		Business business = new Business();
		business.setAddressLine1("");
		facts.add(business);
		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertEquals("", response.getBusiness().getAddressLine1());
		Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("validation error", response.getResponseCode());
		Assert.assertNotNull(response.getReasons());
		Assert.assertTrue(response.getReasons().contains(reason));
	}
	
	@Test
	public void shouldCreateValidationErrorsForNullAddressLine1Field(){
		Collection<Object> facts = new ArrayList<Object>();
		Reason reason = new Reason();
		reason.setReasonMessage("the address line 1 is null");
		Business business = new Business();
		facts.add(business);
		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertNull(response.getBusiness().getAddressLine1());
		Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("validation error", response.getResponseCode());
		Assert.assertNotNull(response.getReasons());
		Assert.assertTrue(response.getReasons().contains(reason));
	}
	
	@Test
	public void shouldCreateValidationErrorsForEmptyAddressLine2Field(){
		Collection<Object> facts = new ArrayList<Object>();
		Reason reason = new Reason();
		reason.setReasonMessage("the address line 2 is empty");
		Business business = new Business();
		business.setAddressLine2("");
		facts.add(business);
		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertEquals("", response.getBusiness().getAddressLine2());
		Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("validation error", response.getResponseCode());
		Assert.assertNotNull(response.getReasons());
		Assert.assertTrue(response.getReasons().contains(reason));
	}
	
	@Test
	public void shouldCreateValidationErrorsForNullAddressLine2Field(){
		Collection<Object> facts = new ArrayList<Object>();
		Reason reason = new Reason();
		reason.setReasonMessage("the address line 2 is null");
		Business business = new Business();
		facts.add(business);
		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertNull(response.getBusiness().getAddressLine2());
		Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("validation error", response.getResponseCode());
		Assert.assertNotNull(response.getReasons());
		Assert.assertTrue(response.getReasons().contains(reason));
	}
	
	@Test
	public void shouldCreateValidationErrorsForEmptyPhoneField(){
		Collection<Object> facts = new ArrayList<Object>();
		Reason reason = new Reason();
		reason.setReasonMessage("the phone number is empty");
		Business business = new Business();
		business.setPhoneNumber("");
		facts.add(business);
		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertEquals("", response.getBusiness().getPhoneNumber());
		Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("validation error", response.getResponseCode());
		Assert.assertNotNull(response.getReasons());
		Assert.assertTrue(response.getReasons().contains(reason));
	}
	
	@Test
	public void shouldCreateValidationErrorsForNullPhoneField(){
		Collection<Object> facts = new ArrayList<Object>();
		Reason reason = new Reason();
		reason.setReasonMessage("the phone number is null");
		Business business = new Business();
		facts.add(business);
		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertNull(response.getBusiness().getPhoneNumber());
		Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("validation error", response.getResponseCode());
		Assert.assertNotNull(response.getReasons());
		Assert.assertTrue(response.getReasons().contains(reason));
	}
	
	@Test
	public void shouldCreateValidationErrorsForEmptyStateField(){
		Collection<Object> facts = new ArrayList<Object>();
		Reason reason = new Reason();
		reason.setReasonMessage("the state code is empty");
		Business business = new Business();
		business.setStateCode("");
		facts.add(business);
		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertEquals("", response.getBusiness().getStateCode());
		Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("validation error", response.getResponseCode());
		Assert.assertNotNull(response.getReasons());
		Assert.assertTrue(response.getReasons().contains(reason));
	}
	
	@Test
	public void shouldCreateValidationErrorsForNullStateField(){
		Collection<Object> facts = new ArrayList<Object>();
		Reason reason = new Reason();
		reason.setReasonMessage("the state code is null");
		Business business = new Business();
		facts.add(business);
		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertNull(response.getBusiness().getStateCode());
		Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("validation error", response.getResponseCode());
		Assert.assertNotNull(response.getReasons());
		Assert.assertTrue(response.getReasons().contains(reason));
	}
	
	@Test
	public void shouldCreateValidationErrorsForEmptyCityField(){
		Collection<Object> facts = new ArrayList<Object>();
		Reason reason = new Reason();
		reason.setReasonMessage("the city is empty");
		Business business = new Business();
		business.setCity("");
		facts.add(business);
		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertEquals("", response.getBusiness().getCity());
		Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("validation error", response.getResponseCode());
		Assert.assertNotNull(response.getReasons());
		Assert.assertTrue(response.getReasons().contains(reason));
	}
	
	@Test
	public void shouldCreateValidationErrorsForNullCityField(){
		Collection<Object> facts = new ArrayList<Object>();
		Reason reason = new Reason();
		reason.setReasonMessage("the city is null");
		Business business = new Business();
		facts.add(business);
		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertNull(response.getBusiness().getCity());
		Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("validation error", response.getResponseCode());
		Assert.assertNotNull(response.getReasons());
		Assert.assertTrue(response.getReasons().contains(reason));
	}
	
	@Test
	public void shouldCreateValidationErrorsForEmptyZipField(){
		Collection<Object> facts = new ArrayList<Object>();
		Reason reason = new Reason();
		reason.setReasonMessage("the zip code is empty");
		Business business = new Business();
		business.setZipCode("");
		facts.add(business);
		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertEquals("", response.getBusiness().getZipCode());
		Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("validation error", response.getResponseCode());
		Assert.assertNotNull(response.getReasons());
		Assert.assertTrue(response.getReasons().contains(reason));
	}
	
	@Test
	public void shouldCreateValidationErrorsForNullZipField(){
		Collection<Object> facts = new ArrayList<Object>();
		Reason reason = new Reason();
		reason.setReasonMessage("the zip code is null");
		Business business = new Business();
		facts.add(business);
		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertNull(response.getBusiness().getZipCode());
		Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("validation error", response.getResponseCode());
		Assert.assertNotNull(response.getReasons());
		Assert.assertTrue(response.getReasons().contains(reason));
	}
	
	@Test
	public void shouldCreateValidationErrorsForEmptyTaxIdField(){
		Collection<Object> facts = new ArrayList<Object>();
		Reason reason = new Reason();
		reason.setReasonMessage("the federal tax id is empty");
		Business business = new Business();
		business.setFederalTaxId("");
		facts.add(business);
		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertEquals("", response.getBusiness().getFederalTaxId());
		Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("validation error", response.getResponseCode());
		Assert.assertNotNull(response.getReasons());
		Assert.assertTrue(response.getReasons().contains(reason));
	}
	
	@Test
	public void shouldCreateValidationErrorsForNullTaxIdField(){
		Collection<Object> facts = new ArrayList<Object>();
		Reason reason = new Reason();
		reason.setReasonMessage("the federal tax id is null");
		Business business = new Business();
		facts.add(business);
		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertNull(response.getBusiness().getFederalTaxId());
		Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("validation error", response.getResponseCode());
		Assert.assertNotNull(response.getReasons());
		Assert.assertTrue(response.getReasons().contains(reason));
	}
	
	
	@Test
	public void shouldEnrichTheTaxIdWithZipCode(){
		// scenario: we need to enrich the taxId with the zipcode for system XYZ
		// given a business 
		// and the business' zipcode is 10002
		// and the business' taxId is 98765
		// when I apply the enrichment rules
		// then the business' taxId should be enriched to 98765-10002
		
		Collection<Object> facts = new ArrayList<Object>();
		Reason reason = new Reason();
		reason.setReasonMessage("enrich tax id with zip code");
		Business business = new Business();
		business.setZipCode("10002");
		business.setFederalTaxId("98765");
		facts.add(business);
		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertEquals("98765-10002", response.getBusiness().getFederalTaxId());
		Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("validation error", response.getResponseCode()); //due to unpopulated fields
		Assert.assertNotNull(response.getReasons());
		Assert.assertTrue(response.getReasons().contains(reason));
	}
	
}
