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
import java.util.*;
import javax.swing.*;
import java.text.BreakIterator;

import net.tinyos.tinysoa.common.*;

/*******************************************************************************
 * Renderer for the network selection list
 * 
 * @author		Edgardo Avilés López
 * @version	0.1
 ******************************************************************************/
public class MonitorListCellRenderer implements ListCellRenderer {
	
	private ImageIcon icon;
	
	/***************************************************************************
	 * Class constructor
	 * 
	 * @param icon	Icono de una red
	 **************************************************************************/
	public MonitorListCellRenderer(ImageIcon icon) {
		this.icon = icon;
	}
	
	/***************************************************************************
	 * Makes a multiline string inserting <pre><br></pre> where the line width
	 * exceeds the indicated pixel longitude.
	 * 
	 * @param text	String to adjust
	 * @param font	Font to use
	 * @param width	Desired width
	 * @return			A multiline string
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
	 * Implements the design of the network list element
	 * 
	 * @param list		Parent list
	 * @param value		Value of the list element
	 * @param index		Element index
	 * @param selected	True if its selected
	 * @param hasFocus	True if has focus
	 * @return				A panel with the desired design 
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
