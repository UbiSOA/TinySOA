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
 * Implements a tree cell renderer to provide functionality of a JCheckBox inside
 * the tree.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/25/2006
 ******************************************************************************/
public class NetTreeNodeRenderer extends JPanel
		implements TreeCellRenderer {
	private static final long serialVersionUID = 9163424875253834373L;

	protected JCheckBox check;
	protected TreeLabel label;
	
	/***************************************************************************
	 * Basic class constructor.
	 **************************************************************************/
	public NetTreeNodeRenderer() {
		setLayout(null);
		add(check = new JCheckBox());
		add(label = new TreeLabel());
		check.setBackground(UIManager.getColor("Tree.textBackground"));
		label.setForeground(UIManager.getColor("Tree.textForeground"));
	}
	
	/***************************************************************************
	 * Gets the component that represents the tree cell.
	 **************************************************************************/
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		String stringValue = tree.convertValueToText(value, isSelected,
				expanded, leaf, row, hasFocus);
		setEnabled(tree.isEnabled());
		
		if (value instanceof NetTreeNode) {
			check.setSelected(((NetTreeNode)value).isSelected());
			label.setFont(tree.getFont());
			label.setText(stringValue);
			label.setSelected(isSelected);
			label.setFocus(hasFocus);
			
			label.setIcon(((NetTreeNode)value).getIcon());
		}

		return this;
	}
	
	/***************************************************************************
	 * Gets this components preferred size
	 **************************************************************************/
	public Dimension getPreferredSize() {
		Dimension d_check = check.getPreferredSize();
		Dimension d_label = label.getPreferredSize();
		return new Dimension(d_check.width + d_label.width,
				(d_check.height < d_label.height ?
						d_label.height: d_check.height));
	}
	
	/***************************************************************************
	 * Prepares position and dimention of cell components.
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
	 * Defines cell background color.
	 **************************************************************************/
	public void setBackground(Color color) {
		if (color instanceof ColorUIResource)
			color = null;
		super.setBackground(color);
	}
	
	/***************************************************************************
	 * Class that implements a label inside the tree node.
	 * 
	 * @author		Edgardo Avilés López
	 * @version	0.1, 07/25/2006
	 **************************************************************************/
	private class TreeLabel extends JLabel {
		private static final long serialVersionUID = -4518980052144690066L;

		boolean isSelected;
		boolean hasFocus;
		
		/** Basic class constructor */
		public TreeLabel() {}
		
		/***********************************************************************
		 * Defines the label background.
		 **********************************************************************/
		public void setBackground(Color color) {
			if (color instanceof ColorUIResource)
				color = null;
			super.setBackground(color);
		}
		
		/***********************************************************************
		 * Draws the component.
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
		 * Returns preferred size of the label.
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
		 * Sets if the label is selected.
		 * 
		 * @param isSelected	True if selected
		 **********************************************************************/
		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}
		
		/***********************************************************************
		 * Sets the component focus.
		 * 
		 * @param hasFocus	True if the component has focus
		 **********************************************************************/
		public void setFocus(boolean hasFocus) {
			this.hasFocus = hasFocus;
		}
		
	}

}
