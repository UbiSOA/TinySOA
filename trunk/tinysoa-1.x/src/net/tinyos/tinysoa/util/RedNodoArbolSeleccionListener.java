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

import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;

/*******************************************************************************
 * Clase que implementa un <i>listener</i> de nodo de arbol.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/25/2006
 ******************************************************************************/
public class RedNodoArbolSeleccionListener extends MouseAdapter {
	
	private JTree tree;
	
	/***************************************************************************
	 * Constructor de la clase.
	 * 
	 * @param tree	Arbol a escuchar
	 **************************************************************************/
	public RedNodoArbolSeleccionListener(JTree tree) {
		this.tree = tree;
	}
	
	/***************************************************************************
	 * Método ejecutado al detectarse un click en el arbol. Este selecciona o
	 * deselecciona según sea el caso al nodo en el cual se presionó el ratón.
	 **************************************************************************/
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int row = tree.getRowForLocation(x, y);
		TreePath path = tree.getPathForRow(row);
		
		if (path != null) {
			
			if (e.getButton() != MouseEvent.BUTTON1) return;
			
			RedNodoArbol nodo = (RedNodoArbol)path.getLastPathComponent();
			
			if (e.getClickCount() == 1)
				if (nodo.isLeaf()) {
					if (x > 47) return;
				} else if (x > 25) return;

			boolean isSelected = !(nodo.isSelected());
			nodo.setSelected(isSelected);
			
			if (nodo.children() != null) {
				Enumeration en = nodo.children();
				while (en.hasMoreElements()) {
					((RedNodoArbol)en.nextElement()).setSelected(isSelected);					
				}
			}
			
			tree.expandPath(path);
			((DefaultTreeModel)tree.getModel()).nodeChanged(nodo);
			if (row == 0) {
				tree.revalidate();
				tree.repaint();
			}
		}
	}
	
}
