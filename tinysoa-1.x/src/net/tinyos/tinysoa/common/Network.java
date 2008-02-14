/*
 *  Copyright 2006 Edgardo Avilés López
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
 * Network class which instances are provided by the services. It represents a
 * sensor network registered in the system.
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
	 * Defines the ID of the sensor network.
	 * 
	 * @param id	The ID for the sensor network
	 **************************************************************************/
	public void setId(int id) {
		this.id = id;
	}
	
	/***************************************************************************
	 * Returns the ID of the sensor network.
	 * 
	 * @return	The ID of the sensor network
	 **************************************************************************/
	public int getId() {
		return id;
	}
	
	/***************************************************************************
	 * Defines the name of the sensor network.
	 * 
	 * @param name	The name for the sensor network
	 **************************************************************************/
	public void setName(String name) {
		this.name = name;
	}
	
	/***************************************************************************
	 * Returns the name of the sensor network.
	 * 
	 * @return	The name of the sensor network
	 **************************************************************************/
	public String getName() {
		return name;
	}
	
	/***************************************************************************
	 * Defines the description of the sensor network.
	 * 
	 * @param description	The description for the sensor network
	 **************************************************************************/
	public void setDescription(String description) {
		this.description = description;
	}
	
	/***************************************************************************
	 * Returns the description of the sensor network.
	 * 
	 * @return	Network The description of the sensor network
	 **************************************************************************/
	public String getDescription() {
		return description;
	}
	
	/***************************************************************************
	 * Defines the URL of the WSDL description for the sensor network Web
	 * service.
	 * 
	 * @param wsdl	The URL of the WSDL description
	 **************************************************************************/
	public void setWsdl(String wsdl) {
		this.wsdl = wsdl;
	}
	
	/***************************************************************************
	 * Returns the URL of the WSDL description for the sensor network Web
	 * service.
	 * 
	 * @return	The URL of the WSDL description
	 **************************************************************************/
	public String getWsdl() {
		return wsdl;
	}
	
	/***************************************************************************
	 * Returns a string with the network object information.
	 * 
	 * @return	A string with the object information
	 **************************************************************************/
	public String toString() {
		return id + ": " + name + " (" + description + ")";
	}

}
