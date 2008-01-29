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
 * Clase que implementa un <i>renderer</i> de celda de tabla personalizado.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/25/2006
 ******************************************************************************/
public class TablaCeldaRenderer extends JLabel
		implements TableCellRenderer {
	private static final long serialVersionUID = 1170124836368156719L;
	
	private int alineacion;
	private boolean esNegrita;
	
	/***************************************************************************
	 * Constructor de la clase.
	 * 
	 * @param alineacion	Alineación del texto de la celda	
	 * @param esNegrita	Verdadero si el texto es en negritas
	 **************************************************************************/
	public TablaCeldaRenderer(int alineacion, boolean esNegrita) {
		super();
		this.alineacion = alineacion;
		this.esNegrita = esNegrita;
	}

	/***************************************************************************
	 * Devuelve un componente que representa a la celda.
	 **************************************************************************/
    public Component getTableCellRendererComponent(
    		JTable table, Object value, boolean isSelected, boolean hasFocus,
    		int rowIndex, int vColIndex) {

    	if (esNegrita) setFont(new Font(table.getFont().getFontName(),
    			Font.BOLD, table.getFont().getSize()));
    	else setFont(table.getFont());
    	setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    	this.setOpaque(true);
    	
        if (value != null) setText(value.toString()); else setText(" ");
        this.setHorizontalAlignment(alineacion);
        
        if ((vColIndex > 4) && (value instanceof SensedData))
        	setToolTipText(((SensedData)value).getTooltip());
        
       if (value instanceof CellTableTooltip) {
    	   setToolTipText(((CellTableTooltip)value).getTooltip());
       }
        
        return this;
    }
    
    //--------------------------------------------------------------------------
    //
    //   Métodos sobreescritos por motivos de optimización.
    //
    //==========================================================================

    public void validate() {}
    public void revalidate() {}
    protected void firePropertyChange(
    		String propertyName, Object oldValue, Object newValue) {}
    public void firePropertyChange(
    		String propertyName, boolean oldValue, boolean newValue) {}
    
}
