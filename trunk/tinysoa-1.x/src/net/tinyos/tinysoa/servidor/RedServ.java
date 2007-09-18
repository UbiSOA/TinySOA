/*
 * "Copyright (c) 2005-2006 The Regents of the Centro de Investigaci�n y de
 * Educaci�n Superior de la ciudad de Ensenada, Baja California (CICESE).
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

import java.util.*;

import net.tinyos.tinysoa.comun.*;

/*******************************************************************************
 * Interfaz que define el servicio de red.
 * 
 * @author		Edgardo Avil�s L�pez
 * @version	0.5, 07/28/2006
 ******************************************************************************/
public interface RedServ {
	
	/***************************************************************************
	 * Regresa el ID de la red.
	 * 
	 * @return	ID de la red
	 **************************************************************************/
	public int obtenerIdRed();
	
	/***************************************************************************
	 * Regresa el nombre de la red.
	 * 
	 * @return	Nombre de la red.
	 **************************************************************************/
	public String obtenerNombreRed();
	
	/***************************************************************************
	 * Regresa la descripci�n de la red.
	 * 
	 * @return	Descripci�n de la red.
	 **************************************************************************/
	public String obtenerDescripcionRed();
	
	/***************************************************************************
	 * Regresa la fecha y hora desde la cual se tiene informaci�n.
	 * 
	 * @return Tiempo m�nimo de informaci�n disponible
	 **************************************************************************/
	public String obtenerTiempoMinimo();
	
	/***************************************************************************
	 * Regresa la fecha y hora hasta la cual se tiene informaci�n.
	 * 
	 * @return	Tiempo m�ximo de informaci�n disponible
	 **************************************************************************/
	public String obtenerTiempoMaximo();
	
	/***************************************************************************
	 * Regresa una lista de los nodos disponibles en la red.
	 * 
	 * @return Una lista de nodos
	 * @see		Nodo
	 **************************************************************************/
	public Vector<Nodo> obtenerListadoNodos();
	
	/***************************************************************************
	 * Regresa una lista de los actuadores disponibles en la red.
	 * 
	 * @return	Una lista de actuadores
	 * @see		Actuador
	 **************************************************************************/
	public Vector<Actuador> obtenerActuadores();
	
	/***************************************************************************
	 * Regresa una lista de los par�metros de sensado disponibles.
	 * 
	 * @return Una lista de par�metros
	 * @see		Parametro
	 **************************************************************************/
	public Vector<Parametro> obtenerParametros();
	
	/***************************************************************************
	 * Regresa un listado de las �ltimas lecturas por cada uno de los nodos
	 * en la red.
	 * 
	 * @param parametro	El par�metro de sensado deseado, deje vac�o para todos
	 * @param limite	El n�mero l�mite de lecturas a regresar
	 * @return			Un listado de lecturas
	 * @see				Lectura
	 **************************************************************************/
	public Vector<Lectura> obtenerUltimasLecturas(String parametro, int limite);
	
	/***************************************************************************
	 * Regresa un listado de las lecturas disponibles hasta el tiempo indicado,
	 * regresando todas las lecturas si <code>limite</code> es igual a
	 * <code>0</code> � el n�mero de lecturas indicado por <code>limite</code>.
	 * 
	 * @param tiempo	El tiempo hasta el cual regresar lecturas
	 * @param limite	El n�mero l�mite de lecturas a regresar
	 * @return			Un listado de lecturas
	 * @see				Lectura
	 **************************************************************************/
	public Vector<Lectura> obtenerLecturasAlTiempo(String tiempo, int limite);
	
	/***************************************************************************
	 * Regresa un listado de las lecturas disponibles en el rango de tiempo
	 * indicado, regresando todas las lecturas si <code>limite</code> es igual
	 * a <code>0</code> � el n�mero de lecturas indicado por
	 * <code>limite</code>. De manera similar se pueden regresar las lecturas
	 * para un par�metro determinado, si este par�metro es una cadena vac�a
	 * �ste regresa valores para todos los par�metros.
	 * 
	 * @param desde		Tiempo desde el cual regresar lecturas
	 * @param hasta		Tiempo hasta el cual regresar lecturas
	 * @param parametro	Par�metro del cu�l obtener lecturas
	 * @param limite		El n�mero l�mite de lecturas a regresar
	 * @return				Un listado de lecturas
	 * @see					Lectura
	 **************************************************************************/
	public Vector<Lectura> obtenerLecturas(String desde, String hasta,
			String parametro, int limite);
	
	/***************************************************************************
	 * Regresa un listado de los eventos, regresando todos los eventos si
	 * <code>limite</code> es igual a <code>0</code> � el n�mero de eventos
	 * indicados por <code>limite</code>.
	 * 
	 * @param limite	El n�mero l�mite de eventos a regresar
	 * @return			Un listado de eventos
	 * @see				Evento
	 **************************************************************************/
	public Vector<Evento> obtenerListadoEventos(int limite);
	
	/***************************************************************************
	 * Regresa la informaci�n de un evento.
	 * 
	 * @param id	ID del evento
	 * @return		La informaci�n del evento indicado
	 * @see			Evento
	 **************************************************************************/
	public Evento obtenerEventoPorId(int id);
	
	/***************************************************************************
	 * Agrega un evento a la lista de eventos. Si existe un error en la
	 * especificaci�n del criterio, esta regresa falso.
	 * 
	 * @param nombre	Nombre del evento
	 * @param criterio	Criterio del evento
	 * @return			Falso si existe un error
	 **************************************************************************/
	public boolean agregarEvento(String nombre, String criterio);
	
	/***************************************************************************
	 * Modifica el evento especificado en la lista de eventos. Si existe un
	 * error en la especificaci�n del criterio, esta regresa falso y no
	 * realiza los cambios.
	 * 
	 * @param id		ID del evento a modificar
	 * @param nombre	Nombre del evento
	 * @param criterio	Criterio del evento
	 * @return			Falso si existe un error
	 **************************************************************************/
	public boolean modificarEvento(int id, String nombre, String criterio);
	
	/***************************************************************************
	 * Elimina el evento con el ID especificado.
	 * 
	 * @param id	ID del evento a eliminar
	 **************************************************************************/
	public void eliminarEvento(int id);
	
	/***************************************************************************
	 * Regresa un listado de las tareas de mantenimiento, regresando todas las
	 * tareas si <code>limite</code> es igual a <code>0</code> � el n�mero de
	 * tareas indicadas por <code>limite</code>.
	 * 
	 * @param limite	El n�mero l�mite de tareas a regresar
	 * @return			Un listado de tareas
	 * @see				Tarea
	 **************************************************************************/
	public Vector<Tarea> obtenerListadoTareas(int limite);
	
	/***************************************************************************
	 * Regresa la informaci�n de una tarea de mantenimiento.
	 * 
	 * @param id	ID de la tarea
	 * @return		La informaci�n de la tarea indicada
	 * @see			Tarea
	 **************************************************************************/
	public Tarea obtenerTareaPorId(int id);
	
	/***************************************************************************
	 * Agrega una tarea de mantenimiento a la lista de tareas. Si existe un
	 * error en la especificaci�n de la tarea, esta regresa falso.
	 * 
	 * @param tipo		Tipo de la tarea
	 * @param valor	Valor de la acci�n
	 * @param destino	ID del nodo destino, <code>0</code> para todos los nodos
	 * @param tiempo	Tiempo en el cual ejecutar la acci�n, si la cadena es
	 * 					vac�a este toma el valor del tiempo actual
	 * @param repetir	N�mero de minutos en el cual volver a repetir la tarea,
	 * 					si <code>repetir</code> es <code>0</code> esta no se
	 *					repite
	 * @param evento	ID del evento a esperar, <code>0</code> para ninguno
	 * @return			Falso si existe un error
	 **************************************************************************/
	public boolean agregarTarea(int tipo, int valor, int destino,
			String tiempo, int repetir, int evento);
	
	/***************************************************************************
	 * Modifica la tarea de mantenimiento especificada a la lista de tareas.
	 * Si existe un error en la especificaci�n de la tarea, esta regresa falso.
	 * 
	 * @param tipo		Tipo de la tarea
	 * @param valor	Valor de la acci�n
	 * @param destino	ID del nodo destino, <code>0</code> para todos los nodos
	 * @param tiempo	Tiempo en el cual ejecutar la acci�n, si la cadena es
	 * 					vac�a este toma el valor del tiempo actual
	 * @param repetir	N�mero de minutos en el cual volver a repetir la tarea,
	 * 					si <code>repetir</code> es <code>0</code> esta no se
	 *					repite
	 * @param evento	ID del evento a esperar, <code>0</code> para ninguno
	 * @return			Falso si existe un error
	 **************************************************************************/
	public boolean modificarTarea(int id, int tipo, int valor, int destino,
			String tiempo, int repetir, int evento);
	
	/***************************************************************************
	 * Elimina la tarea de mantenimiento con el ID especificado.
	 * 
	 * @param id	ID de la tarea a eliminar
	 **************************************************************************/
	public void eliminarTarea(int id);

}
