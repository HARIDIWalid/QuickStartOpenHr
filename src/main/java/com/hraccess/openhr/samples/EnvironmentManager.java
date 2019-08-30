package com.hraccess.openhr.samples;



import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.hraccess.openhr.HRApplication;
import com.hraccess.openhr.HRLoggingSystem;
import com.hraccess.openhr.HRSessionFactory;
import com.hraccess.openhr.IHRSession;
import com.hraccess.openhr.IHRUser;
import com.hraccess.openhr.exception.AuthenticationException;
import com.hraccess.openhr.exception.SessionBuildException;
import com.hraccess.openhr.exception.SessionConnectionException;
import com.hraccess.openhr.exception.UserConnectionException;

public class EnvironmentManager {
	
	private static IHRSession session;
	private static IHRUser user;
	
	public static IHRSession createSession() {
		
//		HRApplication.setLoggingSystem(HRLoggingSystem.LOG4J);
		// Configuring logging system from given Log4J property configuration file
		HRApplication.configureLogs("src/main/resources/conf/log4j.properties");
		
		if(session == null){
			Configuration configuration = null;
			try {
				configuration = new PropertiesConfiguration("src/main/resources/conf/openhr.properties");
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				session = HRSessionFactory.getFactory().createSession(configuration);
			} catch (SessionBuildException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SessionConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return session;
	}

	public static IHRUser connectUser(String login, String pwd) {
		
		if(session == null){
			createSession();
		}
		
		//IHRUser user = null;
		try {
			user = session.connectUser(login, pwd);
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UserConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return user;
	}

	public static void closeSession() {
		if(session.isConnected()){
			session.disconnect();
		}
	}
	
	// Add by Achraf
	public static IHRSession getsession() {
		return session;
	}
	
	public static IHRUser getConnectUser() {
		
		return user;
	}

}
