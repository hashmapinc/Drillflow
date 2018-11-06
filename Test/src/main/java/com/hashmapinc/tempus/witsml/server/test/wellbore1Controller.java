package com.hashmapinc.tempus.witsml.server.test;

import java.net.URI;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("wellbore/v1")
public class wellbore1Controller {
	
	/**
	 * Get wellbore information by wellboreId.
       sc: url Request URL
       https://api-demo.nam.drillops.slb.com/democore/wellbore/v1/wellbores/{id}[?contentType]
	 */
	@GetMapping("/{uuid}/{contentType}")
	public ResponseEntity<String> getWellBoreById(@PathVariable String uuid,@PathVariable String contentType)
	{
		//TODO get wellbore from model data by the uuid and contentType
		//eg WellBore1 wb= getWellbore(String uuid,String contentType)
	    String wellbore1="";	
		return new ResponseEntity<String>(wellbore1, HttpStatus.OK);
	}
	
	/**
	 * Get all wellbores(Ids) belong to the specified wellId.
	   sc Request URL https://api-demo.nam.drillops.slb.com/democore/wellbore/v1/wellbores?wellId={wellId}	
	 */
	@GetMapping("/{wellId}")
	public ResponseEntity<String> getWellBoreById(@PathVariable String wellId)
	{
		//TODO get wellbores from model data by wellid, and will change return type String to a list of wellbore
		String wellbore1="";	
		return new ResponseEntity<String>(wellbore1, HttpStatus.OK);
	}
	
	/**
	 * Create a new wellbore with an existed wellId and a wellbore name.
	 * https://api-demo.nam.drillops.slb.com/democore/wellbore/v1/wellbores
	 */
	@PostMapping("/{wellId}/{nema}")
	public ResponseEntity<Void> addWellborebyIdName(String wellId,String name) 
	{
		//TODO get wellbores from  data model, then check if the wellId and name are exiting
		//if true the add a new wellbore
		
		//URI location = ServletUriComponentsBuilder.fromCurrentRequest().path(
		//		"/{wellId}").build()toUri();//buildAndExpand(contract.getId()).toUri();
					
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path(
				"/{wellId}").build().expand("/{name}").toUri();
		return ResponseEntity.created(location).build();
	}
	
	/**
	 * Create a new wellbore or update an existing wellbore with a witsml2.0 object.
        Response returns the information of the wellbore created or updated. 
        sc defined Request URL
        https://api-demo.nam.drillops.slb.com/democore/wellbore/v1/wellbores/{id}       
	 */
	@PutMapping("/{id}")
	public ResponseEntity<String> updateWellBore(@PathVariable String id) {
		//TODO will access model data to retrieve a wellbore by the input id and this wellbore is witsml2.0 object
		//will walk throught the wellbore to findout what is the ifo to be update?
		
		String wellboreStr="";  
		return new ResponseEntity<String>(wellboreStr, HttpStatus.OK);//200
	}
	
	/**
	 * 	Create a new wellbore or update an existing wellbore with a witsml2.0 object.
		Response returns the information of the wellbore created or updated.
	 *  Request URL
        https://api-demo.nam.drillops.slb.com/democore/wellbore/v1/wellbores/{id}
	 */
	@DeleteMapping("/{uuid}")
	public ResponseEntity<Void> deleteWellBoreById(@PathVariable String uuid) {
		//TODO delete wellbore from data model by id
		//boolean deleted=false;
		//boolean deleted=deleteFromModel(uuid)
		//if(deleted=true)
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);//code 204		
	}	

}
