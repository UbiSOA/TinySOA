// $Id: LecturasC.nc,v 0.2 2006/03/23 12:00:00 avilesl Exp $

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
 
configuration LecturasC {
	provides {
		interface Lecturas;
		interface StdControl;
	}
}
implementation {
	components LecturasM, PhotoTemp, TimerC, AttrMag, Attr, Accel, MicC, VoltageC;
	
	StdControl = LecturasM.StdControl;
	Lecturas = LecturasM.Lecturas;
	
	LecturasM.TempControl -> PhotoTemp.TempStdControl;
	LecturasM.Temp -> PhotoTemp.ExternalTempADC;
	LecturasM.TempTimer -> TimerC.Timer[unique("Timer")];
	
	LecturasM.LuzControl -> PhotoTemp.PhotoStdControl;
	LecturasM.Luz -> PhotoTemp.ExternalPhotoADC;
	
	StdControl = AttrMag;
	LecturasM.AttrUse -> Attr.AttrUse;
	
	LecturasM.AcelControl -> Accel;
	LecturasM.AcelX -> Accel.AccelX;
	LecturasM.AcelY -> Accel.AccelY;
	
	LecturasM.MicControl -> MicC;
	LecturasM.Mic -> MicC;
	LecturasM.MicADC -> MicC;
	
	LecturasM.BatControl -> VoltageC;
	LecturasM.Bat -> VoltageC;	
}
