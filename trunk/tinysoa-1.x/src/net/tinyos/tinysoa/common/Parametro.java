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
 * PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND CICESE
 * HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS."
 * 
 ******************************************************************************/

package net.tinyos.tinysoa.common;

/*******************************************************************************
 * Clase par�metro cuyas instancias son ofrecidas por los servicios.
 * 
 * @author		Edgardo Avil�s L�pez
 * @version	0.1, 07/24/2006
 ******************************************************************************/
public class Parametro {
	
	private String nombre;
	private String descripcion;
	
	/***************************************************************************
	 * Define el nombre del par�metro.
	 * 
	 * @param nombre	Nombre del par�metro
	 **************************************************************************/
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	/***************************************************************************
	 * Regresa el nombre del par�metro.
	 * 
	 * @return	Nombre del par�metro
	 **************************************************************************/
	public String getNombre() {
		return nombre;
	}
	
	/***************************************************************************
	 * Define la descripci�n del par�metro.
	 * 
	 * @param descripcion	Descripci�n del par�metro
	 **************************************************************************/
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	/***************************************************************************
	 * Regresa la descripci�n del par�metro.
	 * 
	 * @return	Descripci�n del par�metro
	 **************************************************************************/
	public String getDescripcion() {
		return descripcion;
	}
	
}