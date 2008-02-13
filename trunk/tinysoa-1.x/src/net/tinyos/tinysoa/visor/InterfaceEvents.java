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

package net.tinyos.tinysoa.visor;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import net.tinyos.tinysoa.common.*;
import net.tinyos.tinysoa.common.Event;
import net.tinyos.tinysoa.server.*;
import net.tinyos.tinysoa.util.*;

import org.codehaus.xfire.client.*;
import org.codehaus.xfire.service.*;
import org.codehaus.xfire.service.binding.*;

/*******************************************************************************
 * Handles and manages the monitor's dynamic behavior.
 * Included are event handling, component information refreshing and communication
 * with the server services.
 * 
 * @author		Edgardo Avilés López
 * @version	0.5, 07/28/2006
 ******************************************************************************/
public class InterfaceEvents
	implements ActionListener, ChangeListener, MouseListener {

	private String configurationArchive;
	private String serverUrl;
	private InfoServ infoServ;
	private NetServ netServ;
	private NetSelectDialog netSelectionDialog;
	private JTree tree;
	private JSlider slider;
	private JLabel labelSlider;
	@SuppressWarnings("unused")
	private ImageIcon netIcon, normalStateIcon, specialStateIcon,
			problemStateIcon, playIcon, pauseIcon,
			play2Icon, pause2Icon;
	private long minTime, maxTime;
	private Timer rewindTimer, forwardTimer, playTimer,
			refreshLive;
	private String netServUrl;
	private JCheckBox refreshCheck;
	private JButton dragBarButton, forwardBarButton,
			playBarButton, refreshBarButton, exportGraphButton,
			exportTopologyButton, importBackgroundButton;
	private JMenuItem startMenu, playMenu, finalMenu, refreshMenu;
	private JProgressBar progressBar;
	private MonitorTable dataTable, eventTable, maintenanceTable;
	private DefaultTableModel dataModel, eventModel, maintenanceModel;
	private boolean refreshBusy = false;
	private Date time;
	private JTabbedPane panelTabs;
	private Plotter grapher;
	private TopologyPlotter topologyGrapher;
	private JComboBox comboParsGraph, comboParsTopology;
	private static Point[] posTopNodes;
	private JFrame mainWindow;
	private EventDialog eventDialog;
	private MaintenanceDialog eventMaintenance;
	
	/***************************************************************************
	 * Class constructor
	 * 
	 * @param configurationArchive	Configuration values archive
	 * @param window				Parent window
	 * @param netIcon				Network icon
	 * @param normalStateIcon		Normal state icon
	 * @param specialStateIcon		Special state icon
	 * @param problemStateIcon		Problem state icon
	 **************************************************************************/
	public InterfaceEvents(String configurationArchive, JFrame window,
			ImageIcon netIcon, ImageIcon normalStateIcon,
			ImageIcon specialStateIcon, ImageIcon problemStateIcon) {
		this.configurationArchive = configurationArchive;
		this.mainWindow = window;
		this.netIcon = netIcon;
		this.normalStateIcon = normalStateIcon;
		this.specialStateIcon = specialStateIcon;
		this.problemStateIcon = problemStateIcon;
		netSelectionDialog = new NetSelectDialog(window, netIcon, this);
		loadConfiguration();
		
		rewindTimer = new Timer(250, new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				int newValue = slider.getValue() - 50;
				if (newValue < 0) newValue = 0;
				slider.setValue(newValue);
				System.out.println("act rewindTimer");
				refresh();
			}});
		forwardTimer = new Timer(250, new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				int newValue = slider.getValue() + 50;
				if (newValue > 10000) newValue = 10000;
				slider.setValue(newValue);
				System.out.println("act forwardTimer");
				refresh();
			}});
		playTimer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				int newValue = slider.getValue() + 25;
				if (newValue > 10000) newValue = 10000;
				slider.setValue(newValue);
				System.out.println("act playTimer");
				refresh();
				if (newValue == 10000)
					playPause();
			}});
		refreshLive = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				refreshEverything();				
			}});
		rewindTimer.setInitialDelay(0);
		forwardTimer.setInitialDelay(0);
		playTimer.setInitialDelay(0);
	}
	
	//--------------------------------------------------------------------------
	//
	//	Components to be manipulated by events
	//
	//==========================================================================
	
	/***************************************************************************
	 * Sets the node tree
	 * 
	 * @param tree	Node tree
	 **************************************************************************/
	public void setTree(JTree tree) {
		this.tree = tree;
	}
	
	/***************************************************************************
	 * Sets the time selection slider and its label
	 * 
	 * @param slider		Selection slider
	 * @param labelSlider	Slider label
	 **************************************************************************/
	public void setSlider(JSlider slider, JLabel labelSlider) {
		this.slider = slider;
		this.labelSlider = labelSlider;
	}

	/***************************************************************************
	 * Sets the refresh checkbox
	 * 
	 * @param refresh	Refresh checkbox
	 **************************************************************************/
	public void setRefreshCheckbox(JCheckBox refresh) {
		this.refreshCheck = refresh;
	}
	
	/***************************************************************************
	 * Set play controls
	 * 
	 * @param dragBarButton	Button to go Back
	 * @param playBarButton	Button to play
	 * @param forwardBarButton	Button to go forward
	 * @param playIcon	Play icon
	 * @param pauseIcon			Pause icon
	 **************************************************************************/
	public void setPlayControls(JButton dragBarButton,
			JButton playBarButton, JButton forwardBarButton,
			ImageIcon playIcon, ImageIcon pauseIcon) {
		this.dragBarButton = dragBarButton;
		this.playBarButton = playBarButton;
		this.forwardBarButton = forwardBarButton;
		this.playIcon = playIcon;
		this.pauseIcon = pauseIcon;
	}
	
	/***************************************************************************
	 * Set the play controls for the toolbar
	 * 
	 * @param startMenu			Button to go back to start
	 * @param playMenu		Button to play
	 * @param finalMenu			Button to go to the end
	 * @param play2Icon		Play icon
	 * @param pause2Icon			Pause icon
	 **************************************************************************/
	public void setPlayControls2(JMenuItem startMenu,
			JMenuItem playMenu, JMenuItem finalMenu,
			ImageIcon play2Icon, ImageIcon pause2Icon) {
		this.startMenu = startMenu;
		this.playMenu = playMenu;
		this.finalMenu = finalMenu;
		this.play2Icon = play2Icon;
		this.pause2Icon = pause2Icon;
	}
	
	/***************************************************************************
	 * Sets the refresh everything button
	 * 
	 * @param refreshBarButton	Button to refresh everything
	 **************************************************************************/
	public void setRefreshEverythingButton(JButton refreshBarButton) {
		this.refreshBarButton = refreshBarButton;
	}
	
	/***************************************************************************
	 * Sets the refresh everything toolbar button
	 * 
	 * @param refreshMenu	Button to refresh everything
	 **************************************************************************/
	public void setRefreshEverythingMenuButton(JMenuItem refreshMenu) {
		this.refreshMenu = refreshMenu;
	}
	
	/***************************************************************************
	 * Sets the progress bar
	 * 
	 * @param progressBar	Progress Bar
	 **************************************************************************/
	public void setProgressBar(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}
	
	/***************************************************************************
	 * Sets the tab panel
	 * 
	 * @param panelTabs	Tab panel
	 **************************************************************************/
	public void setTabPanel(JTabbedPane panelTabs) {
		this.panelTabs = panelTabs;
	}
	
	/***************************************************************************
	 * Sets the data table for using inside the data tab
	 * @param table
	 * @param model
	 **************************************************************************/
	public void setDataTable(MonitorTable table, DefaultTableModel model) {
		dataTable = table;
		dataModel = model;
	}

	/***************************************************************************
	 * Sets the grapher for using inside the graph tab
	 * 
	 * @param grapher	Grapher to use
	 **************************************************************************/
	public void setGrapher(Plotter grapher) {
		this.grapher = grapher;
	}
	
	/***************************************************************************
	 * Sets the combobox used to select the graphed parameter
	 * 
	 * @param comboParsGraph	ComboBox with the parameter list
	 **************************************************************************/
	public void setComboParsGraf(JComboBox comboParsGraf) {
		this.comboParsGraph = comboParsGraf;
	}
	
	/***************************************************************************
	 * Sets a button to export the actual state of the grapher icon
	 * 
	 * @param exportGraphButton	Button for exporting an image
	 **************************************************************************/
	public void setExportGraphButton(JButton exportGraphButton) {
		this.exportGraphButton = exportGraphButton;
	}
	
	/***************************************************************************
	 * Sets the topology grapher to use in the topology tab
	 * 
	 * @param topologyGrapher	Topology Plotter to use
	 **************************************************************************/
	public void setGraphTopology(TopologyPlotter graphTopology) {
		this.topologyGrapher = graphTopology;
	}
	
	/***************************************************************************
	 * Sets the ComboBox used to select the graphed topology parameter
	 * 
	 * @param comboParsTopology	Combobox with the parameters
	 **************************************************************************/
	public void setTopologyComboParams(JComboBox topologyComboParams) {
		this.comboParsTopology = topologyComboParams;
	}

	/***************************************************************************
	 * Sets the import background picture button for the topology grapher
	 * 
	 * @param importBackgroundButton	Button to import image
	 **************************************************************************/
	public void setImportBackgroundButton(JButton importBackgroundButton) {
		this.importBackgroundButton = importBackgroundButton;
	}

	/***************************************************************************
	 * Sets the export image button for the current state of the topology grapher
	 * 
	 * @param exportTopologyButton	Export image button
	 **************************************************************************/
	public void setExportTopologyButton(JButton exportTopologyButton) {
		this.exportTopologyButton = exportTopologyButton;
	}
	
	/***************************************************************************
	 * Sets the event table used by the events tab
	 * 
	 * @param table
	 * @param model
	 **************************************************************************/
	public void setEventTable(MonitorTable table, DefaultTableModel model) {
		eventTable = table;
		eventModel = model;
		
		eventModel = new DefaultTableModel();
		TableSorter sorter = new TableSorter(eventModel);
		eventTable.setModel(sorter);
		sorter.setTableHeader(eventTable.getTableHeader());
		
		eventModel.addColumn("ID");
		eventModel.addColumn("Name");
		eventModel.addColumn("Criteria");
		eventModel.addColumn("Done");
		eventModel.addColumn("NID");
		eventModel.addColumn("Detected in");
		
		eventTable.getTableHeader().setBackground(new Color(0xe1e6ec));
		eventTable.getTableHeader().setReorderingAllowed(false);
		
		eventTable.getColumnModel().getColumn(0).setCellRenderer(
				new MonitorCellRenderer(SwingConstants.CENTER, true));
		for (int i = 1; i < eventTable.getColumnCount(); i++)
			eventTable.getColumnModel().getColumn(i).setCellRenderer(
					new MonitorCellRenderer(SwingConstants.CENTER, false));
		eventTable.getColumnModel().getColumn(3).setCellRenderer(
				new MonitorCellRenderer(SwingConstants.CENTER, true));
		
		eventTable.getColumnModel().getColumn(0).setPreferredWidth(42);
		eventTable.getColumnModel().getColumn(0).setMinWidth(42);
		eventTable.getColumnModel().getColumn(2).setPreferredWidth(130);
		eventTable.getColumnModel().getColumn(2).setMinWidth(130);
		eventTable.getColumnModel().getColumn(3).setPreferredWidth(42);
		eventTable.getColumnModel().getColumn(3).setMinWidth(42);
		eventTable.getColumnModel().getColumn(4).setPreferredWidth(42);
		eventTable.getColumnModel().getColumn(4).setMinWidth(42);
		eventTable.getColumnModel().getColumn(5).setPreferredWidth(130);
		eventTable.getColumnModel().getColumn(5).setMinWidth(130);
		
		((TableSorter)eventTable.getModel()).setSortingStatus(
				0, TableSorter.DESCENDING);
	}
	
	/***************************************************************************
	 * Sets the maintenance table used by the maintenance tab
	 * 
	 * @param table
	 * @param model
	 **************************************************************************/
	public void setMaintenanceTable(MonitorTable table, DefaultTableModel model) {
		maintenanceTable = table;
		maintenanceModel = model;
		
		maintenanceModel = new DefaultTableModel();
		TableSorter sorter = new TableSorter(maintenanceModel);
		maintenanceTable.setModel(sorter);
		sorter.setTableHeader(maintenanceTable.getTableHeader());
		
		maintenanceModel.addColumn("ID");
		maintenanceModel.addColumn("Action");
		maintenanceModel.addColumn("Value");
		maintenanceModel.addColumn("NID");
		maintenanceModel.addColumn("Execute in");
		maintenanceModel.addColumn("Executed in");
		maintenanceModel.addColumn("Done");
		maintenanceModel.addColumn("Repeat every");
		
		maintenanceTable.getTableHeader().setBackground(new Color(0xe1e6ec));
		maintenanceTable.getTableHeader().setReorderingAllowed(false);
		
		maintenanceTable.getColumnModel().getColumn(0).setCellRenderer(
				new MonitorCellRenderer(SwingConstants.CENTER, true));
		for (int i = 1; i < maintenanceTable.getColumnCount(); i++)
			maintenanceTable.getColumnModel().getColumn(i).setCellRenderer(
					new MonitorCellRenderer(SwingConstants.CENTER, false));
		maintenanceTable.getColumnModel().getColumn(6).setCellRenderer(
				new MonitorCellRenderer(SwingConstants.CENTER, true));
		
		int[] widths = {42, 0, 0, 42, 130, 130, 42, 84};
		
		for (int i = 0; i < widths.length; i++)
			if (widths[i] != 0) {
				maintenanceTable.getColumnModel().getColumn(i).
						setPreferredWidth(widths[i]);
				maintenanceTable.getColumnModel().getColumn(i).
						setMinWidth(widths[i]);
			}
	
		((TableSorter)maintenanceTable.getModel()).setSortingStatus(
				4, TableSorter.DESCENDING);
	}
	
	//--------------------------------------------------------------------------
	//	Methods to make a connection to the services provider
	//
	//
	//==========================================================================
	
	/***************************************************************************
	 * Creates the connection to the service provider
	 **************************************************************************/
	public void connectServer() {
		String url = (String)JOptionPane.showInputDialog(
				null, "URL of TinySOA services provider:",
				"Connect to server", JOptionPane.QUESTION_MESSAGE,
				null, null, serverUrl.trim());
		if (url == null) return;
		serverUrl = url;
		saveConfiguration();
		
		new Thread(){
			public void run() {
				try {
					progressBar.setVisible(true);
					Service serviceModel = new ObjectServiceFactory().
							create(InfoServ.class);
					infoServ = (InfoServ)new XFireProxyFactory().
							create(serviceModel, serverUrl + "/InfoServ");	
					netSelectionDialog.show(infoServ,
							progressBar);
				} catch (Exception e) {
					progressBar.setVisible(false);
					infoServ = null;
					JOptionPane.showMessageDialog(null,
							"Impossible to stablish communication with the " +
							"server.\nPlease, verify the URL and try again.", 
							"Connection Problem", JOptionPane.ERROR_MESSAGE);
					connectServer();
					deactivateControls();
				}
			}
		}.start();
	}

	/***************************************************************************
	 * Creates and prepares the network client service
	 * 
	 * @param url	Service provider URL
	 **************************************************************************/
	public void createNetworkService(String url) {
		this.netServUrl = url;
		new Thread(){
			public void run() {
				try {
					refreshBusy = true;
					progressBar.setVisible(true);
					Service serviceModel = new ObjectServiceFactory().
							create(NetServ.class);
					netServ = (NetServ)new XFireProxyFactory().
							create(serviceModel, netServUrl);

					eventDialog = new EventDialog(
							mainWindow, netServ, progressBar);
					eventMaintenance = new MaintenanceDialog(
							mainWindow, netServ, progressBar);
					
					processNodes();
					processParameters();
									
					defineTimes();
					activateControls();
					progressBar.setVisible(false);
					refreshBusy = false;
					System.out.println("act createNetworkService");
					refresh();
				} catch (Exception e) {
					progressBar.setVisible(false);
					netServ = null;
					JOptionPane.showMessageDialog(null,
							"There has been an error communicating with the network service ", 
							"Communication Problem",
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
					deactivateControls();
				}
			}}.start();
	}

	//--------------------------------------------------------------------------
	//
	//	Functions that refresh the UI with new data
	//
	//==========================================================================
	
	/***************************************************************************
	 * Refresh the data based on the slider value
	 **************************************************************************/
	public void refresh() {
		
		new Thread() {
			public void run() {
				if (netServ == null) return;
				if (refreshBusy) {
					System.out.println("refreshing denied");
					return;
				}
				refreshBusy = true;
				blockRefresh();
				
				refreshTime();
				if (panelTabs.getSelectedIndex() == 0) refreshDataTable();
				if (panelTabs.getSelectedIndex() == 1) refreshGrapher();
				if (panelTabs.getSelectedIndex() == 2) refreshTopology();
				if (panelTabs.getSelectedIndex() == 3) refreshEventTable();
				if (panelTabs.getSelectedIndex() == 4) refreshMaintenanceTable();
				
				unblockRefresh();
				refreshBusy = false;
			}
		}.start();
	}

	private void refreshEverything() {
		defineTimes();
		System.out.println("act refreshEverything");
		refresh();
	}

	/***************************************************************************
	 * Refresh the data table
	 **************************************************************************/
	private void refreshDataTable() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					DateFormat formato4 = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Vector<Reading> readings = netServ.
							getReadingsUntil(formato4.format(time),
									((NetTreeNode)tree.getModel().getRoot()).
									getChildCount() * 
									dataModel.getColumnCount() * 3);
	
					String[][] empty = new String[dataModel.getRowCount()][];
					for (int i = 0; i < empty.length; i++) {
						empty[i] = new String[dataModel.getColumnCount()];
						for (int j = 0; j < empty[i].length; j++)
							empty[i][j] = "";
					}
					
					String maxTime = "";
					
					Reading l;
					for (int i = 0; i < readings.size(); i++) {
						l = (Reading)readings.get(i);
						int row = findRow(l.getNid());
						int column = dataModel.findColumn(l.getParameter());
						if (row >= 0) {
							if (empty[row][column].compareTo("") == 0) {
								dataModel.setValueAt(
										formatReading(l.getValue(),
										l.getParameter()), row, column);
								empty[row][column] = formatReading(
										l.getValue(), l.getParameter());
							}
							if (empty[row][1].compareTo("") == 0) {
								dataModel.setValueAt(
										l.getDateTime().substring(0,
										l.getDateTime().length() - 2),
										row, 1);
								empty[row][1] =l.getDateTime().substring(0,
										l.getDateTime().length() - 2);
							}
							if (l.getDateTime().compareTo(maxTime) > 0)
								maxTime = l.getDateTime();
						}
					}
					
					for (int i = 0; i < dataModel.getRowCount(); i++)
						if (((String)dataModel.getValueAt(i, 1)) != null)
							if (((String)dataModel.getValueAt(i, 1)).
									compareTo(maxTime) > 0)
								for (int j = 1;
									j < dataModel.getColumnCount(); j++)
									dataModel.setValueAt("", i, j);
					
				}
			});
		} catch (Exception e) { Errors.error(e, "refreshDataTable"); }
	}

	/***************************************************************************
	 * Refresh data table nodes
	 **************************************************************************/
	@SuppressWarnings("unchecked")
	private void refreshDataTableNodes() {
		int row; boolean modified = false;
	
		if (tree.getModel().getRoot() == null) return;
		
		Enumeration e = ((NetTreeNode)tree.getModel().getRoot()).children();
		while (e.hasMoreElements()) {
			NetTreeNode el = (NetTreeNode)e.nextElement();
			String id = el.toString().substring(5);
			boolean isSelected = el.isSelected();
	
			row = findRow(Integer.parseInt(id));
			if ((row == -1) && (isSelected)) {
				modified = true;
				dataModel.addRow(new Object[]{id});
			}
			if ((row >= 0) && (!isSelected)) {
				modified = true;
				dataModel.removeRow(row);
			}
			dataTable.repaint();
		}
	
		if (modified) {
			System.out.println("act refreshDataTableNodes");
			refresh();					
		}
	}

	/***************************************************************************
	 * Refresh the graph
	 **************************************************************************/
	private void refreshGrapher() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@SuppressWarnings("unchecked")
				public void run() {
					if (netServ == null) return;
					if (comboParsGraph.getSelectedItem() == null) return;
					
					String parameter = comboParsGraph.
							getSelectedItem().toString();
					
					DateFormat format4 = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Vector<Reading> readings = netServ.getReadings(
							format4.format(new Date(time.getTime() -
									grapher.obtDif() - 1000)),
							format4.format(time), parameter, 0);
	
					Vector<Vector> data = new Vector<Vector>();
					Vector<Reading> dataNodes = new Vector<Reading>();
					
					int nid = -1;
					if (readings.size() > 0)
						nid = readings.get(0).getNid();
							
					int max = 0;
					Enumeration e = ((NetTreeNode)tree.
							getModel().getRoot()).children();
					while (e.hasMoreElements()) {
						NetTreeNode n = (NetTreeNode)e.nextElement();
						int i = Integer.parseInt(n.toString().substring(5));
						if (i > max) max = i;
					}
					boolean[] nodeSel = new boolean[max + 1];
					e = ((NetTreeNode)tree.
							getModel().getRoot()).children();
					while (e.hasMoreElements()) {
						NetTreeNode n = (NetTreeNode)e.nextElement();
						nodeSel[Integer.parseInt(n.toString().substring(5))] =
							n.isSelected();
					}
					
					e = readings.elements();
					while (e.hasMoreElements()) {
						Reading l = (Reading)e.nextElement();
						if (nodeSel[l.getNid()])
						if (l.getParameter().compareTo(parameter) == 0) {
							if (l.getNid() != nid) {
								data.add(dataNodes);
								
								//System.out.println(datosNodo);
								dataNodes = new Vector<Reading>();
								nid = l.getNid();
							}
							dataNodes.add(l);
						}
					}
					data.add(dataNodes);
					
					grapher.defTime(time.getTime());
					grapher.defData(data);
				}
			});
			
			grapher.repaint();
		} catch (Exception e) { Errors.error(e, "refreshGrapher"); }
	}

	/***************************************************************************
	 * Refresh the topology graph
	 **************************************************************************/
	private void refreshTopology() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@SuppressWarnings("unchecked")
				public void run() {
					DateFormat format4 = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Vector<Reading> readings = netServ.
							getReadingsUntil(format4.format(time),
									((NetTreeNode)tree.getModel().getRoot()).
									getChildCount() * 
									dataModel.getColumnCount() * 3);
					
					topologyGrapher.deleteAllNodes();
					
					int max = 0;
					Enumeration e = ((NetTreeNode)tree.
							getModel().getRoot()).children();
					while (e.hasMoreElements()) {
						NetTreeNode n = (NetTreeNode)e.nextElement();
						int nid = Integer.parseInt(n.toString().substring(5));
						if (nid > max) max = nid;
					}
					boolean[] nodeSel = new boolean[max + 1];
					e = ((NetTreeNode)tree.
							getModel().getRoot()).children();
					while (e.hasMoreElements()) {
						NetTreeNode n = (NetTreeNode)e.nextElement();
						nodeSel[Integer.parseInt(n.toString().substring(5))] =
							n.isSelected();
					}
					
					e = readings.elements();
					String par = comboParsTopology.
							getSelectedItem().toString();
					
					if (par.compareTo("Temp") == 0) {
						topologyGrapher.defEscala(0.0d, 35.0d);
						topologyGrapher.defTypeScale(
								TopologyPlotter.SCALE_HEAT);
					} else if (par.compareTo("Light") == 0) {
						topologyGrapher.defEscala(300.0d, 900.0d);
						topologyGrapher.defTypeScale(
								TopologyPlotter.SCALE_LIGHT);
					} else if (par.compareTo("Volt") == 0) {
						topologyGrapher.defEscala(1.5d, 3.0d);
						topologyGrapher.defTypeScale(
								TopologyPlotter.SCALE_ENERGY);
					} else {
						topologyGrapher.defEscala(300.0d, 1024.0d);
						topologyGrapher.defTypeScale(
								TopologyPlotter.SCALE_HEAT);
					}
					
					
					while (e.hasMoreElements()) {
						Reading l = (Reading)e.nextElement();
						if (par.compareTo(l.getParameter()) == 0)
							if (!topologyGrapher.existNode(l.getNid()))
								if (nodeSel[l.getNid()]) {
									NodeTopologyChart ngt =
										new NodeTopologyChart(l.getNid(),
												Double.parseDouble(
														l.getValue()),
												posTopNodes[l.getNid()].x,
												posTopNodes[l.getNid()].y);
									topologyGrapher.addNode(ngt);
								}
					}
					
					topologyGrapher.repaint();
				}
			});
		} catch (Exception e) { Errors.error(e, "refreshTopology"); }
	}
	
	/***************************************************************************
	 * Refresh the event table
	 **************************************************************************/
	private void refreshEventTable() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@SuppressWarnings("unchecked")
				public void run() {
					Vector<Event> events = netServ.
							getEventsList(0);
					
					String id, name, criteria, done, nid, time;
					
					eventTable.clearTable();
					Enumeration e = events.elements();
					while (e.hasMoreElements()) {
						Event event = (Event)e.nextElement();
						
						id = event.getId() + "";
						name = event.getName();
						criteria = event.getCriteria();
						if (event.getDetected()) done = "Yes";
						else done = "No";
						if (event.getNid() == 0) nid = "-";
						else nid = event.getNid() + "";
						if (event.getDateTime() == null)
							time = "Waiting";
						else if (event.getDateTime().compareTo("") == 0)
							time = "Waiting";
						else time = event.getDateTime();
						
						eventModel.addRow(new Object[]{id, name,
								new CellTableTooltip(criteria),
								done, nid, time});
						
						eventMaintenance.defEvents(events.toArray());
					}
				}
			});
		} catch (Exception e) { Errors.error(e, "refreshEventTable"); }
	}
	
	/***************************************************************************
	 * Refresh maintenance table
	 **************************************************************************/
	private void refreshMaintenanceTable() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@SuppressWarnings("unchecked")
				public void run() {
					Vector<Task> tasks = netServ.
							getTasksList(0);
					
					String id, action, value, nid, time,
						executed, done, repeat;

					maintenanceTable.clearTable();
					Enumeration e = tasks.elements();
					while (e.hasMoreElements()) {
						Task task = (Task)e.nextElement();
						
						id = task.getId() + "";
						
						action = "";
						if (task.getType() ==
							Constants.TYPE_ACTUATOR_START)
							action = "Act. Act.";
						if (task.getType() ==
							Constants.TYPE_ACTUATOR_STOP)
							action = "Des. Act.";
						if (task.getType() ==
							Constants.TYPE_CHANGE_DATA_RATE)
							action = "Chg. Data Rate";
						if (task.getType() ==
							Constants.TYPE_SLEEP)
							action = "Sleep";
						if (task.getType() ==
							Constants.TYPE_WAKEUP)
							action = "Wake Up";
						
						value = "-";
						if (task.getValue() > 0)
							value = task.getValue() + " segs";
						if (task.getValue() ==
							Constants.ACTUATOR_BUZZER)
							value = "Buzz";
						if (task.getValue() ==
							Constants.ACTUATOR_LED_YELLOW)
							value = "LedY";
						if (task.getValue() ==
							Constants.ACTUATOR_LED_RED)
							value = "LedR";
						if (task.getValue() ==
							Constants.ACTUATOR_LED_GREEN)
							value = "LedG";
						
						nid = "-";
						if (task.getTargetNodeID() > 0)
							nid = task.getTargetNodeID() + "";
						
						time = task.getExecutionDateTime();
						time = time.substring(0, time.length() - 2);
						if (task.getWaitEventID() > 0)
							time = "Esperando evento " + task.getWaitEventID();
						
						executed = "-";
						if (task.getLastExecuted() != null)
							if (task.getLastExecuted().compareTo("") != 0)
								executed = task.getLastExecuted();
						
						done = "No";
						if (task.getDone())
							done = "Yes";
						if (task.getMinsToRepeat() > 0)
							done = "-";
						
						repeat = "-";
						if (task.getMinsToRepeat() > 0)
							repeat = task.getMinsToRepeat() + " min";

						maintenanceModel.addRow(new Object[]{
								id, action, value, nid, time,
								executed, done, repeat});
					}
				}
			});
		} catch (Exception e) { Errors.error(e, "refreshmaintenanceTable"); }
	}

	/***************************************************************************
	 * Refresh the slider label and current time
	 **************************************************************************/
	private void refreshTime() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					double timeFactor = slider.getValue() / 10000.0d;
					time = new Date(Math.round(minTime *
							(1.0d - timeFactor) + maxTime *
							(timeFactor)));
					DateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
					DateFormat format3 = new SimpleDateFormat("HH:mm:ss");
					labelSlider.setText("<html><center>" +
							format2.format(time) +
							"<br><b>" + format3.format(time) +
							"</b></center></html>");
				}
			});
		} catch (Exception e) { Errors.error(e, "refreshTime"); }
	}

	/***************************************************************************
	 * Define the sliders time and scale
	 **************************************************************************/
	private void defineTimes() {
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date start = (Date)format.parse(
					netServ.getMinDateTime());
			Object res = netServ.getMaxDateTime();
			System.out.println(res);
			Date fin = (Date)format.parse(res.toString());
			minTime = start.getTime();
			maxTime = fin.getTime();
			slider.setValue(10000);
		} catch (Exception e) { Errors.error(e, "defineTimes"); }
	}
	
	//--------------------------------------------------------------------------
	//	Functions to prepare and configure the GUI components
	//
	//
	//==========================================================================

	/***************************************************************************
	 * Process the network nodes
	 **************************************************************************/
	private void processNodes() {
		Vector<Node> nodes = netServ.getNodesList();
		createTreeNodes(nodes.toArray());
		createTable(nodes.toArray());
		createNodeTopology(nodes.toArray());
		eventMaintenance.defNodes(nodes.toArray());
	}

	/***************************************************************************
	 * Process network parameters
	 **************************************************************************/
	private void processParameters() {
		Vector<Parameter> parameters = netServ.getSensorTypesList();
		comboParsGraph.removeAllItems();
		comboParsTopology.removeAllItems();
		for (int i = 0; i < parameters.size(); i++) {
			comboParsGraph.addItem(parameters.get(i).getName());
			comboParsTopology.addItem(parameters.get(i).getName());
		}
		if (comboParsGraph.getItemCount() > 2) {
			comboParsGraph.setSelectedIndex(
					comboParsGraph.getItemCount() - 2);
			comboParsTopology.setSelectedIndex(
					comboParsTopology.getItemCount() - 2);
		}
	}

	/***************************************************************************
	 * Create the node tree
	 * 
	 * @param nodes	A node array with the node names
	 **************************************************************************/
	private void createTreeNodes(Object[] nodes) {
		NetTreeNode top = new NetTreeNode(
				netServ.getNetName(), netIcon);
		((DefaultTreeModel)tree.getModel()).setRoot(top);	
		NetTreeNode c = null;
		for (int i = 0; i < nodes.length; i++) {
			c = new NetTreeNode("Nodo " + ((Node)nodes[i]).getId(),
					normalStateIcon);
			top.add(c);
		}
		tree.expandRow(0);
	}

	/***************************************************************************
	 * Create the data table
	 * 
	 * @param nodes	A node array with the node ID
	 **************************************************************************/
	private void createTable(Object[] nodes) {
		Vector<Parameter> parameters = netServ.getSensorTypesList();
		Object[] columnParameters = new Object[parameters.size() + 2];
		columnParameters[0] = "ID";
		columnParameters[1] = "Time";
		for (int i = 0; i < parameters.size(); i++)
			columnParameters[i + 2] = parameters.get(i).getName();
		
		dataModel = new DefaultTableModel();
		TableSorter sorter = new TableSorter(dataModel);
		dataTable.setModel(sorter);
		sorter.setTableHeader(dataTable.getTableHeader());
		
		for (int i = 0; i < columnParameters.length; i++)
			dataModel.addColumn(columnParameters[i]);
		
		dataTable.getTableHeader().setBackground(new Color(0xe1e6ec));
		dataTable.getTableHeader().setReorderingAllowed(false);
		
		dataTable.getColumnModel().getColumn(0).setCellRenderer(
				new MonitorCellRenderer(SwingConstants.CENTER, true));
		for (int i = 1; i < dataTable.getColumnCount(); i++)
			dataTable.getColumnModel().getColumn(i).setCellRenderer(
					new MonitorCellRenderer(SwingConstants.CENTER, false));
				
		for (int i = 0; i < nodes.length; i++)
			dataModel.addRow(new Object[]{((Node)nodes[i]).getId()});
		
		dataTable.getColumnModel().getColumn(0).setPreferredWidth(42);
		dataTable.getColumnModel().getColumn(0).setMinWidth(42);
		dataTable.getColumnModel().getColumn(1).setPreferredWidth(130);
		dataTable.getColumnModel().getColumn(1).setMinWidth(130);
		
		((TableSorter)dataTable.getModel()).setSortingStatus(
				0, TableSorter.ASCENDING);
	}

	/***************************************************************************
	 * Create the topology graph nodes
	 * 
	 * @param nodes Array with the nodes ID
	 **************************************************************************/
	private void createNodeTopology(Object[] nodes) {
		int max = 0, ancho, alto, x, y;
		for (int i = 0; i < nodes.length; i++)
			if (((Node)nodes[i]).getId() > max)
				max = ((Node)nodes[i]).getId();
		
		posTopNodes = new Point[max + 1];
		
		for (int i = 0; i < nodes.length; i++) {
			ancho = topologyGrapher.getWidth();
			alto = topologyGrapher.getHeight();
			x = (int)Math.round(Math.random() * ancho);
			y = (int)Math.round(Math.random() * alto);			
			posTopNodes[((Node)nodes[i]).getId()] = new Point(x, y);
		}
		
		topologyGrapher.defPosNodes(posTopNodes);
	}

	//--------------------------------------------------------------------------
	//
	//   Block/Unblock GUI components
	//
	//==========================================================================
	
	/***************************************************************************
	 * Unblock refresh and show progress bar
	 **************************************************************************/
	private void unblockRefresh() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					progressBar.setVisible(false);
				}
			});
		} catch (Exception e) { Errors.error(e, "unblickRefresh"); }
	}

	/***************************************************************************
	 * Blocks refresh and show progress bar
	 **************************************************************************/
	private void blockRefresh() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					progressBar.setVisible(true);
				}
			});
		} catch (Exception e) { Errors.error(e, "blockRefresh"); }
	}

	/***************************************************************************
	 * Activate all controls
	 **************************************************************************/
	public void activateControls() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				slider.setEnabled(true);
				refreshCheck.setEnabled(true);
				dragBarButton.setEnabled(true);
				playBarButton.setEnabled(true);
				forwardBarButton.setEnabled(true);
				startMenu.setEnabled(true);
				playMenu.setEnabled(true);
				finalMenu.setEnabled(true);
				refreshBarButton.setEnabled(true);
				refreshMenu.setEnabled(true);
				panelTabs.setEnabled(true);		
				comboParsGraph.setEnabled(true);
				comboParsTopology.setEnabled(true);
	
				refreshCheck.setSelected(false);
			}
		});
	}

	/***************************************************************************
	 * Deactivate all controls
	 **************************************************************************/
	public void deactivateControls() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				slider.setEnabled(false);
				refreshCheck.setEnabled(false);
				dragBarButton.setEnabled(false);
				playBarButton.setEnabled(false);
				forwardBarButton.setEnabled(false);
				startMenu.setEnabled(false);
				playMenu.setEnabled(false);
				finalMenu.setEnabled(false);
				refreshBarButton.setEnabled(false);
				refreshMenu.setEnabled(false);
				panelTabs.setEnabled(false);
				comboParsGraph.setEnabled(false);
				comboParsTopology.setEnabled(false);
			}
		});
	}

	/***************************************************************************
	 * Activate/Deactivate controls in preparation of live update
	 **************************************************************************/
	private void prepareLiveRefresh() {
		boolean enVivo = refreshCheck.isSelected();
		if (enVivo) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					rewindTimer.stop();
					forwardTimer.stop();
					playTimer.stop();
					dragBarButton.setEnabled(false);
					playBarButton.setEnabled(false);
					forwardBarButton.setEnabled(false);
					exportGraphButton.setEnabled(false);
					exportTopologyButton.setEnabled(false);
					importBackgroundButton.setEnabled(false);
					startMenu.setEnabled(false);
					playMenu.setEnabled(false);
					finalMenu.setEnabled(false);
					slider.setEnabled(false);
					topologyGrapher.setEnabled(false);
					eventTable.setEnabled(false);
					
					refreshLive.restart();
				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					dragBarButton.setEnabled(true);
					playBarButton.setEnabled(true);
					forwardBarButton.setEnabled(true);
					exportGraphButton.setEnabled(true);
					exportTopologyButton.setEnabled(true);
					importBackgroundButton.setEnabled(true);
					startMenu.setEnabled(true);
					playMenu.setEnabled(true);
					finalMenu.setEnabled(true);
					slider.setEnabled(true);
					topologyGrapher.setEnabled(true);
					eventTable.setEnabled(true);
					
					refreshLive.stop();
				}
			});
		}
	}

	/***************************************************************************
	 * Activate/Deactivate controls in preparation of play/pause
	 **************************************************************************/
	private void playPause() {
		if (playTimer.isRunning()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					dragBarButton.setEnabled(true);
					forwardBarButton.setEnabled(true);
					playBarButton.setToolTipText("Play");
					playBarButton.setIcon(playIcon);
					playMenu.setText("Play");
					playMenu.setIcon(play2Icon);
					refreshCheck.setEnabled(true);
					refreshMenu.setEnabled(true);
					refreshBarButton.setEnabled(true);
					exportGraphButton.setEnabled(true);
					exportTopologyButton.setEnabled(true);
					importBackgroundButton.setEnabled(true);
					topologyGrapher.setEnabled(true);
					eventTable.setEnabled(true);
					
					playTimer.stop();
				}
			});
			
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					dragBarButton.setEnabled(false);
					forwardBarButton.setEnabled(false);
					playBarButton.setToolTipText("Pause");
					playBarButton.setIcon(pauseIcon);
					playMenu.setText("Pause");
					playMenu.setIcon(pause2Icon);
					refreshCheck.setEnabled(false);
					refreshMenu.setEnabled(false);
					refreshBarButton.setEnabled(false);
					exportGraphButton.setEnabled(false);
					exportTopologyButton.setEnabled(false);
					importBackgroundButton.setEnabled(false);
					topologyGrapher.setEnabled(false);
					eventTable.setEnabled(false);
					
					playTimer.restart();
				}
			});
		}
	}

	//--------------------------------------------------------------------------
	//
	//   Load system configuration
	//
	//==========================================================================
	
	/***************************************************************************
	 * Load Monitor configuration
	 **************************************************************************/
	private void loadConfiguration() {
		Properties p = new Properties();
		try {
			p.loadFromXML(new FileInputStream(configurationArchive));
			serverUrl = p.getProperty("visor.servidor");
		} catch (Exception e) {
			serverUrl = "http://localhost:8080";
			saveConfiguration();
		}
		
		if (serverUrl == null) {
			serverUrl = "http://localhost:8080";
			saveConfiguration();
		}
	}
	
	/***************************************************************************
	 * Save monitor configuration
	 **************************************************************************/
	private void saveConfiguration() {
		Properties p = new Properties();
		try {
			p.loadFromXML(new FileInputStream(configurationArchive));
			p.setProperty("visor.servidor", serverUrl);
			p.storeToXML(new FileOutputStream(configurationArchive), null);
		} catch (Exception e) {}
	}
	
	//--------------------------------------------------------------------------
	//
	//   Import and Export image functions
	//
	//==========================================================================
	
	/***************************************************************************
	 * Presents a dialog and exports an image with the current grapher state
	 **************************************************************************/
	private void exportGraph() {
		JFileChooser dialog = new JFileChooser();
		
		dialog.addChoosableFileFilter(
				new FilterFile(new String[]{"png"}, "Imagen PNG"));
		dialog.addChoosableFileFilter(
				new FilterFile(new String[]{"bmp"}, "Imagen BMP"));
		//dialogo.addChoosableFileFilter(
		//		new FiltroArchivos(new String[]{"gif"}, "Imagen GIF"));
		dialog.addChoosableFileFilter(
				new FilterFile(new String[]{"jpg", "jpeg", "jpe"},
						"Imagen JPG"));
		dialog.setAcceptAllFileFilterUsed(false);
		
		grapher.defProgress(progressBar);
		
		int res = dialog.showSaveDialog(null);
		if (res == JFileChooser.APPROVE_OPTION) {
			String a = dialog.getSelectedFile().getAbsoluteFile().toString();
			String f = dialog.getFileFilter().getDescription().
					substring(7, 10).toLowerCase();
			if (a.substring(a.length() - 4, a.length() -3).compareTo(".") != 0)
				a += "." + f;
			grapher.guardarImagen(a, f);
		}
		
	}
	
	/***************************************************************************
	 * Presents a dialog and defines the background image of the topology grapher
	 **************************************************************************/
	private void importBackgroundImage() {
		new Thread() {
			public void run() {
				JFileChooser dialog = new JFileChooser();
		
				dialog.addChoosableFileFilter(
						new FilterFile(new String[]{"png"}, "PNG image"));
				dialog.addChoosableFileFilter(
						new FilterFile(new String[]{"bmp"}, "BMP image"));
				//dialogo.addChoosableFileFilter(
				//		new FiltroArchivos(new String[]{"gif"}, "Imagen GIF"));
				dialog.addChoosableFileFilter(
						new FilterFile(new String[]{"jpg", "jpeg", "jpe"},
								"JPG image"));
				dialog.setAcceptAllFileFilterUsed(false);
		
				int res = dialog.showOpenDialog(null);
				if (res == JFileChooser.APPROVE_OPTION) {
					String archive = dialog.getSelectedFile().
							getAbsoluteFile().toString();
			
					Image image = Toolkit.getDefaultToolkit().
							createImage(archive);
					if (image != null) {
						BufferedImage bImage = toBufferedImage(image);
						topologyGrapher.defBackgr(bImage);
						topologyGrapher.repaint();
					}
				}
			}
		}.start();
	}

	/***************************************************************************
	 * Presents a dialog and exports an image with the current topology grapher
	 * state
	 **************************************************************************/
	private void exportTopologyGraph() {
		JFileChooser dialog = new JFileChooser();
		
		dialog.addChoosableFileFilter(
				new FilterFile(new String[]{"png"}, "PNG image"));
		dialog.addChoosableFileFilter(
				new FilterFile(new String[]{"bmp"}, "BMP image"));
		//dialogo.addChoosableFileFilter(
		//		new FiltroArchivos(new String[]{"gif"}, "Imagen GIF"));
		dialog.addChoosableFileFilter(
				new FilterFile(new String[]{"jpg", "jpeg", "jpe"},
						"JPG image"));
		dialog.setAcceptAllFileFilterUsed(false);
		
		grapher.defProgress(progressBar);
		
		int res = dialog.showSaveDialog(null);
		if (res == JFileChooser.APPROVE_OPTION) {
			String a = dialog.getSelectedFile().getAbsoluteFile().toString();
			String f = dialog.getFileFilter().getDescription().
					substring(7, 10).toLowerCase();
			if (a.substring(a.length() - 4, a.length() -3).compareTo(".") != 0)
				a += "." + f;
			topologyGrapher.saveImage(a, f);
		}
		
	}

	/***************************************************************************
	 * Verify if an image is transparent
	 * 
	 * @param image	Image to verify
	 * @return			True if image is transparent
	 **************************************************************************/
	private boolean isTransparent(Image image) {
	    if (image instanceof BufferedImage) {
	        BufferedImage bimage = (BufferedImage)image;
	        return bimage.getColorModel().hasAlpha();
	    }
	
	     PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
	    try {
	        pg.grabPixels();
	    } catch (InterruptedException e) {}
	
	    ColorModel cm = pg.getColorModel();
	    return cm.hasAlpha();
	}
	
	/***************************************************************************
	 * Transform an <code>Image</code> to a <code>BufferedImage</code>
	 * 
	 * @param image	Image to convert
	 * @return			A <code>BufferedImage</code> version of <code>imagen</code>
	 **************************************************************************/
	private BufferedImage toBufferedImage(Image image) {
	    if (image instanceof BufferedImage) {
	        return (BufferedImage)image;
	    }
	
	    image = new ImageIcon(image).getImage();	
	    boolean hasAlpha = isTransparent(image);
	    BufferedImage bimage = null;
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    try {
	        int transparency = Transparency.OPAQUE;
	        if (hasAlpha) {
	            transparency = Transparency.BITMASK;
	        }
	        GraphicsDevice gs = ge.getDefaultScreenDevice();
	        GraphicsConfiguration gc = gs.getDefaultConfiguration();
	        bimage = gc.createCompatibleImage(
	            image.getWidth(null), image.getHeight(null), transparency);
	    } catch (HeadlessException e) {}
	
	    if (bimage == null) {
	        int type = BufferedImage.TYPE_INT_RGB;
	        if (hasAlpha) {
	            type = BufferedImage.TYPE_INT_ARGB;
	        }
	        bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
	    }
	
	    Graphics g = bimage.createGraphics();
	    g.drawImage(image, 0, 0, null);
	    g.dispose();
	
	    return bimage;
	}
	
	//--------------------------------------------------------------------------
	//
	//   Handle mayor events
	//
	//==========================================================================
	
	/***************************************************************************
	 * Prepares and show the add event dialog
	 **************************************************************************/
	private void addEvent() {
		eventDialog.setTitle("Add Event");
		eventDialog.setValues(0, "", "");
		eventDialog.showDialog();
		refresh();
	}
	
	/***************************************************************************
	 * Prepares and show the modify event dialog
	 **************************************************************************/
	private void modifyEvent() {
		new Thread() {
			public void run() {
				int i = eventTable.getSelectedRow();
				if (i == -1) {
					JOptionPane.showMessageDialog(
							mainWindow, "Select the event to modify.",
							"Problem", JOptionPane.WARNING_MESSAGE);
					return;
				}

				int id = Integer.parseInt(
						eventTable.getValueAt(i, 0).toString());
				
				refreshBusy = true;
				Event e = netServ.getEventByID(id);
				refreshBusy = false;
				
				eventDialog.setTitle("Modify Event");
				eventDialog.setValues(id, e.getName(), e.getCriteria());
				eventDialog.showDialog();
				refresh();
			}
		}.start();
	}
	
	/***************************************************************************
	 * Deletes selected event
	 **************************************************************************/
	private void deleteEvent() {
		new Thread() {
			public void run() {
				int i = eventTable.getSelectedRow();
				if (i == -1) {
					JOptionPane.showMessageDialog(
							mainWindow, "Select the event to delete.",
							"Problem", JOptionPane.WARNING_MESSAGE);
					return;
				}

				int id = Integer.parseInt(
						eventTable.getValueAt(i, 0).toString());
				String nombre = eventTable.getValueAt(i, 1).toString();
				
				int res = JOptionPane.showConfirmDialog(
						mainWindow, "¿Do you wish to delete the event " + nombre + "?",
						"Delete Event", JOptionPane.YES_NO_OPTION);
				
				if (res == JOptionPane.NO_OPTION) return;
				if (res == -1) return;
				
				refreshBusy = true;
				netServ.removeEvent(id);
				refreshBusy = false;
				refresh();
			}
		}.start();
	}
	
	/***************************************************************************
	 * Prepares and shows the add maintenance task dialog
	 **************************************************************************/
	private void agregarTareaMantenimiento() {
		eventMaintenance.showAdd();
		refresh();
	}
	
	/***************************************************************************
	 * Prepares and show the modify maintenance tasks dialog
	 **************************************************************************/
	private void modificarTareaMantenimiento() {
		new Thread() {
			public void run() {
				int i = maintenanceTable.getSelectedRow();
				if (i == -1) {
					JOptionPane.showMessageDialog(
							mainWindow, "Select the task to modify.",
							"Problem", JOptionPane.WARNING_MESSAGE);
					return;
				}

				int id = Integer.parseInt(
						maintenanceTable.getValueAt(i, 0).toString());
				
				eventMaintenance.showChange(id);
				refresh();
			}
		}.start();
	}
	
	/***************************************************************************
	 * Deletes selected task
	 **************************************************************************/
	private void deleteMaintenanceTask() {
		new Thread() {
			public void run() {
				int i = maintenanceTable.getSelectedRow();
				if (i == -1) {
					JOptionPane.showMessageDialog(
							mainWindow, "Select task to delete.",
							"Problem", JOptionPane.WARNING_MESSAGE);
					return;
				}

				int id = Integer.parseInt(
						maintenanceTable.getValueAt(i, 0).toString());
				
				int res = JOptionPane.showConfirmDialog(
						mainWindow, "Do you wish to delete the task with ID " + id + "?",
						"Delete Task", JOptionPane.YES_NO_OPTION);
				
				if (res == JOptionPane.NO_OPTION) return;
				if (res == -1) return;
				
				refreshBusy = true;
				netServ.removeTask(id);
				refreshBusy = false;
				refresh();
			}
		}.start();
	}
	
	//--------------------------------------------------------------------------
	//
	//   Misc Functions
	//
	//==========================================================================

	/***************************************************************************
	 * Searches for the row corresponding to a given node id
	 * 
	 * @param nid 	Node to search for
	 * @return		Index of the row where the node is
	 **************************************************************************/
	private int findRow(int nid) {
		int row = -1;
		for (int i = 0; i < dataModel.getRowCount(); i++)
			if (dataModel.getValueAt(i, 0).
					toString().compareTo(nid + "") == 0)
				row = i;
		return row;
	}

	/***************************************************************************
	 * Converts a reading parameter value into a string
	 * 
	 * @param value		Parameter value
	 * @param parameter	Parameter type
	 * @return				A string with the formated reading
	 **************************************************************************/
	private String formatReading(String value, String parameter) {
		NumberFormat nf0 = new DecimalFormat("0");
		NumberFormat nf1 = new DecimalFormat("0.0");
		NumberFormat nf2 = new DecimalFormat("0.00");
		if (parameter.compareTo("Temp") == 0)
			return nf1.format(Double.parseDouble(value)) + " °C";
		else if (parameter.compareTo("TmIn") == 0)
			return nf1.format(Double.parseDouble(value)) + " °C";
		else if (parameter.compareTo("Hum") == 0)
			return nf1.format(Double.parseDouble(value)) + " %";
		else if (parameter.compareTo("Pres") == 0)
			return nf1.format(Double.parseDouble(value)) + " mba";
		else if (parameter.compareTo("AceX") == 0)
			return nf2.format(Double.parseDouble(value)) + " g";
		else if (parameter.compareTo("AceY") == 0)
			return nf2.format(Double.parseDouble(value)) + " g";
		else if (parameter.compareTo("Volt") == 0)
			return nf2.format(Double.parseDouble(value)) + " V";
		else if (parameter.compareTo("Light") == 0)
			return nf1.format(Double.parseDouble(value)) + " Lux";
		else return nf0.format(Double.parseDouble(value));
	}
	
	//--------------------------------------------------------------------------
	//
	//   Event handling
	//
	//==========================================================================

	/***************************************************************************
	 * Process an action
	 * 
	 * @param evt	Event generator
	 **************************************************************************/
	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();
		if (cmd.compareTo("") == 0)
			cmd = ((JButton)evt.getSource()).getToolTipText();
		
		if (cmd.compareTo("Exit") == 0) {
			System.exit(0);
		} else if (cmd.compareTo("Connect to server...") == 0) {
			connectServer();
		} else if (cmd.compareTo("Select network...") == 0) {
			netSelectionDialog.show(infoServ, progressBar);
		} else if (cmd.compareTo("Go to start") == 0) {
			slider.setValue(0);
			System.out.println("act Go to start");
			refresh();
		} else if (cmd.compareTo("Go to end") == 0) {
			slider.setValue(10000);
			System.out.println("act Go to end");
			refresh();
		} else if (cmd.compareTo("Refresh") == 0) {
			prepareLiveRefresh();
		} else if (cmd.compareTo("Play") == 0) {
			playPause();
		} else if (cmd.compareTo("Pause") == 0) {
			playPause();
		} else if (cmd.compareTo("Update everything") == 0) {
			refreshEverything();
		} else if (cmd.compareTo("Zoom out") == 0) {
			grapher.defDif(Math.round(grapher.obtDif() +
					grapher.obtDif() * 0.2d));
			System.out.println("act Zoom out");
			refresh();
		} else if (cmd.compareTo("Zoom in") == 0) {
			grapher.defDif(Math.round(grapher.obtDif() -
					grapher.obtDif() * 0.2d));
			System.out.println("act Zoom in");
			refresh();
		} else  if (cmd.compareTo("Export graph...") == 0) {
			exportGraph();
		} else if (cmd.compareTo("comboBoxChanged") == 0) {
			cmd = ((JComboBox)evt.getSource()).getToolTipText();
			if (cmd.compareTo("Parameter to graph") == 0)
				if (((JComboBox)evt.getSource()).isEnabled()) {
				System.out.println("act Parameter to graph");
				refresh();
			}
		} else if (cmd.compareTo("Import background image...") == 0) {
			importBackgroundImage();
		} else if (cmd.compareTo("Export topology image...") == 0) {
			exportTopologyGraph();
		} else if (cmd.compareTo("Add event...") == 0) {
			addEvent();
		} else if (cmd.compareTo("Modify event...") == 0) {
			modifyEvent();
		} else if (cmd.compareTo("Delete event...") == 0) {
			deleteEvent();
		} else if (cmd.compareTo("Add task...") == 0) {
			agregarTareaMantenimiento();
		} else if (cmd.compareTo("Modify task...") == 0) {
			modificarTareaMantenimiento();
		} else if (cmd.compareTo("Delete task...") == 0) {
			deleteMaintenanceTask();
		}
		
		else System.out.println(cmd);
	}

	/***************************************************************************
	 * Listen for slider changes
	 * 
	 * @param evt	Evento generador del cambio
	 **************************************************************************/
	public void stateChanged(ChangeEvent evt) {
		if (evt.getSource() instanceof JSlider) {		
			JSlider slider = (JSlider)evt.getSource();
			if (!slider.getValueIsAdjusting()) {
				System.out.println("act stateChanged");
				refresh();
			}
		} else if (evt.getSource() instanceof JTabbedPane) {
			System.out.println("act JTabbedPane");
			refresh();
		} else System.out.println(evt.getSource());
	}

	/***************************************************************************
	 * Executed when clicking a button. this is used in the
	 * node tree to select/unselect nodes
	 **************************************************************************/
	public void mouseClicked(MouseEvent evt) {
		if (evt.getSource() instanceof JTree) refreshDataTableNodes();
	}
	
	/***************************************************************************
	 * Executed when pressing a button. used to go forward or backwards in time
	 * with the toolbar buttons
	 * 
	 * @param evt	event originator
	 **************************************************************************/
	public void mousePressed(MouseEvent evt) {
		if (evt.getButton() != MouseEvent.BUTTON1) return;
		if (!(evt.getSource() instanceof JButton)) return;
		String cmd = ((JButton)evt.getSource()).getToolTipText();
		if (cmd.compareTo("Rewind") == 0) rewindTimer.restart();
		if (cmd.compareTo("Forward") == 0) forwardTimer.restart();
	}

	/***************************************************************************
	 * Executed when letting go a button. Used to stop the rewind or go forward
	 * actions
	 * 
	 * @param evt	event originator
	 **************************************************************************/
	public void mouseReleased(MouseEvent evt) {
		mouseExited(evt);
	}

	/***************************************************************************
	 * Executed when the mouse pointer exits the component area. Used to stop
	 * the rewind or forward actions
	 * 
	 * @param evt	event originator
	 **************************************************************************/
	public void mouseExited(MouseEvent evt) {
		if (!(evt.getSource() instanceof JButton)) return;
		String cmd = ((JButton)evt.getSource()).getToolTipText();
		if (cmd.compareTo("Rewind") == 0) rewindTimer.stop();
		if (cmd.compareTo("Forward") == 0) forwardTimer.stop();
	}
	
	/***************************************************************************
	 * Executed when the mouse pointer enters the component area. This is never
	 * used
	 **************************************************************************/	
	public void mouseEntered(MouseEvent evt) {}
	
}
 
