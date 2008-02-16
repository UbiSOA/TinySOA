/*
 *  Copyright 2006 Edgardo Avilés López
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *    
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 ******************************************************************************/

package net.tinyos.tinysoa.util.trees;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;

/*******************************************************************************
 * Implements a tree node listener.
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
