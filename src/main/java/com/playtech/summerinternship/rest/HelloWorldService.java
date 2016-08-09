package com.playtech.summerinternship.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.playtech.summerinternship.HelloWorld;

@Path("sample")
public class HelloWorldService {

    @Path("/helloWorld/{name}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public HelloWorld helloWorld(@PathParam("name") String name) {
        return new HelloWorld(name);
    }
}
