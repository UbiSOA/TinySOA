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

/*******************************************************************************
 * ClassUtility that allows the value of the cell in a table has
 * a <i>tooltip</i> with the cell value.
 * 
 * @author		Edgardo	Avilés López
 * @version	0.1, 07/25/2006
 ******************************************************************************/
public class CellTableTooltip {

	private String value;
	
	/***************************************************************************
	 * Main constructor of the class.
	 * 
	 * @param value	Value of cell
	 **************************************************************************/
	public CellTableTooltip(String value) {
		this.value = value;
	}
	
	/***************************************************************************
	 * Returns the string to used with <i>tooltip</i>.
	 * 
	 * @return	Returns the value of cell
	 **************************************************************************/
	public String getTooltip() {
		return value;
	}
	
	/***************************************************************************
	 * Returns the value of cell.
	 **************************************************************************/
	public String toString() {
		return value;
	}
	
}
