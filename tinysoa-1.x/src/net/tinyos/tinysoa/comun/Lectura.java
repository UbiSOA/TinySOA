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
 * Clase lectura cuyas instancias son ofrecidas por los servicios.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/24/2006
 ******************************************************************************/
public class Lectura {
	
	private int nid;
	private String tiempo;
	private String parametro;
	private String valor;
	
	/***************************************************************************
	 * Define el ID del nodo responsable de la lectura.
	 * 
	 * @param nid	ID del nodo
	 **************************************************************************/
	public void setNid(int nid) {
		this.nid = nid;
	}
	
	/***************************************************************************
	 * Regresa el ID del nodo responsable de la lectura.
	 * 
	 * @return	ID del nodo
	 **************************************************************************/
	public int getNid() {
		return nid;
	}
	
	/***************************************************************************
	 * Define el tiempo cuando fue realizada la lectura.
	 * 
	 * @param tiempo	Tiempo de la lectura
	 **************************************************************************/
	public void setTiempo(String tiempo) {
		this.tiempo = tiempo;
	}
	
	/***************************************************************************
	 * Regresa el tiempo cuando fue realizada la lectura.
	 * 
	 * @return	Tiempo de la lectura
	 **************************************************************************/
	public String getTiempo() {
		return tiempo;
	}
	
	/***************************************************************************
	 * Define el parámetro de la lectura.
	 * 
	 * @param parametro	Parámetro de la lectura
	 **************************************************************************/
	public void setParametro(String parametro) {
		this.parametro = parametro;
	}
	
	/***************************************************************************
	 * Regresa el parámetro de la lectura.
	 * 
	 * @return	Parámetro de la lectura
	 **************************************************************************/
	public String getParametro() {
		return parametro;
	}
	
	/***************************************************************************
	 * Define el valor de la lectura.
	 * 
	 * @param valor	Valor de la lectura
	 **************************************************************************/
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	/***************************************************************************
	 * Regresa el valor de la lectura.
	 * 
	 * @return	Valor de la lectura
	 **************************************************************************/
	public String getValor() {
		return valor;
	}
	
	/***************************************************************************
	 * Regresa una cadena con la información de la instancia.
	 * 
	 * @return	Una cadena con los valores del objeto
	 **************************************************************************/
	public String toString() {
		return nid + ": " + tiempo + " (" + parametro + "=" + valor + ")";
	}
	
}
