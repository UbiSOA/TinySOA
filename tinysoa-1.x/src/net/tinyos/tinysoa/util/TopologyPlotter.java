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
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import java.text.*;
import javax.imageio.*;
import javax.swing.*;

/*******************************************************************************
 * Clase utilería para generar gráficas de topología con la información de
 * sensado. 
 * 
 * @author		Edgardo Avilés López
 * @version	0.5, 02/25/2006
 ******************************************************************************/
public class TopologyPlotter extends JPanel
	implements DragGestureListener, DragSourceListener, DropTargetListener {
	private static final long serialVersionUID = 9143159172653372529L;
	
	private double valueMin = -10.0d;	// Valor mínimo
	private double valueMax = 40.0d;	// Valor máximo
	private double valuePro = 0.0d;	// Valor promedio
	
	private int tamPix	= 10;		// Tamaño del pixelado de la gráfica
	private int radio	= 1000;		// Radio de alcance del valor de un nodo
	private int movNod	= -1;		// Índice del nodo en arrastre
	private int diaNod = 20;		// Diámetro del ícono del nodo
	
	private BufferedImage buffer, fondo = null;		// Buffer de la imagen
	private int movX, movY;		// Posición del nodo en arrastre
	private DragSource fuenArr;	// Fuente de arrastre
	private DropTarget destArr;	// Destino de arrastre
	private int tipoEscala = ESCALA_CALOR;
	private Point[] posTopologiaNodos;
	private JProgressBar progreso = null;
	private String archUrl, archTipo;
	
	public static int ESCALA_CALOR = 0, ESCALA_LUZ = 1, ESCALA_ENERGIA = 2;
	
	private Vector<NodeTopologyChart> node;
	
	/***************************************************************************
	 * Constructor principal de la clase.
	 **************************************************************************/
	public TopologyPlotter() {
		super();
		setBackground(Color.WHITE);
		node = new Vector<NodeTopologyChart>();		
		fuenArr = new DragSource();
		fuenArr.createDefaultDragGestureRecognizer(
				this, DnDConstants.ACTION_COPY_OR_MOVE, this);
		destArr = new DropTarget(this, this);
	}

	/***************************************************************************
	 * Define los valores mínimo y máximo de la escala.
	 * 
	 * @param valueMin	Valor mínimo de la escala
	 * @param valueMax	Valor máximo de la escala
	 **************************************************************************/
	public void defEscala(double valueMin, double valueMax) {
		this.valueMin = valueMin;
		this.valueMax = valueMax;
	}

	/***************************************************************************
	 * Devuelve el valor mínimo de la escala utilizada.
	 * 
	 * @return	Valor mínimo de la escala
	 **************************************************************************/
	public double obtMinimum() {
		return valueMin;
	}
	
	/***************************************************************************
	 * Devuelve el valor máximo de la escala utilizada.
	 * 
	 * @return	Valor máximo de la escala
	 **************************************************************************/
	public double obtMaximum() {
		return valueMax;
	}
	
	/***************************************************************************
	 * Define el tipo de escala a utilizar.
	 * 
	 * @param tipoEscala	Tipo de escala
	 **************************************************************************/
	public void defTipoEscala(int tipoEscala) {
		this.tipoEscala = tipoEscala;
	}

	/***************************************************************************
	 * Define el arreglo de puntos donde se especifica la coordenada de cada
	 * uno de los nodos en el gráfico.
	 * 
	 * @param posTopologiaNodos	Posición de los nodos en el gráfico
	 **************************************************************************/
	public void defPosNodos(Point[] posTopologiaNodos) {
		this.posTopologiaNodos = posTopologiaNodos;
	}
	
	/***************************************************************************
	 * Define la imagen de fondo a utilizar en el gráfico. La imagen es
	 * redimensionada al tamaño del gráfico.
	 * 
	 * @param fondo	Imágen de fondo
	 **************************************************************************/
	public void defFondo(BufferedImage fondo) {
		AffineTransform tx = new AffineTransform();
		double s;
		if (getWidth() >= getHeight())
			s = (double)getWidth() / fondo.getWidth();
		else s = (double)getHeight() / fondo.getHeight();
		System.out.println(s);
	    tx.scale(s, s);
	    AffineTransformOp op = new AffineTransformOp(
	    		tx, AffineTransformOp.TYPE_BILINEAR);
		this.fondo = op.filter(fondo, null);
	}
	
	/***************************************************************************
	 * Define la barra de progreso a utilizar.
	 * 
	 * @param progress	Barra de progreso
	 **************************************************************************/
	public void defProgress(JProgressBar progress) {
		this.progreso = progress;
	}

	/***************************************************************************
	 * Método principal de dibujo del componente.
	 * 
	 * @param g	Gráfico a utilizar
	 **************************************************************************/
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
	
		if (node.size() == 0) {
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, getWidth(), getHeight());
			return;
		}
		
		if (movNod == -1) {
			buffer = g2.getDeviceConfiguration().createCompatibleImage(
					getWidth(), getHeight(), Transparency.TRANSLUCENT);
			drawImage();
			g2.drawImage(buffer, null, 0, 0);
		}
		else dibujarArrastre(g2);
	}

	/***************************************************************************
	 * Dibuja la imagen de la topología en el buffer.
	 **************************************************************************/
	private void drawImage() {
		Graphics2D g2d = buffer.createGraphics();
		g2d.setComposite(AlphaComposite.Src);
		
		drawBackground(g2d);
		if (node != null) {
			drawImgBackground(g2d);
			drawGradiente(g2d);
			drawNodes(g2d);
		}
		
		g2d.dispose();
	}
	
	/***************************************************************************
	 * Si se está arrastrando un nodo esta grafica una sombra del nodo en
	 * cuestión con su posición actual.
	 * 
	 * @param g2d	Gráfico a utilizar
	 **************************************************************************/
	private void dibujarArrastre(Graphics2D g2d) {
		int x, y, id;
		double v;
		
		g2d.drawImage(buffer, null, 0, 0);
		
		x = movX;
		y = movY;
		id = node.get(movNod).getId();
		v = node.get(movNod).getValue();
		
		drawNode(g2d, id, x, y, v, 0.35f);
	}
	
	/***************************************************************************
	 * Dibuja la imagen de fondo en el centro del gráfico.
	 * 
	 * @param g2d	Gráfico a utilizar
	 **************************************************************************/
	private void drawImgBackground(Graphics2D g2d) {
		if (fondo == null) return;
		int x = (getWidth() - fondo.getWidth()) / 2;
		int y = (getHeight() - fondo.getHeight()) / 2;
		g2d.drawImage(fondo, null, x, y);
	}
	
	/***************************************************************************
	 * Dibuja el fondo de la imagen.
	 * 
	 * @param g2d	Gráfico a utilizar
	 **************************************************************************/
	private void drawBackground(Graphics2D g2d) {
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());
	}

	/***************************************************************************
	 * Dibuja la capa de color gradiente correspondiente a interpolaciones de
	 * la información de sensado.
	 * 
	 * @param g2d	Gráfico a utilizar
	 **************************************************************************/
	private void drawGradiente(Graphics2D g2d) {
		
		double color = 0.0d, distMax, distMen, dist, factor, valor,
				valorMen = valueMax, valorMay = valueMin, sumFact;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

		g2d.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 0.75f));
		
		NodeTopologyChart nodo;
		
		valuePro = 0.0d;
		for (int k = 0; k < node.size(); k++)
			if (node.get(k) != null) {
				valor = node.get(k).getValue();
				if (valor < valorMen) valorMen = valor;
				if (valor > valorMay) valorMay = valor;
				valuePro += valor;
			}
		valuePro /= node.size();
		
		for (int i = 0; i < getWidth(); i = i + tamPix)
			for (int j = 0; j < getHeight(); j = j + tamPix) {

				distMen = 10000.0d;
				distMax = 0.0d;
				for (int k = 0; k < node.size(); k++)
					if (node.get(k) != null) {
						nodo = node.get(k);
						dist = distance(i, j, nodo.getPosition().x,
								nodo.getPosition().y);
						if (dist < distMen) distMen = dist;
						if (dist > distMax) distMax = dist;
					}
				
				sumFact = 0.0d;
				for (int k = 0; k < node.size(); k++)
					if (node.get(k) != null) {
						nodo = node.get(k);
						dist = distance(i, j, nodo.getPosition().x,
								nodo.getPosition().y);
						sumFact += distMen / dist;
					}
					
				color = 0.0d;
				for (int k = 0; k < node.size(); k++)
					if (node.get(k) != null) {
						nodo = node.get(k);
						dist = distance(i, j , nodo.getPosition().x,
								nodo.getPosition().y);
						factor = (distMen / dist) / sumFact;
						valor = nodo.getValue();
						valor = interpolar(valor, valuePro, 1, 0,
								Math.cos(interpolar(
										0, Math.PI / 2, 0, radio, dist)));
						valor = valor * factor;
						color += valor;
				}
				
				color = interpolar(0.0d, 1.0d, valueMin, valueMax, color);
				g2d.setColor(escalaColor(color));
				g2d.fillRect(i, j, tamPix, tamPix);			
			}
	}
	
	/***************************************************************************
	 * Dibuja los nodos en el gráfico.
	 * 
	 * @param g2d	Gráfico a utilizar
	 **************************************************************************/
	private void drawNodes(Graphics2D g2d) {
		
		int x, y, id;
		double v;
		
		for (int i = 0; i < node.size(); i++)
			if (node.get(i) != null) {
				NodeTopologyChart nodo = node.get(i);
				
				x = nodo.getPosition().x;
				y = nodo.getPosition().y;
				id = nodo.getId();
				v = nodo.getValue();
				drawNode(g2d, id, x, y, v, 1.0f);
			}
	}
	
	/***************************************************************************
	 * Dibuja un nodo con las posiciones y el valor datos.
	 * 
	 * @param g2d		Gráfico a utlizar
	 * @param id		ID del nodo
	 * @param x		Valor X de la posición
	 * @param y		Valor Y de la posición
	 * @param v		Valor de la lectura
	 * @param opacidad	Opacidad del dibujo del nodo
	 **************************************************************************/
	private void drawNode(Graphics2D g2d, int id, int x, int y,
			double v, float opacidad) {

		NumberFormat f = new DecimalFormat("0.0");
		String vs = f.format(v);
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);	
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				0.85f * opacidad));
			
		int t = diaNod - 4;
		g2d.setColor(Color.BLACK);
		g2d.fillOval(Math.round(x - t / 2.0f), Math.round(y - t / 2.0f), t, t);

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Arial", Font.BOLD, 11));
		g2d.drawString(id + "", Math.round(x - g2d.getFontMetrics().
				stringWidth(id + "") / 2.0f), Math.round(y + 4));

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				0.25f * opacidad));
		g2d.setColor(Color.BLACK);

		g2d.setFont(new Font("Arial", Font.BOLD, 9));
		g2d.drawString(vs + "", Math.round(x + 1 - g2d.getFontMetrics().
				stringWidth(vs + "") / 2.0f), Math.round(y + 1 + 20 - 4));
			
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				1.0f * opacidad));
		g2d.setColor(Color.WHITE);

		g2d.setFont(new Font("Arial", Font.BOLD, 9));
		g2d.drawString(vs + "", Math.round(x - g2d.getFontMetrics().
				stringWidth(vs + "") / 2.0f), Math.round(y + 20 - 4));
	}
	
	/***************************************************************************
	 * Calcula la distancia entre las dos coordenadas dadas.
	 * 
	 * @param x0	Valor X del punto inicial
	 * @param y0	Valor Y del punto inicial
	 * @param x1	Valor X del punto final	
	 * @param y1	Valor Y del punto final
	 * @return		Un doble con la distancia entre los puntos
	 **************************************************************************/
	private double distance(int x0, int y0, int x1, int y1) {
		return Math.sqrt(Math.pow(Math.abs(x1 - x0), 2) +
				Math.pow(Math.abs(y1 - y0), 2));
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

	/***************************************************************************
	 * Siendo <code>i</code> un valor entre <code>0.0</code> y <code>1.0</code>
	 * esta regresa el color correspondiente a la escala utilizada.
	 * 
	 * @param i	Valor a buscar en la escala
	 * @return		Un color correspondiente al valor en la escala
	 **************************************************************************/
	private Color escalaColor(double i) {	
		float f = 0.0f;		
		try {
			
			if (tipoEscala == ESCALA_CALOR) {
			
				if (i < 0.0) return new Color(0.0f, 0.0f, 0.0f);
				if (i > 1.0) return new Color(1.0f, 0.0f, 0.0f);
				if (i <= 0.2) {
					f = (float)(i / 0.2d);
					return new Color(0.0f, 0.0f, f);
				} else if (i <= 0.4) {
					f = (float)((i - 0.2d) / 0.2d);
					return new Color(0.0f, f, 1.0f);
				} else if (i <= 0.6) {
					f = (float)((i - 0.4d) / 0.2d); 
					return new Color(0.0f, 1.0f, 1.0f - f);
				} else if (i <= 0.8) {
					f = (float)((i - 0.6d) / 0.2d); 
					return new Color(f, 1.0f, 0.0f);
				} else {
					f = (float)((i - 0.8d) / 0.2d); 
					return new Color(1.0f, 1.0f - f, 0.0f);
				}
			
			} else if (tipoEscala == ESCALA_ENERGIA) {
				
				if (i < 0.0) return new Color(1.0f, 0.0f, 0.0f);
				if (i > 1.0) return new Color(0.0f, 1.0f, 0.0f);
				if (i <= 0.5d) {
					f = (float)(i / 0.5d);
					return new Color(1.0f, f, 0.0f);
				} else {
					f = (float)((i - 0.5d) / 0.5d);
					return new Color(1.0f - f, 1.0f, 0.0f);
				}
				
				
			} else if (tipoEscala == ESCALA_LUZ) {
				
				if (i < 0.0) return new Color(0.0f, 0.0f, 0.0f);
				if (i > 1.0) return new Color(1.0f, 1.0f, 1.0f);
				f = (float)i;
				return new Color(f, f, f);
				
			} else return new Color(0.0f, 0.0f, 0.0f);
		} catch (IllegalArgumentException e) {
			System.out.println("Error: " + f);
			return new Color(0.0f, 0.0f, 0.0f);
		}
	}

	/***************************************************************************
	 * Genera y guarda una imagen del estado actual del graficador.
	 * 
	 * @param archivo	URL del archivo a generar y guardar
	 * @param tipo		Tipo de imagen deseada (jpg, gif, png ó bmp)
	 **************************************************************************/
	public void saveImage(String archivo, String tipo) {
		if (node == null) return;
			
		this.archUrl = archivo;
		this.archTipo = tipo;
			
		new Thread() {
			public void run() {
				int ancho = getWidth();
				int alto = getHeight();
				
				if (progreso != null) {
					progreso.setString("Exportando...");
					progreso.setVisible(true);
				}
	
				BufferedImage buffer = new BufferedImage(
						(int)Math.round(ancho), (int)Math.round(alto),
						BufferedImage.TYPE_INT_RGB);
	
				Graphics2D g2db = buffer.createGraphics();
				g2db.setComposite(AlphaComposite.Src);
				
				drawBackground(g2db);
				drawImgBackground(g2db);
				drawGradiente(g2db);
				drawNodes(g2db);
				
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
	 * Agrega un nodo al gráfico.
	 * 
	 * @param nodo	Nodo a agregar
	 * @see			NodeTopologyChart
	 **************************************************************************/
	public void addNode(NodeTopologyChart nodo) {
		node.add(nodo);
	}

	/***************************************************************************
	 * Modifica el nodo con el ID especificado.
	 * 
	 * @param id			ID del nodo a reemplazar
	 * @param nuevoValor	Nodo con el cual reemplazar
	 * @see					NodeTopologyChart
	 **************************************************************************/
	public void changeNode(int id, NodeTopologyChart nuevoValor) {
		for (int i = 0; i < node.size(); i++)
			if (node.get(i).getId() == id) {
				node.set(id, nuevoValor);
				return;
			}	
	}

	/***************************************************************************
	 * Busca el ID de nodo en los nodos actuales.
	 * 
	 * @param id	ID del nodo a buscar
	 * @return		Verdadero si el nodo existe en el gráfico
	 **************************************************************************/
	public boolean existNode(int id) {
		for (int i = 0; i < node.size(); i++)
			if (node.get(i).getId() == id)
				return true;
		return false;
	}

	/***************************************************************************
	 * Elimina el nodo con el ID especificado.
	 * 
	 * @param id	ID del nodo a eliminar
	 **************************************************************************/
	public void deleteNode(int id) {
		for (int i = 0; i < node.size(); i++)
			if (node.get(i).getId() == id) {
				node.remove(i);
				return;
			}
	}

	/***************************************************************************
	 * Elimina todos los nodos del gráfico.
	 **************************************************************************/
	public void deleteAllNodes() {
		node.removeAllElements();
	}

	/***************************************************************************
	 * Procedimiento para reconocer e inicializar el arrastre.
	 * 
	 * @param evt	Evento de arrastre
	 **************************************************************************/
	public void dragGestureRecognized(DragGestureEvent evt) {
		int x = evt.getDragOrigin().x;
		int y = evt.getDragOrigin().y;
		double d = 0;
		
		if (!isEnabled()) return;
		
		movNod = -1;
		
		for (int k = 0; k < node.size(); k++)
			if (node.get(k) != null) {
				d = distance(x, y, node.get(k).getPosition().x,
						node.get(k).getPosition().y);
				if (d < diaNod / 2.0d) movNod = k;
			}
		
		if (movNod > -1) {
			Transferable t = new StringSelection(destArr.toString());
			fuenArr.startDrag(evt, DragSource.DefaultMoveDrop, t, this);
		}
	}

	/***************************************************************************
	 * Procedimiento para finalizar el arrastre y definir la nueva posición del
	 * nodo, antes de eso la posición es validada.
	 * 
	 * @param evt	Evento de arrastre
	 **************************************************************************/
	public void dragDropEnd(DragSourceDropEvent evt) {
		int nx = evt.getLocation().x - getLocationOnScreen().x;
		int ny = evt.getLocation().y - getLocationOnScreen().y;
		
		if (movNod > -1) {
			if ((nx >= 0) && (nx <= getWidth()) && (ny >= 0) &&
					(ny <= getHeight())) {
				NodeTopologyChart n = node.get(movNod);
				n.setPosition(nx, ny);
				node.set(movNod, n);
				posTopologiaNodos[n.getId()] = new Point(nx, ny);
			}
			repaint();
			movNod = -1;
		}
	}

	/***************************************************************************
	 * Identifica la posición actual del arrastre y redibuja la pantalla.
	 * 
	 * @param evt	Evento de arrastre
	 **************************************************************************/
	public void dragOver(DropTargetDragEvent evt) {
		movX = evt.getLocation().x;
		movY = evt.getLocation().y;
		this.repaint();
	}

	//--------------------------------------------------------------------------
	//
	//   Procedimientos de interfaces no utilizados.
	//
	//==========================================================================
	
	public void dragEnter(DragSourceDragEvent evt)				{}
	public void dragOver(DragSourceDragEvent evt)				{}
	public void dragExit(DragSourceEvent evt)					{}
	public void dropActionChanged(DragSourceDragEvent evt)		{}
	public void dragEnter(DropTargetDragEvent evt) 			{}
	public void dropActionChanged(DropTargetDragEvent arg0)	{}
	public void dragExit(DropTargetEvent arg0) 				{}
	public void drop(DropTargetDropEvent arg0)					{}
	
}
