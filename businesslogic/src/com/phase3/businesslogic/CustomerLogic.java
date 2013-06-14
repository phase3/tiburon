package com.phase3.businesslogic;

import com.phase3.jsonbind.*;
import com.phase3.model.*;
import org.hibernate.*;
import org.slf4j.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Project: tiburon
 * User:    cgh
 * Created: 6/14/13
 */
public class CustomerLogic extends Logic {
	private static final Logger log = LoggerFactory.getLogger(CustomerLogic.class);
	private JsonMapper mapper = new JsonMapper();

	public CustomerLogic() {
		super();
	}

	public String getAll() {
		// get all customers
		Session session = factory.openSession();
		Criteria search = session.createCriteria(Customer.class);
		List<Customer> list  = search.list();

		try {
			return mapper.serializeListAsString(list);
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return null;
		} finally {
			session.close();
		}
	}
	public String getOne(String id) {
		//get a single customer based on id
		Session session = factory.openSession();
		Object o = session.get(Customer.class, new Long(id));
		try {
			return mapper.serializeAsString(o);
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return null;
		} finally {
			session.close();
		}
	}
	public String create (String content) {
		// create a new customer
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		try {
			Customer c = mapper.deserialize(content, Customer.class);
			session.save(c);
			t.commit();
			return mapper.serializeAsString(c);
		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return null;
		} finally {
			session.close();
		}
	}
	public String update(String id, String content) {
		// update a customer
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		try {
			Customer original = (Customer) session.get(Customer.class, new Long(id));

			Customer customer = mapper.deserialize(content,Customer.class);
			original.mergeUpdate(customer, session);

			session.saveOrUpdate(original);
			t.commit();
			return mapper.serializeAsString(original);
		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return null;
		} finally {
			session.close();
		}
	}
}
