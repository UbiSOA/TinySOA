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
 * Implements a tree node listener
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/25/2006
 ******************************************************************************/
public class SelectedNetTreeNodeListener extends MouseAdapter {
	
	private JTree tree;
	
	/***************************************************************************
	 * Class constructor
	 * 
	 * @param tree	Tree to listen
	 **************************************************************************/
	public SelectedNetTreeNodeListener(JTree tree) {
		this.tree = tree;
	}
	
	/***************************************************************************
	 * This method is executed when a click is detected in the tree. Selects or
	 * deselects, depending on the node being clicked.
	 **************************************************************************/
	@SuppressWarnings("unchecked")
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int row = tree.getRowForLocation(x, y);
		TreePath path = tree.getPathForRow(row);
		
		if (path != null) {
			
			if (e.getButton() != MouseEvent.BUTTON1) return;
			
			NetTreeNode node = (NetTreeNode)path.getLastPathComponent();
			
			if (e.getClickCount() == 1)
				if (node.isLeaf()) {
					if (x > 47) return;
				} else if (x > 25) return;

			boolean isSelected = !(node.isSelected());
			node.setSelected(isSelected);
			
			if (node.children() != null) {
				Enumeration en = node.children();
				while (en.hasMoreElements()) {
					((NetTreeNode)en.nextElement()).setSelected(isSelected);					
				}
			}
			
			tree.expandPath(path);
			((DefaultTreeModel)tree.getModel()).nodeChanged(node);
			if (row == 0) {
				tree.revalidate();
				tree.repaint();
			}
		}
	}
	
}
