package witsmlTest.controller;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JsonContentAssert;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.Assert;

import org.skyscreamer.jsonassert.JSONAssert;

import witsmlTest.controller.Well2Controller;
import witsmlTest.model.Well2;
import witsmlTest.service.WellService;

/**
 * 
 * @author cathychen
 * This test case for testing Well2Controller only
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = Well2Controller.class, secure = false)
public class Well2ControllerTest 
{
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private WellService wellservice;
	
	Well2 mockwell =new Well2("AAA");
	Well2 addwell= new Well2("SS");
	Well2 updatewell= new Well2("WELLCOMPANY");
	String exampleWellJson= "{\"id\":\"BHI\"}";
	
	@Test
	public void getFromStore() throws Exception 
	{
		Mockito.when(wellservice.getFromStore(Mockito.anyString())
				).thenReturn(mockwell);
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
				"/well/well/AAA").accept(
				MediaType.APPLICATION_JSON);
		
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		int status=result.getResponse().getStatus();
		assertEquals(200, status);
		
	}
	
	@Test
	public void addToStore() throws Exception
	{
		Mockito.when(
				wellservice.addToStore(Mockito.anyString())
				).thenReturn(addwell);
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post("/well/well/SS")
				.accept(MediaType.APPLICATION_JSON).content(exampleWellJson)
				.contentType(MediaType.APPLICATION_JSON);
		
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertEquals(HttpStatus.CREATED.value(), response.getStatus());
		
	}
	
	@Test
	public void updateInStore() throws Exception
	{
		Mockito.when(
				wellservice.updateInStore(Mockito.anyString(),Mockito.anyString())
				).thenReturn(updatewell);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/well/well/WELLCOMPANY")
				.accept(MediaType.APPLICATION_JSON).content(exampleWellJson)
				.contentType(MediaType.APPLICATION_JSON);
		
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();
		
		assertEquals(HttpStatus.OK.value(), response.getStatus());
	}
	
    @Test
    public void deleteFromStore() throws Exception
    {
    	
    	RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/well/well/WELLCOMPANY")
				.accept(MediaType.APPLICATION_JSON).content(exampleWellJson)
				.contentType(MediaType.APPLICATION_JSON);
    	
    	MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();
		
		assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    	
    }
	

}
