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

package net.tinyos.tinysoa.util.tables;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/*******************************************************************************
 * Implements a personalized table
 * 
 * @author		Edgardo Avilés López
 * @version	0.3, 07/25/2006
 ******************************************************************************/
public class MonitorTable extends JTable {
	private static final long serialVersionUID = 9181178032329545226L;
	
	/** Basic class constructor */
	public MonitorTable() {
		super();
	}
	
	/***************************************************************************
	 * Typic class constructor
	 * 
	 * @param tableModel	Table model
	 **************************************************************************/
	public MonitorTable(TableModel tableModel) {
		super(tableModel);
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
	 * Prepares the table cell <i>renderer</i>
	 **************************************************************************/
	public Component prepareRenderer(TableCellRenderer renderer,
			int rowIndex, int vColIndex) {
		Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
		
		if (vColIndex == 2) {
			String value = "";
			Object val = getValueAt(rowIndex, vColIndex);
			if (val != null) value = val.toString();
			if (value.compareTo("Read") == 0)
				c.setForeground(new Color(38, 130, 36));
			else if (value.compareTo("0x00") == 0)
				c.setForeground(new Color(38, 130, 36));
			else if (value.compareTo("Reg") == 0)
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
	 * Get the cell's editable state.
	 **************************************************************************/
	public boolean isCellEditable(int rowIndex, int vColIndex) {
		return false;
	}

	/***************************************************************************
	 * Clears the table
	 **************************************************************************/
	public synchronized void clearTable() {
		DefaultTableModel model = (DefaultTableModel)((TableSorter)this.
				getModel()).getTableModel();
		int numrows = model.getRowCount();
		for(int i = numrows - 1; i >=0; i--) model.removeRow(i);
	}
	
}
