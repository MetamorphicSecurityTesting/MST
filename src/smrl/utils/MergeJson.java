package smrl.utils;
// Java program to merge two
// files into third file

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;


public class MergeJson
{
	public static String Final_JSON_String;
	public static JSONObject merge = new JSONObject();
	public static JSONObject tempobj;
	
	
    public static List<File> listf(String directoryName) {
        File directory = new File(directoryName);
        List<File> resultList = new ArrayList<File>();
        // get all the files from a directory
      

        File[] fList = directory.listFiles();
        resultList.addAll(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile()) {
               // System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                resultList.addAll(listf(file.getAbsolutePath()));
            }
        }
        //System.out.println(fList);
        return resultList;
    } 


   
    
	
	public static void main(String[] args) 
	{
		System.out.println("Start");
	    FileWriter file_Writer;

	    String dest = args[1];
		
		//Getting all files in folder and sub folders, also only selecting inputs.json files from all the directories
		List<File> mainlist;
		
		File parentDir = new File(args[0]);
		mainlist = listf(parentDir.getAbsolutePath());
        List<File> LIST = new ArrayList<File>();        
    	String temp, temp_compare;
		for (File file : mainlist) {
			//System.out.println("Start");
			temp = file.getAbsolutePath();
			//System.out.println(temp.substring(temp.length() - 11));
			temp_compare = temp.substring(temp.length() - 11);
			if(new String(temp_compare).equals("inputs.json")) {
				LIST.addAll(Arrays.asList(file));				
			}			
        }
		
		
		//Creating final empty JSON object 
		JSONObject Final_JSON_Object;
        JSONArray jsonArrayProfile = new JSONArray();
        JSONParser parse = new JSONParser();

		//
		
		//setting a few variables
		String file_name,file_name_procesesed,JSON_in_string;
		int JSON_Size;	
		String dynamic_path;
		
		try {
				for (File file : LIST) {
					//for each files getting path in string variable			
				file_name = file.getAbsolutePath();		
				System.out.println(file_name);
				
				
				
				//processing path and creating the new key to be replaced			
				file_name_procesesed = file_name.replace(parentDir.getAbsolutePath(), "");
				//System.out.println(file_name_procesesed);
				file_name_procesesed = file_name_procesesed.replace("/inputs.json", "");
				//System.out.println(file_name_procesesed);
				file_name_procesesed = file_name_procesesed.replace("/", ":");
				//System.out.println(file_name_procesesed);

				// Creating parser JSON parser object
				JSONParser parser = new JSONParser();
			
				
				FileReader filereader=new FileReader(file);
				Object obj = parser.parse(filereader);
				
				//creating file reader to read json file	
				//Object obj = parser.parse(new FileReader(file));				
				JSONObject jsonObject = (JSONObject) obj;
	
				//converting json object into hashable JSON array to get the highest number of JSON arrays in each file
				//JSONArray a = (JSONArray) jsonObject.get("path1");
	
				// Identifying the size of array in JSON file
				JSON_Size = jsonObject.size();
				System.out.println("Size of JSON for this file is :" + JSON_Size);

				//iterator to just check if all the files is readable
				//Iterator<JSONObject> iterator = a.iterator();
			  //  String text = "Text with special character /\"\'\b\f\t\r\n.";

			   // System.out.println(jsonObject.escape(text));

				//converting json obect into string to replace paths
				JSON_in_string = jsonObject.toString();
				//System.out.println(jsonObject.escape(JSON_in-string));
//				System.out.println("Converstion to JSON in stiring is sucess - line 105");

				//for loop to iterate over the file and replace the path names
				for (int i = 1; i <= JSON_Size; i++) {
					dynamic_path = "path"+i;
				    
					JSON_in_string = JSON_in_string.replace(dynamic_path, file_name_procesesed + "_path" + i);

				}
				tempobj = (JSONObject)parse.parse(JSON_in_string);

					//JSONObject[] mobjs = new JSONObject[] {merge,tempobj};
					
				merge.putAll(tempobj);

			    JSONArray jsonArray = new JSONArray();
			    jsonArray.add(Final_JSON_String);


				} // end for loopr for files
				
				
				 try {
			    	 
			            // Constructs a FileWriter given a file name, using the platform's default charset
			            file_Writer = new FileWriter(dest);
			            file_Writer.write(merge.toJSONString());
			            System.out.println("Successfully Copied JSON Object to File...");
			         //   System.out.println("\nJSON Object: " + obj);
			            file_Writer.close();
			        } catch (IOException e) {
			            e.printStackTrace();
			 
			        }
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			
			//JSONObject jsonObject = (JSONObject) a;
			//get("path1");

			//System.out.println(.);
			

		
		System.out.println("End - Sucess");

	}
}
