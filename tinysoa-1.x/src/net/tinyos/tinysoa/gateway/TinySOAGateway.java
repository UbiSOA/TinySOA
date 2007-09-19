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

package net.tinyos.tinysoa.gateway;

import net.tinyos.message.*;
import net.tinyos.tinysoa.common.*;
import net.tinyos.tinysoa.util.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.sql.*;

/*******************************************************************************
 * Clase que implementa la funcionalidad del componente TinySOA Gateway.
 * 
 * @author		Edgardo Avilés López
 * @version	0.2, 07/24/2006
 ******************************************************************************/
public class TinySOAGateway {
	
	private static String TITULO_VENTANA = "TinySOA Gateway v0.2";
	private static String ARCHIVO_CONFIGURACION = "configuracion.xml";

	/** JFrame de la ventana principal. */
	public static JFrame ventana;
	
	/** Conector con SerialForwarder. */
	public static MoteIF mote;
	
	/** Configuración de la aplicación */
	public static Properties configuracion;
	
	/** Cliente de servicios internos. */
	public static ClienteServInter cliente;
	
	/** Procesador de mensajes. */
	public static ProcesadorMensajes procesador;
	
	/** Conector con MySQL */
	public static Connection bd;
	
	private static BorderLayout bl01;
	private static Border bo01;
	private static JLabel l01;
	private static JTable t01;
	private static DefaultTableModel tm01;
	private static JPanel p01, p02;
	private static Border bo02, bo03;
	private static JScrollPane sp01;
	private static BorderLayout bl02, bl03;
	private static JTabbedPane tp01;
	private static Font f01, f02;
	static ImageIcon i01, i02, i03;

	/***************************************************************************
	 * Realiza la conexión con SerialForwarder.
	 **************************************************************************/
	public static void conectar() {		
		String servidor, usuario, password, bDatos;
		servidor = usuario = password = bDatos = "";
		
		try {
			servidor	= configuracion.getProperty("mysql.servidor");
			usuario		= configuracion.getProperty("mysql.usuario");
			password	= configuracion.getProperty("mysql.password");
			bDatos		= configuracion.getProperty("mysql.bDatos");

			if (servidor == null) {
				
				servidor	= "localhost";
				usuario		= "root";
				password	= "";
				bDatos		= "tinysoabd";
				
				configuracion.setProperty("mysql.servidor", servidor);
				configuracion.setProperty("mysql.usuario", usuario);
				configuracion.setProperty("mysql.password", password);
				configuracion.setProperty("mysql.bDatos", bDatos);
				configuracion.storeToXML(
						new FileOutputStream(ARCHIVO_CONFIGURACION), null);
			}
		} catch (Exception ex) {}
		
		imprimirEstado(	"<html><strong>Conectando</strong> a " +
						"SerialForwarder...</html>");

		mote = new MoteIF();
		procesador = new ProcesadorMensajes(configuracion, t01, tm01, l01);
		cliente = new ClienteServInter(mote, procesador);
		mote.registerListener(new TinySOAMsg(), cliente);
		procesador.defCliente(cliente);
		
		imprimirEstado(	"<html><strong>Conectado exitosamente</strong> a " +
						"SerialForwarder.</html>");
		
		imprimirEstado(	"<html><strong>Conectando</strong> a " +
		"la base de datos...</html>");

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			bd = DriverManager.getConnection(
					"jdbc:mysql://" + servidor + "/" + bDatos + "?" +
					"user=" + usuario + "&password=" + password);
		} catch (Exception ex) {
			//ex.printStackTrace();
			System.err.println("Imposible conectar a la base de datos.");
			System.err.println(
					"Verifique que la información en el archivo " +
					"configuracion.xml es correcta y que la base de datos " +
					"responde adecuadamente.");
			System.exit(1);
		}
		
		procesador.defBD(bd);
		
		imprimirEstado(	"<html><strong>Conectado exitosamente</strong> a " +
		"la base de datos...</html>");
		
		if (configuracion.getProperty("red.id") == null) {
			procesador.defListo(false);
			DialogoPropiedades dp = new DialogoPropiedades(
					procesador, ventana, configuracion,
					ARCHIVO_CONFIGURACION, bd);
			dp.centrarDialogo();
			dp.setVisible(true);
		} else {
			Statement st = null;
			ResultSet rs = null;
			try {
				st = bd.createStatement();
				rs = st.executeQuery(
						"SELECT * FROM redes WHERE id='" + 
						configuracion.getProperty("red.id") + "'");
				if (rs.next()) procesador.defListo(true);
				else {
					procesador.defListo(false);
					DialogoPropiedades dp = new DialogoPropiedades(
							procesador, ventana, configuracion,
							ARCHIVO_CONFIGURACION, bd);
					dp.centrarDialogo();
					dp.setVisible(true);
				}
			} catch (SQLException ex) {
				Errores.errorBD(ex);
			} finally {
				if ((rs != null) && (st != null)) {
					try {
						rs.close();
						st.close();
					} catch (Exception e) {}
				}
			}
		}
		
		imprimirEstado("<html>Esperando <strong>registros</strong>...</html>");
		cliente.enviarComando(
				0, Constants.TIPO_SOLICITUD_REGISTRO, 0);
		
	}
	
	/***************************************************************************
	 * Crea la intefaz de usuario.
	 **************************************************************************/
	public static void crearVentana() {
		try {
			UIManager.setLookAndFeel(	"com.sun.java.swing.plaf.windows." +
										"WindowsLookAndFeel");
		} catch (Exception e) {}
		
		ventana = new JFrame(TITULO_VENTANA);
		ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Inicia construcción de interfaz de usuario --------------------------

		bl01 = new BorderLayout();
		p01	= new JPanel();

		ventana.getContentPane().setLayout(bl01);
		ventana.getContentPane().add(p01, BorderLayout.CENTER);
		
		bo01 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
		bl02 = new BorderLayout();
		p02 = new JPanel();
		
		p01.setBorder(bo01);
		bl02.setHgap(8);
		p01.setLayout(bl02);
		p01.add(p02, BorderLayout.CENTER);
		
		bl03 = new BorderLayout();		
		tp01 = new JTabbedPane(JTabbedPane.LEFT);
		l01 = new JLabel();
		
		bl03.setVgap(8);
		p02.setLayout(bl03);
		f02 = new Font("Arial", Font.BOLD, 12);
		tp01.setFont(f02);
		p02.add(tp01, BorderLayout.CENTER);
		bo02 = BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(150,150,150), 1),
				BorderFactory.createEmptyBorder(6,6,6,6));
		l01.setBorder(bo02);
		f01 = new Font("Arial", Font.PLAIN, 12);
		l01.setFont(f01);
		l01.setBackground(Color.WHITE);
		l01.setOpaque(true);
		p02.add(l01, BorderLayout.SOUTH);
		
		tm01 = new DefaultTableModel();
		t01 = new Tabla(tm01);
		sp01 = new JScrollPane(t01, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);		
		
		bo03 = BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(8,8,8,8),
				BorderFactory.createLineBorder(new Color(0x7a8a99), 2));
		sp01.setBorder(bo03);
		sp01.setOpaque(false);
		tp01.add(sp01, i01);
		tp01.add(new JPanel(), i02);

		// Finaliza construcción de interfaz de usuario ------------------------
		
		ventana.setSize(new Dimension(800, 700));
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		ventana.setLocation((dim.width - ventana.getSize().width) / 2,
				(dim.height - ventana.getSize().height) / 2);
		
		ventana.setVisible(true);
				
		conectar();
	}

	/***************************************************************************
	 * Imprime el estado en la interfaz y en la consola.
	 **************************************************************************/
	private static void imprimirEstado(String s) {
		imprimirEstado(s, false);
	}
	
	/***************************************************************************
	 * Imprime el estado en la interfaz y en la consola opcionalmente.
	 **************************************************************************/
	private static void imprimirEstado(String s, boolean soloGUI) {
		l01.setText(s);
		if (!soloGUI) System.out.println(s.replaceAll("\\<.*?\\>",""));
	}
	/***************************************************************************
	 * Clase principal.
	 * 
	 * @param	args	Argumentos de entrada.
	 **************************************************************************/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				i01 = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/tab.capturas.gif"));
				i02 = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/tab.mensajes.gif"));
				i03 = new ImageIcon(getClass().getResource(
						"/net/tinyos/tinysoa/img/ico.propiedades.gif"));
				
				configuracion	= new Properties();
		
				try {
					configuracion.loadFromXML(
							new FileInputStream(ARCHIVO_CONFIGURACION));
				} catch (IOException e) {}
				
				crearVentana();
			}
		});
	}
}