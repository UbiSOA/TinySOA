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
 * PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND CICESE
 * HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS."
 * 
 ******************************************************************************/

package net.tinyos.tinysoa.common;

/*******************************************************************************
 * Clase tarea cuyas instancias son ofrecidas por los servicios.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/27/2006
 ******************************************************************************/
public class Tarea {
	
	private int id;
	private int tipo;
	private int valor;
	private int nid;
	private int repetir;
	private int evento;
	private String tiempo;
	private String ejecutada;
	private boolean listo;
	
	/***************************************************************************
	 * Define el ID de la tarea.
	 * 
	 * @param id	ID de la tarea
	 **************************************************************************/
	public void setId(int id) {
		this.id = id;
	}

	/***************************************************************************
	 * Regresa el ID de la tarea.
	 * 
	 * @return ID de la tarea
	 **************************************************************************/
	public int getId() {
		return id;
	}

	/***************************************************************************
	 * Define el tipo de la acción a realizar por la tarea.
	 * 
	 * @param tipo	Tipo de la acción a realizar
	 **************************************************************************/
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	/***************************************************************************
	 * Regresa el tipo de la acción a realizar por la tarea.
	 * 
	 * @return Tipo de la acción a realizar
	 **************************************************************************/
	public int getTipo() {
		return tipo;
	}

	/***************************************************************************
	 * Define el valor de la acción a realizar por la tarea.
	 * 
	 * @param valor	Valor de la acción
	 **************************************************************************/
	public void setValor(int valor) {
		this.valor = valor;
	}

	/***************************************************************************
	 * Regresa el valor de la acción a realizar por la tarea.
	 * 
	 * @return Valor de la acción
	 **************************************************************************/
	public int getValor() {
		return valor;
	}

	/***************************************************************************
	 * Define el ID del nodo objetivo de la tarea. Si el ID es <code>0</code>
	 * este corresponde a todos los nodos de la red.
	 * 
	 * @param nid	ID del nodo objetivo
	 **************************************************************************/
	public void setNid(int nid) {
		this.nid = nid;
	}

	/***************************************************************************
	 * Regresa el ID del nodo objetivo de la tarea. Si el ID es <code>0</code>
	 * este corresponde a todos los nodos de la red.
	 * 
	 * @return ID del nodo objetivo
	 **************************************************************************/
	public int getNid() {
		return nid;
	}

	/***************************************************************************
	 * Define la cantidad de minutos en la que se estará repitiendo la tarea,
	 * si <code>repetir</code> es <code>0</code> no se realizarán repeticiones.
	 * 
	 * @param repetir	Número de minutos en los cuales se repetirá la tarea
	 **************************************************************************/
	public void setRepetir(int repetir) {
		this.repetir = repetir;
	}

	/***************************************************************************
	 * Regresa la cantidad de minutos en la que se estará repitiendo la tarea,
	 * si <code>repetir</code> es <code>0</code> no se realizarán repeticiones.
	 * 
	 * @return Número de minutos en los cuales se repetirá la tarea
	 **************************************************************************/
	public int getRepetir() {
		return repetir;
	}

	/***************************************************************************
	 * Define el ID del evento a esperar para ejecutar la tarea. Si
	 * <code>evento</code> es <code>0</code> la tarea se ejecuta sin esperar
	 * ningún evento.
	 * 
	 * @param evento ID del evento a esperar
	 **************************************************************************/
	public void setEvento(int evento) {
		this.evento = evento;
	}

	/***************************************************************************
	 * Regresa el ID del evento a esperar para ejecutar la tarea. Si
	 * <code>evento</code> es <code>0</code> la tarea se ejecuta sin esperar
	 * ningún evento.
	 * 
	 * @return ID del evento a esperar
	 **************************************************************************/
	public int getEvento() {
		return evento;
	}

	/***************************************************************************
	 * Define el tiempo en el cual se ejecutará la tarea.
	 * 
	 * @param tiempo	Tiempo en el cual se ejecutará la tarea
	 **************************************************************************/
	public void setTiempo(String tiempo) {
		this.tiempo = tiempo;
	}

	/***************************************************************************
	 * Regresa el tiempo en el cual se ejecutará la tarea.
	 * 
	 * @return Tiempo en el cual se ejecutará la tarea
	 **************************************************************************/
	public String getTiempo() {
		return tiempo;
	}

	/***************************************************************************
	 * Define el tiempo en el que la tarea fue ejecutada por última vez.
	 * 
	 * @param ejecutada	Tiempo de la última ejecución de la tarea
	 **************************************************************************/
	public void setEjecutada(String ejecutada) {
		this.ejecutada = ejecutada;
	}

	/***************************************************************************
	 * Regresa el tiempo en el que la tarea fue ejecutada por última vez.
	 * 
	 * @return Returns Tiempo de la última ejecución de la tarea
	 **************************************************************************/
	public String getEjecutada() {
		return ejecutada;
	}
	
	/***************************************************************************
	 * Define si la tarea ya fue ejecutada.
	 * 
	 * @param listo	Verdadero si la tarea ya fue ejecutada
	 **************************************************************************/
	public void setListo(boolean listo) {
		this.listo = listo;
	}

	/***************************************************************************
	 * Regresa si la tarea ya fue ejecutada.
	 * 
	 * @return Verdadero si la tarea ya fue ejecutada
	 **************************************************************************/
	public boolean getListo() {
		return listo;
	}
	
}
