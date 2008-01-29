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
 * Stores readings results in a table
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/24/2006
 ******************************************************************************/
public class SensedData {
	
	private int paramType, paramData, nodeId;
	private boolean onlyCookedValues;
	private long readingTime;
	private double readingValue;
	
	/***************************************************************************
	 * Class constructor that defines all of the object's values
	 * 
	 * @param nodeId			Node ID of whom this lecture comes from
	 * @param paramType			Sensed parameter type
	 * @param paramData			ADC value of the parameter
	 * @param readingTime		Reading time
	 * @param onlyCookedValues	False if you want raw data 
	 **************************************************************************/
	public SensedData(int nodeId, int paramType, int paramData,
			long readingTime, boolean onlyCookedValues) {
		this.nodeId = nodeId;
		this.paramType = paramType;
		this.paramData = paramData;
		this.readingTime = readingTime;
		this.onlyCookedValues = onlyCookedValues;
		
		if (paramType == Constants.SENSOR_TEMP)
			readingValue = Converter.adcToTempD(paramData);
		else if (paramType == Constants.SENSOR_VOLT)
			readingValue = Converter.adcToVoltD(paramData);
		else readingValue = paramData;
	}
	
	/***************************************************************************
	 * Class constructor that defines the main values of the object.
	 * 
	 * @param paramType			Sensed parameter type
	 * @param paramData			ADC value of the parameter
	 * @param onlyCookedValues	False if you want raw data
	 **************************************************************************/
	public SensedData(int paramType, int paramData, boolean onlyCookedValues) {
		this(0, paramType, paramData, new Date().getTime(), onlyCookedValues);
	}

	/***************************************************************************
	 * Sets the node ID of this reading.
	 * 
	 * @param nodeId	Node ID
	 **************************************************************************/
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	
	/***************************************************************************
	 * Gets the node ID of this reading.
	 * 
	 * @return	The node ID
	 **************************************************************************/
	public int getNodeId() {
		return nodeId;
	}

	/***************************************************************************
	 * Gets the time of this reading.
	 * 
	 * @return	Time of this reading
	 **************************************************************************/
	public long getReadingTime() {
		return readingTime;
	}
	
	/***************************************************************************
	 * Gets this reading's value.
	 * 
	 * @return	A double with this readings value
	 **************************************************************************/
	public double getReadingValue() {
		return readingValue;
	}
	
	/***************************************************************************
	 * Gets a string that is used as <i>tooltip</i> of the cell's table. This 
	 * includes instance information.
	 * 
	 * @return	A string with the objects information
	 **************************************************************************/
	public String getTooltip() {
		String tip = "<html><table cellspacing=\"0\" cellpadding=\"1\" " +
				"border=\"0\">";
		String t = Converter.sensorLabel(paramType, 0);
		if (t.compareTo("v0") == 0) t = "Nulo";
		tip += r("Tipo:", t);
		
		if (paramType == Constants.SENSOR_TEMP)
			tip += r("Valor:", Converter.adcToTemp(paramData));
		if (paramType == Constants.SENSOR_VOLT)
			tip += r("Valor:", Converter.adcToVolt(paramData));
		
		tip += r("Dec:", paramData + "");
		tip += r("Hex:", Converter.intToHex(paramData));
		return tip + "</table></html>";
	}

	/***************************************************************************
	 * Creates a row of the HTML table returned by <code>getTooltip()</code>.
	 * 
	 * @param k	Row key
	 * @param v	Row value
	 * @return	A string with a rows information
	 **************************************************************************/
	private String r(String k, String v) {
		return "<tr><td align=\"right\">&nbsp;" + k +
			"</td><td align=\"left\"><b>" + v + "</b>&nbsp;</td></tr>";
	}

	/***************************************************************************
	 * Turns the current instance into a string with information about the instance.
	 * 
	 * @return	A string with information of this instance
	 **************************************************************************/
	public String toString() {
		if (onlyCookedValues) {
			if (paramType == Constants.SENSOR_TEMP)
				return Converter.adcToTemp(paramData);
			else if (paramType == Constants.SENSOR_VOLT)
				return Converter.adcToVolt(paramData);
			else return paramData + "";
		}
		else return Converter.intToHex(paramData, 4);
	}
	
}
