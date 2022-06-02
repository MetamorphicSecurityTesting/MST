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



public class MrMultiDataDB<D> extends MrDataDB<D>{
	Logger LOGGER = Logger.getLogger(MrMultiDataDB.class.getName());
	
	protected MrDataDB<D> current;
	protected HashMap<String,MrDataDB<D>> DBs = new HashMap<String,MrDataDB<D>>();
	

	public MrMultiDataDB(String dbName){
		super(dbName);
	}
	
	public void addDB( MrDataDB<D> DB) {
		DBs.put( DB.getDbName(), DB ); 
	}
	
	public boolean setCurrent( String dbName ) {
		if ( current != null ) {
			if ( current.getDbName().equals(dbName) ) {
				return true;
			}
		}
		current = DBs.get(dbName);
		
		if ( current == null ) {
			return false;
		}
		
		current.cleanUpGeneratedAndReassignedData();
		current.resetTestsCounter();
		
		return true;
	}
	
	protected HashMap<String,D> generatedData = new HashMap<String,D>();
	private HashMap<String,D> reassignedData = new HashMap<String,D>();
	
	public Iterator<D> _it() {
		return current._it();
	}
	
	public D get(int i) {
		return current.get(i);
	}


	
	public void nextTest() {
		current.nextTest();
		LOGGER.log(Level.FINE,"!!!NEXT_TEST");
		START++;
		cleanUpGeneratedAndReassignedData();
	}



	

	public void resetTestsCounter() {
		START=0;
	}

	
	
	@Override
	public List<D> values() {
		// TODO Auto-generated method stub
		return current.values();
	}

	public int size() {
		return 0;
	}
	
	


}
