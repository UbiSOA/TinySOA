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
import javax.swing.table.*;

/*******************************************************************************
 * Clase que implementa una tabla personalizada.
 * 
 * @author		Edgardo Avilés López
 * @version	0.3, 07/25/2006
 ******************************************************************************/
public class Tabla extends JTable {
	private static final long serialVersionUID = 9181178032329545226L;
	
	/** Constructor básico de la clase. */
	public Tabla() {
		super();
	}
	
	/***************************************************************************
	 * Constructor normal de la clase.
	 * 
	 * @param modelo	Modelo de la tabla
	 **************************************************************************/
	public Tabla(TableModel modelo) {
		super(modelo);
		setGridColor(new Color(215, 217, 220));
		setFont(new Font("Arial",Font.PLAIN,12));
		getTableHeader().setFont(new Font("Arial", Font.PLAIN, 12));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		((DefaultTableCellRenderer)getTableHeader().getDefaultRenderer()).
				setHorizontalAlignment(SwingConstants.CENTER);
		((DefaultTableCellRenderer)getTableHeader().getDefaultRenderer()).
				setVerticalAlignment(SwingConstants.CENTER);
		((DefaultTableCellRenderer)getTableHeader().getDefaultRenderer()).
				setPreferredSize(new Dimension(50,20));
	}

	/***************************************************************************
	 * Prepara el <i>renderer</i> de la celda de la tabla.
	 **************************************************************************/
	public Component prepareRenderer(TableCellRenderer renderer,
			int rowIndex, int vColIndex) {
		Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
		
		if (vColIndex == 2) {
			String value = "";
			Object val = getValueAt(rowIndex, vColIndex);
			if (val != null) value = val.toString();
			if (value.compareTo("Lect.") == 0)
				c.setForeground(new Color(38, 130, 36));
			else if (value.compareTo("0x00") == 0)
				c.setForeground(new Color(38, 130, 36));
			else if (value.compareTo("Reg.") == 0)
				c.setForeground(new Color(230, 139, 44));
			else if (value.compareTo("0x01") == 0)
				c.setForeground(new Color(230, 139, 44));
			else c.setForeground(new Color(80, 80, 80));
		} else c.setForeground(new Color(80, 80, 80));
		
		if (isRowSelected(rowIndex)) c.setBackground(new Color(0xe8e8e8));
		else if (rowIndex % 2 == 0) c.setBackground(new Color(0xf3f3f3));
		else c.setBackground(getBackground());
		return c;
	}
	
	/***************************************************************************
	 * Regresa si la celda es editable.
	 **************************************************************************/
	public boolean isCellEditable(int rowIndex, int vColIndex) {
		return false;
	}

	/***************************************************************************
	 * Vacía el contenido de la tabla.
	 **************************************************************************/
	public synchronized void clearTable() {
		DefaultTableModel model = (DefaultTableModel)((TableSorter)this.
				getModel()).getTableModel();
		int numrows = model.getRowCount();
		for(int i = numrows - 1; i >=0; i--) model.removeRow(i);
	}
	
}
