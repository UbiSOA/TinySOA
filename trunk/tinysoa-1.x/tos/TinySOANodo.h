// $Id: TinySOANodo.h,v 0.3 2006/03/23 12:00:00 avilesl Exp $

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

/*******************************************************************************
 * Sensores
 ******************************************************************************/

//#define SENSOR_BOARD_ID 0x01	// MDA500
//#define SENSOR_BOARD_ID 0x02	// MTS510
//#define SENSOR_BOARD_ID 0x03	// MEP500
//#define SENSOR_BOARD_ID 0x80	// MDA400
//#define SENSOR_BOARD_ID 0x81	// MDA300
//#define SENSOR_BOARD_ID 0x82	// MTS101
//#define SENSOR_BOARD_ID 0x83	// MTS300
#define SENSOR_BOARD_ID 0x84	// MTS310
//#define SENSOR_BOARD_ID 0x85	// MTS400
//#define SENSOR_BOARD_ID 0x86	// MTS420
//#define SENSOR_BOARD_ID 0x87	// MEP401
//#define SENSOR_BOARD_ID 0x90	// MDA320
//#define SENSOR_BOARD_ID 0xA0	// MSP410
//#define SENSOR_BOARD_ID 0xE1	// TMSKY1

/*******************************************************************************
 * Opciones
 ******************************************************************************/
 
enum {
	LEDS_DEBUG	= TRUE,
	REP_PUBLIC	= 50
};

/*******************************************************************************
 * Constantes y Estructuras Generales
 ******************************************************************************/

enum {
	DATA_RATE_INICIAL	= 5000,
	DATA_RATE_ACTIVO	= 2000,
	DATA_RATE_ESTABLE	= 10000
};

enum {
	TINYSOA_TIPO_LECTURA,
	TINYSOA_TIPO_REGISTRO,
	TINYSOA_TIPO_ACTIVA_ACTUADOR,
	TINYSOA_TIPO_DESACTIVA_ACTUADOR,
	TINYSOA_TIPO_DUERME,
	TINYSOA_TIPO_DESPIERTA,
	TINYSOA_TIPO_CAMBIA_DATA_RATE,
	TINYSOA_TIPO_SOLICITUD_REGISTRO,
	TINYSOA_TIPO_SUSCRIBIR
};

enum {
	TINYSOA_SENSOR_TEMP	= 0xB001,
	TINYSOA_SENSOR_LUZ,
	TINYSOA_SENSOR_MAGX,
	TINYSOA_SENSOR_MAGY,
	TINYSOA_SENSOR_ACEX,
	TINYSOA_SENSOR_ACEY,
	TINYSOA_SENSOR_MIC,
	TINYSOA_SENSOR_VOLT
};

enum {
	TINYSOA_ACTUADOR_BOCINA			= 0xA001,
	TINYSOA_ACTUADOR_LED_ROJO		= 0xA002,
	TINYSOA_ACTUADOR_LED_AMARILLO	= 0xA003,
	TINYSOA_ACTUADOR_LED_AZUL		= 0xA003,
	TINYSOA_ACTUADOR_LED_VERDE		= 0xA004
};

typedef struct TinySOAMsg {
	uint16_t l1;
	uint16_t l2;
	uint16_t l3;
	uint16_t l4;
	uint16_t l5;
	uint16_t l6;
	uint16_t l7;
	uint16_t l8;
	uint8_t id;
	uint8_t padre;
	uint8_t nsec;
	uint8_t	sensor;
	uint8_t	tipo;
} TinySOAMsg;

typedef struct TinySOACmdMsg {
	uint16_t datos;
	uint8_t tipo;
	uint8_t id;
} TinySOACmdMsg;

enum {
	AM_TINYSOAMSG	= 24,
	AM_TINYSOACMDMSG
};
