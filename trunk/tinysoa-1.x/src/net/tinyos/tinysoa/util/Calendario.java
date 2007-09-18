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

/*******************************************************************************
 * Clase utilería relacionadas con el calendario.
 * 
 * @author		Edgardo Avilés López
 * @version	0.2, 07/24/2006
 ******************************************************************************/
public final class Calendario {
	
	private static Calendar cal;
	private static String d, m, y, y2, h, mi, s, ms;
	
	/***************************************************************************
	 * Actualiza la información al tiempo actual.
	 **************************************************************************/
	public static void leer() {
		cal = new GregorianCalendar();
		d	= pad(cal.get(Calendar.DAY_OF_MONTH) + "", 2, "0");
		m	= pad(cal.get(Calendar.MONTH) + "", 2, "0");
		y	= pad(cal.get(Calendar.YEAR) + "", 2, "0");
		y2	= pad(cal.get(Calendar.YEAR) + "", 4, "0");
		h	= pad(cal.get(Calendar.HOUR_OF_DAY) + "", 2, "0");
		mi	= pad(cal.get(Calendar.MINUTE) + "", 2, "0");
		s	= pad(cal.get(Calendar.SECOND) + "", 2, "0");
		ms	= pad(cal.get(Calendar.MILLISECOND) + "", 3, "0");
	}
	
	/***************************************************************************
	 * Devuelve la fecha actual en el formato d/m/y.
	 * 
	 * @return	Una cadena con la fecha en formato d/m/y.
	 **************************************************************************/
	public static String fechaActual() {
		leer(); return d + "/" + m + "/" + y;
	}
	
	/***************************************************************************
	 * Devuelve la fecha actual en el formato Y-m-d.
	 * 
	 * @return	Una cadena con la fecha en formato Y-m-d;
	 **************************************************************************/
	public static String fechaActualBD() {
		leer(); return y2 + "-" + m + "-" + d;
	}

	/***************************************************************************
	 * Devuelve la hora actual en el formato h:mi:s.ms.
	 * 
	 * @return	Una cadena con la hora en formato h:mi:s.ms.
	 **************************************************************************/
	public static String horaActual() {
		leer(); return h + ":" + mi + ":" + s + "." + ms;
	}
	
	/***************************************************************************
	 * Devuelve la hora actual en el formato h:mi:s
	 * 
	 * @return	Una cadena con la hora en formato h:mi:s
	 **************************************************************************/
	public static String horaActualBD() {
		leer(); return h + ":" + mi + ":" + s;
	}
	
	/***************************************************************************
	 * Rellena una cadena con la longitud y cadena relleno dados.
	 * 
	 * @param	s			Cadena a ajustar.
	 * @param	longitud	Longitud deseada.
	 * @param	padCad		Cadena relleno.
	 * @return La cadena ajustada según las opciones.
	 **************************************************************************/
	public static String pad(String s, int longitud, String padCad) {
		while (s.length() < longitud)
			s = padCad + s;
		return s;
	}
	
}
