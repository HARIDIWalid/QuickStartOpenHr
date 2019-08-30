package com.hraccess.openhr.samples;

import java.io.File;
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

public class QuickStartSampleDictionary {
	
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
			System.out.println("*********  Tests proprieties	*********" );
			System.out.println("-------->    getCompilationTimestamp() : " + dictionary.getCompilationTimestamp());
			System.out.println("-------->    dictionary.getLanguage() : " + dictionary.getLanguage());
			System.out.println("-------->    dictionary.getProcesses() : " + dictionary.getProcesses());
			
			// Dumping some of the dictionary's properties
			System.out.println("-------->    The dictionary was created on " + new Date(dictionary.getCompilationTimestamp()));
			System.out.println("-------->    The dictionary's default language is <" + dictionary.getLanguage() + ">");
			System.out.println("-------->    The dictionary's processes are <" + dictionary.getProcesses() + ">");
			System.out.println("-------->    The dictionary's refresh packet size is " + dictionary.getRefreshPacketSize());
			System.out.println("-------->    Keeping dictionary's unused entities ? " + dictionary.isKeepUnusedEntities());
			System.out.println();
			
			
			System.out.println("*********  Retrieving the dictionary's data structures as a List<IHRDataStructure>	*********" );
			// Retrieving the dictionary's data structures as a List<IHRDataStructure>
			List<IHRDataStructure> dataStructures = dictionary.getDataStructures();
			for(Iterator it=dataStructures.iterator(); it.hasNext();) {
				IHRDataStructure dataStructure = (IHRDataStructure) it.next();
				System.out.println("	Found data structure <" + dataStructure.getName() + "> " + dataStructure.getLabel());
			}
			System.out.println();
			
			System.out.println("*********  Retrieving the dictionary's data structures as a Map<String, IHRDataStructure>	*********" );
			// Retrieving the dictionary's data structures as a Map<String, IHRDataStructure> 
			Map<String, IHRDataStructure>  dataStructuresMap = dictionary.getDataStructureMap();
			
			for(Iterator it=dataStructuresMap.keySet().iterator(); it.hasNext();) {
				String dataStructureName = (String) it.next();
				IHRDataStructure dataStructure = (IHRDataStructure)
				dataStructuresMap.get(dataStructureName);
				System.out.println("	Found data structure <" + dataStructureName + "> " + dataStructure.getLabel());
			}

			System.out.println();
			System.out.println("*********  Recupération d'une structure de données par son nom	*********" );
			// Retrieving a named data structure
			IHRDataStructure dataStructure = dictionary.getDataStructureByName("ZY");
			if (dataStructure == null) {
				System.err.println("	Unable to find data structure <ZY>");
			} else {
				System.out.println("	Found data structure <ZY> " + dataStructure.getLabel());
			}
			
			System.out.println();
			System.out.println("*********  Tester l'exentence d'une structure de données	*********" );
			// Testing the presence of a named data structure
			System.out.println("	Data structure <ZE> exists ? " + dictionary.hasDataStructure("ZE"));
			
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
