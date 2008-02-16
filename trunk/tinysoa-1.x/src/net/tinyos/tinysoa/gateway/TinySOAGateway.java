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

import net.tinyos.message.*;
import net.tinyos.tinysoa.common.*;
import net.tinyos.tinysoa.util.dialogs.*;
import net.tinyos.tinysoa.util.tables.*;

import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.*;

import org.apache.log4j.*;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.sql.*;

/*******************************************************************************
 * Class that implements the functionality of the TinySOA Gateway component.
 * 
 * @author		Edgardo Avilés López
 * @version	0.2, 07/24/2006
 ******************************************************************************/
public class TinySOAGateway {
	
	private static String WINDOW_TITLE = "TinySOA Gateway";
	private static String CONFIG_FILE = "config.xml";

	/** JFrame of the main window. */
	public static JFrame window;
	
	/** SerialForwarder connector. */
	public static MoteIF mote;
	
	/** Application configuration */
	public static Properties configuration;
	
	/** Internal services client. */
	public static InternalServicesClient client;
	
	/** Messages processor. */
	public static MessageProcessor processor;
	
	/** Database connector. */
	public static Connection db;
	
	private static BorderLayout bl01;
	private static Border bo01;
	private static JLabel l01;
	private static JTable t01;
	private static DefaultTableModel tm01;
	private static JPanel p01, p02;
	private static Border bo02, bo03;
	private static JScrollPane sp01;
	private static BorderLayout bl02, bl03;
	private static JTabbedPane tp01;
	private static Font f01, f02;
	static ImageIcon i01, i02, i03, i04;
	
	private static Logger logger = Logger.getLogger(TinySOAGateway.class);

	/***************************************************************************
	 * Starts SerialForwarder connection.
	 **************************************************************************/
	public static void connect() {		
		String server, user, password, database;
		server = user = password = database = "";
		
		try {
			server		= configuration.getProperty("mysql.server");
			user		= configuration.getProperty("mysql.user");
			password	= configuration.getProperty("mysql.password");
			database	= configuration.getProperty("mysql.database");

			if (server == null) {
				
				server		= "localhost";
				user		= "root";
				password	= "";
				database	= "tinysoadb";
				
				configuration.setProperty("mysql.server", server);
				configuration.setProperty("mysql.user", user);
				configuration.setProperty("mysql.password", password);
				configuration.setProperty("mysql.database", database);
				configuration.storeToXML(
						new FileOutputStream(CONFIG_FILE), null);
			}
		} catch (Exception ex) {}
		
		setStatus(	"<html><strong>Connecting</strong> to " +
						"SerialForwarder&hellip;</html>");

		mote = new MoteIF();
		processor = new MessageProcessor(configuration, t01, tm01, l01, logger);
		client = new InternalServicesClient(mote, processor);
		mote.registerListener(new TinySOAMsg(), client);
		processor.setClient(client);
		
		setStatus("<html><strong>Connected successfully</strong> to " +
				"SerialForwarder.</html>");
		
		setStatus("<html><strong>Connecting</strong> to " +
				"database&hellip;</html>");

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			db = DriverManager.getConnection(
					"jdbc:mysql://" + server + "/" + database + "?" +
					"user=" + user + "&password=" + password);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			//ex.printStackTrace();
			System.err.println("Impossible to connect to database.");
			System.err.println(
					"Please check if the config.xml file information is " +
					"correct and that the database is working correctly.");
			System.exit(1);
		}
		
		processor.setDB(db);
		
		setStatus("<html><strong>Connected successfully</strong> to " +
				"database.</html>");
		
		if (configuration.getProperty("network.id") == null) {
			processor.setReady(false);
			PropertiesDialog dp = new PropertiesDialog(
					processor, window, configuration,
					CONFIG_FILE, db, logger);
			dp.center();
			dp.setVisible(true);
		} else {
			Statement st = null;
			ResultSet rs = null;
			try {
				st = db.createStatement();
				rs = st.executeQuery(
						"SELECT * FROM networks WHERE id='" + 
						configuration.getProperty("network.id") + "'");
				if (rs.next()) processor.setReady(true);
				else {
					processor.setReady(false);
					PropertiesDialog dp = new PropertiesDialog(
							processor, window, configuration,
							CONFIG_FILE, db, logger);
					dp.center();
					dp.setVisible(true);
				}
			} catch (SQLException ex) {
				logger.error(ex);
			} finally {
				if ((rs != null) && (st != null)) {
					try {
						rs.close();
						st.close();
					} catch (Exception e) {}
				}
			}
		}
		
		setStatus("<html>Waiting <strong>registers</strong>&hellip;</html>");
		client.sendCommand(0, Constants.TYPE_REGISTER_REQUEST, 0);
	}
	
	/***************************************************************************
	 * Method to create the user interface.
	 **************************************************************************/
	public static void createWindow() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows." +
					"WindowsLookAndFeel");
		} catch (Exception e) {}
		
		window = new JFrame(WINDOW_TITLE);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setIconImage(i04.getImage());
		
		// Starts user interface construction ----------------------------------

		bl01 = new BorderLayout();
		p01	= new JPanel();

		window.getContentPane().setLayout(bl01);
		window.getContentPane().add(p01, BorderLayout.CENTER);
		
		bo01 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
		bl02 = new BorderLayout();
		p02 = new JPanel();
		
		p01.setBorder(bo01);
		bl02.setHgap(8);
		p01.setLayout(bl02);
		p01.add(p02, BorderLayout.CENTER);
		
		bl03 = new BorderLayout();		
		tp01 = new JTabbedPane(JTabbedPane.LEFT);
		l01 = new JLabel();
		
		bl03.setVgap(8);
		p02.setLayout(bl03);
		f02 = new Font("Arial", Font.BOLD, 12);
		tp01.setFont(f02);
		p02.add(tp01, BorderLayout.CENTER);
		bo02 = BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(150,150,150), 1),
				BorderFactory.createEmptyBorder(6,6,6,6));
		l01.setBorder(bo02);
		f01 = new Font("Arial", Font.PLAIN, 12);
		l01.setFont(f01);
		l01.setBackground(Color.WHITE);
		l01.setOpaque(true);
		p02.add(l01, BorderLayout.SOUTH);
		
		tm01 = new DefaultTableModel();
		t01 = new MonitorTable(tm01);
		sp01 = new JScrollPane(t01, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);		
		
		bo03 = BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(8,8,8,8),
				BorderFactory.createLineBorder(new Color(0x7a8a99), 2));
		sp01.setBorder(bo03);
		sp01.setOpaque(false);
		tp01.add(sp01, i01);
		//tp01.add(new JPanel(), i02);

		// Ends user interface construction ------------------------------------
		
		window.setSize(new Dimension(800, 700));
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation((dim.width - window.getSize().width) / 2,
				(dim.height - window.getSize().height) / 2);
		
		window.setVisible(true);
				
		connect();
	}

	/***************************************************************************
	 * Sets current status in the user interface and in the console.
	 * 
	 * @param s	Status text
	 **************************************************************************/
	private static void setStatus(String s) {
		setStatus(s, false);
	}
	
	/***************************************************************************
	 * Sets current status in the user interface and optionally in the console.
	 * 
	 * @param s			Status text
	 * @param justGUI	<code>True</code> to just update GUI
	 **************************************************************************/
	private static void setStatus(String s, boolean justGUI) {
		l01.setText(s);
		if (!justGUI) logger.info(s.replaceAll("&hellip;", "...").
				replaceAll("\\<.*?\\>",""));
	}
	
	/***************************************************************************
	 * Main class.
	 * 
	 * @param	args	Input arguments
	 **************************************************************************/
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				i01 = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/tab.readings.png"));
				i02 = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/tab.messages.png"));
				i03 = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/ico.properties.gif"));
				i04 = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/ico.tinysoa.png"));
				
				configuration	= new Properties();
		
				try {
					configuration.loadFromXML(
							new FileInputStream(CONFIG_FILE));
				} catch (IOException e) {}
				
				createWindow();
			}
		});
	}
	
}