/*******************************************************************************
 * Copyright (c) University of Luxembourg 2018-2022
 * Created by Fabrizio Pastore (fabrizio.pastore@uni.lu)
 *      
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *   
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package smrl.utils;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LicenceReader {

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		// TODO Auto-generated method stub
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(new File("target/generated-resources/licenses.xml"));
		doc.getDocumentElement().normalize();
		
		String SEPARATOR = ";";
		
		NodeList nodeList = doc.getElementsByTagName("dependency");
		
		for ( int i =0; i < nodeList.getLength(); i++ ) {
			System.out.println("");
			Node dependncyNode = nodeList.item(i);
			NodeList children = dependncyNode.getChildNodes();
			for ( int j =0; j < children.getLength(); j++ ) {
				Node child = children.item(j);
				if ( child.getNodeName().equals("artifactId") ) {
					System.out.print(child.getTextContent());
				}
				if ( child.getNodeName().equals("version") ) {
					System.out.print(SEPARATOR+child.getTextContent());
				}
				if ( child.getNodeName().equals("licenses") ) {
					NodeList licenses = child.getChildNodes();
					if ( licenses.getLength() == 0 ) {
						//
					}
					for ( int t =0; t < licenses.getLength(); t++ ) {
						Node license = licenses.item(t);
						
						NodeList licenseChilder = license.getChildNodes();
						
						
						
						for ( int q =0; q < licenseChilder.getLength(); q++ ) {
							Node licenseChild = licenseChilder.item(q);
							if ( licenseChild.getNodeName().equals("name") ) {
								
								if ( t > 1 ) {
									System.out.println("");
									System.out.print(SEPARATOR);
								}
								
								System.out.print(SEPARATOR+licenseChild.getTextContent());
							}
							if ( licenseChild.getNodeName().equals("url") ) {
								
								System.out.print(SEPARATOR+licenseChild.getTextContent().trim());
							}
						}
						
						
					}
				}
			}
			
		}
	}

}
