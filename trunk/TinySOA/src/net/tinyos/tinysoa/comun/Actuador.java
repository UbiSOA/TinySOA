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

/******************* ************************************************************
 * Clase actuador cuyas instancias son ofrecidas por los servicios.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/24/2006
 ******************************************************************************/
public class Actuador {
	
	private String nombre;
	private String descripcion;
	
	/***************************************************************************
	 * Define el nombre del actuador.
	 * 
	 * @param nombre	Nombre del actuador
	 **************************************************************************/
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	/***************************************************************************
	 * Regresa el nombre del actuador.
	 * 
	 * @return	Nombre del actuador
	 **************************************************************************/
	public String getNombre() {
		return nombre;
	}
	
	/***************************************************************************
	 * Define la descripción del actuador.
	 * 
	 * @param descripcion	Descripción del actuador
	 **************************************************************************/
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	/***************************************************************************
	 * Regresa la descripción del actuador.
	 * 
	 * @return	Descripción del actuador
	 **************************************************************************/
	public String getDescripcion() {
		return descripcion;
	}
	
}
