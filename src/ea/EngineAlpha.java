/* Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
 *
 * Copyright (C) 2011  Michael Andonie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ea;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Diese Klasse definiert Versions-Konstanten und sorgt für eine About-Box
 * beim Ausführen der .jar-Datei.
 * 
 * @author Niklas Keller <me@kelunik.com>
 */
@SuppressWarnings("serial")
public class EngineAlpha extends Frame {
	// 10000 => 1.0
	// 10100 => 1.1
	// 10200 => 1.2
	// 20000 => 2.0
	// 30000 => 3.0
	
	public static final int VERSION_CODE = 30000;
	public static final String VERSION_STRING = "v3.0";

	public static final boolean IS_JAR;
	public static final long BUILD_TIME;

	static {
		IS_JAR = isJar();
		BUILD_TIME = IS_JAR ? getBuildTime() / 1000 : System.currentTimeMillis() / 1000;
	}

	private EngineAlphaPromotion promo;
	
	public EngineAlpha() {
		super("Engine Alpha " + VERSION_STRING);
		
		try {
			setIconImage(ImageIO.read(getClass().getResourceAsStream("/ea/assets/favicon.png")));
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				promo.shutdown();

				setVisible(false);
				dispose();
				
				System.exit(0);
			}
		});

		promo = new EngineAlphaPromotion(this);
	}
	
	private class EngineAlphaPromotion extends Canvas implements Runnable {
		private Thread thread;
		private BufferedImage logo;
		private double alpha = 0;
		private boolean loading = true;
		private boolean alive = true;
		private int version_stable = -1;
		private int version_dev = -1;
		
		public EngineAlphaPromotion(EngineAlpha parent) {
			try {
				logo = ImageIO.read(getClass().getResource("/ea/assets/logo.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			setSize(400, 300);
			setPreferredSize(getSize());
			parent.add(this);
			parent.pack();
			
			Dimension screen = getToolkit().getScreenSize();
			parent.setLocation((screen.width-parent.getWidth())/2, (screen.height-parent.getHeight())/2);
			
			parent.setVisible(true);
			
			thread = new Thread(this) {{ setDaemon(true); }};
			thread.start();
			
			new Thread() {{ setDaemon(true); }
				public void run() {
					try {
						String body = getUrlBody("https://raw.githubusercontent.com/engine-alpha/engine-alpha/master/VERSION_STABLE");
						version_stable = Integer.parseInt(body);
					} catch(NumberFormatException e) {
						e.printStackTrace();
					}

					try {
						String body = getUrlBody("https://raw.githubusercontent.com/engine-alpha/engine-alpha/master/VERSION_DEVELOPMENT");
						version_dev = Integer.parseInt(body);
					} catch(NumberFormatException e) {
						e.printStackTrace();
					}
					
					loading = false;
				}
			}.start();
		}

		private String getUrlBody(String uri) {
			// workaround, make sure this is set to false
			// see http://stackoverflow.com/a/14884941/2373138
			System.setProperty("jsse.enableSNIExtension", "false");

			BufferedInputStream bis = null;
			URL url;

			try {
				url = new URL(uri);
				bis = new BufferedInputStream(url.openStream());

				StringBuilder builder = new StringBuilder();
				byte[] data = new byte[1024];
				int read;

				while((read = bis.read(data)) != -1) {
					builder.append(new String(data, 0, read));
				}

				return builder.toString();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(bis != null) {
					try {
						bis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			return null;
		}
		
		public void run() {
			createBufferStrategy(2);
			BufferStrategy bs = getBufferStrategy();
			Graphics2D g = (Graphics2D) bs.getDrawGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			long lastTime, currTime = System.currentTimeMillis();
			
			while(alive) {
				lastTime = currTime;
				currTime = System.currentTimeMillis();
				
				update(currTime - lastTime);
				
				render(g);
				bs.show();
				
				try {
					Thread.sleep(50);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void shutdown() {
			this.alive = false;

			try {
				thread.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void update(long passedTime) {
			alpha += passedTime * .01;
			alpha %= 360;
		}

		public void render(Graphics2D g) {
			g.setFont(new Font("SansSerif", Font.ITALIC, 14));
			FontMetrics fm = g.getFontMetrics();

			g.setColor(new Color(250, 250, 250));
			g.fillRect(0, 0, getWidth(), getHeight());
			
			g.drawImage(logo, (getWidth() - logo.getWidth()) / 2, 40, null);
			
			if(loading) {
				g.setColor(new Color(0,0,0,150));
				g.fillOval((int) (getWidth()/2+8*Math.cos(alpha))-2, (int) (getHeight()-80+8*Math.sin(alpha))-2, 4, 4);
				g.fillOval((int) (getWidth()/2+8*Math.cos(180+alpha))-2, (int) (getHeight()-80+8*Math.sin(180+alpha))-2, 4, 4);
				g.drawLine((int) (getWidth()/2+8*Math.cos(alpha)), (int) (getHeight()-80+8*Math.sin(alpha)),
						(int) (getWidth()/2+8*Math.cos(180+alpha)), (int) (getHeight()-80+8*Math.sin(180+alpha)));
			} else {
				String message;
				Color color = new Color(30,30,30);
				
				if(version_stable == -1) {
					message = "Server für Versionsabgleich nicht erreichbar.";
				}
				
				else if(version_stable == VERSION_CODE) {
					message = "Dies ist die aktuelle Stable-Version.";
					color = new Color(50, 200, 25);
				}
				
				else if(version_stable > VERSION_CODE) {
					message = "Es ist eine neue Version verfügbar!";
					color = new Color(200,50,0);
				}
				
				else if(version_dev == VERSION_CODE) {
					message = "Dies ist die aktuelle Dev-Version.";
					color = new Color(0,100,150);
				}

				else {
					message = "Du verwendest eine unbekannte Version.";
				}
				
				g.setColor(color);
				g.drawString(message, (getWidth() - fm.stringWidth(message)) / 2, getHeight() - 70);
			}

			Date date = new Date(BUILD_TIME * 1000);
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			g.setColor(new Color(100, 100, 100));
			String str = "Build: " + sdf.format(date);
			g.drawString(str, (getWidth() - fm.stringWidth(str)) / 2, getHeight() - 40);
		}
	}
	
	public static void main(String[] args) {
		new EngineAlpha();
	}

	public static boolean isJar() {
		String className = EngineAlpha.class.getName().replace('.', '/');
		String classJar = EngineAlpha.class.getResource("/" + className + ".class").toString();

		return classJar.startsWith("jar:");
	}

	@SuppressWarnings("unused")
	public static String getJarName() {
		String className = EngineAlpha.class.getName().replace('.', '/');
		String classJar = EngineAlpha.class.getResource("/" + className + ".class").toString();

		if(classJar.startsWith("jar:")) {
			String vals[] = classJar.split("/");

			for(String val: vals) {
				if (val.contains("!")) {
					try {
						return java.net.URLDecoder.decode(val.substring(0, val.length() - 1), "UTF-8");
					} catch(Exception e) {
						return null;
					}
				}
			}
		}

		return null;
	}

	public static long getBuildTime() {
		try {
			String uri = EngineAlpha.class.getName().replace('.', '/') + ".class";
			JarURLConnection j = (JarURLConnection) ClassLoader.getSystemResource(uri).openConnection();

			long time = j.getJarFile().getEntry("META-INF/MANIFEST.MF").getTime();
			return time > 0 ? time : System.currentTimeMillis() / 1000;
		} catch (Exception e) {
			return System.currentTimeMillis() / 1000;
		}
	}
}