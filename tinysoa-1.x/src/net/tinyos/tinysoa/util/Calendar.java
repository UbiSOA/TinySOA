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

import java.util.*;

/*******************************************************************************
 * Calendar class that provides utility methods that returns the current date or
 * time in various formats.
 * 
 * @author		Edgardo Avilés López
 * @version	0.2, 07/24/2006
 ******************************************************************************/
public final class Calendar {
	
	private static java.util.Calendar cal;
	private static String d, m, y, y2, h, mi, s, ms;
	
	/***************************************************************************
	 * Updates internal data with the current date and time.
	 **************************************************************************/
	public static void read() {
		cal = new GregorianCalendar();
		d	= pad(cal.get(java.util.Calendar.DAY_OF_MONTH) + "", 2, "0");
		m	= pad(cal.get(java.util.Calendar.MONTH) + "", 2, "0");
		y	= pad(cal.get(java.util.Calendar.YEAR) + "", 2, "0");
		y2	= pad(cal.get(java.util.Calendar.YEAR) + "", 4, "0");
		h	= pad(cal.get(java.util.Calendar.HOUR_OF_DAY) + "", 2, "0");
		mi	= pad(cal.get(java.util.Calendar.MINUTE) + "", 2, "0");
		s	= pad(cal.get(java.util.Calendar.SECOND) + "", 2, "0");
		ms	= pad(cal.get(java.util.Calendar.MILLISECOND) + "", 3, "0");
	}
	
	/***************************************************************************
	 * Returns the current date with format <code>d/m/y</code>.
	 * 
	 * @return A string for the current date with format <code>d/m/y</code>
	 **************************************************************************/
	public static String currentDate() {
		read(); return m + "/" + d + "/" + y;
	}
	
	/***************************************************************************
	 * Returns the current date with format <code>Y-m-d</code>. Useful to
	 * database queries.
	 * 
	 * @return	A string for the current date with format <code>Y-m-d</code>
	 **************************************************************************/
	public static String currentDateBD() {
		read(); return y2 + "-" + m + "-" + d;
	}

	/***************************************************************************
	 * Returns the current time with format <code>h:mi:s.ms</code>.
	 * 
	 * @return	A string for the current time with format
	 * 			<code>h:mi:s.ms</code>.
	 **************************************************************************/
	public static String currentTime() {
		read(); return h + ":" + mi + ":" + s + "." + ms;
	}

	/***************************************************************************
	 * Returns the current time with format <code>h:mi:s</code>. Useful to
	 * database queries.
	 * 
	 * @return	A string for the current time with format <code>h:mi:s</code>
	 **************************************************************************/
	public static String currentTimeBD() {
		read(); return h + ":" + mi + ":" + s;
	}

	/***************************************************************************
	 * Pads a string on the left to a certain length with another given string.
	 * 
	 * @param	s		The input string
	 * @param	length	Target length
	 * @param	padStr	Padding string
	 * @return The padded string
	 **************************************************************************/
	public static String pad(String s, int length, String padStr) {
		while (s.length() < length)
			s = padStr + s;
		return s;
	}

}
