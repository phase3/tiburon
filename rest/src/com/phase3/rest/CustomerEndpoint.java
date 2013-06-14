package com.phase3.rest;

/**
 * Project: tiburon
 * User:    cgh
 * Created: 6/14/13
 */

import com.phase3.businesslogic.*;
import com.phase3.logic.*;
import com.phase3.model.*;
import org.slf4j.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/customers")
public class CustomerEndpoint {
	private static final Logger log = LoggerFactory.getLogger(CustomerEndpoint.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll(@Context HttpHeaders headers, @Context SecurityContext securityContext) {
		log.trace("customers [get]");
		try {
			Logic logic = LogicFactory.getLogic(securityContext, Customer.class);
			String json = logic.getAll();
			return Response.ok(json, MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return Response.status(500).build();
		}

	}
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOne(@Context HttpHeaders headers, @Context SecurityContext securityContext, @PathParam("id") String id) {
		log.trace("customers/"+id+" [get]");
		try {
			Logic logic = LogicFactory.getLogic(securityContext, Customer.class);
			String json = logic.getOne(id);
			return Response.ok(json, MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return Response.status(500).build();
		}	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(@Context HttpHeaders headers, String content, @Context SecurityContext securityContext) {
		log.trace("customers [update]");
		try {
			Logic logic = LogicFactory.getLogic(securityContext, Customer.class);
			String json = logic.create(content);
			return Response.ok(json, MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return Response.status(500).build();
		}	}
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(@Context HttpHeaders headers, String content, @Context SecurityContext securityContext, @PathParam("id") String id) {
		log.trace("customers/"+id+" [update]");
		try {
			Logic logic = LogicFactory.getLogic(securityContext, Customer.class);
			String json = logic.update(id, content);
			return Response.ok(json, MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return Response.status(500).build();
		}	}

}
