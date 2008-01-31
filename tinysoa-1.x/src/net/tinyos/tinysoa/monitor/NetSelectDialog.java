/*
 * "Copyright (c) 2005-2006 The Regents of the Centro de Investigación y de
 * Educación Superior de la ciudad de Ensenada, Baja California (CICESE).
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice, the following
 * two paragraphs and the author appear in all copies of this software.
 * 
 * IN NO EVENT SHALL CICESE BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
 * SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OF THIS
 * SOFTWARE AND ITS DOCUMENTATION, EVEN IF CICESE HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * CICESE SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND CICESE
 * HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS."
 * 
 ******************************************************************************/

package net.tinyos.tinysoa.monitor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import net.tinyos.tinysoa.common.*;
import net.tinyos.tinysoa.server.*;
import net.tinyos.tinysoa.util.*;

/*******************************************************************************
 * Class that implement the dialog for network selection .
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
	 * @param window	Window father of dialogue
	 * @param icon	    Icon of a network
	 * @param event	    Controller of events to call when selecting
	 **************************************************************************/
	public NetSelectDialog(JFrame window, ImageIcon icon,
			InterfaceEvents event) {
		super(window, "Select network", false);
		this.interfaceEvents = event;
		
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
		
		JLabel label = new JLabel("Networks of sensors available:");
		label.setBorder(BorderFactory.createEmptyBorder(0,0,4,0));
		
		panel.add(label, BorderLayout.NORTH);
		
		netList = new JList();
		netList.setCellRenderer(new MonitorListCellRenderer(icon));
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
	 * @param service	Service of information to use
	 **************************************************************************/
	private void getAvailableNetworks(InfoServ service) {
		Vector<Network> networks = service.getNetworksList();
		netList.setListData(networks.toArray());
	}
	
	/***************************************************************************
	 * Sample dialogue selection network.
	 * 
	 * @param service		Information service to use
	 * @param progressBar	Progress Bar show
	 **************************************************************************/
	public void show(InfoServ service, JProgressBar progressBar) {
		if (service == null) {
			JOptionPane.showMessageDialog(
					null, "You are not connected to a services provider yet.\nPlease " +
							"connect to a service provider and try again.",
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
