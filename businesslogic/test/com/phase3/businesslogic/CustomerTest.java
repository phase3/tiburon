package com.phase3.businesslogic;

import org.apache.log4j.*;
import org.junit.*;

import java.util.*;

/**
 * Project: tiburon
 * User:    cgh
 * Created: 6/14/13
 */
public class CustomerTest {
	@Before
	public void before(){
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);

	}

	@Test
	public void testGetAll() {
		CustomerLogic logic = new CustomerLogic();
		System.out.println(logic.getAll());
	}
//	@Test
//	public void testGetOne() {
//		CustomerLogic logic = new CustomerLogic();
//		System.out.println(logic.getOne("1"));
//	}
//	@Test
//	public void testCreate() {
//		CustomerLogic logic = new CustomerLogic();
//		String s = "{\"fullName\":\"Created "+new Date().getTime() + "\", \"creator\": {\"uid\": 1}}";
//
//		System.out.println(logic.create(s));
//	}
//	@Test
//	public void testUpdate() {
//		CustomerLogic logic = new CustomerLogic();
//		String s = "{\"fullName\":\"Updated "+new Date().getTime() + "\", \"creator\": {\"uid\": 2}}";
//		//String s = "{\"fullName\":\"Updated "+new Date().getTime() + "\", \"creator\": null}";
//		System.out.println(logic.update("7",s));
//	}
}
