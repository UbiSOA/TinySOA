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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;

import net.tinyos.tinysoa.common.*;
import net.tinyos.tinysoa.common.Event;
import net.tinyos.tinysoa.server.*;

/*******************************************************************************
 * Dialog to add or change a task of maintenance.
 * 
 * @author		Edgardo Avilés López
 * @version	0.2, 07/28/2006
 ******************************************************************************/
public class MaintenanceDialog extends JDialog {
	private static final long serialVersionUID = -2649034852161564309L;

	private NetServ networkService;
	private JProgressBar progress;
	private Object[] events;
	private int id = 0;
	
	private JRadioButton rb01, rb02, rb03, rb04, rb05, rb06, rb07;
	private JComboBox cb01, cb02, cb03;
	private JSpinner s01, s02, s03;
	private JButton b01, b02;
	private JTabbedPane tp01;
	private JCheckBox ckb01;
	private JLabel l01;
	
	public MaintenanceDialog(JFrame window, NetServ networkService,
			JProgressBar progress) {
		super(window, "Add Task", false);
		this.networkService = networkService;
		this.progress = progress;
		
		DialogueMaintenanceShares evts =
			new DialogueMaintenanceShares(this);
		
		JPanel p2 = new JPanel();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(p2, BorderLayout.CENTER);
		p2.setLayout(new GridBagLayout());
	
		// Actions Types Tabs -------------------------------------------------
		
		tp01 = new JTabbedPane();
		p2.add(tp01, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(8, 8, 0, 8), 0, 0));
		
		// Actuators Tab -------------------------------------------------------
		
		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new GridBagLayout());
		
		ButtonGroup bg = new ButtonGroup();
		rb01 = new JRadioButton("Turn on");
		rb01.setOpaque(false);
		rb01.setSelected(true);
		p.add(rb01, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(4, 8, 0, 4), 0, 0));
		bg.add(rb01);
		
		rb02 = new JRadioButton("Shutdown");
		rb02.setOpaque(false);
		p.add(rb02, new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(4, 0, 0, 8), 0, 0));
		bg.add(rb02);
		
		cb01 = new JComboBox(new Object[]{"Buzz", "Yellow Led",
				"Blue Led", "Red Led", "Green Led"});
		p.add(cb01, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(4, 8, 8, 8), 0, 0));
		
		tp01.add(p, "Actrs.");
		tp01.setToolTipTextAt(0, "Actuators");
		
		// Rate Tab ------------------------------------------------------------
		
		p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new GridBagLayout());
		
		JLabel l = new JLabel("Change sampling rate:");
		p.add(l, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(8, 8, 0, 8), 0, 0));
		
		s01 = new JSpinner(
				new SpinnerNumberModel(10, 1, 24 * 60 * 60, 1));
		p.add(s01, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(8, 8, 8, 8), 0, 0));
		
		l = new JLabel("seconds");
		p.add(l, new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(8, 0, 8, 8), 0, 0));
		
		tp01.add(p, "Rate");
		tp01.setToolTipTextAt(1, "Sampling Rate");
		
		// Tab waiting -------------------------------------------------------
		
		p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new GridBagLayout());
		
		bg = new ButtonGroup();
		
		rb03 = new JRadioButton("Enter State waiting");
		rb03.setOpaque(false);
		rb03.setSelected(true);
		p.add(rb03, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
				new Insets(0, 8, 0, 8), 0, 0));
		bg.add(rb03);
		
		rb04 = new JRadioButton("Out-of-state waiting");
		rb04.setOpaque(false);
		p.add(rb04, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(0, 8, 0, 8), 0, 0));
		bg.add(rb04);
		
		tp01.add(p, "Wait");
		tp01.setToolTipTextAt(2, "State waiting");
		
		// ComboBox of destiny node --------------------------------------------
		
		p = new JPanel();
		p.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Destiny node"),
				BorderFactory.createEmptyBorder(0, 12, 4, 12)));
		p.setLayout(new BorderLayout());
		p2.add(p, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(8, 8, 0, 8), 0, 0));
		
		cb02 = new JComboBox();
		p.add(cb02, BorderLayout.CENTER);
		
		// Select time -------------------------------------------------
		
		p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder("Time Performance"));
		p.setLayout(new GridBagLayout());
		
		p2.add(p, new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(8, 8, 0, 8), 0, 0));
		
		bg = new ButtonGroup();
		rb05 = new JRadioButton("Immediately");
		rb05.setSelected(true);
		rb05.addActionListener(evts);
		p.add(rb05, new GridBagConstraints(1, 1, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 8, 0, 8), 0, 0));
		bg.add(rb05);
		
		rb06 = new JRadioButton("Execute:");
		rb06.addActionListener(evts);
		p.add(rb06, new GridBagConstraints(1, 2, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 8, 0, 8), 0, 0));
		bg.add(rb06);
		
		s02 = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.MONTH));
		s02.setEditor(new JSpinner.DateEditor(s02, "yyyy-MM-dd HH:mm:ss"));
		p.add(s02, new GridBagConstraints(1, 3, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 28, 0, 8), 0, 0));
		
		ckb01 = new JCheckBox("Repeat every");
		ckb01.addActionListener(evts);
		p.add(ckb01, new GridBagConstraints(1, 4, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 28, 0, 8), 0, 0));
		
		s03 = new JSpinner(new SpinnerNumberModel(10, 1, 60 * 24, 1));
		p.add(s03, new GridBagConstraints(1, 5, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 28, 0, 8), 0, 0));
		
		l01 = new JLabel("minutes");
		p.add(l01, new GridBagConstraints(2, 5, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 8), 0, 0));
		
		rb07 = new JRadioButton("Wait event");
		rb07.addActionListener(evts);
		p.add(rb07, new GridBagConstraints(1, 6, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 8, 0, 8), 0, 0));
		bg.add(rb07);
		
		cb03 = new JComboBox();
		p.add(cb03, new GridBagConstraints(1, 7, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 28, 8, 8), 0, 0));

		// Buttons Accept and Cancel ------------------------------------------
		
		p = new JPanel();
		p.setOpaque(false);
		
		b01 = new JButton("Accept");
		b01.addActionListener(evts);
		p.add(b01);
		
		b02 = new JButton("Cancel");
		b02.addActionListener(evts);
		p.add(b02);
		
		p.setBorder(BorderFactory.createEmptyBorder(0,0,4,0));
		getContentPane().add(p, BorderLayout.SOUTH);
		
		// Final configuration -------------------------------------------------
		
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		pack();

		setModal(true);
		setResizable(false);
		setLocationByPlatform(true);
	}
	
	public void defNodes(Object[] nodes) {
		Object[] values = new Object[nodes.length + 1];
		values[0] = "All";
		for (int i = 0; i < nodes.length; i++)
			values[i + 1] = "Node " + ((Node)nodes[i]).getId();
		cb02.setModel(new DefaultComboBoxModel(values));		
	}
	
	public void defEvents(Object[] events) {
		this.events = events;
		Object[] values = new Object[events.length];
		for (int i = 0; i < events.length; i++)
			values[i] = ((Event)events[i]).getName();
		cb03.setModel(new DefaultComboBoxModel(values));		
	}
	
	private void chooseControls() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (rb05.isSelected()) {
					s02.setEnabled(false);
					ckb01.setEnabled(false);
					ckb01.setSelected(false);
					s03.setEnabled(false);
					l01.setEnabled(false);
					cb03.setEnabled(false);
				}
				if (rb06.isSelected()) {
					s02.setEnabled(true);
					ckb01.setEnabled(true);
					s03.setEnabled(false);
					l01.setEnabled(false);
					cb03.setEnabled(false);
				}
				if (rb07.isSelected()) {
					s02.setEnabled(false);
					ckb01.setEnabled(false);
					ckb01.setSelected(false);
					s03.setEnabled(false);
					l01.setEnabled(false);
					cb03.setEnabled(true);			
				}
				s03.setEnabled(ckb01.isSelected());
				l01.setEnabled(ckb01.isSelected());
			}
		});
	}
	
	/***************************************************************************
	 * Show dialogue
	 **************************************************************************/
	private void Show() {
		chooseControls();
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2,
				(dim.height - getSize().height) / 2);
		setVisible(true);
	}
	
	public void showAdd() {
		
		if (cb01.getItemCount() == 0) {
			progress.setVisible(true);
			Vector<Node> nodes = networkService.getNodesList();
			defNodes(nodes.toArray());
			progress.setVisible(false);
		}
		
		if (cb03.getItemCount() == 0) {
			progress.setVisible(true);
			Vector<Event> events = networkService.getEventsList(0);
			defEvents(events.toArray());
			progress.setVisible(false);
		}
		
		tp01.setSelectedIndex(0);
		rb01.setSelected(true);
		cb01.setSelectedIndex(0);
		s01.setValue(10);
		rb03.setSelected(true);
		cb02.setSelectedIndex(0);
		rb05.setSelected(true);
		s02.setValue(new Date());
		s03.setValue(10);
		if (cb03.getModel().getSize() > 0)
			cb03.setSelectedIndex(0);
		setTitle("Add Task");
		b01.setEnabled(true);
		Show();
	}
	
	public void showChange(int id) {
		progress.setVisible(true);
		Task task = networkService.getTaskByID(id);
		progress.setVisible(false);
		
		if (cb01.getItemCount() == 0) {
			progress.setVisible(true);
			Vector<Node> nodes = networkService.getNodesList();
			defNodes(nodes.toArray());
			progress.setVisible(false);
		}
		
		if (cb03.getItemCount() == 0) {
			progress.setVisible(true);
			Vector<Event> event = networkService.getEventsList(0);
			defEvents(event.toArray());
			progress.setVisible(false);
		}
		
		if ((task.getType() == Constants.TYPE_ACTUATOR_START) ||
				(task.getType() == Constants.TYPE_ACTUATOR_STOP)) {
			tp01.setSelectedIndex(0);
			if (task.getValue() == Constants.ACTUATOR_BUZZER)
				cb01.setSelectedIndex(0);
			if (task.getValue() == Constants.ACTUATOR_LED_YELLOW)
				cb01.setSelectedIndex(1);
			if (task.getValue() == Constants.ACTUATOR_LED_RED)
				cb01.setSelectedIndex(3);
			if (task.getValue() == Constants.ACTUATOR_LED_GREEN)
				cb01.setSelectedIndex(4);
			s01.setValue(10);
			if (task.getType() == Constants.TYPE_ACTUATOR_START)
				rb01.setSelected(true);
			else rb02.setSelected(true);
		}
		
		if (task.getType() == Constants.TYPE_CHANGE_DATA_RATE) {
			tp01.setSelectedIndex(1);
			cb01.setSelectedIndex(0);
			s01.setValue(task.getValue());
			rb03.setSelected(true);
		}
		
		if ((task.getType() == Constants.TYPE_SLEEP) ||
				(task.getType() == Constants.TYPE_WAKEUP)) {
			tp01.setSelectedIndex(2);
			cb01.setSelectedIndex(0);
			s01.setValue(10);
			if (task.getType() == Constants.TYPE_SLEEP)
				rb03.setSelected(true);
			else rb04.setSelected(true);
		}
		
		if (task.getTargetNodeID() == 0)
			cb02.setSelectedIndex(0);
		for (int i = 1; i < cb02.getModel().getSize(); i++)
			if (Integer.parseInt(cb02.getModel().getElementAt(i).
					toString().substring(5)) == task.getTargetNodeID())
				cb02.setSelectedIndex(i);
		
		if (task.getWaitEventID() == 0) {
			rb06.setSelected(true);
			s02.setEnabled(true);
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				s02.setValue(format.parse(task.getExecutionDateTime()));
			} catch (ParseException e) {
				System.out.println("Problem parsing: " + task.getExecutionDateTime());
				s02.setValue(new Date());
			}
			if (task.getMinsToRepeat() > 0) {
				ckb01.setSelected(true);
				s03.setValue(task.getMinsToRepeat());
			} else {
				ckb01.setSelected(false);
			}
		} else {
			rb07.setSelected(true);
			String nes = "";
			for (int i = 0; i < events.length; i++)
				if (((Event)events[i]).getId() == task.getWaitEventID())
					nes = ((Event)events[i]).getName();
			for (int i = 0; i < cb03.getModel().getSize(); i++)
				if (cb03.getModel().getElementAt(i).
						toString().compareTo(nes) == 0)
					cb03.setSelectedIndex(i);
		}
		
		this.id = id;
		setTitle("Change Task");
		b01.setEnabled(true);
		Show();
	}
	
	/***************************************************************************
	 * Class that implements the events of the dialogue.
	 * 
	 * @author		Edgardo Avilés López
	 * @version	0.1, 07/27/2006
	 **************************************************************************/
	private class DialogueMaintenanceShares implements ActionListener {

		JDialog f;
		
		public DialogueMaintenanceShares(JDialog f) {
			this.f = f;
		}
		
		/***********************************************************************
		 * System exit.
		 **********************************************************************/
		private void cancel() {
			setVisible(false);
		}
		
		/***********************************************************************
		 * Controls event creation / selection of network sensors.
		 **********************************************************************/
		private void accept() {
			new Thread() {
				public void run() {
					progress.setVisible(true);
					b01.setEnabled(false);
			
					int type = 0;
					int value = 0;
					int destiny = 0;
					String time = "";
					int repeat = 0;
					int event = 0;
					
					DateFormat format = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					time = format.format(new Date());
					
					if (tp01.getSelectedIndex() == 0) {
						if (rb01.isSelected())
							type = Constants.TYPE_ACTUATOR_START;
						if (rb02.isSelected())
							type = Constants.TYPE_ACTUATOR_STOP;
						if (cb01.getSelectedIndex() == 0)
							value = Constants.ACTUATOR_BUZZER;
						if (cb01.getSelectedIndex() == 1)
							value = Constants.ACTUATOR_LED_YELLOW;
						if (cb01.getSelectedIndex() == 2)
							value = Constants.ACTUATOR_LED_BLUE;
						if (cb01.getSelectedIndex() == 3)
							value = Constants.ACTUATOR_LED_RED;
						if (cb01.getSelectedIndex() == 4)
							value = Constants.ACTUATOR_LED_GREEN;
					}
					if (tp01.getSelectedIndex() == 1) {
						type = Constants.TYPE_CHANGE_DATA_RATE;
						value = Integer.parseInt(s01.getValue().toString());
					}
					if (tp01.getSelectedIndex() == 2) {
						if (rb03.isSelected())
							type = Constants.TYPE_SLEEP;
						if (rb04.isSelected())
							type = Constants.TYPE_WAKEUP;
					}
					
					if (cb02.getSelectedItem().toString().substring(0, 3).
							compareTo("Nod") == 0)
						destiny = Integer.parseInt(
								cb02.getSelectedItem().toString().substring(5));
					
					if (s02.isEnabled())
						time = format.format((Date)s02.getValue());
					
					if (s03.isEnabled())
						repeat = Integer.parseInt(s03.getValue().toString());
					
					if (cb03.isEnabled())
						if (cb03.getSelectedItem() != null) {
							String nEv = cb03.getSelectedItem().toString();
							for (int i = 0; i < events.length; i++)
								if (((Event)events[i]).getName().
										compareTo(nEv) == 0)
									event = ((Event)events[i]).getId();
						}
					
					boolean res = false;
					if (getTitle().compareTo("Add Task") == 0)
						res = networkService.addTask(
								type, value, destiny, time, repeat, event);
					if (getTitle().compareTo("Change Task") == 0)
						res = networkService.updateTask(
								id, type, value, destiny, time,
								repeat, event);

					if (!res) {
						JOptionPane.showMessageDialog(
								f, "There is an error in the definition " +
								"of the task.",
								"Problem", JOptionPane.WARNING_MESSAGE);
						b01.setEnabled(true);
						progress.setVisible(false);
						return;
					}

					progress.setVisible(false);
					setVisible(false);
				}
			}.start();
		}

		public void actionPerformed(ActionEvent evt) {
			String cmd = evt.getActionCommand();
			if (cmd.compareTo("Cancel") == 0) cancel();
			if (cmd.compareTo("Accept") == 0) accept();
			chooseControls();
		}
		
	}
	
}
