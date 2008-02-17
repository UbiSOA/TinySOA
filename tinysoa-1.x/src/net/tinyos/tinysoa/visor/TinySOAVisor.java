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

package net.tinyos.tinysoa.visor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import net.tinyos.tinysoa.util.graphs.*;
import net.tinyos.tinysoa.util.tables.*;
import net.tinyos.tinysoa.util.trees.*;

/*******************************************************************************
 * Demo app that uses those services offered by TinySOA Server
 * 
 * @author		Edgardo Avilés López
 * @version	0.4, 07/28/2006
 ******************************************************************************/
public class TinySOAVisor {

	private static String CONFIG_FILE = "config.xml";
	
	private static JFrame window;
	
	private static ImageIcon[] icons;
	private static InterfaceEvents interfaceEvents;
	private static JTree tree;
	private static JPopupMenu popMenuEvents, popMenuMaintenance;
	
	private static Logger logger = Logger.getLogger(TinySOAVisor.class);

	/***************************************************************************
	 * Creates the application menu
	 **************************************************************************/
	private static void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("System");
		menu.setMnemonic(KeyEvent.VK_S);
		menuBar.add(menu);
		
		JMenuItem item;
		
		item = new JMenuItem("Connect to server...", icons[1]);
		item.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_C, KeyEvent.CTRL_MASK + KeyEvent.ALT_MASK));
		item.setMnemonic(KeyEvent.VK_C);
		item.addActionListener(interfaceEvents);
		menu.add(item);
		
		item = new JMenuItem("Select network...", icons[2]);
		item.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, KeyEvent.CTRL_MASK + KeyEvent.ALT_MASK));
		item.setMnemonic(KeyEvent.VK_S);
		item.addActionListener(interfaceEvents);
		menu.add(item);
		
		item = new JMenuItem("Update everything", icons[3]);
		item.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_F5, KeyEvent.CTRL_MASK));
		item.setMnemonic(KeyEvent.VK_A);
		item.addActionListener(interfaceEvents);
		menu.add(item);
		
		interfaceEvents.setRefreshEverythingMenuButton(item);
		
		menu.addSeparator();
		
		item = new JMenuItem("Exit", icons[0]);
		item.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_X, KeyEvent.CTRL_MASK + KeyEvent.ALT_MASK));
		item.setMnemonic(KeyEvent.VK_L);
		item.addActionListener(interfaceEvents);
		menu.add(item);
		
		menu = new JMenu("Play");
		menu.setMnemonic(KeyEvent.VK_R);
		menuBar.add(menu);
		
		JMenuItem itemInicio = new JMenuItem("Go to start", icons[10]);
		itemInicio.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK));
		itemInicio.setMnemonic(KeyEvent.VK_I);
		itemInicio.addActionListener(interfaceEvents);
		menu.add(itemInicio);
		
		JMenuItem itemReproduccion = new JMenuItem("Play", icons[12]);
		itemReproduccion.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
		itemReproduccion.setMnemonic(KeyEvent.VK_R);
		itemReproduccion.addActionListener(interfaceEvents);
		menu.add(itemReproduccion);
		
		JMenuItem itemFinal = new JMenuItem("Go to end", icons[13]);
		itemFinal.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK));
		itemFinal.setMnemonic(KeyEvent.VK_F);
		itemFinal.addActionListener(interfaceEvents);
		menu.add(itemFinal);
		
		interfaceEvents.setPlayControls2(itemInicio, itemReproduccion,
				itemFinal, icons[12], icons[11]);
		
		window.setJMenuBar(menuBar);	
	}
	
	/***************************************************************************
	 * Creates the main toolbar
	 **************************************************************************/
	private static void createToolbar() {
		JToolBar bar = new JToolBar();
		bar.setFloatable(true);
		bar.setRollover(true);
		JButton button;
		
		button = new JButton(icons[1]);
		button.setToolTipText("Connect to server...");
		button.addActionListener(interfaceEvents);
		button.setFocusPainted(false);
		bar.add(button);
		
		button = new JButton(icons[2]);
		button.setToolTipText("Select network...");
		button.addActionListener(interfaceEvents);
		button.setFocusPainted(false);
		bar.add(button);
		
		button = new JButton(icons[3]);
		button.setToolTipText("Update everything");
		button.addActionListener(interfaceEvents);
		button.setFocusPainted(false);
		bar.add(button);
		
		interfaceEvents.setRefreshEverythingButton(button);
		
		bar.addSeparator();
		
		JButton rewindButton = new JButton(icons[5]);
		rewindButton.setToolTipText("Rewind");
		rewindButton.addMouseListener(interfaceEvents);
		rewindButton.setFocusPainted(false);
		bar.add(rewindButton);
		
		JButton playButton = new JButton(icons[7]);
		playButton.setToolTipText("Play");
		playButton.addActionListener(interfaceEvents);
		playButton.setFocusPainted(false);
		bar.add(playButton);
		
		JButton forwardButton = new JButton(icons[8]);
		forwardButton.setToolTipText("Forward");
		forwardButton.addMouseListener(interfaceEvents);
		forwardButton.setFocusPainted(false);
		bar.add(forwardButton);
		
		interfaceEvents.setPlayControls(rewindButton, playButton,
				forwardButton, icons[7], icons[6]);
		
		bar.addSeparator();
		
		JCheckBox cb = new JCheckBox("Refresh");
		cb.setOpaque(false);
		cb.setFocusPainted(false);
		cb.addActionListener(interfaceEvents);
		interfaceEvents.setRefreshCheckbox(cb);
		bar.add(cb);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setString("Loading...");
		progressBar.setStringPainted(true);
		progressBar.setBackground(Color.WHITE);
		progressBar.setPreferredSize(new Dimension(100,20));
		progressBar.setMaximumSize(new Dimension(100,20));
		progressBar.setVisible(false);
		bar.add(Box.createGlue());
		bar.add(progressBar);
		bar.addSeparator();
		
		interfaceEvents.setProgressBar(progressBar);
		
		Container cont = window.getContentPane();
		cont.setLayout(new BorderLayout());
		cont.add(bar, BorderLayout.NORTH);
	}
	
	/***************************************************************************
	 * Creates the node tree
	 **************************************************************************/
	private static JScrollPane createTree() {
		tree = new JTree() {
			private static final long serialVersionUID =
					-1739266534130784240L;
			protected void setExpandedState(TreePath path, boolean state) {
				super.setExpandedState(path, true);
			}
		};
		
		tree.setCellRenderer(new NetTreeNodeRenderer());
		tree.addMouseListener(new SelectedNetTreeNodeListener(tree));
		tree.addMouseListener(interfaceEvents);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		
		((DefaultTreeModel)tree.getModel()).setRoot(null);
		interfaceEvents.setTree(tree);
				
		ToolTipManager.sharedInstance().registerComponent(tree);
		JScrollPane treeView = new JScrollPane(tree);
		treeView.setBorder(BorderFactory.createEmptyBorder());
		treeView.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		treeView.setMinimumSize(new Dimension(120, 120));
		treeView.setPreferredSize(new Dimension(136, 136));
		treeView.setMaximumSize(new Dimension(200, 200));
		
		return treeView;
	}
	
	/***************************************************************************
	 * Create the slider
	 **************************************************************************/
	private static JPanel createPanelSlider() {
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 10000, 10000);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(1000);
		slider.setMinorTickSpacing(100);
		slider.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		slider.addChangeListener(interfaceEvents);
		JPanel panelSlider = new JPanel();
		panelSlider.setLayout(new BorderLayout());
		panelSlider.add(slider, BorderLayout.CENTER);
		
		JLabel label = new JLabel("<html><center>00/00/0000<br><b>00:00:00" +
				"</b></center></html>");
		label.setFont(new Font("Arial", Font.PLAIN, 11));
		label.setBorder(BorderFactory.createEmptyBorder(4,2,4,8));
		panelSlider.add(label, BorderLayout.EAST);
		
		interfaceEvents.setSlider(slider, label);
		
		return panelSlider;
	}

	/***************************************************************************
	 * Create the tab panel
	 **************************************************************************/
	private static JPanel createTabPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(4,6,2,6));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel tab = createDataTab();
		tab.setOpaque(false);
		tabbedPane.add(tab, "Data");
		
		tab = createGraphsTab();
		tab.setOpaque(false);
		tabbedPane.add(tab, "Graphs");
		
		tab = createTopologyTab();
		tab.setOpaque(false);
		tabbedPane.add(tab, "Topology");
		
		tab = createEventTab();
		tab.setOpaque(false);
		tabbedPane.add(tab, "Events");
		
		tab = createMaintenanceTab();
		tab.setOpaque(false);
		tabbedPane.add(tab, "Maintenance");
		
		interfaceEvents.setTabPanel(tabbedPane);
		tabbedPane.addChangeListener(interfaceEvents);
		
		panel.add(tabbedPane, BorderLayout.CENTER);		
		return panel;
	}
	
	/***************************************************************************
	 * Creates the data tab
	 **************************************************************************/
	private static JPanel createDataTab() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		panel.setLayout(new BorderLayout());
		
		DefaultTableModel model = new DefaultTableModel();
		TableSorter sorter = new TableSorter(model);
		MonitorTable table = new MonitorTable(sorter);
		sorter.setTableHeader(table.getTableHeader());
		JScrollPane tableScroll = new JScrollPane(table);
		tableScroll.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(tableScroll, BorderLayout.CENTER);
		interfaceEvents.setDataTable(table, model);
		
		return panel;
	}
	
	/***************************************************************************
	 * Creates the graph tab
	 **************************************************************************/
	private static JPanel createGraphsTab() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(4,4,4,4),
				new JTextField().getBorder()));
		panel.setLayout(new BorderLayout());
		panel.setOpaque(true);
		panel.setBackground(Color.WHITE);
		
		JPanel panel2 = new JPanel();
		panel2.setOpaque(true);
		panel2.setBackground(Color.WHITE);
		//panel2.setBorder(new JTextField().getBorder());
		panel2.setLayout(new BorderLayout());
		Plotter grapher = new Plotter();
		panel2.add(grapher, BorderLayout.CENTER);
		interfaceEvents.setGrapher(grapher);
		
		JToolBar bar = new JToolBar();
		
		bar.add(Box.createGlue());

		JLabel label = new JLabel("Parameter: ");
		label.setFocusable(false);
		label.setFont(new Font("Arial", Font.PLAIN, 12));
		
		JComboBox combo = new JComboBox();
		combo.setFocusable(false);
		combo.setMaximumSize(new Dimension(120, 25));
		combo.setToolTipText("Parameter to graph");
		interfaceEvents.setComboParsGraf(combo);
		combo.addActionListener(interfaceEvents);
		
		bar.add(label);
		bar.add(combo);
		
		bar.addSeparator();

		JButton button = new JButton(icons[17]);
		button.setToolTipText("Exporting graphic...");
		button.addActionListener(interfaceEvents);
		button.setFocusPainted(false);
		bar.add(button);
		interfaceEvents.setExportGraphButton(button);

		bar.addSeparator();

		button = new JButton(icons[18]);
		button.setToolTipText("Zoom in");
		button.addActionListener(interfaceEvents);
		button.setFocusPainted(false);
		bar.add(button);
		
		button = new JButton(icons[19]);
		button.setToolTipText("Zoom out");
		button.addActionListener(interfaceEvents);
		button.setFocusPainted(false);
		bar.add(button);
		
		bar.add(Box.createGlue());
		
		bar.setFloatable(false);
		bar.setRollover(true);
		
		
		panel.add(panel2, BorderLayout.CENTER);
		panel.add(bar, BorderLayout.NORTH);
		
		return panel;
	}
	
	/***************************************************************************
	 * Create topology tab
	 **************************************************************************/
	private static JPanel createTopologyTab() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(4,4,4,4),
				new JTextField().getBorder()));
		panel.setLayout(new BorderLayout());
		panel.setOpaque(true);
		panel.setBackground(Color.WHITE);
		
		JPanel panel2 = new JPanel();
		panel2.setOpaque(true);
		panel2.setBackground(Color.WHITE);
		//panel2.setBorder(new JTextField().getBorder());
		panel2.setLayout(new BorderLayout());
		ColorMap grapher = new ColorMap();
		panel2.add(grapher, BorderLayout.CENTER);
		interfaceEvents.setGraphTopology(grapher);
		
		JToolBar bar = new JToolBar();
		
		bar.add(Box.createGlue());

		JLabel label = new JLabel("Parameter: ");
		label.setFocusable(false);
		label.setFont(new Font("Arial", Font.PLAIN, 12));
		
		JComboBox combo = new JComboBox();
		combo.setFocusable(false);
		combo.setMaximumSize(new Dimension(120, 25));
		combo.setToolTipText("Parameter to graph");
		interfaceEvents.setTopologyComboParams(combo);
		combo.addActionListener(interfaceEvents);
		
		bar.add(label);
		bar.add(combo);
		
		bar.addSeparator();
		
		JButton button = new JButton(icons[20]);
		button.setToolTipText("Import background image...");
		button.addActionListener(interfaceEvents);
		button.setFocusPainted(false);
		bar.add(button);
		interfaceEvents.setImportBackgroundButton(button);

		button = new JButton(icons[17]);
		button.setToolTipText("Export topology image...");
		button.addActionListener(interfaceEvents);
		button.setFocusPainted(false);
		bar.add(button);
		interfaceEvents.setExportTopologyButton(button);
		
		bar.add(Box.createGlue());
		
		bar.setFloatable(false);
		bar.setRollover(true);
		
		panel.add(panel2, BorderLayout.CENTER);
		panel.add(bar, BorderLayout.NORTH);
		
		return panel;
	}
	
	/***************************************************************************
	 * Creates the event tab
	 **************************************************************************/
	private static JPanel createEventTab() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		panel.setLayout(new BorderLayout());
		
		DefaultTableModel model = new DefaultTableModel();
		TableSorter sorter = new TableSorter(model);
		MonitorTable table = new MonitorTable(sorter);
		sorter.setTableHeader(table.getTableHeader());
		JScrollPane scrollTable = new JScrollPane(table);
		scrollTable.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scrollTable, BorderLayout.CENTER);
		interfaceEvents.setEventTable(table, model);
		
		popMenuEvents = new JPopupMenu();
		
		JMenuItem item = new JMenuItem("Add event...");
		item.addActionListener(interfaceEvents);
		popMenuEvents.add(item);
		
		item = new JMenuItem("Modify event...");
		item.addActionListener(interfaceEvents);
		popMenuEvents.add(item);
		
		item = new JMenuItem("Delete event...");
		item.addActionListener(interfaceEvents);
		popMenuEvents.add(item);
		
		scrollTable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					boolean act = false;
					if (evt.getSource() instanceof JScrollPane)
						act = ((JScrollPane)evt.getSource()).isEnabled();
					if (evt.getSource() instanceof JTable)
						act = ((JTable)evt.getSource()).isEnabled();
					if (act) popMenuEvents.show(evt.getComponent(),
								evt.getX(), evt.getY());
				}
			}
	       public void mouseReleased(MouseEvent evt) {
	            if (evt.isPopupTrigger()) {
					boolean act = false;
					if (evt.getSource() instanceof JScrollPane)
						act = ((JScrollPane)evt.getSource()).isEnabled();
					if (evt.getSource() instanceof JTable)
						act = ((JTable)evt.getSource()).isEnabled();
					if (act) popMenuEvents.show(evt.getComponent(),
								evt.getX(), evt.getY());
	            }
	        }
		});
		
		table.addMouseListener(scrollTable.getMouseListeners()[0]);
		
		return panel;
	}
	
	/***************************************************************************
	 * Create the maintenance tab
	 **************************************************************************/
	private static JPanel createMaintenanceTab() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		panel.setLayout(new BorderLayout());
		
		DefaultTableModel model = new DefaultTableModel();
		TableSorter sorter = new TableSorter(model);
		MonitorTable table = new MonitorTable(sorter);
		sorter.setTableHeader(table.getTableHeader());
		JScrollPane scrollTable = new JScrollPane(table);
		scrollTable.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scrollTable, BorderLayout.CENTER);
		interfaceEvents.setMaintenanceTable(table, model);
		
		popMenuMaintenance = new JPopupMenu();
		
		JMenuItem item = new JMenuItem("Add task...");
		item.addActionListener(interfaceEvents);
		popMenuMaintenance.add(item);
		
		item = new JMenuItem("Modify task...");
		item.addActionListener(interfaceEvents);
		popMenuMaintenance.add(item);
		
		item = new JMenuItem("Delete task...");
		item.addActionListener(interfaceEvents);
		popMenuMaintenance.add(item);
		
		scrollTable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					boolean act = false;
					if (evt.getSource() instanceof JScrollPane)
						act = ((JScrollPane)evt.getSource()).isEnabled();
					if (evt.getSource() instanceof JTable)
						act = ((JTable)evt.getSource()).isEnabled();
					if (act) popMenuMaintenance.show(evt.getComponent(),
								evt.getX(), evt.getY());
				}
			}
	       public void mouseReleased(MouseEvent evt) {
	            if (evt.isPopupTrigger()) {
					boolean act = false;
					if (evt.getSource() instanceof JScrollPane)
						act = ((JScrollPane)evt.getSource()).isEnabled();
					if (evt.getSource() instanceof JTable)
						act = ((JTable)evt.getSource()).isEnabled();
					if (act) popMenuMaintenance.show(evt.getComponent(),
								evt.getX(), evt.getY());
	            }
	        }
		});
		
		table.addMouseListener(scrollTable.getMouseListeners()[0]);
		
		return panel;
	}
	
	/***************************************************************************
	 * Create the time and information panel
	 **************************************************************************/
	private static JPanel createContentPanes() {
		JPanel panel = new JPanel();
		panel.setMinimumSize(new Dimension(500,300));
		panel.setLayout(new BorderLayout());
		panel.add(createTabPanel(), BorderLayout.CENTER);		
		panel.add(createPanelSlider(), BorderLayout.SOUTH);
		return panel;
	}
	
	/***************************************************************************
	 * Create the GUI
	 **************************************************************************/
	private static void createWindow() {
		//try {	UIManager.setLookAndFeel(	"com.sun.java.swing.plaf.windows." + "WindowsLookAndFeel");	} catch (Exception e) {}
		
		window = new JFrame("TinySOA Monitor 1.0");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		interfaceEvents = new InterfaceEvents(CONFIG_FILE, window,
				icons[2], icons[15], icons[14], icons[16], logger);
		createMenu();
		createToolbar();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.add(createTree(), JSplitPane.LEFT);
		splitPane.add(createContentPanes(), JSplitPane.RIGHT);
		window.getContentPane().add(splitPane, BorderLayout.CENTER);

		window.setSize(new Dimension(800, 600));
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation((dim.width - window.getSize().width) / 2,
				(dim.height - window.getSize().height) / 2);
		
		window.setVisible(true);
	}
	
	/***************************************************************************
	 * Main monitor function
	 * 
	 * @param args	Input arguments
	 **************************************************************************/	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		UIManager.getDefaults().put("ScrollPane.background", new Color(0xe1e6ec));
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				icons = new ImageIcon[21];
				icons[0] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.empty.png"));
				icons[1] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.server.png"));
				icons[2] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.select.net.png"));
				icons[3] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.refresh.png"));
				icons[4] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control.start.png"));
				icons[5] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control.back.png"));
				icons[6] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control.pause.png"));
				icons[7] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control.play.png"));
				icons[8] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control.forward.png"));
				icons[9] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control.final.png"));
				icons[10] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control2.start.png"));
				icons[11] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control2.pause.png"));
				icons[12] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control2.play.png"));
				icons[13] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control2.final.png"));
				icons[14] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.sensor.waiting.png"));
				icons[15] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.sensor.normal.png"));
				icons[16] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.sensor.problem.png"));
				icons[17] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.export.png"));
				icons[18] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.zoom.in.png"));
				icons[19] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.zoom.out.png"));
				icons[20] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.import.image.png"));

				Locale.setDefault(new Locale("us", "US"));
				
				createWindow();
				interfaceEvents.deactivateControls();
				interfaceEvents.connectServer();
			}
		});
	}
}
