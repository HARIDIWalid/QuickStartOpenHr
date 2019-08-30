package com.hraccess.openhr.samples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hraccess.datasource.TDataNode;
import com.hraccess.openhr.AccessMode;
import com.hraccess.openhr.HRApplication;
import com.hraccess.openhr.IHRRole;
import com.hraccess.openhr.IHRSession;
import com.hraccess.openhr.IHRSessionUser;
import com.hraccess.openhr.IHRUser;
import com.hraccess.openhr.UpdateMode;
import com.hraccess.openhr.beans.HRDataSourceParameters;
import com.hraccess.openhr.beans.HRExtractionSource;
import com.hraccess.openhr.blob.IHRBlob.Key;
import com.hraccess.openhr.blob.IHRBlob.Storage;
import com.hraccess.openhr.dossier.HRDataSect;
import com.hraccess.openhr.dossier.HRDossier;
import com.hraccess.openhr.dossier.HRDossierCollection;
import com.hraccess.openhr.dossier.HRDossierCollectionException;
import com.hraccess.openhr.dossier.HRDossierCollectionParameters;
import com.hraccess.openhr.dossier.HRDossierFactory;
import com.hraccess.openhr.dossier.HROccur;
import com.hraccess.openhr.msg.HRMsgGetBlobs;
import com.hraccess.openhr.msg.HRResultGetBlobs;
import com.hraccess.selfservice.SelfConfigurator;
import com.hraccess.selfservice.SelfWebAppConfiguration;
import com.hraccess.selfservice.api.ExecutionResult;
import com.hraccess.selfservice.api.GPManager;
import com.hraccess.selfservice.api.Instance;
import com.hraccess.selfservice.api.InstanceManager;
import com.hraccess.selfservice.api.Notification;
import com.hraccess.selfservice.api.NotificationManager;
import com.hraccess.selfservice.api.Request;
import com.hraccess.selfservice.api.RequestManager;
import com.hraccess.selfservice.api.SelfServiceApi;
import com.hraccess.selfservice.api.Task;
import com.hraccess.selfservice.api.TaskManager;

public class QuickStartSampleAll {
	
	
	IHRUser user = null;
	IHRSessionUser sessionUser = null;
	public static void main(String[] args) throws Exception {
		// Configuring logging system from given Log4J property configuration file
		HRApplication.configureLogs("src/main/resources/conf/log4j.properties");
		
		
		IHRSession session = EnvironmentManager.createSession();
		IHRUser user = EnvironmentManager.connectUser("MJALLOUF", "HR");

		IHRSessionUser sessionUser = session.getSessionUser(user);

		

		
	}
	
	/**
	 * Read information ZY10DATNAI
	 */
	public void readInformation() {
		
		String processName = "FS001";
		HRDossier dossier = getDossier(user, sessionUser, processName);

		System.out.println();
		if (dossier == null) {
			throw new RuntimeException("Unable to retrieve ZY dossier mapped to user <" + user.getUserId() + ">");
		}

		HRDataSect dataSectZY10 = dossier.getDataSectionByName("10");
		if (dataSectZY10 == null) {
			throw new RuntimeException("Unable to retrieve data section ZY10");
		}
		
		HROccur occur = dataSectZY10.getOccur(0);
		Date dateNaissance = occur.getDate("DATNAI");
		
		System.out.println();
		System.out.println("***	Lecture de l'information ZY10DATNAI ***");
		System.out.println(" 		User 			: " + user.getLabel());
		System.out.println(" 		Mot de passe 	: HR");
		System.out.println(" 		User ID		 	: " + user.getUserId());
		System.out.println(" 		Process 		: " + processName);
		System.out.println("		Date de naissance ZY10DATNAI : " + dateNaissance);
	}
	
	/**
	 * Retrieve dossier employees
	 * @param user					Current user
	 * @param sessionUser			Current user session
	 * @param processName			Process
	 * @return {@link HRDossier} 	employee dossier
	 */
	public static HRDossier getDossier(IHRUser user, IHRSessionUser sessionUser, String processName) {
		// TODO Rechercher le dossier en cache
		HRDossier dossier = null;

		if (dossier == null) {
			// Dossier absent du cache, le charger depuis le serveur
			dossier = loadDossier(user, sessionUser, processName);
		}

		return dossier;
	}
	
	/**
	 * Load employee dossier
	 * @param user					Current user
	 * @param sessionUser			Current user session
	 * @param processName			Process
	 * @return {@link HRDossier} 	employee dossier
	 */
	public static HRDossier loadDossier(IHRUser user, IHRSessionUser sessionUser, String processName) {
		HRDataSourceParameters.DataSection dataSection00 = new HRDataSourceParameters.DataSection("00");
		HRDataSourceParameters.DataSection dataSection2N = new HRDataSourceParameters.DataSection("10");

		HRDossierCollectionParameters parameters = new HRDossierCollectionParameters();
		parameters.setAccessMode(AccessMode.ONE_SHOT);
		parameters.setDataStructureName("ZY");
		parameters.setUpdateMode(UpdateMode.NORMAL);
		parameters.setIgnoreSeriousWarnings(false);
		parameters.setProcessName(processName);
		parameters.setType(HRDossierCollectionParameters.TYPE_NORMAL);
		parameters.addDataSection(dataSection00);
		parameters.addDataSection(dataSection2N);

		HRDossierCollection dossierCollection = new HRDossierCollection(parameters, 
				sessionUser.getMainConversation(),
				sessionUser.getRole(), 
				new HRDossierFactory(HRDossierFactory.TYPE_DOSSIER));

		// Dossier du salarié
		HRDossier dossier = null;

		// NUDOSS du dossier ZY à charger ?
//		int nudoss = user.getDescription().getUserDossierNudoss();
		int nudoss = 731;

		if (nudoss == 0) {
			// Cet utilisateur n'est mappé à aucun dossier de salarié !
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
	
	private static String getImageUrl(int nudoss, IHRUser user, IHRRole role) {
		File blob=null;
		String imageUrl="";
		try {
			
				
				HRMsgGetBlobs getBlobsMsg = new HRMsgGetBlobs();
				String processId = "FS001";
				getBlobsMsg.setDesignProcess(processId);
				Key blobKey = new Key(nudoss, "ZY", "00", "BLOB01");
				
				
				getBlobsMsg.addBlob(blobKey,Storage.DATABASE, null);
				HRResultGetBlobs resultBlobs = (HRResultGetBlobs) user.getMainConversation().send(getBlobsMsg, role);
				String fileName = resultBlobs.getBlobFilename(blobKey);

				
				try {
					 blob = resultBlobs.getBlob(new Key(nudoss, "ZY", "00",
							"BLOB01"));
					 
					 imageUrl = "HRBlob?TYPE=GET_BLOB&BLOB_KEY="+nudoss+"_ZY00BLOB01_0&PROCESS="+processId+"&FILENAME=person.ico";
					
				} catch (IOException e) {
					System.out.println("error while reading blob file"+ e);
				}
			
		} catch (Exception e) {
			System.out.println("error while reading blob file"+ e);
			//return Response.status(Status.INTERNAL_SERVER_ERROR);
		}
			
		return imageUrl;
	}

	private static Map<String, String> searchOrgUnitByLabel(IHRUser user, IHRRole role, String inputText, String searchOperator) {

		String query = "";
		if ("Equals".equals(searchOperator))
			query = "select T1.IDOU00, T2.LBOULG from ZE01 T2, ZE2A T1 where T1.nudoss=T2.nudoss AND T1.NBORDR = 0 AND T2.CDLANG='F' and T2.LBOULG ='"
					+ inputText + "'";
		else if ("Not equal".equals(searchOperator))
			query = "select T1.IDOU00, T2.LBOULG from ZE01 T2, ZE2A T1 where T1.nudoss=T2.nudoss AND T1.NBORDR = 0 AND T2.CDLANG='F' and T2.LBOULG !='"
					+ inputText + "'";
		else if ("Contains".equals(searchOperator))
			query = "select T1.IDOU00, T2.LBOULG from ZE01 T2, ZE2A T1 where T1.nudoss=T2.nudoss AND T1.NBORDR = 0 AND T2.CDLANG='F' and T2.LBOULG like '%"
					+ inputText + "%'";
		else if ("Contains not".equals(searchOperator))
			query = "select T1.IDOU00, T2.LBOULG from ZE01 T2, ZE2A T1 where T1.nudoss=T2.nudoss AND T1.NBORDR = 0 AND T2.CDLANG='F' and T2.LBOULG not like '%"
					+ inputText + "%'";
		else if ("Starts with".equals(searchOperator))
			query = "select T1.IDOU00, T2.LBOULG from ZE01 T2, ZE2A T1 where T1.nudoss=T2.nudoss AND T1.NBORDR = 0 AND T2.CDLANG='F' and T2.LBOULG like '"
					+ inputText + "%'";
		else if ("Starts not with".equals(searchOperator))
			query = "select T1.IDOU00, T2.LBOULG from ZE01 T2, ZE2A T1 where T1.nudoss=T2.nudoss AND T1.NBORDR = 0 AND T2.CDLANG='F' and T2.LBOULG not like '"
					+ inputText + "%'";
		else if ("Ends with".equals(searchOperator))
			query = "select T1.IDOU00, T2.LBOULG from ZE01 T2, ZE2A T1 where T1.nudoss=T2.nudoss AND T1.NBORDR = 0 AND T2.CDLANG='F' and T2.LBOULG like '%"
					+ inputText + "'";
		else if ("Ends not with".equals(searchOperator))
			query = "select T1.IDOU00, T2.LBOULG from ZE01 T2, ZE2A T1 where T1.nudoss=T2.nudoss AND T1.NBORDR = 0 AND T2.CDLANG='F' and T2.LBOULG not like '%"
					+ inputText + "'";

		HRExtractionSource extractionSource = new HRExtractionSource();
		extractionSource.setConversation(user.getMainConversation());
		extractionSource.setRole(role);

		Map<String, String> resultMap = new HashMap<String, String>();
		char quote = user.getSession().getDatabaseQuote();
		String queryParsed = query.replace('\'', quote);
		// log.debug("extractSourceSQL --> " + queryParsed);
		extractionSource.setSQLExtraction(queryParsed);
		// Connecting extraction source
		extractionSource.setActive(true);
		String idou = "";
		String uoLabel = "";
		try {
			// Retrieving the result set as a TDataNode
			TDataNode node = extractionSource.getDataNode();
			if (node.getRecordCount() > 0) {
				int i = 0;
				do {
					idou = node.getValue(0).toString();
					uoLabel = node.getValue(1).toString();
					resultMap.put(idou, uoLabel);
					i++;
				} while (node.next());

			}

		} finally {
			if ((extractionSource != null) && extractionSource.isActive()) {
				// Disconnecting extraction source
				extractionSource.setActive(false);
			}
		}

		return resultMap;

	}

	private static Map<String, String> searchOrgUnitByID(IHRUser user,
			IHRRole role, String inputText, String searchOperator) {

		String query = "";
		if ("Equals".equals(searchOperator))
			query = "select T1.IDOU00, T2.LBOULG from ZE01 T2, ZE2A T1 where T1.nudoss=T2.nudoss AND T1.NBORDR = 0 AND T2.CDLANG='F' and T1.IDOU00 ='"
					+ inputText + "'";
		else if ("Not equal".equals(searchOperator))
			query = "select T1.IDOU00, T2.LBOULG from ZE01 T2, ZE2A T1 where T1.nudoss=T2.nudoss AND T1.NBORDR = 0 AND T2.CDLANG='F' and T1.IDOU00 !='"
					+ inputText + "'";
		else if ("Contains".equals(searchOperator))
			query = "select T1.IDOU00, T2.LBOULG from ZE01 T2, ZE2A T1 where T1.nudoss=T2.nudoss AND T1.NBORDR = 0 AND T2.CDLANG='F' and T1.IDOU00 like '%"
					+ inputText + "%'";
		else if ("Contains not".equals(searchOperator))
			query = "select T1.IDOU00, T2.LBOULG from ZE01 T2, ZE2A T1 where T1.nudoss=T2.nudoss AND T1.NBORDR = 0 AND T2.CDLANG='F' and T1.IDOU00 not like '%"
					+ inputText + "%'";
		else if ("Starts with".equals(searchOperator))
			query = "select T1.IDOU00, T2.LBOULG from ZE01 T2, ZE2A T1 where T1.nudoss=T2.nudoss AND T1.NBORDR = 0 AND T2.CDLANG='F' and T1.IDOU00 like '"
					+ inputText + "%'";
		else if ("Starts not with".equals(searchOperator))
			query = "select T1.IDOU00, T2.LBOULG from ZE01 T2, ZE2A T1 where T1.nudoss=T2.nudoss AND T1.NBORDR = 0 AND T2.CDLANG='F' and T1.IDOU00 not like '"
					+ inputText + "%'";
		else if ("Ends with".equals(searchOperator))
			query = "select T1.IDOU00, T2.LBOULG from ZE01 T2, ZE2A T1 where T1.nudoss=T2.nudoss AND T1.NBORDR = 0 AND T2.CDLANG='F' and T1.IDOU00 like '%"
					+ inputText + "'";
		else if ("Ends not with".equals(searchOperator))
			query = "select T1.IDOU00, T2.LBOULG from ZE01 T2, ZE2A T1 where T1.nudoss=T2.nudoss AND T1.NBORDR = 0 AND T2.CDLANG='F' and T1.IDOU00 not like '%"
					+ inputText + "'";

		HRExtractionSource extractionSource = new HRExtractionSource();
		extractionSource.setConversation(user.getMainConversation());
		extractionSource.setRole(role);

		Map<String, String> resultMap = new HashMap<String, String>();
		char quote = user.getSession().getDatabaseQuote();
		String queryParsed = query.replace('\'', quote);
		// log.debug("extractSourceSQL --> " + queryParsed);
		extractionSource.setSQLExtraction(queryParsed);
		// Connecting extraction source
		extractionSource.setActive(true);
		String idou = "";
		String uoLabel = "";
		try {
			// Retrieving the result set as a TDataNode
			TDataNode node = extractionSource.getDataNode();
			if (node.getRecordCount() > 0) {
				int i = 0;
				do {
					idou = node.getValue(0).toString();
					uoLabel = node.getValue(1).toString();
					resultMap.put(idou, uoLabel);
					i++;
				} while (node.next());

			}

		} finally {
			if ((extractionSource != null) && extractionSource.isActive()) {
				// Disconnecting extraction source
				extractionSource.setActive(false);
			}
		}

		return resultMap;

	}

	private static Map<String, String> searchPositionByLabel(IHRUser user,
			IHRRole role, String inputText, String searchOperator) {

		String query = "";
		if ("Equals".equals(searchOperator))
			query = "select T1.IDPS00, T2.LBPSLG from ZA01 T2, ZA00 T1 where T1.nudoss = T2.nudoss and T2.LBPSLG ='"
					+ inputText + "'";
		else if ("Not equal".equals(searchOperator))
			query = "select T1.IDPS00, T2.LBPSLG from ZA01 T2, ZA00 T1 where T1.nudoss = T2.nudoss and T2.LBPSLG !='"
					+ inputText + "'";
		else if ("Contains".equals(searchOperator))
			query = "select T1.IDPS00, T2.LBPSLG from ZA01 T2, ZA00 T1 where T1.nudoss = T2.nudoss and T2.LBPSLG like '%"
					+ inputText + "%'";
		else if ("Contains not".equals(searchOperator))
			query = "select T1.IDPS00, T2.LBPSLG from ZA01 T2, ZA00 T1 where T1.nudoss = T2.nudoss and T2.LBPSLG not like '%"
					+ inputText + "%'";
		else if ("Starts with".equals(searchOperator))
			query = "select T1.IDPS00, T2.LBPSLG from ZA01 T2, ZA00 T1 where T1.nudoss = T2.nudoss and T2.LBPSLG like '"
					+ inputText + "%'";
		else if ("Starts not with".equals(searchOperator))
			query = "select T1.IDPS00, T2.LBPSLG from ZA01 T2, ZA00 T1 where T1.nudoss = T2.nudoss and T2.LBPSLG not like '"
					+ inputText + "%'";
		else if ("Ends with".equals(searchOperator))
			query = "select T1.IDPS00, T2.LBPSLG from ZA01 T2, ZA00 T1 where T1.nudoss = T2.nudoss and T2.LBPSLG like '%"
					+ inputText + "'";
		else if ("Ends not with".equals(searchOperator))
			query = "select T1.IDPS00, T2.LBPSLG from ZA01 T2, ZA00 T1 where T1.nudoss = T2.nudoss and T2.LBPSLG not like '%"
					+ inputText + "'";

		HRExtractionSource extractionSource = new HRExtractionSource();
		extractionSource.setConversation(user.getMainConversation());
		extractionSource.setRole(role);

		Map<String, String> resultMap = new HashMap<String, String>();
		char quote = user.getSession().getDatabaseQuote();
		String queryParsed = query.replace('\'', quote);
		// log.debug("extractSourceSQL --> " + queryParsed);
		extractionSource.setSQLExtraction(queryParsed);
		// Connecting extraction source
		extractionSource.setActive(true);
		String positionID = "";
		String positionLabel = "";
		try {
			// Retrieving the result set as a TDataNode
			TDataNode node = extractionSource.getDataNode();
			if (node.getRecordCount() > 0) {
				int i = 0;
				do {
					positionID = node.getValue(0).toString();
					positionLabel = node.getValue(1).toString();
					resultMap.put(positionID, positionLabel);
					i++;
				} while (node.next());

			}

		} finally {
			if ((extractionSource != null) && extractionSource.isActive()) {
				// Disconnecting extraction source
				extractionSource.setActive(false);
			}
		}

		return resultMap;

	}

	private static Map<String, String> searchPositionByID(IHRUser user,
			IHRRole role, String inputText, String searchOperator) {

		String query = "";
		if ("Equals".equals(searchOperator))
			query = "select T1.IDPS00, T2.LBPSLG from ZA01 T2, ZA00 T1 where T1.nudoss = T2.nudoss and T1.IDPS00 ='"
					+ inputText + "'";
		else if ("Not equal".equals(searchOperator))
			query = "select T1.IDPS00, T2.LBPSLG from ZA01 T2, ZA00 T1 where T1.nudoss = T2.nudoss and T1.IDPS00 !='"
					+ inputText + "'";
		else if ("Contains".equals(searchOperator))
			query = "select T1.IDPS00, T2.LBPSLG from ZA01 T2, ZA00 T1 where T1.nudoss = T2.nudoss and T1.IDPS00 like '%"
					+ inputText + "%'";
		else if ("Contains not".equals(searchOperator))
			query = "select T1.IDPS00, T2.LBPSLG from ZA01 T2, ZA00 T1 where T1.nudoss = T2.nudoss and T1.IDPS00 not like '%"
					+ inputText + "%'";
		else if ("Starts with".equals(searchOperator))
			query = "select T1.IDPS00, T2.LBPSLG from ZA01 T2, ZA00 T1 where T1.nudoss = T2.nudoss and T1.IDPS00 like '"
					+ inputText + "%'";
		else if ("Starts not with".equals(searchOperator))
			query = "select T1.IDPS00, T2.LBPSLG from ZA01 T2, ZA00 T1 where T1.nudoss = T2.nudoss and T1.IDPS00 not like '"
					+ inputText + "%'";
		else if ("Ends with".equals(searchOperator))
			query = "select T1.IDPS00, T2.LBPSLG from ZA01 T2, ZA00 T1 where T1.nudoss = T2.nudoss and T1.IDPS00 like '%"
					+ inputText + "'";
		else if ("Ends not with".equals(searchOperator))
			query = "select T1.IDPS00, T2.LBPSLG from ZA01 T2, ZA00 T1 where T1.nudoss = T2.nudoss and T1.IDPS00 not like '%"
					+ inputText + "'";

		HRExtractionSource extractionSource = new HRExtractionSource();
		extractionSource.setConversation(user.getMainConversation());
		extractionSource.setRole(role);

		Map<String, String> resultMap = new HashMap<String, String>();
		char quote = user.getSession().getDatabaseQuote();
		String queryParsed = query.replace('\'', quote);
		// log.debug("extractSourceSQL --> " + queryParsed);
		extractionSource.setSQLExtraction(queryParsed);
		// Connecting extraction source
		extractionSource.setActive(true);
		String positionID = "";
		String positionLabel = "";
		try {
			// Retrieving the result set as a TDataNode
			TDataNode node = extractionSource.getDataNode();
			if (node.getRecordCount() > 0) {
				int i = 0;
				do {
					positionID = node.getValue(0).toString();
					positionLabel = node.getValue(1).toString();
					resultMap.put(positionID, positionLabel);
					i++;
				} while (node.next());

			}

		} finally {
			if ((extractionSource != null) && extractionSource.isActive()) {
				// Disconnecting extraction source
				extractionSource.setActive(false);
			}
		}

		return resultMap;

	}
	
	private static Map<String, List<String>> searchPersonByID(IHRUser user,
			IHRRole role, String inputText, String searchOperator) {

		String query = "";
		if ("Equals".equals(searchOperator))
			query = "select T1.matcle, T1.PRENOM, T1.NOMUSE, T2.NMPRES from  ZY00 T1, ZY3Y T2 where T1.matcle ='"
					+ inputText + "'";
		else if ("Not equal".equals(searchOperator))
			query = "select T1.matcle, T1.PRENOM, T1.NOMUSE, T2.NMPRES from  ZY00 T1, ZY3Y T2 where T1.matcle !='"
					+ inputText + "'";
		else if ("Contains".equals(searchOperator))
			query = "select T1.matcle, T1.PRENOM, T1.NOMUSE, T2.NMPRES from  ZY00 T1, ZY3Y T2 where T1.matcle like '%"
					+ inputText + "%'";
		else if ("Contains not".equals(searchOperator))
			query = "select T1.matcle, T1.PRENOM, T1.NOMUSE, T2.NMPRES from  ZY00 T1, ZY3Y T2 where T1.matcle not like '%"
					+ inputText + "%'";
		else if ("Starts with".equals(searchOperator))
			query = "select T1.matcle, T1.PRENOM, T1.NOMUSE, T2.NMPRES from  ZY00 T1, ZY3Y T2 where T1.matcle like '"
					+ inputText + "%'";
		else if ("Starts not with".equals(searchOperator))
			query = "select T1.matcle, T1.PRENOM, T1.NOMUSE, T2.NMPRES from  ZY00 T1, ZY3Y T2 where T1.matcle not like '"
					+ inputText + "%'";
		else if ("Ends with".equals(searchOperator))
			query = "select T1.matcle, T1.PRENOM, T1.NOMUSE, T2.NMPRES from  ZY00 T1, ZY3Y T2 where T1.matcle like '%"
					+ inputText + "'";
		else if ("Ends not with".equals(searchOperator))
			query = "select T1.matcle, T1.PRENOM, T1.NOMUSE, T2.NMPRES from  ZY00 T1, ZY3Y T2 where T1.matcle not like '%"
					+ inputText + "'";

		HRExtractionSource extractionSource = new HRExtractionSource();
		extractionSource.setConversation(user.getMainConversation());
		extractionSource.setRole(role);

		Map<String, List<String>> resultMap = new HashMap<String, List<String>>();
		char quote = user.getSession().getDatabaseQuote();
		String queryParsed = query.replace('\'', quote);
		// log.debug("extractSourceSQL --> " + queryParsed);
		extractionSource.setSQLExtraction(queryParsed);
		// Connecting extraction source
		extractionSource.setActive(true);
		String matcle = "";
		String firstName = "";
		String lastName = "";
		String name = "";
		List<String> values;
		try {
			// Retrieving the result set as a TDataNode
			TDataNode node = extractionSource.getDataNode();
			if (node.getRecordCount() > 0) {
				int i = 0;
				do {
					values= new ArrayList<String>();
					matcle = node.getValue(0).toString();
					firstName = node.getValue(1).toString();
					lastName = node.getValue(2).toString();
					name = node.getValue(3).toString();
					values.add(firstName);
					values.add(lastName);
					values.add(name);
					
					
					resultMap.put(matcle, values);
					i++;
				} while (node.next());

			}

		} finally {
			if ((extractionSource != null) && extractionSource.isActive()) {
				// Disconnecting extraction source
				extractionSource.setActive(false);
			}
		}

		return resultMap;

	}

	private static String extractSQLQueries() {

		IHRSession session = EnvironmentManager.createSession();
		IHRUser user = EnvironmentManager.connectUser("ADJEBBIF", "AA");
		String roleId = "ALLHRLO(FR)";

		SelfWebAppConfiguration selfWebAppConfiguration = new SelfWebAppConfiguration();
		selfWebAppConfiguration.setUser(user);
		selfWebAppConfiguration.setSession(session);
		SelfConfigurator.setOpenHRAccessor(selfWebAppConfiguration);
		SelfConfigurator.setRegionalParameterAccessor(selfWebAppConfiguration);

		// Add employees assigned to the manager
		HRExtractionSource extractionSource = new HRExtractionSource();
		extractionSource.setConversation(user.getMainConversation());
		extractionSource.setRole(user.getRole(roleId));
		// Setting SQL statement to perform
		// Get Employees assigned to the manager
		String matcle = "ATP000000006";
		String query = "select nudoss, matcle, PRENOM, NOMUSE from zy00 where nudoss in (select T.nudoss from zy3e T where matcle = '"
				+ matcle + "')";

		char quote = user.getSession().getDatabaseQuote();
		String queryParsed = query.replace('\'', quote);
		// log.debug("extractSourceSQL --> " + queryParsed);
		extractionSource.setSQLExtraction(queryParsed);
		// Connecting extraction source
		extractionSource.setActive(true);
		try {
			// Retrieving the result set as a TDataNode
			TDataNode node = extractionSource.getDataNode();
			String nudoss = "";
			String nudossList = "";
			Map<String, String> matcles = new HashMap<String, String>();
			Map<String, String> firstNames = new HashMap<String, String>();
			Map<String, String> lastNames = new HashMap<String, String>();
			List<String> employeesNudoss = new ArrayList<String>();

			int employeesNumber = node.getRecordCount();
			String firstName;
			String lastName;
			String phoneNumber;
			String email;
			String position;
			if (employeesNumber > 0) {
				int i = 0;
				do {
					nudoss = node.getValue(0).toString();
					employeesNudoss.add(nudoss);
					matcle = node.getValue(1).toString();
					matcles.put(nudoss, matcle);
					firstName = node.getValue(2).toString();
					firstNames.put(nudoss, firstName);
					lastName = node.getValue(3).toString();
					lastNames.put(nudoss, lastName);
					if (i < employeesNumber - 1)
						nudossList = nudossList + nudoss + ",";
					else
						nudossList = nudossList + nudoss;

					i++;
				} while (node.next());

			}
			// phone numbers
			query = "select T1.nudoss, T2.NUMTEL from ZY0H T2, ZY00 T1 where T1.nudoss = T2.nudoss and T2.TYPTEL='HPN' and T1.nudoss in ("
					+ nudossList + ") ";
			Map<String, String> phoneNumbers = new HashMap<String, String>();
			quote = user.getSession().getDatabaseQuote();
			queryParsed = query.replace('\'', quote);
			// log.debug("extractSourceSQL --> " + queryParsed);
			extractionSource.setSQLExtraction(queryParsed);
			// Connecting extraction source
			extractionSource.setActive(true);
			try {
				// Retrieving the result set as a TDataNode
				node = extractionSource.getDataNode();
				if (node.getRecordCount() > 0) {
					int i = 0;
					do {
						nudoss = node.getValue(0).toString();
						phoneNumber = node.getValue(1).toString();
						phoneNumbers.put(nudoss, phoneNumber);
						i++;
					} while (node.next());

				}

			} finally {
				if ((extractionSource != null) && extractionSource.isActive()) {
					// Disconnecting extraction source
					extractionSource.setActive(false);
				}
			}

			// emails
			query = "select T1.nudoss, T2.NUMTEL from ZY0H T2, ZY00 T1 where T1.nudoss = T2.nudoss and T2.TYPTEL='EML' and T1.nudoss in ("
					+ nudossList + ") ";
			Map<String, String> emails = new HashMap<String, String>();
			quote = user.getSession().getDatabaseQuote();
			queryParsed = query.replace('\'', quote);
			// log.debug("extractSourceSQL --> " + queryParsed);
			extractionSource.setSQLExtraction(queryParsed);
			// Connecting extraction source
			extractionSource.setActive(true);
			try {
				// Retrieving the result set as a TDataNode
				node = extractionSource.getDataNode();
				if (node.getRecordCount() > 0) {
					int i = 0;
					do {
						nudoss = node.getValue(0).toString();
						email = node.getValue(1).toString();
						emails.put(nudoss, email);
						i++;
					} while (node.next());

				}

			} finally {
				if ((extractionSource != null) && extractionSource.isActive()) {
					// Disconnecting extraction source
					extractionSource.setActive(false);
				}
			}
			// title of the post
			query = query = "select T4.nudoss, T3.LBPSLG from ZA01 T3, ZA00 T2, ZY3B T1, ZY00 T4 where T3.nudoss = T2.nudoss  and T4.nudoss in("
					+ nudossList
					+ ") and T1.IDPS00 = T2.IDPS00 and T1.nudoss=T4.nudoss";
			Map<String, String> positions = new HashMap<String, String>();
			quote = user.getSession().getDatabaseQuote();
			queryParsed = query.replace('\'', quote);
			// log.debug("extractSourceSQL --> " + queryParsed);
			extractionSource.setSQLExtraction(queryParsed);
			// Connecting extraction source
			extractionSource.setActive(true);
			try {
				// Retrieving the result set as a TDataNode
				node = extractionSource.getDataNode();
				if (node.getRecordCount() > 0) {
					int i = 0;
					do {
						nudoss = node.getValue(0).toString();
						position = node.getValue(1).toString();
						positions.put(nudoss, position);
						i++;
					} while (node.next());

				}

			} finally {
				if ((extractionSource != null) && extractionSource.isActive()) {
					// Disconnecting extraction source
					extractionSource.setActive(false);
				}
			}

			for (String currentNudoss : employeesNudoss) {
				System.out.println("matcle : " + matcles.get(currentNudoss)
						+ " first name : " + firstNames.get(currentNudoss)
						+ " last name : " + lastNames.get(currentNudoss)
						+ " position  : " + positions.get(currentNudoss)
						+ " phone number : " + phoneNumbers.get(currentNudoss)
						+ " email : " + emails.get(currentNudoss));

			}

		} finally {
			if ((extractionSource != null) && extractionSource.isActive()) {
				// Disconnecting extraction source
				extractionSource.setActive(false);
			}
		}

		return "ok";
	}

	private static String extractSQLQuery() {

		IHRSession session = EnvironmentManager.createSession();
		IHRUser user = EnvironmentManager.connectUser("IELFELLF", "HR");
		String roleId = "ALLHRLO(FR)";

		SelfWebAppConfiguration selfWebAppConfiguration = new SelfWebAppConfiguration();
		selfWebAppConfiguration.setUser(user);
		selfWebAppConfiguration.setSession(session);
		SelfConfigurator.setOpenHRAccessor(selfWebAppConfiguration);
		SelfConfigurator.setRegionalParameterAccessor(selfWebAppConfiguration);

		// GPManager gpManager = new
		// GPManager(session,user,user.getRole(roleId));
		// gpManager.setDisplayPatternsFromChart();
		// List<Request> requests = gpManager.getRequestsByUser(user, roleId);

		// List<Request> requests = gpManager.getRequestsByRole();

		// List<Request> requests=null;

		HRExtractionSource extractionSource = new HRExtractionSource();
		extractionSource.setConversation(user.getMainConversation());
		extractionSource.setRole(user.getRole(roleId));

		// Setting SQL statement to perform
		// prise en compte des quotes en fonction du sgbd
		// Get Employees assigned to the manager
		String query = "select nudoss,socdos, nmpres from zy3y where nudoss in (select T1.nudoss from ZY00 T1)";

		char quote = user.getSession().getDatabaseQuote();
		String queryParsed = query.replace('\'', quote);
		// log.debug("extractSourceSQL --> " + queryParsed);
		extractionSource.setSQLExtraction(queryParsed);
		extractionSource.setMaxRowCount(3);
		// Connecting extraction source
		extractionSource.setActive(true);
		String result = "";
		try {
			// Retrieving the result set as a TDataNode
			TDataNode node = extractionSource.getDataNode();
			if (node.getRecordCount() > 0) {
				int row = 0;
				do {
					String matcle = node.getValue(0).toString();
					String firstName = node.getValue(1).toString();
					String lastName = node.getValue(2).toString();
					row++;
				} while (node.next());

			}
		} finally {
			if ((extractionSource != null) && extractionSource.isActive()) {
				// Disconnecting extraction source
				extractionSource.setActive(false);
			}
		}

		// System.out.println("sql result is : "+result);
		return result;

	}

	private static void deleteNotification() {
		IHRSession session = EnvironmentManager.createSession();
		IHRUser user = EnvironmentManager.connectUser("ADJEBBIF", "AA");
		String roleId = "EMPLOYEE(FRLFRP1500)";
		GPManager gpManager = new GPManager(session, user, user.getRole(roleId));

		String guidedProcessId = "AGW05GM0";
		int notificationId = 40915;
		int stepId = 2;
		ExecutionResult result = gpManager.deleteNotification(guidedProcessId,
				notificationId, stepId);
		// List<Instance> ListGp=null;

	}

	private static void getStatus() {
		int nudoss = 38724;
		IHRSession session = EnvironmentManager.createSession();
		IHRUser user = EnvironmentManager.connectUser("ADJEBBIF", "AA");
		String roleId = "EMPLOYEE(FRLFRP1500)";
		GPManager gpManager = new GPManager(session, user, user.getRole(roleId));

		String statux = gpManager.getGPStatux(nudoss);
		System.out.println("the statux is : " + statux);
		String statua = gpManager.getGPStatua(nudoss);
		System.out.println("the statua is : " + statua);
		// List<Instance> ListGp=null;

	}

	private static void displayUserGpList(List<Instance> adminGpLists) {
		System.out.println("****************** AdminGP List ***********");
		Iterator<Instance> iter = adminGpLists.iterator();

		while (iter.hasNext()) {

			Instance AdGp = iter.next();
			System.out.println("****************** GP List ***********");
			System.out.println(AdGp.getGuidedProcessName());
			System.out.println("****************** Label  ***********");
			System.out.println(AdGp.getLabel());
			System.out.println("****************** Subject  ***********");
			System.out.println(AdGp.getSubject());

		}

	}

	private static List<Instance> getGpList() {
		IHRSession session = EnvironmentManager.createSession();
		IHRUser user = EnvironmentManager.connectUser("ADJEBBIF", "AA");
		String roleId = "EMPLOYEE(FRLFRP1500)";
		SelfWebAppConfiguration selfWebAppConfiguration = new SelfWebAppConfiguration();
		selfWebAppConfiguration.setUser(user);
		selfWebAppConfiguration.setSession(session);
		SelfConfigurator.setOpenHRAccessor(selfWebAppConfiguration);
		SelfConfigurator.setRegionalParameterAccessor(selfWebAppConfiguration);
		// GPManager gpManager = new
		// GPManager(session,user,user.getRole(roleId));

		// Create a HashMap which stores Strings as the keys and values
		Map<String, String> params = new HashMap<String, String>();
		// Adding some values to the HashMap
		params.put("rowmax", new String("5"));
		params.put("idbppr", new String("FSW0AGE0"));

		SelfServiceApi selfApi = new SelfServiceApi(session);
		InstanceManager instanceManager = selfApi.getInstanceManager(user,
				user.getRole(roleId));
		List<Instance> ListGp = instanceManager.getInstances(params);

		// List<Instance> ListGp = gpManager.getInstances(params);
		// List<Instance> ListGp=null;

		return ListGp;
	}

	private static List<Request> getRequestsList() {

		IHRSession session = EnvironmentManager.createSession();
		IHRUser user = EnvironmentManager.connectUser("ADJEBBIF", "AA");
		String roleId = "EMPLOYEE(FRRFRP2204)";

		SelfWebAppConfiguration selfWebAppConfiguration = new SelfWebAppConfiguration();
		selfWebAppConfiguration.setUser(user);
		selfWebAppConfiguration.setSession(session);
		SelfConfigurator.setOpenHRAccessor(selfWebAppConfiguration);
		SelfConfigurator.setRegionalParameterAccessor(selfWebAppConfiguration);

		// GPManager gpManager = new
		// GPManager(session,user,user.getRole(roleId));
		// gpManager.setDisplayPatternsFromChart();
		// List<Request> requests = gpManager.getRequestsByUser(user, roleId);

		// List<Request> requests = gpManager.getRequestsByRole();

		// List<Request> requests=null;

		SelfServiceApi selfApi = new SelfServiceApi(session);
		// RequestManager requestManager=selfApi.getRequestManager(user,
		// user.getRole(roleId));
		RequestManager requestManager = selfApi.getRequestManager(user,
				user.getRole(roleId));
		List<Request> requests = requestManager.getRequests();
		return requests;
	}

	private static List<Notification> getNotifications() {

		IHRSession session = EnvironmentManager.createSession();
		IHRUser user = EnvironmentManager.connectUser("ADJEBBIF", "AA");
		String roleId = "EMPLOYEE(FRLFRP1500)";

		SelfWebAppConfiguration selfWebAppConfiguration = new SelfWebAppConfiguration();
		selfWebAppConfiguration.setUser(user);
		selfWebAppConfiguration.setSession(session);
		SelfConfigurator.setOpenHRAccessor(selfWebAppConfiguration);
		SelfConfigurator.setRegionalParameterAccessor(selfWebAppConfiguration);

		// GPManager gpManager = new GPManager(session, user,
		// user.getRole(roleId));
		// gpManager.setDisplayPatternsFromChart();

		// List<Notification> notifications =
		// gpManager.getNotificationsByUser(user, roleId);

		SelfServiceApi selfApi = new SelfServiceApi(session);
		NotificationManager notificationManager = selfApi
				.getNotificationManager(user, user.getRole(roleId));
		selfApi.setDisplayPatternsFromChart();
		List<Notification> notifications = notificationManager
				.getNotifications();

		return notifications;
	}

	private static void displayUserNotificationss(
			List<Notification> notifications) {
		System.out.println("****************** Notification List ***********");
		Iterator<Notification> iter = notifications.iterator();

		while (iter.hasNext()) {

			Notification notif = iter.next();
			System.out.println("****************** Message ***********");
			System.out.println(notif.getMessage());
			System.out.println("****************** GPName  ***********");
			System.out.println(notif.getGpId());
			System.out.println("****************** Subject***********");
			System.out.println(notif.getSubject());
			System.out.println("****************** Date***********");
			System.out.println(notif.getDate().toString());
			System.out.println("****************** Nudoss***********");
			System.out.println(notif.getId());
			System.out.println("****************** NuStep***********");
			System.out.println(notif.getNustep());
			System.out.println("****************** GP NAME***********");
			System.out.println(notif.getGpId());

		}

	}

	private static List<Task> getTasksList() {

		IHRSession session = EnvironmentManager.createSession();
		IHRUser user = EnvironmentManager.connectUser("ADJEBBIF", "AA");
		String roleId = "EMPLOYEE(FRLFRP1500)";

		SelfWebAppConfiguration selfWebAppConfiguration = new SelfWebAppConfiguration();
		selfWebAppConfiguration.setUser(user);
		selfWebAppConfiguration.setSession(session);
		SelfConfigurator.setOpenHRAccessor(selfWebAppConfiguration);
		SelfConfigurator.setRegionalParameterAccessor(selfWebAppConfiguration);

		// GPManager gpManager = new GPManager(session);
		// gpManager.setDisplayPatternsFromChart();
		// List<Task> userTasks = gpManager.getTasksByUser(user,roleId);

		SelfServiceApi selfApi = new SelfServiceApi(session);
		TaskManager taskManager = selfApi.getTaskManager(user,
				user.getRole(roleId));
		List<Task> userTasks = taskManager.getTasks();

		return userTasks;
	}

	private static void displayUserTasks(List<Task> tasks) {
		System.out.println("****************** TASKS LIST ***********");
		Iterator<Task> task = tasks.iterator();
		while (task.hasNext()) {
			Task taskOcc = task.next();
			System.out.println("****************** Label Tasks ***********");
			System.out.println(taskOcc.getLabel());
			System.out.println("****************** subject Tasks ***********");
			System.out.println(taskOcc.getSubject());
			System.out.println("****************** transmissable ***********");
			System.out.println(taskOcc.isTransmittable());
			// System.out.println("****************** quickvalidable ***********");
			// System.out.println(taskOcc.isQuickvalidable());
			System.out.println("****************** rejectable ***********");
			System.out.println(taskOcc.isRejectable());
		}
	}

	private static void displayUserrequests(List<Request> requests) {
		System.out.println("****************** Request LIST ***********");
		Iterator<Request> userRequest = requests.iterator();
		while (userRequest.hasNext()) {
			Request requestOcc = userRequest.next();
			System.out.println("****************** GP ***********");
			System.out.println(requestOcc.getGpId());
			System.out
					.println("****************** Statuts Request ***********");
			System.out.println(requestOcc.getStatus());
			System.out.println("****************** Label Request ***********");
			System.out.println(requestOcc.getLabel());
			// System.out.println("****************** ID ***********");
			// System.out.println(requestOcc.getID());
			System.out.println("****************** Recipient ***********");
			System.out.println(requestOcc.getRecipient());
			System.out.println("****************** Subject ***********");
			System.out.println(requestOcc.getSubject());
			System.out
					.println("****************** technicalStatus ***********");
			System.out.println(requestOcc.getTechnicalStatusCode());
			System.out.println("****************** statusCode ***********");
			System.out.println(requestOcc.getStatusCode());
			System.out.println("****************** isCancellable ***********");
			System.out.println(requestOcc.isCancellable());
			System.out.println("****************** isDeletable ***********");
			System.out.println(requestOcc.isDeletable());
		}

	}
}
