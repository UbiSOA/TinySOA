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
 * Event class which instances are provided by the services. It represents an
 * event which can be detected using the reading data of a sensor network.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/24/2006
 ******************************************************************************/
public class Event {
	
	private int id;
	private int nid;
	private String added;
	private String name;
	private String criteria;
	private String dateTime;
	private boolean detected;
	
	/***************************************************************************
	 * Defines the ID of the event.
	 * 
	 * @param id The ID for the event
	 **************************************************************************/
	public void setId(int id) {
		this.id = id;
	}
	
	/***************************************************************************
	 * Returns the ID of the event.
	 * 
	 * @return	The ID of the event
	 **************************************************************************/
	public int getId() {
		return id;
	}
	
	/***************************************************************************
	 * Defines the date and dateTime in which the event was added.
	 * 
	 * @param added	Time of event addition
	 **************************************************************************/
	public void setAdded(String added) {
		this.added = added;
	}
	
	/***************************************************************************
	 * Returns the date and dateTime in which the event was added.
	 * 
	 * @return Time of event addition
	 **************************************************************************/
	public String getAdded() {
		return added;
	}
	
	/***************************************************************************
	 * Defines the ID of the node who detected the event.
	 * 
	 * @param nid	The ID of the responsible node
	 **************************************************************************/
	public void setNid(int nid) {
		this.nid = nid;
	}
	
	/***************************************************************************
	 * Returns the ID of the node who detected the event.
	 * 
	 * @return	The ID of the responsible node
	 **************************************************************************/
	public int getNid() {
		return nid;
	}
	
	/***************************************************************************
	 * Defines the name of the event.
	 * 
	 * @param name	The name for the event
	 **************************************************************************/
	public void setName(String name) {
		this.name = name;
	}
	
	/***************************************************************************
	 * Returns the name of the event.
	 * 
	 * @return	The name of the event
	 **************************************************************************/
	public String getName() {
		return name;
	}
	
	/***************************************************************************
	 * Defines the criteria for the event.
	 * 
	 * @param criterio	The criteria for event detection
	 **************************************************************************/
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	/***************************************************************************
	 * Returns the criteria for the event.
	 * 
	 * @return	The criteria for event detection
	 **************************************************************************/
	public String getCriteria() {
		return criteria;
	}
	
	/***************************************************************************
	 * Defines the date and dateTime in which the event was detected.
	 * 
	 * @param tiempo	Date and dateTime of event detection
	 **************************************************************************/
	public void setDateTime(String time) {
		this.dateTime = time;
	}
	
	/***************************************************************************
	 * Returns the date and dateTime in which the event was detected.
	 * 
	 * @return	Date and dateTime of event detection
	 **************************************************************************/
	public String getDateTime() {
		return dateTime;
	}
	
	/***************************************************************************
	 * Defines if the event has been detected or not.
	 * 
	 * @param detected	<code>True</code> if the event has been detected
	 **************************************************************************/
	public void setDetected(boolean detected) {
		this.detected = detected;
	}
	
	/***************************************************************************
	 * Returns if the event has been detected or not.
	 * 
	 * @return	<code>True</code> if the event has been detected
	 **************************************************************************/
	public boolean getDetected() {
		return detected;
	}

}
