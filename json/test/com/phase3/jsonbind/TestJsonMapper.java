package com.phase3.jsonbind;

import com.phase3.jsonbind.mask.*;
import com.phase3.jsonbind.model.*;
import org.junit.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class TestJsonMapper {
	@Before
	public void before() {

	}
	@After
	public void after() {
	}

	@Test
	public void testSimpleClass() throws Exception {
		Customer c = new Customer();
		c.setName("John Doe");
		c.setAge(32);
		c.setDateOfBirth(new Date());
		c.setActive(true);

		JsonMapper jb = new JsonMapper();
		//jb.setPretty(false);
		System.out.println(jb.serializeAsString(c));
	}
	@Test
	public void testMasterDetailClass() throws Exception {
		Customer c = new Customer();
		c.setName("John Doe");
		c.setAge(32);
		c.setDateOfBirth(new Date());
		c.setActive(true);
		c.setPhoneList(new ArrayList<Phone>());

		Phone p = new Phone();
		p.setNumber("111-111-1111");
		p.setType(new PhoneType(1, "cell"));
		c.getPhoneList().add(p);
		p = new Phone();
		p.setNumber("222-222-2222");
		p.setType(new PhoneType(2,"main"));
		c.getPhoneList().add(p);


		JsonMapper jb = new JsonMapper();
		System.out.println(jb.serializeAsString(c));
	}
	@Test
	public void testMaskClass() throws Exception {
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
		JsonMask mask = new JsonMask("{\"include\": [\"name\", \"phoneList.number\", \"phoneList.type.name\"]}");
		jb.setMask(mask);
		System.out.println(jb.serializeAsString(c));
	}
}
