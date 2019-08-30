package com.hraccess.openhr.samples;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hraccess.openhr.HRCurrency;
import com.hraccess.openhr.IHRDataSection;
import com.hraccess.openhr.IHRDataStructure;
import com.hraccess.openhr.IHRDictionary;
import com.hraccess.openhr.IHRItem;
import com.hraccess.openhr.IHRLink;
import com.hraccess.openhr.IHRSession;

public class QuickStartSampleCurrency {
	
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
			IHRDataSection dataSection = dataStructure.getDataSectionByName("00");
			System.out.println("*********  Manupulation des devise	*********" );
			// Dumping the currencies
			Map currencies = dictionary.getCurrencies();
			for(Iterator it=currencies.keySet().iterator(); it.hasNext(); ) {
				String currencyName = (String) it.next();
				HRCurrency currency = (HRCurrency) currencies.get(currencyName);
				System.out.println("	Currency <" + currencyName + "> (" + currency.getLabel() + ") has " + currency.getDecimal() + " decimal(s)");
			}
			// Retrieving a named currency
			HRCurrency dollarCurrency = dictionary.getCurrency("USD");
			System.out.println("	Currency <USD> (" + dollarCurrency.getLabel() + ") has " + dollarCurrency.getDecimal() + " decimal(s)");
			// Dumping the currency names
			String[] currencyNames = dictionary.getCurrencyNames();
			for(int i=0; i<currencyNames.length; i++) {
				String currencyName = currencyNames[i];
				System.out.println("	Found currency <" + currencyName + ">");
			}
			
			
			
			
			
			
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
