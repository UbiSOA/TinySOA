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

import java.text.*;

import net.tinyos.tinysoa.common.*;

/*******************************************************************************
 * Clase convertidor de parámetros de lectura, y de números e identificadores de
 * parámetros, actuadores, y sensores.
 * 
 * @author		Edgardo Avilés López
 * @version	0.3, 07/24/2006 
 ******************************************************************************/
public final class Convertidor {
	
	private static NumberFormat nf1d = new DecimalFormat("0.0");
	private static NumberFormat nf2d = new DecimalFormat("0.00");
	private static NumberFormat nf3d = new DecimalFormat("0.000");
	
	/***************************************************************************
	 * Convierte una lectura ADC a su equivalente de temperatura en grados
	 * centígrados con precisión de un digito decimal.
	 * 
	 * @param ADC	Lectura ADC del sensor
	 * @return		Una cadena con la temperatura en grados centígrados
	 **************************************************************************/
	public static String adcToTemp(int ADC) {
		return adcToTemp(ADC, 1);
	}
	
	/***************************************************************************
	 * Convierte una lectura ADC a su equivalente de temperatura en grados
	 * centígrados con la precisión indicada.
	 * 
	 * @param ADC			Lectura ADC del sensor
	 * @param precision	Precisión deseada
	 * @return				Una cadena con la temperatura en grados centígrados
	 **************************************************************************/
	public static String adcToTemp(int ADC, int precision) {
		double a , b, c, R1, Rthr, ADC_FS, kelvin;
		a = 0.00130705;
		b = 0.000214381;
		c = 0.000000093;
		R1 = 10000;
		ADC_FS = 1023;
		Rthr = R1 * (ADC_FS - ADC) / ADC;
		kelvin = 1 / (a + b * Math.log(Rthr) + c *
				Math.pow(Math.log(Rthr), 3.0));
		return redondear(kelvin - 273.15, precision) + " °C";
	}
	
	/***************************************************************************
	 * Convierte una lectura ADC a su equivalente de temperatura grados
	 * centígrados.
	 * 
	 * @param ADC	Lectura ADC del sensor
	 * @return		Un doble con la temperatura en grados centígrados
	 **************************************************************************/
	public static double adcToTempD(int ADC) {
		double a , b, c, R1, Rthr, ADC_FS, kelvin;
		a = 0.00130705;
		b = 0.000214381;
		c = 0.000000093;
		R1 = 10000;
		ADC_FS = 1023;
		Rthr = R1 * (ADC_FS - ADC) / ADC;
		kelvin = 1 / (a + b * Math.log(Rthr) + c *
				Math.pow(Math.log(Rthr), 3.0));
		return Double.parseDouble(redondear(kelvin - 273.15, 3));
	}
	
	/***************************************************************************
	 * Convierte una lectura ADC a su equivalente en voltaje con precisión de
	 * dos dígitos decimales.
	 * 
	 * @param ADC	Lectura ADC del sensor
	 * @return		Una cadena con el voltaje equivalente
	 **************************************************************************/
	public static String adcToVolt(int ADC) {
		return redondear(ADC / 1000.0, 2) + " v";
	}
	
	/***************************************************************************
	 * Convierte una lectura ADC a su equivalente en voltaje.
	 * 
	 * @param ADC	Lectura ADC del sensor
	 * @return		Un doble con el voltaje equivalente
	 **************************************************************************/
	public static double adcToVoltD(int ADC) {
		return Double.parseDouble(redondear(ADC / 1000.0, 3));
	}
	
	/***************************************************************************
	 * Convierte un número ID de un nodo a una representación en cadena del
	 * número de identificación del nodo. Si convertir es falso esta regresa
	 * una cadena en hexadecimal equivalente al número de ID.
	 * 
	 * @param i			ID del nodo
	 * @param convertir	Verdadero si hay que convertir el número
	 * @return				Una cadena con la identificación del nodo
	 **************************************************************************/
	public static String intToId(int i, boolean convertir) {
		if (convertir) {
			if (i == 0x7e) return "-";
			else return i + "";
		} else return Convertidor.intToHex(i, 2);
	}
	
	/***************************************************************************
	 * Devuelve una cadena con el tipo de actuador indicado por <code>i</code>.
	 * 
	 * @param i	Tipo de actuador
	 * @return		Una cadena con el nombre abreviado del actuador
	 * @see			Constantes
	 **************************************************************************/
	public static String intToActuador(int i) {
		String s = "";
		if (i == Constantes.ACTUADOR_BOCINA)		s = "Boc";
		if (i == Constantes.ACTUADOR_LED_AMARILLO)	s = "LedAm";
		if (i == Constantes.ACTUADOR_LED_AZUL)		s = "LedAz";
		if (i == Constantes.ACTUADOR_LED_ROJO)		s = "LedRo";
		if (i == Constantes.ACTUADOR_LED_VERDE)		s = "LedVe";
		return s;
	}

	/***************************************************************************
	 * Devuelve una cadena con el tipo de mensaje indicado por <code>i</code>.
	 * Si convertir es falso esta devuelve el tipo en una cadena hexadecimal.
	 * 
	 * @param i			Tipo de mensaje
	 * @param convertir	Verdadero si hay que convertir el número
	 * @return				Una cadena con el tipo de mensaje
	 * @see					Constantes
	 **************************************************************************/
	public static String intToTipo(int i, boolean convertir) {
		String[] TIPOS = {"Lect.", "Reg.", "Act. Act.", "Des. Act.", "Duerme",
				"Desp.", "Cam. D.R."};
		if (convertir) return TIPOS[i];
		else return Convertidor.intToHex(i, 2);
	}
	
	/***************************************************************************
	 * Devuelve una cadena con el <i>sensor board</i> indicado por
	 * <code>i</code>.
	 * 
	 * @param i	Tipo de <i>sensor board</i>
	 * @return		Cadena con el tipo de <i>sensor board</i>
	 * @see			Constantes
	 **************************************************************************/
	public static String intToSens(int i) {
		return intToSens(i, true);
	}

	/***************************************************************************
	 * Devuelve una cadena con el <i>sensor board</i> indicado por
	 * <code>i</code>. Si convertir es falso esta devuelve el número en una
	 * cadena hexadecimal.
	 * 
	 * @param i			Tipo de <i>sensor board</i>
	 * @param convertir	Verdadero si hay que convertir el número
	 * @return				Una cadena con el tipo de <i>sensor board</i>
	 * @see					Constantes
	 **************************************************************************/
	public static String intToSens(int i, boolean convertir) {
		String s = "-";
		if (i == 0x01) s = "MDA500";
		if (i == 0x02) s = "MTS510";
		if (i == 0x03) s = "MEP500";
		if (i == 0x80) s = "MDA400";
		if (i == 0x81) s = "MDA300";
		if (i == 0x82) s = "MTS101";
		if (i == 0x83) s = "MTS300";
		if (i == 0x84) s = "MTS310";
		if (i == 0x85) s = "MTS400";
		if (i == 0x86) s = "MTS420";
		if (i == 0x87) s = "MEP401";
		if (i == 0x90) s = "MDA320";
		if (i == 0xA0) s = "MSP410";
		if (convertir) return s;
		else return Convertidor.intToHex(i, 2);
	}
	
	/***************************************************************************
	 * Devuelve una cadena con el tipo de parámetro de sensado indicado por
	 * <code>i</code>, si no se encuentra un equivalente esta regresa la
	 * cadena "?". Si convertir es falso esta devuelve el número en una
	 * cadena hexadecimal.
	 * 
	 * @param i			Tipo de parámetro
	 * @param convertir	Verdadero si hay que convertir el número
	 * @return				Una cadena con el parámetro de sensado
	 * @see					Constantes
	 **************************************************************************/
	public static String intToSensParam(int i, boolean convertir) {
		String s = sensorEtiqueta(i, 0);
		if (s.compareTo("v0") == 0) s = "?";
		if (convertir) return s;
		else return Convertidor.intToHex(i, 4);
	}

	/***************************************************************************
	 * Devuelve una cadena con el tipo de parámetro de sensado indicado por
	 * <code>sensor</code>, si no se encuentra el tipo esta devuelve una
	 * cadena nula.
	 * 
	 * @param sensor	Tipo de parámetro (sensor)
	 * @return			Una cadena con el parámetro de sensado
	 * @see				Constantes
	 **************************************************************************/
	public static String sensorEtiqueta(int sensor) {
		String t = sensorEtiqueta(sensor, 0);
		if ("v0".compareTo(t) == 0) t = null;
		return t;
	}

	/***************************************************************************
	 * Devuelve una cadena con el tipo de parámetro de sensado incicado por
	 * <code>sensor</code>, si el tipo es <code>SENSOR_NULO</code> esta regresa
	 * una cadena "vX" donde X es la posición de <i>slot</i> indicado por
	 * <code>pos</code>.
	 * 
	 * @param sensor	Tipo de parámetro (sensor)
	 * @param pos		Posición en el <i>slot</i>
	 * @return			Una cadena con el parámetro de sensado
	 * @see				Constantes
	 **************************************************************************/
	public static String sensorEtiqueta(int sensor, int pos) {
		if (sensor == Constantes.SENSOR_NULO) return "v" + pos;
		else if (sensor == Constantes.SENSOR_TEMP)	return "Temp";
		else if (sensor == Constantes.SENSOR_LUZ) return "Luz";
		else if (sensor == Constantes.SENSOR_MAGX) return "MagX";
		else if (sensor == Constantes.SENSOR_MAGY) return "MagY";
		else if (sensor == Constantes.SENSOR_ACEX) return "AceX";
		else if (sensor == Constantes.SENSOR_ACEY) return "AceY";
		else if (sensor == Constantes.SENSOR_MIC) return "Mic";
		else if (sensor == Constantes.SENSOR_VOLT) return "Volt";
		else return sensor + "";
	}
	
	/***************************************************************************
	 * Devuelte el número de tipo de sensor correspondiente al nombre de
	 * parámetro de sensado indicado por <code>sensor</code>, si no se encuentra
	 * esta regresa <code>-1</code>.
	 * 
	 * @param sensor	Nombre del parámetro de sensado
	 * @return			El número de tipo de sensor
	 * @see				Constantes
	 **************************************************************************/
	public static int sensorEtiquetaToId(String sensor) {
		if (sensor.compareTo("Temp") == 0) return Constantes.SENSOR_TEMP;
		else if (sensor.compareTo("Luz") == 0) return Constantes.SENSOR_LUZ;
		else if (sensor.compareTo("MagX") == 0) return Constantes.SENSOR_MAGX;
		else if (sensor.compareTo("MagY") == 0) return Constantes.SENSOR_MAGY;
		else if (sensor.compareTo("AceX") == 0) return Constantes.SENSOR_ACEX;
		else if (sensor.compareTo("AceY") == 0) return Constantes.SENSOR_ACEY;
		else if (sensor.compareTo("Mic") == 0) return Constantes.SENSOR_MIC;
		else if (sensor.compareTo("Volt") == 0) return Constantes.SENSOR_VOLT;
		return -1;
	}
	
	/***************************************************************************
	 * Regresa una cadena con la representación hexadecimal del número indicado
	 * en <code>i</code>.
	 * 
	 * @param i	Número a convertir
	 * @return		Una cadena con la representación hexadecimal
	 **************************************************************************/
	public static String intToHex(int i) {
		return intToHex(i, 0);
	}

	/***************************************************************************
	 * Regresa una cadena con la representación hexadecimal del número indicado
	 * en <code>i</code> con la longitud deseada.
	 * 
	 * @param i		Número a convertir
	 * @param longitud	Longitud deseada para la cadena resultado
	 * @return			Una cadena con la representación hexadecimal
	 **************************************************************************/
	public static String intToHex(int i, int longitud) {
		String h = Long.toHexString(i);
		while (h.length() < longitud)
			h = "0" + h;
		return "0x" + h;
	}
	
	/***************************************************************************
	 * Si <code>convertir</code> es falso, esta regresa una cadena con la
	 * representación en hexadecimal del número indicado en <code>i</code> con
	 * la longitud deseada. Si <code>convertir</code> es verdadero esta regresa
	 * una cadena con el número indicado por <code>i</code> sin conversión.s
	 * 
	 * @param i			Número a convertir
	 * @param longitud		Longitud deseada para la cadena resultado
	 * @param convertir	Falso si se desea la representación en hexadecimal
	 * @return				Una cadena con la representación del número en
	 * 						decimal o hexadecimal según sea el caso
	 **************************************************************************/
	public static String intToN(int i, int longitud, boolean convertir) {
		if (convertir) return i + "";
		else return Convertidor.intToHex(i, longitud);
	}

	/***************************************************************************
	 * Devuelve una cadena con el valor indicado en <code>val</code> redondeado
	 * al número de lugares decimales indicados.
	 * 
	 * @param val		Valor a redondear
	 * @param lugares	Número de decimales a redondear
	 * @return			Una cadena con el valor redondeado
	 **************************************************************************/
	public static String redondear(double val, int lugares) {
		if (lugares == 1) return nf1d.format(val);
		if (lugares == 2) return nf2d.format(val);
		if (lugares == 3) return nf3d.format(val);
	
		String p = ""; for (int i = 0; i < lugares; i++) p += "0";
		NumberFormat nf = new DecimalFormat("0." + p);
		return nf.format(val);
	}

}
