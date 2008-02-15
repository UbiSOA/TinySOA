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
