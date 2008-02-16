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
import java.util.*;
import javax.swing.*;

import net.tinyos.tinysoa.common.*;
import net.tinyos.tinysoa.server.*;
import net.tinyos.tinysoa.util.lists.*;
import net.tinyos.tinysoa.visor.*;

/*******************************************************************************
 * Class that implements a dialog for sensor network selection.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/25/2006
 ******************************************************************************/
public class NetSelectDialog extends JDialog {
	private static final long serialVersionUID = -522846215043252060L;

	private JList netList;
	private final InterfaceEvents interfaceEvents;
	
	/***************************************************************************
	 * Constructor class.
	 * 
	 * @param parent	Parent window
	 * @param icon	    Icon for a network
	 * @param event	    Event controller
	 **************************************************************************/
	public NetSelectDialog(JFrame parent, ImageIcon icon,
			InterfaceEvents event) {
		super(parent, "Select network", false);
		this.interfaceEvents = event;
		
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
		
		JLabel label = new JLabel("Sensors networks available:");
		label.setBorder(BorderFactory.createEmptyBorder(0,0,4,0));
		
		panel.add(label, BorderLayout.NORTH);
		
		netList = new JList();
		netList.setCellRenderer(new NetworksListCellRenderer(icon));
		JScrollPane scrollLista = new JScrollPane(netList);
		scrollLista.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollLista.setPreferredSize(new Dimension(320,250));
		panel.add(scrollLista, BorderLayout.CENTER);
		
		JPanel panel2 = new JPanel();
		panel2.setBorder(BorderFactory.createEmptyBorder(4,0,0,0));
		panel2.setLayout(new FlowLayout(FlowLayout.CENTER));

		ActionListener action = new ActionListener(){
			private static final long serialVersionUID =
				-4115615620227275613L;
			public void actionPerformed(ActionEvent ev) {
				if (ev.getActionCommand().compareTo("Accept") == 0) {
					if (netList.getSelectedValue() == null) {
						JOptionPane.showMessageDialog(
							null, "You must select a network.",
							"Select a network", JOptionPane.WARNING_MESSAGE);
						return;
					}
					setVisible(false);
					String wsdl = ((Network)netList.
							getSelectedValue()).getWsdl(); 
					interfaceEvents.createNetworkService(
							wsdl.substring(0, wsdl.length() - 5));
				}
				if (ev.getActionCommand().compareTo("Cancel") == 0) {
					setVisible(false);
				}
			}};
		
		JButton button = new JButton("Accept");
		button.addActionListener(action);
		panel2.add(button);
		
		button = new JButton("Cancel");
		button.addActionListener(action);
		panel2.add(button);

		panel.add(panel2, BorderLayout.SOUTH);
		
		pack();
		
		setModal(true);
		setResizable(false);
	}
	
	/***************************************************************************
	 * Get the list of networks available on the server.
	 * 
	 * @param service	Information service to use
	 **************************************************************************/
	private void getAvailableNetworks(InfoServ service) {
		Vector<Network> networks = service.getNetworksList();
		netList.setListData(networks.toArray());
	}
	
	/***************************************************************************
	 * Shows the network selection dialog.
	 * 
	 * @param service		Information service to use
	 * @param progressBar	Progress bar to update
	 **************************************************************************/
	public void show(InfoServ service, JProgressBar progressBar) {
		if (service == null) {
			JOptionPane.showMessageDialog(
				null, "You are not connected to a services provider yet.\n " +
				"Please connect to a service provider and try again.",
				"Connection problem", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		getAvailableNetworks(service);
		if (netList.getModel().getSize() == 0) {
			JOptionPane.showMessageDialog(null,
					"No sensor networks available on the server.",
					"No networks available",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		progressBar.setVisible(false);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2,
				(dim.height - getSize().height) / 2);
		setVisible(true);
	}
	
}
