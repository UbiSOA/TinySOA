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
 ****************************************************************************************************/

package net.tinyos.tinysoa.common;

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
