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
 * Clase red cuyas instancias son ofrecidas por los servicios.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/24/2006
 ******************************************************************************/
public class Red {
	
	private int id;
	private String nombre;
	private String descripcion;
	private String wsdl;
	
	/***************************************************************************
	 * Define el ID de la red.
	 * 
	 * @param id	ID de la red
	 **************************************************************************/
	public void setId(int id) {
		this.id = id;
	}
	
	/***************************************************************************
	 * Regresa el ID de la red.
	 * 
	 * @return	ID de la red
	 **************************************************************************/
	public int getId() {
		return id;
	}
	
	/***************************************************************************
	 * Define el nombre de la red.
	 * 
	 * @param nombre	Nombre de la red
	 **************************************************************************/
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	/***************************************************************************
	 * Regresa el nombre de la red.
	 * 
	 * @return	Nombre de la red
	 **************************************************************************/
	public String getNombre() {
		return nombre;
	}
	
	/***************************************************************************
	 * Define la descripción de la red.
	 * 
	 * @param descripcion	Descripción de la red
	 **************************************************************************/
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	/***************************************************************************
	 * Devuelve la descripción de la red.
	 * 
	 * @return	Descripción de la red
	 **************************************************************************/
	public String getDescripcion() {
		return descripcion;
	}
	
	/***************************************************************************
	 * Define el URL de la ubicación del archivo WSDL del servicio.
	 * 
	 * @param wsdl	URL con la ubicación del archivo WSDL
	 **************************************************************************/
	public void setWsdl(String wsdl) {
		this.wsdl = wsdl;
	}
	
	/***************************************************************************
	 * Regresa el URL de la ubicación del archivo WSDL del servicio.
	 * 
	 * @return	URL con la ubicación del archivo WSDL
	 **************************************************************************/
	public String getWsdl() {
		return wsdl;
	}
	
	/***************************************************************************
	 * Regresa una cadena con la información de la instancia.
	 * 
	 * @return	Una cadena con los valores del objeto
	 **************************************************************************/
	public String toString() {
		return id + ": " + nombre + " (" + descripcion + ")";
	}
	
}
