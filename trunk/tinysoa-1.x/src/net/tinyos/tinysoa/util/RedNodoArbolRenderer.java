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
import javax.swing.plaf.*;
import javax.swing.tree.*;

/*******************************************************************************
 * Clase que implementa un <i>renderer</i> de celda de arbol con el propósito
 * de proveer la funcionalidad de un JCheckBox en el arbol.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/25/2006
 ******************************************************************************/
public class RedNodoArbolRenderer extends JPanel
		implements TreeCellRenderer {
	private static final long serialVersionUID = 9163424875253834373L;

	protected JCheckBox check;
	protected TreeLabel label;
	
	/***************************************************************************
	 * Constructor básico de la clase.
	 **************************************************************************/
	public RedNodoArbolRenderer() {
		setLayout(null);
		add(check = new JCheckBox());
		add(label = new TreeLabel());
		check.setBackground(UIManager.getColor("Tree.textBackground"));
		label.setForeground(UIManager.getColor("Tree.textForeground"));
	}
	
	/***************************************************************************
	 * Devuelve el componente que representa a la celda del arbol.
	 **************************************************************************/
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		String stringValue = tree.convertValueToText(value, isSelected,
				expanded, leaf, row, hasFocus);
		setEnabled(tree.isEnabled());
		
		if (value instanceof RedNodoArbol) {
			check.setSelected(((RedNodoArbol)value).isSelected());
			label.setFont(tree.getFont());
			label.setText(stringValue);
			label.setSelected(isSelected);
			label.setFocus(hasFocus);
			
			label.setIcon(((RedNodoArbol)value).getIcon());
		}

		return this;
	}
	
	/***************************************************************************
	 * Regresa el tamaño preferido del componente.
	 **************************************************************************/
	public Dimension getPreferredSize() {
		Dimension d_check = check.getPreferredSize();
		Dimension d_label = label.getPreferredSize();
		return new Dimension(d_check.width + d_label.width,
				(d_check.height < d_label.height ?
						d_label.height: d_check.height));
	}
	
	/***************************************************************************
	 * Prepara la posición y dimensión de los componentes de la celda.
	 **************************************************************************/
	public void doLayout() {
		Dimension d_check = check.getPreferredSize();
		Dimension d_label = label.getPreferredSize();
		int y_check = 0;
		int y_label = 0;
		if (d_check.height > d_label.height) {
			y_check = (d_label.height - d_check.height) / 2;
		} else {
			y_label = (d_check.height - d_label.height) / 2;
		}
		check.setLocation(0, y_check);
		check.setBounds(0, y_check, d_check.width, d_check.height);
		label.setLocation(d_check.width, y_label);
		label.setBounds(d_check.width, y_label, d_label.width, d_label.height);
	}
	
	/***************************************************************************
	 * Define el color de fondo de la celda.
	 **************************************************************************/
	public void setBackground(Color color) {
		if (color instanceof ColorUIResource)
			color = null;
		super.setBackground(color);
	}
	
	/***************************************************************************
	 * Clase que implementa una etiqueta en el nodo del arbol.
	 * 
	 * @author		Edgardo Avilés López
	 * @version	0.1, 07/25/2006
	 **************************************************************************/
	private class TreeLabel extends JLabel {
		private static final long serialVersionUID = -4518980052144690066L;

		boolean isSelected;
		boolean hasFocus;
		
		/** Constructor básico de la clase */
		public TreeLabel() {}
		
		/***********************************************************************
		 * Define el fondo de la etiqueta.
		 **********************************************************************/
		public void setBackground(Color color) {
			if (color instanceof ColorUIResource)
				color = null;
			super.setBackground(color);
		}
		
		/***********************************************************************
		 * Dibuja el componente.
		 **********************************************************************/
		public void paint(Graphics g) {
			String str;
			if ((str = getText()) != null) {
				if (0 < str.length()) {
					if (isSelected && hasFocus) {
						g.setColor(UIManager.getColor(
								"Tree.selectionBackground"));
					} else {
						g.setColor(UIManager.getColor(
								"Tree.textBackground"));
					}
					Dimension d = getPreferredSize();
					int imageOffset = 0;
					Icon currentI = getIcon();
					if (currentI != null) {
						imageOffset = currentI.getIconWidth() +
								Math.max(0, getIconTextGap() - 1);
					}
					g.fillRect(imageOffset, 0,
							d.width - 1 - imageOffset, d.height);
					if (hasFocus) {
						g.setColor(UIManager.getColor(
								"Tree.selectionBorderColor"));
						g.drawRect(imageOffset, 0, d.width - 1 - imageOffset,
								d.height - 1);
					}
				}
			}
			super.paint(g);
		}
		
		/***********************************************************************
		 * Devuelve el tamaño preferido de la etiqueta.
		 **********************************************************************/
		public Dimension getPreferredSize() {
			Dimension retDimension = super.getPreferredSize();
			if (retDimension != null) {
				retDimension = new Dimension(retDimension.width + 3,
						retDimension.height);
			}
			return retDimension;
		}
		
		/***********************************************************************
		 * Define si la etiqueta está seleccionada.
		 * 
		 * @param isSelected	Verdadero si está seleccionada
		 **********************************************************************/
		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}
		
		/***********************************************************************
		 * Define el enfoque del componente.
		 * 
		 * @param hasFocus	Verdadero si tiene el enfoque
		 **********************************************************************/
		public void setFocus(boolean hasFocus) {
			this.hasFocus = hasFocus;
		}
		
	}

}
