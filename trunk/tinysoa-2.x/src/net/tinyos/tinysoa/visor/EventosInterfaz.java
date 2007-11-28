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
 * Clase que maneja y administra el comportamiento dinámico del visor.
 * Incluídos el manejo de eventos, las actualizaciones de la información de
 * los componentes y la comunicación con los servicios del servidor.
 * 
 * @author		Edgardo Avilés López
 * @version	0.5, 07/28/2006
 ******************************************************************************/
public class EventosInterfaz
	implements ActionListener, ChangeListener, MouseListener {

	private String archivoConfiguracion;
	private String urlServidor;
	private InfoServ servicioInformacion;
	private RedServ servicioRed;
	private DialogoSeleccionRed dialogoSeleccionRed;
	private JTree arbol;
	private JSlider slider;
	private JLabel etiquetaSlider;
	@SuppressWarnings("unused")
	private ImageIcon iconoRed, iconoEstadoNormal, iconoEstadoEspera,
			iconoEstadoProblema, iconoReproduccion, iconoPausa,
			iconoReproduccion2, iconoPausa2;
	private long tiempoMinimo, tiempoMaximo;
	private Timer rebobinarTimer, adelantarTimer, reproduccionTimer,
			actualizarVivo;
	private String urlServicioRed;
	private JCheckBox actualizarCheck;
	private JButton botonBarraAtrasar, botonBarraAdelantar,
			botonBarraReproducir, botonBarraActualizar, botonExportarGrafica,
			botonExportarTopologia, botonImportarFondo;
	private JMenuItem menuInicio, menuReproducir, menuFinal, menuActualizar;
	private JProgressBar barraProgreso;
	private Tabla tablaDatos, tablaEventos, tablaMantenimiento;
	private DefaultTableModel modeloDatos, modeloEventos, modeloMantenimiento;
	private boolean actualizacionOcupada = false;
	private Date tiempo;
	private JTabbedPane panelTabs;
	private Graficador graficador;
	private GraficadorTopologia graficadorTopologia;
	private JComboBox comboParsGraf, comboParsTopologia;
	private static Point[] posTopNodos;
	private JFrame ventana;
	private DialogoEvento dialogoEvento;
	private DialogoMantenimiento dialogoMantenimiento;
	
	/***************************************************************************
	 * Constructor principal de la clase.
	 * 
	 * @param archivoConfiguracion	Archivo con los valores de configuración
	 * @param window				Ventana padre de la clase
	 * @param iconoRed				Icono de una red
	 * @param iconoEstadoNormal	Icono de nodo con estado normal
	 * @param iconoEstadoEspera	Icono de nodo con estado de espera
	 * @param iconoEstadoProblema	Icono de nodo con estado problema
	 **************************************************************************/
	public EventosInterfaz(String archivoConfiguracion, JFrame ventana,
			ImageIcon iconoRed, ImageIcon iconoEstadoNormal,
			ImageIcon iconoEstadoEspera, ImageIcon iconoEstadoProblema) {
		this.archivoConfiguracion = archivoConfiguracion;
		this.ventana = ventana;
		this.iconoRed = iconoRed;
		this.iconoEstadoNormal = iconoEstadoNormal;
		this.iconoEstadoEspera = iconoEstadoEspera;
		this.iconoEstadoProblema = iconoEstadoProblema;
		dialogoSeleccionRed = new DialogoSeleccionRed(ventana, iconoRed, this);
		cargarConfiguracion();
		
		rebobinarTimer = new Timer(250, new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				int nuevoValor = slider.getValue() - 50;
				if (nuevoValor < 0) nuevoValor = 0;
				slider.setValue(nuevoValor);
				System.out.println("act rebobinarTimer");
				actualizar();
			}});
		adelantarTimer = new Timer(250, new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				int nuevoValor = slider.getValue() + 50;
				if (nuevoValor > 10000) nuevoValor = 10000;
				slider.setValue(nuevoValor);
				System.out.println("act adelantarTimer");
				actualizar();
			}});
		reproduccionTimer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				int nuevoValor = slider.getValue() + 25;
				if (nuevoValor > 10000) nuevoValor = 10000;
				slider.setValue(nuevoValor);
				System.out.println("act reproduccionTimer");
				actualizar();
				if (nuevoValor == 10000)
					reproducirPausar();
			}});
		actualizarVivo = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				actualizarTodo();				
			}});
		rebobinarTimer.setInitialDelay(0);
		adelantarTimer.setInitialDelay(0);
		reproduccionTimer.setInitialDelay(0);
	}
	
	//--------------------------------------------------------------------------
	//
	//   INDICACIÓN DE COMPONENTES A SER MANIPULADOS POR LOS EVENTOS.
	//
	//==========================================================================
	
	/***************************************************************************
	 * Define el arbol de nodos.
	 * 
	 * @param arbol	Arbol de nodos
	 **************************************************************************/
	public void defArbol(JTree arbol) {
		this.arbol = arbol;
	}
	
	/***************************************************************************
	 * Define el <i>slider</i> de selección de tiempos y su etiqueta.
	 * 
	 * @param slider			<i>Slider</i> de selección
	 * @param etiquetaSlider	Etiqueta del <i>slider</i>
	 **************************************************************************/
	public void defSlider(JSlider slider, JLabel etiquetaSlider) {
		this.slider = slider;
		this.etiquetaSlider = etiquetaSlider;
	}

	/***************************************************************************
	 * Define el <i>checkbox</i> de actualización.
	 * 
	 * @param actualizar	<i>Checkbox</i> de actualización
	 **************************************************************************/
	public void defActualizarCheck(JCheckBox actualizar) {
		this.actualizarCheck = actualizar;
	}
	
	/***************************************************************************
	 * Define los controles de reproducción.
	 * 
	 * @param botonBarraAtrasar	Botón para atrasar
	 * @param botonBarraReproducir	Botón para reproducir
	 * @param botonBarraAdelantar	Botón para adelantar
	 * @param iconoReproduccion	Icono de reproducción
	 * @param iconoPausa			Icono de pausa
	 **************************************************************************/
	public void defControlesReproduccion(JButton botonBarraAtrasar,
			JButton botonBarraReproducir, JButton botonBarraAdelantar,
			ImageIcon iconoReproduccion, ImageIcon iconoPausa) {
		this.botonBarraAtrasar = botonBarraAtrasar;
		this.botonBarraReproducir = botonBarraReproducir;
		this.botonBarraAdelantar = botonBarraAdelantar;
		this.iconoReproduccion = iconoReproduccion;
		this.iconoPausa = iconoPausa;
	}
	
	/***************************************************************************
	 * Define los controles de reproducción de la barra de herramientas.
	 * 
	 * @param menuInicio			Botón para regresar al inicio
	 * @param menuReproducir		Botón para reproducir
	 * @param menuFinal			Botón para ir al final
	 * @param iconoReproduccion2	Icono de reproducción
	 * @param iconoPausa2			Icono de pausa
	 **************************************************************************/
	public void defControlesReproduccion2(JMenuItem menuInicio,
			JMenuItem menuReproducir, JMenuItem menuFinal,
			ImageIcon iconoReproduccion2, ImageIcon iconoPausa2) {
		this.menuInicio = menuInicio;
		this.menuReproducir = menuReproducir;
		this.menuFinal = menuFinal;
		this.iconoReproduccion2 = iconoReproduccion2;
		this.iconoPausa2 = iconoPausa2;
	}
	
	/***************************************************************************
	 * Define el botón para actualizar todo.
	 * 
	 * @param botonBarraActualizar	Botón para actualizar todo
	 **************************************************************************/
	public void defBotonActualizarTodo(JButton botonBarraActualizar) {
		this.botonBarraActualizar = botonBarraActualizar;
	}
	
	/***************************************************************************
	 * Define el botón de la barra de herramientas para actualizar todo.
	 * 
	 * @param menuActualizar	Botón para actualizar todo
	 **************************************************************************/
	public void defBotonMenuActualizarTodo(JMenuItem menuActualizar) {
		this.menuActualizar = menuActualizar;
	}
	
	/***************************************************************************
	 * Define la barra de progreso.
	 * 
	 * @param barraProgreso	Barra de progreso
	 **************************************************************************/
	public void defBarraProgreso(JProgressBar barraProgreso) {
		this.barraProgreso = barraProgreso;
	}
	
	/***************************************************************************
	 * Define el panel de <i>tabs</i>.
	 * 
	 * @param panelTabs	Panel de <i>tabs</i>
	 **************************************************************************/
	public void defPanelTabs(JTabbedPane panelTabs) {
		this.panelTabs = panelTabs;
	}
	
	/***************************************************************************
	 * Define la tabla de datos a utilizar por el <i>tab</i> de datos.
	 * @param tabla
	 * @param modelo
	 **************************************************************************/
	public void defTablaDatos(Tabla tabla, DefaultTableModel modelo) {
		tablaDatos = tabla;
		modeloDatos = modelo;
	}

	/***************************************************************************
	 * Define el graficador a utilizar por el <i>tab</i> de gráficas.
	 * 
	 * @param graficador	Graficador a utilizar
	 **************************************************************************/
	public void defGraficador(Graficador graficador) {
		this.graficador = graficador;
	}
	
	/***************************************************************************
	 * Define el <i>ComboBox</i> a utilizar para seleccionar el parámetro a
	 * utilizar con el graficador.
	 * 
	 * @param comboParsGraf	<i>ComboBox</i> con el listado de parámetros
	 **************************************************************************/
	public void defComboParsGraf(JComboBox comboParsGraf) {
		this.comboParsGraf = comboParsGraf;
	}
	
	/***************************************************************************
	 * Define el botón para exportar una imágen del estado actual del
	 * graficador.
	 * 
	 * @param botonExportarGrafica	Botón para exportar una imagen
	 **************************************************************************/
	public void defBotonExportarGrafica(JButton botonExportarGrafica) {
		this.botonExportarGrafica = botonExportarGrafica;
	}
	
	/***************************************************************************
	 * Define el graficador de topología a utilizar por el <i>tab</i> de
	 * topología.
	 * 
	 * @param graficadorTopologia	Graficador de topología a utilizar
	 **************************************************************************/
	public void defGrafTopologia(GraficadorTopologia graficadorTopologia) {
		this.graficadorTopologia = graficadorTopologia;
	}
	
	/***************************************************************************
	 * Define el <i>ComboBox</i> a utilizar para seleccionar el parámetro a
	 * utilizar con el graficador de topología.
	 * 
	 * @param comboParsTopologia	<i>ComboBox</i> con los parámetros
	 **************************************************************************/
	public void defComboParsTopologia(JComboBox comboParsTopologia) {
		this.comboParsTopologia = comboParsTopologia;
	}

	/***************************************************************************
	 * Define el botón para importar una imágen de fondo a utlizar en el
	 * graficador de topología.
	 * 
	 * @param botonImportarFondo	Botón para importar fondo
	 **************************************************************************/
	public void defBotonImportarFondo(JButton botonImportarFondo) {
		this.botonImportarFondo = botonImportarFondo;
	}

	/***************************************************************************
	 * Define el botón para exportar una imágen del estado actual del
	 * graficador de topología.
	 * 
	 * @param botonExportarTopologia	Botón para exportar una imágen
	 **************************************************************************/
	public void defBotonExportarTopologia(JButton botonExportarTopologia) {
		this.botonExportarTopologia = botonExportarTopologia;
	}
	
	/***************************************************************************
	 * Define la tabla de eventos a utilizar por el <i>tab</i> de eventos.
	 * 
	 * @param tabla
	 * @param modelo
	 **************************************************************************/
	public void defTablaEventos(Tabla tabla, DefaultTableModel modelo) {
		tablaEventos = tabla;
		modeloEventos = modelo;
		
		modeloEventos = new DefaultTableModel();
		TableSorter ordenador = new TableSorter(modeloEventos);
		tablaEventos.setModel(ordenador);
		ordenador.setTableHeader(tablaEventos.getTableHeader());
		
		modeloEventos.addColumn("ID");
		modeloEventos.addColumn("Nombre");
		modeloEventos.addColumn("Criterio");
		modeloEventos.addColumn("Listo");
		modeloEventos.addColumn("NID");
		modeloEventos.addColumn("Detectado el");
		
		tablaEventos.getTableHeader().setBackground(new Color(0xe1e6ec));
		tablaEventos.getTableHeader().setReorderingAllowed(false);
		
		tablaEventos.getColumnModel().getColumn(0).setCellRenderer(
				new TablaCeldaRenderer(SwingConstants.CENTER, true));
		for (int i = 1; i < tablaEventos.getColumnCount(); i++)
			tablaEventos.getColumnModel().getColumn(i).setCellRenderer(
					new TablaCeldaRenderer(SwingConstants.CENTER, false));
		tablaEventos.getColumnModel().getColumn(3).setCellRenderer(
				new TablaCeldaRenderer(SwingConstants.CENTER, true));
		
		tablaEventos.getColumnModel().getColumn(0).setPreferredWidth(42);
		tablaEventos.getColumnModel().getColumn(0).setMinWidth(42);
		tablaEventos.getColumnModel().getColumn(2).setPreferredWidth(130);
		tablaEventos.getColumnModel().getColumn(2).setMinWidth(130);
		tablaEventos.getColumnModel().getColumn(3).setPreferredWidth(42);
		tablaEventos.getColumnModel().getColumn(3).setMinWidth(42);
		tablaEventos.getColumnModel().getColumn(4).setPreferredWidth(42);
		tablaEventos.getColumnModel().getColumn(4).setMinWidth(42);
		tablaEventos.getColumnModel().getColumn(5).setPreferredWidth(130);
		tablaEventos.getColumnModel().getColumn(5).setMinWidth(130);
		
		((TableSorter)tablaEventos.getModel()).setSortingStatus(
				0, TableSorter.DESCENDING);
	}
	
	/***************************************************************************
	 * Define la tabla de mantenimiento a utilizar por el <i>tab</i>
	 * de mantenimiento.
	 * 
	 * @param tabla
	 * @param modelo
	 **************************************************************************/
	public void defTablaMantenimiento(Tabla tabla, DefaultTableModel modelo) {
		tablaMantenimiento = tabla;
		modeloMantenimiento = modelo;
		
		modeloMantenimiento = new DefaultTableModel();
		TableSorter ordenador = new TableSorter(modeloMantenimiento);
		tablaMantenimiento.setModel(ordenador);
		ordenador.setTableHeader(tablaMantenimiento.getTableHeader());
		
		modeloMantenimiento.addColumn("ID");
		modeloMantenimiento.addColumn("Acción");
		modeloMantenimiento.addColumn("Valor");
		modeloMantenimiento.addColumn("NID");
		modeloMantenimiento.addColumn("Ejecutar el");
		modeloMantenimiento.addColumn("Ejecutada el");
		modeloMantenimiento.addColumn("Listo");
		modeloMantenimiento.addColumn("Repertir cada");
		
		tablaMantenimiento.getTableHeader().setBackground(new Color(0xe1e6ec));
		tablaMantenimiento.getTableHeader().setReorderingAllowed(false);
		
		tablaMantenimiento.getColumnModel().getColumn(0).setCellRenderer(
				new TablaCeldaRenderer(SwingConstants.CENTER, true));
		for (int i = 1; i < tablaMantenimiento.getColumnCount(); i++)
			tablaMantenimiento.getColumnModel().getColumn(i).setCellRenderer(
					new TablaCeldaRenderer(SwingConstants.CENTER, false));
		tablaMantenimiento.getColumnModel().getColumn(6).setCellRenderer(
				new TablaCeldaRenderer(SwingConstants.CENTER, true));
		
		int[] anchos = {42, 0, 0, 42, 130, 130, 42, 84};
		
		for (int i = 0; i < anchos.length; i++)
			if (anchos[i] != 0) {
				tablaMantenimiento.getColumnModel().getColumn(i).
						setPreferredWidth(anchos[i]);
				tablaMantenimiento.getColumnModel().getColumn(i).
						setMinWidth(anchos[i]);
			}
	
		((TableSorter)tablaMantenimiento.getModel()).setSortingStatus(
				4, TableSorter.DESCENDING);
	}
	
	//--------------------------------------------------------------------------
	//
	//   FUNCIONES PARA REALIZAR LA CONEXIÓN CON EL SERVIDOR DE SERVICIOS.
	//
	//==========================================================================
	
	/***************************************************************************
	 * Realiza la conexión al servidor de servicios.
	 **************************************************************************/
	public void conectarServidor() {
		String url = (String)JOptionPane.showInputDialog(
				null, "Indique el URL del servidor de servicios de TinySOA:",
				"Conectar al servidor", JOptionPane.QUESTION_MESSAGE,
				null, null, urlServidor.trim());
		if (url == null) return;
		urlServidor = url;
		guardarConfiguracion();
		
		new Thread(){
			public void run() {
				try {
					barraProgreso.setVisible(true);
					Service modeloServicio = new ObjectServiceFactory().
							create(InfoServ.class);
					servicioInformacion = (InfoServ)new XFireProxyFactory().
							create(modeloServicio, urlServidor + "/InfoServ");	
					dialogoSeleccionRed.mostrar(servicioInformacion,
							barraProgreso);
				} catch (Exception e) {
					barraProgreso.setVisible(false);
					servicioInformacion = null;
					JOptionPane.showMessageDialog(null,
							"No se ha podido establecer comunicación con el " +
							"servidor.\nPor favor, compruebe que la " + 
							"dirección es correcta y vuelva a intentarlo.",
							"Problema de conexión", JOptionPane.ERROR_MESSAGE);
					conectarServidor();
					desactivarControles();
				}
			}
		}.start();
	}

	/***************************************************************************
	 * Crea y prepara el servicio client de red.
	 * 
	 * @param url	URL del servidor de servicios
	 **************************************************************************/
	public void crearServicioRed(String url) {
		this.urlServicioRed = url;
		new Thread(){
			public void run() {
				try {
					actualizacionOcupada = true;
					barraProgreso.setVisible(true);
					Service modeloServicio = new ObjectServiceFactory().
							create(RedServ.class);
					servicioRed = (RedServ)new XFireProxyFactory().
							create(modeloServicio, urlServicioRed);

					dialogoEvento = new DialogoEvento(
							ventana, servicioRed, barraProgreso);
					dialogoMantenimiento = new DialogoMantenimiento(
							ventana, servicioRed, barraProgreso);
					
					procesarNodos();
					procesarParametros();
									
					definirTiempos();
					activarControles();
					barraProgreso.setVisible(false);
					actualizacionOcupada = false;
					System.out.println("act crearServicioRed");
					actualizar();
				} catch (Exception e) {
					barraProgreso.setVisible(false);
					servicioRed = null;
					JOptionPane.showMessageDialog(null,
							"Hubo un error al comunicarse con el servicio de " +
							"red.", "Problema de comunicación",
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
					desactivarControles();
				}
			}}.start();
	}

	//--------------------------------------------------------------------------
	//
	//   FUNCIONES PARA ACTUALIZAR LA INTERFAZ CON DATOS.
	//
	//==========================================================================
	
	/***************************************************************************
	 * Actualiza los datos en base al <i>slider</i>.
	 **************************************************************************/
	public void actualizar() {
		
		new Thread() {
			public void run() {
				if (servicioRed == null) return;
				if (actualizacionOcupada) {
					System.out.println("intento de actualizar denegado");
					return;
				}
				actualizacionOcupada = true;
				bloquearActualizacion();
				
				actualizarTiempo();
				if (panelTabs.getSelectedIndex() == 0) actualizarTablaDatos();
				if (panelTabs.getSelectedIndex() == 1) actualizarGraficador();
				if (panelTabs.getSelectedIndex() == 2) actualizarTopologia();
				if (panelTabs.getSelectedIndex() == 3) actualizarTablaEventos();
				if (panelTabs.getSelectedIndex() == 4) actualizarTablaMant();
				
				desbloquearActualizacion();
				actualizacionOcupada = false;
			}
		}.start();
	}

	private void actualizarTodo() {
		definirTiempos();
		System.out.println("act actualizarTodo");
		actualizar();
	}

	/***************************************************************************
	 * Actualiza la tabla de datos.
	 **************************************************************************/
	private void actualizarTablaDatos() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					DateFormat formato4 = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Vector<Reading> lecturas = servicioRed.
							obtenerLecturasAlTiempo(formato4.format(tiempo),
									((RedNodoArbol)arbol.getModel().getRoot()).
									getChildCount() * 
									modeloDatos.getColumnCount() * 3);
	
					String[][] vacio = new String[modeloDatos.getRowCount()][];
					for (int i = 0; i < vacio.length; i++) {
						vacio[i] = new String[modeloDatos.getColumnCount()];
						for (int j = 0; j < vacio[i].length; j++)
							vacio[i][j] = "";
					}
					
					String tiempoMayor = "";
					
					Reading l;
					for (int i = 0; i < lecturas.size(); i++) {
						l = (Reading)lecturas.get(i);
						int renglon = buscarRenglon(l.getNid());
						int columna = modeloDatos.findColumn(l.getParameter());
						if (renglon >= 0) {
							if (vacio[renglon][columna].compareTo("") == 0) {
								modeloDatos.setValueAt(
										formatearLectura(l.getValue(),
										l.getParameter()), renglon, columna);
								vacio[renglon][columna] = formatearLectura(
										l.getValue(), l.getParameter());
							}
							if (vacio[renglon][1].compareTo("") == 0) {
								modeloDatos.setValueAt(
										l.getTime().substring(0,
										l.getTime().length() - 2),
										renglon, 1);
								vacio[renglon][1] =l.getTime().substring(0,
										l.getTime().length() - 2);
							}
							if (l.getTime().compareTo(tiempoMayor) > 0)
								tiempoMayor = l.getTime();
						}
					}
					
					for (int i = 0; i < modeloDatos.getRowCount(); i++)
						if (((String)modeloDatos.getValueAt(i, 1)) != null)
							if (((String)modeloDatos.getValueAt(i, 1)).
									compareTo(tiempoMayor) > 0)
								for (int j = 1;
									j < modeloDatos.getColumnCount(); j++)
									modeloDatos.setValueAt("", i, j);
					
				}
			});
		} catch (Exception e) { Errors.error(e, "actualizarTablaDatos"); }
	}

	/***************************************************************************
	 * Actualiza los nodos de la tabla de datos
	 **************************************************************************/
	@SuppressWarnings("unchecked")
	private void actualizarNodosTablaDatos() {
		int renglon; boolean modificado = false;
	
		if (arbol.getModel().getRoot() == null) return;
		
		Enumeration e = ((RedNodoArbol)arbol.getModel().getRoot()).children();
		while (e.hasMoreElements()) {
			RedNodoArbol el = (RedNodoArbol)e.nextElement();
			String id = el.toString().substring(5);
			boolean seleccionado = el.isSelected();
	
			renglon = buscarRenglon(Integer.parseInt(id));
			if ((renglon == -1) && (seleccionado)) {
				modificado = true;
				modeloDatos.addRow(new Object[]{id});
			}
			if ((renglon >= 0) && (!seleccionado)) {
				modificado = true;
				modeloDatos.removeRow(renglon);
			}
			tablaDatos.repaint();
		}
	
		if (modificado) {
			System.out.println("act actualizarNodosTablaDatos");
			actualizar();					
		}
	}

	/***************************************************************************
	 * Actualiza la grafica.
	 **************************************************************************/
	private void actualizarGraficador() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@SuppressWarnings("unchecked")
				public void run() {
					if (servicioRed == null) return;
					if (comboParsGraf.getSelectedItem() == null) return;
					
					String parametro = comboParsGraf.
							getSelectedItem().toString();
					
					DateFormat formato4 = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Vector<Reading> lecturas = servicioRed.obtenerLecturas(
							formato4.format(new Date(tiempo.getTime() -
									graficador.obtDif() - 1000)),
							formato4.format(tiempo), parametro, 0);
	
					Vector<Vector> datos = new Vector<Vector>();
					Vector<Reading> datosNodo = new Vector<Reading>();
					
					int nid = -1;
					if (lecturas.size() > 0)
						nid = lecturas.get(0).getNid();
							
					int max = 0;
					Enumeration e = ((RedNodoArbol)arbol.
							getModel().getRoot()).children();
					while (e.hasMoreElements()) {
						RedNodoArbol n = (RedNodoArbol)e.nextElement();
						int i = Integer.parseInt(n.toString().substring(5));
						if (i > max) max = i;
					}
					boolean[] nodoSel = new boolean[max + 1];
					e = ((RedNodoArbol)arbol.
							getModel().getRoot()).children();
					while (e.hasMoreElements()) {
						RedNodoArbol n = (RedNodoArbol)e.nextElement();
						nodoSel[Integer.parseInt(n.toString().substring(5))] =
							n.isSelected();
					}
					
					e = lecturas.elements();
					while (e.hasMoreElements()) {
						Reading l = (Reading)e.nextElement();
						if (nodoSel[l.getNid()])
						if (l.getParameter().compareTo(parametro) == 0) {
							if (l.getNid() != nid) {
								datos.add(datosNodo);
								
								//System.out.println(datosNodo);
								datosNodo = new Vector<Reading>();
								nid = l.getNid();
							}
							datosNodo.add(l);
						}
					}
					datos.add(datosNodo);
					
					graficador.defTiempo(tiempo.getTime());
					graficador.defDatos(datos);
				}
			});
			
			graficador.repaint();
		} catch (Exception e) { Errors.error(e, "actualizarGraficador"); }
	}

	/***************************************************************************
	 * Actualiza la gráfica de topología.
	 **************************************************************************/
	private void actualizarTopologia() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@SuppressWarnings("unchecked")
				public void run() {
					DateFormat formato4 = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Vector<Reading> lecturas = servicioRed.
							obtenerLecturasAlTiempo(formato4.format(tiempo),
									((RedNodoArbol)arbol.getModel().getRoot()).
									getChildCount() * 
									modeloDatos.getColumnCount() * 3);
					
					graficadorTopologia.eliminarTodosNodos();
					
					int max = 0;
					Enumeration e = ((RedNodoArbol)arbol.
							getModel().getRoot()).children();
					while (e.hasMoreElements()) {
						RedNodoArbol n = (RedNodoArbol)e.nextElement();
						int nid = Integer.parseInt(n.toString().substring(5));
						if (nid > max) max = nid;
					}
					boolean[] nodoSel = new boolean[max + 1];
					e = ((RedNodoArbol)arbol.
							getModel().getRoot()).children();
					while (e.hasMoreElements()) {
						RedNodoArbol n = (RedNodoArbol)e.nextElement();
						nodoSel[Integer.parseInt(n.toString().substring(5))] =
							n.isSelected();
					}
					
					e = lecturas.elements();
					String par = comboParsTopologia.
							getSelectedItem().toString();
					
					if (par.compareTo("Temp") == 0) {
						graficadorTopologia.defEscala(0.0d, 35.0d);
						graficadorTopologia.defTipoEscala(
								GraficadorTopologia.ESCALA_CALOR);
					} else if (par.compareTo("Luz") == 0) {
						graficadorTopologia.defEscala(300.0d, 900.0d);
						graficadorTopologia.defTipoEscala(
								GraficadorTopologia.ESCALA_LUZ);
					} else if (par.compareTo("Volt") == 0) {
						graficadorTopologia.defEscala(1.5d, 3.0d);
						graficadorTopologia.defTipoEscala(
								GraficadorTopologia.ESCALA_ENERGIA);
					} else {
						graficadorTopologia.defEscala(300.0d, 1024.0d);
						graficadorTopologia.defTipoEscala(
								GraficadorTopologia.ESCALA_CALOR);
					}
					
					
					while (e.hasMoreElements()) {
						Reading l = (Reading)e.nextElement();
						if (par.compareTo(l.getParameter()) == 0)
							if (!graficadorTopologia.existeNodo(l.getNid()))
								if (nodoSel[l.getNid()]) {
									NodoGraficaTopologia ngt =
										new NodoGraficaTopologia(l.getNid(),
												Double.parseDouble(
														l.getValue()),
												posTopNodos[l.getNid()].x,
												posTopNodos[l.getNid()].y);
									graficadorTopologia.agregarNodo(ngt);
								}
					}
					
					graficadorTopologia.repaint();
				}
			});
		} catch (Exception e) { Errors.error(e, "actualizarTopologia"); }
	}
	
	/***************************************************************************
	 * Actualiza la tabla de eventos.
	 **************************************************************************/
	private void actualizarTablaEventos() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@SuppressWarnings("unchecked")
				public void run() {
					Vector<Event> eventos = servicioRed.
							obtenerListadoEventos(0);
					
					String id, nombre, criterio, listo, nid, tiempo;
					
					tablaEventos.clearTable();
					Enumeration e = eventos.elements();
					while (e.hasMoreElements()) {
						Event evento = (Event)e.nextElement();
						
						id = evento.getId() + "";
						nombre = evento.getName();
						criterio = evento.getCriteria();
						if (evento.getReady()) listo = "Sí";
						else listo = "No";
						if (evento.getNid() == 0) nid = "-";
						else nid = evento.getNid() + "";
						if (evento.getTime() == null)
							tiempo = "En espera";
						else if (evento.getTime().compareTo("") == 0)
							tiempo = "En espera";
						else tiempo = evento.getTime();
						
						modeloEventos.addRow(new Object[]{id, nombre,
								new CeldaTablaTooltip(criterio),
								listo, nid, tiempo});
						
						dialogoMantenimiento.defEventos(eventos.toArray());
					}
				}
			});
		} catch (Exception e) { Errors.error(e, "actualizarTablaEventos"); }
	}
	
	/***************************************************************************
	 * Actualiza la tabla de mantenimiento.
	 **************************************************************************/
	private void actualizarTablaMant() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@SuppressWarnings("unchecked")
				public void run() {
					Vector<Task> tasks = servicioRed.
							obtenerListadoTareas(0);
					
					String id, accion, valor, nid, tiempo,
						ejecutada, listo, repetir;

					tablaMantenimiento.clearTable();
					Enumeration e = tasks.elements();
					while (e.hasMoreElements()) {
						Task task = (Task)e.nextElement();
						
						id = task.getId() + "";
						
						accion = "";
						if (task.getType() ==
							Constants.TYPE_ACTUATOR_START)
							accion = "Enc. Act.";
						if (task.getType() ==
							Constants.TYPE_ACTUATOR_STOP)
							accion = "Apa. Act.";
						if (task.getType() ==
							Constants.TYPE_CHANGE_DATA_RATE)
							accion = "Cam. Tasa";
						if (task.getType() ==
							Constants.TYPE_SLEEP)
							accion = "Entrar Esp.";
						if (task.getType() ==
							Constants.TYPE_WAKEUP)
							accion = "Salir Esp.";
						
						valor = "-";
						if (task.getValue() > 0)
							valor = task.getValue() + " segs";
						if (task.getValue() ==
							Constants.ACTUATOR_BUZZER)
							valor = "Bocina";
						if (task.getValue() ==
							Constants.ACTUATOR_LED_YELLOW)
							valor = "Led Ama.";
						if (task.getValue() ==
							Constants.ACTUATOR_LED_RED)
							valor = "Led Rojo";
						if (task.getValue() ==
							Constants.ACTUATOR_LED_GREEN)
							valor = "Led Ver.";
						
						nid = "-";
						if (task.getNid() > 0)
							nid = task.getNid() + "";
						
						tiempo = task.getTime();
						tiempo = tiempo.substring(0, tiempo.length() - 2);
						if (task.getEvent() > 0)
							tiempo = "Esperando evento " + task.getEvent();
						
						ejecutada = "-";
						if (task.getExecuted() != null)
							if (task.getExecuted().compareTo("") != 0)
								ejecutada = task.getExecuted();
						
						listo = "No";
						if (task.getDone())
							listo = "Sí";
						if (task.getRepeat() > 0)
							listo = "-";
						
						repetir = "-";
						if (task.getRepeat() > 0)
							repetir = task.getRepeat() + " min";

						modeloMantenimiento.addRow(new Object[]{
								id, accion, valor, nid, tiempo,
								ejecutada, listo, repetir});
					}
				}
			});
		} catch (Exception e) { Errors.error(e, "actualizarTablaEventos"); }
	}

	/***************************************************************************
	 * Actualiza la etiqueta del <i>slider</i> y el tiempo actual.
	 **************************************************************************/
	private void actualizarTiempo() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					double factorTiempo = slider.getValue() / 10000.0d;
					tiempo = new Date(Math.round(tiempoMinimo *
							(1.0d - factorTiempo) + tiempoMaximo *
							(factorTiempo)));
					DateFormat formato2 = new SimpleDateFormat("dd/MM/yyyy");
					DateFormat formato3 = new SimpleDateFormat("HH:mm:ss");
					etiquetaSlider.setText("<html><center>" +
							formato2.format(tiempo) +
							"<br><b>" + formato3.format(tiempo) +
							"</b></center></html>");
				}
			});
		} catch (Exception e) { Errors.error(e, "actualizarTiempo"); }
	}

	/***************************************************************************
	 * Define los tiempos y escalas del <i>slider</i>.
	 **************************************************************************/
	private void definirTiempos() {
		try {
			DateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date inicio = (Date)formato.parse(
					servicioRed.obtenerTiempoMinimo());
			Object res = servicioRed.obtenerTiempoMaximo();
			System.out.println(res);
			Date fin = (Date)formato.parse(res.toString());
			tiempoMinimo = inicio.getTime();
			tiempoMaximo = fin.getTime();
			slider.setValue(10000);
		} catch (Exception e) { Errors.error(e, "definirTiempos"); }
	}
	
	//--------------------------------------------------------------------------
	//
	//   FUNCIONES PARA PREPARAR Y CONFIGURAR LOS COMPONENTES DE LA INTERFAZ.
	//
	//==========================================================================

	/***************************************************************************
	 * Procesa los nodos de la red.
	 **************************************************************************/
	private void procesarNodos() {
		Vector<Node> nodos = servicioRed.obtenerListadoNodos();
		crearArbolNodos(nodos.toArray());
		crearTabla(nodos.toArray());
		crearTopologiaNodos(nodos.toArray());
		dialogoMantenimiento.defNodos(nodos.toArray());
	}

	/***************************************************************************
	 * Procesa los parámetros de la red.
	 **************************************************************************/
	private void procesarParametros() {
		Vector<Parameter> parameters = servicioRed.obtenerParametros();
		comboParsGraf.removeAllItems();
		comboParsTopologia.removeAllItems();
		for (int i = 0; i < parameters.size(); i++) {
			comboParsGraf.addItem(parameters.get(i).getName());
			comboParsTopologia.addItem(parameters.get(i).getName());
		}
		if (comboParsGraf.getItemCount() > 2) {
			comboParsGraf.setSelectedIndex(
					comboParsGraf.getItemCount() - 2);
			comboParsTopologia.setSelectedIndex(
					comboParsTopologia.getItemCount() - 2);
		}
	}

	/***************************************************************************
	 * Crea el arbol de nodos.
	 * 
	 * @param nodos	Un arreglo con el nombre de los nodos
	 **************************************************************************/
	private void crearArbolNodos(Object[] nodos) {
		RedNodoArbol top = new RedNodoArbol(
				servicioRed.obtenerNombreRed(), iconoRed);
		((DefaultTreeModel)arbol.getModel()).setRoot(top);	
		RedNodoArbol c = null;
		for (int i = 0; i < nodos.length; i++) {
			c = new RedNodoArbol("Nodo " + ((Node)nodos[i]).getId(),
					iconoEstadoNormal);
			top.add(c);
		}
		arbol.expandRow(0);
	}

	/***************************************************************************
	 * Crea la tabla de datos.
	 * 
	 * @param nodos	Un arreglo con el ID de los nodos
	 **************************************************************************/
	private void crearTabla(Object[] nodos) {
		Vector<Parameter> parameters = servicioRed.obtenerParametros();
		Object[] parametrosCols = new Object[parameters.size() + 2];
		parametrosCols[0] = "ID";
		parametrosCols[1] = "Tiempo";
		for (int i = 0; i < parameters.size(); i++)
			parametrosCols[i + 2] = parameters.get(i).getName();
		
		modeloDatos = new DefaultTableModel();
		TableSorter ordenador = new TableSorter(modeloDatos);
		tablaDatos.setModel(ordenador);
		ordenador.setTableHeader(tablaDatos.getTableHeader());
		
		for (int i = 0; i < parametrosCols.length; i++)
			modeloDatos.addColumn(parametrosCols[i]);
		
		tablaDatos.getTableHeader().setBackground(new Color(0xe1e6ec));
		tablaDatos.getTableHeader().setReorderingAllowed(false);
		
		tablaDatos.getColumnModel().getColumn(0).setCellRenderer(
				new TablaCeldaRenderer(SwingConstants.CENTER, true));
		for (int i = 1; i < tablaDatos.getColumnCount(); i++)
			tablaDatos.getColumnModel().getColumn(i).setCellRenderer(
					new TablaCeldaRenderer(SwingConstants.CENTER, false));
				
		for (int i = 0; i < nodos.length; i++)
			modeloDatos.addRow(new Object[]{((Node)nodos[i]).getId()});
		
		tablaDatos.getColumnModel().getColumn(0).setPreferredWidth(42);
		tablaDatos.getColumnModel().getColumn(0).setMinWidth(42);
		tablaDatos.getColumnModel().getColumn(1).setPreferredWidth(130);
		tablaDatos.getColumnModel().getColumn(1).setMinWidth(130);
		
		((TableSorter)tablaDatos.getModel()).setSortingStatus(
				0, TableSorter.ASCENDING);
	}

	/***************************************************************************
	 * Crea los nodos de la grafica de topología.
	 * 
	 * @param nodos	Un arreglo con el ID de los nodos
	 **************************************************************************/
	private void crearTopologiaNodos(Object[] nodos) {
		int max = 0, ancho, alto, x, y;
		for (int i = 0; i < nodos.length; i++)
			if (((Node)nodos[i]).getId() > max)
				max = ((Node)nodos[i]).getId();
		
		posTopNodos = new Point[max + 1];
		
		for (int i = 0; i < nodos.length; i++) {
			ancho = graficadorTopologia.getWidth();
			alto = graficadorTopologia.getHeight();
			x = (int)Math.round(Math.random() * ancho);
			y = (int)Math.round(Math.random() * alto);			
			posTopNodos[((Node)nodos[i]).getId()] = new Point(x, y);
		}
		
		graficadorTopologia.defPosNodos(posTopNodos);
	}

	//--------------------------------------------------------------------------
	//
	//   FUNCIONES PARA BLOQUEAR Y/O DESBLOQUEAR COMPONENTES EN LA INTERFAZ.
	//
	//==========================================================================
	
	/***************************************************************************
	 * Desbloquea la actualización y muestra la barra de progreso.
	 **************************************************************************/
	private void desbloquearActualizacion() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					barraProgreso.setVisible(false);
				}
			});
		} catch (Exception e) { Errors.error(e, "desbloquearActualizacion"); }
	}

	/***************************************************************************
	 * Bloquea la actualización y muestra la barra de progreso.
	 **************************************************************************/
	private void bloquearActualizacion() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					barraProgreso.setVisible(true);
				}
			});
		} catch (Exception e) { Errors.error(e, "bloquearActualizacion"); }
	}

	/***************************************************************************
	 * Activa todos los controles.
	 **************************************************************************/
	public void activarControles() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				slider.setEnabled(true);
				actualizarCheck.setEnabled(true);
				botonBarraAtrasar.setEnabled(true);
				botonBarraReproducir.setEnabled(true);
				botonBarraAdelantar.setEnabled(true);
				menuInicio.setEnabled(true);
				menuReproducir.setEnabled(true);
				menuFinal.setEnabled(true);
				botonBarraActualizar.setEnabled(true);
				menuActualizar.setEnabled(true);
				panelTabs.setEnabled(true);		
				comboParsGraf.setEnabled(true);
				comboParsTopologia.setEnabled(true);
	
				actualizarCheck.setSelected(false);
			}
		});
	}

	/***************************************************************************
	 * Desactiva todos los controles.
	 **************************************************************************/
	public void desactivarControles() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				slider.setEnabled(false);
				actualizarCheck.setEnabled(false);
				botonBarraAtrasar.setEnabled(false);
				botonBarraReproducir.setEnabled(false);
				botonBarraAdelantar.setEnabled(false);
				menuInicio.setEnabled(false);
				menuReproducir.setEnabled(false);
				menuFinal.setEnabled(false);
				botonBarraActualizar.setEnabled(false);
				menuActualizar.setEnabled(false);
				panelTabs.setEnabled(false);
				comboParsGraf.setEnabled(false);
				comboParsTopologia.setEnabled(false);
			}
		});
	}

	/***************************************************************************
	 * Activa/desactiva controles en preparación para la actualización en vivo.
	 **************************************************************************/
	private void prepararActualizacionVivo() {
		boolean enVivo = actualizarCheck.isSelected();
		if (enVivo) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					rebobinarTimer.stop();
					adelantarTimer.stop();
					reproduccionTimer.stop();
					botonBarraAtrasar.setEnabled(false);
					botonBarraReproducir.setEnabled(false);
					botonBarraAdelantar.setEnabled(false);
					botonExportarGrafica.setEnabled(false);
					botonExportarTopologia.setEnabled(false);
					botonImportarFondo.setEnabled(false);
					menuInicio.setEnabled(false);
					menuReproducir.setEnabled(false);
					menuFinal.setEnabled(false);
					slider.setEnabled(false);
					graficadorTopologia.setEnabled(false);
					tablaEventos.setEnabled(false);
					
					actualizarVivo.restart();
				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					botonBarraAtrasar.setEnabled(true);
					botonBarraReproducir.setEnabled(true);
					botonBarraAdelantar.setEnabled(true);
					botonExportarGrafica.setEnabled(true);
					botonExportarTopologia.setEnabled(true);
					botonImportarFondo.setEnabled(true);
					menuInicio.setEnabled(true);
					menuReproducir.setEnabled(true);
					menuFinal.setEnabled(true);
					slider.setEnabled(true);
					graficadorTopologia.setEnabled(true);
					tablaEventos.setEnabled(true);
					
					actualizarVivo.stop();
				}
			});
		}
	}

	/***************************************************************************
	 * Activa/desactiva controles en preparación para la reproducción/pausa.
	 **************************************************************************/
	private void reproducirPausar() {
		if (reproduccionTimer.isRunning()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					botonBarraAtrasar.setEnabled(true);
					botonBarraAdelantar.setEnabled(true);
					botonBarraReproducir.setToolTipText("Reproducir");
					botonBarraReproducir.setIcon(iconoReproduccion);
					menuReproducir.setText("Reproducir");
					menuReproducir.setIcon(iconoReproduccion2);
					actualizarCheck.setEnabled(true);
					menuActualizar.setEnabled(true);
					botonBarraActualizar.setEnabled(true);
					botonExportarGrafica.setEnabled(true);
					botonExportarTopologia.setEnabled(true);
					botonImportarFondo.setEnabled(true);
					graficadorTopologia.setEnabled(true);
					tablaEventos.setEnabled(true);
					
					reproduccionTimer.stop();
				}
			});
			
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					botonBarraAtrasar.setEnabled(false);
					botonBarraAdelantar.setEnabled(false);
					botonBarraReproducir.setToolTipText("Pausar");
					botonBarraReproducir.setIcon(iconoPausa);
					menuReproducir.setText("Pausar");
					menuReproducir.setIcon(iconoPausa2);
					actualizarCheck.setEnabled(false);
					menuActualizar.setEnabled(false);
					botonBarraActualizar.setEnabled(false);
					botonExportarGrafica.setEnabled(false);
					botonExportarTopologia.setEnabled(false);
					botonImportarFondo.setEnabled(false);
					graficadorTopologia.setEnabled(false);
					tablaEventos.setEnabled(false);
					
					reproduccionTimer.restart();
				}
			});
		}
	}

	//--------------------------------------------------------------------------
	//
	//   FUNCIONES PARA CARGAR LA CONFIGURACIÓN DEL SISTEMA.
	//
	//==========================================================================
	
	/***************************************************************************
	 * Carga la configuración del visor.
	 **************************************************************************/
	private void cargarConfiguracion() {
		Properties p = new Properties();
		try {
			p.loadFromXML(new FileInputStream(archivoConfiguracion));
			urlServidor = p.getProperty("visor.servidor");
		} catch (Exception e) {
			urlServidor = "http://localhost:8080";
			guardarConfiguracion();
		}
		
		if (urlServidor == null) {
			urlServidor = "http://localhost:8080";
			guardarConfiguracion();
		}
	}
	
	/***************************************************************************
	 * Guarda la configuración del visor.
	 **************************************************************************/
	private void guardarConfiguracion() {
		Properties p = new Properties();
		try {
			p.loadFromXML(new FileInputStream(archivoConfiguracion));
			p.setProperty("visor.servidor", urlServidor);
			p.storeToXML(new FileOutputStream(archivoConfiguracion), null);
		} catch (Exception e) {}
	}
	
	//--------------------------------------------------------------------------
	//
	//   FUNCIONES PARA IMPORTAR Y EXPORTAR IMÁGENES.
	//
	//==========================================================================
	
	/***************************************************************************
	 * Presenta un diálogo y exporta una imágen con el estado actual del
	 * graficador.
	 **************************************************************************/
	private void exportarGrafica() {
		JFileChooser dialogo = new JFileChooser();
		
		dialogo.addChoosableFileFilter(
				new FiltroArchivos(new String[]{"png"}, "Imagen PNG"));
		dialogo.addChoosableFileFilter(
				new FiltroArchivos(new String[]{"bmp"}, "Imagen BMP"));
		//dialogo.addChoosableFileFilter(
		//		new FiltroArchivos(new String[]{"gif"}, "Imagen GIF"));
		dialogo.addChoosableFileFilter(
				new FiltroArchivos(new String[]{"jpg", "jpeg", "jpe"},
						"Imagen JPG"));
		dialogo.setAcceptAllFileFilterUsed(false);
		
		graficador.defProgreso(barraProgreso);
		
		int res = dialogo.showSaveDialog(null);
		if (res == JFileChooser.APPROVE_OPTION) {
			String a = dialogo.getSelectedFile().getAbsoluteFile().toString();
			String f = dialogo.getFileFilter().getDescription().
					substring(7, 10).toLowerCase();
			if (a.substring(a.length() - 4, a.length() -3).compareTo(".") != 0)
				a += "." + f;
			graficador.guardarImagen(a, f);
		}
		
	}
	
	/***************************************************************************
	 * Presenta un diálogo y define la imágen de fondo del graficador de
	 * topología.
	 **************************************************************************/
	private void importarImagenFondo() {
		new Thread() {
			public void run() {
				JFileChooser dialogo = new JFileChooser();
		
				dialogo.addChoosableFileFilter(
						new FiltroArchivos(new String[]{"png"}, "Imagen PNG"));
				dialogo.addChoosableFileFilter(
						new FiltroArchivos(new String[]{"bmp"}, "Imagen BMP"));
				//dialogo.addChoosableFileFilter(
				//		new FiltroArchivos(new String[]{"gif"}, "Imagen GIF"));
				dialogo.addChoosableFileFilter(
						new FiltroArchivos(new String[]{"jpg", "jpeg", "jpe"},
								"Imagen JPG"));
				dialogo.setAcceptAllFileFilterUsed(false);
		
				int res = dialogo.showOpenDialog(null);
				if (res == JFileChooser.APPROVE_OPTION) {
					String archivo = dialogo.getSelectedFile().
							getAbsoluteFile().toString();
			
					Image image = Toolkit.getDefaultToolkit().
							createImage(archivo);
					if (image != null) {
						BufferedImage bImage = toBufferedImage(image);
						graficadorTopologia.defFondo(bImage);
						graficadorTopologia.repaint();
					}
				}
			}
		}.start();
	}

	/***************************************************************************
	 * Presenta un diálogo y exporta una imágen con el estado actual del
	 * graficador de topología.
	 **************************************************************************/
	private void exportarGraficaTopologia() {
		JFileChooser dialogo = new JFileChooser();
		
		dialogo.addChoosableFileFilter(
				new FiltroArchivos(new String[]{"png"}, "Imagen PNG"));
		dialogo.addChoosableFileFilter(
				new FiltroArchivos(new String[]{"bmp"}, "Imagen BMP"));
		//dialogo.addChoosableFileFilter(
		//		new FiltroArchivos(new String[]{"gif"}, "Imagen GIF"));
		dialogo.addChoosableFileFilter(
				new FiltroArchivos(new String[]{"jpg", "jpeg", "jpe"},
						"Imagen JPG"));
		dialogo.setAcceptAllFileFilterUsed(false);
		
		graficador.defProgreso(barraProgreso);
		
		int res = dialogo.showSaveDialog(null);
		if (res == JFileChooser.APPROVE_OPTION) {
			String a = dialogo.getSelectedFile().getAbsoluteFile().toString();
			String f = dialogo.getFileFilter().getDescription().
					substring(7, 10).toLowerCase();
			if (a.substring(a.length() - 4, a.length() -3).compareTo(".") != 0)
				a += "." + f;
			graficadorTopologia.guardarImagen(a, f);
		}
		
	}

	/***************************************************************************
	 * Función para verificar si una imagen es transparente.
	 * 
	 * @param imagen	Imagen a revisar
	 * @return			Verdadero si la imagen es transparente
	 **************************************************************************/
	private boolean esTransparente(Image imagen) {
	    if (imagen instanceof BufferedImage) {
	        BufferedImage bimage = (BufferedImage)imagen;
	        return bimage.getColorModel().hasAlpha();
	    }
	
	     PixelGrabber pg = new PixelGrabber(imagen, 0, 0, 1, 1, false);
	    try {
	        pg.grabPixels();
	    } catch (InterruptedException e) {}
	
	    ColorModel cm = pg.getColorModel();
	    return cm.hasAlpha();
	}
	
	/***************************************************************************
	 * Función para transformar una <code>Image</code> a una
	 * <code>BufferedImage</code>.
	 * 
	 * @param imagen	Imagen a convertir
	 * @return			Una versión <code>BufferedImage</code> de
	 * 					<code>imagen</code>
	 **************************************************************************/
	private BufferedImage toBufferedImage(Image imagen) {
	    if (imagen instanceof BufferedImage) {
	        return (BufferedImage)imagen;
	    }
	
	    imagen = new ImageIcon(imagen).getImage();	
	    boolean hasAlpha = esTransparente(imagen);
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
	            imagen.getWidth(null), imagen.getHeight(null), transparency);
	    } catch (HeadlessException e) {}
	
	    if (bimage == null) {
	        int type = BufferedImage.TYPE_INT_RGB;
	        if (hasAlpha) {
	            type = BufferedImage.TYPE_INT_ARGB;
	        }
	        bimage = new BufferedImage(imagen.getWidth(null), imagen.getHeight(null), type);
	    }
	
	    Graphics g = bimage.createGraphics();
	    g.drawImage(imagen, 0, 0, null);
	    g.dispose();
	
	    return bimage;
	}
	
	//--------------------------------------------------------------------------
	//
	//   FUNCIONES PARA MANEJAR EVENTOS MAYORES.
	//
	//==========================================================================
	
	/***************************************************************************
	 * Prepara y muestra el diálogo de agregar evento.
	 **************************************************************************/
	private void agregarEvento() {
		dialogoEvento.setTitle("Agregar Evento");
		dialogoEvento.defValores(0, "", "");
		dialogoEvento.mostrar();
		actualizar();
	}
	
	/***************************************************************************
	 * Prepara y muestra el diálogo de modificar evento.
	 **************************************************************************/
	private void modificarEvento() {
		new Thread() {
			public void run() {
				int i = tablaEventos.getSelectedRow();
				if (i == -1) {
					JOptionPane.showMessageDialog(
							ventana, "Seleccione el evento a modificar.",
							"Problema", JOptionPane.WARNING_MESSAGE);
					return;
				}

				int id = Integer.parseInt(
						tablaEventos.getValueAt(i, 0).toString());
				
				actualizacionOcupada = true;
				Event e = servicioRed.obtenerEventoPorId(id);
				actualizacionOcupada = false;
				
				dialogoEvento.setTitle("Modificar Evento");
				dialogoEvento.defValores(id, e.getName(), e.getCriteria());
				dialogoEvento.mostrar();
				actualizar();
			}
		}.start();
	}
	
	/***************************************************************************
	 * Elimina el evento seleccionado en la tabla de eventos.
	 **************************************************************************/
	private void eliminarEvento() {
		new Thread() {
			public void run() {
				int i = tablaEventos.getSelectedRow();
				if (i == -1) {
					JOptionPane.showMessageDialog(
							ventana, "Seleccione el evento a eliminar.",
							"Problema", JOptionPane.WARNING_MESSAGE);
					return;
				}

				int id = Integer.parseInt(
						tablaEventos.getValueAt(i, 0).toString());
				String nombre = tablaEventos.getValueAt(i, 1).toString();
				
				int res = JOptionPane.showConfirmDialog(
						ventana, "¿Desea eliminar el evento " + nombre + "?",
						"Eliminar Evento", JOptionPane.YES_NO_OPTION);
				
				if (res == JOptionPane.NO_OPTION) return;
				if (res == -1) return;
				
				actualizacionOcupada = true;
				servicioRed.eliminarEvento(id);
				actualizacionOcupada = false;
				actualizar();
			}
		}.start();
	}
	
	/***************************************************************************
	 * Prepara y muestra el diálogo de agregar tarea de mantenimiento.
	 **************************************************************************/
	private void agregarTareaMantenimiento() {
		dialogoMantenimiento.mostrarAgregar();
		actualizar();
	}
	
	/***************************************************************************
	 * Prepara y muestra el diálogo de modificar tarea de mantenimiento.
	 **************************************************************************/
	private void modificarTareaMantenimiento() {
		new Thread() {
			public void run() {
				int i = tablaMantenimiento.getSelectedRow();
				if (i == -1) {
					JOptionPane.showMessageDialog(
							ventana, "Seleccione la tarea a modificar.",
							"Problema", JOptionPane.WARNING_MESSAGE);
					return;
				}

				int id = Integer.parseInt(
						tablaMantenimiento.getValueAt(i, 0).toString());
				
				dialogoMantenimiento.mostrarModificar(id);
				actualizar();
			}
		}.start();
	}
	
	/***************************************************************************
	 * Elimina la tarea seleccionada en la tabla de mantenimiento.
	 **************************************************************************/
	private void eliminarTareaMantenimiento() {
		new Thread() {
			public void run() {
				int i = tablaMantenimiento.getSelectedRow();
				if (i == -1) {
					JOptionPane.showMessageDialog(
							ventana, "Seleccione la tarea a eliminar.",
							"Problema", JOptionPane.WARNING_MESSAGE);
					return;
				}

				int id = Integer.parseInt(
						tablaMantenimiento.getValueAt(i, 0).toString());
				
				int res = JOptionPane.showConfirmDialog(
						ventana, "¿Desea eliminar la tarea con ID " + id + "?",
						"Eliminar Task", JOptionPane.YES_NO_OPTION);
				
				if (res == JOptionPane.NO_OPTION) return;
				if (res == -1) return;
				
				actualizacionOcupada = true;
				servicioRed.eliminarTarea(id);
				actualizacionOcupada = false;
				actualizar();
			}
		}.start();
	}
	
	//--------------------------------------------------------------------------
	//
	//   FUNCIONES VARIAS.
	//
	//==========================================================================

	/***************************************************************************
	 * Busca el renglón correspondiente al nodo nid
	 * 
	 * @param nid 	Nodo a buscar
	 * @return		El índice del renglón donde se encuentra el nodo indicado.
	 **************************************************************************/
	private int buscarRenglon(int nid) {
		int renglon = -1;
		for (int i = 0; i < modeloDatos.getRowCount(); i++)
			if (modeloDatos.getValueAt(i, 0).
					toString().compareTo(nid + "") == 0)
				renglon = i;
		return renglon;
	}

	/***************************************************************************
	 * Convierte el valor de un parámetro de lectura a una cadena.
	 * 
	 * @param valor		Valor del parámetro
	 * @param parametro	Tipo del parámetro
	 * @return				Una cadena con el valor formateado
	 **************************************************************************/
	private String formatearLectura(String valor, String parametro) {
		NumberFormat nf0 = new DecimalFormat("0");
		NumberFormat nf1 = new DecimalFormat("0.0");
		NumberFormat nf2 = new DecimalFormat("0.00");
		if (parametro.compareTo("Temp") == 0)
			return nf1.format(Double.parseDouble(valor)) + " °C";
		else if (parametro.compareTo("TmIn") == 0)
			return nf1.format(Double.parseDouble(valor)) + " °C";
		else if (parametro.compareTo("Hum") == 0)
			return nf1.format(Double.parseDouble(valor)) + " %";
		else if (parametro.compareTo("Pres") == 0)
			return nf1.format(Double.parseDouble(valor)) + " mba";
		else if (parametro.compareTo("AceX") == 0)
			return nf2.format(Double.parseDouble(valor)) + " g";
		else if (parametro.compareTo("AceY") == 0)
			return nf2.format(Double.parseDouble(valor)) + " g";
		else if (parametro.compareTo("Volt") == 0)
			return nf2.format(Double.parseDouble(valor)) + " V";
		else if (parametro.compareTo("Luz") == 0)
			return nf1.format(Double.parseDouble(valor)) + " Lux";
		else return nf0.format(Double.parseDouble(valor));
	}
	
	//--------------------------------------------------------------------------
	//
	//   FUNCIONES PARA MANEJAR LOS EVENTOS.
	//
	//==========================================================================

	/***************************************************************************
	 * Función para procesar una acción realizada.
	 * 
	 * @param evt	Evento generador de la acción
	 **************************************************************************/
	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();
		if (cmd.compareTo("") == 0)
			cmd = ((JButton)evt.getSource()).getToolTipText();
		
		if (cmd.compareTo("Salir") == 0) {
			System.exit(0);
		} else if (cmd.compareTo("Conectar al servidor...") == 0) {
			conectarServidor();
		} else if (cmd.compareTo("Seleccionar red...") == 0) {
			dialogoSeleccionRed.mostrar(servicioInformacion, barraProgreso);
		} else if (cmd.compareTo("Ir al inicio") == 0) {
			slider.setValue(0);
			System.out.println("act Ir al inicio");
			actualizar();
		} else if (cmd.compareTo("Ir al final") == 0) {
			slider.setValue(10000);
			System.out.println("act Ir al final");
			actualizar();
		} else if (cmd.compareTo("Actualizar") == 0) {
			prepararActualizacionVivo();
		} else if (cmd.compareTo("Reproducir") == 0) {
			reproducirPausar();
		} else if (cmd.compareTo("Pausar") == 0) {
			reproducirPausar();
		} else if (cmd.compareTo("Actualizar todo") == 0) {
			actualizarTodo();
		} else if (cmd.compareTo("Alejar") == 0) {
			graficador.defDif(Math.round(graficador.obtDif() +
					graficador.obtDif() * 0.2d));
			System.out.println("act Alejar");
			actualizar();
		} else if (cmd.compareTo("Acercar") == 0) {
			graficador.defDif(Math.round(graficador.obtDif() -
					graficador.obtDif() * 0.2d));
			System.out.println("act Acercar");
			actualizar();
		} else  if (cmd.compareTo("Exportar gráfica...") == 0) {
			exportarGrafica();
		} else if (cmd.compareTo("comboBoxChanged") == 0) {
			cmd = ((JComboBox)evt.getSource()).getToolTipText();
			if (cmd.compareTo("Parámetro a graficar") == 0)
				if (((JComboBox)evt.getSource()).isEnabled()) {
				System.out.println("act Parameter a graficar");
				actualizar();
			}
		} else if (cmd.compareTo("Importar imagen de fondo...") == 0) {
			importarImagenFondo();
		} else if (cmd.compareTo("Exportar imagen de topología...") == 0) {
			exportarGraficaTopologia();
		} else if (cmd.compareTo("Agregar evento...") == 0) {
			agregarEvento();
		} else if (cmd.compareTo("Modificar evento...") == 0) {
			modificarEvento();
		} else if (cmd.compareTo("Eliminar evento...") == 0) {
			eliminarEvento();
		} else if (cmd.compareTo("Agregar tarea...") == 0) {
			agregarTareaMantenimiento();
		} else if (cmd.compareTo("Modificar tarea...") == 0) {
			modificarTareaMantenimiento();
		} else if (cmd.compareTo("Eliminar tarea...") == 0) {
			eliminarTareaMantenimiento();
		}
		
		else System.out.println(cmd);
	}

	/***************************************************************************
	 * Escucha los cambios en el <code>slider</code>.
	 * 
	 * @param evt	Evento generador del cambio
	 **************************************************************************/
	public void stateChanged(ChangeEvent evt) {
		if (evt.getSource() instanceof JSlider) {		
			JSlider slider = (JSlider)evt.getSource();
			if (!slider.getValueIsAdjusting()) {
				System.out.println("act stateChanged");
				actualizar();
			}
		} else if (evt.getSource() instanceof JTabbedPane) {
			System.out.println("act JTabbedPane");
			actualizar();
		} else System.out.println(evt.getSource());
	}

	/***************************************************************************
	 * Método ejecutado al hacer click con algún botón del ratón. Utilizado
	 * en el arbol de nodos para seleccionar/deseleccionar nodos.
	 **************************************************************************/
	public void mouseClicked(MouseEvent evt) {
		if (evt.getSource() instanceof JTree) actualizarNodosTablaDatos();
	}
	
	/***************************************************************************
	 * Método ejecutado al presionar algún botón del ratón. Utilizado para
	 * atrasar y adelantar el tiempo con los botones de las barras de
	 * herramientas.
	 * 
	 * @param evt	Evento originador de la acción
	 **************************************************************************/
	public void mousePressed(MouseEvent evt) {
		if (evt.getButton() != MouseEvent.BUTTON1) return;
		if (!(evt.getSource() instanceof JButton)) return;
		String cmd = ((JButton)evt.getSource()).getToolTipText();
		if (cmd.compareTo("Rebobinar") == 0) rebobinarTimer.restart();
		if (cmd.compareTo("Adelantar") == 0) adelantarTimer.restart();
	}

	/***************************************************************************
	 * Método ejecutado al soltar algún botón del ratón. Utilizado para
	 * detener las acciones de atrasar o adelantar.
	 * 
	 * @param evt	Evento originador de la acción
	 **************************************************************************/
	public void mouseReleased(MouseEvent evt) {
		mouseExited(evt);
	}

	/***************************************************************************
	 * Método ejecutado al salir el apuntador del ratón del área del
	 * componente. Utilizado para detener las acciones de atrasar o adelantar.
	 * 
	 * @param evt	Evento originador de la acción
	 **************************************************************************/
	public void mouseExited(MouseEvent evt) {
		if (!(evt.getSource() instanceof JButton)) return;
		String cmd = ((JButton)evt.getSource()).getToolTipText();
		if (cmd.compareTo("Rebobinar") == 0) rebobinarTimer.stop();
		if (cmd.compareTo("Adelantar") == 0) adelantarTimer.stop();
	}
	
	/***************************************************************************
	 * Método ejecutado al entrar el apuntador del ratón del área del
	 * componente. Método no utilizado.
	 **************************************************************************/	
	public void mouseEntered(MouseEvent evt) {}
	
}
