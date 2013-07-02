package com.phase3.businesslogic;

import com.phase3.model.hibernate.*;
import org.hibernate.*;

import java.security.*;

/**
 * Project: tiburon
 * User:    cgh
 * Created: 6/14/13
 */
public abstract class Logic {
	SessionFactory factory = null;
	private transient Principal principal;

	protected Logic() {
		factory = HibernateConfiguration.init("jdbc:mysql://localhost/tiburon","tib","tib");
	}

	public abstract String getAll(String path);
	public abstract String getOne(String path, String id);
	public abstract String create(String path, String content);
	public abstract String update(String path, String id, String content);

	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	public Principal getPrincipal() {
		return principal;
	}
}
