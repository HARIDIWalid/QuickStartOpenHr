package com.hraccess.openhr.samples;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hraccess.openhr.HRCurrency;
import com.hraccess.openhr.IHRConversation;
import com.hraccess.openhr.IHRDataSection;
import com.hraccess.openhr.IHRDataStructure;
import com.hraccess.openhr.IHRDictionary;
import com.hraccess.openhr.IHRItem;
import com.hraccess.openhr.IHRLink;
import com.hraccess.openhr.IHRRole;
import com.hraccess.openhr.IHRSession;
import com.hraccess.openhr.IHRUser;
import com.hraccess.openhr.event.ConversationChangeEvent;
import com.hraccess.openhr.event.ConversationChangeListener;

public class QuickStartSampleNotification {
	
	public static void main(String[] args) throws Exception {
		
		// Instance Session
		IHRSession session = EnvironmentManager.createSession();
		
		IHRUser user =null;
		
		try {
			
			
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("*********  Manupulation des notifications	*********" );
			// Creating a new (secondary) conversation from an existing user
			user = session.connectUser("MJALLOUF", "HR");
			
			System.out.println();
			System.out.println();
			System.out.println();
			
			IHRConversation conversation = user.createConversation();
			ConversationChangeListener listener = new ConversationChangeListener() {
			public void conversationStateChanged(ConversationChangeEvent event) {
				IHRConversation conversation = (IHRConversation) event.getSource();
					if (conversation.isClosed()) {
						// Process the conversation's disconnection event (not detailed here)
						System.out.println();
						System.out.println(" Process the conversation's disconnection event > En cours ......");
					}
				}
			};
			
			conversation.addConversationChangeListener(listener);
			System.out.println("------->    " + conversation.getConversationNumber());
			// Closing the conversation
			conversation.close();
			conversation.removeConversationChangeListener(listener);
			
			System.out.println();
			System.out.println();
			System.out.println();

		} finally {
//			if ((user != null) && user.isConnected()) {
//				// Disconnecting user
//				user.disconnect();
//			}
//			if ((session != null) && session.isConnected()) {
//				// Disconnecting OpenHR session
//				session.disconnect();
//			}
		}
	}
}
