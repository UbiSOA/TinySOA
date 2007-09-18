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

package net.tinyos.tinysoa.gateway;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*;

import net.tinyos.tinysoa.util.Errores;

/*******************************************************************************
 * Diálogo con la configuración inicial de la red. 
 * 
 * @author		Edgardo Avilés López
 * @version	0.2, 07/24/2006
 ******************************************************************************/
public class DialogoPropiedades extends JDialog {
	private static final long serialVersionUID = -378032218137841324L;
	
	private JLabel l02, l03;
	private Font f01;
	private JButton b01, b02;
	private JComboBox cb01;
	private JTextArea ta01;
	private JScrollPane sp01;
	private Vector v;
	private Connection c;
	private Properties p;
	private JPanel p01, p02;
	private ProcesadorMensajes procesador;
	private String propiedadesArchivo;
	
	/***************************************************************************
	 * Constructor de la clase.
	 * 
	 * @param procesador	Procesador de mensajes a activar.
	 * @param f			Ventana en la cual se va a desplegar el diálogo.
	 * @param p			Archivo de propiedades a editar.
	 * @param arch			Nombre del archivo de propiedades.
	 * @param c			Conexión a la base de datos.
	 **************************************************************************/
	@SuppressWarnings("unchecked")
	public DialogoPropiedades(
			ProcesadorMensajes procesador, JFrame f, Properties p,
			String arch, Connection c) {
		
		super(f, "Configuración Inicial", false);
		this.p = p;
		this.c = c;
		this.procesador = procesador;
		this.propiedadesArchivo = arch;

		f01 = new Font("Tahoma", Font.PLAIN, 11);

		p01 = new JPanel();
		p01.setOpaque(false);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(p01, BorderLayout.CENTER);
		p01.setLayout(new GridBagLayout());
		
		l02 = new JLabel("Nombre:");
		l02.setFont(f01);

		p01.add(l02, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(8, 8, 0, 8), 0, 0));
		
		v = new Vector();
		
		try {
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(
					"SELECT * FROM redes ORDER BY nombre ASC");
			while (rs.next()) v.add(rs.getString("nombre"));
		} catch (SQLException e) { Errores.errorBD(e); }
		
		cb01 = new JComboBox(v.toArray());
		cb01.setEditable(true);
		cb01.setFont(f01);
		cb01.addActionListener(new DialogoEventos(this));
		cb01.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(0,4,0,0)));
		
		p01.add(cb01, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(2, 8, 0, 8), 0, 0));
		
		l03 = new JLabel("Descripción:");
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
		
		b01 = new JButton("Aceptar");
		b01.setFont(f01);
		b01.addActionListener(new DialogoEventos(this));
		b02 = new JButton("Salir");
		b02.setFont(f01);
		b02.addActionListener(new DialogoEventos(this));

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
					"SELECT * FROM redes ORDER BY nombre ASC LIMIT 0,1");
			if (rs.next())
				ta01.setText(rs.getString("descripcion"));
		} catch (SQLException e) { Errores.errorBD(e); }
		
		setModal(true);
		setResizable(false);
		setLocationByPlatform(true);
	}
	
	/***************************************************************************
	 * Posiciona la ventana en el centro de la pantalla.
	 **************************************************************************/
	public void centrarDialogo() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2,
				(dim.height - getSize().height) / 2);
	}
	
	/***************************************************************************
	 * Clase que implementa los eventos del diálogo.
	 * 
	 * @author		Edgardo Avilés López
	 * @version	0.1
	 **************************************************************************/
	private class DialogoEventos implements ActionListener {

		boolean mismo = false;
		Object viejo;
		JDialog f;
		
		/***********************************************************************
		 * Constructor de la clase.
		 * 
		 * @param f	Diálogo padre.
		 **********************************************************************/
		public DialogoEventos(JDialog f) {
			this.f = f;			
		}
		
		/***********************************************************************
		 * Controla el evento de selección de un elemento de lista.
		 * 
		 * @param evt	Evento de actionPerformed().
		 **********************************************************************/
		private void seleccionLista(ActionEvent evt) {
			Object nuevo = ((JComboBox)evt.getSource()).getSelectedItem();
			
			if (nuevo != null) {
				mismo = nuevo.equals(viejo);
				viejo = nuevo;
				if (("comboBoxChanged".equals(
						evt.getActionCommand())) && (!mismo)) {
					try {
						Statement st = c.createStatement();
						ResultSet rs = st.executeQuery(
								"SELECT * FROM redes WHERE nombre='" + 
								nuevo + "'");
						if (rs.next()) ta01.setText(
								rs.getString("descripcion"));
					} catch (SQLException e) { Errores.errorBD(e); }
				}
			}
		}
		
		/***********************************************************************
		 * Sale del sistema.
		 **********************************************************************/
		private void salir() {
			System.exit(0);
		}
		
		/***********************************************************************
		 * Controla el evento de creación/selección de red de sensores.
		 **********************************************************************/
		private void aceptar() {
			String nom = cb01.getSelectedItem().toString();
			String des = ta01.getText();

			if (nom.trim().compareTo("") == 0) {
				JOptionPane.showMessageDialog(
						f, "Debe indicar un nombre para la red de sensores.",
						"Problema", JOptionPane.ERROR_MESSAGE);
				cb01.requestFocus();
				return;
			}
			
			if (des.trim().compareTo("") == 0) {
				JOptionPane.showMessageDialog(
						f, "Debe indiciar una descripción para la red de " +
						"sensores.", "Problema", JOptionPane.ERROR_MESSAGE);
				ta01.requestFocus();
				return;
			}
			
			int id = 0;
			
			try {
				Statement st = c.createStatement();
				ResultSet rs = st.executeQuery(
						"SELECT * FROM redes WHERE nombre='" + nom + "'");
				if (rs.next()) id = rs.getInt("id");
				
				if (id == 0) {
					st.executeUpdate(
							"INSERT INTO redes VALUES('" + id + "', '" + 
							nom + "', '" + des + "')");
					rs = st.executeQuery(
							"SELECT * FROM redes WHERE nombre='" + nom + "'");
					if (rs.next()) id = rs.getInt("id");
				}
			} catch (SQLException e) { Errores.errorBD(e); }
			
			try {
				p.setProperty("red.id", id + "");
				p.setProperty("red.nombre", nom);
				p.setProperty("red.descripcion", des);
				p.storeToXML(new FileOutputStream(propiedadesArchivo), null);
			} catch (Exception e) {}
			
			setVisible(false);
			procesador.defListo(true);
		}
		
		/***********************************************************************
		 * Función controladora de eventos.
		 * 
		 * @param	Evento productor de la acción
		 **********************************************************************/
		public void actionPerformed(ActionEvent evt) {
			String cmd = evt.getActionCommand();
			if (cmd.compareTo("comboBoxChanged") == 0) seleccionLista(evt);
			if (cmd.compareTo("Salir") == 0) salir();
			if (cmd.compareTo("Aceptar") == 0) aceptar();
		}
		
	}
	
}
