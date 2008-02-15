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

import java.sql.*;
import java.util.logging.*;

/*******************************************************************************
 * Class providing support for the registration and control of errors.
 * 
 * @author		Edgardo Avilés López
 * @version	0.2, 07/24/2006
 ******************************************************************************/
public final class Errors {

	/***************************************************************************
	 * Close the program, show and records SQL error caused the problem
	 * 
	 * @param ex	Excepción capturada
	 **************************************************************************/
	public static void errorBD(SQLException ex) {
		try {
			FileHandler fh = new FileHandler("error.log", true);
			fh.setFormatter(new SimpleFormatter());
			Logger logger = Logger.getLogger("error");
			logger.addHandler(fh);
			
			logger.severe("Database error: " + ex.getMessage());
		} catch (Exception e) { e.printStackTrace(); }
		
		System.err.println("Database error: " + ex.getMessage());
		ex.printStackTrace();
		System.exit(1);
	}
	
	/***************************************************************************
	 * Close the program and show the error cause as well as 
	 * the description indicated.
	 * 
	 * @param ex			Exception
	 * @param description	Description of Error
	 **************************************************************************/
	public static void error(Exception ex, String description) {
		String s = "";
		for (int i = 0; i < 80; i++) s += "-";
		System.err.println(s);
		System.err.println(description);
		System.err.println(s);
		ex.printStackTrace();
		System.exit(1);
	}
	
}
