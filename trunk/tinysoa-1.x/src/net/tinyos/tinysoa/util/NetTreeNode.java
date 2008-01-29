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
import javax.swing.*;
import javax.swing.tree.*;

/*******************************************************************************
 * Handles the information of a tree node, representing a node in the sensor network
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/25/2006
 ******************************************************************************/
public class NetTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 5275043962674696848L;
	
	protected boolean isSelected;
	protected Icon stateIcon;

	/***************************************************************************
	 * Basic constructor
	 * 
	 * @param object	Name of the node
	 **************************************************************************/
	public NetTreeNode(Object object) {
		this(object, UIManager.getIcon("Tree.leafIcon"));
	}
	
	/***************************************************************************
	 * Regular constructor
	 * 
	 * @param object	Name of the node
	 * @param icon	Node icon
	 **************************************************************************/
	public NetTreeNode(Object object, Icon icon) {
		super(object);
		isSelected = true;
		stateIcon = icon;
	}
	
	/***************************************************************************
	 * Sets selected state
	 * 
	 * @param isSelected	True if the node is selected
	 **************************************************************************/
	@SuppressWarnings("unchecked")
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		
		if (children != null) {
			Enumeration e = children.elements();
			while (e.hasMoreElements()) {
				NetTreeNode node = (NetTreeNode)e.nextElement();
				node.setSelected(true);
			}
		}
	}
	
	/***************************************************************************
	 * Gets selected state
	 * 
	 * @return	True if the node is selected
	 **************************************************************************/
	public boolean isSelected() {
		return isSelected;
	}
	
	/***************************************************************************
	 * Sets the nodes icon
	 * 
	 * @param icon	Node icon
	 **************************************************************************/
	public void setIcon(Icon icon) {
		this.stateIcon = icon;
	}

	/***************************************************************************
	 * Gets the node icon
	 * 
	 * @return	Node icon
	 **************************************************************************/
	public Icon getIcon() {
		return stateIcon;
	}

}
