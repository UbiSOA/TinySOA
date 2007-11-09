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

package net.tinyos.tinysoa.server;

import java.util.*;

import net.tinyos.tinysoa.common.*;

/*******************************************************************************
 * Interfaz que define el servicio de información.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/24/2006
 ******************************************************************************/
public interface InfoServ {
	
	/***************************************************************************
	 * Obtiene un listado de los servicios de red disponibles.
	 * 
	 * @return Un vector con la información de los servicios disponibles
	 **************************************************************************/
	public Vector<Network> obtenerListadoRedes();
	
}
