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

import java.sql.*;
import java.util.logging.*;

/*******************************************************************************
 * Clase que proporciona soporte para el registro y control de errores.
 * 
 * @author		Edgardo Avilés López
 * @version	0.2, 07/24/2006
 ******************************************************************************/
public final class Errores {

	/***************************************************************************
	 * Cierra el programa, presenta y registra el error de SQL causante del
	 * problema.
	 * 
	 * @param ex	Excepción capturada
	 **************************************************************************/
	public static void errorBD(SQLException ex) {
		try {
			FileHandler fh = new FileHandler("error.log", true);
			fh.setFormatter(new SimpleFormatter());
			Logger logger = Logger.getLogger("error");
			logger.addHandler(fh);
			
			logger.severe("Error en la base de datos: " + ex.getMessage());
		} catch (Exception e) { e.printStackTrace(); }
		
		System.err.println("Error en la base de datos: " + ex.getMessage());
		System.exit(1);
	}
	
	/***************************************************************************
	 * Cierra el programa y presenta el error causante así como también la
	 * descripción indicada.
	 * 
	 * @param ex			Excepción capturada
	 * @param descripcion	Descripción del error
	 **************************************************************************/
	public static void error(Exception ex, String descripcion) {
		String s = "";
		for (int i = 0; i < 80; i++) s += "-";
		System.err.println(s);
		System.err.println(descripcion);
		System.err.println(s);
		ex.printStackTrace();
		System.exit(1);
	}
	
}
