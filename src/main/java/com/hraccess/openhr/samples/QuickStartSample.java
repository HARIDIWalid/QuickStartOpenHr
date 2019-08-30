package com.hraccess.openhr.samples;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.hraccess.openhr.HRApplication;
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

public class QuickStartSample {
	
	public static void main(String[] args) throws Exception {
		// Configuring logging system from given Log4J property configuration file
		HRApplication.configureLogs("src/main/resources/conf/log4j.properties");
		
		// Creating from given OpenHR configuration file and connecting session to HR Access server
//		IHRSession session = HRSessionFactory.getFactory().createSession(new PropertiesConfiguration("C:\\Dev\\openhr\\conf\\openhr.properties"));
		IHRSession session = HRSessionFactory.getFactory().createSession(new PropertiesConfiguration("src/main/resources/conf/openhr.properties"));
		IHRUser user = null;
		
		try {
		
			// Connecting user with given login ID and password
//			user = EnvironmentManager.connectUser("MOSASSIF", "HR");
			user = session.connectUser("MJALLOUF", "HR");
			System.out.println("Paramètres de connection : ");
			System.out.println("	Login 		:	MJALLOUF");
			System.out.println("	Password	: 	HR");
			System.out.println();
			
			// Creating configuration to handle HR Access employee dossiers
			HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();
			parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
			parameters.setProcessName("FS001"); // Using process FS001 to read and update dossiers
			parameters.setDataStructureName("ZY"); // Dossiers are based on data structure ZY
			System.out.println("Configuration ti handle HR Access employee dossier : ");
			System.out.println("	Type 		:	" + HRDossierCollectionParameters.TYPE_NORMAL);
			System.out.println("	Process		: 	FS001");
			System.out.println("	Structure	: 	ZY");
			System.out.println();
			
			// Reading data sections ZY00 (ID) and ZY10 (Birth)
			parameters.addDataSection(new HRDataSourceParameters.DataSection("00"));
			parameters.addDataSection(new HRDataSourceParameters.DataSection("10"));
			System.out.println("Reading data sections ZY00 (ID) and ZY10 (Birthday) ");
			System.out.println();
			
			// Instantiating a new dossier collection with given role, conversation and configuration
			String roleId = "MMGRHIE";
			HRDossierCollection dossierCollection = new HRDossierCollection(parameters,
			user.getMainConversation(),
			user.getRole(roleId), 
			new HRDossierFactory(HRDossierFactory.TYPE_DOSSIER));
			System.out.println("	Instantiating a new dossier collection with given role |  Rolle :	EMPLOYEE(FRLFRP1048)");
			System.out.println();
			
			// Loading an employee dossier from given functional key (policy group, employee ID)
			HRDossier employeeDossier = dossierCollection.loadDossier(new HRKey("HRA","123456"));
			System.out.println("	Loading an employee from given functional key : ");
			System.out.println("		policy group:	'HRA',  employee ID:	'123456'");
			System.out.println();
			
			System.out.println(" *******	Result	******* ");
			// Retrieving the unique occurrence of data section ZY10 (Birth)
//			HROccur birthOccurrence = employeeDossier.getDataSectionByName("10").getOccur();
			
			// Updating the employee's birth date (Item ZY10 DATNAI)
//			System.out.println("	Retrieving the unique occurrence of data section ZY10 (Birthday) : " + birthOccurrence.getDate("DATNAI"));
			
			// Committing the changes to the HR Access server
			employeeDossier.commit();
		} finally {
			if ((user != null) && user.isConnected()) {
				// Disconnecting user
				user.disconnect();
			}
			if ((session != null) && session.isConnected()) {
				// Disconnecting OpenHR session
				session.disconnect();
			}
		}
	}
}
