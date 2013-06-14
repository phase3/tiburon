package com.phase3.logic;

import com.phase3.businesslogic.*;
import org.slf4j.*;

import javax.ws.rs.core.*;

/**
 * Project: tiburon
 * User:    cgh
 * Created: 6/14/13
 */
public class LogicFactory {
	private static final Logger log = LoggerFactory.getLogger(LogicFactory.class);

	public static Logic getLogic(SecurityContext securityContext, Class c) throws Exception {
		log.trace("getLogic (" + c + ")");
		Logic logic =  (Logic)c.getClassLoader().loadClass(c.getName() + "Logic").newInstance();

		logic.setPrincipal(securityContext.getUserPrincipal());
		// todo assign role/permission context
		return logic;
	}
}
