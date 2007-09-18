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

import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

/*******************************************************************************
 * Clase para manejar la información de un nodo de arbol representando a un
 * nodo en la red de sensores.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/25/2006
 ******************************************************************************/
public class RedNodoArbol extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 5275043962674696848L;
	
	protected boolean isSelected;
	protected Icon iconoEstado;

	/***************************************************************************
	 * Constructor básico de la clase.
	 * 
	 * @param objeto	Nombre del nodo
	 **************************************************************************/
	public RedNodoArbol(Object objeto) {
		this(objeto, UIManager.getIcon("Tree.leafIcon"));
	}
	
	/***************************************************************************
	 * Constructor normal de la red.
	 * 
	 * @param objeto	Nombre del nodo
	 * @param icono	Icono del nodo
	 **************************************************************************/
	public RedNodoArbol(Object objeto, Icon icono) {
		super(objeto);
		isSelected = true;
		iconoEstado = icono;
	}
	
	/***************************************************************************
	 * Define si el nodo está seleccionado.
	 * 
	 * @param isSelected	Verdadero si está seleccionado
	 **************************************************************************/
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		
		if (children != null) {
			Enumeration e = children.elements();
			while (e.hasMoreElements()) {
				RedNodoArbol nodo = (RedNodoArbol)e.nextElement();
				nodo.setSelected(true);
			}
		}
	}
	
	/***************************************************************************
	 * Regresa si el nodo está seleccionado.
	 * 
	 * @return	Verdadero si está seleccionado
	 **************************************************************************/
	public boolean isSelected() {
		return isSelected;
	}
	
	/***************************************************************************
	 * Define el icono del nodo.
	 * 
	 * @param icono	Icono del nodo
	 **************************************************************************/
	public void setIcon(Icon icono) {
		this.iconoEstado = icono;
	}

	/***************************************************************************
	 * Devuelve el icono del nodo.
	 * 
	 * @return	Icono del nodo
	 **************************************************************************/
	public Icon getIcon() {
		return iconoEstado;
	}

}
