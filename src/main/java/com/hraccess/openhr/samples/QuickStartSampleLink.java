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
import com.hraccess.openhr.IHRDataSection;
import com.hraccess.openhr.IHRDataStructure;
import com.hraccess.openhr.IHRDictionary;
import com.hraccess.openhr.IHRDossierType;
import com.hraccess.openhr.IHRLink;
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
import com.hraccess.openhr.message.parser.callback.impl.DictionaryCallback;

public class QuickStartSampleLink {
	
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
			
			// Create instance of Dictionary
			IHRDictionary dictionary = session.getDictionary();
	          
			// Create instance of DataSource
			IHRDataStructure dataStructure = dictionary.getDataStructureByName("ZY");
			
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("*********  Manupulation des liens	*********" );
			System.out.println("	Récupération des liens ");
			System.out.println("	Récupération des liens (List)");
			// Retrieving the data structure's links as a List<IHRLink>
			List<IHRLink> links = dataStructure.getLinks();
			for(Iterator it= links.iterator(); it.hasNext();) {
				IHRLink link = (IHRLink) it.next();
			//	System.out.println("Found link <" + link.getName() + "> " + link.getLabel());
			}
			
			System.out.println();
			System.out.println("	Récupération des liens (Map)");
			// Retrieving the data structure's links as a Map<String, IHRLink>
			Map<String, IHRLink> linksMap = dataStructure.getLinkMap();
			for(Iterator it=linksMap.keySet().iterator(); it.hasNext();) {
				String linkName = (String) it.next();
				IHRLink link = (IHRLink) linksMap.get(linkName);
				System.out.println("		Found link <" + linkName + "> " + link.getLabel());
			}
			
			System.out.println();
			System.out.println("	Récupération d'un lien par son nom");
			// Retrieving a named link
			IHRLink link = dataStructure.getLinkByName("10");
			if (link == null) {
				System.err.println("		Unable to find link <ZY10>");
			} else {
				System.out.println("		Found link <ZY10> " + link.getLabel());
			}
			
			System.out.println();
			System.out.println("	Tester l'existence d'un lien");
			// Testing the presence of a named link
			System.out.println("		Link <ZY10> exists ? " + dataStructure.hasLink("10"));
			
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
