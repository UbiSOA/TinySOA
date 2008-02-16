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

package net.tinyos.tinysoa.util;

import java.text.*;

import net.tinyos.tinysoa.common.*;

/*******************************************************************************
 * One of the core utilities class. As suggested by the name, converts the main
 * data types and labels such as reading parameters, sensor identifiers,
 * actuators types, and raw sensor data.
 * 
 * @author		Edgardo Avilés López
 * @version	0.3, 07/24/2006 
 ******************************************************************************/
public final class Converter {
	
	private static NumberFormat nf1d = new DecimalFormat("0.0");
	private static NumberFormat nf2d = new DecimalFormat("0.00");
	private static NumberFormat nf3d = new DecimalFormat("0.000");
	
	/***************************************************************************
	 * Converts an ADC raw value to a temperature equivalent in Celsius degrees.
	 * Result is returned as a string with one digit precision.
	 * 
	 * @param ADC	Raw sensor reading
	 * @return		A string containing the temperature in Celsius degrees
	 **************************************************************************/
	public static String adcToTemp(int ADC) {
		return adcToTemp(ADC, 1);
	}
	
	/***************************************************************************
	 * Converts an ADC raw value to a temperature equivalent in Celsius degrees.
	 * Result is returned as a string with the given precision and the string
	 * " °C" added at the end.
	 * 
	 * @param ADC		Raw sensor reading
	 * @param precision	Desired precision
	 * @return			A string containing the temperature in Celsius degrees
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
		return roundStr(kelvin - 273.15, precision) + " °C";
	}
	
	/***************************************************************************
	 * Converts an ADC raw value to a temperature equivalent in Celsius degrees.
	 * Result is returned as a double.
	 * 
	 * @param ADC	Raw sensor reading
	 * @return		A double containing the temperature in Celsius degrees
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
		return Double.parseDouble(roundStr(kelvin - 273.15, 3));
	}
	
	/***************************************************************************
	 * Converts an ADC raw value to a voltage equivalent. Result is returned as
	 * a string with two digits precision and the string " v" added at the end.
	 * 
	 * @param ADC	Raw sensor reading
	 * @return		A string containing the voltage equivalent for the input
	 **************************************************************************/
	public static String adcToVolt(int ADC) {
		return roundStr(ADC / 1000.0, 2) + " v";
	}
	
	/***************************************************************************
	 * Converts an ADC raw value to a voltage equivalent. Result is returned as
	 * a double.
	 * 
	 * @param ADC	Raw sensor reading
	 * @return		A double containing the voltage equivalent for the input
	 **************************************************************************/
	public static double adcToVoltD(int ADC) {
		return Double.parseDouble(roundStr(ADC / 1000.0, 3));
	}
	
	/***************************************************************************
	 * Converts a node ID number to its equivalent string representation. If ID
	 * number equals the gateway station, returns the string "-". If
	 * <code>convert</code> is <code>false</code>, returns a string with the
	 * ID number in hexadecimal format.
	 * 
	 * @param id		Node ID
	 * @param convert	<code>False</code> to hexadecimal format
	 * @return			A string containing the node ID
	 **************************************************************************/
	public static String intToId(int id, boolean convert) {
		if (convert) {
			if (id == 0x7e) return "-";
			else return id + "";
		} else return Converter.intToHex(id, 2);
	}
	
	/***************************************************************************
	 * Converts an actuator type number to its equivalent string representation.
	 * 
	 * @param type	Input actuator type
	 * @return		A string with the name of the actuator
	 * @see			Constants
	 **************************************************************************/
	public static String intToActuator(int type) {
		String s = "";
		if (type == Constants.ACTUATOR_BUZZER)		s = "Buzz";
		if (type == Constants.ACTUATOR_LED_YELLOW)	s = "LedY";
		if (type == Constants.ACTUATOR_LED_BLUE)	s = "LedB";
		if (type == Constants.ACTUATOR_LED_RED)		s = "LedR";
		if (type == Constants.ACTUATOR_LED_GREEN)	s = "LedG";
		return s;
	}

	/***************************************************************************
	 * Converts a message type to its equivalent string representation. If
	 * <code>convert</code> is <code>false</code>, returns the input in
	 * hexadecimal format.
	 * 
	 * @param type		Input message type
	 * @param convert	<code>False</code> to hexadecimal format
	 * @return			A string with the message type
	 * @see				Constants
	 **************************************************************************/
	public static String intToType(int type, boolean convert) {
		String[] TYPES = {"Read", "Reg", "Act. Act.", "Des. Act.", "Sleep",
				"Wake Up", "Chg. Data Rate"};
		if (convert) return TYPES[type];
		else return Converter.intToHex(type, 2);
	}
	
	/***************************************************************************
	 * Converts a sensor board ID number to its equivalent string
	 * representation.
	 * 
	 * @param boardID	Sensor board ID number
	 * @return			A string with the sensor board name
	 * @see				Constants
	 **************************************************************************/
	public static String intToSens(int boardID) {
		return intToSens(boardID, true);
	}

	/***************************************************************************
	 * Converts a sensor board ID number to its equivalent string
	 * representation. If <code>convert</code> is <code>false</code> result is
	 * returned as an hexadecimal string.
	 * 
	 * @param boardID	Sensor board ID number
	 * @param convert	<code>False</code> to get an hexadecimal string
	 * @return			A string with the sensor board name
	 * @see				Constants
	 **************************************************************************/
	public static String intToSens(int boardID, boolean convert) {
		String s = "-";
		if (boardID == 0x01) s = "MDA500";
		if (boardID == 0x02) s = "MTS510";
		if (boardID == 0x03) s = "MEP500";
		if (boardID == 0x80) s = "MDA400";
		if (boardID == 0x81) s = "MDA300";
		if (boardID == 0x82) s = "MTS101";
		if (boardID == 0x83) s = "MTS300";
		if (boardID == 0x84) s = "MTS310";
		if (boardID == 0x85) s = "MTS400";
		if (boardID == 0x86) s = "MTS420";
		if (boardID == 0x87) s = "MEP401";
		if (boardID == 0x90) s = "MDA320";
		if (boardID == 0xA0) s = "MSP410";
		if (convert) return s;
		else return Converter.intToHex(boardID, 2);
	}
	
	/***************************************************************************
	 * Converts a sensor parameter type to a string with the form
	 * "v<code>X</code>" where <code>X</code> is the parameter type specified by
	 * <code>type</code>. If parameter type is <code>SENSOR_NULL</code>, returns
	 * the string "?". If <code>convert</code> is <code>false</code>, returns an
	 * hexadecimal string.
	 * 
	 * @param type		Sensor parameter type
	 * @param convert	<code>False</code> to get an hexadecimal string
	 * @return			A string with the sensor parameter name or a
	 * 					<code>null</code> value instead
	 * @see				Constants
	 **************************************************************************/
	public static String intToSensParam(int type, boolean convert) {
		String s = sensorLabel(type, 0);
		if (s.compareTo("v0") == 0) s = "?";
		if (convert) return s;
		else return Converter.intToHex(type, 4);
	}

	/***************************************************************************
	 * Converts a sensor parameter type to its equivalent string representation.
	 * if <code>type</code> is <code>SENSOR_NULL</code>, returns a
	 * <code>null</code> string.
	 * 
	 * @param type	Sensor parameter type
	 * @return		A string with the sensor parameter name
	 * @see			Constants
	 **************************************************************************/
	public static String sensorLabel(int type) {
		String t = sensorLabel(type, 0);
		if ("v0".compareTo(t) == 0) t = null;
		return t;
	}

	/***************************************************************************
	 * Converts a sensor parameter type to its equivalent string representation.
	 * If parameter type is <code>SENSOR_NULL</code>, returns a string with the
	 * format "v<code>X</code>" where <code>X</code> is the container position
	 * number specified by <code>pos</code>.
	 * 
	 * @param type	Sensor parameter type
	 * @param pos	Container position
	 * @return		A string with the parameter name, or a label string
	 * @see			Constants
	 **************************************************************************/
	public static String sensorLabel(int type, int pos) {
		if (type == Constants.SENSOR_NULL) return "v" + pos;
		else if (type == Constants.SENSOR_TEMP)	return "Temp";
		else if (type == Constants.SENSOR_LIGHT) return "Lght";
		else if (type == Constants.SENSOR_MAGX) return "MagX";
		else if (type == Constants.SENSOR_MAGY) return "MagY";
		else if (type == Constants.SENSOR_ACEX) return "AceX";
		else if (type == Constants.SENSOR_ACEY) return "AceY";
		else if (type == Constants.SENSOR_MIC) return "Mic";
		else if (type == Constants.SENSOR_VOLT) return "Volt";
		else return type + "";
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
		else if (sensor.compareTo("Lght") == 0) return Constants.SENSOR_LIGHT;
		else if (sensor.compareTo("MagX") == 0) return Constants.SENSOR_MAGX;
		else if (sensor.compareTo("MagY") == 0) return Constants.SENSOR_MAGY;
		else if (sensor.compareTo("AceX") == 0) return Constants.SENSOR_ACEX;
		else if (sensor.compareTo("AceY") == 0) return Constants.SENSOR_ACEY;
		else if (sensor.compareTo("Mic") == 0) return Constants.SENSOR_MIC;
		else if (sensor.compareTo("Volt") == 0) return Constants.SENSOR_VOLT;
		return -1;
	}
	
	/***************************************************************************
	 * Returns a string with the hexadecimal representation of the integer
	 * input value.
	 * 
	 * @param x	Integer input value
	 * @return	A string with the hexadecimal representation of the input
	 **************************************************************************/
	public static String intToHex(int x) {
		return intToHex(x, 0);
	}

	/***************************************************************************
	 * Returns a string with the hexadecimal representation of the integer
	 * input value. The result is padded to the specified length.
	 * 
	 * @param x			Integer input value
	 * @param length	Desired length for result string
	 * @return			A string with the hexadecimal representation of the
	 * 					input padded to the desired length
	 **************************************************************************/
	public static String intToHex(int x, int length) {
		String h = Long.toHexString(x);
		while (h.length() < length)
			h = "0" + h;
		return "0x" + h;
	}
	
	/***************************************************************************
	 * Converts an integer number to its equivalent string representation. If
	 * <code>toHex</code> is <code>true</code>, the result is a string with the
	 * hexadecimal equivalent for the input and it will be padded to the length
	 * specified by <code>hexLength</code>.
	 * 
	 * @param x			Input integer number to convert
	 * @param hexLength	Desired length if hexadecimal format
	 * @param toHex		<code>True</code> to get an hexadecimal equivalent
	 * @return			A string with the equivalent representation for the
	 * 					integer input or a string with the equivalent
	 * 					hexadecimal
	 **************************************************************************/
	public static String intToStr(int x, int hexLength, boolean toHex) {
		if (toHex) return Converter.intToHex(x, hexLength);
		else return x + "";
	}

	/***************************************************************************
	 * Converts the input number to a string with the same number rounded to the
	 * specified precision.
	 * 
	 * @param x			Double value to round
	 * @param precision	Precision of the result
	 * @return			A string with the rounded input number
	 **************************************************************************/
	public static String roundStr(double x, int precision) {
		if (precision == 1) return nf1d.format(x);
		if (precision == 2) return nf2d.format(x);
		if (precision == 3) return nf3d.format(x);
	
		String p = ""; for (int i = 0; i < precision; i++) p += "0";
		NumberFormat nf = new DecimalFormat("0." + p);
		return nf.format(x);
	}

}
