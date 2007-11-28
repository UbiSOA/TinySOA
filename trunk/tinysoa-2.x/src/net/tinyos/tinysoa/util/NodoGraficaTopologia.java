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

package net.tinyos.tinysoa.util;

import java.awt.*;
import java.text.*;

/*******************************************************************************
 * Clase para manejar la información de un nodo en la gráfica de topología.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/25/2006
 ******************************************************************************/
public class NodoGraficaTopologia {
	
	private Point p;
	private double v;
	private int id;
	
	/***************************************************************************
	 * Constructor básico de la clase.
	 * 
	 * @param id	ID del nodo
	 **************************************************************************/
	public NodoGraficaTopologia(int id) {
		this.id = id;
		this.v = 0.0d;
		this.p = new Point(0, 0);
	}
	
	/***************************************************************************
	 * Constructor normal de la clase.
	 * 
	 * @param id	ID del nodo
	 * @param v	Valor del nodo
	 **************************************************************************/
	public NodoGraficaTopologia(int id, double v) {
		this.id = id;
		this.v = v;
		this.p = new Point(0, 0);
	}
	
	/***************************************************************************
	 * Constructor detallado de la clase.
	 * 
	 * @param id	ID del nodo
	 * @param v	Valor del nodo
	 * @param x	Valor X de la posición del nodo
	 * @param y	Valor Y de la posición del nodo
	 **************************************************************************/
	public NodoGraficaTopologia(int id, double v, int x, int y) {
		this.id = id;
		this.v = v;
		this.p = new Point(x, y);
	}
	
	/***************************************************************************
	 * Define la posición del nodo en el gráfico.
	 * 
	 * @param x	Valor X de la posición del nodo
	 * @param y	Valor Y de la posición del nodo
	 **************************************************************************/
	public void defPosicion(int x, int y) {
		p.x = x;
		p.y = y;
	}
	
	/***************************************************************************
	 * Define la posición del nodo en el gráfico.
	 * 
	 * @param p	Posición del nodo
	 **************************************************************************/
	public void defPosicion(Point p) {
		this.p.x = p.x;
		this.p.y = p.y;
	}
	
	/***************************************************************************
	 * Regresa la posición del nodo en el gráfico.
	 * 
	 * @return	Posición del nodo
	 **************************************************************************/
	public Point obtPosicion() {
		return p;
	}

	/***************************************************************************
	 * Define el ID del nodo.
	 * 
	 * @param id	ID del nodo
	 **************************************************************************/
	public void defId(int id) {
		this.id = id;
	}
	
	/***************************************************************************
	 * Regresa el ID del nodo.
	 * 
	 * @return	ID del nodo
	 **************************************************************************/
	public int obtId() {
		return id;
	}
	
	/***************************************************************************
	 * Define el valor del nodo.
	 * 
	 * @param v	Valor del nodo
	 **************************************************************************/
	public void defValor(double v) {
		this.v = v;
	}
	
	/***************************************************************************
	 * Regresa el valor del nodo.
	 * 
	 * @return	Valor del nodo
	 **************************************************************************/
	public double obtValor() {
		return v;
	}
	
	/***************************************************************************
	 * Regresa una cadena con la representación de los valores del nodo.
	 **************************************************************************/
	public String toString() {
		NumberFormat f = new DecimalFormat("0.00");
		return id + ": (" + p.x + "," + p.y + ")," + f.format(v);
	}
	
}
