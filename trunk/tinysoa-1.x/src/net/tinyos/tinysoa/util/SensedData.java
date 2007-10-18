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

import java.util.*;

import net.tinyos.tinysoa.common.Constants;

/*******************************************************************************
 * Clase para almacenar los datos de una lectura en una tabla.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/24/2006
 ******************************************************************************/
public class SensedData {
	
	private int tipo, dato, nid;
	private boolean convertir;
	private long tiempo;
	private double valor;
	
	/***************************************************************************
	 * Constructor de la clase que define todos los valores del objeto.
	 * 
	 * @param nid			ID del nodo al que pertenece la lectura
	 * @param tipo			Tipo de parámetro de sensado
	 * @param dato			Valor ADC del parámetro
	 * @param tiempo		Tiempo de la lectura
	 * @param convertir	Falso si se desea sólo valores en hexadecimal
	 **************************************************************************/
	public SensedData(int nid, int tipo, int dato,
			long tiempo, boolean convertir) {
		this.nid = nid;
		this.tipo = tipo;
		this.dato = dato;
		this.tiempo = tiempo;
		this.convertir = convertir;
		
		if (tipo == Constants.SENSOR_TEMP)
			valor = Converter.adcToTempD(dato);
		else if (tipo == Constants.SENSOR_VOLT)
			valor = Converter.adcToVoltD(dato);
		else valor = dato;
	}
	
	/***************************************************************************
	 * Constructor de la clase que define los valores principales del objeto.
	 * 
	 * @param tipo			Tipo de parámetro de sensado
	 * @param dato			Valor ADC del parámetro
	 * @param convertir	Falso si se desea sólo valores en hexadecimal
	 **************************************************************************/
	public SensedData(int tipo, int dato, boolean convertir) {
		this(0, tipo, dato, new Date().getTime(), convertir);
	}

	/***************************************************************************
	 * Define el ID del nodo al que pertenece la lectura.
	 * 
	 * @param nid	ID del nodo
	 **************************************************************************/
	public void defNid(int nid) {
		this.nid = nid;
	}
	
	/***************************************************************************
	 * Regresa el ID del nodo al que pertenece la lectura.
	 * 
	 * @return	El número ID del nodo
	 **************************************************************************/
	public int obtNid() {
		return nid;
	}

	/***************************************************************************
	 * Regresa el tiempo de la lectura
	 * 
	 * @return	Tiempo de la lectura
	 **************************************************************************/
	public long obtTiempo() {
		return tiempo;
	}
	
	/***************************************************************************
	 * Regresa el valor del parámetro.
	 * 
	 * @return	Un doble con el valor del parámetro
	 **************************************************************************/
	public double obtValor() {
		return valor;
	}
	
	/***************************************************************************
	 * Regresa una cadena a ser utilizada como <i>tooltip</i> de la celda de la
	 * tabla. Esta incluye la información de la instancia.
	 * 
	 * @return	Una cadena con la información del objeto
	 **************************************************************************/
	public String getTooltip() {
		String tip = "<html><table cellspacing=\"0\" cellpadding=\"1\" " +
				"border=\"0\">";
		String t = Converter.sensorLabel(tipo, 0);
		if (t.compareTo("v0") == 0) t = "Nulo";
		tip += r("Tipo:", t);
		
		if (tipo == Constants.SENSOR_TEMP)
			tip += r("Valor:", Converter.adcToTemp(dato));
		if (tipo == Constants.SENSOR_VOLT)
			tip += r("Valor:", Converter.adcToVolt(dato));
		
		tip += r("Dec:", dato + "");
		tip += r("Hex:", Converter.intToHex(dato));
		return tip + "</table></html>";
	}

	/***************************************************************************
	 * Crea un renglón de la tabla HTML devuelta por <code>getTooltip()</code>.
	 * 
	 * @param k	Llave del renglón
	 * @param v	Valor del renglón
	 * @return		Una cadena con información de un renglón
	 **************************************************************************/
	private String r(String k, String v) {
		return "<tr><td align=\"right\">&nbsp;" + k +
			"</td><td align=\"left\"><b>" + v + "</b>&nbsp;</td></tr>";
	}

	/***************************************************************************
	 * Convierte la instancia actual a una cadena la cual incluye la
	 * información del objeto.
	 * 
	 * @return	Una cadena con la información de la instancia
	 **************************************************************************/
	public String toString() {
		if (convertir) {
			if (tipo == Constants.SENSOR_TEMP)
				return Converter.adcToTemp(dato);
			else if (tipo == Constants.SENSOR_VOLT)
				return Converter.adcToVolt(dato);
			else return dato + "";
		}
		else return Converter.intToHex(dato, 4);
	}
	
}
