package com.hraccess.openhr.samples;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.hraccess.openhr.AccessMode;
import com.hraccess.openhr.HRApplication;
import com.hraccess.openhr.HRSessionFactory;
import com.hraccess.openhr.IHRSession;
import com.hraccess.openhr.IHRSessionUser;
import com.hraccess.openhr.IHRUser;
import com.hraccess.openhr.UpdateMode;
import com.hraccess.openhr.beans.HRDataSourceParameters;
import com.hraccess.openhr.dossier.HRDataSect;
import com.hraccess.openhr.dossier.HRDossier;
import com.hraccess.openhr.dossier.HRDossierCollection;
import com.hraccess.openhr.dossier.HRDossierCollectionException;
import com.hraccess.openhr.dossier.HRDossierCollectionParameters;
import com.hraccess.openhr.dossier.HRDossierFactory;
import com.hraccess.openhr.dossier.HROccur;

public class QuickStartSampleGestionDossier {
	
	public static void main(String[] args) throws Exception {
		
		// Configuring logging system from given Log4J property configuration file
		HRApplication.configureLogs("src/main/resources/conf/log4j.properties");
				
		IHRSession session = HRSessionFactory.getFactory().createSession(new PropertiesConfiguration("src/main/resources/conf/openhr.properties"));
		IHRUser user = null;
		
		try {
			
			
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("*********  Gestion des dossiers	*********" );
			// Creating a new (secondary) conversation from an existing user

			System.out.println();
			// Connecting the user by providing an hint and a precision to resolve the roles
			user = session.connectUser("MJALLOUF", "HR");
			IHRSessionUser sessionUser = session.getSessionUser(user);
			System.out.println("	Paramètres de connection : ");
			System.out.println("		Login 		:	MJALLOUF");
			System.out.println("		Password	: 	HR");
			System.out.println();
			System.out.println("	Création d'une collection de dossier " );
			

			String processName = "AS000";
			HRDossier dossier = getDossier(user, sessionUser, processName);

			if (dossier == null) {
				throw new RuntimeException("		Unable to retrieve ZY dossier mapped to user <" + user.getUserId() + ">");
			}

			HRDataSect dataSectZY4I = dossier.getDataSectionByName("4I");

			if (dataSectZY4I == null) {
				throw new RuntimeException("		Unable to retrieve data section ZY4I");
			}

			HROccur occur = dataSectZY4I.getOccur(0);
			
			String lognid = (String) occur.getValue("LOGNID");
			
			System.out.println("		lognid : "+lognid);
			
			System.out.println();
			System.out.println();
			System.out.println();
			
			System.out.println();
			System.out.println();
			System.out.println();

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
	
	
	public static HRDossier getDossier(IHRUser user, IHRSessionUser sessionUser, String processName) {
		// TODO Rechercher le dossier en cache
		HRDossier dossier = null;

		if (dossier == null) {
			// Dossier absent du cache, le charger depuis le serveur
			dossier = loadDossier(user, sessionUser, processName);
		}

		return dossier;
	}
	
	public static HRDossier loadDossier(IHRUser user, IHRSessionUser sessionUser, String processName) {
		HRDataSourceParameters.DataSection dataSection00 = new HRDataSourceParameters.DataSection("00");
		HRDataSourceParameters.DataSection dataSection4I = new HRDataSourceParameters.DataSection("4I");

		HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();
		parameters.setAccessMode(AccessMode.ONE_SHOT);
		parameters.setDataStructureName("ZY");
		parameters.setUpdateMode(UpdateMode.NORMAL);
		parameters.setIgnoreSeriousWarnings(false);
		parameters.setProcessName(processName);
		parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
		parameters.addDataSection(dataSection00);
		parameters.addDataSection(dataSection4I);

		HRDossierCollection dossierCollection = new HRDossierCollection(parameters, sessionUser.getMainConversation(),
				sessionUser.getRole("MMGRHIE"), new HRDossierFactory(HRDossierFactory.TYPE_DOSSIER));

		// Dossier du salarié
		HRDossier dossier = null;

		// NUDOSS du dossier ZY à  charger ?
		int nudoss = user.getDescription().getUserDossierNudoss();

		if (nudoss == 0) {
			// Cet utilisateur n'est mappé à  aucun dossier de salarié !
			// Impossible de charger son dossier
			throw new RuntimeException(
					"Unable to retrieve user's dossier since it's not mapped to a ZY dossier (NUDOSS is null)");
		}

		try {
			dossier = dossierCollection.loadDossier(nudoss);
		} catch (HRDossierCollectionException e) {
			throw new RuntimeException("Error when loading user's dossier with NUDOSS <" + nudoss + ">", e);
		}

		if (dossier == null) {
			throw new RuntimeException("Unable to load the user's dossier with NUDOSS <" + nudoss + ">");
		}

		return dossier;
	}
}
