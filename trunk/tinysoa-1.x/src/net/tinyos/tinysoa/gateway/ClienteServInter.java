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

package net.tinyos.tinysoa.gateway;

import net.tinyos.drip.*;
import net.tinyos.message.*;
import net.tinyos.tinysoa.comun.*;

/*******************************************************************************
 * Clase que implementa la funcionalidad del módulo Cliente de Servicios
 * Internos del componente TinySOA Gateway.
 * 
 * @author		Edgardo Avilés López
 * @version	0.2, 07/24/2006
 ******************************************************************************/
public class ClienteServInter implements MessageListener {

	private ProcesadorMensajes procesador;
	private boolean listo = false, enviarOcupado = false;
	private int destino, tipo, valor;
	private Drip drip;
	
	/***************************************************************************
	 * Constructor del cliente de servicios internos.
	 * 
	 * @param mote			Conector de SerialForwarder a utilizar.
	 * @param procesador	Procesador de mensajes a usar.
	 **************************************************************************/
	public ClienteServInter(MoteIF mote, ProcesadorMensajes procesador) {
		drip = new Drip(Constantes.AM_TINYSOACMDMSG, mote);
		this.procesador = procesador;
		listo = true;
	}
	
	/***************************************************************************
	 * Envía un comando a la red por medio del protocolo Drip.
	 * 
	 * @param d	Nodo al que va destinado el mensaje.
	 * @param t	Tipo del mensaje.
	 * @param v	Valor del mensaje.
	 **************************************************************************/
	public void enviarComando(int d, int t, int v) {
		this.destino = d;
		this.tipo = t;
		this.valor = v;
		
		new Thread() {
			public void run() {
				enviarOcupado = true;
				TinySOACmdMsg mensaje = new TinySOACmdMsg();
				mensaje.set_tipo((short)tipo);
				mensaje.set_id((short)destino);
				mensaje.set_datos(valor);
				drip.send(mensaje, TinySOACmdMsg.DEFAULT_MESSAGE_SIZE);
				try { Thread.sleep(5000); } catch (Exception e) {}
				enviarOcupado = false;
			}
		}.start();
	}
	
	/***************************************************************************
	 * Función para comprobar si el canal de envío de comando está ocupado.
	 * 
	 * @return	Verdadero si se está enviando un mensaje
	 **************************************************************************/
	public boolean obtEnviarOcupado() {
		return enviarOcupado;
	}
	
	/***************************************************************************
	 * Función para recibir el mensaje recogido por SerialForwarder.
	 * 
	 * @param to	Nodo destino del mensaje.
	 * @param m	Mensaje.
	 **************************************************************************/
	public void messageReceived(int to, Message m) {
		if (m instanceof TinySOAMsg) {
			procesador.recibir(to, (TinySOAMsg)m);
		}
	}
	
	/***************************************************************************
	 * Define si el cliente está listo para enviar/recibir mensajes.
	 * 
	 * @param listo
	 **************************************************************************/
	public void defListo(boolean listo) {
		this.listo = listo;
	}
	
	/***************************************************************************
	 * Devuelve verdadero si el cliente está listo para enviar/recibir mensajes.
	 * 
	 * @return	Verdadero si el cliente está listo.
	 **************************************************************************/
	public boolean obtListo() {
		return listo;
	}
	
}
