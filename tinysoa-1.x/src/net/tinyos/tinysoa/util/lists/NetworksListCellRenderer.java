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

package net.tinyos.tinysoa.util.lists;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.text.BreakIterator;

import net.tinyos.tinysoa.common.*;

/*******************************************************************************
 * Implements a list cell renderer that is used to display a menu of available
 * providers such as available sensor networks.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1	07/25/2006
 ******************************************************************************/
public class NetworksListCellRenderer implements ListCellRenderer {
	
	private ImageIcon icon;
	
	/***************************************************************************
	 * Class constructor.
	 * 
	 * @param icon	Icon for a network
	 **************************************************************************/
	public NetworksListCellRenderer(ImageIcon icon) {
		this.icon = icon;
	}
	
	/***************************************************************************
	 * Produces a multiline string inserting &lt;pre&gt;&lt;br&gt;&lt;/pre&gt;
	 * where the line exceeds the desired pixel width by line.
	 * 
	 * @param text	String to adjust
	 * @param font	Font to use
	 * @param width	Desired width
	 * @return		A multiline string
	 **************************************************************************/
	private String wrapLine(String text, Font font, int width) {
		BreakIterator iterator = BreakIterator.getWordInstance(
				Locale.getDefault());
		iterator.setText(text);
		int start = iterator.first();
		int end = iterator.next();
		FontMetrics fm = new Button().getFontMetrics(font);
		String s = "";
		int len = 0;
		while (end != BreakIterator.DONE) {
			String word = text.substring(start,end);
			if( len + fm.stringWidth(word) > width ) {
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
	 * Implements the design of a network list element.
	 * 
	 * @param list		Parent list
	 * @param value		Value of the list element
	 * @param index		Element index
	 * @param selected	<code>True</code> if item is selected
	 * @param hasFocus	<code>True</code> if item has focus
	 * @return			A panel with the target design 
	 **************************************************************************/
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean selected, boolean hasFocus) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setOpaque(true);
		
		JLabel label = new JLabel(((Network)value).getName());
		label.setIcon(icon);
		label.setFont(new Font("Arial", Font.BOLD, 12));
		panel.add(label, BorderLayout.NORTH);
		
		String description = wrapLine(((Network)value).getDescription(),
				new Font("Arial", Font.PLAIN, 12), list.getWidth() - 35);
		
		label = new JLabel("<html>" + description +
				"<br><font color=\"#6382BF\">" + ((Network)value).getWsdl() +
				"</a></html>");
		label.setFont(new Font(list.getFont().getFamily(), Font.PLAIN,
				list.getFont().getSize()));
		label.setBorder(BorderFactory.createEmptyBorder(0,
				icon.getIconWidth() + label.getIconTextGap(),0,0));
		panel.add(label, BorderLayout.CENTER);
		
		if (selected)
			panel.setBackground(UIManager.getColor("List.selectionBackground"));
		else panel.setBackground(UIManager.getColor(list.getBackground()));
		
		if (hasFocus && selected) {
			panel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(
							UIManager.getColor("Tree.selectionBorderColor")),
					BorderFactory.createEmptyBorder(4,4,4,4)));
		} else if (selected) {
			panel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(
							UIManager.getColor("List.selectionBackground")),
					BorderFactory.createEmptyBorder(4,4,4,4)));			
		} else {
			panel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(list.getBackground()),
					BorderFactory.createEmptyBorder(4,4,4,4)));
		}
		
		return panel;
	}

}
