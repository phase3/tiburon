package com.phase3.jsonbind;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.phase3.jsonbind.model.*;
import org.junit.*;
import org.junit.Before;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class TestPerformance {
	Customer c = new Customer();
	private int testSize = 100000;

	@Before
	public void before() {
		c.setName("John \"Doe\" Smith");
		c.setAge(32);
		c.setDateOfBirth(new Date());
		c.setActive(true);
		c.setPhoneList(new ArrayList<Phone>());
		Phone p = new Phone();
		p.setNumber("111-111-1111");
		p.setType(new PhoneType(1,"cell"));
		c.getPhoneList().add(p);
		p = new Phone();
		p.setNumber("222-222-2222");
		p.setType(new PhoneType(2,"main"));
		c.getPhoneList().add(p);
	}
	@Test
	public void testSingle() throws Exception {
		JsonMapper jb = new JsonMapper();
		jb.setPretty(false);
//		JsonMask mask = new JsonMask("{\"include\": [\"name\", \"phoneList.number\", \"phoneList.type.name\"]}");
//		jb.setMask(mask);

		System.out.println(jb.serializeAsString(c));

		long now = new Date().getTime();
		for(int x=0;x<testSize;x++) {
			jb.serializeAsString(c);
		}
		System.out.println("JsonMapper: " + ((new Date().getTime()) - now));

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

		System.out.println(mapper.writeValueAsString(c));

		now = new Date().getTime();
		for(int x=0;x<testSize;x++) {
			mapper.writeValueAsString(c);
		}
		System.out.println("Jackson: " + ((new Date().getTime()) - now));

	}
	//@Test
	public void testEvery() throws Exception {

//		System.out.println(jb.serializeAsString(c));

		long now = new Date().getTime();
		for(int x=0;x<testSize;x++) {
			JsonMapper jb = new JsonMapper();
			jb.setPretty(false);
//		JsonMask mask = new JsonMask("{\"include\": [\"name\", \"phoneList.number\", \"phoneList.type.name\"]}");
//		jb.setMask(mask);
			jb.serializeAsString(c);
		}
		System.out.println("JsonMapper: " + ((new Date().getTime()) - now));


		now = new Date().getTime();
		for(int x=0;x<testSize;x++) {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
			mapper.writeValueAsString(c);
		}
		System.out.println("Jackson: " + ((new Date().getTime()) - now));
	}
}
