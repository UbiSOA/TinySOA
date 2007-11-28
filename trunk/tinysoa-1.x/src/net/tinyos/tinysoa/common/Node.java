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
 * Node class which instances are provided by the services. It represents a
 * sensor network node.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/24/2006
 ******************************************************************************/
public class Node {
	
	private int id;
	
	/***************************************************************************
	 * Defines the ID of the sensor node.
	 * 
	 * @param id	The ID for the sensor node
	 **************************************************************************/
	public void setId(int id) {
		this.id = id;
	}
	
	/***************************************************************************
	 * Returns the ID of the sensor node.
	 * 
	 * @return	The ID of the sensor node
	 **************************************************************************/
	public int getId() {
		return id;
	}
	
	/***************************************************************************
	 * Returns a string with the sensor node object information.
	 * 
	 * @return	A string with the object information
	 **************************************************************************/
	public String toString() {
		return id + "";
	}
}
