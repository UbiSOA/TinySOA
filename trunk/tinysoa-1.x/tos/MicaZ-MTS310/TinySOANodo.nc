// $Id: TinySOANodo.nc,v 0.3 2006/03/23 12:00:00 avilesl Exp $

/* "Copyright (c) 2005-2006 The Regents of the Centro de Investigaci�n y de
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
 * PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND CICESE
 * HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS."
 */

includes TinySOANodo;
includes MultiHop;

configuration TinySOANodo {
}
implementation {
	components Main, TinySOANodoM, ComunicacionC, TimerC, LecturasC,
		ActuadoresC, DescubridorC;
	
	// TinySOA
	Main.StdControl -> TinySOANodoM;
	Main.StdControl -> ComunicacionC.StdControl;
	Main.StdControl -> DescubridorC.StdControl;
	Main.StdControl -> LecturasC;
	Main.StdControl -> TimerC;
	TinySOANodoM.Timer -> TimerC.Timer[unique("Timer")];
	
	// Comunicaci�n
	TinySOANodoM.Receive -> ComunicacionC.Receive;
	TinySOANodoM.Send -> ComunicacionC.Send;
	TinySOANodoM.Drip -> ComunicacionC.Drip;
	TinySOANodoM.RouteControl -> ComunicacionC.RouteControl;
	
	// Lecturas
	TinySOANodoM.Lecturas -> LecturasC;
	
	// Actuadores
	TinySOANodoM.BocinaControl -> ActuadoresC.BocinaControl;
	TinySOANodoM.Leds -> ActuadoresC.Leds;
	
	// Descubridor
	TinySOANodoM.Descubridor -> DescubridorC.Descubridor;
}
