/*
 *  Copyright 2006 Edgardo Avilés López
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

package net.tinyos.tinysoa.gateway;

import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import net.tinyos.tinysoa.common.*;
import net.tinyos.tinysoa.util.*;
import net.tinyos.tinysoa.util.Calendar;
import net.tinyos.tinysoa.util.tables.MonitorCellRenderer;

/*******************************************************************************
 * Class that implements the Message Processor module of the TinySOA Gateway
 * component.
 * 
 * @author		Edgardo Avilés López
 * @version	0.3, 07/24/2006
 ******************************************************************************/
public class MessageProcessor {
	private boolean update		= true;
	private boolean convert		= true;
	private boolean autoScroll	= true;
	private boolean ready		= false;
	private int elementsToShow	= 50;
	
	private Robot robot;
	private DefaultTableModel tableModel;
	private JTable table;
	private Properties types, properties;
	private JLabel status;
	private InternalServicesClient client;
	private int sensorBoard, nodeID, netID;
	private Connection db;
	
	private TinySOAMsg m;
	
	/***************************************************************************
	 * Internal services client constructor.
	 *
	 * @param properties	Network properties object
	 * @param table			Table to show the receive messages
	 * @param tableModel	Table model of the specified table
	 * @param status		Status label to show updates
	 **************************************************************************/
	public MessageProcessor(
			Properties properties, JTable table,
			DefaultTableModel tableModel, JLabel status) {
		
		this.table		= table;
		this.tableModel	= tableModel;
		this.properties	= properties;
		this.status		= status;
		
		initializeInterface();
	}
	
	/***************************************************************************
	 * Defines the internal services client to use.
	 *
	 * @param client	Internal services client to use
	 **************************************************************************/
	public void setClient(InternalServicesClient client) {
		this.client = client;
	}
	
	/***************************************************************************
	 * Defines the database connection to use.
	 * 
	 * @param db	Database connector object to use
	 **************************************************************************/
	public void setDB(Connection db) {
		this.db = db;
	}
	
	/***************************************************************************
	 * Prepares the user interface.
	 **************************************************************************/
	private void initializeInterface() {
		int i;
		
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
		String columns[] = new String[]{"ID", "PID", "Type", "NSeq", "Sensor",
				"v1", "v2", "v3", "v4", "v5", "v6", "v7", "v8"};

		for (i = 0; i < columns.length; i++)
			tableModel.addColumn(columns[i]);
		
		MonitorCellRenderer tcr01, tcr02, tcr03;
		
		tcr01	= new MonitorCellRenderer(SwingConstants.CENTER, true);
		tcr02	= new MonitorCellRenderer(SwingConstants.CENTER, false);
		tcr03	= new MonitorCellRenderer(SwingConstants.RIGHT, false);
				
		table.getColumnModel().getColumn(0).setCellRenderer(tcr01);
		for (i = 1; i <= 4; i++)
			table.getColumnModel().getColumn(i).setCellRenderer(tcr02);
		for (i = 5; i <= 12; i++)
			table.getColumnModel().getColumn(i).setCellRenderer(tcr03);
		
		for (i = 0; i < 50; i++)
			tableModel.addRow(new Object[]{});
		
		table.getColumnModel().getColumn(0).setMinWidth(50);
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(1).setMinWidth(50);
		table.getColumnModel().getColumn(1).setMaxWidth(50);
		table.getColumnModel().getColumn(2).setMinWidth(50);
		table.getColumnModel().getColumn(2).setMaxWidth(50);
		table.getColumnModel().getColumn(3).setMinWidth(50);
		table.getColumnModel().getColumn(3).setMaxWidth(50);
		table.getColumnModel().getColumn(4).setMinWidth(60);
		table.getColumnModel().getColumn(4).setMaxWidth(60);
		table.getColumnModel().getColumn(5).setMinWidth(55);
		table.getColumnModel().getColumn(5).setPreferredWidth(55);
		
		types = new Properties();
		for (i = 1; i <= 8; i++)
			types.setProperty("v" + i, Constants.SENSOR_NULL + "");
	}
	
	/***************************************************************************
	 * Defines if the processor is ready to write in the database.
	 * 
	 * @param ready	<code>True</code> if processor is ready
	 **************************************************************************/
	public void setReady(boolean ready) {
		this.ready = ready;
		if (ready) netID = Integer.parseInt(
				properties.getProperty("network.id"));
	}
	
	/***************************************************************************
	 * Method to receive and start to process a message.
	 * 
	 * @param to		Message target
	 * @param message	Received message
	 **************************************************************************/
	public void receive(int to, TinySOAMsg message) {
		this.m = message;
		
		if (update) {
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {			
					int type = m.get_type();
					if (type == Constants.TYPE_READING) {
						if (types.getProperty(Converter.intToSens(
								m.get_sensor())) != null)
							tableModel.insertRow(0,
									processReading(m, convert));
						else return;
					}
					else if (type == Constants.TYPE_REGISTER)
						tableModel.insertRow(
								0, processRegister(m, convert));
					else System.out.println(
							"Error: Type not expected: " + type + ".");
					
					if (tableModel.getRowCount() > elementsToShow)
						tableModel.removeRow(tableModel.getRowCount() - 1);
					
					if (autoScroll) {
						if (table.isFocusOwner()) {
							robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
							robot.keyPress(java.awt.event.KeyEvent.VK_HOME);
							robot.keyRelease(
									java.awt.event.KeyEvent.VK_CONTROL);
							robot.keyRelease(java.awt.event.KeyEvent.VK_HOME);
						}
					}
				}
			});
			
		} else {
			
			int tipo = m.get_type();
			if (tipo == Constants.TYPE_READING) {
				if (types.getProperty(
						Converter.intToSens(m.get_sensor())) != null)
					processReading(m, convert);
				else return;
			}
			else if (tipo == Constants.TYPE_REGISTER)
				processRegister(m, convert);
			else System.out.println("Error: Type not expected: " + tipo + ".");
			
		}
		
		if (db != null) processMaintenance();
	}
	
	/***************************************************************************
	 * Method to process message queue and send messages if required.
	 **************************************************************************/
	private void processMaintenance() {
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = db.createStatement();
			rs = st.executeQuery("SELECT * FROM maintenance WHERE done=0");
			while (rs.next()) {
				
				if (!client.getSendBusy()) {
					Statement st2 = db.createStatement();
					client.sendCommand(rs.getInt("node_id"), rs.getInt("type"),
							rs.getInt("value"));
					st2.execute("UPDATE maintenance SET ready=1 WHERE id=" +
							rs.getInt("id"));
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
	}
	
	/***************************************************************************
	 * Method to process a reading message.
	 * 
	 * @param m			Received message
	 * @param convert	True if data must be converted
	 * @return			An object array with the requested information
	 **************************************************************************/
	private Object[] processReading(TinySOAMsg m, boolean convert) {
		status.setText("<html>Last <b>reading</b> received at <b>" +
				Calendar.currentDate() + " " + Calendar.currentTime() +
				"</b>.</html>");
		
		sensorBoard = m.get_sensor();
		String s = Converter.intToSens(sensorBoard);
		
		nodeID = m.get_id();
		registerReading(
				Integer.parseInt(types.getProperty(s + "v1")), m.get_l1());
		registerReading(
				Integer.parseInt(types.getProperty(s + "v2")), m.get_l2());
		registerReading(
				Integer.parseInt(types.getProperty(s + "v3")), m.get_l3());
		registerReading(
				Integer.parseInt(types.getProperty(s + "v4")), m.get_l4());
		registerReading(
				Integer.parseInt(types.getProperty(s + "v5")), m.get_l5());
		registerReading(
				Integer.parseInt(types.getProperty(s + "v6")), m.get_l6());
		registerReading(
				Integer.parseInt(types.getProperty(s + "v7")), m.get_l7());
		registerReading(
				Integer.parseInt(types.getProperty(s + "v8")), m.get_l8());
		
		return new Object[]{
				Converter.intToId(m.get_id(), convert),
				Converter.intToId(m.get_parent(), convert),
				Converter.intToType(m.get_type(), convert),
				Converter.intToN(m.get_nseq(), 2, convert),
				Converter.intToSens(m.get_sensor(), convert),
				new SensedData(
						Integer.parseInt(types.getProperty(s + "v1")),
						m.get_l1(), convert),
				new SensedData(
						Integer.parseInt(types.getProperty(s + "v2")),
						m.get_l2(), convert),
				new SensedData(
						Integer.parseInt(types.getProperty(s + "v3")),
						m.get_l3(), convert),
				new SensedData(
						Integer.parseInt(types.getProperty(s + "v4")),
						m.get_l4(), convert),
				new SensedData(
						Integer.parseInt(types.getProperty(s + "v5")),
						m.get_l5(), convert),
				new SensedData(
						Integer.parseInt(types.getProperty(s + "v6")),
						m.get_l6(), convert),
				new SensedData(
						Integer.parseInt(types.getProperty(s + "v7")),
						m.get_l7(), convert),
				new SensedData(
						Integer.parseInt(types.getProperty(s + "v8")),
						m.get_l8(), convert)};
	}
	
	/***************************************************************************
	 * Registers the specified parameter into the database.
	 * 
	 * @param parameter	Parameter to register
	 **************************************************************************/
	private void registerParameter(int parameter) {
		if (!ready) return;
		
		int netID = Integer.parseInt(properties.getProperty("network.id"));
		if (parameter != Constants.SENSOR_NULL) {
			String par = Converter.sensorLabel(parameter);
			
			Statement st = null;
			ResultSet rs = null;
			
			try {
				st = db.createStatement();
				rs = st.executeQuery(
						"SELECT * FROM parameters WHERE net_id='" +
						netID + "' AND parameter='" + par +"'");
				if (rs.next()) return;
				
				st.executeUpdate(
						"INSERT INTO parameters VALUES('0', '" + netID +
						"', '" + par + "')");
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
			
		}	
	}
	
	/***************************************************************************
	 * Registers the specified reading into the database.
	 * 
	 * @param type	Reading type
	 * @param value	Raw reading value
	 **************************************************************************/
	private void registerReading(int type, int value) {
		if (!ready) return;
		if (type == Constants.SENSOR_NULL) return;
		
		Statement st = null;
		
		String par = Converter.intToSensParam(type, true);
		
		double val = value;
		if (type == Constants.SENSOR_TEMP)	val = Converter.adcToTempD(value);
		if (type == Constants.SENSOR_VOLT) val = Converter.adcToVoltD(value);
		
		try {
			st = db.createStatement();
			st.executeUpdate("INSERT INTO history VALUES('0', '" + netID +
					"', '" + nodeID + "', NOW(), '" + par +
					"', '" + val + "')");
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
	 * Registers the specified actuator into the database.
	 * 
	 * @param actuator	Actuator to register
	 **************************************************************************/
	private void registerActuator(int actuator) {
		if (!ready) return;
		
		netID = Integer.parseInt(properties.getProperty("network.id"));
		String act = Converter.intToActuator(actuator);
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = db.createStatement();
			rs = st.executeQuery(
					"SELECT * FROM actuators  WHERE net_id='" + netID +
					"' AND actuator='" + act + "'");
			if (rs.next()) return;
			
			st.executeUpdate(
					"INSERT INTO actuators VALUES('0', '" + netID +
					"', '" + act + "')");
			
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
	}
	
	/***************************************************************************
	 * Registers the actuators of the specified sensor board into the database.
	 * 
	 * @param sensorBoard	Sensor board to get its actuators registered
	 **************************************************************************/
	private void registerActuators(int sensorBoard) {
		if (!ready) return;
		
		if (sensorBoard == Constants.MTS310) {
			registerActuator(Constants.ACTUATOR_BUZZER);
			registerActuator(Constants.ACTUATOR_LED_YELLOW);
			registerActuator(Constants.ACTUATOR_LED_RED);
			registerActuator(Constants.ACTUATOR_LED_GREEN);
		} else {
			System.err.println("SensorBoard not expected!");
			System.exit(-1);
		}
	}
	
	/***************************************************************************
	 * Process a register message.
	 * 
	 * @param m			Received message
	 * @param convert	<code>True</code> is data must be converted
	 * @return			An object array with the requested information
	 **************************************************************************/
	private Object[] processRegister(TinySOAMsg m, boolean convert) {
		status.setText("<html>Last <b>registry</b> received at <b>" +
				Calendar.currentDate() + " " + Calendar.currentTime() +
				"</b>.</html>");
		
		netID = Integer.parseInt(properties.getProperty("network.id"));
		
		sensorBoard = m.get_sensor();
		String s = Converter.intToSens(sensorBoard);
		types.setProperty(s, "registred");
		types.setProperty(s + "v1", m.get_l1() + "");
		types.setProperty(s + "v2", m.get_l2() + "");
		types.setProperty(s + "v3", m.get_l3() + "");
		types.setProperty(s + "v4", m.get_l4() + "");
		types.setProperty(s + "v5", m.get_l5() + "");
		types.setProperty(s + "v6", m.get_l6() + "");
		types.setProperty(s + "v7", m.get_l7() + "");
		types.setProperty(s + "v8", m.get_l8() + "");
		
		registerParameter(m.get_l1());
		registerParameter(m.get_l2());
		registerParameter(m.get_l3());
		registerParameter(m.get_l4());
		registerParameter(m.get_l5());
		registerParameter(m.get_l6());
		registerParameter(m.get_l7());
		registerParameter(m.get_l8());
		
		registerActuators(sensorBoard);
		
		table.getColumnModel().getColumn(5).setHeaderValue(
				Converter.sensorLabel(m.get_l1(), 1));
		table.getColumnModel().getColumn(6).setHeaderValue(
				Converter.sensorLabel(m.get_l2(), 2));
		table.getColumnModel().getColumn(7).setHeaderValue(
				Converter.sensorLabel(m.get_l3(), 3));
		table.getColumnModel().getColumn(8).setHeaderValue(
				Converter.sensorLabel(m.get_l4(), 4));
		table.getColumnModel().getColumn(9).setHeaderValue(
				Converter.sensorLabel(m.get_l5(), 5));
		table.getColumnModel().getColumn(10).setHeaderValue(
				Converter.sensorLabel(m.get_l6(), 6));
		table.getColumnModel().getColumn(11).setHeaderValue(
				Converter.sensorLabel(m.get_l7(), 7));
		table.getColumnModel().getColumn(12).setHeaderValue(
				Converter.sensorLabel(m.get_l8(), 8));
		table.getTableHeader().repaint();
		
		new Thread() {
			public void run() {
				if (!client.getSendBusy())
					client.sendCommand(
							0, Constants.TYPE_SUBSCRIBE, sensorBoard);
			}
		}.start();
		
		return new Object[]{
				Converter.intToId(m.get_id(), convert),
				Converter.intToId(m.get_parent(), convert),
				Converter.intToType(m.get_type(), convert),
				Converter.intToN(m.get_nseq(), 2, convert),
				Converter.intToSens(m.get_sensor(), convert),
				Converter.intToSensParam(m.get_l1(), convert),
				Converter.intToSensParam(m.get_l2(), convert),
				Converter.intToSensParam(m.get_l3(), convert),
				Converter.intToSensParam(m.get_l4(), convert),
				Converter.intToSensParam(m.get_l5(), convert),
				Converter.intToSensParam(m.get_l6(), convert),
				Converter.intToSensParam(m.get_l7(), convert),
				Converter.intToSensParam(m.get_l8(), convert)
		};
	}
	
}
