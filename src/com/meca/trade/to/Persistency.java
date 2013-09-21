package com.meca.trade.to;

import java.util.List;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

public class Persistency {

	
	private static final Persistency INSTANCE = new Persistency();
	private static final String DB_NAME = "trade.db";
	private ObjectContainer db = null; 

	   // Private constructor prevents instantiation from other classes
	   private Persistency() {
		   
		   db = Db4oEmbedded.openFile(Db4oEmbedded
			        .newConfiguration(), DB_NAME);

	   }

	   public static Persistency getInstance() {
	      return INSTANCE;
	   }

	   
	   public void storeActionList(List<Action> actionList){
		   for(Action act:actionList){
			   db.store(act);
		   }
	   }
	   
}
