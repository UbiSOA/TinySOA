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

package net.tinyos.tinysoa.comun;

/*******************************************************************************
 * Constantes globales. Incluye números de identificación de <i>sensor
 * boards</i>, tasas de flujos de datos, tipos de sensores, de actuadores y
 * de mensajes.
 * 
 * @author		Edgardo Avilés López
 * @version	0.2, 07/24/2006
 ******************************************************************************/
public final class Constantes {
	
	// IDs de los sensor boards
	public static int MDA500 = 0x01;
	public static int MDA510 = 0x02;
	public static int MEP500 = 0x03;
	public static int MDA400 = 0x80;
	public static int MDA300 = 0x81;
	public static int MTS101 = 0x82;
	public static int MTS300 = 0x83;
	public static int MTS310 = 0x84;
	public static int MTS400 = 0x85;
	public static int MTS420 = 0x86;
	public static int MEP401 = 0x87;
	public static int MDA320 = 0x90;
	public static int MSP410 = 0xA0;
	public static int TMSKY1 = 0xE1;
	
	// Tasa de flujos de datos
	public static int DATA_RATE_INICIAL	= 2000;
	public static int DATA_RATE_ACTIVO	= 1000;
	public static int DATA_RATE_ESTABLE	= 5000;
	
	// Tipos de mensajes
	public static int TIPO_LECTURA			= 0;
	public static int TIPO_REGISTRO			= 1;
	public static int TIPO_ACTIVA_ACTUADOR	= 2;
	public static int TIPO_DESACTIVA_ACTUADOR	= 3;
	public static int TIPO_DUERME				= 4;
	public static int TIPO_DESPIERTA			= 5;
	public static int TIPO_CAMBIA_DATA_RATE	= 6;
	public static int TIPO_SOLICITUD_REGISTRO	= 7;
	public static int TIPO_SUSCRIBIR			= 8;
	
	// Tipos de sensores
	public static int SENSOR_NULO	= 0xB000;
	public static int SENSOR_TEMP	= 0xB001;
	public static int SENSOR_LUZ	= 0xB002;
	public static int SENSOR_MAGX	= 0xB003;
	public static int SENSOR_MAGY = 0xB004;
	public static int SENSOR_ACEX	= 0xB005;
	public static int SENSOR_ACEY	= 0xB006;
	public static int SENSOR_MIC	= 0xB007;
	public static int SENSOR_VOLT	= 0xB008;
	
	// Tipos de actuadores
	public static int ACTUADOR_BOCINA			= 0xA001;
	public static int ACTUADOR_LED_ROJO		= 0xA002;
	public static int ACTUADOR_LED_AMARILLO	= 0xA003;
	public static int ACTUADOR_LED_AZUL		= 0xA003;
	public static int ACTUADOR_LED_VERDE		= 0xA004;
	
	// Tipos de mensajes
	public static int AM_TINYSOAMSG		= 24;
	public static int AM_TINYSOACMDMSG	= 25;
	
}
