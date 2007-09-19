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

import javax.jws.*;

import net.tinyos.tinysoa.common.*;
import net.tinyos.tinysoa.util.*;

/*******************************************************************************
 * Clase que implementa la funcionalidad del servicio de red.
 * 
 * @author		Edgardo Avilés López
 * @version	0.5, 07/28/2006
 ******************************************************************************/
@WebService(name="RedServ", targetNamespace="http://numenor.cicese.mx/TinySOA")
public class RedServImpl implements RedServ
{
	private Connection bd;
	private int rid;
	
	/***************************************************************************
	 * Constructor de la clase.
	 * 
	 * @param bd	Conexión de la base de datos a utilizar
	 * @param rid	ID de la red del servicio
	 **************************************************************************/
	public RedServImpl(Connection bd, int rid) {
		this.bd = bd;
		this.rid = rid;
	}
	
	/***************************************************************************
	 * Regresa el ID de la red.
	 * 
	 * @return	ID de la red
	 **************************************************************************/
	@WebMethod(operationName="obtenerIdRed", action="urn:obtenerIdRed")
	@WebResult(name="idRedResultado")
	public int obtenerIdRed() {
		return rid;
	}

	/***************************************************************************
	 * Regresa el nombre de la red.
	 * 
	 * @return	Nombre de la red.
	 **************************************************************************/
	@WebMethod(operationName="obtenerNombreRed", action="urn:obtenerNombreRed")
	@WebResult(name="nombreRedResultado")
	public String obtenerNombreRed() {
		String nombre = "";
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = bd.createStatement();
			rs = st.executeQuery("SELECT nombre FROM redes WHERE id=" + rid);
			if (rs.next()) nombre = rs.getString("nombre");
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
		return nombre;
	}
	
	/***************************************************************************
	 * Regresa la descripción de la red.
	 * 
	 * @return	Descripción de la red.
	 **************************************************************************/
	@WebMethod(operationName="obtenerDescripcionRed",
			action="urn:obtenerDescripcionRed")
	@WebResult(name="descripcionRedResultado")
	public String obtenerDescripcionRed() {
		String descripcion = "";
		Statement st = null;
		ResultSet rs = null;
		try {
			st = bd.createStatement();
			rs = st.executeQuery(
					"SELECT descripcion FROM redes WHERE id=" + rid);
			if (rs.next()) descripcion = rs.getString("descripcion");
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
		return descripcion;
	}
	
	/***************************************************************************
	 * Regresa la fecha y hora desde la cual se tiene información.
	 * 
	 * @return Tiempo mínimo de información disponible
	 **************************************************************************/
	@WebMethod(operationName="obtenerTiempoMinimo",
			action="urn:obtenerTiempoMinimo")
	@WebResult(name="tiempoMinimoResultado")
	public String obtenerTiempoMinimo() {
		String tiempo = "";
		Statement st = null;
		ResultSet rs = null;
		try {
			st = bd.createStatement();
			rs = st.executeQuery("SELECT MIN(tiempo) AS tiempo FROM " +
					"historico WHERE rid=" + rid);
			if (rs.next()) tiempo = rs.getString("tiempo");
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
		return tiempo;
	}

	/***************************************************************************
	 * Regresa la fecha y hora hasta la cual se tiene información.
	 * 
	 * @return	Tiempo máximo de información disponible
	 **************************************************************************/
	@WebMethod(operationName="obtenerTiempoMaximo",
			action="urn:obtenerTiempoMaximo")
	@WebResult(name="tiempoMaximoResultado")
	public String obtenerTiempoMaximo() {
		String tiempo = "";
		Statement st = null;
		ResultSet rs = null;
		try {
			st = bd.createStatement();
			rs = st.executeQuery("SELECT MAX(tiempo) AS tiempo FROM " +
					"historico WHERE rid=" + rid);
			if (rs.next()) tiempo = rs.getString("tiempo");
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
		return tiempo;
	}
	
	/***************************************************************************
	 * Regresa una lista de los nodos disponibles en la red.
	 * 
	 * @return Una lista de nodos
	 * @see		Node
	 **************************************************************************/
	@WebMethod(operationName="obtenerListadoNodos",
			action="urn:obtenerListadoNodos")
	@WebResult(name="listadoNodosResultado")
	public Vector<Node> obtenerListadoNodos() {
		Vector<Node> nodos = new Vector<Node>();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = bd.createStatement();
			rs = st.executeQuery("SELECT nid FROM historico WHERE rid=" + rid +
					" GROUP BY nid ORDER BY nid");
			
			while (rs.next()) {
				Node n = new Node();
				n.setId(rs.getInt("nid"));
				nodos.add(n);
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
		
		return nodos;
	}
	
	/***************************************************************************
	 * Regresa una lista de los actuadores disponibles en la red.
	 * 
	 * @return	Una lista de actuadores
	 * @see		Actuator
	 **************************************************************************/
	@WebMethod(operationName="obtenerActuadores",
			action="urn:obtenerActuadores")
	@WebResult(name="actuadoresResultado")
	public Vector<Actuator> obtenerActuadores() {
		// TODO Implementar servicio "obtenerActuadores"
		return null;
	}

	/***************************************************************************
	 * Regresa una lista de los parámetros de sensado disponibles.
	 * 
	 * @return Una lista de parámetros
	 * @see		Parametro
	 **************************************************************************/
	@WebMethod(operationName="obtenerParametros",
			action="urn:obtenerParametros")
	@WebResult(name="parametrosResultado")
	public Vector<Parametro> obtenerParametros() {
		Vector<Parametro> parametros = new Vector<Parametro>();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = bd.createStatement();
			rs = st.executeQuery("SELECT t1.parametro, t2.descripcion FROM " +
					"parametros AS t1, descripciones AS t2 WHERE rid=" + rid +
					" AND t1.parametro = t2.parametro ORDER BY t1.parametro");
			
			while (rs.next()) {
				Parametro p = new Parametro();
				p.setNombre(rs.getString("parametro"));
				p.setDescripcion(rs.getString("descripcion"));
				parametros.add(p);
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
		
		return parametros;
	}
	
	/***************************************************************************
	 * Regresa un listado de las últimas lecturas por cada uno de los nodos
	 * en la red.
	 * 
	 * @param parametro	El parámetro de sensado deseado, deje vacío para todos
	 * @param limite	El número límite de lecturas a regresar
	 * @return			Un listado de lecturas
	 * @see				Reading
	 **************************************************************************/
	@WebMethod(operationName="obtenerUltimasLecturas",
			action="urn:obtenerUltimasLecturas")
	@WebResult(name="ultimasLecturasResultado")
	public Vector<Reading> obtenerUltimasLecturas(
			@WebParam(name="parametro") String parametro,
			@WebParam(name="limite") int limite) {
		Vector<Reading> lecturas = new Vector<Reading>();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			Vector<Node> nodos = obtenerListadoNodos();
			for (int i = 0; i < nodos.size(); i++) {
			
				st = bd.createStatement();
				if (parametro.compareTo("") == 0)
					rs = st.executeQuery("SELECT * FROM historico WHERE rid=" + rid +
							" AND nid='" + ((Node)nodos.get(i)).getId() + "' " +
							"ORDER BY tiempo DESC LIMIT 0," + obtenerParametros().size());
				else rs = st.executeQuery("SELECT * FROM historico WHERE rid=" + rid +
						" AND nid='" + ((Node)nodos.get(i)).getId() + "' AND parametro='" +
						parametro + "' " +
						"ORDER BY tiempo DESC LIMIT 0,1");
			
				while (rs.next()) {
					Reading p = new Reading();
					p.setNid(rs.getInt("nid"));
					p.setParameter(rs.getString("parametro"));
					p.setTime(rs.getString("tiempo"));
					p.setValue(rs.getString("valor"));
					lecturas.add(p);
				}
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
		
		
		
		
		return lecturas;	
	}
	
	/***************************************************************************
	 * Regresa un listado de las lecturas disponibles hasta el tiempo indicado,
	 * regresando todas las lecturas si <code>limite</code> es igual a
	 * <code>0</code> ó el número de lecturas indicado <code>limite</code>.
	 * 
	 * @param tiempo	El tiempo hasta el cual regresar lecturas
	 * @param limite	El número límite de lecturas a regresar
	 * @return			Un listado de lecturas
	 * @see				Reading
	 **************************************************************************/
	@WebMethod(operationName="obtenerLecturasAlTiempo",
			action="urn:obtenerLecturasAlTiempo")
	@WebResult(name="lecturasAlTiempoResultado")
	public Vector<Reading> obtenerLecturasAlTiempo(
			@WebParam(name="tiempo") String tiempo,
			@WebParam(name="limite") int limite) {
		Vector<Reading> lecturas = new Vector<Reading>();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = bd.createStatement();
			rs = st.executeQuery("SELECT * FROM historico WHERE rid=" + rid +
					" AND tiempo<='" + tiempo + "' ORDER BY tiempo DESC " +
					"LIMIT 0," + limite);
			
			while (rs.next()) {
				Reading p = new Reading();
				p.setNid(rs.getInt("nid"));
				p.setParameter(rs.getString("parametro"));
				p.setTime(rs.getString("tiempo"));
				p.setValue(rs.getString("valor"));
				lecturas.add(p);
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
		
		return lecturas;
	}
	
	/***************************************************************************
	 * Regresa un listado de las lecturas disponibles en el rango de tiempo
	 * indicado, regresando todas las lecturas si <code>limite</code> es igual
	 * a <code>0</code> ó el número de lecturas indicado por
	 * <code>limite</code>. De manera similar se pueden regresar las lecturas
	 * para un parámetro determinado, si <code>parametro</code> es una cadena
	 * vacía ésta regresa valores para todos los parámetros.
	 * 
	 * @param desde		Tiempo desde el cual regresar lecturas
	 * @param hasta		Tiempo hasta el cual regresar lecturas
	 * @param parametro	Parámetro del cuál obtener lecturas
	 * @param limite		El número límite de lecturas a regresar
	 * @return				Un listado de lecturas
	 * @see					Reading
	 **************************************************************************/
	@WebMethod(operationName="obtenerLecturas", action="urn:obtenerLecturas")
	@WebResult(name="lecturasResultado")
	public Vector<Reading> obtenerLecturas(
			@WebParam(name="desde") String desde,
			@WebParam(name="hasta") String hasta,
			@WebParam(name="parametro") String parametro,
			@WebParam(name="limite") int limite) {
		Vector<Reading> lecturas = new Vector<Reading>();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = bd.createStatement();
			
			String query = "SELECT * FROM historico WHERE rid=" + rid;
			if (parametro.compareTo("") != 0)
				query += " AND parametro='" + parametro + "'";
			query += " AND tiempo >= '" + desde + "' AND tiempo<='" + hasta +
					"' ORDER BY nid ASC, tiempo DESC";
			if (limite > 0) query += " LIMIT 0," + limite;
			
			rs = st.executeQuery(query);
			
			while (rs.next()) {
				Reading p = new Reading();
				p.setNid(rs.getInt("nid"));
				p.setParameter(rs.getString("parametro"));
				p.setTime(rs.getString("tiempo"));
				p.setValue(rs.getString("valor"));
				lecturas.add(p);
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
		
		return lecturas;
	}
	
	/***************************************************************************
	 * Regresa un listado de los eventos, regresando todos los eventos si
	 * <code>limite</code> es igual a <code>0</code> ó el número de eventos
	 * indicados por <code>limite</code>.
	 * 
	 * @param limite	El número límite de eventos a regresar
	 * @return			Un listado de eventos
	 * @see				Event
	 **************************************************************************/
	@WebMethod(operationName="obtenerListadoEventos",
			action="urn:obtenerListadoEventos")
	@WebResult(name="listadoEventosResultado")
	public Vector<Event> obtenerListadoEventos(
			@WebParam(name="limite") int limite) {
		Vector<Event> eventos = new Vector<Event>();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = bd.createStatement();
			
			String query = "SELECT * FROM eventos WHERE rid=" + rid +
					" ORDER BY id DESC";
			if (limite > 0) query += " LIMIT 0," + limite;
			
			rs = st.executeQuery(query);
			
			while (rs.next()) {
				Event e = new Event();
				e.setId(rs.getInt("id"));
				e.setAdded(rs.getString("agregado"));
				e.setName(rs.getString("nombre"));
				e.setCriteria(rs.getString("criterio"));
				e.setReady(rs.getBoolean("listo"));
				e.setNid(rs.getInt("nid"));
				e.setTime(rs.getString("tiempo"));
				eventos.add(e);
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
		
		return eventos;
	}
	
	/***************************************************************************
	 * Regresa la información de un evento.
	 * 
	 * @param id	ID del evento
	 * @return		La información del evento indicado
	 * @see			Event
	 **************************************************************************/
	@WebMethod(operationName="obtenerEventoPorId",
			action="urn:obtenerEventoPorId")
	@WebResult(name="eventoPorIdResultado")
	public Event obtenerEventoPorId(@WebParam(name="id") int id) {
		Event evento = new Event();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = bd.createStatement();
			
			String query = "SELECT * FROM eventos WHERE rid=" + rid +
					" AND id='" + id + "'";
			
			rs = st.executeQuery(query);
			
			while (rs.next()) {
				evento.setId(rs.getInt("id"));
				evento.setAdded(rs.getString("agregado"));
				evento.setName(rs.getString("nombre"));
				evento.setCriteria(rs.getString("criterio"));
				evento.setReady(rs.getBoolean("listo"));
				evento.setNid(rs.getInt("nid"));
				evento.setTime(rs.getString("tiempo"));
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
		
		return evento;
	}
	
	/***************************************************************************
	 * Agrega un evento a la lista de eventos. Si existe un error en la
	 * especificación del criterio, este regresa falso.
	 * 
	 * @param nombre	Nombre del evento
	 * @param criterio	Criterio del evento
	 * @return			Falso si existe un error
	 **************************************************************************/
	@WebMethod(operationName="agregarEvento",
			action="urn:agregarEvento")
	@WebResult(name="agregarEventoResultado")
	public boolean agregarEvento(
			@WebParam(name="nombre") String nombre,
			@WebParam(name="criterio") String criterio) {
		
		if (nombre == null) return false;
		if (nombre.trim().compareTo("") == 0) return false;
		if (criterio == null) return false;
		if (criterio.trim().compareTo("") == 0) return false;
		
		int i, j; String par, val, extra = "", critq = "";
		String[] crit = criterio.toLowerCase().split(" and ");
		for (i = 0; i < crit.length; i++) {
			crit[i] = crit[i].trim();
			j = crit[i].indexOf(">");
			if (j == -1) j = crit[i].indexOf("<");
			if (j == -1) j = crit[i].indexOf("=");
			par = ""; val = "";
			if (j > -1) {
				par = crit[i].substring(0, j).trim();
				val = crit[i].substring(j, crit[i].length());
			}
			if (par.compareTo("tiempo") == 0)
				extra += "AND tiempo" + val;
			else critq += " OR (parametro='" + par + "' AND valor" + val + ")";
		}
		if (critq.length() > 4)
			critq = critq.substring(4);
		
		String query = "SELECT nid, tiempo, parametro AS N FROM historico " +
				"WHERE (" +	critq + ") " + extra + " AND rid='" + rid +
				"' GROUP BY parametro";
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = bd.createStatement();
			rs = st.executeQuery(query);
		} catch (SQLException ex) {
			System.out.println(query);
			System.out.println(ex.getMessage());
			return false;
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}
		
		try {
			st = bd.createStatement();
			st.executeUpdate("INSERT INTO eventos(rid, agregado, nombre, " +
					"criterio) VALUES('" + rid + "', NOW(), '" + nombre +
					"', '" + criterio + "')");
			
		} catch (SQLException ex) {
			Errores.errorBD(ex);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (Exception e) {}
			}
		}
		
		return true;
	}
	
	/***************************************************************************
	 * Modifica el evento especificado en la lista de eventos. Si existe un
	 * error en la especificación del criterio, esta regresa falso y no
	 * realiza los cambios.
	 * 
	 * @param id		ID del evento a modificar
	 * @param nombre	Nombre del evento
	 * @param criterio	Criterio del evento
	 * @return			Falso si existe un error
	 **************************************************************************/
	@WebMethod(operationName="modificarEvento",
			action="urn:modificarEvento")
	@WebResult(name="modificarEventoResultado")
	public boolean modificarEvento(
			@WebParam(name="id") int id,
			@WebParam(name="nombre") String nombre,
			@WebParam(name="criterio") String criterio) {
		
		if (nombre == null) return false;
		if (nombre.trim().compareTo("") == 0) return false;
		if (criterio == null) return false;
		if (criterio.trim().compareTo("") == 0) return false;
		
		int i, j; String par, val, extra = "", critq = "";
		String[] crit = criterio.toLowerCase().split(" and ");
		for (i = 0; i < crit.length; i++) {
			crit[i] = crit[i].trim();
			j = crit[i].indexOf(">");
			if (j == -1) j = crit[i].indexOf("<");
			if (j == -1) j = crit[i].indexOf("=");
			par = ""; val = "";
			if (j > -1) {
				par = crit[i].substring(0, j).trim();
				val = crit[i].substring(j, crit[i].length());
			}
			if (par.compareTo("tiempo") == 0)
				extra += "AND tiempo" + val;
			else critq += " OR (parametro='" + par + "' AND valor" + val + ")";
		}
		if (critq.length() > 4)
			critq = critq.substring(4);
		
		String query = "SELECT nid, tiempo, parametro AS N FROM historico " +
				"WHERE (" +	critq + ") " + extra + " AND rid='" + rid +
				"' GROUP BY parametro";
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = bd.createStatement();
			rs = st.executeQuery(query);
		} catch (SQLException ex) {
			System.out.println(query);
			System.out.println(ex.getMessage());
			return false;
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}
		
		try {
			st = bd.createStatement();
			st.executeUpdate("UPDATE eventos SET nombre='" + nombre +
					"', criterio='" + criterio + "' WHERE id=" + id);
			
		} catch (SQLException ex) {
			Errores.errorBD(ex);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (Exception e) {}
			}
		}
		
		return true;
	}
	
	/***************************************************************************
	 * Elimina el evento con el ID especificado.
	 * 
	 * @param id	ID del evento a eliminar
	 **************************************************************************/
	@WebMethod(operationName="eliminarEvento", action="urn:eliminarEvento")
	public void eliminarEvento(@WebParam(name="id") int id) {
		Statement st = null;
		try {
			st = bd.createStatement();
			st.executeUpdate("DELETE FROM eventos WHERE id=" + id);
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
	 * Regresa un listado de las tareas de mantenimiento, regresando todas las
	 * tareas si <code>limite</code> es igual a <code>0</code> ó el número de
	 * tareas indicadas por <code>limite</code>.
	 * 
	 * @param limite	El número límite de tareas a regresar
	 * @return			Un listado de tareas
	 * @see				Tarea
	 **************************************************************************/
	@WebMethod(operationName="obtenerListadoTareas",
			action="urn:obtenerListadoTareas")
	@WebResult(name="listadoTareasResultado")
	public Vector<Tarea> obtenerListadoTareas(
			@WebParam(name="limite") int limite) {
		Vector<Tarea> tareas = new Vector<Tarea>();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = bd.createStatement();
			
			String query = "SELECT * FROM mantenimiento WHERE rid=" + rid +
					" ORDER BY tiempo DESC";
			if (limite > 0) query += " LIMIT 0," + limite;
			
			rs = st.executeQuery(query);
			
			while (rs.next()) {
				Tarea t = new Tarea();
				t.setId(rs.getInt("id"));
				t.setTipo(rs.getInt("tipo"));
				t.setValor(rs.getInt("valor"));
				t.setNid(rs.getInt("nid"));
				t.setRepetir(rs.getInt("repetir") / 1000);
				t.setEvento(rs.getInt("evento"));
				t.setTiempo(rs.getString("tiempo"));
				t.setEjecutada(rs.getString("ejecutada"));
				t.setListo(rs.getBoolean("listo"));
				tareas.add(t);
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
		
		return tareas;
	}
	
	/***************************************************************************
	 * Regresa la información de una tarea de mantenimiento.
	 * 
	 * @param id	ID de la tarea
	 * @return		La información de la tarea indicada
	 * @see			Tarea
	 **************************************************************************/
	@WebMethod(operationName="obtenerTareaPorId",
			action="urn:obtenerTareaPorId")
	@WebResult(name="tareaPorIdResultado")
	public Tarea obtenerTareaPorId(@WebParam(name="id") int id) {
		Tarea tarea = new Tarea();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = bd.createStatement();
			
			String query = "SELECT * FROM mantenimiento WHERE rid=" + rid +
					" AND id='" + id + "'";
			
			rs = st.executeQuery(query);
			
			while (rs.next()) {
				tarea.setId(rs.getInt("id"));
				tarea.setTipo(rs.getInt("tipo"));
				tarea.setValor(rs.getInt("valor"));
				tarea.setNid(rs.getInt("nid"));
				tarea.setRepetir(rs.getInt("repetir") / 1000);
				tarea.setEvento(rs.getInt("evento"));
				tarea.setTiempo(rs.getString("tiempo"));
				tarea.setEjecutada(rs.getString("ejecutada"));
				tarea.setListo(rs.getBoolean("listo"));
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
		
		return tarea;
	}
	
	/***************************************************************************
	 * Agrega una tarea de mantenimiento a la lista de tareas. Si existe un
	 * error en la especificación de la tarea, esta regresa falso.
	 * 
	 * @param tipo		Tipo de la tarea
	 * @param valor	Valor de la acción
	 * @param destino	ID del nodo destino, <code>0</code> para todos los nodos
	 * @param tiempo	Tiempo en el cual ejecutar la acción, si la cadena es
	 * 					vacía este toma el valor del tiempo actual
	 * @param repetir	Número de minutos en el cual volver a repetir la tarea,
	 * 					si <code>repetir</code> es <code>0</code> esta no se
	 *					repite
	 * @param evento	ID del evento a esperar, <code>0</code> para ninguno
	 * @return			Falso si existe un error
	 **************************************************************************/
	@WebMethod(operationName="agregarTarea", action="urn:agregarTarea")
	@WebResult(name="agregarTareaResultado")
	public boolean agregarTarea(
			@WebParam(name="tipo") int tipo,
			@WebParam(name="valor") int valor,
			@WebParam(name="destino") int destino,
			@WebParam(name="tiempo") String tiempo,
			@WebParam(name="repetir") int repetir,
			@WebParam(name="evento") int evento) {
		
		if ((tipo < 2) || (tipo > 6)) return false;
		
		if (tiempo == null) tiempo = "";
		repetir = repetir * 1000;
		
		String query = "INSERT INTO mantenimiento(" +
				"tipo, valor, nid, repetir, evento, tiempo, rid) " +
				"VALUES('" + tipo + "', '" + valor + "', '" + destino + "', '" +
				repetir + "', '" + evento + "', '" + tiempo +
				"', '" + rid + "')";
		
		Statement st = null;
		
		try {
			st = bd.createStatement();
			st.executeUpdate(query);
		} catch (SQLException ex) {
			Errores.errorBD(ex);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (Exception e) {}
			}
		}
		
		return true;
	}
	
	/***************************************************************************
	 * Modifica la tarea de mantenimiento especificada a la lista de tareas.
	 * Si existe un error en la especificación de la tarea, esta regresa falso.
	 * 
	 * @param tipo		Tipo de la tarea
	 * @param valor	Valor de la acción
	 * @param destino	ID del nodo destino, <code>0</code> para todos los nodos
	 * @param tiempo	Tiempo en el cual ejecutar la acción, si la cadena es
	 * 					vacía este toma el valor del tiempo actual
	 * @param repetir	Número de minutos en el cual volver a repetir la tarea,
	 * 					si <code>repetir</code> es <code>0</code> esta no se
	 *					repite
	 * @param evento	ID del evento a esperar, <code>0</code> para ninguno
	 * @return			Falso si existe un error
	 **************************************************************************/
	@WebMethod(operationName="modificarTarea", action="urn:modificarTarea")
	@WebResult(name="modificarTareaResultado")
	public boolean modificarTarea(
			@WebParam(name="id") int id,
			@WebParam(name="tipo") int tipo,
			@WebParam(name="valor") int valor,
			@WebParam(name="destino") int destino,
			@WebParam(name="tiempo") String tiempo,
			@WebParam(name="repetir") int repetir,
			@WebParam(name="evento") int evento) {
		
		if (tiempo == null) tiempo = "";
		repetir = repetir * 1000;
		
		String query = "UPDATE mantenimiento SET tipo='" + tipo + "', valor='" +
				valor + "', nid='" + destino + "', tiempo='" + tiempo +
				"', repetir='" + repetir + "', evento='" + evento +
				"' WHERE id=" + id;
		
		Statement st = null;
		
		try {
			st = bd.createStatement();
			st.executeUpdate(query);
		} catch (SQLException ex) {
			Errores.errorBD(ex);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (Exception e) {}
			}
		}
		
		return true;
	}
	
	/***************************************************************************
	 * Elimina la tarea de mantenimiento con el ID especificado.
	 * 
	 * @param id	ID de la tarea a eliminar
	 **************************************************************************/
	@WebMethod(operationName="eliminarTarea", action="urn:eliminarTarea")
	public void eliminarTarea(@WebParam(name="id") int id) {
		Statement st = null;
		try {
			st = bd.createStatement();
			st.executeUpdate("DELETE FROM mantenimiento WHERE id=" + id);
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

}
