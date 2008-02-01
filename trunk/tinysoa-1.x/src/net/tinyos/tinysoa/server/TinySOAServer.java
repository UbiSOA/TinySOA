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

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

import javax.swing.*;

import net.tinyos.tinysoa.util.*;
import net.tinyos.tinysoa.util.dialogs.*;

import org.apache.log4j.*;
import org.codehaus.xfire.*;
import org.codehaus.xfire.annotations.*;
import org.codehaus.xfire.annotations.jsr181.*;
import org.codehaus.xfire.server.http.*;
import org.codehaus.xfire.service.*;
import org.codehaus.xfire.service.invoker.*;
import org.mortbay.util.*;

/*******************************************************************************
 * Class that implements the functionality of the TinySOA Server component.
 * 
 * @author		Edgardo Avilés López
 * @version	1.1, 11/28/2007
 ******************************************************************************/

public class TinySOAServer {
	private static String WINDOW_TITLE = "TinySOA Server v1.1";
	private static String CONFIG_FILE = "config.xml";
	private static String serv, user, passwd, dBase;
	private static String port;
	private static Connection db;

	private static XFireHttpServer server;
	
	private static Logger logger = Logger.getLogger(TinySOAServer.class);
	
	/***************************************************************************
	 * Method to load configuration parameters from <code>CONFIG_FILE</code>.
	 **************************************************************************/
	private static void loadConfig() {
		serv = user = passwd = dBase = "";
		
		try {
			Properties config = new Properties();
			config.loadFromXML(new FileInputStream(CONFIG_FILE));
			
			serv	= config.getProperty("mysql.server");
			user	= config.getProperty("mysql.user");
			passwd	= config.getProperty("mysql.password");
			dBase	= config.getProperty("mysql.database");
			port	= config.getProperty("server.port");
			
			if (serv == null)	serv = "localhost";
			if (user == null)	user = "root";
			if (passwd == null)	passwd = "";
			if (dBase == null)	dBase = "tinysoadb";
			if (port == null)	port = "8080";
			
			config.setProperty("mysql.server", serv);
			config.setProperty("mysql.user", user);
			config.setProperty("mysql.password", passwd);
			config.setProperty("mysql.database", dBase);
			config.setProperty("server.port", port);
			
			config.storeToXML(new FileOutputStream(CONFIG_FILE), null);
		} catch (IOException e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, e.getMessage(), "Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}
	
	/***************************************************************************
	 * Method to make a database connection.
	 **************************************************************************/
	private static void connectDB() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			db = DriverManager.getConnection("jdbc:mysql://" + serv + "/" +
					dBase + "?user=" + user + "&password=" + passwd);
		} catch (Exception ex) {
			
			// Show database connection dialog on access fail.
			DatabaseDialog d;
			d = new DatabaseDialog(logger, serv, user, passwd, dBase);
			d.setVisible(true);
			
			try {
				
				// Save dialog new DB access configuration.
				Properties configuracion = new Properties();
				configuracion.loadFromXML(
						new FileInputStream(CONFIG_FILE));
			
				serv	= d.server.getText();
				user	= d.username.getText();
				passwd	= new String(d.password.getPassword());
				dBase	= d.database.getSelectedItem().toString();
			
				configuracion.setProperty("mysql.server", serv);
				configuracion.setProperty("mysql.user", user);
				configuracion.setProperty("mysql.password", passwd);
				configuracion.setProperty("mysql.database", dBase);
				configuracion.storeToXML(
						new FileOutputStream(CONFIG_FILE), null);
				
			} catch (IOException e) {
				logger.error(e);
				JOptionPane.showMessageDialog(null, e.getMessage(),
						"Fatal Error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		}
		
		try {
			
			// Try to connect once again. It should work now.
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			db = DriverManager.getConnection("jdbc:mysql://" + serv + "/" +
					dBase + "?user=" + user + "&password=" + passwd);
			
		} catch (Exception e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, e.getMessage(), "Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}
	
	/***************************************************************************
	 * Starts the server and registers the information and a network service for
	 * each of the available sensor networks.
	 **************************************************************************/
	public static void start() {
		XFire xfire = XFireFactory.newInstance().getXFire();
		AnnotationServiceFactory factory = new AnnotationServiceFactory(
				new Jsr181WebAnnotations(), xfire.getTransportManager());
		
		// Information service registration.
		Service service = factory.create(InfoServImpl.class, "InfoServ",
				"http://numenor.cicese.mx/TinySOA", null);
		service.setInvoker(new BeanInvoker(new InfoServImpl(db, port)));
		xfire.getServiceRegistry().register(service);
		
		Statement st = null;
		ResultSet rs = null;

		try {
			
			// Query for available sensor networks.
			st = db.createStatement();
			rs = st.executeQuery("SELECT * FROM networks ORDER BY id");
			
			// Register each network service.
			while (rs.next()) {
				int rid = rs.getInt("id");
				Service netService = factory.create(NetServImpl.class,
						"NetServ" + rid, "http://numenor.cicese.mx/TinySOA",
						null);
				netService.setInvoker(new BeanInvoker(
						new NetServImpl(db, rid)));
				xfire.getServiceRegistry().register(netService);
			}
			
		} catch (SQLException e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, e.getMessage(),
					"Fatal Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}
		
		try {
			server = new XFireHttpServer();
			server.setPort(Integer.parseInt(port));
			server.start();
		} catch (Exception e) {
			if (((MultiException)e).getException(0) instanceof BindException)
				JOptionPane.showMessageDialog(null, "<html>" +
						"The port <i>" + port + "</i> is already in use by another service. Please,<br>" +
						"change the port for TinySOA Server in the " + CONFIG_FILE + " file.</html>",
						"Server Initialization Error", JOptionPane.ERROR_MESSAGE);
			logger.error(e);
			System.exit(1);
		}
	}
	
	/***************************************************************************
	 * Stops the service provider
	 **************************************************************************/
	public static void stop() {
		try {
			server.stop();
		} catch (Exception e) {
			Errors.error(e, "Error stopping the server.");
		}
	}
	
	/***************************************************************************
	 * Main method
	 * 
	 * @param args	Arguments
	 **************************************************************************/
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");

		try {
			System.out.println("Starting " + WINDOW_TITLE + "...");
			loadConfig();
			connectDB();
			System.out.println("Opening " + InetAddress.getLocalHost() +
					":" + port + "...");
			start();
			System.out.println("Ready and waiting requests...");
		} catch(Exception ex) {
			Errors.error(ex, "Error starting the system.");
		}
	}	
	
}