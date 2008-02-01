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

import java.sql.*;
import java.util.*;

import javax.jws.*;

import net.tinyos.tinysoa.common.*;
import net.tinyos.tinysoa.util.*;

/*******************************************************************************
 * Class that implements the network Web service functionality.
 * 
 * @author		Edgardo Avilés López
 * @version	0.5, 07/28/2006
 ******************************************************************************/
@WebService(name="NetServ", targetNamespace="http://numenor.cicese.mx/TinySOA")
public class NetServImpl implements NetServ
{
	private Connection db;
	private int netID;
	
	/***************************************************************************
	 * Class constructor.
	 * 
	 * @param db	Database connector object to use
	 * @param netID	ID of the sensor network to represent
	 **************************************************************************/
	public NetServImpl(Connection db, int netID) {
		this.db = db;
		this.netID = netID;
	}
	
	/***************************************************************************
	 * Returns the ID of the sensor network.
	 * 
	 * @return	ID of sensor network
	 **************************************************************************/
	@WebMethod(operationName="getNetID", action="urn:getNetID")
	@WebResult(name="getNetIDResult")
	public int getNetID() {
		return netID;
	}

	/***************************************************************************
	 * Returns the name of the sensor network.
	 * 
	 * @return	Name of sensor network
	 **************************************************************************/
	@WebMethod(operationName="getNetName", action="urn:getNetName")
	@WebResult(name="getNetNameResult")
	public String getNetName() {
		String name = "";
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = db.createStatement();
			rs = st.executeQuery("SELECT name FROM networks WHERE id=" + netID);
			if (rs.next()) name = rs.getString("name");
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}
		return name;
	}
	
	/***************************************************************************
	 * Returns the description of the sensor network.
	 * 
	 * @return	Description of the sensor network
	 **************************************************************************/
	@WebMethod(operationName="getNetDescription",
			action="urn:getNetDescription")
	@WebResult(name="getNetDescriptionResult")
	public String getNetDescription() {
		String description = "";
		Statement st = null;
		ResultSet rs = null;
		try {
			st = db.createStatement();
			rs = st.executeQuery(
					"SELECT description FROM networks WHERE id=" + netID);
			if (rs.next()) description = rs.getString("description");
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}
		return description;
	}
	
	/***************************************************************************
	 * Returns the start date and time of the available data.
	 * 
	 * @return	Minimum date and time of the data
	 **************************************************************************/
	@WebMethod(operationName="getMinDateTime", action="urn:getMinDateTime")
	@WebResult(name="getMinDateTimeResult")
	public String getMinDateTime() {
		String dateTime = "";
		Statement st = null;
		ResultSet rs = null;
		try {
			st = db.createStatement();
			rs = st.executeQuery("SELECT MIN(date_time) AS date_time FROM " +
					"history WHERE net_id=" + netID);
			if (rs.next()) dateTime = rs.getString("date_time");
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}
		return dateTime;
	}

	/***************************************************************************
	 * Returns the end date and time of the available data.
	 * 
	 * @return	Maximum date and time of the data
	 **************************************************************************/
	@WebMethod(operationName="getMaxDateTime", action="urn:getMaxDateTime")
	@WebResult(name="getMaxDateTimeResult")
	public String getMaxDateTime() {
		String dateTime = "";
		Statement st = null;
		ResultSet rs = null;
		try {
			st = db.createStatement();
			rs = st.executeQuery("SELECT MAX(date_time) AS date_time FROM " +
					"history WHERE net_id=" + netID);
			if (rs.next()) dateTime = rs.getString("date_time");
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}
		return dateTime;
	}
	
	/***************************************************************************
	 * Returns a listing of all the available nodes on the network.
	 * 
	 * @return	A vector of sensor nodes
	 * @see		Node
	 **************************************************************************/
	@WebMethod(operationName="getNodesList", action="urn:getNodesList")
	@WebResult(name="getNodesListResult")
	public Vector<Node> getNodesList() {
		Vector<Node> nodes = new Vector<Node>();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = db.createStatement();
			rs = st.executeQuery("SELECT node_id FROM history WHERE net_id=" + netID +
					" GROUP BY node_id ORDER BY node_id");
			
			while (rs.next()) {
				Node n = new Node();
				n.setId(rs.getInt("node_id"));
				nodes.add(n);
			}
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}
		
		return nodes;
	}
	
	/***************************************************************************
	 * Returns a listing of all the available actuators on the network.
	 * 
	 * @return	A vector of actuators
	 * @see		Actuator
	 **************************************************************************/
	@WebMethod(operationName="getActuatorsList", action="urn:getActuatorsList")
	@WebResult(name="getActuatorsListResult")
	public Vector<Actuator> getActuatorsList() {
		// TODO Implement getActuatorsList() service method
		return null;
	}

	/***************************************************************************
	 * Returns a listing of all the available sensor types on the network.
	 * 
	 * @return	A vector of sensor types
	 * @see		Parameter
	 **************************************************************************/
	@WebMethod(operationName="getSensorTypesList",
			action="urn:getSensorTypesList")
	@WebResult(name="getSensorTypesListResult")
	public Vector<Parameter> getSensorTypesList() {
		Vector<Parameter> parameters = new Vector<Parameter>();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = db.createStatement();
			rs = st.executeQuery("SELECT t1.parameter, t2.description FROM " +
					"parameters AS t1, descriptions AS t2 WHERE net_id=" +
					netID + " AND t1.parameter = t2.parameter " +
					"ORDER BY t1.parameter");
			
			while (rs.next()) {
				Parameter p = new Parameter();
				p.setName(rs.getString("parameter"));
				p.setDescription(rs.getString("description"));
				parameters.add(p);
			}
			
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}		
		
		return parameters;
	}
	
	/***************************************************************************
	 * Returns a listing of the last readings by sensor node on the network.
	 * 
	 * @param type	The asked sensor type, leave empty for "all"
	 * @param limit	The limit number of readings to return
	 * @return		A vector of readings
	 * @see			Reading
	 **************************************************************************/
	@WebMethod(operationName="getLastReadings", action="urn:getLastReadings")
	@WebResult(name="getLastReadingsResult")
	public Vector<Reading> getLastReadings(
			@WebParam(name="sensorType") String sensorType,
			@WebParam(name="limit") int limit) {
		Vector<Reading> readings = new Vector<Reading>();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			Vector<Node> nodes = getNodesList();
			for (int i = 0; i < nodes.size(); i++) {
			
				st = db.createStatement();
				if (sensorType.compareTo("") == 0)
					rs = st.executeQuery("SELECT * FROM history WHERE net_id=" +
							netID + " AND node_id='" +
							((Node)nodes.get(i)).getId() + "' " +
							"ORDER BY date_time DESC LIMIT 0," +
							getSensorTypesList().size());
				else rs = st.executeQuery("SELECT * FROM history WHERE net_id=" +
							netID + " AND node_id='" +
							((Node)nodes.get(i)).getId() + "' AND parameter='" +
							sensorType + "' " +
							"ORDER BY date_time DESC LIMIT 0,1");
			
				while (rs.next()) {
					Reading p = new Reading();
					p.setNid(rs.getInt("node_id"));
					p.setParameter(rs.getString("parameter"));
					p.setDateTime(rs.getString("date_time"));
					p.setValue(rs.getString("value"));
					readings.add(p);
				}
			}
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}
		
		return readings;	
	}
	
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
	@WebMethod(operationName="getReadingsUntil", action="urn:getReadingsUntil")
	@WebResult(name="getReadingsUntilResult")
	public Vector<Reading> getReadingsUntil(
			@WebParam(name="dateTime") String dateTime,
			@WebParam(name="limit") int limit) {
		Vector<Reading> readings = new Vector<Reading>();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = db.createStatement();
			rs = st.executeQuery("SELECT * FROM history WHERE net_id=" + netID +
					" AND date_time <= '" + dateTime +
					"' ORDER BY date_time DESC LIMIT 0," + limit);
			
			while (rs.next()) {
				Reading p = new Reading();
				p.setNid(rs.getInt("node_id"));
				p.setParameter(rs.getString("parameter"));
				p.setDateTime(rs.getString("date_time"));
				p.setValue(rs.getString("value"));
				readings.add(p);
			}
			
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}
		
		return readings;
	}
	
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
	@WebMethod(operationName="getReadings", action="urn:getReadings")
	@WebResult(name="getReadingsResult")
	public Vector<Reading> getReadings(
			@WebParam(name="startDateTime") String startDateTime,
			@WebParam(name="endDateTime") String endDateTime,
			@WebParam(name="sensorType") String sensorType,
			@WebParam(name="limit") int limit) {
		Vector<Reading> readings = new Vector<Reading>();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = db.createStatement();
			
			String query = "SELECT * FROM history WHERE net_id=" + netID;
			if (sensorType.compareTo("") != 0)
				query += " AND parameter='" + sensorType + "'";
			query += " AND date_time >= '" + startDateTime +
				"' AND date_time <= '" + endDateTime +
				"' ORDER BY node_id ASC, date_time DESC";
			if (limit > 0) query += " LIMIT 0," + limit;
			
			rs = st.executeQuery(query);
			
			while (rs.next()) {
				Reading p = new Reading();
				p.setNid(rs.getInt("node_id"));
				p.setParameter(rs.getString("parameter"));
				p.setDateTime(rs.getString("date_time"));
				p.setValue(rs.getString("value"));
				readings.add(p);
			}
			
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}		
		
		return readings;
	}
	
	/***************************************************************************
	 * Returns a listing for all the registered events. Returns all the events
	 * if <code>limit</code> is <code>0</code> or the number of events given by
	 * <code>limit</code>.
	 * 
	 * @param limit	The limit of events to return. <code>0</code> for all
	 * @return		A vector of events
	 * @see			Event
	 **************************************************************************/
	@WebMethod(operationName="getEventsList", action="urn:getEventsList")
	@WebResult(name="getEventsListResult")
	public Vector<Event> getEventsList(
			@WebParam(name="limit") int limit) {
		Vector<Event> events = new Vector<Event>();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = db.createStatement();
			
			String query = "SELECT * FROM events WHERE net_id=" + netID +
					" ORDER BY id DESC";
			if (limit > 0) query += " LIMIT 0," + limit;
			
			rs = st.executeQuery(query);
			
			while (rs.next()) {
				Event e = new Event();
				e.setId(rs.getInt("id"));
				e.setAdded(rs.getString("added"));
				e.setName(rs.getString("name"));
				e.setCriteria(rs.getString("criteria"));
				e.setDetected(rs.getBoolean("detected"));
				e.setNid(rs.getInt("node_id"));
				e.setDateTime(rs.getString("date_time"));
				events.add(e);
			}
			
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}		
		
		return events;
	}
	
	/***************************************************************************
	 * Returns information about the specified event ID.
	 * 
	 * @param id	Event ID
	 * @return		An event object containing full event information
	 * @see			Event
	 **************************************************************************/
	@WebMethod(operationName="getEventByID", action="urn:getEventByID")
	@WebResult(name="getEventByIDResult")
	public Event getEventByID(@WebParam(name="id") int id) {
		Event event = new Event();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = db.createStatement();
			
			String query = "SELECT * FROM events WHERE net_id=" + netID +
					" AND id='" + id + "'";
			
			rs = st.executeQuery(query);
			
			while (rs.next()) {
				event.setId(rs.getInt("id"));
				event.setAdded(rs.getString("added"));
				event.setName(rs.getString("name"));
				event.setCriteria(rs.getString("criteria"));
				event.setDetected(rs.getBoolean("detected"));
				event.setNid(rs.getInt("node_id"));
				event.setDateTime(rs.getString("date_time"));
			}
			
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}		
		
		return event;
	}
	
	/***************************************************************************
	 * Adds an event to the events list. If there is an error in the event's
	 * criteria specification, the method returns <code>false</code>, <code>true
	 * <code> otherwise.
	 * 
	 * @param name		Name of the event
	 * @param criteria	Criteria to be matched to produce event detection
	 * @return			<code>False</code> if criteria is incorrect
	 **************************************************************************/
	@WebMethod(operationName="addEvent", action="urn:addEvent")
	@WebResult(name="addEventResult")
	public boolean addEvent(
			@WebParam(name="name") String name,
			@WebParam(name="criteria") String criteria) {
		
		if (name == null) return false;
		if (name.trim().compareTo("") == 0) return false;
		if (criteria == null) return false;
		if (criteria.trim().compareTo("") == 0) return false;
		
		int i, j; String par, val, extra = "", critq = "";
		String[] crit = criteria.toLowerCase().split(" and ");
		for (i = 0; i < crit.length; i++) {
			crit[i] = crit[i].trim();
			j = crit[i].indexOf(">");
			if (j == -1) j = crit[i].indexOf("<");
			if (j == -1) j = crit[i].indexOf("=");
			par = ""; val = "";
			if (j > -1) {
				par = crit[i].substring(0, j).trim();
				val = crit[i].substring(j, crit[i].length());
			}
			if (par.compareTo("date_time") == 0)
				extra += "AND date_time" + val;
			else critq += " OR (parameter='" + par + "' AND value" + val + ")";
		}
		if (critq.length() > 4)
			critq = critq.substring(4);
		
		String query = "SELECT node_id, date_time, parameter AS N FROM history " +
				"WHERE (" +	critq + ") " + extra + " AND net_id='" + netID +
				"' GROUP BY parameter";
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = db.createStatement();
			rs = st.executeQuery(query);
		} catch (SQLException ex) {
			System.out.println(query);
			System.out.println(ex.getMessage());
			return false;
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}
		
		try {
			st = db.createStatement();
			st.executeUpdate("INSERT INTO events(net_id, added, name, " +
					"criteria) VALUES('" + netID + "', NOW(), '" + name +
					"', '" + criteria + "')");
			
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (Exception e) {}
			}
		}
		
		return true;
	}
	
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
	@WebMethod(operationName="updateEvent", action="urn:updateEvent")
	@WebResult(name="updateEventResult")
	public boolean updateEvent(
			@WebParam(name="id") int id,
			@WebParam(name="name") String name,
			@WebParam(name="criteria") String criteria) {
		
		if (name == null) return false;
		if (name.trim().compareTo("") == 0) return false;
		if (criteria == null) return false;
		if (criteria.trim().compareTo("") == 0) return false;
		
		int i, j; String par, val, extra = "", critq = "";
		String[] crit = criteria.toLowerCase().split(" and ");
		for (i = 0; i < crit.length; i++) {
			crit[i] = crit[i].trim();
			j = crit[i].indexOf(">");
			if (j == -1) j = crit[i].indexOf("<");
			if (j == -1) j = crit[i].indexOf("=");
			par = ""; val = "";
			if (j > -1) {
				par = crit[i].substring(0, j).trim();
				val = crit[i].substring(j, crit[i].length());
			}
			if (par.compareTo("date_time") == 0)
				extra += "AND date_time" + val;
			else critq += " OR (parameter='" + par + "' AND value" + val + ")";
		}
		if (critq.length() > 4)
			critq = critq.substring(4);
		
		String query = "SELECT node_id, date_time, parameter AS N FROM history " +
				"WHERE (" +	critq + ") " + extra + " AND net_id='" + netID +
				"' GROUP BY parameter";
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = db.createStatement();
			rs = st.executeQuery(query);
		} catch (SQLException ex) {
			System.out.println(query);
			System.out.println(ex.getMessage());
			return false;
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}
		
		try {
			st = db.createStatement();
			st.executeUpdate("UPDATE events SET name='" + name +
					"', criteria='" + criteria + "' WHERE id=" + id);
			
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (Exception e) {}
			}
		}
		
		return true;
	}
	
	/***************************************************************************
	 * Removes the event with the given ID.
	 * 
	 * @param id	ID of the event to remove
	 **************************************************************************/
	@WebMethod(operationName="removeEvent", action="urn:removeEvent")
	public void removeEvent(@WebParam(name="id") int id) {
		Statement st = null;
		try {
			st = db.createStatement();
			st.executeUpdate("DELETE FROM events WHERE id=" + id);
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (Exception e) {}
			}
		}
	}
	
	/***************************************************************************
	 * Returns a listing of the registered maintenance tasks. Returns all tasks
	 * if <code>limit</code> is <code>0</code> or the number of tasks specified
	 * by <code>limit</code>.
	 * 
	 * @param limit	The number of tasks to return
	 * @return		A vector of tasks
	 * @see			Task
	 **************************************************************************/
	@WebMethod(operationName="getTasksList", action="urn:getTasksList")
	@WebResult(name="getTasksListResult")
	public Vector<Task> getTasksList(
			@WebParam(name="limit") int limit) {
		Vector<Task> tasks = new Vector<Task>();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = db.createStatement();
			
			String query = "SELECT * FROM maintenance WHERE net_id=" + netID +
					" ORDER BY date_time DESC";
			if (limit > 0) query += " LIMIT 0," + limit;
			
			rs = st.executeQuery(query);
			
			while (rs.next()) {
				Task t = new Task();
				t.setId(rs.getInt("id"));
				t.setType(rs.getInt("type"));
				t.setValue(rs.getInt("value"));
				t.setTargetNodeID(rs.getInt("node_id"));
				t.setMinsToRepeat(rs.getInt("repeat") / 1000);
				t.setWaitEventID(rs.getInt("event_id"));
				t.setExecutionDateTime(rs.getString("date_time"));
				t.setLastExecuted(rs.getString("executed"));
				t.setDone(rs.getBoolean("done"));
				tasks.add(t);
			}
			
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}		
		
		return tasks;
	}
	
	/***************************************************************************
	 * Returns information on the maintenance task with the given ID.
	 * 
	 * @param id	ID of the asked task
	 * @return		Task information with the given ID
	 * @see			Task
	 **************************************************************************/
	@WebMethod(operationName="getTaskByID", action="urn:getTaskByID")
	@WebResult(name="getTaskByIDResult")
	public Task getTaskByID(@WebParam(name="id") int id) {
		Task task = new Task();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = db.createStatement();
			
			String query = "SELECT * FROM maintenance WHERE net_id=" + netID +
					" AND id='" + id + "'";
			
			rs = st.executeQuery(query);
			
			while (rs.next()) {
				task.setId(rs.getInt("id"));
				task.setType(rs.getInt("type"));
				task.setValue(rs.getInt("value"));
				task.setTargetNodeID(rs.getInt("node_id"));
				task.setMinsToRepeat(rs.getInt("repeat") / 1000);
				task.setWaitEventID(rs.getInt("event_id"));
				task.setExecutionDateTime(rs.getString("date_time"));
				task.setLastExecuted(rs.getString("executed"));
				task.setDone(rs.getBoolean("done"));
			}
			
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}
		
		return task;
	}
	
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
	@WebMethod(operationName="addTask", action="urn:addTask")
	@WebResult(name="addTaskResult")
	public boolean addTask(
			@WebParam(name="type") int type,
			@WebParam(name="value") int value,
			@WebParam(name="targetNodeID") int targetNodeID,
			@WebParam(name="executionDateTime") String executionDateTime,
			@WebParam(name="minsToRepeat") int minsToRepeat,
			@WebParam(name="waitEventID") int waitEventID) {
		
		if ((type < 2) || (type > 6)) return false;
		
		if (executionDateTime == null) executionDateTime = "";
		minsToRepeat = minsToRepeat * 1000;
		
		String query = "INSERT INTO maintenance(" +
				"type, value, node_id, repeat, event_id, date_time, net_id) " +
				"VALUES('" + type + "', '" + value + "', '" + targetNodeID + "', '" +
				minsToRepeat + "', '" + waitEventID + "', '" + executionDateTime +
				"', '" + netID + "')";
		
		Statement st = null;
		
		try {
			st = db.createStatement();
			st.executeUpdate(query);
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (Exception e) {}
			}
		}
		
		return true;
	}
	
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
	@WebMethod(operationName="updateTask", action="urn:updateTask")
	@WebResult(name="updateTaskResult")
	public boolean updateTask(
			@WebParam(name="id") int id,
			@WebParam(name="tipo") int type,
			@WebParam(name="valor") int value,
			@WebParam(name="destino") int targetNodeID,
			@WebParam(name="tiempo") String executionDateTime,
			@WebParam(name="repetir") int minsToRepeat,
			@WebParam(name="evento") int waitEventID) {
		
		if (executionDateTime == null) executionDateTime = "";
		minsToRepeat = minsToRepeat * 1000;
		
		String query = "UPDATE maintenance SET type='" + type + "', value='" +
				value + "', node_id='" + targetNodeID + "', date_time='" +
				executionDateTime + "', repeat='" + minsToRepeat +
				"', event_id='" + waitEventID + "' WHERE id=" + id;
		
		Statement st = null;
		
		try {
			st = db.createStatement();
			st.executeUpdate(query);
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (Exception e) {}
			}
		}
		
		return true;
	}
	
	/***************************************************************************
	 * Removes the maintenance task with the given ID.
	 * 
	 * @param id	ID of the task to remove
	 **************************************************************************/
	@WebMethod(operationName="removeTask", action="urn:removeTask")
	public void removeTask(@WebParam(name="id") int id) {
		Statement st = null;
		try {
			st = db.createStatement();
			st.executeUpdate("DELETE FROM maintenance WHERE id=" + id);
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (Exception e) {}
			}
		}
	}

}
