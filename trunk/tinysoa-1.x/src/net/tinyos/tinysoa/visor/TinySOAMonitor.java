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
 * PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND CICESE
 * HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS."
 * 
 ******************************************************************************/

package net.tinyos.tinysoa.visor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import net.tinyos.tinysoa.util.*;

/*******************************************************************************
 * Aplicación de demostración que utiliza los servicios ofrecidos por TinySOA
 * Servidor.
 * 
 * @author		Edgardo Avilés López
 * @version	0.4, 07/28/2006
 ******************************************************************************/
public class TinySOAMonitor {

	private static String ARCHIVO_CONFIGURACION = "config.xml";
	
	private static JFrame ventana;
	
	private static ImageIcon[] iconos;
	private static InterfaceEvents eventosInterfaz;
	private static JTree arbol;
	private static JPopupMenu popMenuEventos, popMenuMantenimiento;

	/***************************************************************************
	 * Crea el menú de la aplicación.
	 **************************************************************************/
	private static void crearMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Sistema");
		menu.setMnemonic(KeyEvent.VK_S);
		menuBar.add(menu);
		
		JMenuItem item;
		
		item = new JMenuItem("Conectar al servidor...", iconos[1]);
		item.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_C, KeyEvent.CTRL_MASK + KeyEvent.ALT_MASK));
		item.setMnemonic(KeyEvent.VK_C);
		item.addActionListener(eventosInterfaz);
		menu.add(item);
		
		item = new JMenuItem("Seleccionar red...", iconos[2]);
		item.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, KeyEvent.CTRL_MASK + KeyEvent.ALT_MASK));
		item.setMnemonic(KeyEvent.VK_S);
		item.addActionListener(eventosInterfaz);
		menu.add(item);
		
		item = new JMenuItem("Actualizar todo", iconos[3]);
		item.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_F5, KeyEvent.CTRL_MASK));
		item.setMnemonic(KeyEvent.VK_A);
		item.addActionListener(eventosInterfaz);
		menu.add(item);
		
		eventosInterfaz.defBotonMenuActualizarTodo(item);
		
		menu.addSeparator();
		
		item = new JMenuItem("Salir", iconos[0]);
		item.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_X, KeyEvent.CTRL_MASK + KeyEvent.ALT_MASK));
		item.setMnemonic(KeyEvent.VK_L);
		item.addActionListener(eventosInterfaz);
		menu.add(item);
		
		menu = new JMenu("Reproducción");
		menu.setMnemonic(KeyEvent.VK_R);
		menuBar.add(menu);
		
		JMenuItem itemInicio = new JMenuItem("Ir al inicio", iconos[10]);
		itemInicio.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK));
		itemInicio.setMnemonic(KeyEvent.VK_I);
		itemInicio.addActionListener(eventosInterfaz);
		menu.add(itemInicio);
		
		JMenuItem itemReproduccion = new JMenuItem("Reproducir", iconos[12]);
		itemReproduccion.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
		itemReproduccion.setMnemonic(KeyEvent.VK_R);
		itemReproduccion.addActionListener(eventosInterfaz);
		menu.add(itemReproduccion);
		
		JMenuItem itemFinal = new JMenuItem("Ir al final", iconos[13]);
		itemFinal.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK));
		itemFinal.setMnemonic(KeyEvent.VK_F);
		itemFinal.addActionListener(eventosInterfaz);
		menu.add(itemFinal);
		
		eventosInterfaz.defControlesReproduccion2(itemInicio, itemReproduccion,
				itemFinal, iconos[12], iconos[11]);
		
		ventana.setJMenuBar(menuBar);	
	}
	
	/***************************************************************************
	 * Crea la barra de herramientas principal de la aplicación.
	 **************************************************************************/
	private static void crearBarraHerramientas() {
		JToolBar barra = new JToolBar();
		barra.setFloatable(true);
		barra.setRollover(true);
		JButton boton;
		
		boton = new JButton(iconos[1]);
		boton.setToolTipText("Conectar al servidor...");
		boton.addActionListener(eventosInterfaz);
		boton.setFocusPainted(false);
		barra.add(boton);
		
		boton = new JButton(iconos[2]);
		boton.setToolTipText("Seleccionar red...");
		boton.addActionListener(eventosInterfaz);
		boton.setFocusPainted(false);
		barra.add(boton);
		
		boton = new JButton(iconos[3]);
		boton.setToolTipText("Actualizar todo");
		boton.addActionListener(eventosInterfaz);
		boton.setFocusPainted(false);
		barra.add(boton);
		
		eventosInterfaz.defBotonActualizarTodo(boton);
		
		barra.addSeparator();
		
		JButton botonAtrasar = new JButton(iconos[5]);
		botonAtrasar.setToolTipText("Rebobinar");
		botonAtrasar.addMouseListener(eventosInterfaz);
		botonAtrasar.setFocusPainted(false);
		barra.add(botonAtrasar);
		
		JButton botonReproducir = new JButton(iconos[7]);
		botonReproducir.setToolTipText("Reproducir");
		botonReproducir.addActionListener(eventosInterfaz);
		botonReproducir.setFocusPainted(false);
		barra.add(botonReproducir);
		
		JButton botonAdelantar = new JButton(iconos[8]);
		botonAdelantar.setToolTipText("Adelantar");
		botonAdelantar.addMouseListener(eventosInterfaz);
		botonAdelantar.setFocusPainted(false);
		barra.add(botonAdelantar);
		
		eventosInterfaz.defControlesReproduccion(botonAtrasar, botonReproducir,
				botonAdelantar, iconos[7], iconos[6]);
		
		barra.addSeparator();
		
		JCheckBox cb = new JCheckBox("Actualizar");
		cb.setOpaque(false);
		cb.setFocusPainted(false);
		cb.addActionListener(eventosInterfaz);
		eventosInterfaz.defActualizarCheck(cb);
		barra.add(cb);
		
		JProgressBar barraProgreso = new JProgressBar();
		barraProgreso.setIndeterminate(true);
		barraProgreso.setString("Cargando...");
		barraProgreso.setStringPainted(true);
		barraProgreso.setBackground(Color.WHITE);
		barraProgreso.setPreferredSize(new Dimension(100,20));
		barraProgreso.setMaximumSize(new Dimension(100,20));
		barraProgreso.setVisible(false);
		barra.add(Box.createGlue());
		barra.add(barraProgreso);
		barra.addSeparator();
		
		eventosInterfaz.defBarraProgreso(barraProgreso);
		
		Container cont = ventana.getContentPane();
		cont.setLayout(new BorderLayout());
		cont.add(barra, BorderLayout.NORTH);
	}
	
	/***************************************************************************
	 * Crea el arbol de nodos.
	 **************************************************************************/
	private static JScrollPane crearArbol() {
		arbol = new JTree() {
			private static final long serialVersionUID =
					-1739266534130784240L;
			protected void setExpandedState(TreePath path, boolean state) {
				super.setExpandedState(path, true);
			}
		};
		
		arbol.setCellRenderer(new NetTreeNodeRenderer());
		arbol.addMouseListener(new SelectedNetTreeNodeListener(arbol));
		arbol.addMouseListener(eventosInterfaz);
		arbol.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		arbol.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		
		((DefaultTreeModel)arbol.getModel()).setRoot(null);
		eventosInterfaz.defArbol(arbol);
				
		ToolTipManager.sharedInstance().registerComponent(arbol);
		JScrollPane vistaArbol = new JScrollPane(arbol);
		vistaArbol.setBorder(BorderFactory.createEmptyBorder());
		vistaArbol.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		vistaArbol.setMinimumSize(new Dimension(120, 120));
		vistaArbol.setPreferredSize(new Dimension(136, 136));
		vistaArbol.setMaximumSize(new Dimension(200, 200));
		
		return vistaArbol;
	}
	
	/***************************************************************************
	 * Crea el <i>slider</i>.
	 **************************************************************************/
	private static JPanel crearPanelSlider() {
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 10000, 10000);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(1000);
		slider.setMinorTickSpacing(100);
		slider.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		slider.addChangeListener(eventosInterfaz);
		JPanel panelSlider = new JPanel();
		panelSlider.setLayout(new BorderLayout());
		panelSlider.add(slider, BorderLayout.CENTER);
		
		JLabel label = new JLabel("<html><center>00/00/0000<br><b>00:00:00" +
				"</b></center></html>");
		label.setFont(new Font("Arial", Font.PLAIN, 11));
		label.setBorder(BorderFactory.createEmptyBorder(4,2,4,8));
		panelSlider.add(label, BorderLayout.EAST);
		
		eventosInterfaz.defSlider(slider, label);
		
		return panelSlider;
	}

	/***************************************************************************
	 * Crea el panel de <i>tabs</i>.
	 **************************************************************************/
	private static JPanel crearPanelTabs() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(4,6,2,6));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel tab = crearTabDatos();
		tab.setOpaque(false);
		tabbedPane.add(tab, "Datos");
		
		tab = crearTabGraficas();
		tab.setOpaque(false);
		tabbedPane.add(tab, "Gráficas");
		
		tab = crearTabTopologia();
		tab.setOpaque(false);
		tabbedPane.add(tab, "Topología");
		
		tab = crearTabEventos();
		tab.setOpaque(false);
		tabbedPane.add(tab, "Eventos");
		
		tab = crearTabMantenimiento();
		tab.setOpaque(false);
		tabbedPane.add(tab, "Mantenimiento");
		
		eventosInterfaz.defPanelTabs(tabbedPane);
		tabbedPane.addChangeListener(eventosInterfaz);
		
		panel.add(tabbedPane, BorderLayout.CENTER);		
		return panel;
	}
	
	/***************************************************************************
	 * Crea el <i>tab</i> de datos.
	 **************************************************************************/
	private static JPanel crearTabDatos() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		panel.setLayout(new BorderLayout());
		
		DefaultTableModel modelo = new DefaultTableModel();
		TableSorter ordenador = new TableSorter(modelo);
		MonitorTable tabla = new MonitorTable(ordenador);
		ordenador.setTableHeader(tabla.getTableHeader());
		JScrollPane scrollTabla = new JScrollPane(tabla);
		scrollTabla.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scrollTabla, BorderLayout.CENTER);
		eventosInterfaz.defTablaDatos(tabla, modelo);
		
		return panel;
	}
	
	/***************************************************************************
	 * Crea el <i>tab</i> de gráficas.
	 **************************************************************************/
	private static JPanel crearTabGraficas() {
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
		Plotter graficador = new Plotter();
		panel2.add(graficador, BorderLayout.CENTER);
		eventosInterfaz.defGraficador(graficador);
		
		JToolBar barra = new JToolBar();
		
		barra.add(Box.createGlue());

		JLabel label = new JLabel("Parámetro: ");
		label.setFocusable(false);
		label.setFont(new Font("Arial", Font.PLAIN, 12));
		
		JComboBox combo = new JComboBox();
		combo.setFocusable(false);
		combo.setMaximumSize(new Dimension(120, 25));
		combo.setToolTipText("Parámetro a graficar");
		eventosInterfaz.defComboParsGraf(combo);
		combo.addActionListener(eventosInterfaz);
		
		barra.add(label);
		barra.add(combo);
		
		barra.addSeparator();

		JButton boton = new JButton(iconos[17]);
		boton.setToolTipText("Exportar gráfica...");
		boton.addActionListener(eventosInterfaz);
		boton.setFocusPainted(false);
		barra.add(boton);
		eventosInterfaz.defBotonExportarGrafica(boton);

		barra.addSeparator();

		boton = new JButton(iconos[18]);
		boton.setToolTipText("Acercar");
		boton.addActionListener(eventosInterfaz);
		boton.setFocusPainted(false);
		barra.add(boton);
		
		boton = new JButton(iconos[19]);
		boton.setToolTipText("Alejar");
		boton.addActionListener(eventosInterfaz);
		boton.setFocusPainted(false);
		barra.add(boton);
		
		barra.add(Box.createGlue());
		
		barra.setFloatable(false);
		barra.setRollover(true);
		
		
		panel.add(panel2, BorderLayout.CENTER);
		panel.add(barra, BorderLayout.NORTH);
		
		return panel;
	}
	
	/***************************************************************************
	 * Crea el <i>tab</i> de topología.
	 **************************************************************************/
	private static JPanel crearTabTopologia() {
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
		TopologyPlotter graficador = new TopologyPlotter();
		panel2.add(graficador, BorderLayout.CENTER);
		eventosInterfaz.defGrafTopologia(graficador);
		
		JToolBar barra = new JToolBar();
		
		barra.add(Box.createGlue());

		JLabel label = new JLabel("Parámetro: ");
		label.setFocusable(false);
		label.setFont(new Font("Arial", Font.PLAIN, 12));
		
		JComboBox combo = new JComboBox();
		combo.setFocusable(false);
		combo.setMaximumSize(new Dimension(120, 25));
		combo.setToolTipText("Parámetro a graficar");
		eventosInterfaz.defComboParsTopologia(combo);
		combo.addActionListener(eventosInterfaz);
		
		barra.add(label);
		barra.add(combo);
		
		barra.addSeparator();
		
		JButton boton = new JButton(iconos[20]);
		boton.setToolTipText("Importar imagen de fondo...");
		boton.addActionListener(eventosInterfaz);
		boton.setFocusPainted(false);
		barra.add(boton);
		eventosInterfaz.defBotonImportarFondo(boton);

		boton = new JButton(iconos[17]);
		boton.setToolTipText("Exportar imagen de topología...");
		boton.addActionListener(eventosInterfaz);
		boton.setFocusPainted(false);
		barra.add(boton);
		eventosInterfaz.defBotonExportarTopologia(boton);
		
		barra.add(Box.createGlue());
		
		barra.setFloatable(false);
		barra.setRollover(true);
		
		panel.add(panel2, BorderLayout.CENTER);
		panel.add(barra, BorderLayout.NORTH);
		
		return panel;
	}
	
	/***************************************************************************
	 * Crea el <i>tab</i> de eventos.
	 **************************************************************************/
	private static JPanel crearTabEventos() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		panel.setLayout(new BorderLayout());
		
		DefaultTableModel modelo = new DefaultTableModel();
		TableSorter ordenador = new TableSorter(modelo);
		MonitorTable tabla = new MonitorTable(ordenador);
		ordenador.setTableHeader(tabla.getTableHeader());
		JScrollPane scrollTabla = new JScrollPane(tabla);
		scrollTabla.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scrollTabla, BorderLayout.CENTER);
		eventosInterfaz.defTablaEventos(tabla, modelo);
		
		popMenuEventos = new JPopupMenu();
		
		JMenuItem item = new JMenuItem("Agregar evento...");
		item.addActionListener(eventosInterfaz);
		popMenuEventos.add(item);
		
		item = new JMenuItem("Modificar evento...");
		item.addActionListener(eventosInterfaz);
		popMenuEventos.add(item);
		
		item = new JMenuItem("Eliminar evento...");
		item.addActionListener(eventosInterfaz);
		popMenuEventos.add(item);
		
		scrollTabla.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					boolean act = false;
					if (evt.getSource() instanceof JScrollPane)
						act = ((JScrollPane)evt.getSource()).isEnabled();
					if (evt.getSource() instanceof JTable)
						act = ((JTable)evt.getSource()).isEnabled();
					if (act) popMenuEventos.show(evt.getComponent(),
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
					if (act) popMenuEventos.show(evt.getComponent(),
								evt.getX(), evt.getY());
	            }
	        }
		});
		
		tabla.addMouseListener(scrollTabla.getMouseListeners()[0]);
		
		return panel;
	}
	
	/***************************************************************************
	 * Crea el <i>tab</i> de mantenimiento.
	 **************************************************************************/
	private static JPanel crearTabMantenimiento() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		panel.setLayout(new BorderLayout());
		
		DefaultTableModel modelo = new DefaultTableModel();
		TableSorter ordenador = new TableSorter(modelo);
		MonitorTable tabla = new MonitorTable(ordenador);
		ordenador.setTableHeader(tabla.getTableHeader());
		JScrollPane scrollTabla = new JScrollPane(tabla);
		scrollTabla.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scrollTabla, BorderLayout.CENTER);
		eventosInterfaz.defTablaMantenimiento(tabla, modelo);
		
		popMenuMantenimiento = new JPopupMenu();
		
		JMenuItem item = new JMenuItem("Agregar tarea...");
		item.addActionListener(eventosInterfaz);
		popMenuMantenimiento.add(item);
		
		item = new JMenuItem("Modificar tarea...");
		item.addActionListener(eventosInterfaz);
		popMenuMantenimiento.add(item);
		
		item = new JMenuItem("Eliminar tarea...");
		item.addActionListener(eventosInterfaz);
		popMenuMantenimiento.add(item);
		
		scrollTabla.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					boolean act = false;
					if (evt.getSource() instanceof JScrollPane)
						act = ((JScrollPane)evt.getSource()).isEnabled();
					if (evt.getSource() instanceof JTable)
						act = ((JTable)evt.getSource()).isEnabled();
					if (act) popMenuMantenimiento.show(evt.getComponent(),
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
					if (act) popMenuMantenimiento.show(evt.getComponent(),
								evt.getX(), evt.getY());
	            }
	        }
		});
		
		tabla.addMouseListener(scrollTabla.getMouseListeners()[0]);
		
		return panel;
	}
	
	/***************************************************************************
	 * Crea los páneles de tiempo e información.
	 **************************************************************************/
	private static JPanel crearPanelesContenido() {
		JPanel panel = new JPanel();
		panel.setMinimumSize(new Dimension(500,300));
		panel.setLayout(new BorderLayout());
		panel.add(crearPanelTabs(), BorderLayout.CENTER);		
		panel.add(crearPanelSlider(), BorderLayout.SOUTH);
		return panel;
	}
	
	/***************************************************************************
	 * Crea la interfaz de usuario.
	 **************************************************************************/
	private static void crearVentana() {
		//try {	UIManager.setLookAndFeel(	"com.sun.java.swing.plaf.windows." + "WindowsLookAndFeel");	} catch (Exception e) {}
		
		ventana = new JFrame("TinySOA Visor 1.0");
		ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		eventosInterfaz = new InterfaceEvents(ARCHIVO_CONFIGURACION, ventana,
				iconos[2], iconos[15], iconos[14], iconos[16]);
		crearMenu();
		crearBarraHerramientas();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.add(crearArbol(), JSplitPane.LEFT);
		splitPane.add(crearPanelesContenido(), JSplitPane.RIGHT);
		ventana.getContentPane().add(splitPane, BorderLayout.CENTER);

		ventana.setSize(new Dimension(800, 600));
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		ventana.setLocation((dim.width - ventana.getSize().width) / 2,
				(dim.height - ventana.getSize().height) / 2);
		
		ventana.setVisible(true);
	}
	
	/***************************************************************************
	 * Función principal del visor.
	 * 
	 * @param args	Argumentos de entrada.
	 **************************************************************************/	
	public static void main(String[] args) {
		UIManager.getDefaults().put("ScrollPane.background", new Color(0xe1e6ec));
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				iconos = new ImageIcon[21];
				iconos[0] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.empty.png"));
				iconos[1] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.server.png"));
				iconos[2] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.select.net.png"));
				iconos[3] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.refresh.png"));
				iconos[4] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control.start.png"));
				iconos[5] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control.back.png"));
				iconos[6] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control.pause.png"));
				iconos[7] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control.play.png"));
				iconos[8] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control.forward.png"));
				iconos[9] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control.final.png"));
				iconos[10] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control2.start.png"));
				iconos[11] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control2.pause.png"));
				iconos[12] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control2.play.png"));
				iconos[13] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.control2.final.png"));
				iconos[14] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.sensor.waiting.png"));
				iconos[15] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.sensor.normal.png"));
				iconos[16] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.sensor.problem.png"));
				iconos[17] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.export.png"));
				iconos[18] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.zoom.in.png"));
				iconos[19] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.zoom.out.png"));
				iconos[20] = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/men.import.image.png"));

				Locale.setDefault(new Locale("us", "US"));
				
				crearVentana();
				eventosInterfaz.desactivarControles();
				eventosInterfaz.conectarServidor();
			}
		});
	}
}
