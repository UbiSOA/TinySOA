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

package net.tinyos.tinysoa.util;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.sql.*;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.*;

import org.apache.log4j.*;

/*******************************************************************************
 * Class for a dialog to configure general database access.
 * 
 * @author		Edgardo Avilés López
 * @version	1.0, 11/28/2007
 ******************************************************************************/
public class DatabaseDialog extends JDialog implements PropertyChangeListener {

	private static final long serialVersionUID = -8205378715276535251L;
	
	public JTextField server, username;
	public JPasswordField password;
	public JComboBox database;
	
	private JOptionPane optionPane;
	private JLabel message;
	
	private String iniSrv, iniUsr, iniPwd, iniDB;
	
	private Logger logger;
	
	private ImageIcon icoTSOALogo, icoDBError, icoDBSelect;
	
	private Font plainFont = new Font("Arial", Font.PLAIN, 12);
	private Font boldFont = new Font("Arial", Font.BOLD, 14);
	
	private Connection db = null;
	
	private void loadIcons() {
		icoTSOALogo = new ImageIcon(getClass().getResource(
				"/net/tinyos/tinysoa/img/ico.tinysoa.png"));
		icoDBError = new ImageIcon(getClass().getResource(
				"/net/tinyos/tinysoa/img/dlg.db.error.png"));
		icoDBSelect = new ImageIcon(getClass().getResource(
				"/net/tinyos/tinysoa/img/dlg.db.selection.png"));
	}
	
	private JOptionPane createDBSelectionPane(Vector<String> dbs) {
		JLabel databaseL = new JLabel("Select a TinySOA database:");
		databaseL.setFont(plainFont);
		
		database = new JComboBox(dbs);
		database.setFont(boldFont);
		
		Object[] array = {databaseL, database};
		Object[] options = {"Select", "Back", "Exit"};
		
		return new JOptionPane(array,
        		JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION,
        		icoDBSelect, options, options[0]);
	}
	
	private JOptionPane createDBServerPane() {
		message = new JLabel(
				"<html>Unable to connect to MySQL server <i>" + iniSrv +
				"</i>. Using iniUsr <i>" + iniUsr + "</i> and " +
				((iniPwd.compareTo("") == 0)? "<i>no</i> ": "") +
				"password in the <i>" + iniDB + "</i> database. " +
				"Please provide an valid access and try to connect again.</html>");
		message.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
		message.setPreferredSize(new Dimension(260, 72));
		
		server = new JTextField();			server.setText(iniSrv);
		username = new JTextField();		username.setText(iniUsr);
		password = new JPasswordField();	password.setText(iniPwd);
		
		server.setFont(boldFont);
		username.setFont(boldFont);
		
		JLabel serverL = new JLabel("MySQL Server:");
		serverL.setFont(plainFont);
		
		JLabel usernameL = new JLabel("Username:");
		usernameL.setFont(plainFont);
		
		JLabel passwordL = new JLabel("Password:");
		passwordL.setFont(plainFont);
		
		Object[] array = {message, serverL, server, usernameL, username,
				passwordL, password};
		
		Object[] options = {"Connect…", "Exit"};

        return new JOptionPane(array,
        		JOptionPane.ERROR_MESSAGE, JOptionPane.OK_CANCEL_OPTION,
        		icoDBError, options, options[0]);
	}
	
	/** Creates the reusable dialog. */
	public DatabaseDialog(Logger logger, String initialServer, String initialUsername, String initialPassword,
			String initialDatabase) {
		super((Frame) null, true);
		loadIcons();
		this.logger = logger;
		setTitle("Database Connection");
		setIconImage(icoTSOALogo.getImage());
		setLocationByPlatform(true);
		setResizable(false);
		
		this.iniSrv = initialServer;
		this.iniUsr = initialUsername;
		this.iniPwd = initialPassword;
		this.iniDB = initialDatabase;
		
		optionPane = createDBServerPane();
        setContentPane(optionPane);

        //Handle window closing correctly.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent we) {
        		optionPane.setValue("Exit");
        	}
        });
        
        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
        	public void componentShown(ComponentEvent ce) {
        		server.requestFocusInWindow();
        		server.selectAll();
        	}
        });
        
        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
        pack();
	}
	
	private void tryDBSelect() {
		try {
			Statement st = db.createStatement();
			st.executeUpdate("USE " + database.getSelectedItem());
			ResultSet rs = st.executeQuery("SHOW TABLES");
			Vector<String> tbs = new Vector<String>();
			while (rs.next()) tbs.add(rs.getString(1));
			int tSOATabs = 0;
			tSOATabs += (tbs.indexOf("actuators") != -1)? 1: 0;
			tSOATabs += (tbs.indexOf("descriptions") != -1)? 1: 0;
			tSOATabs += (tbs.indexOf("events") != -1)? 1: 0;
			tSOATabs += (tbs.indexOf("history") != -1)? 1: 0;
			tSOATabs += (tbs.indexOf("maintenance") != -1)? 1: 0;
			tSOATabs += (tbs.indexOf("networks") != -1)? 1: 0;
			tSOATabs += (tbs.indexOf("parameters") != -1)? 1: 0;
			
			if (tSOATabs < 7) {
				JOptionPane.showMessageDialog(this, "<html>" +
						"The selected database is not a TinySOA database, please select<br>" +
						"another database or create a new TinySOA database." +
						"</html>", "Connection Error",
						JOptionPane.ERROR_MESSAGE);
				database.requestFocus();
				return;
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "<html>Unable to connect to " +
					"MySQL Server.<br>Please check access data and try " +
					"again.</html>", "Connection Error",
					JOptionPane.ERROR_MESSAGE);
			optionPane.setValue("Back");
			return;
		}
		
		JOptionPane.showMessageDialog(this,
    			"<html>Database connection is successfully complete.<br>" +
    			"TinySOA system is ready to start.<html>", "Connection Ready",
    			JOptionPane.INFORMATION_MESSAGE);
		setVisible(false);
	}
	
	private void tryConnection() {
		Vector<String> dbs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			db = DriverManager.getConnection("jdbc:mysql://" +
					server.getText() + "/?user=" + username.getText() +
					"&password=" + new String(password.getPassword()));
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "<html>Unable to connect to " +
					"MySQL Server.<br>Please check access data and try " +
					"again.</html>", "Connection Error",
					JOptionPane.ERROR_MESSAGE);
			server.requestFocus();
			server.selectAll();
			return;
		}
		
		try {
			Statement st = db.createStatement();
			ResultSet rs = st.executeQuery("SHOW DATABASES");
			dbs = new Vector<String>();
			while (rs.next()) {
				String dbi = rs.getString("Database");
				if ((dbi.compareTo("information_schema") != 0) &&
					(dbi.compareTo("test") != 0) &&
					(dbi.compareTo("mysql") != 0))
					dbs.add(dbi);
			}
			
			if (dbs.isEmpty()) {
				JOptionPane.showMessageDialog(this, "<html>" +
						"The specified database server does not have any databases.<br>" +
						"Please create a TinySOA database or specify another MySQL<br>" +
						"server and try again.</html>", "Connection Error",
						JOptionPane.ERROR_MESSAGE);
				server.requestFocus();
				server.selectAll();
				return;
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "<html>Unable to connect to " +
					"MySQL Server.<br>Please check access data and try " +
					"again.</html>", "Connection Error",
					JOptionPane.ERROR_MESSAGE);
			server.requestFocus();
			server.selectAll();
			return;
		}
		
		optionPane = createDBSelectionPane(dbs);
		optionPane.addPropertyChangeListener(this);
		setContentPane(optionPane);
		pack();
	}
	
	public void propertyChange(PropertyChangeEvent e) {
		Object value = optionPane.getValue();
		
		if (isVisible() && (e.getSource() == optionPane) && 
				(value != JOptionPane.UNINITIALIZED_VALUE)) {
	        if (value.equals("Exit")) {
	        	logger.info("Exiting without completing database connection.");
	        	System.exit(0);
	        }
	        else if (value.equals("Connect…")) {
	        	tryConnection();
	        }
	        else if (value.equals("Select")) {
	        	tryDBSelect();
	        }
	        else if (value.equals("Back")) {
	        	iniSrv = server.getText();
	        	iniUsr = username.getText();
	        	iniPwd = new String(password.getPassword());
	        	
	        	optionPane = createDBServerPane();
	    		optionPane.addPropertyChangeListener(this);
	    		setContentPane(optionPane);
	    		pack();
	        }
	        else System.out.println(value);
		}
		optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
	}

}
