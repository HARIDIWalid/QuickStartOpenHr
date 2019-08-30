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

public class QuickStartSampleDataStructure {
	
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
			System.out.println("*********  Manupulation des structures de données	*********" );
			System.out.println("	Proprieties de Data Structure ");
			// Retrieving the data structure with name "ZY" (representing an employee dossier)
			IHRDataStructure dataStructure = dictionary.getDataStructureByName("ZY");
			// Dumping data structure's properties
			System.out.println("		Name is " + dataStructure.getName());
			System.out.println("		Label is " + dataStructure.getLabel());
			System.out.println("		Data structure type is " + dataStructure.getType());
			System.out.println("		Data structure was last modified on " + new Date(dataStructure.getLastUpdateTimestamp()));
			System.out.println("		Key items are " + dataStructure.getKeyItems());
			System.out.println("		Item for dossier type is " + dataStructure.getDossierTypeItem());
			System.out.println("		Rule system item is " + dataStructure.getRuleSystemItem());
			if (dataStructure.getRuleSystemDataStructure() == null) {
				System.out.println("		Data structure has no defined rule system data structure");
			} else {
				System.out.println("		Data structure is mapped to rule system data structure " + dataStructure.getRuleSystemDataStructure().getName());
			}
			System.out.println("		Rule system data structure ? " + dataStructure.isRuleSystemDataStructure());
			
			System.out.println();
			System.out.println("	Récupération des informations ");
			System.out.println("		Rule system data structure ? " + dataStructure.isRuleSystemDataStructure());
			// Retrieving the data structure's data sections as a List<IHRDataSection>
			List<IHRDataSection> dataSections = dataStructure.getDataSections();
			for(Iterator it=dataSections.iterator(); it.hasNext();) {
				IHRDataSection dataSection = (IHRDataSection) it.next();
				System.out.println("		Found data section <" + dataSection.getName() + ">" + dataSection.getLabel());
			}
			
			System.out.println();
			// Retrieving the data structure's data sections as a Map<String, IHRDataSection>
			Map<String, IHRDataSection> dataSectionsMap = dataStructure.getDataSectionMap();
			for(Iterator it=dataSectionsMap.keySet().iterator(); it.hasNext();) {
				String dataSectionName = (String) it.next();
				IHRDataSection dataSection = (IHRDataSection) dataSectionsMap.get(dataSectionName);
				//System.out.println("		Found data section <" + dataSectionName + "> " + dataSection.getLabel());
			}
			
			System.out.println();
			// Testing the presence of a named data section
			System.out.println("		Data section <ZY00> exists ? " + dataStructure.hasDataSection("00"));
			
			System.out.println();
			// Retrieving the data structure's dossier types as a List<IHRDossierType>
			List<IHRDossierType> dossierTypes = dataStructure.getDossierTypes();
			for(Iterator it= dossierTypes.iterator(); it.hasNext();) {
				IHRDossierType dossierType = (IHRDossierType) it.next();
				//System.out.println("		Found dossier type (List) <" + dossierType.getName() + "> " + dossierType.getLabel());
			}
			System.out.println();
			
			// Retrieving the data structure's dossier types as a Map<String, IHRDossierType>
			Map<String, IHRDossierType> dossierTypesMap = dataStructure.getDossierTypeMap();
			for(Iterator it=dossierTypesMap.keySet().iterator(); it.hasNext();) {
				String dossierTypeName = (String) it.next();
				IHRDossierType dossierType = (IHRDossierType) dossierTypesMap.get(dossierTypeName);
				System.out.println("		Found dossier type (Map) <" + dossierTypeName + "> " + dossierType.getLabel());
			}
			
			System.out.println();
			System.out.println("		Récupération d'un type de dossier par son nom ");
			// Retrieving a named dossier type
			IHRDossierType dossierType = dataStructure.getDossierTypeByName("SAL");
			if (dossierType == null) {
				System.err.println("			Unable to find dossier type <ZYSAL>");
			} else {
				System.out.println("			Found dossier type < ZYSAL > " + dossierType.getLabel());
			}
			System.out.println();
			System.out.println("		Tester l'existence d'un type de dossier ");
			// Testing the presence of a named dossier type
			System.out.println("			Dossier type <ZYSAL> exists ? " + dataStructure.hasDossierType("SAL"));
			
			System.out.println();
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
