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
import javax.swing.*;

import net.tinyos.tinysoa.servidor.*;

/*******************************************************************************
 * Diálogo para agregar o modificar un evento. 
 * 
 * @author		Edgardo Avilés López
 * @version	0.2, 07/26/2006
 ******************************************************************************/
public class DialogoEvento extends JDialog {
	private static final long serialVersionUID = -378032218137841324L;
	
	private RedServ servicioRed;
	private String nombre, criterio;
	private int id;
	
	private JLabel l02, l03;
	private JButton b01, b02;
	private JTextField tf01;
	private JTextArea ta01;
	private JPanel p01, p02;
	private JScrollPane sp01;
	private JProgressBar progreso;
	
	/***************************************************************************
	 * Constructor de la clase.
	 * 
	 * @param procesador	Procesador de mensajes a activar.
	 * @param f			Ventana en la cual se va a desplegar el diálogo.
	 * @param p			Archivo de propiedades a editar.
	 * @param arch			Nombre del archivo de propiedades.
	 * @param c			Conexión a la base de datos.
	 **************************************************************************/
	public DialogoEvento(JFrame ventana, RedServ servicioRed,
			JProgressBar progreso) {
		super(ventana, "Agregar Evento", false);
		this.servicioRed = servicioRed;
		this.progreso = progreso;

		p01 = new JPanel();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(p01, BorderLayout.CENTER);
		p01.setLayout(new GridBagLayout());
		
		l02 = new JLabel("Nombre:");

		p01.add(l02, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(8, 8, 0, 8), 0, 0));
		
		tf01 = new JTextField();
		
		p01.add(tf01, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(2, 8, 0, 8), 0, 0));
		
		l03 = new JLabel("Criterio:");
		
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
		
		b01 = new JButton("Aceptar");
		b01.addActionListener(new DialogoEventosAcciones(this));
		b02 = new JButton("Cancelar");
		b02.addActionListener(new DialogoEventosAcciones(this));

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
	 * Muestra el diálogo
	 **************************************************************************/
	public void mostrar() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2,
				(dim.height - getSize().height) / 2);
		
		b01.setEnabled(true);
		setVisible(true);
		
		tf01.requestFocus();
	}
	
	public void defValores(int id, String nombre, String criterio) {
		this.id = id;
		this.nombre = nombre;
		this.criterio = criterio;
		
		tf01.setText(nombre);
		ta01.setText(criterio);
	}
	
	/***************************************************************************
	 * Clase que implementa los eventos del diálogo.
	 * 
	 * @author		Edgardo Avilés López
	 * @version	0.1, 07/26/2006
	 **************************************************************************/
	private class DialogoEventosAcciones implements ActionListener {

		boolean mismo = false;
		Object viejo;
		JDialog f;
		
		/***********************************************************************
		 * Constructor de la clase.
		 * 
		 * @param f	Diálogo padre.
		 **********************************************************************/
		public DialogoEventosAcciones(JDialog f) {
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
			if (tf01.getText().trim().compareTo("") == 0) {
				JOptionPane.showMessageDialog(
						f, "Debe indicar un nombre para el evento.",
						"Problema", JOptionPane.WARNING_MESSAGE);
				tf01.requestFocus();
				return;
			}
			
			if (ta01.getText().trim().compareTo("") == 0) {
				JOptionPane.showMessageDialog(
						f, "Debe indicar el criterio del evento.",
						"Problema", JOptionPane.WARNING_MESSAGE);
				ta01.requestFocus();
				return;
			}
			
			if (ta01.getText().toLowerCase().indexOf("or") >= 0) {
				JOptionPane.showMessageDialog(
						f, "El operador OR no está soportado.",
						"Problema", JOptionPane.WARNING_MESSAGE);
				ta01.requestFocus();
				return;				
			}

			ta01.setText(ta01.getText().replaceAll("\\(", ""));
			ta01.setText(ta01.getText().replaceAll("\\)", ""));

			new Thread() {
				public void run() {
					progreso.setVisible(true);
					b01.setEnabled(false);
			
					nombre = tf01.getText().trim();
					criterio = ta01.getText().trim();

					boolean res = false;
					if (getTitle().compareTo("Agregar Evento") == 0)
						res = servicioRed.agregarEvento(nombre, criterio);
					if (getTitle().compareTo("Modificar Evento") == 0)
						res = servicioRed.modificarEvento(id, nombre, criterio);

					if (!res) {
						JOptionPane.showMessageDialog(
								f, "Existe un error en el criterio, por " +
								"favor verifique que los nombres de los " +
								"parámetros y sintaxis sean correctas y " +
								"vuelva a intentarlo.",
								"Problema", JOptionPane.WARNING_MESSAGE);
						ta01.requestFocus();
						b01.setEnabled(true);
						progreso.setVisible(false);
						return;
					}

					progreso.setVisible(false);
					setVisible(false);
				}
			}.start();
		}
		
		/***********************************************************************
		 * Función controladora de eventos.
		 * 
		 * @param	Evento productor de la acción
		 **********************************************************************/
		public void actionPerformed(ActionEvent evt) {
			String cmd = evt.getActionCommand();
			if (cmd.compareTo("Cancelar") == 0) cancelar();
			if (cmd.compareTo("Aceptar") == 0) aceptar();
		}
		
	}
	
}
