package com.phase3.businesslogic;

import com.phase3.businesslogic.cache.*;
import com.phase3.jsonbind.*;
import com.phase3.model.*;
import org.hibernate.*;
import org.slf4j.*;

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

	public String getAll(String path) {
        String json = CacheService.getCache().getCached(path);
        if (json == null) {
            // get all customers
            Session session = factory.openSession();
            Criteria search = session.createCriteria(Customer.class);
            List<Customer> list  = search.list();
            try {
                json = mapper.serializeListAsString(list);
                CacheService.getCache().cache(path, json);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                return null;
            } finally {
                session.close();
            }
        }
        return json;
	}
	public String getOne(String path, String id) {
        String json = CacheService.getCache().getCached(path);
        if (json == null) {
            //get a single customer based on id
            Session session = factory.openSession();
            Object o = session.get(Customer.class, new Long(id));
            try {
                json =  mapper.serializeAsString(o);
                CacheService.getCache().cache(path, json);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                return null;
            } finally {
                session.close();
            }
        }
        return json;
	}
	public String create (String path, String content) {
        CacheService.getCache().invalidate(path);

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
	public String update(String path, String id, String content) {
        CacheService.getCache().invalidate(path); // path: /customers/{id}
        CacheService.getCache().invalidate(path.substring(0,path.lastIndexOf("/")-1)); // path: /customers

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
