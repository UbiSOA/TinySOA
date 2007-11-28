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
 * Task class which instances are provided by the services. It represents a
 * maintenance task registered in the system. This tasks allow to send commands
 * to the sensor network, this to change data rate, turn on or off an actuator,
 * put a sensor in sleep mode, etc.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/27/2006
 ******************************************************************************/
public class Task {
	
	private int id;
	private int type;
	private int value;
	private int targetNodeID;
	private int minsToRepeat;
	private int waitEventID;
	private String executionDateTime;
	private String lastExecuted;
	private boolean done;
	
	/***************************************************************************
	 * Defines the ID of the maintenance task.
	 * 
	 * @param id	The ID for the maintenance task
	 **************************************************************************/
	public void setId(int id) {
		this.id = id;
	}

	/***************************************************************************
	 * Returns the ID of the maintenance task.
	 * 
	 * @return The ID of the maintenance task
	 **************************************************************************/
	public int getId() {
		return id;
	}

	/***************************************************************************
	 * Defines the action type to realize in the task.
	 * 
	 * @param type	The action type
	 **************************************************************************/
	public void setType(int type) {
		this.type = type;
	}

	/***************************************************************************
	 * Returns the action type to realize in the task.
	 * 
	 * @return The action type
	 **************************************************************************/
	public int getType() {
		return type;
	}

	/***************************************************************************
	 * Defines the value of the action to realize in the task.
	 * 
	 * @param value	Action value
	 **************************************************************************/
	public void setValue(int value) {
		this.value = value;
	}

	/***************************************************************************
	 * Returns the value of the action to realize in the task.
	 * 
	 * @return Action value
	 **************************************************************************/
	public int getValue() {
		return value;
	}

	/***************************************************************************
	 * Defines the task target node ID. If ID is <code>0</code> the target is
     * all the available nodes in the network.
	 * 
	 * @param targetNodeID	The target sensor node ID
	 **************************************************************************/
	public void setTargetNodeID(int nid) {
		this.targetNodeID = nid;
	}

	/***************************************************************************
     * Returns the task target node ID. If ID is <code>0</code> the target is
     * all the available nodes in the network.
	 * 
	 * @return The target sensor node ID
	 **************************************************************************/
	public int getTargetNodeID() {
		return targetNodeID;
	}

	/***************************************************************************
     * Defines the amount of minutes in which the task will be repeating, if
     * <code>minsToRepeat</code> is <code>0</code> there will be no repetitions.
	 * 
	 * @param minsToRepeat	The amount of minutes to repeat the task
	 **************************************************************************/
	public void setMinsToRepeat(int minsToRepeat) {
		this.minsToRepeat = minsToRepeat;
	}

	/***************************************************************************
	 * Returns the amount of minutes in which the task will be repeating, if
     * <code>minsToRepeat</code> is <code>0</code> there will be no repetitions.
	 * 
	 * @return The amount of minutes to repeat the task
	 **************************************************************************/
	public int getMinsToRepeat() {
		return minsToRepeat;
	}

	/***************************************************************************
	 * Defines the ID of the event which must be detected in order to execute
	 * the task. If <code>waitEventID</code> is <code>0</code> the task will be
	 * executed without waiting for any event.
 	 * 
	 * @param waitEventID The ID of the event to wait
	 **************************************************************************/
	public void setWaitEventID(int eventID) {
		this.waitEventID = eventID;
	}

	/***************************************************************************
	 * Returns the ID of the event which must be detected in order to execute
	 * the task. If <code>event</code> is <code>0</code> the task will be
	 * executed without waiting for any event.
	 * 
	 * @return The ID of the event to wait
	 **************************************************************************/
	public int getWaitEventID() {
		return waitEventID;
	}

	/***************************************************************************
	 * Defines the date and time in which the task will be executed.
	 * 
	 * @param executionDateTime	The date and time of task execution
	 **************************************************************************/
	public void setExecutionDateTime(String dateTime) {
		this.executionDateTime = dateTime;
	}

	/***************************************************************************
	 * Returns the date and time in which the task will be executed.
	 * 
	 * @return The date and time of task execution
	 **************************************************************************/
	public String getExecutionDateTime() {
		return executionDateTime;
	}

	/***************************************************************************
	 * Defines the date and time in which the task was last executed.
	 * 
	 * @param lastExecuted	The date and time of last task execution
	 **************************************************************************/
	public void setLastExecuted(String lastExecuted) {
		this.lastExecuted = lastExecuted;
	}

	/***************************************************************************
	 * Returns the date and time in which the task was last executed.
	 * 
	 * @return The date and time of last task execution
	 **************************************************************************/
	public String getLastExecuted() {
		return lastExecuted;
	}
	
	/***************************************************************************
	 * Defines if the task was already executed.
	 * 
	 * @param done	<code>True</code> if task was executed
	 **************************************************************************/
	public void setDone(boolean done) {
		this.done = done;
	}

	/***************************************************************************
	 * Returns if the task was already executed.
	 * 
	 * @return <code>True</code> if task was executed
	 **************************************************************************/
	public boolean getDone() {
		return done;
	}
	
}
