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

package net.tinyos.tinysoa.util.dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import net.tinyos.tinysoa.server.*;

/*******************************************************************************
 * Dialog for adding or modifying an event 
 * 
 * @author		Edgardo Avilés López
 * @version	0.2, 07/26/2006
 ******************************************************************************/
public class EventDialog extends JDialog {
	private static final long serialVersionUID = -378032218137841324L;
	
	private NetServ netServ;
	private String name, criteria;
	private int id;
	
	private JLabel l02, l03;
	private JButton b01, b02;
	private JTextField tf01;
	private JTextArea ta01;
	private JPanel p01, p02;
	private JScrollPane sp01;
	private JProgressBar progress;
	
	/***************************************************************************
	 * Class constructor
	 * 
	 * @param window			Window in which the dialog will be displayed
	 * @param netServ			Network information service
	 * @param progress			Progressbar
	 **************************************************************************/
	public EventDialog(JFrame window, NetServ netServ,
			JProgressBar progress) {
		super(window, "Add Event", false);
		this.netServ = netServ;
		this.progress = progress;

		p01 = new JPanel();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(p01, BorderLayout.CENTER);
		p01.setLayout(new GridBagLayout());
		
		l02 = new JLabel("Name:");

		p01.add(l02, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(8, 8, 0, 8), 0, 0));
		
		tf01 = new JTextField();
		
		p01.add(tf01, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(2, 8, 0, 8), 0, 0));
		
		l03 = new JLabel("Criteria:");
		
		p01.add(l03, new GridBagConstraints(1, 3, 1, 1, 0.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(4, 8, 0, 8), 0, 0));

		ta01 = new JTextArea();
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
		
		b01 = new JButton("Ok");
		b01.addActionListener(new DialogEventActions(this));
		b02 = new JButton("Cancel");
		b02.addActionListener(new DialogEventActions(this));

		p02 = new JPanel();
		p02.setOpaque(false);
		p02.add(b01);
		p02.add(b02);
		p02.setBorder(BorderFactory.createEmptyBorder(0,0,4,0));
		
		getContentPane().add(p02, BorderLayout.SOUTH);
		
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		pack();

		setModal(true);
		setResizable(false);
		setLocationByPlatform(true);
	}
	
	/***************************************************************************
	 * Shows the dialog
	 **************************************************************************/
	public void showDialog() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2,
				(dim.height - getSize().height) / 2);
		
		b01.setEnabled(true);
		setVisible(true);
		
		tf01.requestFocus();
	}
	
	public void setValues(int id, String name, String criteria) {
		this.id = id;
		this.name = name;
		this.criteria = criteria;
		
		tf01.setText(name);
		ta01.setText(criteria);
	}
	
	/***************************************************************************
	 * Implements the dialog events
	 * 
	 * @author		Edgardo Avilés López
	 * @version	0.1, 07/26/2006
	 **************************************************************************/
	private class DialogEventActions implements ActionListener {

		boolean same = false;
		Object old;
		JDialog f;
		
		/***********************************************************************
		 * Class constructor
		 * 
		 * @param f	Parent Dialog
		 **********************************************************************/
		public DialogEventActions(JDialog f) {
			this.f = f;			
		}
		
		/***********************************************************************
		 * Exits the system
		 **********************************************************************/
		private void cancel() {
			setVisible(false);
		}
		
		/***********************************************************************
		 * Controls the creation/selection event in the sensor network
		 **********************************************************************/
		private void accept() {
			if (tf01.getText().trim().compareTo("") == 0) {
				JOptionPane.showMessageDialog(
						f, "You must provide an event name.",
						"Problem", JOptionPane.WARNING_MESSAGE);
				tf01.requestFocus();
				return;
			}
			
			if (ta01.getText().trim().compareTo("") == 0) {
				JOptionPane.showMessageDialog(
						f, "You must indicate an event criteria.",
						"Problem", JOptionPane.WARNING_MESSAGE);
				ta01.requestFocus();
				return;
			}
			
			if (ta01.getText().toLowerCase().indexOf("or") >= 0) {
				JOptionPane.showMessageDialog(
						f, "The OR operator is not supported.",
						"Problem", JOptionPane.WARNING_MESSAGE);
				ta01.requestFocus();
				return;				
			}

			ta01.setText(ta01.getText().replaceAll("\\(", ""));
			ta01.setText(ta01.getText().replaceAll("\\)", ""));

			new Thread() {
				public void run() {
					progress.setVisible(true);
					b01.setEnabled(false);
			
					name = tf01.getText().trim();
					criteria = ta01.getText().trim();

					boolean res = false;
					if (getTitle().compareTo("Add Event") == 0)
						res = netServ.addEvent(name, criteria);
					if (getTitle().compareTo("Modify Event") == 0)
						res = netServ.updateEvent(id, name, criteria);

					if (!res) {
						JOptionPane.showMessageDialog(
								f, "There is an error in the specified criteria, " +
								"please verify all parameter names and sintax are correct " +
								"and try again.",
								"Problem", JOptionPane.WARNING_MESSAGE);
						ta01.requestFocus();
						b01.setEnabled(true);
						progress.setVisible(false);
						return;
					}

					progress.setVisible(false);
					setVisible(false);
				}
			}.start();
		}
		
		/***********************************************************************
		 * Event controller function
		 * 
		 * @param	Event producer action
		 **********************************************************************/
		public void actionPerformed(ActionEvent evt) {
			String cmd = evt.getActionCommand();
			if (cmd.compareTo("Cancel") == 0) cancel();
			if (cmd.compareTo("Ok") == 0) accept();
		}
		
	}
	
}
