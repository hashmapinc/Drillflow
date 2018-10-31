package witsmlTest.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import witsmlTest.model.WellBore;
import witsmlTest.service.WellBoreService;


@RunWith(SpringRunner.class)
@WebMvcTest(value = WellBoreController.class, secure = false)
public class WellBoreControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private WellBoreService wellboreservice;
	
	WellBore mockwell =new WellBore("10");
	WellBore addwell= new WellBore("20");
	WellBore updatewell= new WellBore("30");
	String exampleWellJson= "{\"id\":\"20\"}";
	
	@Test
	public void getFromStore() throws Exception 
	{
		Mockito.when(wellboreservice.getFromStore(Mockito.anyString())
				).thenReturn(mockwell);
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
				"/wellbore/wellbore/10").accept(
				MediaType.APPLICATION_JSON);
		
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		int status=result.getResponse().getStatus();
		assertEquals(200, status);
		
	}
	
	@Test
	public void addToStore() throws Exception
	{
		Mockito.when(
				wellboreservice.addToStore(Mockito.anyString())
				).thenReturn(addwell);
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post("/wellbore/wellbore/20")
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
				wellboreservice.updateInStore(Mockito.anyString(),Mockito.anyString())
				).thenReturn(updatewell);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/wellbore/wellbore/30")
				.accept(MediaType.APPLICATION_JSON).content(exampleWellJson)
				.contentType(MediaType.APPLICATION_JSON);
		
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();
		
		assertEquals(HttpStatus.OK.value(), response.getStatus());
	}
	
    @Test
    public void deleteFromStore() throws Exception
    {
    	
    	RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/wellbore/wellbore/1000")
				.accept(MediaType.APPLICATION_JSON).content(exampleWellJson)
				.contentType(MediaType.APPLICATION_JSON);
    	
    	MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();
		
		assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    	
    }
	
	
	
}
