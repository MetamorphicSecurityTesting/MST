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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;



public class MrDataDB<D> {
	Logger LOGGER = Logger.getLogger(MrDataDB.class.getName());

	protected String dbName;

	public String getDbName() {
		return dbName;
	}

	public MrDataDB(String dbName){
		this.dbName = dbName;
	}

	protected HashMap<String,D> generatedData = new HashMap<String,D>();
	private HashMap<String,D> reassignedData = new HashMap<String,D>();

	public Iterator<D> _it() {
		return inputs.iterator();
	}

	public D get(int i) {


		String key = dbName+"("+i+")";

		logDataStatus(key);


		if ( generatedData.containsKey(key) ){
			//			System.out.println("!!!Returning existing data "+key);
			LOGGER.log(Level.FINE,"!!!Returning existing data "+key);
			return generatedData.get(key);
		}

		if ( reassignedData.containsKey(key) ){
			LOGGER.log(Level.FINE,"!!!Returning existing reassigned data "+key);
			return reassignedData.get(key);
		}

		LOGGER.log(Level.FINE,"\t!!!Referring to origimal data "+key);

		D input = inputs.get( (START+i-1) % LEN );

		try {

			//			Method cloneM = input.getClass().getMethod("clone"); 

			//			if ( cloneM == null ){
			if ( Modifier.isFinal(input.getClass().getModifiers() ) ){
				//we assume that we cannot alter the content of final classes
				//(it might not be the case but is safe in this context)
				//thus we return the class itself
				//it works for String, Integer, etc...
				//it might not be good for custom defined types

				return input;
			}
			//			}
			Method cloneM = input.getClass().getMethod("clone"); 

			D _input = (D) cloneM.invoke(input);
			generatedData.put(key, _input);
			if(MR.notTried_flag==false) {
				sourceInputsCounter++;}

			if ( _input instanceof MRData ){
				((MRData)_input).setDbName(dbName);
				((MRData)_input).setID(key);
			}
			return _input;
			//			return (D) ( ((Cloneable)input)).clone();

		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void logDataStatus(String key) {
		if ( ! LOGGER.isLoggable(Level.FINE) ) {
			return;
		}

		LOGGER.log(Level.FINE,"!!!Request for : "+key);

		LOGGER.log(Level.FINE,"\t!!!GeneratedData: ");
		for ( Entry e : generatedData.entrySet() ) {
			LOGGER.log(Level.FINE,"\t\t"+e.getKey()+" "+e.getValue());
		}

		LOGGER.log(Level.FINE,"\t!!!reassignedData: ");
		for ( Entry e : reassignedData.entrySet() ) {
			LOGGER.log(Level.FINE,"\t\t"+e.getKey()+" "+e.getValue());
		}
	}

	public void nextTest() {
		LOGGER.log(Level.FINE,"!!!NEXT_TEST");
		START++;
		cleanUpGeneratedAndReassignedData();
	}

	public void cleanUpGeneratedAndReassignedData() {
		generatedData.clear();
		reassignedData.clear();
	}

	private List<D> inputs;
	protected int LEN;
	protected int START;
	protected int BASE_START = 0;

	public void load(List<D> loadInputs) {
		inputs = loadInputs;
		LEN = inputs.size();
		START=BASE_START;
	}

	public boolean hasMore() {
		return START < LEN;
	}

	public void resetTestsCounter() {
		START=BASE_START;
	}

	public HashMap<String, D> getProcessedInputs() {
		return generatedData;
	}

	//	public void addProcessedInput( MRData d) {
	//		generatedData.put(d.id, (D) d );
	//	}


	int followUpInputsCounter;
	int sourceInputsCounter;

	public boolean reassign(MRData lhs, MRData rhs) {
		if ( lhs.isReassignable() == false ){
			return false;
		}


		LOGGER.log(Level.FINE,"!!!Reassigning "+lhs.id +" "+lhs);

		MRData _lhs;
		try {
			if(MR.extractCost) {
				MR.executedAction++;
				followUpInputsCounter++;
				sourceInputsCounter--;

				return true;}
			else {
				_lhs = (MRData) rhs.clone();
				rhs.setAlreadyUsedInRHS();

				_lhs.id = lhs.id;
				_lhs.addReassignment( rhs );
				generatedData.put( lhs.id, (D)_lhs );
				reassignedData.put( lhs.id, (D)_lhs );

				followUpInputsCounter++;
				sourceInputsCounter--;

				return true;
			}
		} catch (CloneNotSupportedException e) {
			return false;
		}

	}

	public boolean contains(MRData lhs) {
		return generatedData.containsKey(lhs.id);
	}

	public void cleanupReassignedData() {

		for (  Entry<String, D> e:  reassignedData.entrySet() ) {
			//			System.out.println("Deleting "+e.getKey());
			//No need to set D as not reassigned because it should never be referenced again; anyway, it would be unsafe.
			generatedData.remove(e.getKey());	
		}

		reassignedData.clear();
	}

	public int getUsedSourceInputs() {
		return generatedData.size()-reassignedData.size();
	}

	public int size() {
		return inputs.size();
	}

	List<D> unshuffled = null;
	Random rnd = null;
	public void shuffle() {
		if ( unshuffled != null ) {
			inputs = unshuffled;
		} else {
			//			System.out.println("New random");
			rnd = new Random(System.currentTimeMillis());
		}

		int start = START % LEN;

		//we shuffle everything except the one at position start, which should remain at position start

		unshuffled = inputs;
		inputs = new LinkedList<D>(); 

		if ( BASE_START > 0 ) {
			LinkedList<D> toShuffle = new LinkedList<D>();

			//			toShuffle.addAll(unshuffled.subList(BASE_START,BASE_START+LEN));
			if (BASE_START+LEN > 160) { 
				return;
//				int a = BASE_START+LEN;
//				System.out.println(a);
//				toShuffle.addAll(unshuffled.subList(BASE_START, BASE_START+LEN)); 
			}
			else {
				toShuffle.addAll(unshuffled.subList(BASE_START,BASE_START+LEN));
			}

			D first = toShuffle.remove(start);

			Collections.shuffle(toShuffle, rnd);

			//At the beginning we keep unshuffled data
			inputs.addAll(unshuffled.subList(0,BASE_START));

			//Shuffled data in the middle
			inputs.add(start, first);
			inputs.addAll(toShuffle);

			//At the end we keep unshuffled data
			inputs.addAll(unshuffled.subList(BASE_START+LEN,unshuffled.size()));

		} else {
			//Original implementation for non shuffled code
			LinkedList<D> toShuffle = new LinkedList<D>();
			toShuffle.addAll(unshuffled);

			D first = toShuffle.remove(start);

			Collections.shuffle(toShuffle, rnd);

			inputs.addAll(toShuffle);
			inputs.add(start, first);
		}

		cleanUpGeneratedAndReassignedData();

	}

	public void unshuffle() {
		if ( unshuffled != null ) {
			inputs = unshuffled;
		}
		unshuffled = null;
	}

	public List<D> values() {
		//FIXME: probably we need to clone them see implementation of get)
		LinkedList<D> all = new LinkedList<D>();
		all.addAll(inputs);
		return all;
	}

	public void setSplit(int totalSplits, int selectedSplit) {
		int chunksSize = LEN / totalSplits;

		BASE_START = chunksSize * selectedSplit;
		START = BASE_START;
		int chunkEnd = Math.min( START + chunksSize, LEN );

		LEN = chunkEnd;
	}

	public void setSplit_cost(int totalSplits, int selectedSplit) {
		LEN = 160;
		int chunksSize = LEN / totalSplits;
		BASE_START = chunksSize * selectedSplit;
		START = BASE_START;

		int chunkEnd = Math.min( START + chunksSize, LEN );

		LEN = chunkEnd;
		MR.reset = true; 

		followUpInputsCounter=0;
		sourceInputsCounter=0;
	}

	protected void set(int i, D v) {
		String key = dbName+"("+i+")";
		generatedData.put( key, v );
	}

	public boolean shufflingEnabled() {
		return true;
	}

	public int shuffleSize() {
		return size();
	}
}
