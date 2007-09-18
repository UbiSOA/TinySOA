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

package net.tinyos.tinysoa.util;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;

import net.tinyos.tinysoa.comun.*;

/*******************************************************************************
 * Clase utilería para generar gráficas de información de sensado. 
 * 
 * @author		Edgardo Avilés López
 * @version	0.5, 02/25/2006
 ******************************************************************************/
public class Graficador extends JPanel {
	private static final long serialVersionUID = -2891710764794158927L;

	private Vector<Vector> datos;
	private double margIzq = 52, margSup = 20, margDer = 20, margInf = 62,
		ancho, alto, sepHor = 12, sepVer = 20;
	private double x, y;
	private Color colorLineasFondo = new Color(0xcccccc),
		colorLineas = new Color(0x000000);
	private double max, min, difMaxMin;
	private Date actual, anterior;
	private long dif = 1000 * 30;
	private int ALINEACION_IZQUIERDA = 0, ALINEACION_CENTRO = 1,
		ALINEACION_DERECHA = 2;
	private Font fuenteEjeVer = new Font("Arial", Font.PLAIN, 10),
		fuenteEjeHor = new Font("Arial", Font.BOLD, 9),
		fuenteLeyenda = new Font("Arial", Font.PLAIN, 11);
	private Color[] colores = new Color[]{ new Color(0x000000),
			new Color(0x9bbdde), new Color(0xffbc46), new Color(0xa2c488),
			new Color(0xd6b9db), new Color(0xcdc785), new Color(0xdea19b),
			new Color(0xb9dbcf), new Color(0xffdc46), new Color(0x9b88c4),
			new Color(0x85cd9b), new Color(0xd8d7c8), new Color(0x97968c) };
	private JProgressBar progreso = null;
	private String archUrl, archTipo;
	private BufferedImage buffer;
	
	/***************************************************************************
	 * Constructor principal del graficador.
	 **************************************************************************/
	public Graficador() {
		this.setOpaque(true);
		this.setBackground(Color.WHITE);
	}
	
	/***************************************************************************
	 * Define el vector de datos a graficar.
	 * 
	 * @param datos	Vector de datos a graficar
	 **************************************************************************/
	public void defDatos(Vector<Vector> datos) {
		this.datos = datos;
		
		max = 0;
		min = 100000;
		
		Enumeration e = datos.elements();
		while (e.hasMoreElements()) {
			Enumeration e2 = ((Vector)e.nextElement()).elements();
			while (e2.hasMoreElements()) {
				Lectura d = (Lectura)(e2.nextElement());
				double valor = Double.parseDouble(d.getValor());
				if (valor > max) max = valor;
				if (valor < min) min = valor;
			}
		}
	
		difMaxMin = (max - min) * 0.2d;
		if (difMaxMin < 0.5) difMaxMin = (max - min) * 0.5d;
		if (difMaxMin == 0) difMaxMin = 0.2d;
		if (max > 0) max = max + difMaxMin;
		if (min > 0) min = min - difMaxMin;
		if (min > max) { max = 0; min = 0; }
	}

	/***************************************************************************
	 * Define el tiempo final de la gráfica.
	 * 
	 * @param tiempo	Tiempo final
	 **************************************************************************/
	public void defTiempo(long tiempo) {
		defTiempo(tiempo, dif);
	}

	/***************************************************************************
	 * Define el tiempo final de la gráfica y la diferencia con el tiempo
	 * inicial.
	 * 
	 * @param tiempo	Tiempo final
	 * @param dif		Diferencia deseada con tiempo inicial
	 **************************************************************************/
	public void defTiempo(long tiempo, long dif) {
		this.actual = new Date(tiempo);
		this.dif = dif;
		anterior = new Date(actual.getTime() - dif);
	}

	/***************************************************************************
	 * Define la diferencia entre el tiempo inicial y final.
	 * 
	 * @param dif	Diferencia deseada entre el rango de tiempos graficados
	 **************************************************************************/
	public void defDif(long dif) {
		this.dif = dif;
		anterior = new Date(actual.getTime() - dif);
	}

	/***************************************************************************
	 * Obtiene la diferencia entre el tiempo inicial y final.
	 * 
	 * @return	Diferencia entre el rango de tiempo
	 **************************************************************************/
	public long obtDif() {
		return dif;
	}

	/***************************************************************************
	 * Define la barra de progreso a ser utilizada para mostrar el estado
	 * actual del guardado de imagen.
	 * 
	 * @param progreso	Barra de progreso a utilizar
	 **************************************************************************/
	public void defProgreso(JProgressBar progreso) {
		this.progreso = progreso;
	}

	/***************************************************************************
	 * Genera y guarda una imagen del estado actual del graficador.
	 * 
	 * @param archivo	URL del archivo a generar y guardar
	 * @param tipo		Tipo de imagen deseada (jpg, gif, png ó bmp)
	 **************************************************************************/
	public void guardarImagen(String archivo, String tipo) {
		if (datos == null) return;
			
		this.archUrl = archivo;
		this.archTipo = tipo;
			
		new Thread() {
			public void run() {
				ancho = 800;
				alto = 400;
				
				if (progreso != null) {
					progreso.setString("Exportando...");
					progreso.setVisible(true);
				}
	
				BufferedImage buffer = new BufferedImage(
						(int)Math.round(ancho), (int)Math.round(alto),
						BufferedImage.TYPE_INT_RGB);
	
				Graphics2D g2db = buffer.createGraphics();
				g2db.setColor(Color.WHITE);
				g2db.fillRect(0, 0, (int)Math.round(ancho),
						(int)Math.round(alto));
				dibujarFondo(g2db);
				dibujarEjes(g2db);
				dibujarEtiquetasEjes(g2db);
				dibujarLeyenda(g2db);
				dibujarDatos(g2db);
				g2db.dispose();
	
				try {
					File arch = new File(archUrl);
					ImageIO.write(buffer, archTipo, arch);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null,
							"Error al guardar la imagen " + archUrl,
							"Error al guardar", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
				
				if (progreso != null) {
					progreso.setString("Cargando...");
					progreso.setVisible(false);
				}
			}
		}.start();
	}

	/***************************************************************************
	 * Dibuja la gráfica con la información y el estado actual.
	 * 
	 * @param g	Objeto de gráficos a utilizar
	 **************************************************************************/
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		super.paint(g2d);
		if (datos == null) return;
		
		ancho = getWidth();
		alto = getHeight();
		x = y = 0.0d;
		
		buffer = g2d.getDeviceConfiguration().createCompatibleImage(
				getWidth(), getHeight(), Transparency.TRANSLUCENT);
		
		Graphics2D g2db = buffer.createGraphics();
		dibujarFondo(g2db);
		dibujarEjes(g2db);
		dibujarEtiquetasEjes(g2db);
		dibujarLeyenda(g2db);
		dibujarDatos(g2db);
		g2db.dispose();
		
		g2d.drawImage(buffer, null, 0, 0);
	}

	/***************************************************************************
	 * Dibuja el fondo de la gráfica.
	 * 
	 * @param g2d	Gráfico a utilizar
	 **************************************************************************/
	private void dibujarFondo(Graphics2D g2d) {
		// Dibuja las líneas puntadas verticales menores -----------------------
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		
		g2d.setColor(colorLineasFondo);
		g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 0.0f, new float[]{6.0f, 4.0f}, 0.0f));
		
		x = margIzq;
		for (int i = 0; i < sepHor; i++) {
			x += (ancho - margIzq - margDer) / sepHor;
			dibujarLinea(g2d, x, margSup, x, alto - margInf);
		}
		
		// Dibuja el rectángulo exterior ---------------------------------------
		
		g2d.setStroke(new BasicStroke(1.0f));
		dibujarRectangulo(g2d, margIzq, margSup, ancho - margIzq - margDer,
				alto - margSup - margInf);
		
		// Dibuja las líneas horizontales menores ------------------------------
		
		y = margSup;
		for (int i = 0; i < sepVer; i++) {
			y += (alto - margSup - margInf) / sepVer;
			dibujarLinea(g2d, margIzq, y, ancho - margDer, y);
		}		
	}
	
	/***************************************************************************
	 * Dibuja las líneas de los ejes
	 * 
	 * @param g2d	Gráfico a utilizar
	 **************************************************************************/
	private void dibujarEjes(Graphics2D g2d) {
		// Dibuja el eje vertical ----------------------------------------------
		
		g2d.setColor(colorLineas);
		g2d.setStroke(new BasicStroke(2.0f));
		
		dibujarLinea(g2d, margIzq, margSup, margIzq, alto - margInf);
		
		// Dibuja las separaciones menores del eje vertical --------------------
		
		y = margSup;
		for (int i = 0; i < sepVer; i++) {
			y += (alto - margSup - margInf) / sepVer;
			if ((i + 1) % (sepVer / 4) != 0)
				dibujarLinea(g2d, margIzq, y, margIzq - 6, y);
		}
		
		// Dibuja las separaciones mayores del eje vertical --------------------
		
		y = margSup;
		for (int i = 0; i <= 4; i++) {
			dibujarLinea(g2d, margIzq, y, margIzq - 12, y);
			y += (alto - margSup - margInf) / 4;
		}
		
		// Dibuja el eje horizontal --------------------------------------------
		
		dibujarLinea(g2d, margIzq, alto - margInf, ancho - margDer,
				alto - margInf);
		
		// Dibuja las separaciones mayores del eje horizontal ------------------
		
		x = margIzq;
		for (int i = 0; i <= sepHor; i++) {
			dibujarLinea(g2d, x, alto - margInf, x, alto - margInf + 4);
			x += (ancho - margIzq - margDer) / 4;
		}
	}
	
	/***************************************************************************
	 * Dibuja las etiquetas de los ejes.
	 * 
	 * @param g2d	Gráfico a utilizar
	 **************************************************************************/
	private void dibujarEtiquetasEjes(Graphics2D g2d) {
		// Dibuja las etiquetas del eje vertical -------------------------------
		
		if (actual == null) return;
		
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		NumberFormat nf;
		if (difMaxMin > 2) nf = new DecimalFormat("0");
		else nf = new DecimalFormat("0.0");
		
		y = margSup;
		for (int i = 0; i <= 4; i++) {
			dibujarTexto(g2d, fuenteEjeVer,
					nf.format(min * (i / 4.0d) + max * (1.0d - i / 4.0d)),
					margIzq - 16, y, this.ALINEACION_DERECHA);
			y += (alto - margSup - margInf) / 4;
		}
		
		// Dibuja las etiquetas del eje horizontal -----------------------------
		
		x = margIzq;
		for (int i = 0; i <= 4; i++) {
			Date t = new Date((long)(actual.getTime() * (i / 4.0d) +
					anterior.getTime() * (1.0d - i / 4.0d)));
			String c = new SimpleDateFormat("EEE d/M").format(t);
			String c2 = new SimpleDateFormat("HH:mm:ss").format(t);
			c = c.substring(0, 1).toUpperCase() + c.substring(1);
			int al = this.ALINEACION_CENTRO;
			if (i == 0) al = this.ALINEACION_IZQUIERDA;
			if (i == 4) al = this.ALINEACION_DERECHA;
			dibujarTexto(g2d, fuenteEjeHor, c, x, alto - margInf + 12, al);
			dibujarTexto(g2d, new Font(fuenteEjeHor.getFamily(), Font.PLAIN,
					fuenteEjeHor.getSize()), c2, x, alto - margInf + 22, al);
			x += (ancho - margIzq - margDer) / 4;
		}
	}

	/***************************************************************************
	 * Dibuja los datos de la gráfica.
	 * 
	 * @param g2d	Gráfica a utilizar
	 **************************************************************************/
	private void dibujarDatos(Graphics2D g2d) {
		DateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setClip((int)Math.round(margIzq) + 1, (int)Math.round(margSup),
				(int)Math.round(ancho - margDer),
				(int)Math.round(alto - margInf) - 1);

		Enumeration e = datos.elements();
		while (e.hasMoreElements()) {
			GeneralPath path = new GeneralPath();
			
			boolean iniciado = false;
			Vector lecturas = (Vector)e.nextElement();
			
			Enumeration e2 = lecturas.elements();
			while (e2.hasMoreElements()) {
				Lectura dato = (Lectura)e2.nextElement();
				
				double x, y;
				try {
					x = interpolar(0, g2d.getClip().getBounds().width,
							anterior.getTime(), actual.getTime(),
							((Date)formato.parse(dato.getTiempo())).getTime());
					y = interpolar(g2d.getClip().getBounds().height, 0, min,
							max, Double.parseDouble(dato.getValor()));
				} catch (Exception ex) { ex.printStackTrace(); return; }

				if (!iniciado) {
					path.moveTo((float)x, (float)y);
					g2d.setColor(colores[dato.getNid() % colores.length]);
					iniciado = true;
				}				
				path.lineTo((float)x, (float)y);
				if (x < 0) break;
			}			
			g2d.draw(path);
			
			if (dif < 1000 * 60) {
			
			e2 = lecturas.elements();
			while (e2.hasMoreElements()) {
				Lectura dato = (Lectura)e2.nextElement();
				double x, y;
				try {
					x = interpolar(0, g2d.getClip().getBounds().width,
							anterior.getTime(), actual.getTime(),
							((Date)formato.parse(dato.getTiempo())).getTime());
					y = interpolar(g2d.getClip().getBounds().height, 0, min,
							max, Double.parseDouble(dato.getValor()));
				} catch (Exception ex) { ex.printStackTrace(); return; }
				dibujarPunto(g2d, x, y);
				if (x < 0) break;
			}
			}

		}
		
	}
	
	/***************************************************************************
	 * Dibuja una línea con las coordenadas especificadas.
	 * 
	 * @param g2d	Gráfica a utilizar
	 * @param x1	Valor X del punto inicial
	 * @param y1	Valor Y del punto inicial
	 * @param x2	Valor X del punto final
	 * @param y2	Valor Y del punto final
	 **************************************************************************/
	private void dibujarLinea(Graphics2D g2d, double x1, double y1,
			double x2, double y2) {
		g2d.drawLine((int)Math.round(x1), (int)Math.round(y1),
				(int)Math.round(x2), (int)Math.round(y2));
	}
	
	/***************************************************************************
	 * Dibuja un rectángulo de un ancho y alto determinados en las coordenadas
	 * indicadas.
	 * 
	 * @param g2d		Gráfica a utilizar
	 * @param x		Valor X de la posición
	 * @param y		Valor Y de la posición
	 * @param ancho	Ancho deseado
	 * @param alto		Alto deseado
	 **************************************************************************/
	private void dibujarRectangulo(Graphics2D g2d, double x, double y,
			double ancho, double alto) {
		g2d.drawRect((int)Math.round(x), (int)Math.round(y),
				(int)Math.round(ancho), (int)Math.round(alto));		
	}
	
	/***************************************************************************
	 * Dibuja una cadena de texto en una posición indicada con un tipo de letra
	 * y alineación especificados.
	 * 
	 * @param g2d			Gráfica a utilizar
	 * @param fuente		Tipo de letra a utilizar
	 * @param texto		Texto a graficar
	 * @param x			Valor X de la posición
	 * @param y			Valor Y de la posición
	 * @param alineacion	Alineación del texto
	 **************************************************************************/
	private void dibujarTexto(Graphics2D g2d, Font fuente, String texto,
			double x, double y, int alineacion) {
		TextLayout tl = new TextLayout(texto, fuente,
				g2d.getFontRenderContext());
		if (alineacion == ALINEACION_IZQUIERDA)
			tl.draw(g2d, (float)(x),
					(float)(y - tl.getBounds().getCenterY()));			
		if (alineacion == ALINEACION_CENTRO)
			tl.draw(g2d, (float)(x - tl.getBounds().getCenterX()),
					(float)(y - tl.getBounds().getCenterY()));
		if (alineacion == ALINEACION_DERECHA)
			tl.draw(g2d, (float)(x - tl.getBounds().getWidth()),
					(float)(y - tl.getBounds().getCenterY()));
	}
	
	/***************************************************************************
	 * Devuelve el ancho de una cadena de texto con el tipo de letra indicado.
	 * 
	 * @param g2d		Gráfica a utilizar
	 * @param fuente	Tipo de letra a utilizar
	 * @param texto	Texto a medir
	 * @return			El ancho del texto
	 **************************************************************************/
	private double anchoTexto(Graphics2D g2d, Font fuente, String texto) {
		TextLayout tl = new TextLayout(texto, fuente,
				g2d.getFontRenderContext());
		return tl.getBounds().getWidth();
	}
	
	/***************************************************************************
	 * Dibuja un círculo relleno en la posición y con el diámetro indicados.
	 * 
	 * @param g2d		Gráfica a utilizar
	 * @param x		Valor X de la posición
	 * @param y		Valor Y de la posición
	 * @param diametro	Diámetro del círculo
	 **************************************************************************/
	private void dibujarCirculoLleno(Graphics2D g2d, double x, double y,
			double diametro) {
		g2d.fillOval((int)Math.round(x - diametro / 2.0d),
				(int)Math.round(y - diametro / 2.0d),
				(int)Math.round(diametro), (int)Math.round(diametro));
	}
	
	/***************************************************************************
	 * Aclara el color especificado en un 25%.
	 * 
	 * @param color	Color a aclarar
	 * @return			El color aclarado en un 25%
	 **************************************************************************/
	private Color aclararColor(Color color) {
		int rojo, verde, azul;
		rojo = (int)Math.round(color.getRed() + color.getRed() * 0.15);
		verde = (int)Math.round(color.getGreen() + color.getGreen() * 0.15);
		azul = (int)Math.round(color.getBlue() + color.getBlue() * 0.15);
		if (rojo > 255) rojo = 255;
		if (verde > 255) verde = 255;
		if (azul > 255) azul = 255;
		return new Color(rojo, verde, azul);
	}
	
	/***************************************************************************
	 * Dibuja un punto de vértice en la posición indicada.
	 * 
	 * @param g2d	Gráfica a utilizar
	 * @param x	Valor X de la posición
	 * @param y	Valor Y de la posición
	 **************************************************************************/
	private void dibujarPunto(Graphics2D g2d, double x, double y) {
		Color c1 = g2d.getColor();
		Color c = g2d.getColor();
		
		float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(),
				c.getBlue(), null);
		int nc = Color.HSBtoRGB(hsb[0], hsb[1] - hsb[1] * 0.30f,
				hsb[2] - hsb[2] * 0.15f);
		c = new Color((nc>>16)&0xFF, (nc>>8)&0xFF, nc&0xFF);
		
		g2d.setColor(c);
		dibujarCirculoLleno(g2d, x, y, 9);
		g2d.setColor(aclararColor(c));
		dibujarCirculoLleno(g2d, x, y, 7);
		
		g2d.setColor(aclararColor(aclararColor(c)));
		dibujarCirculoLleno(g2d, x - 1, y - 1, 5);
		g2d.setColor(aclararColor(aclararColor(aclararColor(c))));
		dibujarCirculoLleno(g2d, x - 1, y - 1, 2);
		g2d.setColor(aclararColor(aclararColor(aclararColor(aclararColor(c)))));
		g2d.setColor(c1);
	}
	
	/***************************************************************************
	 * Dibuja una leyenda con los nodos que conforman el gráfico.
	 * 
	 * @param g2d	Gráfica a utilizar
	 **************************************************************************/
	private void dibujarLeyenda(Graphics2D g2d) {
		if (datos == null) return;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		double anchoLeyenda = 0;
		
		Enumeration e = datos.elements();
		while (e.hasMoreElements()) {
			Vector nodo = (Vector)e.nextElement();
			if (nodo.size() > 0) anchoLeyenda += 14 + 42;
		}
		
		e = datos.elements();
		double x = (ancho - anchoLeyenda) / 2.0d;
		double y = alto - margInf + 43;
		while (e.hasMoreElements()) {
			Vector nodo = (Vector)e.nextElement();
			if (nodo.size() > 0) {
				int nid = ((Lectura)nodo.get(0)).getNid();
				g2d.setColor(colores[nid % colores.length]);
				dibujarPunto(g2d, x + 6, y);
				x += 9 + 5;
				g2d.setColor(Color.DARK_GRAY);
				dibujarTexto(g2d, fuenteLeyenda, "Nodo " + nid, x, y,
						this.ALINEACION_IZQUIERDA);
				x += anchoTexto(g2d, fuenteLeyenda, "Nodo " + nid) + 10;
			}
		}
	}
	
	/***************************************************************************
	 * Teniendo <i>y0</i>=<i>f</i>(<i>x0</i>) y <i>y1</i>=<i>f</i>(<i>x1</i>)
	 * esta devuelve el valor correspondiente a la interpolación de <i>x</i>
	 * con la línea creada por las coordenadas (<i>x0</i>,<i>y0</i>) y
	 * (<i>x1</i>,<i>y1</i>).
	 * 
	 * @param y0	Resultado inicial
	 * @param y1	Resultado final
	 * @param x0	Valor inicial
	 * @param x1	Valor final
	 * @param x	Valor a interpolar
	 * @return		El resultado de la interpolación
	 **************************************************************************/
	private double interpolar(double y0, double y1, double x0,
			double x1, double x) {
		double m = (y0 - y1) / (x0 - x1);
		double b = (x1 * y0 - x0 * y1) / (x1 - x0);
		return m * x + b;
	}
	
}
