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

/*******************************************************************************
 * Utility class that allows a table cell to have a tooltip for the cell value.
 * 
 * @author		Edgardo	Avilés López
 * @version	0.1, 07/25/2006
 ******************************************************************************/
public class CellTableTooltip {

	private String value;
	
	/***************************************************************************
	 * Main constructor of the class.
	 * 
	 * @param value	Value for the table cell
	 **************************************************************************/
	public CellTableTooltip(String value) {
		this.value = value;
	}
	
	/***************************************************************************
	 * Returns the string to be used as tooltip.
	 * 
	 * @return	A tooltip string
	 **************************************************************************/
	public String getTooltip() {
		return value;
	}
	
	/***************************************************************************
	 * Returns the value of the cell.
	 **************************************************************************/
	public String toString() {
		return value;
	}
	
}
