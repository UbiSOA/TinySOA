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

package net.tinyos.tinysoa.util;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.text.BreakIterator;

import net.tinyos.tinysoa.comun.*;

/*******************************************************************************
 * Clase <i>renderer</i> de la lista de redes a seleccionar.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1
 ******************************************************************************/
public class ListaRedesRenderer implements ListCellRenderer {
	
	private ImageIcon icono;
	
	/***************************************************************************
	 * Constructor del renderer.
	 * 
	 * @param icono	Icono de una red
	 **************************************************************************/
	public ListaRedesRenderer(ImageIcon icono) {
		this.icono = icono;
	}
	
	/***************************************************************************
	 * Hace una cadena multilínea introduciendo <pre><br></pre> en donde el
	 * ancho de la línea excede la longitud en pixeles indicada.
	 * 
	 * @param texto	Cadena a acomodar
	 * @param fuente	Fuente a utilizar
	 * @param ancho	Ancho en pixeles deseado
	 * @return			Una cadena multilínea
	 **************************************************************************/
	private String wrapLine(String texto, Font fuente, int ancho) {
		BreakIterator iterator = BreakIterator.getWordInstance(
				Locale.getDefault());
		iterator.setText(texto);
		int start = iterator.first();
		int end = iterator.next();
		FontMetrics fm = new Button().getFontMetrics(fuente);
		String s = "";
		int len = 0;
		while (end != BreakIterator.DONE) {
			String word = texto.substring(start,end);
			if( len + fm.stringWidth(word) > ancho ) {
				s += "<br>";
				len = fm.stringWidth(word);
			} else {
				len += fm.stringWidth(word);
			}
			s += word;
			start = end;
			end = iterator.next();
		} 
		return s;
	}
	
	/***************************************************************************
	 * Función parte de la interfáz ListCellRenderer que implementa el diseño
	 * del elemento de la lista de redes.
	 * 
	 * @param lista		Lista padre
	 * @param valor		Valor del elemento de la lista
	 * @param index		Índice del elemento
	 * @param seleccionado	Verdadero si el elemento está seleccionado
	 * @param conEnfoque	Verdadero si el elemento tiene el enfoque
	 * @return				Un Panel con el diseño deseado 
	 **************************************************************************/
	public Component getListCellRendererComponent(JList lista, Object valor,
			int index, boolean seleccionado, boolean conEnfoque) {
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setOpaque(true);
		
		JLabel etiqueta = new JLabel(((Red)valor).getNombre());
		etiqueta.setIcon(icono);
		etiqueta.setFont(new Font("Arial", Font.BOLD, 12));
		panel.add(etiqueta, BorderLayout.NORTH);
		
		String descripcion = wrapLine(((Red)valor).getDescripcion(),
				new Font("Arial", Font.PLAIN, 12), lista.getWidth() - 35);
		
		etiqueta = new JLabel("<html>" + descripcion +
				"<br><font color=\"#6382BF\">" + ((Red)valor).getWsdl() +
				"</a></html>");
		etiqueta.setFont(new Font(lista.getFont().getFamily(), Font.PLAIN,
				lista.getFont().getSize()));
		etiqueta.setBorder(BorderFactory.createEmptyBorder(0,
				icono.getIconWidth() + etiqueta.getIconTextGap(),0,0));
		panel.add(etiqueta, BorderLayout.CENTER);
		
		if (seleccionado)
			panel.setBackground(UIManager.getColor("List.selectionBackground"));
		else panel.setBackground(UIManager.getColor(lista.getBackground()));
		
		if (conEnfoque && seleccionado) {
			panel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(
							UIManager.getColor("Tree.selectionBorderColor")),
					BorderFactory.createEmptyBorder(4,4,4,4)));
		} else if (seleccionado) {
			panel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(
							UIManager.getColor("List.selectionBackground")),
					BorderFactory.createEmptyBorder(4,4,4,4)));			
		} else {
			panel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(lista.getBackground()),
					BorderFactory.createEmptyBorder(4,4,4,4)));
		}
		
		return panel;
	}

}
