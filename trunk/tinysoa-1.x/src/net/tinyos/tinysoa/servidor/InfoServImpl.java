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

package net.tinyos.tinysoa.servidor;

import java.sql.*;
import java.util.*;
import java.net.*;
import javax.jws.*;

import net.tinyos.tinysoa.common.*;
import net.tinyos.tinysoa.util.*;

/*******************************************************************************
 * Clase que implementa la funcionalidad del servicio de información.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/24/2006
 ******************************************************************************/
@WebService(name="InfoServ", targetNamespace="http://numenor.cicese.mx/TinySOA")
public class InfoServImpl implements InfoServ {
	
	private Connection bd;
	private String puerto;
	
	/***************************************************************************
	 * Constructor de la clase.
	 * 
	 * @param bd		Conexión de la base de datos a utilizar
	 * @param puerto	Puerto en el cual se ofrecen los servicios
	 **************************************************************************/
	public InfoServImpl(Connection bd, String puerto) {
		this.bd = bd;
		this.puerto = puerto;
	}
	
	/***************************************************************************
	 * Devuelve un listado de los servicios de red ofrecidos por el servidor.
	 * 
	 * @return	Un vector con la información de los servicios ofrecidos
	 * @see		Red
	 **************************************************************************/
	@WebMethod(operationName="obtenerListadoRedes", action="urn:obtenerListadoRedes")
	@WebResult(name="listadoRedesResultado")
	public Vector<Red> obtenerListadoRedes() {
		Vector<Red> redes = new Vector<Red>();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = bd.createStatement();
			rs = st.executeQuery("SELECT * FROM redes ORDER BY nombre");
			
			while (rs.next()) {
				Red r = new Red();
				r.setId(rs.getInt("id"));
				r.setNombre(rs.getString("nombre"));
				r.setDescripcion(rs.getString("descripcion"));
				r.setWsdl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + puerto + "/RedServ" + r.getId() + "?wsdl");
				redes.add(r);
			}
			
		} catch (SQLException ex) {
			Errores.errorBD(ex);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}
		
		return redes;
	}
	
}
