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
 * Class converter parameters reading, as well as numbers and identifiers  
 * of parameters, actuators, and sensors.
 * 
 * @author		Edgardo Avilés López
 * @version	0.3, 07/24/2006 
 ******************************************************************************/
public final class Converter {
	
	private static NumberFormat nf1d = new DecimalFormat("0.0");
	private static NumberFormat nf2d = new DecimalFormat("0.00");
	private static NumberFormat nf3d = new DecimalFormat("0.000");
	
	/***************************************************************************
	 * Converts a reading ADC to the equivalent of temperature in degrees 
	 * Celsius with precision of a decimal digit.
	 * 
	 * @param ADC	Reading ADC of sensor
	 * @return		A string with the temperature in degree celsius 
	 * 	 **************************************************************************/
	public static String adcToTemp(int ADC) {
		return adcToTemp(ADC, 1);
	}
	
	/***************************************************************************
	 * Converts a reading ADC to the equivalent of temperature in degrees 
	 * Celsius with the precision indicated.
	 * 
	 * @param ADC			Reading ADC of sensor
	 * @param precision	    Desired precision
	 * @return				A string with the temperature in degree celsius 
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
		return rounding(kelvin - 273.15, precision) + " °C";
	}
	
	/***************************************************************************
	 * 	Converts a reading ADC to the equivalent temperature in degrees celsius .
	 * 
	 * @param ADC	Reading ADC of sensor
	 * @return		A double with the temperature in degree celsius 
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
		return Double.parseDouble(rounding(kelvin - 273.15, 3));
	}
	
	/***************************************************************************
	 * Converts a reading your ADC voltage equivalent to an accuracy 
	 * of two decimal digits.
	 * 
	 * @param ADC	Reading ADC of sensor
	 * @return		A string with the voltage equivalent
	 **************************************************************************/
	public static String adcToVolt(int ADC) {
		return rounding(ADC / 1000.0, 2) + " v";
	}
	
	/***************************************************************************
	 * Convert a reading ADC to equivalent in voltage.
	 * 
	 * @param ADC	Reading ADC of sensor
	 * @return		A double with the voltage equivalent
	 **************************************************************************/
	public static double adcToVoltD(int ADC) {
		return Double.parseDouble(rounding(ADC / 1000.0, 3));
	}
	
	/***************************************************************************
	 * Turn an ID number from a node to a string representation in the 
	 * identification number of the node. If it is wrong to turn this returns 
	 * a string hex equivalent to the number of ID.
	 * 
	 * @param i			ID of node
	 * @param convert	True if Whether to convert the numbers
	 * @return		    A string with the identification of node
	 **************************************************************************/
	public static String intToId(int i, boolean convert) {
		if (convert) {
			if (i == 0x7e) return "-";
			else return i + "";
		} else return Converter.intToHex(i, 2);
	}
	
	/***************************************************************************
	 * Returns a string with the type of actuator indicated by <code> i </ code>.
	 * 
	 * @param i	Type of actuator
	 * @return		A string with the name of actuator
	 * @see			Constants
	 **************************************************************************/
	public static String intToActuator(int i) {
		String s = "";
		if (i == Constants.ACTUATOR_BUZZER)		s = "Buzz";
		if (i == Constants.ACTUATOR_LED_YELLOW)	s = "LedY";
		if (i == Constants.ACTUATOR_LED_BLUE)		s = "LedB";
		if (i == Constants.ACTUATOR_LED_RED)		s = "LedR";
		if (i == Constants.ACTUATOR_LED_GREEN)		s = "LedG";
		return s;
	}

	/***************************************************************************
	 * Returns a string with the type of message indicated by <code> i </ code>. 
     * If it is wrong to turn this type returns in a hex string.
	 * 
	 * @param i			Type of message
	 * @param convert	True if need convert the number
	 * @return				A string with the type of message
	 * @see					Constants
	 **************************************************************************/
	public static String intToType(int i, boolean convert) {
		String[] TYPES = {"Read", "Reg", "Act. Act.", "Des. Act.", "Sleep",
				"Wake Up", "Chg. Data Rate"};
		if (convert) return TYPES[i];
		else return Converter.intToHex(i, 2);
	}
	
	/***************************************************************************
	 * Returns a string with the <i>sensor board</i> indicated for
	 * <code>i</code>.
	 * 
	 * @param i	Type of <i>sensor board</i>
	 * @return		String with the type of <i>sensor board</i>
	 * @see			Constants
	 **************************************************************************/
	public static String intToSens(int i) {
		return intToSens(i, true);
	}

	/***************************************************************************
	 * Returns a string with the <i>sensor board</i> indicated for
	 * <code>i</code>. If convert is false, returns returns the number in a
	 * string hex.
	 * 
	 * @param i			Type of <i>sensor board</i>
	 * @param convert	True if need convert the number
	 * @return			A string with the type of <i>sensor board</i>
	 * @see				Constants
	 **************************************************************************/
	public static String intToSens(int i, boolean convert) {
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
		if (convert) return s;
		else return Converter.intToHex(i, 2);
	}
	
	/***************************************************************************
	 * Returns a string with the type parameter sensing indicated by 
	 * <code> i </ code> because he is not an equivalent returns this chain? ". 
	 * If this is false returns convert the number into a hex string.
	 * 
	 * @param i			Type of parameter
	 * @param convert	True if need convert the number
	 * @return			A string with the parameter of sense
	 * @see				Constants
	 **************************************************************************/
	public static String intToSensParam(int i, boolean convert) {
		String s = sensorLabel(i, 0);
		if (s.compareTo("v0") == 0) s = "?";
		if (convert) return s;
		else return Converter.intToHex(i, 4);
	}

	/***************************************************************************
	 * Returns a string with the type parameter sensing sensor indicated by 
	 * <code> </ code> because he is not the type that returns a null string.
	 * 
	 * @param sensor	Type of parameter (sensor)
	 * @return			A string with the parameter of sensing
	 * @see				Constants
	 **************************************************************************/
	public static String sensorLabel(int sensor) {
		String t = sensorLabel(sensor, 0);
		if ("v0".compareTo(t) == 0) t = null;
		return t;
	}

	/***************************************************************************
	 * Returns a string with the type parameter sensing indicated by 
	 * <code> sensor </ code> if the type is <code> SENSOR_NULO </ code> 
	 * this returns a string "vX" where X is the position of slot <i> </ i> 
	 * indicated by <code> pos </ code>.
	 * 
	 * @param sensor	Type of parameter (sensor)
	 * @param pos		Position in the <i>slot</i>
	 * @return			A string with the parameter of sensing
	 * @see				Constants
	 **************************************************************************/
	public static String sensorLabel(int sensor, int pos) {
		if (sensor == Constants.SENSOR_NULL) return "v" + pos;
		else if (sensor == Constants.SENSOR_TEMP)	return "Temp";
		else if (sensor == Constants.SENSOR_LIGHT) return "Light";
		else if (sensor == Constants.SENSOR_MAGX) return "MagX";
		else if (sensor == Constants.SENSOR_MAGY) return "MagY";
		else if (sensor == Constants.SENSOR_ACEX) return "AceX";
		else if (sensor == Constants.SENSOR_ACEY) return "AceY";
		else if (sensor == Constants.SENSOR_MIC) return "Mic";
		else if (sensor == Constants.SENSOR_VOLT) return "Volt";
		else return sensor + "";
	}
	
	/***************************************************************************
	 * Returns the number of sensor type for name of
	 * parameter of sensing indicated for <code>sensor</code>, if this is not 
	 * returned <code>-1</code>.
	 * 
	 * @param sensor	Name of parameter of sensing
	 * @return			Number of sensor type 
	 * @see				Constants
	 **************************************************************************/
	public static int sensorLabelToId(String sensor) {
		if (sensor.compareTo("Temp") == 0) return Constants.SENSOR_TEMP;
		else if (sensor.compareTo("Light") == 0) return Constants.SENSOR_LIGHT;
		else if (sensor.compareTo("MagX") == 0) return Constants.SENSOR_MAGX;
		else if (sensor.compareTo("MagY") == 0) return Constants.SENSOR_MAGY;
		else if (sensor.compareTo("AceX") == 0) return Constants.SENSOR_ACEX;
		else if (sensor.compareTo("AceY") == 0) return Constants.SENSOR_ACEY;
		else if (sensor.compareTo("Mic") == 0) return Constants.SENSOR_MIC;
		else if (sensor.compareTo("Volt") == 0) return Constants.SENSOR_VOLT;
		return -1;
	}
	
	/***************************************************************************
	 * Returns a string with the representation hex of number indicated
	 * in <code>i</code>.
	 * 
	 * @param i	   Number to convert
	 * @return	   A string with the representation hex
	 **************************************************************************/
	public static String intToHex(int i) {
		return intToHex(i, 0);
	}

	/***************************************************************************
	 * Return a string with the representation hex of number indicated
	 * in <code>i</code> with the the desired length.
	 * 
	 * @param i		Number to convert
	 * @param length	Desired length for string result
	 * @return			A string with the representation hex
	 **************************************************************************/
	public static String intToHex(int i, int length) {
		String h = Long.toHexString(i);
		while (h.length() < length)
			h = "0" + h;
		return "0x" + h;
	}
	
	/***************************************************************************
	 * If <code> convert </ code> is false, it returns a string representation 
	 * of hex numbers shown in <code> i </ code> with the desired length. 
	 * If <code> convert </ code> is true that returns a string with the number 
	 * indicated by <code> i </ code> without converted.s
	 * 
	 * @param i			Number to convert
	 * @param length	Desired length for the string result
	 * @param convert	False if need the representation hex
	 * 	 * @return		A string with the representation of number in
	 * 					decimal or hex
	 **************************************************************************/
	public static String intToN(int i, int length, boolean convert) {
		if (convert) return i + "";
		else return Converter.intToHex(i, length);
	}

	/***************************************************************************
	 * Returns a string with the value <code>val</code> rounded
	 * the number of decimals indicated.
	 * 
	 * @param val		Value to rounding
	 * @param places	Number of decimal to rounding
	 * @return			A string with the value round
	 *  **************************************************************************/
	public static String rounding(double val, int places) {
		if (places == 1) return nf1d.format(val);
		if (places == 2) return nf2d.format(val);
		if (places == 3) return nf3d.format(val);
	
		String p = ""; for (int i = 0; i < places; i++) p += "0";
		NumberFormat nf = new DecimalFormat("0." + p);
		return nf.format(val);
	}

}
