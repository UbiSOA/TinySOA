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
 * Task class which instances are provided by the services.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/27/2006
 ******************************************************************************/
public class Task {
	
	private int id;
	private int type;
	private int value;
	private int nid;
	private int repeat;
	private int event;
	private String time;
	private String executed;
	private boolean done;
	
	/***************************************************************************
	 * Defines the task ID.
	 * 
	 * @param id	Task ID
	 **************************************************************************/
	public void setId(int id) {
		this.id = id;
	}

	/***************************************************************************
	 * Returns the task ID.
	 * 
	 * @return Task ID
	 **************************************************************************/
	public int getId() {
		return id;
	}

	/***************************************************************************
	 * Defines the action type to realize in the task.
	 * 
	 * @param type	Action type
	 **************************************************************************/
	public void setType(int type) {
		this.type = type;
	}

	/***************************************************************************
	 * Returns the action type to realize in the task.
	 * 
	 * @return Action type
	 **************************************************************************/
	public int getType() {
		return type;
	}

	/***************************************************************************
	 * Defines the action value to realize in the task.
	 * 
	 * @param value	Action value
	 **************************************************************************/
	public void setValue(int value) {
		this.value = value;
	}

	/***************************************************************************
	 * Returns the action value to realize in the task.
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
	 * @param nid	Target node ID
	 **************************************************************************/
	public void setNid(int nid) {
		this.nid = nid;
	}

	/***************************************************************************
     * Returns the task target node ID. If ID is <code>0</code> the target is
     * all the available nodes in the network.
	 * 
	 * @return Target node ID
	 **************************************************************************/
	public int getNid() {
		return nid;
	}

	/***************************************************************************
     * Defines the amount of minutes in which the task will be repeating, if
     * <code>repeat</code> is <code>0</code> there will be no repetitions.
	 * 
	 * @param repeat	Amount of minutes in which the task will be repeating
	 **************************************************************************/
	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

	/***************************************************************************
	 * Returns the amount of minutes in which the task will be repeating, if
     * <code>repeat</code> is <code>0</code> there will be no repetitions.
	 * 
	 * @return Amount of minutes in which the task will be repeating
	 **************************************************************************/
	public int getRepeat() {
		return repeat;
	}

	/***************************************************************************
	 * Defines the event ID which must be detected in order to execute the
     * task. If <code>event</code> is <code>0</code> the task will be executed
     * without waiting for any event.
 	 * 
	 * @param event Event ID to wait
	 **************************************************************************/
	public void setEvent(int event) {
		this.event = event;
	}

	/***************************************************************************
	 * Returns the event ID which must be detected in order to execute the
     * task. If <code>event</code> is <code>0</code> the task will be executed
     * without waiting for any event.
	 * 
	 * @return Event ID to wait
	 **************************************************************************/
	public int getEvent() {
		return event;
	}

	/***************************************************************************
	 * Defines the time in which the task will be executed.
	 * 
	 * @param time	Time of task execution
	 **************************************************************************/
	public void setTime(String time) {
		this.time = time;
	}

	/***************************************************************************
	 * Returns the time in which the task will be executed.
	 * 
	 * @return Time of task execution
	 **************************************************************************/
	public String getTime() {
		return time;
	}

	/***************************************************************************
	 * Defines the time in which the task was last executed.
	 * 
	 * @param executed	Last execution time
	 **************************************************************************/
	public void setExecuted(String executed) {
		this.executed = executed;
	}

	/***************************************************************************
	 * Returns the time in which the task was last executed.
	 * 
	 * @return Last execution time
	 **************************************************************************/
	public String getExecuted() {
		return executed;
	}
	
	/***************************************************************************
	 * Defines if the task was already executed.
	 * 
	 * @param done	True if task was executed
	 **************************************************************************/
	public void setDone(boolean done) {
		this.done = done;
	}

	/***************************************************************************
	 * Returns if the task was already executed.
	 * 
	 * @return True if task was executed
	 **************************************************************************/
	public boolean getDone() {
		return done;
	}
	
}
