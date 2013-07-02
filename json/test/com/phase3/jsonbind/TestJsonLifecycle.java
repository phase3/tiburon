package com.phase3.jsonbind;

import com.phase3.jsonbind.model.*;
import org.junit.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class TestJsonLifecycle {
	@Test
	public void testLifeCycle() throws Exception {
		Customer c = new Customer();
		c.setName("John Doe");
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
		p.setType(new PhoneType(2, "main"));
		c.getPhoneList().add(p);


		JsonMapper jb = new JsonMapper();
		jb.setPretty(false);

		String originalJson = jb.serializeAsString(c);

		Customer c2 = jb.deserialize(originalJson, Customer.class);

		String newJson = jb.serializeAsString(c2);

		assert(newJson.equals(originalJson));

	}
}
