/*
 *  Copyright 2007 Edgardo Avilés López
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
 
package net.tinyos.tinysoa.common;

/*******************************************************************************
 * Global constants. Includes sensor board identification numbers, data rates,
 * sensor types, actuator types, and message types.
 * 
 * @author		Edgardo Avilés López
 * @version	0.2, 07/24/2006
 ******************************************************************************/
public final class Constants {
	
	// Sensor boards identification numbers
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
	
	// Data rates
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
