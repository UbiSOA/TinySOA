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

package net.tinyos.tinysoa.gateway;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*;

import net.tinyos.tinysoa.util.Errors;

/*******************************************************************************
 * Initial network configuration dialog class. 
 * 
 * @author		Edgardo Avilés López
 * @version	0.2, 07/24/2006
 ******************************************************************************/
public class PropertiesDialog extends JDialog {
	private static final long serialVersionUID = -378032218137841324L;
	
	private JLabel l02, l03;
	private Font f01;
	private JButton b01, b02;
	private JComboBox cb01;
	private JTextArea ta01;
	private JScrollPane sp01;
	@SuppressWarnings("unchecked")
	private Vector v;
	private Connection c;
	private Properties p;
	private JPanel p01, p02;
	private MessageProcessor processor;
	private String propertiesFile;
	
	/***************************************************************************
	 * Main class constructor.
	 * 
	 * @param processor	Message processor tu use
	 * @param f			Frame in which the dialog will be show
	 * @param p			Properties object to update
	 * @param file		Propierties file name
	 * @param c			Database connector
	 **************************************************************************/
	@SuppressWarnings("unchecked")
	public PropertiesDialog(
			MessageProcessor processor, JFrame f, Properties p,
			String file, Connection c) {
		
		super(f, "Initial Configuration", false);
		this.p = p;
		this.c = c;
		this.processor = processor;
		this.propertiesFile = file;

		f01 = new Font("Tahoma", Font.PLAIN, 11);

		p01 = new JPanel();
		p01.setOpaque(false);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(p01, BorderLayout.CENTER);
		p01.setLayout(new GridBagLayout());
		
		l02 = new JLabel("Name:");
		l02.setFont(f01);

		p01.add(l02, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(8, 8, 0, 8), 0, 0));
		
		v = new Vector();
		
		try {
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(
					"SELECT * FROM networks ORDER BY name ASC");
			while (rs.next()) v.add(rs.getString("name"));
		} catch (SQLException e) { Errors.errorBD(e); }
		
		cb01 = new JComboBox(v.toArray());
		cb01.setEditable(true);
		cb01.setFont(f01);
		cb01.addActionListener(new EventsDialog(this));
		cb01.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(0,4,0,0)));
		
		p01.add(cb01, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(2, 8, 0, 8), 0, 0));
		
		l03 = new JLabel("Description:");
		l03.setFont(f01);
		
		p01.add(l03, new GridBagConstraints(1, 3, 1, 1, 0.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(4, 8, 0, 8), 0, 0));

		ta01 = new JTextArea();
		ta01.setFont(f01);
		ta01.setBorder(BorderFactory.createEmptyBorder(4,6,4,6));
		sp01 = new JScrollPane(ta01);
		sp01.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		ta01.setLineWrap(true);
		ta01.setWrapStyleWord(true);
		sp01.setPreferredSize(new Dimension(200,60));
		
		p01.add(sp01, new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(2, 8, 4, 8), 0, 0));
		
		b01 = new JButton("Accept");
		b01.setFont(f01);
		b01.addActionListener(new EventsDialog(this));
		b02 = new JButton("Exit");
		b02.setFont(f01);
		b02.addActionListener(new EventsDialog(this));

		p02 = new JPanel();
		p02.setOpaque(false);
		p02.add(b01);
		p02.add(b02);
		p02.setBorder(BorderFactory.createEmptyBorder(0,0,4,0));
		
		getContentPane().add(p02, BorderLayout.SOUTH);
		
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pack();

		try {
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(
					"SELECT * FROM networks ORDER BY name ASC LIMIT 0,1");
			if (rs.next())
				ta01.setText(rs.getString("description"));
		} catch (SQLException e) { Errors.errorBD(e); }
		
		setModal(true);
		setResizable(false);
		setLocationByPlatform(true);
	}
	
	/***************************************************************************
	 * Moves the window to the screen center.
	 **************************************************************************/
	public void center() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2,
				(dim.height - getSize().height) / 2);
	}
	
	/***************************************************************************
	 * Class that implements the dialog events.
	 * 
	 * @author		Edgardo Avilés López
	 * @version	0.1
	 **************************************************************************/
	private class EventsDialog implements ActionListener {
		boolean same = false;
		Object old;
		JDialog f;
		
		/***********************************************************************
		 * Class constructor.
		 * 
		 * @param f Parent dialog
		 **********************************************************************/
		public EventsDialog(JDialog f) {
			this.f = f;			
		}
		
		/***********************************************************************
		 * Controls the event for a list element selection.
		 * 
		 * @param evt Event information
		 **********************************************************************/
		private void listSelection(ActionEvent evt) {
			Object new_obj = ((JComboBox)evt.getSource()).getSelectedItem();
			
			if (new_obj != null) {
				same = new_obj.equals(old);
				old = new_obj;
				if (("comboBoxChanged".equals(
						evt.getActionCommand())) && (!same)) {
					try {
						Statement st = c.createStatement();
						ResultSet rs = st.executeQuery(
								"SELECT * FROM networks WHERE name='" + 
								new_obj + "'");
						if (rs.next()) ta01.setText(
								rs.getString("description"));
					} catch (SQLException e) { Errors.errorBD(e); }
				}
			}
		}
		
		/***********************************************************************
		 * Exits the system.
		 **********************************************************************/
		private void exit() {
			System.exit(0);
		}
		
		/***********************************************************************
		 * Controls the event of acceptation/selection of sensor network.
		 **********************************************************************/
		private void accept() {
			String name = cb01.getSelectedItem().toString();
			String descr = ta01.getText();

			if (name.trim().compareTo("") == 0) {
				JOptionPane.showMessageDialog(
						f, "You must specify a name for the sensor network.",
						"Problem", JOptionPane.ERROR_MESSAGE);
				cb01.requestFocus();
				return;
			}
			
			if (descr.trim().compareTo("") == 0) {
				JOptionPane.showMessageDialog(
						f, "Your must specify a description for the sensor network.",
						"Problem", JOptionPane.ERROR_MESSAGE);
				ta01.requestFocus();
				return;
			}
			
			int id = 0;
			
			try {
				Statement st = c.createStatement();
				ResultSet rs = st.executeQuery(
						"SELECT * FROM networks WHERE name='" + name + "'");
				if (rs.next()) id = rs.getInt("id");
				
				if (id == 0) {
					st.executeUpdate(
							"INSERT INTO networks VALUES('" + id + "', '" + 
							name + "', '" + descr + "', 'WSN')");
					rs = st.executeQuery(
							"SELECT * FROM networks WHERE name='" + name + "'");
					if (rs.next()) id = rs.getInt("id");
				}
			} catch (SQLException e) { Errors.errorBD(e); }
			
			try {
				p.setProperty("network.id", id + "");
				p.setProperty("network.name", name);
				p.setProperty("network.description", descr);
				p.storeToXML(new FileOutputStream(propertiesFile), null);
			} catch (Exception e) {}
			
			setVisible(false);
			processor.setReady(true);
		}
		
		/***********************************************************************
		 * Method to control the events.
		 * 
		 * @param	Event information
		 **********************************************************************/
		public void actionPerformed(ActionEvent evt) {
			String cmd = evt.getActionCommand();
			if (cmd.compareTo("comboBoxChanged") == 0) listSelection(evt);
			if (cmd.compareTo("Exit") == 0) exit();
			if (cmd.compareTo("Accept") == 0) accept();
		}
		
	}
	
}
