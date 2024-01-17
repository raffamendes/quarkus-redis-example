package com.rmendes;

import com.rmendes.model.Appointment;
import com.rmendes.service.AppointmentService;

import io.quarkus.redis.datasource.RedisDataSource;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/schedule")
public class AppointmentResource {

	@Inject
	AppointmentService service;
	
	@Inject
	RedisDataSource defaultRedisDataSource;
	
	@GET
	@Path("/{name}")
	public Response scheduleAppointment(@PathParam(value = "name") String name) {
		return Response.ok(service.saveOnRedisDS(name)).build();
	}
	
	@GET
	@Path("/legal/{name}")
	public Response scheduleLegalAppointment(@PathParam(value = "name") String name) {
		return Response.ok(service.saveOnRedisCache(name)).build();
	}

	@GET
	@Path("/get/{name}")
	public Appointment retrieveAppointment(@PathParam(value = "name") String name) {
		return service.getFromRedisCommands(name);		
	}
	
	@GET
	@Path("/get/cache/{name}")
	public Appointment getAppointmentFromCache(@PathParam(value = "name") String name) {
		return service.getFromCache(name).await().indefinitely();
	}
}
