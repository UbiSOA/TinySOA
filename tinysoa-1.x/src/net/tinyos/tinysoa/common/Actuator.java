/*
 *  Copyright 2006 Edgardo Avil�s L�pez
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
 * Actuator class from which instances are provided by the services. It
 * represents an actuator in the sensor network.
 * 
 * @author		Edgardo Avil�s L�pez
 * @version	0.1, 07/24/2006
 ******************************************************************************/
public class Actuator {
	
	private String name;
	private String description;
	
	/***************************************************************************
	 * Defines the name of the actuator.
	 * 
	 * @param name	The name for the actuator
	 **************************************************************************/
	public void setName(String name) {
		this.name = name;
	}
	
	/***************************************************************************
	 * Returns the name of the actuator.
	 * 
	 * @return	The name of the actuator
	 **************************************************************************/
	public String getName() {
		return name;
	}
	
	/***************************************************************************
	 * Defines the description of the actuator.
	 * 
	 * @param description	The description for the actuator
	 **************************************************************************/
	public void setDescription(String description) {
		this.description = description;
	}
	
	/***************************************************************************
	 * Returns the description of the actuator.
	 * 
	 * @return	The description of the actuator
	 **************************************************************************/
	public String getDescription() {
		return description;
	}
	
}
