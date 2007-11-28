/*
 *  Copyright 2007 Edgardo Avilés López
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

package net.tinyos.tinysoa.server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

import javax.swing.JOptionPane;

import net.tinyos.tinysoa.util.DatabaseDialog;
import net.tinyos.tinysoa.util.Errors;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.codehaus.xfire.*;
import org.codehaus.xfire.annotations.*;
import org.codehaus.xfire.annotations.jsr181.*;
import org.codehaus.xfire.server.http.*;
import org.codehaus.xfire.service.*;
import org.codehaus.xfire.service.invoker.*;
import org.mortbay.util.MultiException;

/*******************************************************************************
 * Clase que implementa la funcionalidad de TinySOA Servidor.
 * 
 * @author		Edgardo Avilés López
 * @version	1.1, 11/28/2007
 ******************************************************************************/

public class TinySOAServer {
	private static String TITULO_VENTANA = "TinySOA Servidor v0.1";
	private static String ARCHIVO_CONFIGURACION = "config.xml";
	private static String serv, user, passwd, dBase;
	private static String puerto;
	private static Connection bd;

	private static XFireHttpServer servidor;
	
	private static Logger logger = Logger.getLogger(TinySOAServer.class);
	
	/***************************************************************************
	 * Carga las opciones del archivo de configuración.
	 **************************************************************************/
	private static void cargarOpciones() {
		serv = user = passwd = dBase = "";
		
		try {
			Properties configuracion = new Properties();
			configuracion.loadFromXML(
					new FileInputStream(ARCHIVO_CONFIGURACION));
			serv	= configuracion.getProperty("mysql.server");
			user	= configuracion.getProperty("mysql.user");
			passwd	= configuracion.getProperty("mysql.password");
			dBase	= configuracion.getProperty("mysql.database");
			puerto	= configuracion.getProperty("server.port");
			
			if (serv == null)		serv = "localhost";
			if (user == null)	user = "root";
			if (passwd == null)		passwd = "";
			if (dBase == null)		dBase = "tinysoadb";
			if (puerto == null)		puerto = "8080";
			
			configuracion.setProperty("mysql.server", serv);
			configuracion.setProperty("mysql.user", user);
			configuracion.setProperty("mysql.password", passwd);
			configuracion.setProperty("mysql.database", dBase);
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
					dBase + "?user=" + user + "&password=" + passwd);
		} catch (Exception ex) {
			DatabaseDialog d;
			d = new DatabaseDialog(logger, serv, user, passwd, dBase);
			d.setVisible(true);
			
			try {
				Properties configuracion = new Properties();
				configuracion.loadFromXML(
						new FileInputStream(ARCHIVO_CONFIGURACION));
			
				serv	= d.server.getText();
				user	= d.username.getText();
				passwd	= new String(d.password.getPassword());
				dBase	= d.database.getSelectedItem().toString();
			
				configuracion.setProperty("mysql.server", serv);
				configuracion.setProperty("mysql.user", user);
				configuracion.setProperty("mysql.password", passwd);
				configuracion.setProperty("mysql.database", dBase);
				configuracion.storeToXML(
						new FileOutputStream(ARCHIVO_CONFIGURACION), null);
			} catch (IOException e) { logger.error(e); System.exit(1); }
		}
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			bd = DriverManager.getConnection("jdbc:mysql://" + serv + "/" +
					dBase + "?user=" + user + "&password=" + passwd);
		} catch (Exception e) { logger.error(e); System.exit(1); }
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
						NetServImpl.class, "NetServ" + rid,
						"http://numenor.cicese.mx/TinySOA", null);
				servicioRed.setInvoker(
						new BeanInvoker(new NetServImpl(bd, rid)));
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
			if (((MultiException)e).getException(0) instanceof BindException)
				JOptionPane.showMessageDialog(null, "<html>" +
						"The port <i>" + puerto + "</i> is already in use by another service. Please,<br>" +
						"change the port for TinySOA Server in the " + ARCHIVO_CONFIGURACION + " file.</html>",
						"Server Initialization Error", JOptionPane.ERROR_MESSAGE);
			logger.error(e);
			System.exit(1);
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
		PropertyConfigurator.configure("log4j.properties");

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
