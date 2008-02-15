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
 
module LecturasM {
	provides {
		interface StdControl;
		interface Lecturas;
	}
	uses {
		interface StdControl as TempControl;
		interface StdControl as LuzControl;
		interface StdControl as AcelControl;
		interface StdControl as MicControl;
		interface StdControl as BatControl;
		interface ADC as Bat;
		interface ADC as Temp;
		interface ADC as Luz;
		interface ADC as AcelX;
		interface ADC as AcelY;
		interface ADC as MicADC;
		interface Timer as TempTimer;
		interface AttrUse;
		interface Mic;
	}
}
implementation {
	norace uint16_t vTemp	= 0x0000;
	norace uint16_t vLuz	= 0x0000;
	norace uint16_t vMagX	= 0x0000;
	norace uint16_t vMagY	= 0x0000;
	norace uint16_t vAcelX	= 0x0000;
	norace uint16_t vAcelY	= 0x0000;
	norace uint16_t vMic	= 0x0000;
	norace uint16_t vBat	= 0x0000;
	norace uint16_t error	= 0x0000;
	
	static void listo() {
		call AttrUse.getAttrValue("mag_x", &vMagX, &error);
		call AttrUse.getAttrValue("mag_y", &vMagY, &error);
		signal Lecturas.leerListo();
	}
	
	command result_t StdControl.init() {
		call TempControl.init();
		call LuzControl.init();
		call MicControl.init();
		call Mic.muxSel(1);
		call Mic.gainAdjust(64);
		call BatControl.init();
		return SUCCESS;
	}
	
	command result_t StdControl.start() {
		call AttrUse.startAttr((call AttrUse.getAttr("mag_x")) -> id);
		call AttrUse.startAttr((call AttrUse.getAttr("mag_y")) -> id);
		return SUCCESS;
	}
	
	command result_t StdControl.stop() {
		return SUCCESS;
	}
	
	default async event result_t Lecturas.leerListo() {
		return SUCCESS;
	}

	async command result_t Lecturas.leer() {
		call TempControl.start();
		call TempTimer.start(TIMER_ONE_SHOT, 10);
		return SUCCESS;
	}
	
	event result_t TempTimer.fired() {
		call Temp.getData();
		return SUCCESS;
	}

	async event result_t Temp.dataReady(uint16_t data) {
		atomic vTemp = data;
		call TempControl.stop();
		call LuzControl.start();
		call Luz.getData();
		return SUCCESS;
	}
	
	event async result_t Luz.dataReady(uint16_t data) {
		atomic vLuz = data;
		call LuzControl.stop();
		call AcelControl.start();
		call AcelX.getData();
		return SUCCESS;
	}
	
	event async result_t AcelX.dataReady(uint16_t data) {
		atomic vAcelX = data;
		call AcelY.getData();
		return SUCCESS;
	}
	
	event async result_t AcelY.dataReady(uint16_t data) {
		atomic vAcelY = data;
		call AcelControl.stop();
		call MicControl.start();
		call MicADC.getData();
		return SUCCESS;
	}
	
	event async result_t MicADC.dataReady(uint16_t data) {
		atomic vMic	 = data;
		call BatControl.start();
		call Bat.getData();
		return SUCCESS;
	}
	
	event async result_t Bat.dataReady(uint16_t data) {
		atomic vBat = data;
		call BatControl.stop();
		listo();
		return SUCCESS;
	}
	
	async command uint16_t Lecturas.leerLect(uint8_t id) {
		if (id == 1)	return vTemp;
		if (id == 2)	return vLuz;
		if (id == 3)	return vMagX;
		if (id == 4)	return vMagY;
		if (id == 5)	return vAcelX;
		if (id == 6)	return vAcelY;
		if (id == 7)	return vMic;
		if (id == 8)	return vBat;
		return 0;
	}
	
	event result_t AttrUse.getAttrDone(char *name, char *resultBuf, SchemaErrorNo errorNo)  {
		return SUCCESS;
	}

	event result_t AttrUse.startAttrDone(uint8_t id) {
		return SUCCESS;
	}
}
