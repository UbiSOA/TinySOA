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
import javax.swing.*;

/*******************************************************************************
 * Clase que implementa un <i>renderer</i> de celda de una lista con un icono.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/25/2006
 ******************************************************************************/
public class ListaCeldaRenderer extends JLabel implements ListCellRenderer {
	private static final long serialVersionUID = -431382948314918856L;

	private ImageIcon[] iconos;
	
	/***************************************************************************
	 * Constructor principal de la clase.
	 * 
	 * @param iconos	Iconos de los elementos de la lista
	 **************************************************************************/
	public ListaCeldaRenderer(ImageIcon[] iconos) {
		this.iconos = iconos;
	}
	
	/***************************************************************************
	 * Método para preparar el elemento de la lista.
	 **************************************************************************/
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (isSelected) {
			setBackground(new Color(230,230,230));
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		setOpaque(true);
		setBorder(BorderFactory.createEmptyBorder(3,6,3,2));
		setText(value.toString());
		setIcon(iconos[index]);
		setFont(list.getFont());
		
		return this;
	}

}
