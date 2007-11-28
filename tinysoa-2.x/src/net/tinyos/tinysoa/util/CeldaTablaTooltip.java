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

/*******************************************************************************
 * Clase utilería que permite que el valor de la celda de una tabla tenga
 * un <i>tooltip</i> con el valor de la celda.
 * 
 * @author		Edgardo	Avilés López
 * @version	0.1, 07/25/2006
 ******************************************************************************/
public class CeldaTablaTooltip {

	private String valor;
	
	/***************************************************************************
	 * Constructor principal de la clase.
	 * 
	 * @param valor	Valor de la celda
	 **************************************************************************/
	public CeldaTablaTooltip(String valor) {
		this.valor = valor;
	}
	
	/***************************************************************************
	 * Devuelve la cadena a utilizarse como <i>tooltip</i>.
	 * 
	 * @return	Valor de la celda
	 **************************************************************************/
	public String getTooltip() {
		return valor;
	}
	
	/***************************************************************************
	 * Devuelve el valor de la celda.
	 **************************************************************************/
	public String toString() {
		return valor;
	}
	
}
