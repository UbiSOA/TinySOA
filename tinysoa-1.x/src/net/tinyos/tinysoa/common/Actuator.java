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
 * Actuator class from which instances are provided by the services.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/24/2006
 ******************************************************************************/
public class Actuator {
	
	private String name;
	private String description;
	
	/***************************************************************************
	 * Defines the actuator's name.
	 * 
	 * @param name	Actuator's name
	 **************************************************************************/
	public void setName(String name) {
		this.name = name;
	}
	
	/***************************************************************************
	 * Returns the actuator's name.
	 * 
	 * @return	Actuator's name
	 **************************************************************************/
	public String getName() {
		return name;
	}
	
	/***************************************************************************
	 * Defines the actuator's description.
	 * 
	 * @param description	Actuator's description
	 **************************************************************************/
	public void setDescription(String description) {
		this.description = description;
	}
	
	/***************************************************************************
	 * Returns the actuator's description.
	 * 
	 * @return	Actuator's description
	 **************************************************************************/
	public String getDescription() {
		return description;
	}
	
}
