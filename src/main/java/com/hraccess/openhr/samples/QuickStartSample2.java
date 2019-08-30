package com.hraccess.openhr.samples;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.hraccess.openhr.HRApplication;
import com.hraccess.openhr.HRLoggingSystem;
import com.hraccess.openhr.HRSessionFactory;
import com.hraccess.openhr.IHRSession;
import com.hraccess.openhr.IHRUser;
import com.hraccess.openhr.beans.HRDataSourceParameters;
import com.hraccess.openhr.dossier.HRDossier;
import com.hraccess.openhr.dossier.HRDossierCollection;
import com.hraccess.openhr.dossier.HRDossierCollectionParameters;
import com.hraccess.openhr.dossier.HRDossierFactory;
import com.hraccess.openhr.dossier.HRKey;
import com.hraccess.openhr.dossier.HROccur;
import com.hraccess.openhr.event.SessionChangeEvent;
import com.hraccess.openhr.event.SessionChangeListener;

public class QuickStartSample2 {
	
	public static void main(String[] args) throws Exception {
		// Configuring logging system from given Log4J property configuration file
		HRApplication.configureLogs("src/main/resources/conf/log4j.properties");
		
		// Instance file
		File file = new File("C:\\Dev\\openhr\\conf\\openhr.properties");
		
		// Creating a new configuration
		Configuration configuration = new PropertiesConfiguration();
		
		System.out.println("	*******	Cofiguration de openhr *********");
		// Populating the configuration
		configuration.setProperty("session.language", "U");
		configuration.setProperty("session_work_directory", "src/main/resources/conf/work");
		configuration.setProperty("session_process_list", "F000,F001");
		configuration.setProperty("openhr_server.server", "dlnxhradev02.ptx.fr.sopra");
		configuration.setProperty("normal_message_sender.security", "disabled");
		configuration.setProperty("normal_message_sender.port", "8800");
		configuration.setProperty("sensitive_message_sender.security", "disabled");
		configuration.setProperty("sensitive_message_sender.port", "8800");
		configuration.setProperty("privilegied_message_sender.security", "disabled");
		configuration.setProperty("privilegied_message_sender.port", "8800");
		
		System.out.println("	*******	Cofiguration de la log *********");
		// Configuring logging system to use Log4J
		HRApplication.setLoggingSystem(HRLoggingSystem.LOG4J);
		// Configuring logging system to use Commons Logging
		/*HRApplication.setLoggingSystem(HRLoggingSystem.COMMONS_LOGGING);*/
		// Configuring logging system to use standard output
		/*HRApplication.setLoggingSystem(HRLoggingSystem.STANDARD);*/
		
		// Configuring Log4J from given configuration file
		HRApplication.configureLogs("src/main/resources/conf/log4j.properties");
					
		// Creating from given OpenHR configuration file and connecting session to HR Access server
		IHRSession session = HRSessionFactory.getFactory().createSession(new PropertiesConfiguration("src/main/resources/conf/openhr.properties"));
		IHRUser user = null;
		
		try {
			// Connecting user with given login ID and password
			/*user = session.connectUser("MJALLOUF", "HR");
			System.out.println("Paramètres de connection : ");
			System.out.println("	Login :	MJALLOUF	|	Password : 	HR");
			System.out.println();*/
			
			// Creating configuration to handle HR Access employee dossiers
			HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();
			parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
			parameters.setProcessName("FS001"); // Using process FS001 to read and update dossiers
			parameters.setDataStructureName("ZY"); // Dossiers are based on data structure ZY
			System.out.println("Configuration handle HR Access employee dossier : ");
			System.out.println("	Type 		:	" + HRDossierCollectionParameters.TYPE_NORMAL);
			System.out.println("	Process		: 	FS001");
			System.out.println("	Structure	: 	ZY");
			System.out.println();
			
			System.out.println("	***************		Listener	 ***************	");
			SessionChangeListener listener = new SessionChangeListener() {
				public void sessionStateChanged(SessionChangeEvent event) {
					IHRSession session = (IHRSession) event.getSource();
					if (!session.isConnected()) {
						// Process the session's disconnection event (not detailed here)
						// session.disconnect();
					}
				}
			};
			
			session.addSessionChangeListener(listener);
			
			// Disconnecting the session
			session.disconnect();
			
			// For remove listener
			session.removeSessionChangeListener(listener);
			
		} finally {
			if ((user != null) && user.isConnected()) {
				// Disconnecting user
				//user.disconnect();
			}
			if ((session != null) && session.isConnected()) {
				// Disconnecting OpenHR session
				session.disconnect();
			}
		}
	}
}
