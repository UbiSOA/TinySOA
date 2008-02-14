/*
 *  Copyright 2006 Edgardo Avilés López
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
 ******************************************************************************/

package net.tinyos.tinysoa.gateway;

import net.tinyos.drip.*;
import net.tinyos.message.*;
import net.tinyos.tinysoa.common.*;

/*******************************************************************************
 * Class that implements the Internal Services module functionality of the
 * TinySOA Gateway component.
 * 
 * @author		Edgardo Avilés López
 * @version	0.2, 07/24/2006
 ******************************************************************************/
public class InternalServicesClient implements MessageListener {

	private MessageProcessor processor;
	private boolean ready = false, sendBusy = false;
	private int target, type, value;
	private Drip drip;
	
	/***************************************************************************
	 * Internal services client constructor.
	 * 
	 * @param mote		SerialForwarder connector to use
	 * @param processor	Message processor to use
	 **************************************************************************/
	public InternalServicesClient(MoteIF mote, MessageProcessor processor) {
		drip = new Drip(Constants.AM_TINYSOACMDMSG, mote);
		this.processor = processor;
		ready = true;
	}
	
	/***************************************************************************
	 * Send a command to sensor network via Drip protocol.
	 * 
	 * @param d	Target node
	 * @param t	Message type
	 * @param v	Message value
	 **************************************************************************/
	public void sendCommand(int d, int t, int v) {
		this.target = d;
		this.type = t;
		this.value = v;
		
		new Thread() {
			public void run() {
				sendBusy = true;
				TinySOACmdMsg message = new TinySOACmdMsg();
				message.set_type((short)type);
				message.set_id((short)target);
				message.set_data(value);
				drip.send(message, TinySOACmdMsg.DEFAULT_MESSAGE_SIZE);
				try { Thread.sleep(5000); } catch (Exception e) {}
				sendBusy = false;
			}
		}.start();
	}
	
	/***************************************************************************
	 * Returns if the send channel is busy.
	 * 
	 * @return	True is a message is been sent
	 **************************************************************************/
	public boolean getSendBusy() {
		return sendBusy;
	}
	
	/***************************************************************************
	 * Method to receive the message gotten by SerialForwarder.
	 * 
	 * @param to	Target node
	 * @param m		Message
	 **************************************************************************/
	public void messageReceived(int to, Message m) {
		if (m instanceof TinySOAMsg) {
			processor.receive(to, (TinySOAMsg)m);
		}
	}
	
	/***************************************************************************
	 * Defines if client is ready to send/receive messages.
	 * 
	 * @param ready
	 **************************************************************************/
	public void setReady(boolean listo) {
		this.ready = listo;
	}
	
	/***************************************************************************
	 * Returns true if client is ready to send/receive messages.
	 * 
	 * @return	True is ready to send/receive
	 **************************************************************************/
	public boolean getReady() {
		return ready;
	}
	
}
