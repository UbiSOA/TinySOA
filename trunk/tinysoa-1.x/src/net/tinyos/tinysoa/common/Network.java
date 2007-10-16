/*
 *  Copyright 2007 Edgardo Avilés López
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *    
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 ******************************************************************************/

package net.tinyos.tinysoa.common;

/*******************************************************************************
 * Network class which instances are provided by the services.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/24/2006
 ******************************************************************************/
public class Network {
	
	private int id;
	private String name;
	private String description;
	private String wsdl;
	
	/***************************************************************************
	 * Defines the network ID.
	 * 
	 * @param id	Network ID
	 **************************************************************************/
	public void setId(int id) {
		this.id = id;
	}
	
	/***************************************************************************
	 * Returns the network ID.
	 * 
	 * @return	Network ID
	 **************************************************************************/
	public int getId() {
		return id;
	}
	
	/***************************************************************************
	 * Defines the network name.
	 * 
	 * @param name	Network name
	 **************************************************************************/
	public void setName(String name) {
		this.name = name;
	}
	
	/***************************************************************************
	 * Returns the network name.
	 * 
	 * @return	Network name
	 **************************************************************************/
	public String getName() {
		return name;
	}
	
	/***************************************************************************
	 * Defines the network description.
	 * 
	 * @param description	Network description
	 **************************************************************************/
	public void setDescription(String description) {
		this.description = description;
	}
	
	/***************************************************************************
	 * Returns the network description.
	 * 
	 * @return	Network description
	 **************************************************************************/
	public String getDescription() {
		return description;
	}
	
	/***************************************************************************
	 * Defines the URL with the location of the service WSDL file.
	 * 
	 * @param wsdl	URL of WSDL file
	 **************************************************************************/
	public void setWsdl(String wsdl) {
		this.wsdl = wsdl;
	}
	
	/***************************************************************************
	 * Returns the URL with the location of the service WSDL file.
	 * 
	 * @return	URL of WSDL file
	 **************************************************************************/
	public String getWsdl() {
		return wsdl;
	}
	
	/***************************************************************************
	 * Returns a string with the instance information.
	 * 
	 * @return	A string with the object values
	 **************************************************************************/
	public String toString() {
		return id + ": " + name + " (" + description + ")";
	}
	
}
