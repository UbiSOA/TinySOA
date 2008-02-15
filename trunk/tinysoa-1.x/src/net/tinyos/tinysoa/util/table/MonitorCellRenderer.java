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

package net.tinyos.tinysoa.util.table;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import net.tinyos.tinysoa.util.SensedData;

/*******************************************************************************
 * Implements a personalized table cell <i>cell renderer</i>
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/25/2006
 ******************************************************************************/
public class MonitorCellRenderer extends JLabel
		implements TableCellRenderer {
	private static final long serialVersionUID = 1170124836368156719L;
	
	private int alignment;
	private boolean isBold;
	
	/***************************************************************************
	 * Class constructor
	 * 
	 * @param alignment	Text cell alignment	
	 * @param isBold	True if text is bold
	 **************************************************************************/
	public MonitorCellRenderer(int alignment, boolean isBold) {
		super();
		this.alignment = alignment;
		this.isBold = isBold;
	}

	/***************************************************************************
	 * Returns a component representing the cell.
	 **************************************************************************/
    public Component getTableCellRendererComponent(
    		JTable table, Object value, boolean isSelected, boolean hasFocus,
    		int rowIndex, int vColIndex) {

    	if (isBold) setFont(new Font(table.getFont().getFontName(),
    			Font.BOLD, table.getFont().getSize()));
    	else setFont(table.getFont());
    	setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    	this.setOpaque(true);
    	
        if (value != null) setText(value.toString()); else setText(" ");
        this.setHorizontalAlignment(alignment);
        
        if ((vColIndex > 4) && (value instanceof SensedData))
        	setToolTipText(((SensedData)value).getTooltip());
        
       if (value instanceof CellTableTooltip) {
    	   setToolTipText(((CellTableTooltip)value).getTooltip());
       }
        
        return this;
    }
    
    //--------------------------------------------------------------------------
    //
    //	Overriden methods for optimization purposes.
    //
    //==========================================================================

    public void validate() {}
    public void revalidate() {}
    protected void firePropertyChange(
    		String propertyName, Object oldValue, Object newValue) {}
    public void firePropertyChange(
    		String propertyName, boolean oldValue, boolean newValue) {}
    
}
