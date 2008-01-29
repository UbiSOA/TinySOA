/*
 * "Copyright (c) 2005-2006 The Regents of the Centro de Investigación Científica y de
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

/*******************************************************************************
 *Class utility related to the calendar.
 * 
 * @author		Edgardo Avilés López
 * @version	0.2, 07/24/2006
 ******************************************************************************/
public final class Calendar {
	
	private static java.util.Calendar cal;
	private static String d, m, y, y2, h, mi, s, ms;
	
	/***************************************************************************
	 * Update information Currently.
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
	 * Returns the current date in format d/m/y.
	 * 
	 * @return Returns a string with the date in format d/m/y.
	 **************************************************************************/
	public static String currentDate() {
		read(); return m + "/" + d + "/" + y;
	}
	
	/***************************************************************************
	 * Returns the current date in format Y-m-d.
	 * 
	 * @return	Returns a string with the date in format Y-m-d;
	 **************************************************************************/
	public static String currentDateBD() {
		read(); return y2 + "-" + m + "-" + d;
	}

	/***************************************************************************
	 * Returns the current time in format h:mi:s.ms.
	 * 
	 * @return	Returns a string with the time in format h:mi:s.ms.
	 **************************************************************************/
	public static String currentTime() {
		read(); return h + ":" + mi + ":" + s + "." + ms;
	}
	
	/***************************************************************************
	 * Returns the current time in format h:mi:s
	 * 
	 * @return	Returns a string with the time in format h:mi:s
	 **************************************************************************/
	public static String currentTimeBD() {
		read(); return h + ":" + mi + ":" + s;
	}
	
	/***************************************************************************
	 * Fills a string with the length and string filled data.
	 * 
	 * @param	s			Desired string.
	 * @param	length	Desired length.
	 * @param	padCad		String filled.
	 * @return The string adjusted according the options.
	 **************************************************************************/
	public static String pad(String s, int length, String padCad) {
		while (s.length() < length)
			s = padCad + s;
		return s;
	}
	
}
