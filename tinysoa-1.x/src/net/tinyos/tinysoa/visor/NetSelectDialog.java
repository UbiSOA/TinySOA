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
import java.util.*;
import javax.swing.*;

import net.tinyos.tinysoa.common.*;
import net.tinyos.tinysoa.server.*;
import net.tinyos.tinysoa.util.*;

/*******************************************************************************
 * Clase que implementa el diálogo para la selección de red.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/25/2006
 ******************************************************************************/
public class NetSelectDialog extends JDialog {
	private static final long serialVersionUID = -522846215043252060L;

	private JList listaRedes;
	private final InterfaceEvents eventosInterfaz;
	
	/***************************************************************************
	 * Constructor de la clase.
	 * 
	 * @param window	Ventana padre del diálogo
	 * @param icono	Icono de una red
	 * @param eventos	Controlador de eventos para llamar al seleccionar
	 **************************************************************************/
	public NetSelectDialog(JFrame ventana, ImageIcon icono,
			InterfaceEvents eventos) {
		super(ventana, "Seleccionar red", false);
		this.eventosInterfaz = eventos;
		
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
		
		JLabel etiqueta = new JLabel("Redes de sensores disponibles:");
		etiqueta.setBorder(BorderFactory.createEmptyBorder(0,0,4,0));
		
		panel.add(etiqueta, BorderLayout.NORTH);
		
		listaRedes = new JList();
		listaRedes.setCellRenderer(new MonitorListCellRenderer(icono));
		JScrollPane scrollLista = new JScrollPane(listaRedes);
		scrollLista.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollLista.setPreferredSize(new Dimension(320,250));
		panel.add(scrollLista, BorderLayout.CENTER);
		
		JPanel panel2 = new JPanel();
		panel2.setBorder(BorderFactory.createEmptyBorder(4,0,0,0));
		panel2.setLayout(new FlowLayout(FlowLayout.CENTER));

		ActionListener accion = new ActionListener(){
			private static final long serialVersionUID =
				-4115615620227275613L;
			public void actionPerformed(ActionEvent ev) {
				if (ev.getActionCommand().compareTo("Aceptar") == 0) {
					if (listaRedes.getSelectedValue() == null) {
						JOptionPane.showMessageDialog(
								null, "Debe seleccionar una red.",
								"Seleccionar red", JOptionPane.WARNING_MESSAGE);
						return;
					}
					setVisible(false);
					String wsdl = ((Network)listaRedes.
							getSelectedValue()).getWsdl(); 
					eventosInterfaz.createNetworkService(
							wsdl.substring(0, wsdl.length() - 5));
				}
				if (ev.getActionCommand().compareTo("Cancelar") == 0) {
					setVisible(false);
				}
			}};
		
		JButton boton = new JButton("Aceptar");
		boton.addActionListener(accion);
		panel2.add(boton);
		
		boton = new JButton("Cancelar");
		boton.addActionListener(accion);
		panel2.add(boton);

		panel.add(panel2, BorderLayout.SOUTH);
		
		pack();
		
		setModal(true);
		setResizable(false);
	}
	
	/***************************************************************************
	 * Obtiene el listado de las redes disponibles en el servidor.
	 * 
	 * @param servicio	Servicio de información a utilizar
	 **************************************************************************/
	private void obtenerRedesDisponibles(InfoServ servicio) {
		Vector<Network> redes = servicio.getNetworksList();
		listaRedes.setListData(redes.toArray());
	}
	
	/***************************************************************************
	 * Muestra el diálogo de selección de red.
	 * 
	 * @param servicio			Servicio de información a utilizar
	 * @param barraProgreso	Barra de progreso a mostrar
	 **************************************************************************/
	public void mostrar(InfoServ servicio, JProgressBar barraProgreso) {
		if (servicio == null) {
			JOptionPane.showMessageDialog(
					null, "Aún se ha conectado al servidor.",
					"Problema de conexión", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		obtenerRedesDisponibles(servicio);
		if (listaRedes.getModel().getSize() == 0) {
			JOptionPane.showMessageDialog(null,
					"No hay redes de sensores disponibles en el servidor.",
					"No hay redes disponibles",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		barraProgreso.setVisible(false);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2,
				(dim.height - getSize().height) / 2);
		setVisible(true);
	}
	
}
