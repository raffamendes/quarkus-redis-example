package com.rmendes.service;

import java.time.LocalDateTime;

import com.rmendes.model.Appointment;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CacheResult;
import io.quarkus.cache.redis.runtime.RedisCache;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AppointmentService {
	
	private final ValueCommands<String, Appointment> commands;
	
	@CacheName("cacheTest")
	Cache cache;
	
	MeterRegistry registry = Metrics.globalRegistry;
	
	public AppointmentService(RedisDataSource ds) {
		commands = ds.value(Appointment.class);
		Timer.builder("method.timer")
		.description("Contador de Tempo")
		.tags("method","cache","method","data-source")
		.register(registry);
	}	
	
	public Appointment saveOnRedisDS(String name) {
		Appointment a = new Appointment();
		a.motive = "Medical";
		a.confirmed = true;
		a.scheduled = LocalDateTime.now();
		commands.set(name, a);
		return a;
	}
	
	@CacheResult(cacheName = "cacheTest")
	public Appointment saveOnRedisCache(@CacheKey String name) {
		Appointment a = new Appointment();
		a.motive = "Legal";
		a.confirmed = true;
		a.scheduled = LocalDateTime.now();
		return a;
	}
	
	@Timed(value = "cache_call", extraTags = {"method","cache"})
	public Uni<Appointment> getFromCache(String name) {
		return cache.as(RedisCache.class).getOrNull(name, Appointment.class);
	}
	
	@Timed(value = "ds_call", extraTags = {"method","data-source"})
	public Appointment getFromRedisCommands(String name) {
		return commands.get(name);
	}

}
