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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;

import net.tinyos.tinysoa.common.*;
import net.tinyos.tinysoa.common.Event;
import net.tinyos.tinysoa.servidor.*;

/*******************************************************************************
 * Diálogo para agregar o modificar una tarea de mantenimiento. 
 * 
 * @author		Edgardo Avilés López
 * @version	0.2, 07/28/2006
 ******************************************************************************/
public class DialogoMantenimiento extends JDialog {
	private static final long serialVersionUID = -2649034852161564309L;

	private RedServ servicioRed;
	private JProgressBar progreso;
	private Object[] eventos;
	private int id = 0;
	
	private JRadioButton rb01, rb02, rb03, rb04, rb05, rb06, rb07;
	private JComboBox cb01, cb02, cb03;
	private JSpinner s01, s02, s03;
	private JButton b01, b02;
	private JTabbedPane tp01;
	private JCheckBox ckb01;
	private JLabel l01;
	
	public DialogoMantenimiento(JFrame ventana, RedServ servicioRed,
			JProgressBar progreso) {
		super(ventana, "Agregar Tarea", false);
		this.servicioRed = servicioRed;
		this.progreso = progreso;
		
		DialogoMantenimientoAcciones evts =
			new DialogoMantenimientoAcciones(this);
		
		JPanel p2 = new JPanel();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(p2, BorderLayout.CENTER);
		p2.setLayout(new GridBagLayout());
	
		// Tabs de tipos de acciones -------------------------------------------
		
		tp01 = new JTabbedPane();
		p2.add(tp01, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(8, 8, 0, 8), 0, 0));
		
		// Tab de Actuadores ---------------------------------------------------
		
		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new GridBagLayout());
		
		ButtonGroup bg = new ButtonGroup();
		rb01 = new JRadioButton("Encender");
		rb01.setOpaque(false);
		rb01.setSelected(true);
		p.add(rb01, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(4, 8, 0, 4), 0, 0));
		bg.add(rb01);
		
		rb02 = new JRadioButton("Apagar");
		rb02.setOpaque(false);
		p.add(rb02, new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(4, 0, 0, 8), 0, 0));
		bg.add(rb02);
		
		cb01 = new JComboBox(new Object[]{"Bocina", "Led Amarillo",
				"Led Azul", "Led Rojo", "Led Verde"});
		p.add(cb01, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(4, 8, 8, 8), 0, 0));
		
		tp01.add(p, "Actrs.");
		tp01.setToolTipTextAt(0, "Actuadores");
		
		// Tab de Tasa ---------------------------------------------------------
		
		p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new GridBagLayout());
		
		JLabel l = new JLabel("Cambiar tasa de muestreo a:");
		p.add(l, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(8, 8, 0, 8), 0, 0));
		
		s01 = new JSpinner(
				new SpinnerNumberModel(10, 1, 24 * 60 * 60, 1));
		p.add(s01, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(8, 8, 8, 8), 0, 0));
		
		l = new JLabel("segundos");
		p.add(l, new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(8, 0, 8, 8), 0, 0));
		
		tp01.add(p, "Tasa");
		tp01.setToolTipTextAt(1, "Tasa de muestreo");
		
		// Tab de Espera -------------------------------------------------------
		
		p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new GridBagLayout());
		
		bg = new ButtonGroup();
		
		rb03 = new JRadioButton("Entrar en estado de espera");
		rb03.setOpaque(false);
		rb03.setSelected(true);
		p.add(rb03, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
				new Insets(0, 8, 0, 8), 0, 0));
		bg.add(rb03);
		
		rb04 = new JRadioButton("Salir del estado de espera");
		rb04.setOpaque(false);
		p.add(rb04, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(0, 8, 0, 8), 0, 0));
		bg.add(rb04);
		
		tp01.add(p, "Espera");
		tp01.setToolTipTextAt(2, "Estado de espera");
		
		// ComboBox de nodo destino --------------------------------------------
		
		p = new JPanel();
		p.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Nodo destino"),
				BorderFactory.createEmptyBorder(0, 12, 4, 12)));
		p.setLayout(new BorderLayout());
		p2.add(p, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(8, 8, 0, 8), 0, 0));
		
		cb02 = new JComboBox();
		p.add(cb02, BorderLayout.CENTER);
		
		// Selección de tiempo -------------------------------------------------
		
		p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder("Tiempo de la ejecución"));
		p.setLayout(new GridBagLayout());
		
		p2.add(p, new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(8, 8, 0, 8), 0, 0));
		
		bg = new ButtonGroup();
		rb05 = new JRadioButton("Inmediatamente");
		rb05.setSelected(true);
		rb05.addActionListener(evts);
		p.add(rb05, new GridBagConstraints(1, 1, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 8, 0, 8), 0, 0));
		bg.add(rb05);
		
		rb06 = new JRadioButton("Ejecutar el:");
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
		
		ckb01 = new JCheckBox("Repetir cada");
		ckb01.addActionListener(evts);
		p.add(ckb01, new GridBagConstraints(1, 4, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 28, 0, 8), 0, 0));
		
		s03 = new JSpinner(new SpinnerNumberModel(10, 1, 60 * 24, 1));
		p.add(s03, new GridBagConstraints(1, 5, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 28, 0, 8), 0, 0));
		
		l01 = new JLabel("minutos");
		p.add(l01, new GridBagConstraints(2, 5, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 8), 0, 0));
		
		rb07 = new JRadioButton("Esperar al evento");
		rb07.addActionListener(evts);
		p.add(rb07, new GridBagConstraints(1, 6, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 8, 0, 8), 0, 0));
		bg.add(rb07);
		
		cb03 = new JComboBox();
		p.add(cb03, new GridBagConstraints(1, 7, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 28, 8, 8), 0, 0));

		// Botones Aceptar y Cancelar ------------------------------------------
		
		p = new JPanel();
		p.setOpaque(false);
		
		b01 = new JButton("Aceptar");
		b01.addActionListener(evts);
		p.add(b01);
		
		b02 = new JButton("Cancelar");
		b02.addActionListener(evts);
		p.add(b02);
		
		p.setBorder(BorderFactory.createEmptyBorder(0,0,4,0));
		getContentPane().add(p, BorderLayout.SOUTH);
		
		// Configuración final -------------------------------------------------
		
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		pack();

		setModal(true);
		setResizable(false);
		setLocationByPlatform(true);
	}
	
	public void defNodos(Object[] nodos) {
		Object[] valores = new Object[nodos.length + 1];
		valores[0] = "Todos";
		for (int i = 0; i < nodos.length; i++)
			valores[i + 1] = "Nodo " + ((Node)nodos[i]).getId();
		cb02.setModel(new DefaultComboBoxModel(valores));		
	}
	
	public void defEventos(Object[] eventos) {
		this.eventos = eventos;
		Object[] valores = new Object[eventos.length];
		for (int i = 0; i < eventos.length; i++)
			valores[i] = ((Event)eventos[i]).getName();
		cb03.setModel(new DefaultComboBoxModel(valores));		
	}
	
	private void elegirControles() {
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
	 * Muestra el diálogo
	 **************************************************************************/
	private void mostrar() {
		elegirControles();
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2,
				(dim.height - getSize().height) / 2);
		setVisible(true);
	}
	
	public void mostrarAgregar() {
		
		if (cb01.getItemCount() == 0) {
			progreso.setVisible(true);
			Vector<Node> nodos = servicioRed.obtenerListadoNodos();
			defNodos(nodos.toArray());
			progreso.setVisible(false);
		}
		
		if (cb03.getItemCount() == 0) {
			progreso.setVisible(true);
			Vector<Event> eventos = servicioRed.obtenerListadoEventos(0);
			defEventos(eventos.toArray());
			progreso.setVisible(false);
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
		setTitle("Agregar Tarea");
		b01.setEnabled(true);
		mostrar();
	}
	
	public void mostrarModificar(int id) {
		progreso.setVisible(true);
		Tarea tarea = servicioRed.obtenerTareaPorId(id);
		progreso.setVisible(false);
		
		if (cb01.getItemCount() == 0) {
			progreso.setVisible(true);
			Vector<Node> nodos = servicioRed.obtenerListadoNodos();
			defNodos(nodos.toArray());
			progreso.setVisible(false);
		}
		
		if (cb03.getItemCount() == 0) {
			progreso.setVisible(true);
			Vector<Event> eventos = servicioRed.obtenerListadoEventos(0);
			defEventos(eventos.toArray());
			progreso.setVisible(false);
		}
		
		if ((tarea.getTipo() == Constants.TIPO_ACTIVA_ACTUADOR) ||
				(tarea.getTipo() == Constants.TIPO_DESACTIVA_ACTUADOR)) {
			tp01.setSelectedIndex(0);
			if (tarea.getValor() == Constants.ACTUADOR_BOCINA)
				cb01.setSelectedIndex(0);
			if (tarea.getValor() == Constants.ACTUADOR_LED_AMARILLO)
				cb01.setSelectedIndex(1);
			if (tarea.getValor() == Constants.ACTUADOR_LED_ROJO)
				cb01.setSelectedIndex(3);
			if (tarea.getValor() == Constants.ACTUADOR_LED_VERDE)
				cb01.setSelectedIndex(4);
			s01.setValue(10);
			if (tarea.getTipo() == Constants.TIPO_ACTIVA_ACTUADOR)
				rb01.setSelected(true);
			else rb02.setSelected(true);
		}
		
		if (tarea.getTipo() == Constants.TIPO_CAMBIA_DATA_RATE) {
			tp01.setSelectedIndex(1);
			cb01.setSelectedIndex(0);
			s01.setValue(tarea.getValor());
			rb03.setSelected(true);
		}
		
		if ((tarea.getTipo() == Constants.TIPO_DUERME) ||
				(tarea.getTipo() == Constants.TIPO_DESPIERTA)) {
			tp01.setSelectedIndex(2);
			cb01.setSelectedIndex(0);
			s01.setValue(10);
			if (tarea.getTipo() == Constants.TIPO_DUERME)
				rb03.setSelected(true);
			else rb04.setSelected(true);
		}
		
		if (tarea.getNid() == 0)
			cb02.setSelectedIndex(0);
		for (int i = 1; i < cb02.getModel().getSize(); i++)
			if (Integer.parseInt(cb02.getModel().getElementAt(i).
					toString().substring(5)) == tarea.getNid())
				cb02.setSelectedIndex(i);
		
		if (tarea.getEvento() == 0) {
			rb06.setSelected(true);
			s02.setEnabled(true);
			DateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				s02.setValue(formato.parse(tarea.getTiempo()));
			} catch (ParseException e) {
				System.out.println("Problema parseando: " + tarea.getTiempo());
				s02.setValue(new Date());
			}
			if (tarea.getRepetir() > 0) {
				ckb01.setSelected(true);
				s03.setValue(tarea.getRepetir());
			} else {
				ckb01.setSelected(false);
			}
		} else {
			rb07.setSelected(true);
			String nes = "";
			for (int i = 0; i < eventos.length; i++)
				if (((Event)eventos[i]).getId() == tarea.getEvento())
					nes = ((Event)eventos[i]).getName();
			for (int i = 0; i < cb03.getModel().getSize(); i++)
				if (cb03.getModel().getElementAt(i).
						toString().compareTo(nes) == 0)
					cb03.setSelectedIndex(i);
		}
		
		this.id = id;
		setTitle("Modificar Tarea");
		b01.setEnabled(true);
		mostrar();
	}
	
	/***************************************************************************
	 * Clase que implementa los eventos del diálogo.
	 * 
	 * @author		Edgardo Avilés López
	 * @version	0.1, 07/27/2006
	 **************************************************************************/
	private class DialogoMantenimientoAcciones implements ActionListener {

		JDialog f;
		
		public DialogoMantenimientoAcciones(JDialog f) {
			this.f = f;
		}
		
		/***********************************************************************
		 * Sale del sistema.
		 **********************************************************************/
		private void cancelar() {
			setVisible(false);
		}
		
		/***********************************************************************
		 * Controla el evento de creación/selección de red de sensores.
		 **********************************************************************/
		private void aceptar() {
			new Thread() {
				public void run() {
					progreso.setVisible(true);
					b01.setEnabled(false);
			
					int tipo = 0;
					int valor = 0;
					int destino = 0;
					String tiempo = "";
					int repetir = 0;
					int evento = 0;
					
					DateFormat formato = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					tiempo = formato.format(new Date());
					
					if (tp01.getSelectedIndex() == 0) {
						if (rb01.isSelected())
							tipo = Constants.TIPO_ACTIVA_ACTUADOR;
						if (rb02.isSelected())
							tipo = Constants.TIPO_DESACTIVA_ACTUADOR;
						if (cb01.getSelectedIndex() == 0)
							valor = Constants.ACTUADOR_BOCINA;
						if (cb01.getSelectedIndex() == 1)
							valor = Constants.ACTUADOR_LED_AMARILLO;
						if (cb01.getSelectedIndex() == 2)
							valor = Constants.ACTUADOR_LED_AZUL;
						if (cb01.getSelectedIndex() == 3)
							valor = Constants.ACTUADOR_LED_ROJO;
						if (cb01.getSelectedIndex() == 4)
							valor = Constants.ACTUADOR_LED_VERDE;
					}
					if (tp01.getSelectedIndex() == 1) {
						tipo = Constants.TIPO_CAMBIA_DATA_RATE;
						valor = Integer.parseInt(s01.getValue().toString());
					}
					if (tp01.getSelectedIndex() == 2) {
						if (rb03.isSelected())
							tipo = Constants.TIPO_DUERME;
						if (rb04.isSelected())
							tipo = Constants.TIPO_DESPIERTA;
					}
					
					if (cb02.getSelectedItem().toString().substring(0, 4).
							compareTo("Nodo") == 0)
						destino = Integer.parseInt(
								cb02.getSelectedItem().toString().substring(5));
					
					if (s02.isEnabled())
						tiempo = formato.format((Date)s02.getValue());
					
					if (s03.isEnabled())
						repetir = Integer.parseInt(s03.getValue().toString());
					
					if (cb03.isEnabled())
						if (cb03.getSelectedItem() != null) {
							String nEv = cb03.getSelectedItem().toString();
							for (int i = 0; i < eventos.length; i++)
								if (((Event)eventos[i]).getName().
										compareTo(nEv) == 0)
									evento = ((Event)eventos[i]).getId();
						}
					
					boolean res = false;
					if (getTitle().compareTo("Agregar Tarea") == 0)
						res = servicioRed.agregarTarea(
								tipo, valor, destino, tiempo, repetir, evento);
					if (getTitle().compareTo("Modificar Tarea") == 0)
						res = servicioRed.modificarTarea(
								id, tipo, valor, destino, tiempo,
								repetir, evento);

					if (!res) {
						JOptionPane.showMessageDialog(
								f, "Existe un error en la definición " +
								"de la tarea.",
								"Problema", JOptionPane.WARNING_MESSAGE);
						b01.setEnabled(true);
						progreso.setVisible(false);
						return;
					}

					progreso.setVisible(false);
					setVisible(false);
				}
			}.start();
		}

		public void actionPerformed(ActionEvent evt) {
			String cmd = evt.getActionCommand();
			if (cmd.compareTo("Cancelar") == 0) cancelar();
			if (cmd.compareTo("Aceptar") == 0) aceptar();
			elegirControles();
		}
		
	}
	
}
