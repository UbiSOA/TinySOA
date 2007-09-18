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
