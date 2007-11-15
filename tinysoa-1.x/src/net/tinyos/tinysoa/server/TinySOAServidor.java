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

package net.tinyos.tinysoa.server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

import net.tinyos.tinysoa.util.Errors;

import org.codehaus.xfire.*;
import org.codehaus.xfire.annotations.*;
import org.codehaus.xfire.annotations.jsr181.*;
import org.codehaus.xfire.server.http.*;
import org.codehaus.xfire.service.*;
import org.codehaus.xfire.service.invoker.*;

/*******************************************************************************
 * Clase que implementa la funcionalidad de TinySOA Servidor.
 * 
 * @author		Edgardo Avilés López
 * @version	0.2, 07/24/2006
 ******************************************************************************/

public class TinySOAServidor {
	private static String TITULO_VENTANA = "TinySOA Servidor v0.1";
	private static String ARCHIVO_CONFIGURACION = "config.xml";
	private static String serv, usuario, passwd, bDatos;
	private static String puerto;
	private static Connection bd;

	private static XFireHttpServer servidor;
	
	/***************************************************************************
	 * Carga las opciones del archivo de configuración.
	 **************************************************************************/
	private static void cargarOpciones() {
		serv = usuario = passwd = bDatos = "";
		
		try {
			Properties configuracion = new Properties();
			configuracion.loadFromXML(
					new FileInputStream(ARCHIVO_CONFIGURACION));
			serv	= configuracion.getProperty("mysql.server");
			usuario	= configuracion.getProperty("mysql.user");
			passwd	= configuracion.getProperty("mysql.password");
			bDatos	= configuracion.getProperty("mysql.database");
			puerto	= configuracion.getProperty("server.port");
			
			if (serv == null)		serv = "localhost";
			if (usuario == null)	usuario = "root";
			if (passwd == null)		passwd = "";
			if (bDatos == null)		bDatos = "tinysoadb";
			if (puerto == null)		puerto = "8080";
			
			configuracion.setProperty("mysql.server", serv);
			configuracion.setProperty("mysql.user", usuario);
			configuracion.setProperty("mysql.password", passwd);
			configuracion.setProperty("mysql.database", bDatos);
			configuracion.setProperty("server.port", puerto);
			configuracion.storeToXML(
					new FileOutputStream(ARCHIVO_CONFIGURACION), null);
		} catch (IOException e) {}
	}
	
	/***************************************************************************
	 * Realiza la conexión con la base de datos.
	 **************************************************************************/
	private static void conectarBD() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			bd = DriverManager.getConnection("jdbc:mysql://" + serv + "/" +
					bDatos + "?user=" + usuario + "&password=" + passwd);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Imposible conectar a la base de datos.");
			System.err.println(
					"Verifique que la información en el archivo " +
					"config.xml es correcta y que la base de datos " +
					"responde adecuadamente.");
			System.exit(1);
		}
	}
	
	/***************************************************************************
	 * Inicia el servidor y registra los servicios de información y de red.
	 **************************************************************************/
	public static void start() {

		XFire xfire = XFireFactory.newInstance().getXFire();
		AnnotationServiceFactory fabrica = new AnnotationServiceFactory(
				new Jsr181WebAnnotations(), xfire.getTransportManager());
		
		Service servicio = fabrica.create(InfoServImpl.class, "InfoServ",
				"http://numenor.cicese.mx/TinySOA", null);
		servicio.setInvoker(new BeanInvoker(new InfoServImpl(bd, puerto)));
		xfire.getServiceRegistry().register(servicio);
		
		Statement st = null;
		ResultSet rs = null;

		try {
			st = bd.createStatement();
			rs = st.executeQuery("SELECT * FROM networks ORDER BY id");
			while (rs.next()) {
				int rid = rs.getInt("id");
				Service servicioRed = fabrica.create(
						RedServImpl.class, "RedServ" + rid,
						"http://numenor.cicese.mx/TinySOA", null);
				servicioRed.setInvoker(
						new BeanInvoker(new RedServImpl(bd, rid)));
				xfire.getServiceRegistry().register(servicioRed);
			}
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}
		
		try {
			servidor = new XFireHttpServer();
			servidor.setPort(Integer.parseInt(puerto));
			servidor.start();
		} catch (Exception e) {
			Errors.error(e, "Error al iniciar el servidor.");
		}
	}
	
	/***************************************************************************
	 * Detiene el servidor de servicios.
	 **************************************************************************/
	public static void stop() {
		try {
			servidor.stop();
		} catch (Exception e) {
			Errors.error(e, "Error al detener el servidor.");
		}
	}
	
	/***************************************************************************
	 * Método principal de la aplicación TinySOA Servidor.
	 * 
	 * @param args	Argumentos de entrada
	 **************************************************************************/
	public static void main(String[] args) {
		/*		
		String datos = "<?xml version=\"1.0\"?>" +
				"<datos>" +
				"	<nodo id=\"0\">" +
				"		<valor parametro=\"Temp\">28.8</valor>" +
				"		<valor parametro=\"Luz\">526</valor>" +
				"	</nodo>" +
				"</datos>";
		
		
		try {
			
			StringReader reader = new StringReader(datos);
			InputSource source = new InputSource(reader);
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			Document doc = builder.parse(source);
			
			Element root = doc.getDocumentElement();
			
			System.out.println(root.getTagName());
			
			NodeList listaNodos = root.getElementsByTagName("nodo");
			System.out.println("Hay " + listaNodos.getLength() + " nodos");
			
			for (int i = 0; i < listaNodos.getLength(); i++) {
				Element elemento = (Element)listaNodos.item(i);
				String nid = elemento.getAttribute("id");
				
				System.out.println("Nodo " + nid);
				
				NodeList listaValores = elemento.getElementsByTagName("valor");
				System.out.println("Hay " + listaValores.getLength() + " valores");
				
				for (int j = 0; j < listaValores.getLength(); j++) {
					Element valor = (Element)listaValores.item(j);
					System.out.println("  valor de " + valor.getAttribute("parametro") + " = " + valor.getFirstChild().getNodeValue());
				}
			}
			
			
		} catch (Exception e){e.printStackTrace();}
		
		*/

		try {
			System.out.println("Iniciando " + TITULO_VENTANA + "...");
			cargarOpciones();
			conectarBD();
			System.out.println("Preparando " + InetAddress.getLocalHost() +
					":" + puerto + "...");
			start();
			System.out.println("Listo y esperando solicitudes...");
		} catch(Exception ex) {
			Errors.error(ex, "Error al iniciar el sistema.");
		}
	}	
	
}
