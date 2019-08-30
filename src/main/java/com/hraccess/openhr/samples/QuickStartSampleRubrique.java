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

public class QuickStartSampleRubrique {
	
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
			System.out.println("*********  Manupulation des rubriques	*********" );
			IHRDataSection dataSection = dataStructure.getDataSectionByName("00");

			
			
			
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
