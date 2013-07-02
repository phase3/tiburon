package com.phase3.rest;

import com.phase3.businesslogic.*;
import com.phase3.logic.*;
import com.phase3.model.*;
import org.apache.cxf.jaxrs.ext.*;
import org.slf4j.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

public class BaseEndpoint {
    private static final Logger log = LoggerFactory.getLogger(BaseEndpoint.class);

    protected int clientCacheExpiration = 3000;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Context HttpHeaders headers, @Context MessageContext context, @Context SecurityContext securityContext) {

        if(log.isTraceEnabled()) log.trace(context.getRequest().getMethod() + " " +  context.getUriInfo().getPath());

        try {
            Logic logic = LogicFactory.getLogic(securityContext, this.getClass());
            String json = logic.getAll(context.getUriInfo().getPath());

            Response.ResponseBuilder response =  Response.ok(json, MediaType.APPLICATION_JSON);

            // suggest client cache
            response.expires(new Date(System.currentTimeMillis() + clientCacheExpiration));
            return response.build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.status(500).build();
        }

    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOne(@Context HttpHeaders headers, @Context MessageContext context, @Context SecurityContext securityContext, @PathParam("id") String id) {
        if(log.isTraceEnabled()) log.trace(context.getRequest().getMethod() + " " +  context.getUriInfo().getPath());
        try {
            Logic logic = LogicFactory.getLogic(securityContext, this.getClass());
            String json = logic.getOne(context.getUriInfo().getPath(), id);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.status(500).build();
        }	}

    /*
return:

201 Created
Location: http://<object>s/xyz

*/
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@Context HttpHeaders headers, @Context MessageContext context, @Context SecurityContext securityContext, String content) {
        if(log.isTraceEnabled()) log.trace(context.getRequest().getMethod() + " " +  context.getUriInfo().getPath());
        try {
            Logic logic = LogicFactory.getLogic(securityContext, this.getClass());
            String json = logic.create(context.getUriInfo().getPath(),content);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.status(500).build();
        }	}

    /*
return:

200 Ok

*/
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@Context HttpHeaders headers,  @Context MessageContext context, @Context SecurityContext securityContext, String content,  @PathParam("id") String id) {
        if(log.isTraceEnabled()) log.trace(context.getRequest().getMethod() + " " +  context.getUriInfo().getPath());
        try {
            Logic logic = LogicFactory.getLogic(securityContext, this.getClass());
            String json = logic.update(context.getUriInfo().getPath(),id, content);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.status(500).build();
        }	}
}
