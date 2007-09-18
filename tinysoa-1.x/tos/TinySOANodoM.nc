// $Id: TinySOANodo.nc,v 0.2 2006/03/23 12:00:00 avilesl Exp $

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
 
includes TinySOANodo;

module TinySOANodoM {
	provides {
		interface StdControl;
	}
	uses {
		// Comunicación
		interface Send;
		interface RouteControl;
		interface Receive;
		interface Drip;
		
		// Lecturas
		interface Lecturas;
		
		// Actuadores
		interface Leds;
		interface StdControl as BocinaControl;
		
		// Descubridor
		interface Descubridor;
		
		// TinyOS
		interface Timer;
	}
}

implementation {

	bool		ocupado, suscritos;
	uint8_t		nsec, tipo, id;
	uint16_t	datos;
	TOS_Msg		mensaje;

	// -------------------------------------------------------------------------
	
	command result_t StdControl.init() {
		dbg(DBG_USR1, "TinySOA: Inicializando.\n");
		nsec = 0;
		suscritos = FALSE;
		atomic ocupado = FALSE;
		call Drip.init();
		call Leds.init();
		call BocinaControl.init();
		return SUCCESS;
	}

	command result_t StdControl.start() {
		call Leds.set(TOS_LOCAL_ADDRESS);
		return call Timer.start(TIMER_REPEAT, DATA_RATE_INICIAL);
	}

	command result_t StdControl.stop() {
		return call Timer.stop();
	}
	
	event result_t Timer.fired() {
		if (nsec == 0) call Leds.set(0);
		if (nsec++ % REP_PUBLIC == 0) {
			if (LEDS_DEBUG) call Leds.yellowOn();
			dbg(DBG_USR1, "TinySOA: Descubriendo servicios...\n");
			call Descubridor.descubrir();
		}
		else {
			 if (suscritos) {
				if (LEDS_DEBUG) call Leds.yellowOn();
				dbg(DBG_USR1, "TinySO: Preparando lecturas...\n");		
				call Lecturas.leer();
			}
		}
		return SUCCESS;
	}
	
	// -------------------------------------------------------------------------

	task void enviarDatos() {
		TinySOAMsg *p;
		uint16_t n;
		
		if (LEDS_DEBUG) call Leds.greenOn();
		dbg(DBG_USR1, "TinySOA: Enviando lecturas.\n");
		
		if ((p = (TinySOAMsg *)call Send.getBuffer(&mensaje, &n))) {
			p->id		= TOS_LOCAL_ADDRESS;
			p->padre	= call RouteControl.getParent();
			p->nsec		= nsec;
			p->tipo		= TINYSOA_TIPO_LECTURA;
			p->sensor	= SENSOR_BOARD_ID;
			p->l1		= call Lecturas.leerLect(1);	// temp
			p->l2		= call Lecturas.leerLect(2);	// luz
			p->l3		= call Lecturas.leerLect(3);	// magx
			p->l4		= call Lecturas.leerLect(4);	// magy
			p->l5		= call Lecturas.leerLect(5);	// acex
			p->l6		= call Lecturas.leerLect(6);	// acey
			p->l7		= call Lecturas.leerLect(7);	// mic
			p->l8		= call Lecturas.leerLect(8);	// volt
			
			if ((call Send.send(&mensaje, sizeof(TinySOAMsg))) != SUCCESS)
				atomic ocupado = FALSE;
		}
		
	}

	async event result_t Lecturas.leerListo() {
		if (LEDS_DEBUG) call Leds.yellowOff();
		dbg(DBG_USR1, "TinySOA: Lecturas listas.\n");
		atomic {
			if (!ocupado) {
				ocupado = TRUE;
				post enviarDatos();
			}
		}
		return SUCCESS;
	}
	
	event result_t Send.sendDone(TOS_MsgPtr param0, result_t param1) {
		if (LEDS_DEBUG) call Leds.greenOff();
		dbg(DBG_USR1, "TinySOA: Lecturas o registro enviados.\n");
		atomic ocupado = FALSE;
		return SUCCESS;
	}
	
	// -------------------------------------------------------------------------
	
	task void enviarDescubiertos() {
		TinySOAMsg *p;
		uint16_t n;
		
		if (LEDS_DEBUG) call Leds.greenOn();
		dbg(DBG_USR1, "TinySOA: Enviando registro.\n");
		
		if ((p = (TinySOAMsg *)call Send.getBuffer(&mensaje, &n))) {
			p->id		= TOS_LOCAL_ADDRESS;
			p->padre	= call RouteControl.getParent();
			p->nsec		= nsec;
			p->tipo		= TINYSOA_TIPO_REGISTRO;
			p->sensor	= SENSOR_BOARD_ID;
			p->l1		= call Descubridor.leerDesc(1);	// temp
			p->l2		= call Descubridor.leerDesc(2);	// luz
			p->l3		= call Descubridor.leerDesc(3);	// magx
			p->l4		= call Descubridor.leerDesc(4);	// magy
			p->l5		= call Descubridor.leerDesc(5);	// acex
			p->l6		= call Descubridor.leerDesc(6);	// acey
			p->l7		= call Descubridor.leerDesc(7);	// mic
			p->l8		= call Descubridor.leerDesc(8);	// volt
			
			if ((call Send.send(&mensaje, sizeof(TinySOAMsg))) != SUCCESS)
				atomic ocupado = FALSE;
		}
		
	}
	
	async event result_t Descubridor.descubrirListo() {
		dbg(DBG_USR1, "TinySOA: Servicios descubiertos y listos.\n");
		if (LEDS_DEBUG) call Leds.yellowOff();
		atomic {
			if (!ocupado) {
				ocupado = TRUE;
				post enviarDescubiertos();
			}
		}
		return SUCCESS;
	}
	
	// -------------------------------------------------------------------------
	
	event TOS_MsgPtr Receive.receive(TOS_MsgPtr pMsg, void* payload, uint16_t payLoadLen) {
	
		TinySOACmdMsg *cmd = (TinySOACmdMsg*)payload;
		if (LEDS_DEBUG) call Leds.redOn();
		
		datos = cmd -> datos;
		tipo = cmd -> tipo;
		id = cmd -> id;		

		if (tipo == TINYSOA_TIPO_ACTIVA_ACTUADOR) {
			if (datos == TINYSOA_ACTUADOR_BOCINA)
				call BocinaControl.start();
			if (datos == TINYSOA_ACTUADOR_LED_ROJO)
				call Leds.redOn();
			if (datos == TINYSOA_ACTUADOR_LED_AMARILLO)
				call Leds.yellowOn();
			if (datos == TINYSOA_ACTUADOR_LED_VERDE)
				call Leds.greenOn();
			if (datos == TINYSOA_ACTUADOR_LED_AZUL)
				call Leds.yellowOn();
		}
				
		if (tipo == TINYSOA_TIPO_DESACTIVA_ACTUADOR) {
			if (datos == TINYSOA_ACTUADOR_BOCINA)
				call BocinaControl.stop();
			if (datos == TINYSOA_ACTUADOR_LED_ROJO)
				call Leds.redOff();
			if (datos == TINYSOA_ACTUADOR_LED_AMARILLO)
				call Leds.yellowOff();
			if (datos == TINYSOA_ACTUADOR_LED_VERDE)
				call Leds.greenOff();
			if (datos == TINYSOA_ACTUADOR_LED_AZUL)
				call Leds.yellowOff();
		}
		
		if (tipo == TINYSOA_TIPO_DUERME) {
			if ((id == TOS_LOCAL_ADDRESS) || (id == 0))
				call Timer.stop();
		}
		
		if (tipo == TINYSOA_TIPO_DESPIERTA) {
			if ((id == TOS_LOCAL_ADDRESS) || (id == 0))
				call Timer.start(TIMER_REPEAT, DATA_RATE_INICIAL);
		}
		
		if (tipo == TINYSOA_TIPO_CAMBIA_DATA_RATE) {
			if ((id == TOS_LOCAL_ADDRESS) || (id == 0)) {
				call Timer.stop();
				call Timer.start(TIMER_REPEAT, datos);
			}
		}
		
		if (tipo == TINYSOA_TIPO_SOLICITUD_REGISTRO) {
			if ((id == TOS_LOCAL_ADDRESS) || (id == 0)) {
				if (LEDS_DEBUG) call Leds.yellowOn();
				dbg(DBG_USR1, "TinySOA: Descubriendo servicios...\n");
				call Descubridor.descubrir();
			}
		}
		
		if (tipo == TINYSOA_TIPO_SUSCRIBIR) {
			if (datos == SENSOR_BOARD_ID)
				suscritos = TRUE;
		}
		
		if (LEDS_DEBUG) call Leds.redOff();
		
		return pMsg;
	}
	
	event result_t Drip.rebroadcastRequest(TOS_MsgPtr msg, void *payload) {
		TinySOACmdMsg *cmd = (TinySOACmdMsg*)payload;

		cmd -> datos = datos;
		cmd -> tipo = tipo;
		cmd -> id = id;
		
		call Drip.rebroadcast(msg, payload, sizeof(TinySOACmdMsg));
		
		return SUCCESS;
	}

}
