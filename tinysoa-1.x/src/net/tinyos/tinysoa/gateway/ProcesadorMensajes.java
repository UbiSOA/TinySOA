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

import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import net.tinyos.tinysoa.common.*;
import net.tinyos.tinysoa.util.*;

/*******************************************************************************
 * Clase que implementa la funcionalidad del módulo Procesador de Mensajes del
 * componente TinySOA Gateway.
 * 
 * @author		Edgardo Avilés López
 * @version	0.3, 07/24/2006
 ******************************************************************************/
public class ProcesadorMensajes {
	
	private boolean actualizar	= true;
	private boolean convertir		= true;
	private boolean autoScroll	= true;
	private boolean listo			= false;
	private int mostrarNElementos	= 50;
	
	private Robot robot;
	private DefaultTableModel modeloTabla;
	private JTable tabla;
	private Properties tipos, propiedades;
	private JLabel estado;
	private ClienteServInter cliente;
	private int sensorBoard, nid, idRed;
	private Connection bd;
	
	private TinySOAMsg m;
	
	/***************************************************************************
	 * Constructor del cliente de servicios internos.
	 *
	 * @param propiedades	Propiedades de la red.
	 * @param tabla		Tabla para mostrar los mensajes recibidos.
	 * @param modeloTabla	Modelo de la tabla indicada.
	 * @param estado		Etiqueta para mostrar el estado.
	 **************************************************************************/
	public ProcesadorMensajes(
			Properties propiedades, JTable tabla,
			DefaultTableModel modeloTabla, JLabel estado) {
		
		this.tabla 			= tabla;
		this.modeloTabla 	= modeloTabla;
		this.propiedades	= propiedades;
		this.estado 		= estado;
		
		inicializarInterfaz();
	}
	
	/***************************************************************************
	 * Define el cliente de servicios internos a utilizar
	 *
	 * @param cliente
	 **************************************************************************/
	public void defCliente(ClienteServInter cliente) {
		this.cliente = cliente;
	}
	
	/***************************************************************************
	 * Define la conexión de base de datos a utilizar.
	 * 
	 * @param bd
	 **************************************************************************/
	public void defBD(Connection bd) {
		this.bd = bd;
	}
	
	/***************************************************************************
	 * Prepara la interfaz.
	 **************************************************************************/
	private void inicializarInterfaz() {
		int i;
		
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
		String columnas[] = new String[]{"ID", "PID", "Tipo", "NSec", "Sensor",
				"v1", "v2", "v3", "v4", "v5", "v6", "v7", "v8"};

		for (i = 0; i < columnas.length; i++)
			modeloTabla.addColumn(columnas[i]);
		
		TablaCeldaRenderer tcr01, tcr02, tcr03;
		
		tcr01	= new TablaCeldaRenderer(SwingConstants.CENTER, true);
		tcr02	= new TablaCeldaRenderer(SwingConstants.CENTER, false);
		tcr03	= new TablaCeldaRenderer(SwingConstants.RIGHT, false);
				
		tabla.getColumnModel().getColumn(0).setCellRenderer(tcr01);
		for (i = 1; i <= 4; i++)
			tabla.getColumnModel().getColumn(i).setCellRenderer(tcr02);
		for (i = 5; i <= 12; i++)
			tabla.getColumnModel().getColumn(i).setCellRenderer(tcr03);
		
		for (i = 0; i < 50; i++)
			modeloTabla.addRow(new Object[]{});
		
		tabla.getColumnModel().getColumn(0).setMinWidth(50);
		tabla.getColumnModel().getColumn(0).setMaxWidth(50);
		tabla.getColumnModel().getColumn(1).setMinWidth(50);
		tabla.getColumnModel().getColumn(1).setMaxWidth(50);
		tabla.getColumnModel().getColumn(2).setMinWidth(50);
		tabla.getColumnModel().getColumn(2).setMaxWidth(50);
		tabla.getColumnModel().getColumn(3).setMinWidth(50);
		tabla.getColumnModel().getColumn(3).setMaxWidth(50);
		tabla.getColumnModel().getColumn(4).setMinWidth(60);
		tabla.getColumnModel().getColumn(4).setMaxWidth(60);
		tabla.getColumnModel().getColumn(5).setMinWidth(55);
		tabla.getColumnModel().getColumn(5).setPreferredWidth(55);
		
		tipos = new Properties();
		for (i = 1; i <= 8; i++)
			tipos.setProperty("v" + i, Constantes.SENSOR_NULO + "");
	}
	
	/***************************************************************************
	 * Define si el procesador está listo para escribir en la base de datos.
	 * 
	 * @param listo
	 **************************************************************************/
	public void defListo(boolean listo) {
		this.listo = listo;
		if (listo) idRed = Integer.parseInt(propiedades.getProperty("red.id"));
	}
	
	/***************************************************************************
	 * Método para recibir e iniciar el procesamiento de un mensaje.
	 * 
	 * @param to		Dirección destino del mensaje
	 * @param mensaje	Mensaje recibido
	 **************************************************************************/
	public void recibir(int to, TinySOAMsg mensaje) {
		this.m = mensaje;
		
		if (actualizar) {
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {			
					int tipo = m.get_tipo();
					if (tipo == Constantes.TIPO_LECTURA) {
						if (tipos.getProperty(Convertidor.intToSens(
								m.get_sensor())) != null)
							modeloTabla.insertRow(0,
									procesarLectura(m, convertir));
						else return;
					}
					else if (tipo == Constantes.TIPO_REGISTRO)
						modeloTabla.insertRow(
								0, procesarRegistro(m, convertir));
					else System.out.println(
							"Error: Tipo no esperado: " + tipo + ".");
					
					if (modeloTabla.getRowCount() > mostrarNElementos)
						modeloTabla.removeRow(modeloTabla.getRowCount() - 1);
					
					if (autoScroll) {
						if (tabla.isFocusOwner()) {
							robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
							robot.keyPress(java.awt.event.KeyEvent.VK_HOME);
							robot.keyRelease(
									java.awt.event.KeyEvent.VK_CONTROL);
							robot.keyRelease(java.awt.event.KeyEvent.VK_HOME);
						}
					}
				}
			});
			
		} else {
			
			int tipo = m.get_tipo();
			if (tipo == Constantes.TIPO_LECTURA) {
				if (tipos.getProperty(
						Convertidor.intToSens(m.get_sensor())) != null)
					procesarLectura(m, convertir);
				else return;
			}
			else if (tipo == Constantes.TIPO_REGISTRO)
				procesarRegistro(m, convertir);
			else System.out.println("Error: Tipo no esperado: " + tipo + ".");
			
		}
		
		if (bd != null) procesarMantenimiento();
	}
	
	/***************************************************************************
	 * Procesa un mensaje de mantenimiento.
	 **************************************************************************/
	private void procesarMantenimiento() {
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = bd.createStatement();
			rs = st.executeQuery("SELECT * FROM mantenimiento WHERE listo=0");
			while (rs.next()) {
				
				if (!cliente.obtEnviarOcupado()) {
					Statement st2 = bd.createStatement();
					cliente.enviarComando(rs.getInt("nid"), rs.getInt("tipo"), rs.getInt("valor"));
					st2.execute("UPDATE mantenimiento SET listo=1 WHERE id=" + rs.getInt("id"));
				}
				
			}
				
				/*				if (rs.getString("accion").compareTo("encenderbocina") == 0) {
					if (!cliente.obtEnviarOcupado()) {
						Statement st2 = bd.createStatement();
						cliente.enviarComando(
								0, Constantes.TIPO_ACTIVA_ACTUADOR,
								Constantes.ACTUADOR_BOCINA);
						st2.executeUpdate("UPDATE mantenimiento SET listo=1 " +
								"WHERE id=" + rs.getString("id"));
					}
				}
				if (rs.getString("accion").compareTo("apagarbocina") == 0) {
					if (!cliente.obtEnviarOcupado()) {
						Statement st2 = bd.createStatement();
						cliente.enviarComando(
								0, Constantes.TIPO_DESACTIVA_ACTUADOR,
								Constantes.ACTUADOR_BOCINA);
						st2.executeUpdate("UPDATE mantenimiento SET listo=1 " +
								"WHERE id=" + rs.getString("id"));
					}
				}
			*/
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
	
	/***************************************************************************
	 * Procesa un mensaje de lectura.
	 * 
	 * @param m			Mensaje recibido
	 * @param convertir	Verdadero si los datos deben convertirse
	 * @return				Un arreglo de objetos con la información recibida
	 **************************************************************************/
	private Object[] procesarLectura(TinySOAMsg m, boolean convertir) {
		estado.setText("<html>&Uacute;ltima <b>lectura</b> recibida el <b>" +
				Calendario.fechaActual() + "</b> a las <b>" +
				Calendario.horaActual() + "</b>.</html>");
		
		sensorBoard = m.get_sensor();
		String s = Convertidor.intToSens(sensorBoard);
		
		nid = m.get_id();
		registrarLectura(
				Integer.parseInt(tipos.getProperty(s + "v1")), m.get_l1());
		registrarLectura(
				Integer.parseInt(tipos.getProperty(s + "v2")), m.get_l2());
		registrarLectura(
				Integer.parseInt(tipos.getProperty(s + "v3")), m.get_l3());
		registrarLectura(
				Integer.parseInt(tipos.getProperty(s + "v4")), m.get_l4());
		registrarLectura(
				Integer.parseInt(tipos.getProperty(s + "v5")), m.get_l5());
		registrarLectura(
				Integer.parseInt(tipos.getProperty(s + "v6")), m.get_l6());
		registrarLectura(
				Integer.parseInt(tipos.getProperty(s + "v7")), m.get_l7());
		registrarLectura(
				Integer.parseInt(tipos.getProperty(s + "v8")), m.get_l8());
		
		return new Object[]{
				Convertidor.intToId(m.get_id(), convertir),
				Convertidor.intToId(m.get_padre(), convertir),
				Convertidor.intToTipo(m.get_tipo(), convertir),
				Convertidor.intToN(m.get_nsec(), 2, convertir),
				Convertidor.intToSens(m.get_sensor(), convertir),
				new DatoSensado(
						Integer.parseInt(tipos.getProperty(s + "v1")),
						m.get_l1(), convertir),
				new DatoSensado(
						Integer.parseInt(tipos.getProperty(s + "v2")),
						m.get_l2(), convertir),
				new DatoSensado(
						Integer.parseInt(tipos.getProperty(s + "v3")),
						m.get_l3(), convertir),
				new DatoSensado(
						Integer.parseInt(tipos.getProperty(s + "v4")),
						m.get_l4(), convertir),
				new DatoSensado(
						Integer.parseInt(tipos.getProperty(s + "v5")),
						m.get_l5(), convertir),
				new DatoSensado(
						Integer.parseInt(tipos.getProperty(s + "v6")),
						m.get_l6(), convertir),
				new DatoSensado(
						Integer.parseInt(tipos.getProperty(s + "v7")),
						m.get_l7(), convertir),
				new DatoSensado(
						Integer.parseInt(tipos.getProperty(s + "v8")),
						m.get_l8(), convertir)};
	}
	
	/***************************************************************************
	 * Registra el parámetro especificado en la base de datos.
	 * 
	 * @param parametro	Parámetro a registrar
	 **************************************************************************/
	private void registrarParametro(int parametro) {
		if (!listo) return;
		
		int idRed = Integer.parseInt(propiedades.getProperty("red.id"));
		if (parametro != Constantes.SENSOR_NULO) {
			String par = Convertidor.sensorEtiqueta(parametro);
			
			Statement st = null;
			ResultSet rs = null;
			
			try {
				st = bd.createStatement();
				rs = st.executeQuery(
						"SELECT * FROM parametros WHERE rid='" +
						idRed + "' AND parametro='" + par +"'");
				if (rs.next()) return;
				
				st.executeUpdate(
						"INSERT INTO parametros VALUES('0', '" + idRed +
						"', '" + par + "')");
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
	}
	
	/***************************************************************************
	 * Registra la lectura indicada en la base de datos.
	 * 
	 * @param tipo		Tipo de la lectura
	 * @param valor	Valor crudo de la lectura
	 **************************************************************************/
	private void registrarLectura(int tipo, int valor) {
		if (!listo) return;
		if (tipo == Constantes.SENSOR_NULO) return;
		
		Statement st = null;
		
		String par = Convertidor.intToSensParam(tipo, true);
		
		double val = valor;
		if (tipo == Constantes.SENSOR_TEMP)	val = Convertidor.adcToTempD(valor);
		if (tipo == Constantes.SENSOR_VOLT) val = Convertidor.adcToVoltD(valor);
		
		try {
			st = bd.createStatement();
			st.executeUpdate("INSERT INTO historico VALUES('0', '" + idRed +
					"', '" + nid + "', NOW(), '" + par + "', '" + val + "')");
		} catch (SQLException ex) {
			Errores.errorBD(ex);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (Exception e) {}
			}
		}		
	}
	
	/***************************************************************************
	 * Registra el actuador especificado en la base de datos.
	 * 
	 * @param actuador	Actuador a registrar
	 **************************************************************************/
	private void registrarActuador(int actuador) {
		if (!listo) return;
		
		idRed = Integer.parseInt(propiedades.getProperty("red.id"));
		String act = Convertidor.intToActuador(actuador);
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = bd.createStatement();
			rs = st.executeQuery(
					"SELECT * FROM actuadores WHERE rid='" + idRed +
					"' AND actuador='" + act + "'");
			if (rs.next()) return;
			
			st.executeUpdate(
					"INSERT INTO actuadores VALUES('0', '" + idRed +
					"', '" + act + "')");
			
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
	
	/***************************************************************************
	 * Registra los actuadores en base al <i>sensor board</i> especificado.
	 * 
	 * @param sensorBoard	<i>Sensor board</i> a registrar
	 **************************************************************************/
	private void registrarActuadores(int sensorBoard) {
		if (!listo) return;
		
		if (sensorBoard == Constantes.MTS310) {
			registrarActuador(Constantes.ACTUADOR_BOCINA);
			registrarActuador(Constantes.ACTUADOR_LED_AMARILLO);
			registrarActuador(Constantes.ACTUADOR_LED_ROJO);
			registrarActuador(Constantes.ACTUADOR_LED_VERDE);
		}
	}
	
	/***************************************************************************
	 * Procesa un mensaje de registro.
	 * 
	 * @param m			Mensaje recibido
	 * @param convertir	Verdadero si los valores deben convertirse
	 * @return				Un arreglo de objetos con la información recibida
	 **************************************************************************/
	private Object[] procesarRegistro(TinySOAMsg m, boolean convertir) {
		estado.setText("<html>&Uacute;ltimo <b>registro</b> recibido el <b>" +
				Calendario.fechaActual() + "</b> a las <b>" +
				Calendario.horaActual() + "</b>.</html>");
		
		idRed = Integer.parseInt(propiedades.getProperty("red.id"));
		
		sensorBoard = m.get_sensor();
		String s = Convertidor.intToSens(sensorBoard);
		tipos.setProperty(s, "registrado");
		tipos.setProperty(s + "v1", m.get_l1() + "");
		tipos.setProperty(s + "v2", m.get_l2() + "");
		tipos.setProperty(s + "v3", m.get_l3() + "");
		tipos.setProperty(s + "v4", m.get_l4() + "");
		tipos.setProperty(s + "v5", m.get_l5() + "");
		tipos.setProperty(s + "v6", m.get_l6() + "");
		tipos.setProperty(s + "v7", m.get_l7() + "");
		tipos.setProperty(s + "v8", m.get_l8() + "");
		
		registrarParametro(m.get_l1());
		registrarParametro(m.get_l2());
		registrarParametro(m.get_l3());
		registrarParametro(m.get_l4());
		registrarParametro(m.get_l5());
		registrarParametro(m.get_l6());
		registrarParametro(m.get_l7());
		registrarParametro(m.get_l8());
		
		registrarActuadores(sensorBoard);
		
		tabla.getColumnModel().getColumn(5).setHeaderValue(
				Convertidor.sensorEtiqueta(m.get_l1(), 1));
		tabla.getColumnModel().getColumn(6).setHeaderValue(
				Convertidor.sensorEtiqueta(m.get_l2(), 2));
		tabla.getColumnModel().getColumn(7).setHeaderValue(
				Convertidor.sensorEtiqueta(m.get_l3(), 3));
		tabla.getColumnModel().getColumn(8).setHeaderValue(
				Convertidor.sensorEtiqueta(m.get_l4(), 4));
		tabla.getColumnModel().getColumn(9).setHeaderValue(
				Convertidor.sensorEtiqueta(m.get_l5(), 5));
		tabla.getColumnModel().getColumn(10).setHeaderValue(
				Convertidor.sensorEtiqueta(m.get_l6(), 6));
		tabla.getColumnModel().getColumn(11).setHeaderValue(
				Convertidor.sensorEtiqueta(m.get_l7(), 7));
		tabla.getColumnModel().getColumn(12).setHeaderValue(
				Convertidor.sensorEtiqueta(m.get_l8(), 8));
		tabla.getTableHeader().repaint();
		
		new Thread() {
			public void run() {
				if (!cliente.obtEnviarOcupado())
					cliente.enviarComando(
							0, Constantes.TIPO_SUSCRIBIR, sensorBoard);
			}
		}.start();
		
		return new Object[]{
				Convertidor.intToId(m.get_id(), convertir),
				Convertidor.intToId(m.get_padre(), convertir),
				Convertidor.intToTipo(m.get_tipo(), convertir),
				Convertidor.intToN(m.get_nsec(), 2, convertir),
				Convertidor.intToSens(m.get_sensor(), convertir),
				Convertidor.intToSensParam(m.get_l1(), convertir),
				Convertidor.intToSensParam(m.get_l2(), convertir),
				Convertidor.intToSensParam(m.get_l3(), convertir),
				Convertidor.intToSensParam(m.get_l4(), convertir),
				Convertidor.intToSensParam(m.get_l5(), convertir),
				Convertidor.intToSensParam(m.get_l6(), convertir),
				Convertidor.intToSensParam(m.get_l7(), convertir),
				Convertidor.intToSensParam(m.get_l8(), convertir)
		};
	}
	
	/*		
	
	private void limpiarInterfaz() {
		for (int i = 0, n = modeloTabla.getRowCount(); i < n; i++)
			modeloTabla.removeRow(modeloTabla.getRowCount() - 1);
	}
	
	public void setActualizar(boolean actualizar) {
		this.actualizar = actualizar;
	}
	
	public void setConvertir(boolean convertir) {
		this.convertir = convertir;
	}
	
	public void setAutoScroll(boolean autoScroll) {
		this.autoScroll = autoScroll;
	}
	
	public void setNElementos(int n) {
		this.mostrarNElementos = n;
		int nAnterior = modeloTabla.getRowCount();
		if (nAnterior > n)
			for (int i = 0; i < nAnterior - n; i++)
				modeloTabla.removeRow(modeloTabla.getRowCount() - 1);
	}*/
	
}
