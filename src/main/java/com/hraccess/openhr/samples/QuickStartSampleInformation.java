package com.hraccess.openhr.samples;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hraccess.openhr.IHRDataSection;
import com.hraccess.openhr.IHRDataStructure;
import com.hraccess.openhr.IHRDictionary;
import com.hraccess.openhr.IHRItem;
import com.hraccess.openhr.IHRLink;
import com.hraccess.openhr.IHRSession;

public class QuickStartSampleInformation {
	
	public static void main(String[] args) throws Exception {
		
		// Instance Session
		IHRSession session = EnvironmentManager.createSession();
		
		try {
			
			// Create instance of Dictionary
			IHRDictionary dictionary = session.getDictionary();
	          
			// Create instance of DataSource
			IHRDataStructure dataStructure = dictionary.getDataStructureByName("ZY");
			
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("*********  Manupulation des Informations	*********" );
			System.out.println("	Quelques propriéties ");
			IHRDataSection dataSection = dataStructure.getDataSectionByName("00");
			System.out.println("		Nom de la propiété : ");
			System.out.println("			Data section name is " + dataSection.getName());
			System.out.println("			Items " + dataSection.getName());
			// Dumping the data section's items
			for(Iterator it=dataSection.getAllItems().iterator(); it.hasNext();) {
				IHRItem item = (IHRItem) it.next();
				System.out.println("				Found item <" + item.getName() + "> " + item.getLabel());
			}
			
			System.out.println();
			System.out.println("			Elementary items ");
			// Dumping the data section's elementary items
			for(Iterator it=dataSection.getElementaryItems().iterator(); it.hasNext();) {
				IHRItem item = (IHRItem) it.next();
				System.out.println("				Found elementary item <" + item.getName() + "> " + item.getLabel());
			}
			System.out.println("			Data section full name is " + dataSection.getFullName());
			System.out.println("			Data section label is " + dataSection.getLabel());
			System.out.println("			Data section has " + dataSection.getKeyCount() + " key item(s)");
			for(Iterator it=dataSection.getKeyItems().iterator(); it.hasNext();) {
				IHRItem item = (IHRItem) it.next();
				System.out.println("				Found key item <" + item.getName() + "> " + item.getLabel());
			}
			System.out.println("			Data section was last updated on " + new Date(dataSection.getLastUpdateTimestamp()));
			System.out.println("			Data section update order is " + dataSection.getOrder());
			System.out.println("			Data section segment name is " + dataSection.getSegmentName());
			System.out.println("			Data section type is " + dataSection.getType());
			
			System.out.println();
			System.out.println("			Récupération d'une rubrique par son nom ");
			// Retrieving the named item
			IHRItem item = dataSection.getItemByName("TYPDOS");
			if (item == null) {
				System.err.println("					Unable to find item <ZYAG MOTIFA>");
			} else {
				System.out.println("					Found item <ZYAG MOTIFA> " + item.getLabel());
			}
			System.out.println("			Tester l'existence d'une rubrique ");
			System.out.println();
			// Testing the presence of a named item
			System.out.println("				Item <ZY00 TYPDOS> exists ? " + dataSection.hasItem("TYPDOS"));
			
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
