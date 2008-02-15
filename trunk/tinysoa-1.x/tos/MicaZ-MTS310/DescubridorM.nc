// $Id: LecturasM.nc,v 0.2 2006/03/23 12:00:00 avilesl Exp $

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
 
module DescubridorM {
	provides {
		interface StdControl;
		interface Descubridor;
	}
}
implementation {

	norace uint16_t v1	= 0x0000;
	norace uint16_t v2	= 0x0000;
	norace uint16_t v3	= 0x0000;
	norace uint16_t v4	= 0x0000;
	norace uint16_t v5	= 0x0000;
	norace uint16_t v6	= 0x0000;
	norace uint16_t v7	= 0x0000;
	norace uint16_t v8	= 0x0000;

	command result_t StdControl.init() {
		return SUCCESS;
	}
	
	command result_t StdControl.start() {
		return SUCCESS;
	}
	
	command result_t StdControl.stop() {
		return SUCCESS;
	}
	
	default async event result_t Descubridor.descubrirListo() {
		return SUCCESS;
	}

	command async result_t Descubridor.descubrir() {
		v1 = TINYSOA_SENSOR_TEMP;
		v2 = TINYSOA_SENSOR_LUZ;
		v3 = TINYSOA_SENSOR_MAGX;
		v4 = TINYSOA_SENSOR_MAGY;
		v5 = TINYSOA_SENSOR_ACEX;
		v6 = TINYSOA_SENSOR_ACEY;
		v7 = TINYSOA_SENSOR_MIC;
		v8 = TINYSOA_SENSOR_VOLT;
		
		return signal Descubridor.descubrirListo();
	}

	command async uint16_t Descubridor.leerDesc(uint8_t id) {
		if (id == 1) return v1;
		if (id == 2) return v2;
		if (id == 3) return v3;
		if (id == 4) return v4;
		if (id == 5) return v5;
		if (id == 6) return v6;
		if (id == 7) return v7;
		if (id == 8) return v8;
		return 0;
	}
}
