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
 * Reading class which instances are provided by the services.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/24/2006
 ******************************************************************************/
public class Reading {
	
	private int nid;
	private String time;
	private String parameter;
	private String value;
	
	/***************************************************************************
	 * Defines the node ID responsable of the reading.
	 * 
	 * @param nid	Node ID
	 **************************************************************************/
	public void setNid(int nid) {
		this.nid = nid;
	}
	
	/***************************************************************************
	 * Returns the node ID responsable of the reading.
	 * 
	 * @return	Node ID
	 **************************************************************************/
	public int getNid() {
		return nid;
	}
	
	/***************************************************************************
	 * Defines the time in which the reading was taken.
	 * 
	 * @param time	Reading taken time
	 **************************************************************************/
	public void setTime(String time) {
		this.time = time;
	}
	
	/***************************************************************************
	 * Returns the time in which the reading was taken.
	 * 
	 * @return	Reading taken time
	 **************************************************************************/
	public String getTime() {
		return time;
	}
	
	/***************************************************************************
	 * Defines the reading parameter.
	 * 
	 * @param parameter	Reading parameter
	 **************************************************************************/
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	
	/***************************************************************************
	 * Returns the reading parameter.
	 * 
	 * @return	Reading parameter
	 **************************************************************************/
	public String getParameter() {
		return parameter;
	}
	
	/***************************************************************************
	 * Defines the reading value.
	 * 
	 * @param value	Reading value
	 **************************************************************************/
	public void setValue(String value) {
		this.value = value;
	}
	
	/***************************************************************************
	 * Returns the reading value.
	 * 
	 * @return	Reading value
	 **************************************************************************/
	public String getValue() {
		return value;
	}
	
	/***************************************************************************
	 * Returns a string with the instance information.
	 * 
	 * @return	A string with the object information
	 **************************************************************************/
	public String toString() {
		return nid + ": " + time + " (" + parameter + "=" + value + ")";
	}
	
}
