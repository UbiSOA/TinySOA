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

package net.tinyos.tinysoa.comun;

/*******************************************************************************
 * Clase evento cuyas instancias son ofrecidas por los servicios.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/24/2006
 ******************************************************************************/
public class Evento {
	
	private int id;
	private int nid;
	private String agregado;
	private String nombre;
	private String criterio;
	private String tiempo;
	private boolean listo;
	
	/***************************************************************************
	 * Define el ID del evento.
	 * 
	 * @param id ID del evento
	 **************************************************************************/
	public void setId(int id) {
		this.id = id;
	}
	
	/***************************************************************************
	 * Regresa el ID del evento.
	 * 
	 * @return	ID del evento
	 **************************************************************************/
	public int getId() {
		return id;
	}
	
	/***************************************************************************
	 * Define el tiempo en el que fue agregado el evento.
	 * 
	 * @param agregado	Tiempo en el que fue agregado el evento
	 **************************************************************************/
	public void setAgregado(String agregado) {
		this.agregado = agregado;
	}
	
	/***************************************************************************
	 * Devuelve el tiempo en el que fue agregado el evento.
	 * 
	 * @return Tiempo en el que fue agregado el evento
	 **************************************************************************/
	public String getAgregado() {
		return agregado;
	}
	
	/***************************************************************************
	 * Define el ID del nodo causante del evento.
	 * 
	 * @param nid	ID del nodo
	 **************************************************************************/
	public void setNid(int nid) {
		this.nid = nid;
	}
	
	/***************************************************************************
	 * Regresa el ID del nodo causante del evento.
	 * 
	 * @return	ID del nodo
	 **************************************************************************/
	public int getNid() {
		return nid;
	}
	
	/***************************************************************************
	 * Define el nombre del evento.
	 * 
	 * @param nombre	Nombre del evento
	 **************************************************************************/
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	/***************************************************************************
	 * Regresa el nombre del evento.
	 * 
	 * @return	Nombre del evento
	 **************************************************************************/
	public String getNombre() {
		return nombre;
	}
	
	/***************************************************************************
	 * Define el criterio del evento.
	 * 
	 * @param criterio	Criterio del evento
	 **************************************************************************/
	public void setCriterio(String criterio) {
		this.criterio = criterio;
	}

	/***************************************************************************
	 * Regresa el criterio del evento.
	 * 
	 * @return	Criterio del evento
	 **************************************************************************/
	public String getCriterio() {
		return criterio;
	}
	
	/***************************************************************************
	 * Define el tiempo cuando fue encontrado el evento.
	 * 
	 * @param tiempo	Tiempo de captura del evento
	 **************************************************************************/
	public void setTiempo(String tiempo) {
		this.tiempo = tiempo;
	}
	
	/***************************************************************************
	 * Regresa el tiempo cuando fue encontrado el evento.
	 * 
	 * @return	Tiempo de captura del evento
	 **************************************************************************/
	public String getTiempo() {
		return tiempo;
	}
	
	/***************************************************************************
	 * Define si el evento se ha cumplido o no.
	 * 
	 * @param listo	Verdadero si el evento se ha cumplido
	 **************************************************************************/
	public void setListo(boolean listo) {
		this.listo = listo;
	}
	
	/***************************************************************************
	 * Regresa si el evento se ha cumplido o no.
	 * 
	 * @return	Verdadero si el evento se ha cumplido
	 **************************************************************************/
	public boolean getListo() {
		return listo;
	}

}
