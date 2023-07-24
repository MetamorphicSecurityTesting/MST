/*******************************************************************************
 * Copyright (c) University of Luxembourg 2018-2020
 * Created by Fabrizio Pastore (fabrizio.pastore@uni.lu), Xuan Phu MAI (xuanphu.mai@uni.lu)
 *     
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package smrl.mr.language;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import smrl.mr.analysis.ASMUtil;
import smrl.mr.analysis.ASMUtil.ASM_MRData;
import smrl.mr.crawljax.Account;
import smrl.mr.crawljax.WebInputCrawlJax;
import smrl.mr.language.actions.IndexAction;
import smrl.mr.language.actions.StandardAction;
import smrl.mr.utils.URLUtil;


public abstract class MR {

	Logger LOGGER = Logger.getLogger(MR.class.getCanonicalName());

	public static MR CURRENT;

	//Added by Nazanin on July 2023 to extract the cost 
	public static boolean extractCost = false; // tested for CWE 792 series  
	public static boolean extractCost_test = true; // default value : true

	public static boolean reset = true; // default value true 
	public static int executedAction = 0; //default value 0
	public static int actionSize = 0; //default value 0
	public static boolean notTried_flag = true; // default value true



	private static boolean COLLECT_ALL_FAILURES = true;

	public static boolean isCOLLECT_ALL_FAILURES() {
		return COLLECT_ALL_FAILURES;
	}


	public static void setCOLLECT_ALL_FAILURES(boolean cOLLECT_ALL_FAILURES) {
		COLLECT_ALL_FAILURES = cOLLECT_ALL_FAILURES;
	}

	private static final boolean PERFORM_FILTERING = true;

	private static final int MAX_SHUFFLING = 10;

	private static boolean MEexecutedAtLeastOnce = false;

	OperationsProvider provider;


	private List<String> dataConsidered;

	private HashMap<String, MrDataDB> dataDBs = new HashMap<String,MrDataDB>();

	public int executions;

	private ArrayList<MrDataDB> sortedDBs;

	public int executedSourceInputsCounter;

	private int executedFollowUpInputActionsCounter;

	private int executedSourceInputActionsCounter;





	//	public MR( OperationsProvider<Input, Output > provider, List<String> dataConsidered ){
	//		this.provider = provider;
	//		this.dataConsidered = dataConsidered;
	//		
	//	}

	public void setProvider(OperationsProvider provider){
		this.provider = provider;
	}


	public void run() {

		LOGGER.log(Level.FINE,"!!! Executing MR: "+this.getClass().getName());

		CURRENT=this;

		try {
			ASM_MRData _mrData = ASMUtil.extractMRData(this);
			dataConsidered = ASMUtil.retrieveDataConsideredInMR(_mrData);

			totalMetamorphicExpressions = _mrData.getExpressionPassCounter();

			LOGGER.log(Level.FINE,"Total ME: "+totalMetamorphicExpressions);

			LOGGER.log(Level.FINE,"Data: "+dataConsidered);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		//Modifed to get the cost of each sequence:
		if(extractCost==true) {
			for (int splitCounter=0; splitCounter<160; splitCounter++) {

				sortedDBs = new ArrayList<MrDataDB>(); 
				{
					for(String dataName : dataConsidered ){
						if ( dataName.equals("RandomValue") ) {
							MrDataDBRandom db = new MrDataDBRandom(dataName);
							dataDBs.put(dataName, db);
							sortedDBs.add(db);
							continue;
						}

						if ( dataName.equals("RandomInteger") ) {
							MrDataDBRandom db = new MrDataDBRandom("RandomValue");
							dataDBs.put("RandomValue", db);
							sortedDBs.add(db);
							continue;
						}

						//				if ( dataName.equals("RandomHttpMethod") ) {
						if ( dataName.equals("HttpMethod") ) {
							MrDataDBHttpMethod db = new MrDataDBHttpMethod(dataName);
							dataDBs.put(dataName, db);
							sortedDBs.add(db);
							continue;
						} 

						if ( dataName.equals("parameterValueUsedByOtherUsers") ) {
							//This should be loaded after loading Input list
							continue;
						}


						MrDataDB db = new MrDataDB(dataName);
						dataDBs.put(dataName, db);
						db.load(provider.load(dataName));  //  loads data using a provider	



						if ( dataName.equals("Input") ) {
							SystemConfig config  = provider.getSysConfig();
							int totalSplits = 160;

							System.out.println("!!!! Considering a subset of Inputs: " );

							System.out.println("!!!! totalSplits: "+totalSplits );
							int selectedSplit = splitCounter;
							System.out.println("!!!! selectedSplit: "+selectedSplit );
							db.setSplit_cost( totalSplits, selectedSplit );
							//							db.setSplit( totalSplits, selectedSplit );



							sortedDBs.add(db);


							//					//just added by Phu on 10/01/2020 to support the function parameterValueUsedByOtherUsers 
							//					if(dataConsidered.contains("parameterValueUsedByOtherUsers")) {
							//						HashMap<String, ArrayList> finalList = extractParameterValuesForEachUser();
							//
							//						//create MrDB
							//						for(String dataName:finalList.keySet()) {
							//							MrDataDB db = new MrDataDB(dataName);
							//							dataDBs.put(dataName, db);
							//							db.load(finalList.get(dataName));
							//							sortedDBs.add(db);
							//						}
							//					}



							reset = true; 
							executions = 0;
							db.followUpInputsCounter = 0;
							db.sourceInputsCounter = 0;
							sourceInputsCounter = 0; 
							followUpInputsCounter = 0;
							executedFollowUpInputsCounter = 0;
							executedSourceInputsCounter = 0;
							executedFollowUpInputActionsCounter = 0;
							executedSourceInputActionsCounter = 0;
							executedAction = 0;

							resetMRState();
							cleanupReassignedData();

							iterateMR( sortedDBs, 0 );

							//		//basically we iterate over a potential set of inputs entities
							//		inputsDB.resetTestsCounter();
							//		while ( inputsDB.hasMore() && ( COLLECT_ALL_FAILURES || FAILED==false ) ){
							//			usersDB.resetTestsCounter();
							//			while( usersDB.hasMore() && ( COLLECT_ALL_FAILURES || FAILED==false ) ){
							//				if ( ! mr() ){
							//					fail();
							//					FAILED=true;
							//				}
							//				String msg = extractExecutionInformation();
							//				System.out.println("Executed with: "+msg);
							//				
							//				executions++;
							//				usersDB.nextTest();
							//			}
							//			inputsDB.nextTest();
							//		}


							System.out.println("MR tested with "+executions+" sets of inputs");

							System.out.println("Source input instances : "+sourceInputsCounter);
							System.out.println("Follow-up input instances : "+followUpInputsCounter);
							//
							//								for ( MrDataDB db1 : sortedDBs ){
							//									System.out.println("\t"+db1.getDbName()+" source input instances: "+db1.sourceInputsCounter+ " follow-up input instances: "+db1.followUpInputsCounter);
							//								}

							System.out.println("Source input sequences (Input) : "+sourceInputSequencesCounter);
							System.out.println("Follow-up input sequences (Input) : "+followUpInputSequencesCounter);

							System.out.println("Follow-up inputs sequences executed : "+executedFollowUpInputsCounter);
							System.out.println("Source inputs sequences executed : "+executedSourceInputsCounter);

							System.out.println("Actions belonging to Follow-up input sequences executed : "+ executedFollowUpInputActionsCounter);
							System.out.println("Actions belonging to Source input sequences executed : "+ executedSourceInputActionsCounter);


							//Nazanin's Modification for the cost: 
							//								executedSourceInputActionsCounter*length_action 
							executedAction = executedAction + executedSourceInputActionsCounter + executedFollowUpInputActionsCounter;
							if (sourceInputsCounter>0) {
								executedAction = executedAction + sourceInputsCounter* actionSize;
							}
							if(followUpInputsCounter>0) {
								executedAction = executedAction + followUpInputsCounter*actionSize;
							}
							System.out.println("Total number of executed actions: "+executedAction);


						}
					}
				}
			}
		}
		else if (extractCost_test)
		{
			for (int splitCounter=0; splitCounter<160; splitCounter++) {
				sortedDBs = new ArrayList<MrDataDB>(); 
				{
					for(String dataName : dataConsidered ){
						if ( dataName.equals("RandomValue") ) {
							MrDataDBRandom db = new MrDataDBRandom(dataName);
							dataDBs.put(dataName, db);
							sortedDBs.add(db);
							continue;
						}

						if ( dataName.equals("RandomInteger") ) {
							MrDataDBRandom db = new MrDataDBRandom("RandomValue");
							dataDBs.put("RandomValue", db);
							sortedDBs.add(db);
							continue;
						}

						//				if ( dataName.equals("RandomHttpMethod") ) {
						if ( dataName.equals("HttpMethod") ) {
							MrDataDBHttpMethod db = new MrDataDBHttpMethod(dataName);
							dataDBs.put(dataName, db);
							sortedDBs.add(db);
							continue;
						} 

						if ( dataName.equals("parameterValueUsedByOtherUsers") ) {
							//This should be loaded after loading Input list
							continue;
						}


						MrDataDB db = new MrDataDB(dataName);
						dataDBs.put(dataName, db);
						db.load(provider.load(dataName));  //  loads data using a provider	








						if ( dataName.equals("Input") ) {
							SystemConfig config  = provider.getSysConfig();
							int totalSplits = 160;
							//							if ( totalSplits > 1 ) {
							System.out.println("!!!! Considering a subset of Inputs: " );
							System.out.println("!!!! totalSplits: "+totalSplits );
							int selectedSplit = splitCounter;
							System.out.println("!!!! selectedSplit: "+selectedSplit );
							db.setSplit( totalSplits, selectedSplit );
							//							}
						}

						sortedDBs.add(db);
					}

					//just added by Phu on 10/01/2020 to support the function parameterValueUsedByOtherUsers 
					if(dataConsidered.contains("parameterValueUsedByOtherUsers")) {
						HashMap<String, ArrayList> finalList = extractParameterValuesForEachUser();

						//create MrDB
						for(String dataName:finalList.keySet()) {
							MrDataDB db = new MrDataDB(dataName);
							dataDBs.put(dataName, db);
							db.load(finalList.get(dataName));
							sortedDBs.add(db);
						}
					}

				}


				executions = 0;

				sourceInputsCounter = 0;
				followUpInputsCounter = 0;
				executedFollowUpInputsCounter = 0;
				executedSourceInputsCounter = 0;
				executedFollowUpInputActionsCounter = 0;
				executedSourceInputActionsCounter = 0;

				resetMRState();

				iterateMR( sortedDBs, 0 );

				//		//basically we iterate over a potential set of inputs entities
				//		inputsDB.resetTestsCounter();
				//		while ( inputsDB.hasMore() && ( COLLECT_ALL_FAILURES || FAILED==false ) ){
				//			usersDB.resetTestsCounter();
				//			while( usersDB.hasMore() && ( COLLECT_ALL_FAILURES || FAILED==false ) ){
				//				if ( ! mr() ){
				//					fail();
				//					FAILED=true;
				//				}
				//				String msg = extractExecutionInformation();
				//				System.out.println("Executed with: "+msg);
				//				
				//				executions++;
				//				usersDB.nextTest();
				//			}
				//			inputsDB.nextTest();
				//		}


				System.out.println("MR tested with "+executions+" sets of inputs");

				System.out.println("Source input instances : "+sourceInputsCounter);
				System.out.println("Follow-up input instances : "+followUpInputsCounter);

				for ( MrDataDB db : sortedDBs ){
					System.out.println("\t"+db.getDbName()+" source input instances: "+db.sourceInputsCounter+ " follow-up input instances: "+db.followUpInputsCounter);
				}

				System.out.println("Source input sequences (Input) : "+sourceInputSequencesCounter);
				System.out.println("Follow-up input sequences (Input) : "+followUpInputSequencesCounter);

				System.out.println("Follow-up inputs sequences executed : "+executedFollowUpInputsCounter);
				System.out.println("Source inputs sequences executed : "+executedSourceInputsCounter);

				System.out.println("Actions belonging to Follow-up input sequences executed : "+executedFollowUpInputActionsCounter);
				System.out.println("Actions belonging to Source input sequences executed : "+executedSourceInputActionsCounter);
//				System.out.println("Total number of executed actions: "+executedAction);
 
 
				executedAction = executedAction + executedSourceInputActionsCounter + executedFollowUpInputActionsCounter;
				if (sourceInputsCounter>0) {
					executedAction = executedAction + sourceInputsCounter* actionSize;
				}
				if(followUpInputsCounter>0) {
					executedAction = executedAction + followUpInputsCounter*actionSize;
				}
				
				System.out.println("Total number of executed actions: "+executedAction);
				executedAction=0;
			}
		}

	}

	//	//Original:
	//	else {
	//		sortedDBs = new ArrayList<MrDataDB>(); 
	//		{
	//			for(String dataName : dataConsidered ){
	//				if ( dataName.equals("RandomValue") ) {
	//					MrDataDBRandom db = new MrDataDBRandom(dataName);
	//					dataDBs.put(dataName, db);
	//					sortedDBs.add(db);
	//					continue;
	//				}
	//
	//				if ( dataName.equals("RandomInteger") ) {
	//					MrDataDBRandom db = new MrDataDBRandom("RandomValue");
	//					dataDBs.put("RandomValue", db);
	//					sortedDBs.add(db);
	//					continue;
	//				}
	//
	//				//				if ( dataName.equals("RandomHttpMethod") ) {
	//				if ( dataName.equals("HttpMethod") ) {
	//					MrDataDBHttpMethod db = new MrDataDBHttpMethod(dataName);
	//					dataDBs.put(dataName, db);
	//					sortedDBs.add(db);
	//					continue;
	//				} 
	//
	//				if ( dataName.equals("parameterValueUsedByOtherUsers") ) {
	//					//This should be loaded after loading Input list
	//					continue;
	//				}
	//
	//
	//				MrDataDB db = new MrDataDB(dataName);
	//				dataDBs.put(dataName, db);
	//				db.load(provider.load(dataName));  //  loads data using a provider	
	//
	//
	//
	//
	//
	//
	//
	//
	//				if ( dataName.equals("Input") ) {
	//					SystemConfig config  = provider.getSysConfig();
	//					int totalSplits = config.getTotalInputSplits();
	//					if ( totalSplits > 1 ) {
	//						System.out.println("!!!! Considering a subset of Inputs: " );
	//						System.out.println("!!!! totalSplits: "+totalSplits );
	//						int selectedSplit = config.getSelectedInputSplit();
	//						System.out.println("!!!! selectedSplit: "+selectedSplit );
	//						db.setSplit( totalSplits, selectedSplit );
	//					}
	//				}
	//
	//				sortedDBs.add(db);
	//			}
	//
	//			//just added by Phu on 10/01/2020 to support the function parameterValueUsedByOtherUsers 
	//			if(dataConsidered.contains("parameterValueUsedByOtherUsers")) {
	//				HashMap<String, ArrayList> finalList = extractParameterValuesForEachUser();
	//
	//				//create MrDB
	//				for(String dataName:finalList.keySet()) {
	//					MrDataDB db = new MrDataDB(dataName);
	//					dataDBs.put(dataName, db);
	//					db.load(finalList.get(dataName));
	//					sortedDBs.add(db);
	//				}
	//			}
	//
	//		}
	//
	//
	//		executions = 0;
	//
	//		sourceInputsCounter = 0;
	//		followUpInputsCounter = 0;
	//		executedFollowUpInputsCounter = 0;
	//		executedSourceInputsCounter = 0;
	//		executedFollowUpInputActionsCounter = 0;
	//		executedSourceInputActionsCounter = 0;
	//
	//		resetMRState();
	//
	//		iterateMR( sortedDBs, 0 );
	//
	//		//		//basically we iterate over a potential set of inputs entities
	//		//		inputsDB.resetTestsCounter();
	//		//		while ( inputsDB.hasMore() && ( COLLECT_ALL_FAILURES || FAILED==false ) ){
	//		//			usersDB.resetTestsCounter();
	//		//			while( usersDB.hasMore() && ( COLLECT_ALL_FAILURES || FAILED==false ) ){
	//		//				if ( ! mr() ){
	//		//					fail();
	//		//					FAILED=true;
	//		//				}
	//		//				String msg = extractExecutionInformation();
	//		//				System.out.println("Executed with: "+msg);
	//		//				
	//		//				executions++;
	//		//				usersDB.nextTest();
	//		//			}
	//		//			inputsDB.nextTest();
	//		//		}
	//
	//
	//		System.out.println("MR tested with "+executions+" sets of inputs");
	//
	//		System.out.println("Source input instances : "+sourceInputsCounter);
	//		System.out.println("Follow-up input instances : "+followUpInputsCounter);
	//
	//		for ( MrDataDB db : sortedDBs ){
	//			System.out.println("\t"+db.getDbName()+" source input instances: "+db.sourceInputsCounter+ " follow-up input instances: "+db.followUpInputsCounter);
	//		}
	//
	//		System.out.println("Source input sequences (Input) : "+sourceInputSequencesCounter);
	//		System.out.println("Follow-up input sequences (Input) : "+followUpInputSequencesCounter);
	//
	//		System.out.println("Follow-up inputs sequences executed : "+executedFollowUpInputsCounter);
	//		System.out.println("Source inputs sequences executed : "+executedSourceInputsCounter);
	//
	//		System.out.println("Actions belonging to Follow-up input sequences executed : "+executedFollowUpInputActionsCounter);
	//		System.out.println("Actions belonging to Source input sequences executed : "+executedSourceInputActionsCounter);
	//
	//
	//		executedAction = executedAction + executedSourceInputActionsCounter + executedFollowUpInputActionsCounter;
	//		if (sourceInputsCounter>0) {
	//			executedAction = executedAction + sourceInputsCounter* actionSize;
	//		}
	//		if(followUpInputsCounter>0) {
	//			executedAction = executedAction + followUpInputsCounter*actionSize;
	//		}
	//		System.out.println("Total number of executed actions: "+executedAction);
	//
	//	}}



	public MrMultiDataDB loadParameterValuesForEachUser() {
		String dataNames = "parameterValuesForEachUser";
		{
			MrDataDB multidb = dataDBs.get( dataNames );
			if ( multidb != null ) {
				return (MrMultiDataDB) multidb;

			}
		}

		MrMultiDataDB multidb = new MrMultiDataDB(dataNames);

		HashMap<String, ArrayList> finalList = extractParameterValuesForEachUser();
		for(String dataName:finalList.keySet()) {
			MrDataDB db = new MrDataDB(dataName);

			db.load(finalList.get(dataName));
			multidb.addDB( db );
		}


		dataDBs.put(dataNames, multidb);
		//sortedDBs.add(db);

		return multidb;
	}

	private HashMap<String, ArrayList> extractParameterValuesForEachUser() {
		HashMap<String, ArrayList<Entry>> allUser_parName_parValue = new HashMap<String, ArrayList<Entry>>();

		//get all entries <par_name, par_value> of each user
		for(Object input:provider.load("Input")) {
			for(Action act:((Input)input).actions()) {
				String username = null;
				if(act.getUser()!=null &&
						(
								((Account)act.getUser()).getUsername()!=null && 
								!((Account)act.getUser()).getUsername().isEmpty())
						) {
					username = ((Account)act.getUser()).getUsername();
				}

				if(username==null || username.isEmpty()) {
					continue;
				}

				if(act.getParameters()!=null &&
						act.getParameters().size()>0) {


					if(!allUser_parName_parValue.containsKey(username)) {
						allUser_parName_parValue.put(username, new ArrayList<Entry>());
					}

					for(Entry<String, String> parPair:act.getParameters()) {
						if(!allUser_parName_parValue.get(username).contains(parPair)) {
							allUser_parName_parValue.get(username).add(parPair);
						}
					}
				}
			}
		}

		//get list of par_values are used by other users
		HashMap<String, ArrayList> finalList = new HashMap<String, ArrayList>();
		for(String user1:allUser_parName_parValue.keySet()) {
			ArrayList<Entry> list1 = allUser_parName_parValue.get(user1);
			for(String user2:allUser_parName_parValue.keySet()) {
				if(user2.equals(user1)) {
					continue;
				}
				ArrayList<Entry> list2 = allUser_parName_parValue.get(user2);

				for(Entry e:list2) {
					if(!list1.contains(e)) {
						String key = user1 + "_" +e.getKey();
						if(!finalList.containsKey(key)) {
							finalList.put(key, new ArrayList<String>());
						}
						if(!finalList.get(key).contains(e.getValue())){
							finalList.get(key).add(e.getValue());
						}
					}
				}

			}
		}
		return finalList;
	}


	private void countExecutedFollowUpInputs() {
		HashSet<Input> uniqueInputs = new HashSet<Input>();

		for ( Input input : lastInputs ) {
			if ( input instanceof MRData ) {
				if ( uniqueInputs.add( input ) ) {//Avoid duplicates

					int actions = 0;
					if ( input instanceof Input ) {
						actions = ((Input) input).actions().size();
					}


					if ( ((MRData) input).isFollowUp() ) {
						executedFollowUpInputsCounter++;
						MR.executedAction++;
						executedFollowUpInputActionsCounter += actions;

					} else {
						MR.executedAction++;
						executedSourceInputsCounter++;
						executedSourceInputActionsCounter += actions;
					}
				}
			}
		}

	}

	boolean FAILED=false;

	public static boolean PRINT_EXECUTED_MRS = false;

	private void iterateMR(List<MrDataDB> sortedDBs, int i) {
		//		System.out.println("!!!iterateMR executions="+executions+" i="+i);

		if ( sortedDBs.size() == i ){ //no other data to iterate on, execute the MR
			try {
				if ( ! mr() ){
					fail();
					FAILED=true;
				}
			} catch ( Throwable t ) {
				if(extractCost) {
					//			t.printStackTrace();
					//			System.out.println("!!!!IGNORING EXCEPTION, sleep");

					killChromeDriver(); 

					provider.resetProxy();	

					//sleep 30s (for the case in which the web server restarts)
					try {
						//					Thread.sleep(30000); 
						Thread.sleep(3);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("!!!!IGNORING EXCEPTION, go ahead");
				}
				else {
					t.printStackTrace();
					System.out.println("!!!!IGNORING EXCEPTION, sleep");

					killChromeDriver(); 

					provider.resetProxy();	

					//sleep 30s (for the case in which the web server restarts)
					try {
						//					Thread.sleep(30000);
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("!!!!IGNORING EXCEPTION, go ahead");

				}
			}


			ExecutionInformation info = extractExecutionInformation(false, true, true);

			if ( PRINT_EXECUTED_MRS ) {
				System.out.println("Executed with: "+info.verboseMSG);
			} 

			executions++;


			cleanupReassignedData();
			resetMRState();

			//			try {
			//				Thread.sleep(1000);
			//			} catch (InterruptedException e) {
			//				e.printStackTrace();
			//			}

			killChromeDriver();

			//			try {
			//				Thread.sleep(500);
			//			} catch (InterruptedException e) {
			//				e.printStackTrace();
			//			}

			//			additionalInputs.cleanUpGeneratedAndReassignedData();

			return;
		}

		MrDataDB db = sortedDBs.get(i);
		db.resetTestsCounter();
		while ( db.hasMore() && ( COLLECT_ALL_FAILURES || FAILED==false ) ){

			//			int expectedSrcInputs = expectedSourceInputsOfType(db);
			//			if ( expectedSrcInputs > 1 ) {
			////				if ( true ) {
			////					throw new IllegalStateException("This is a debug message, this code should be executed for SESS_003, never tested");
			////				}
			//				
			//				iterateMRshuffling(sortedDBs, db, i);
			//			} else {
			//				iterateMR(sortedDBs, i+1);
			//			}

			int expectedSrcInputs = expectedSourceInputsOfType(db);

			if ( expectedSrcInputs <= 1  || ! db.shufflingEnabled() ) {
				iterateMR(sortedDBs, i+1);
				traceSourceInputsOfSameType(db);
				expectedSrcInputs = expectedSourceInputsOfType(db);
			}



			if ( expectedSrcInputs > 1 && db.shufflingEnabled() ) {
				//				if ( true ) {
				//					throw new IllegalStateException("This is a debug message, this code should be executed for SESS_003, never tested");
				//				}

				iterateMRshuffling(sortedDBs, db, i);
			}

			db.nextTest();
			provider.nextTest();
		}
	}

	public OperationsProvider getProvider() {
		return provider;
	}


	//Kill all running chromedriver (and Google Chrome)
	private void killChromeDriver() {

		boolean windows = isWindows();


		int runCode = 0;
		while(runCode==0) {
			try {
				runCode = 1;

				if ( ! windows ) {
					runCode = (Runtime.getRuntime().exec("killall chromedriver").waitFor()==0 || runCode==0) ? 0 : 1;
					runCode = (Runtime.getRuntime().exec("killall Google Chrome").waitFor()==0 || runCode==0) ? 0 : 1;
					runCode = (Runtime.getRuntime().exec("pkill chromedriver").waitFor()==0 || runCode==0) ? 0 : 1;
					runCode = (Runtime.getRuntime().exec("pkill Google Chrome").waitFor()==0 || runCode==0) ? 0 : 1;
				} else {
					runCode = (Runtime.getRuntime().exec("taskkill /f /im chrome.exe").waitFor()==0 || runCode==0) ? 0 : 1;
					runCode = (Runtime.getRuntime().exec("taskkill /f /im chromedriver").waitFor()==0 || runCode==0) ? 0 : 1;
				}
			} catch (IOException | InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}


	private Boolean _WINDOWS;
	private boolean isWindows() {
		if ( _WINDOWS != null ) {
			return _WINDOWS;
		}

		_WINDOWS = Boolean.FALSE;
		String os = System.getProperty("os.name");
		if ( os.contains("Windows") ) {
			_WINDOWS = Boolean.TRUE;
		}

		return _WINDOWS;
	}


	private void iterateMRshuffling(List<MrDataDB> sortedDBs, MrDataDB db, int i) {
		//		System.out.println("Shuffling "+db.dbName);
		int max = db.shuffleSize() < MAX_SHUFFLING ? db.shuffleSize() : MAX_SHUFFLING;
		//		System.out.println("***Shuffling "+db.dbName + " max:" + max);
		for ( int j = 0; j < max; j++ ) {
			db.shuffle();
			iterateMR(sortedDBs, i+1);
		}
		db.unshuffle();
	}

	private int expectedSourceInputsOfType(MrDataDB db) {
		if ( ! usedSourceInputsMap.containsKey(db) ) {
			return 1;
		}
		return usedSourceInputsMap.get(db);
	}

	private void traceSourceInputsOfSameType(MrDataDB db) {
		if ( usedSourceInputsMap.containsKey(db) ) {
			//			System.out.println("OPTIMIZATION");
			return; //This is an optimization, we just compute once per DB. We may change policy in the future.
		}
		int usedSrcInputs = db.getUsedSourceInputs();
		//		System.out.println("!!Used source inputs of last execution for "+db.dbName+" "+usedSrcInputs);
		int lastUsedSrcInputs = 0;
		if ( usedSourceInputsMap.containsKey(db) ) {
			//			System.out.println("!!Used source inputs for "+db.dbName+" "+usedSrcInputs);
			lastUsedSrcInputs = usedSourceInputsMap.get(db);
		}
		if ( MEexecutedAtLeastOnce ) {
			if ( usedSrcInputs > lastUsedSrcInputs ) {
				//				System.out.println("Updating inputs map");
				usedSourceInputsMap.put(db,usedSrcInputs);
			}
		} 
		//		else {
		//			System.out.println("No ME executed");
		//		}
	}

	HashMap<MrDataDB,Integer> usedSourceInputsMap = new HashMap<MrDataDB,Integer>();

	LinkedList<String> failures = new LinkedList<String>();

	private int sourceInputsCounter;
	private int followUpInputsCounter;

	private int sourceInputSequencesCounter;
	private int followUpInputSequencesCounter;

	public int executedFollowUpInputsCounter;

	public LinkedList<String> getFailures() {
		return failures;
	}


	public void fail(){

		LOGGER.log(Level.INFO,"FAILURE");

		ExecutionInformation info = extractExecutionInformation(true, false, true);

		if ( info == null ) {
			System.out.println("(DUPLICATED FAILURE, ignoring)");
			return;
		}

		failures.add(info.verboseMSG);
		System.out.println("FAILURE: \n"+info.verboseMSG);

	}

	public static class ExecutionInformation {
		public String msg;
		public String verboseMSG;
	}

	//	private MrDataDB additionalInputs = new MrDataDB(""); 
	//	protected void addAdditionalInput ( MRData d ) {
	//		additionalInputs.addProcessedInput(d);
	//	}

	private ExecutionInformation extractExecutionInformation(boolean performFiltring, boolean countInputs, boolean verboseOutput ) {
		String msg = "";

		String lastInputStrs[] = new String[lastInputs.size()];
		int lastPosStrs[] = new int[lastInputPos.size()];

		if ( countInputs ) {
			followUpInputsCounter = 0;
			sourceInputsCounter = 0;
			followUpInputSequencesCounter = 0;
			sourceInputSequencesCounter = 0;
		}

		//		msg = processDBDataForFailure(performFiltring, countInputs, msg, lastInputStrs, lastPosStrs, additionalInputs);	

		for ( MrDataDB db : sortedDBs ){

			msg = processDBDataForFailure(performFiltring, countInputs, msg, lastInputStrs, lastPosStrs, db);	
			if ( msg == null ) {
				return null;
			}

		}




		ExecutionInformation info = new ExecutionInformation(); 
		info.msg = msg;

		if ( verboseOutput ) {	

			msg += "\n **Inputs processed: ";


			for ( int j=0; j<lastInputStrs.length; j++ ) {
				msg += "\n"+lastInputStrs[j]+" (action position: "+lastPosStrs[j]+")";
			}

			msg += "\n **** ";

			msg += "\n **Actions : ";


			for ( int j=0; j<lastInputStrs.length; j++ ) {
				msg += "\n"+lastInputStrs[j]+" (action position: "+lastPosStrs[j]+")";
				if ( lastInputs.size() <= j  ) {
					msg += "\n"+"CANNOT FIND INPUT "+j+"";
				} else {
					if ( lastInputs.get(j).actions().size() <= lastPosStrs[j]  ) {
						msg += "\n"+"CANNOT FIND ACTION "+lastPosStrs[j]+"";
						for ( Action action : lastInputs.get(j).actions() ) {
							msg += "\n\t\t"+action;	
						}
					} else { 
						if ( lastPosStrs[j] < 0 ) {
							msg += "\n"+"ALL ACTIONS MIGHT BE FAULTY "+lastPosStrs[j]+"";
							for ( Action action : lastInputs.get(j).actions() ) {
								msg += "\n\t\t"+action;	
							}
						} else {
							msg += "\n"+lastInputs.get(j).actions().get(lastPosStrs[j]).toCompleteString();
						}
					}
				}
			}

			msg += "\n **** ";

		}

		info.verboseMSG = msg;

		//		msg += "\n**[Last equal: "+lastEqualA+" ="+lastEqualBStr+"]";
		//		
		//		msg += "\n**[Last equal: "+lastEqual+"]";



		//		HashMap<String, I> inputsMap = inputsDB.getProcessedInputs();
		//		String msg = "";
		//		for ( Entry<String,I> i : inputsMap.entrySet() ){
		//			msg += i.getKey()+": "+i.toString()+"\n";
		//		}
		//		
		//		HashMap<String, User> usersMap = usersDB.getProcessedInputs();
		//		for ( Entry<String,User> i : usersMap.entrySet() ){
		//			msg += i.getKey()+": "+i.toString()+"\n";
		//		}
		return info;
	}


	private String processDBDataForFailure(boolean performFiltring, boolean countInputs, String msg,
			String[] lastInputStrs, int[] lastPosStrs, MrDataDB db) {
		if(reset) {
			executions = 0;
			db.followUpInputsCounter = 0;
			db.sourceInputsCounter = 0;
			sourceInputsCounter = 0;  
			followUpInputsCounter = 0;
			executedFollowUpInputsCounter = 0;
			executedSourceInputsCounter = 0;
			executedFollowUpInputActionsCounter = 0;
			executedSourceInputActionsCounter = 0;
			executedAction = 0;
			reset = false;
		}
		if ( countInputs ) {

			followUpInputsCounter += db.followUpInputsCounter;
			sourceInputsCounter += db.sourceInputsCounter;

			if ( db.getDbName().equals("Input") ) {
				followUpInputSequencesCounter += db.followUpInputsCounter;
				sourceInputSequencesCounter += db.sourceInputsCounter;
			}
		}

		HashMap<String, Object> inputsMap = db.getProcessedInputs();

		boolean filteringApplied = false;

		for ( Entry<String,Object> i : inputsMap.entrySet() ){

			if ( performFiltring && PERFORM_FILTERING ) {
				Object value = i.getValue();
				if ( value instanceof Input ) {
					if ( filteringApplied == false) { //filtering is done on the first returned follow-up input, which is the one submitted
						if ( value instanceof MRData ) {
							if ( ((MRData) value).isFollowUp() ) {

								boolean containsNewData = registerInput((MRData)value );

								if ( ! containsNewData ) {
									//								System.out.println("!!! Does not contain new data");
									return null;
								} 
								//							else {
								//								System.out.println("!!! Contains new data");
								//							}
								filteringApplied = true;
							}
						}
					}
				}
			}

			String followUp = "[SOURCE INPUT]";
			Object input = i.getValue();
			if ( input instanceof MRData ) {
				if ( ((MRData) input).isFollowUp() ) {
					followUp = "[FOLLOW-UP INPUT]";

					//						if ( countInputs ) {
					//							followUpInputsCounter++;
					//						}
				} else {
					if ( input instanceof Input ) {
						//							if ( countInputs ) {
						//								sourceInputsCounter++;	
						//							}
					}
				}
			}



			for ( int j = 0; j < lastInputs.size(); j++ ) {
				Input linput = lastInputs.get(j);

				if ( input == linput ) {
					lastInputStrs[j] = i.getKey();
					lastPosStrs[j] = lastInputPos.get(j);
				}

				//+lastInputStr+"";	
			}




			PrintUtil.USER_FRIENDLY_TO_STRING = true;
			msg += i.getKey()+ " "+ followUp +" : \n"+i.toString()+"\n";

			Object output = i.getValue();
			if ( output instanceof WebInputCrawlJax ) {
				Output cachedOut = provider.getCachedOutput( (WebInputCrawlJax) output );
				String outputUrl = null;
				if ( cachedOut != null ) {
					File file = cachedOut.getHtmlFile();
					if ( file != null ) {
						outputUrl = file.getAbsolutePath();
					}
				}

				//msg += "output HTML at: "+outputUrl;
			}
			PrintUtil.USER_FRIENDLY_TO_STRING = false;
		}
		return msg;
	}

	private boolean considerParameters = false;

	public void setConsiderParameters() {
		considerParameters = true;
	}

	private HashSet<String> observedInputKeys = new HashSet<>();
	protected boolean registerInput(MRData _value) {

		if ( ! ( _value instanceof Input ) ) {
			return true;
		}

		//		System.out.println("!!!Register input "+inp);
		Input value = (Input) _value;

		boolean isNew = false;
		for ( Action action : value.actions() ) {
			String url = action.getUrl();

			if ( url == null ) {
				url = "";
			}

			url = url.trim();

			url = URLUtil.extractActionURL(url);

			if ( considerParameters ) {
				String pars = extractParametersString(action);
				url = url +":"+pars;
			}

			boolean isThisNew = observedInputKeys.add(url);
			if ( isThisNew ) {
				isNew = true;
			}

		}

		return isNew;
	}


	private String extractParametersString(Action action) {
		JsonArray formInputs = action.getFormInputs();

		String pars = "";

		if(formInputs==null) {
			return pars;
		}

		for(int i=0; i<formInputs.size(); i++){
			JsonObject fi = formInputs.get(i).getAsJsonObject();

			if(fi.keySet().contains("type") &&
					fi.keySet().contains("values")){

				String formType = fi.get("type").getAsString().toLowerCase();
				JsonArray values = fi.get("values").getAsJsonArray();
				if(values.size()>0 &&
						(formType.startsWith("text") ||
								formType.equals("password") || 
								formType.equals("hidden") ||
								formType.equals("file"))){
					for(int iValue=0; iValue<values.size(); iValue++){
						String value = values.get(iValue).getAsString().trim();
						pars=pars+value+";";
					}
				}
			}
		}

		return pars;
	}


	public abstract boolean mr();

	//	public I Input(int i){
	//		I input = (I) dataDBs.get("Input").get(i);
	//
	//		return input;
	//	}
	//
	//	public User User(){
	//		return (User) dataDBs.get("User").get(1);
	//	}










	//SMART COMPARISON
	public boolean equals(Object lhs, Object rhs) {
		return equal(lhs,rhs);
	}

	public boolean equal(Object _lhs, Object rhs) {
		if ( _lhs.equals(rhs) ){
			return true;
		}

		return reassign(_lhs, rhs);
	}

	public boolean create(Object _lhs, Object rhs) {
		return reassign(_lhs, rhs);
	}


	private boolean reassign(Object _lhs, Object rhs) {
		if ( _lhs instanceof MRData ){
			MRData lhs = (MRData) _lhs;
			if ( subTypes( lhs, rhs ) ){
				MrDataDB inputsDB = inputsDB();

				//Code added to handle multiple types of source inputs
				if (inputsDB == null ) {
					inputsDB = dataDBs.get(lhs.getDbName());
				}

				if ( inputsDB.contains(lhs) ){
					MRData _rhs;
					if ( ! instanceOf(lhs, rhs) ){
						_rhs = buildReassignableElement(lhs,rhs);
						if ( _rhs == null ){
							return false;
						}
					} else {
						_rhs = (MRData) rhs;
					}

					return inputsDB.reassign(lhs,_rhs);

				}

			}
		}

		return false;
	}

	private MRData buildReassignableElement(MRData lhs, Object rhs) {
		MRData _rhs=null;
		//		if ( 1 == 1 ){
		//			throw new RuntimeException("PLease note that this is the first time you are re-assigning something to a different obj type, e.g. Input(2)==Action, you'll need to debug");
		//		}
		Class<? extends Object> _parClass = rhs.getClass();

		while ( _rhs == null && _parClass != null ){
			try {
				//				System.out.println("!!! "+_parClass.getCanonicalName());
				Constructor<? extends MRData> constructor = lhs.getClass().getConstructor( _parClass );
				_rhs = constructor.newInstance(rhs);
			} catch (NoSuchMethodException e) {
				//TODO: the best would be to iterate also over interfaces, but for now it's ok
				_parClass = _parClass.getSuperclass();
			}  catch (InstantiationException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return null;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}

		if ( _rhs == null ){
			Constructor<?>[] constrs = lhs.getClass().getConstructors();
			for ( Constructor<?> constr : constrs ){
				Class<?>[] pars = constr.getParameterTypes();
				if ( pars.length > 1 ){
					return null;
				}
				if ( Collection.class.isAssignableFrom( pars[0] ) ){
					try {
						Collection _i = (Collection) pars[0].newInstance();
						_i.add(rhs);
						_rhs = (MRData) constr.newInstance(_i);
						break;
					} catch (InstantiationException e1) {
						e1.printStackTrace();
						return null;
					} catch (IllegalAccessException e1) {
						e1.printStackTrace();
						return null;
					} catch (IllegalArgumentException e1) {
						e1.printStackTrace();
						return null;
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
						return null;
					}
				}
			}


		}

		return _rhs;
	}

	private boolean instanceOf(MRData lhs, Object rhs) {
		return lhs.getClass().isAssignableFrom(rhs.getClass());
	}





	protected MrDataDB inputsDB() {
		return dataDBs.get("Input");
	}

	private boolean subTypes(Object lhs, Object rhs) {
		if ( 
				lhs.getClass().isAssignableFrom( rhs.getClass() )
				||  rhs.getClass().isAssignableFrom( rhs.getClass()) ) {
			return true;
		}
		return false;
	}

	public boolean different(Object lhs, Object rhs) {
		if ( lhs == null || rhs == null ){
			return true;
		}
		return ! lhs.equals(rhs);
	}




	//	protected boolean implies(boolean a, boolean b) {
	//		return (!a) || b;
	//	}
	//	
	//	protected boolean implies(ExecutableParameter a, ExecutableParameter b) {
	//		boolean _a = a.run();
	//		if ( !_a ){
	//			return true;
	//		}
	//		
	//		return b.run();
	//	}




	public List getMRData(String name){
		if(dataDBs.keySet().contains(name)) {
			return dataDBs.get(name).values();
		}
		return null;
	}

	public Object getMRData(String name, int i){
		if(dataDBs.keySet().contains(name)) {
			return dataDBs.get(name).get(i);
		}
		return null;
	}


	public String getCurrentExecutionId() {
		return ""+executions;
	}

	/**
	 * This method is supposed to be invoked after checking the last metamorphic 
	 * expression within a metamorphic relation. It is necessary to ensure that
	 * the follow-up inputs created by the loops inside a MR last for only one 
	 * cycle. 
	 * 
	 * Attention: all teh follow up inputs are supposed to be created within the 
	 * loop, otherwise we'll have inconsistent indexes.
	 * 
	 */
	public void cleanupReassignedData() {

		for ( MrDataDB db : sortedDBs ){

			//		System.out.println("cleanupReassignedData");

			db.cleanupReassignedData();	
		}


	}

	int passingExpressions = 0;
	int totalMetamorphicExpressions = 0;

	private void resetMRState() {
		//		System.out.println("resetPassingExpressionsCounter");
		passingExpressions = 0;
		ifBlocksCounter = 0;

		if ( lineOfFirstME > 0 ) {
			MEexecutedAtLeastOnce = true;
		}
		lineOfFirstME = -1;

		resetLastOutputs();
	}


	private void resetLastOutputs() {

		countExecutedFollowUpInputs( );

		lastInputPos = new ArrayList<Integer>();
		lastInputs = new ArrayList<Input>();
	}

	private void resetIfBocksCounter() {
		//		System.out.println("resetPassingExpressionsCounter");
		ifBlocksCounter = 0;
	}

	int lineOfFirstME=-1;
	int lineOfLastME=-1;
	int ifBlocksCounter = 0;

	private List<Integer> lastInputPos = new ArrayList<>();
	private List<Input> lastInputs = new ArrayList();

	private String lastEqual;

	//	private Object lastEqualA;
	//
	//	private Object lastEqualB;

	public void ifThenBlock() {
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		int pos = 2;
		int currentLine = st[pos].getLineNumber();

		//		System.out.println("IF_THEN_BLOCK");
		LOGGER.fine("Current line "+currentLine+" : "+st[pos].getClassName()+"."+st[pos].getMethodName());

		if ( lineOfFirstME == -1 ) {
			lineOfFirstME = currentLine;	
		}

		//		System.out.println("lineOfFirstME: "+lineOfFirstME);

		if ( currentLine == lineOfFirstME ) {
			LOGGER.fine("new ME cycle "+currentLine);
			cleanupReassignedData();
		} 
		//		The following enables resetting reassigned data every time an internal loop is re-executed, 
		//		but I'm not sure it is what we may want.
		//		else if ( currentLine < lineOfLastME ) {
		//			LOGGER.fine("new ME sub-cycle "+currentLine);
		//			cleanupReassignedData();
		//		}
		//		
		//		lineOfLastME = currentLine;


		ifBlocksCounter++;
	}

	@ExpressionPassTag
	public void expressionPass() {
		passingExpressions++;

		//		//FIXME: for now we always reset, in the future we should add a call at the beginning of each block that 
		//		//counts how many times we entered a block that should lead to a reset
		//		if ( passingExpressions == ifBlocksCounter ) {
		//					cleanupReassignedData();
		//		resetPassingExpressionsCounter();
		//		resetIfBocksCounter();
		//		}

		resetLastOutputs();
	}


	public MrDataDB getDataDB(String string) {
		return dataDBs.get(string);
	}


	public void setLastInputProcessed(Input input, int pos) {
		this.lastInputs.add( input );
		this.lastInputPos.add( pos );
	}

	public void resetLastInputProcessed(Input input, int pos) {
		this.lastInputs.set( this.lastInputs.size() -1 , input );
		this.lastInputPos.set( this.lastInputs.size() -1 , pos );

	}



	public void setLastEQUAL(Object a, Object b) {
		//		lastEqual = " "+ a + " = "+b;
		//		lastEqualA = a;
		//		lastEqualB = b;
	}



	public int getMRDataSize(String name) {
		return dataDBs.get(name).size();
	}

	public String getParameterValueUsedByOtherUsers_DBName(Action action, int parPosition) {
		if(action==null || 
				!((action instanceof StandardAction) || (action instanceof IndexAction)) ||
				action.getParameterName(parPosition)==null ||
				action.getParameterName(parPosition).isEmpty()) {
			return null;
		}

		String username = "";
		if(action.getUser()!=null) {
			Account user = (Account)action.getUser();
			String un = user.getUsername(); 
			if(un==null || un.isEmpty() ||
					un.equalsIgnoreCase("ANONYMOUS")) {
				username = "ANONYMOUS";
			}
			else {
				username = un;
			}
		}

		String dbName = username + "_" + action.getParameterName(parPosition);

		return dbName;
	}

	static boolean printCallerLog=true;
	public static void printCallerLog(String position) {
		if(printCallerLog) {
			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			if(stackTraceElements.length >3) {
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");  
				LocalDateTime now = LocalDateTime.now();  
				String time = dtf.format(now).toString();
				System.out.println("\t!!! " + time + " " +  position.toUpperCase() + 
						" the method: " + stackTraceElements[2].getMethodName());	
			}
		}
	}



}
