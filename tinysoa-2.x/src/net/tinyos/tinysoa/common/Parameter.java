/*
 *  Copyright 2007 Edgardo Avil�s L�pez
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
 * Parameter class which instances are provided by the services.
 * 
 * @author		Edgardo Avil�s L�pez
 * @version	0.1, 07/24/2006
 ******************************************************************************/
public class Parameter {
	
	private String name;
	private String description;
	
	/***************************************************************************
	 * Defines the parameter name.
	 * 
	 * @param name	Parameter name
	 **************************************************************************/
	public void setName(String name) {
		this.name = name;
	}
	
	/***************************************************************************
	 * Returns the parameter name.
	 * 
	 * @return	Parameter name
	 **************************************************************************/
	public String getName() {
		return name;
	}
	
	/***************************************************************************
	 * Defines the parameter description.
	 * 
	 * @param description	Parameter description
	 **************************************************************************/
	public void setDescription(String description) {
		this.description = description;
	}
	
	/***************************************************************************
	 * Returns the parameter description.
	 * 
	 * @return	Parameter description
	 **************************************************************************/
	public String getDescription() {
		return description;
	}
	
}
