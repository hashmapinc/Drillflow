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
@RequestMapping("well/v2")
public class well2Controller {

	/**
	 * Query a well by ID
	 * sc:defined: Request URL https://api-demo.nam.drillops.slb.com/democore/well/v2/{uuid}
	 * sc:defined: HttpResponse response
                   HttpEntity entity = response.getEntity();
	 * 
	 * will change the return type as new ResponseEntity<Well2>(well, HttpStatus.OK) when we have a payload
	 */
	@GetMapping("/{uuid}")
	public ResponseEntity<String> getWell2(@PathVariable String uuid)
	{
		//TODO get well2 from model data by the uuid
	    String well2="";	
		return new ResponseEntity<String>(well2, HttpStatus.OK);
	}
	
	/**
	 * Query wells by company and name
	 * Query wells by speficying different query criteria.
	 * sc:defined Request URL Request URL
        https://api-demo.nam.drillops.slb.com/democore/well/v2/[?query.company][&query.name][&query.streamingState][&query.liveState][&query.includeData]
	 */
	@GetMapping("/{compamy}/{name}")
	public ResponseEntity<ArrayList<String>> getWells2 (@PathVariable String compamy,@PathVariable String name) {
		//TODO get wells2 from model data by the company and name
		ArrayList<String> wells = new ArrayList<String>(2);
		wells.add("");
		wells.add("");
		return new ResponseEntity<ArrayList<String>>(wells, HttpStatus.OK);//HTTP 200 response code		
	}
	
	/**
	 * Provision a new well with contract ID, well name, company and timezone. 
	 * Use this API when there is no proposed ID for the well.
	 * sc: defined Request URL
			https://api-demo.nam.drillops.slb.com/democore/well/v2/?contractId={contractId}
	 */
	@PostMapping("/{contractId}")
	public ResponseEntity<Void> addWell2byContractId(String contractId) 
	{
		//TODO well add to data model
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path(
				"/{contractId}").build().toUri();//buildAndExpand(contract.getId()).toUri();
		return ResponseEntity.created(location).build();
	}
	
	/**
	 * Provision a new well with a specified ID. Use this API when there is a proposed ID for the well.
	 * sc url Request URL
			https://api-demo.nam.drillops.slb.com/democore/well/v2/{uuid}?contractId={contractId}
	 */
	@PostMapping(path = "/{uuid}/{name}", consumes = "application/json", produces = "application/json")
	public ResponseEntity<Void> addWell2(@PathVariable String uuid,@PathVariable String name) {
		//TODO bind wells to the model
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path(
				"/{compamy}").build().expand("/{name}").toUri();
		return ResponseEntity.created(location).build();	    	    
	}
	
	/**
	 * Create or update a well using WITSML object
       Well is being created if it doesn't exist already, otherwise it gets updated.
	 * sc defined: Request URL
			https://api-demo.nam.drillops.slb.com/democore/well/v2/{uuid}?contractId={contractId}
	 */
	@PutMapping("/{uuid}/{contractId")
	public ResponseEntity<String> updateWell2(@PathVariable String uuid,@PathVariable String contractId) {
		//TODO will access model data to retrieve the well by the input reqest
		//Well2 well=mywellservice.updateInStore("BHI",compamy); 
		String wellStr="";
		return new ResponseEntity<String>(wellStr, HttpStatus.OK);//200
	}
	
	
	/**
	 * Delete a well by ID
		Delete an existing well with specified ID. This API will delete a well and all 
		related information including Wellbore, Device, Trajectory, etc.		
		sc: Request URL
			https://api-demo.nam.drillops.slb.com/democore/well/v2/{uuid}
	 */
	@DeleteMapping("/{uuid}")
	public ResponseEntity<Void> deleteWellById(@PathVariable String uuid) {
		//mywellservice.deleteFromStore(compamy);
		//TODO delete from data model
		//boolean deleted=false;
		//boolean deleted=deleteFromModel(uuid)
		//if(deleted=true)
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);//code 204
		
	}	
	
	
	
	
	
}
