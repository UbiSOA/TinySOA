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

package net.tinyos.tinysoa.server;

import java.util.*;

import net.tinyos.tinysoa.common.*;

/*******************************************************************************
 * Interface that defines the network Web service.
 * 
 * @author	Edgardo Avilés López
 * @version	0.5, 07/28/2006
 ******************************************************************************/
public interface NetServ {
	
	/***************************************************************************
	 * Returns the ID of the sensor network.
	 * 
	 * @return	ID of sensor network
	 **************************************************************************/
	public int getNetID();
	
	/***************************************************************************
	 * Returns the name of the sensor network.
	 * 
	 * @return	Name of sensor network
	 **************************************************************************/
	public String getNetName();
	
	/***************************************************************************
	 * Returns the description of the sensor network.
	 * 
	 * @return	Description of the sensor network
	 **************************************************************************/
	public String getNetDescription();
	
	/***************************************************************************
	 * Returns the start date and time of the available data.
	 * 
	 * @return	Minimum date and time of the data
	 **************************************************************************/
	public String getMinDateTime();
	
	/***************************************************************************
	 * Returns the end date and time of the available data.
	 * 
	 * @return	Maximum date and time of the data
	 **************************************************************************/
	public String getMaxDateTime();
	
	/***************************************************************************
	 * Returns a listing of all the available nodes on the network.
	 * 
	 * @return	A vector of sensor nodes
	 * @see		Node
	 **************************************************************************/
	public Vector<Node> getNodesList();
	
	/***************************************************************************
	 * Returns a listing of all the available actuators on the network.
	 * 
	 * @return	A vector of actuators
	 * @see		Actuator
	 **************************************************************************/
	public Vector<Actuator> getActuatorsList();
	
	/***************************************************************************
	 * Returns a listing of all the available sensor types on the network.
	 * 
	 * @return	A vector of sensor types
	 * @see		Parameter
	 **************************************************************************/
	public Vector<Parameter> getSensorTypesList();
	
	/***************************************************************************
	 * Returns a listing of the last readings by sensor node on the network.
	 * 
	 * @param sensorType	The asked sensor type, leave empty for "all"
	 * @param limit			The limit number of readings to return
	 * @return				A vector of readings
	 * @see					Reading
	 **************************************************************************/
	public Vector<Reading> getLastReadings(String sensorType, int limit);
	
	/***************************************************************************
	 * Returns a listing of all the available readings until a given date and
	 * time. Returns all the readings if <code>limit</code> is <code>0</code>
	 * or the number of readings given by <code>limit</code>.
	 * 
	 * @param dateTime	The end date and time of the readings
	 * @param limit		The limit of readings to return. <code>0</code> for all
	 * @return			A vector of readings
	 * @see				Reading
	 **************************************************************************/
	public Vector<Reading> getReadingsUntil(String dateTime, int limit);
	
	/***************************************************************************
	 * Returns a listing of all the available readings in the given date and
	 * time range. Returns all the readings if <code>limit</code> is <code>0
	 * </code> or the number of readings given by <code>limit</code>. A sensor
	 * type can be also specified, leave blank to get all sensor types.
	 * 
	 * @param startDateTime	The range start date and time of the readings
	 * @param endDateTime	The range end date and time of the readings
	 * @param sensorType	Readings sensor type, leave blank to all
	 * @param limit			Limit of readings to return. <code>0</code> for all
	 * @return				A vector of readings
	 * @see					Reading
	 **************************************************************************/
	public Vector<Reading> getReadings(String startDateTime, String endDateTime,
			String sensorType, int limit);
	
	/***************************************************************************
	 * Returns a listing for all the registered events. Returns all the events
	 * if <code>limit</code> is <code>0</code> or the number of events given by
	 * <code>limit</code>.
	 * 
	 * @param limit	The limit of events to return. <code>0</code> for all
	 * @return		A vector of events
	 * @see			Event
	 **************************************************************************/
	public Vector<Event> getEventsList(int limit);
	
	/***************************************************************************
	 * Returns information about the specified event ID.
	 * 
	 * @param id	Event ID
	 * @return		An event object containing full event information
	 * @see			Event
	 **************************************************************************/
	public Event getEventByID(int id);
	
	/***************************************************************************
	 * Adds an event to the events list. If there is an error in the event's
	 * criteria specification, the method returns <code>false</code>, <code>true
	 * <code> otherwise.
	 * 
	 * @param name		Name of the event
	 * @param criteria	Criteria to be matched to produce event detection
	 * @return			<code>False</code> if criteria is incorrect
	 **************************************************************************/
	public boolean addEvent(String name, String criteria);
	
	/***************************************************************************
	 * Changes the information of the event with the given ID. If there is an
	 * error in the criteria for the event, the method returns <code>false
	 * </code> and doesn't apply changes, <code>true</code> otherwise.
	 * 
	 * @param id		ID of the event to change
	 * @param name		New name
	 * @param criteria	New criteria
	 * @return			<code>False</code> if criteria is incorrect
	 **************************************************************************/
	public boolean updateEvent(int id, String name, String criteria);
	
	/***************************************************************************
	 * Removes the event with the given ID.
	 * 
	 * @param id	ID of the event to remove
	 **************************************************************************/
	public void removeEvent(int id);
	
	/***************************************************************************
	 * Returns a listing of the registered maintenance tasks. Returns all tasks
	 * if <code>limit</code> is <code>0</code> or the number of tasks specified
	 * by <code>limit</code>.
	 * 
	 * @param limit	The number of tasks to return
	 * @return		A vector of tasks
	 * @see			Task
	 **************************************************************************/
	public Vector<Task> getTasksList(int limit);
	
	/***************************************************************************
	 * Returns information on the maintenance task with the given ID.
	 * 
	 * @param id	ID of the asked task
	 * @return		Task information with the given ID
	 * @see			Task
	 **************************************************************************/
	public Task getTaskByID(int id);
	
	/***************************************************************************
	 * Adds a maintenance task to the list of tasks. If there is an error on the
	 * task specification the method returns <code>false</code>, <code>true
	 * </code> otherwise.
	 * 
	 * @param type				Action type
	 * @param value				Action value
	 * @param targetNodeID		Target node ID, <code>0</code> for all nodes
	 * @param executionDateTime	Date and time in which the task will be executed
	 * 							if it is empty it takes the current date and 
	 * 							time to immediate action
	 * @param minsToRepeat		Amount of minutes in which the task will be
	 * 							repeating, if is <code>0</code> there will be
	 * 							no repetitions
	 * @param waitEventID		ID of the event to wait for task execution, if
	 * 							<code>0</code> there will be no waiting
	 * @return					<code>False</code> if there is an error
	 **************************************************************************/
	public boolean addTask(int type, int value, int targetNodeID,
			String executionDateTime, int minsToRepeat, int waitEventID);
	
	/***************************************************************************
	 * Changes the information of the task with the given ID. If there is an
	 * error on the task specification the method returns <code>false</code>,
	 * <code>true</code> otherwise.
	 * 
	 * @param type				Action type
	 * @param value				Action value
	 * @param targetNodeID		Target node ID, <code>0</code> for all nodes
	 * @param executionDateTime	Date and time in which the task will be executed
	 * 							if it is empty it takes the current date and time
	 * 							to immediate action
	 * @param minsToRepeat		Amount of minutes in which the task will be
	 * 							repeating, if is <code>0</code> there will be
	 * 							no repetitions
	 * @param waitEventID		ID of the event to wait for task execution, if
	 * 							<code>0</code> there will be no waiting
	 * @return					<code>False</code> if there is an error
	 **************************************************************************/
	public boolean updateTask(int id, int type, int value, int targetNodeID,
			String executionDateTime, int minsToRepeat, int waitEventID);
	
	/***************************************************************************
	 * Removes the maintenance task with the given ID.
	 * 
	 * @param id	ID of the task to remove
	 **************************************************************************/
	public void removeTask(int id);

}
