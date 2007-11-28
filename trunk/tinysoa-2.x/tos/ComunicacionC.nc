// $Id: Comunicacion.nc,v 0.1 2006/03/23 12:00:00 avilesl Exp $

/* "Copyright (c) 2005-2006 The Regents of the Centro de Investigación y de
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
 */
 
configuration ComunicacionC {
	provides {
		interface StdControl;
		interface Send;
		interface RouteControl;
		interface Receive;
		interface Drip;
	}
}
implementation {

	components GenericCommPromiscuous as Comm, MultiHopRouter as multihopM,
		QueuedSend, DripC, DripStateC;

	StdControl = multihopM.StdControl;
	StdControl = QueuedSend.StdControl;
	StdControl = Comm;
	StdControl = DripC;

	Receive = DripC.Receive[AM_TINYSOACMDMSG];
	Drip = DripC.Drip[AM_TINYSOACMDMSG];
	DripC.DripState[AM_TINYSOACMDMSG] -> DripStateC.DripState[unique("DripState")];
	
	RouteControl = multihopM;
	Send = multihopM.Send[AM_TINYSOAMSG];
	multihopM.ReceiveMsg[AM_TINYSOAMSG] -> Comm.ReceiveMsg[AM_TINYSOAMSG];
}
