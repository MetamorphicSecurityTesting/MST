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

import java.util.ArrayList;
import java.util.HashMap;

public class MrDataDBRandom extends MrDataDB<Object> {
	public static int SIZE = 10; 
			
	public MrDataDBRandom(String dbName) {
		super(dbName);
		LEN=SIZE;
	}


	HashMap<Class, ArrayList<Object>> typesDB = new HashMap<>();
	private Class type;
	
	public Object get(Class type, int i) {
		return this.get( type, i, 0, 100 );
	}
	
	public Object get(Class type, int i, int min, int max) {
		ArrayList<Object> db = typesDB.get(type);
		if ( db == null ) {
			db = populateDB( type, min, max );
		}
		
		int pos = (START+i-1) % LEN;
		
		Object data = db.get(pos);
		
		
		String key = dbName+"("+i+")";
		generatedData.put(key, data);
		
		return data;
	}
	
	private Object createDataValue(Class type) {
		if ( type == String.class ) {
			return String.valueOf( Math.floor( Math.random()*100 ) );
		} else if ( type == Integer.class ) {
			return (int) Math.floor( Math.random()*100 );
		} else if ( type == Double.class ) {
			return Math.random()*100 ;
		} else if ( type == Boolean.class ) {
			if ( Math.random() > 0.5 ) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new RuntimeException("Type "+type+"not handled by "+MrDataDBRandom.class.getName() );
		}
	}
	
	private ArrayList<Object> populateDB(Class type, int min, int max) {
		this.type = type;
		ArrayList<Object> db = new ArrayList<>();
		
		int delta = max-min;
		
		for ( int i = 0; i < LEN; i++ ) {

			double value = min + Math.random()*delta;

			if ( type == String.class ) {
				db.add( String.valueOf( value ) );
			} else if ( type == Integer.class ) {

				int val = (int) value;
				db.add(val);

			} else if ( type == Double.class ) {

				db.add(value);

			} else if ( type == Boolean.class ) {
				if ( value < 0.5 )
					db.add(true);
				else
					db.add(false);
			} else {
				throw new RuntimeException("Type "+type+"not handled by "+MrDataDBRandom.class.getName() );
			}

		}
		return db;
	}

//	@Override
//	public Object get(int i) {
//		Object v = createDataValue(this.type);
//		
//		super.set(i,v);
//		
//		return v;
//	}
	
	@Override
	public void shuffle() {
		
	}
	
	public boolean shufflingEnabled() {
		return false;
	}
	
	@Override
	public int size() {
		return SIZE;
	}
	
	private int nextValue=1;
	public int nextValueCounter() {
		return nextValue++;
	}

	@Override
	public void cleanupReassignedData() {
		nextValue=1;
		typesDB.clear();
		super.cleanupReassignedData();
	}

	public int shuffleSize() {
		return size();
	}
}
