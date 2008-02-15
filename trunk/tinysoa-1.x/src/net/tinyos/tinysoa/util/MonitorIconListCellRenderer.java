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

import java.awt.*;
import javax.swing.*;

/*******************************************************************************
 * Implements a list cell rendererer with an icon
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/25/2006
 ******************************************************************************/
public class MonitorIconListCellRenderer extends JLabel implements ListCellRenderer {
	private static final long serialVersionUID = -431382948314918856L;

	private ImageIcon[] icons;
	
	/***************************************************************************
	 * Class constructor
	 * 
	 * @param icons	Iconos de los elementos de la lista
	 **************************************************************************/
	public MonitorIconListCellRenderer(ImageIcon[] icons) {
		this.icons = icons;
	}
	
	/***************************************************************************
	 * Prepares the list element
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
		setIcon(icons[index]);
		setFont(list.getFont());
		
		return this;
	}

}
