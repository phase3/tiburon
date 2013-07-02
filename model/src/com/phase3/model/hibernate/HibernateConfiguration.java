package com.phase3.model.hibernate;

import com.phase3.model.*;
import org.hibernate.*;
import org.hibernate.cfg.*;
import org.hibernate.service.*;
import org.slf4j.*;

import java.util.*;

/**
 * Project: tiburon
 * User:    cgh
 * Created: 6/14/13
 */
public class HibernateConfiguration {
	static Logger log = LoggerFactory.getLogger(HibernateConfiguration.class);

	private static HashMap<String, SessionFactory> factoryMap = new HashMap<String, SessionFactory>();

	public static SessionFactory init(String url, String username, String password) {

		SessionFactory factory = null;
		String showSql = System.getProperty("ShowSql");
		if (showSql == null) {
			showSql = "false";
		}
		String minConnectionSize = System.getProperty("MinSqlConnections");
		if (minConnectionSize == null) {
			minConnectionSize = "5";
		}
		synchronized (factoryMap) {
			factory = factoryMap.get(url + username);
			if (factory == null) {
				Configuration configuration;
				configuration = new Configuration();
				configuration.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
				configuration.setProperty("hibernate.connection.url", url + "?autoReconnect=true");
				configuration.setProperty("hibernate.connection.username", username);
				configuration.setProperty("hibernate.connection.password", password);
				configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
				configuration.setProperty("hibernate.show_sql", showSql);
				try {
					configuration.addAnnotatedClass(User.class);
					configuration.addAnnotatedClass(Customer.class);

					ServiceRegistry registry = new ServiceRegistryBuilder()
							.applySettings(configuration.getProperties()).buildServiceRegistry();

					factory = configuration.buildSessionFactory(registry);
					factoryMap.put(url + username, factory);
				} catch (Exception ex) {
					log.error("Unable to map classes to cvb schema", ex);
				}

			}

		}
		return factory;
	}
}
