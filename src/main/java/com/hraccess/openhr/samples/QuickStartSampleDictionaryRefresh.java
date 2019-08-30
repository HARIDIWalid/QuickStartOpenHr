package com.hraccess.openhr.samples;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.hraccess.openhr.HRApplication;
import com.hraccess.openhr.HRDictionaryElementType;
import com.hraccess.openhr.HRLoggingSystem;
import com.hraccess.openhr.HRSessionFactory;
import com.hraccess.openhr.IHRDataStructure;
import com.hraccess.openhr.IHRDictionary;
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

public class QuickStartSampleDictionaryRefresh {
	
	public static void main(String[] args) throws Exception {
		
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
		
		try {
			IHRDictionary dictionary = session.getDictionary();
	            
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("*********  Rafraîchissement du dictionnaire	(refresh)	*********" );
			// Refreshing the dictionary (the two method calls are equivalent)
			//System.out.println("	Refreshing dictionary ... Dictionary updated ? " + dictionary.refresh());
			//System.out.println("	Refreshing dictionary ... Dictionary updated ? " + dictionary.refresh(false));
			// Forcing a dictionary refresh
			//System.out.println("	Refreshing dictionary (forced) ... Dictionary updated ? " + dictionary.refresh(true));
			
			System.out.println();
			System.out.println("*********  Modification de dictionnaires	*********" );
			System.out.println("      Ajouter un processus ... ");
			// Adding one process to the dictionary's process list
			dictionary.addProcess("AS100");
			System.out.println("	Ajouter une liste de processus ... ");
			// Adding some processes to the dictionary's process list
			dictionary.addProcesses(Arrays.asList(new String[] { "AS800", "AG0GD" }));
			// Setting the dictionary's process list
			dictionary.setProcesses(Arrays.asList(new String[] { "AS100", "AS800", "AG0GD", "AD9TA" }));
			// Refreshing the dictionary
			dictionary.refresh();
					
			System.out.println();
			System.out.println("	Modification de la langue ... ");
			// Modifying the dictionary's language
			//dictionary.setLanguage("F");
			//dictionary.setLanguage("F,U");
			// Refreshing the dictionary
			//dictionary.refresh();
			
			System.out.println();
			System.out.println("	Modification de la taille des paquets a rafraîchir ... ");
			// Logging the initial value
			System.out.println("		The initial refresh packet size is " + dictionary.getRefreshPacketSize());
			// Updating the refreshPacketSize property
			dictionary.setRefreshPacketSize(100);
			// Logging the new value
			System.out.println("		The new refresh packet size is " + dictionary.getRefreshPacketSize());
			// Is updating dictionary
			System.out.println("		Tester si le dictionnaire doit être rafraîchi  " + dictionary.isUpToDate());
			
			
			System.out.println();
			System.out.println();

		} finally {
			if ((session != null) && session.isConnected()) {
				// Disconnecting OpenHR session
				session.disconnect();
			}
		}
	}
}
