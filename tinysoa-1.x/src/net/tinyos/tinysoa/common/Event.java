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
 * Event class which instances are provided by the services.
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
	private String time;
	private boolean ready;
	
	/***************************************************************************
	 * Defines the event ID.
	 * 
	 * @param id Event ID
	 **************************************************************************/
	public void setId(int id) {
		this.id = id;
	}
	
	/***************************************************************************
	 * Return the event ID.
	 * 
	 * @return	Event ID
	 **************************************************************************/
	public int getId() {
		return id;
	}
	
	/***************************************************************************
	 * Defines the time in which the event was added.
	 * 
	 * @param added	Time of event addition
	 **************************************************************************/
	public void setAdded(String added) {
		this.added = added;
	}
	
	/***************************************************************************
	 * Returns the time in which the event was added.
	 * 
	 * @return Time of event addition
	 **************************************************************************/
	public String getAdded() {
		return added;
	}
	
	/***************************************************************************
	 * Defines the node ID reponsable for the event generation.
	 * 
	 * @param nid	Node ID
	 **************************************************************************/
	public void setNid(int nid) {
		this.nid = nid;
	}
	
	/***************************************************************************
	 * Returns the node ID reponsable for the event generation.
	 * 
	 * @return	Node ID
	 **************************************************************************/
	public int getNid() {
		return nid;
	}
	
	/***************************************************************************
	 * Defines the event name.
	 * 
	 * @param name	Event name
	 **************************************************************************/
	public void setName(String name) {
		this.name = name;
	}
	
	/***************************************************************************
	 * Returns the event name.
	 * 
	 * @return	Event name
	 **************************************************************************/
	public String getName() {
		return name;
	}
	
	/***************************************************************************
	 * Defines the event criteria.
	 * 
	 * @param criterio	Event criteria
	 **************************************************************************/
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	/***************************************************************************
	 * Returns the event criteria.
	 * 
	 * @return	Event criteria
	 **************************************************************************/
	public String getCriteria() {
		return criteria;
	}
	
	/***************************************************************************
	 * Defines the time in which the event was detected.
	 * 
	 * @param tiempo	Event detection time
	 **************************************************************************/
	public void setTime(String time) {
		this.time = time;
	}
	
	/***************************************************************************
	 * Returns the time in which the event was detected.
	 * 
	 * @return	Event detection time
	 **************************************************************************/
	public String getTime() {
		return time;
	}
	
	/***************************************************************************
	 * Defines if the event has been fulfilled or not.
	 * 
	 * @param listo	True if event is fulfilled
	 **************************************************************************/
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	
	/***************************************************************************
	 * Returns if the event has been fulfilled or not.
	 * 
	 * @return	True if event is fulfilled
	 **************************************************************************/
	public boolean getReady() {
		return ready;
	}

}
