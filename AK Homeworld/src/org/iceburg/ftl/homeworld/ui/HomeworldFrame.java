package org.iceburg.ftl.homeworld.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.blerf.ftl.parser.DataManager;

import org.iceburg.ftl.homeworld.model.ShipSave;
import org.iceburg.ftl.homeworld.resource.ResourceClass;

//This is the frame that runs the panels/subprograms. The main UI
public class HomeworldFrame extends JFrame {
	JTabbedPane tasksPane;
	private JPanel contentPane;
	public SpaceDockUI spaceDock;
	public CargoBayUI cargoBay;
	public ScienceStationUI scienceStation;
	public DryDockUI dryDock;
	JScrollPane cargoBayPane;
	JScrollPane scienceStationPane;
	// JScrollPane spaceDockPane;
	JPanel spaceDockPane;
	JScrollPane dryDockPane;
	public static ShipSave currentShip;
	//cache for storing ship pics
	private HashMap<String, BufferedImage> imageCache;


	/**
	 * Create the frame.
	 */
	public HomeworldFrame(String appName, float appVersion) {
		// added these guys to look more professional/more like Vhati's prof editor
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 720);
		setTitle(appName + " v" + appVersion);
		// TODO ImageIcon
		Image img = (new ImageIcon((new ResourceClass()).getClass().getResource("LogoIcon.png"))).getImage();
		setIconImage(img);
		imageCache = new HashMap<String, BufferedImage>();
		// setIconImage(Toolkit.getDefaultToolkit().getImage((new ResourceClass()).getClass().getResource("Logo Icon.png")));
		tasksPane = new JTabbedPane();
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.add(tasksPane, BorderLayout.CENTER);
		spaceDock = new SpaceDockUI(this);
		spaceDockPane = new SpaceDockScrollPane(spaceDock);
//		 spaceDockPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		 spaceDockPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		 spaceDockPane.setOpaque(false);
//		 spaceDockPane.getViewport().setOpaque(false);
		tasksPane.add("Space Dock", spaceDockPane);
		cargoBay = new CargoBayUI(this);
		cargoBayPane = new JScrollPane(cargoBay);
//		cargoBayPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		cargoBayPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		cargoBayPane.setOpaque(false);
//		cargoBayPane.getViewport().setOpaque(false);
		tasksPane.add("Cargo Bay", cargoBayPane);
		// scienceStation = new ScienceStationUI();
		// scienceStationPane = new JScrollPane(scienceStation);
		//
		// scienceStationPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// scienceStationPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// scienceStationPane.setOpaque(false);
		// scienceStationPane.getViewport().setOpaque(false);
		// tasksPane.add( "Science Station", scienceStationPane);
//		dryDock = new DryDockUI();
//		dryDockPane = new JScrollPane(dryDock);
//		dryDockPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		dryDockPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		dryDockPane.setOpaque(false);
//		dryDockPane.getViewport().setOpaque(false);
//		tasksPane.add("Dry Dock", dryDockPane);
		// use this to refresh tabs on tab change
		tasksPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// System.out.println("Tab: " + tasksPane.getSelectedIndex());
				if (tasksPane.getSelectedComponent() instanceof JScrollPane) {
					JScrollPane js = (JScrollPane) tasksPane.getSelectedComponent();
					if (js == cargoBayPane) {
						// System.out.println("found");
						cargoBay.init();
//						cargoBay.currentShipInit();
						cargoBay.tradeShipInit();
					}
				} else {
					JPanel jp = (JPanel) tasksPane.getSelectedComponent();
					if (jp == spaceDockPane) {
						// System.out.println("found");
						spaceDock.init();
					}
				}
			}
		});
	}
	
	public BufferedImage getResourceImage(String innerPath, boolean scale) {
		// If caching, you can get(innerPath) from a HashMap and return the pre-loaded pic.
		BufferedImage result = imageCache.get(innerPath);
		if (result != null) {
			//return the scaled image if wanted, otherwise return origional
			if (scale){
				return scaleImage(result);
			} else{
				return result;
			}
		} else {
			InputStream in = null;
			try {
				in = DataManager.get().getResourceInputStream(innerPath);
				result = ImageIO.read(in);
				// If caching, put result in the map before scaleing
				imageCache.put(innerPath, result);
				if (scale = true) {
					result = scaleImage(result);
				}
				
				return result; 
			} catch (IOException e) {
				// log.error( "Failed to load resource image ("+ innerPath +")", e );
				e.printStackTrace();
			} finally {
				try {
					if (in != null)
						in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return result;
		}
	}
	private static BufferedImage scaleImage(BufferedImage image) {
		BufferedImage scaledBI = null;
		if (image.getWidth() > 191 || image.getHeight() > 121) {
			int scaledWidth = 0;
			int scaledHeight = 0;
			if (image.getWidth() > image.getHeight()) {
				scaledWidth = 191;
				scaledHeight = (image.getHeight() / (image.getWidth() / 191));
			} else {
				scaledHeight = 121;
				scaledWidth = (image.getWidth() / (image.getHeight() / 121));
			}
			scaledBI = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TRANSLUCENT);
			Graphics2D g = scaledBI.createGraphics();
			g.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
			g.dispose();
			return scaledBI;
		} else {
			return image;
		}
	}
	public static BufferedImage scaleImageBy(BufferedImage image, float scale) {
		BufferedImage scaledBI = null;
			int scaledWidth = (int) (image.getWidth() * scale);
			int scaledHeight = (int) (image.getHeight() * scale);
			scaledBI = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TRANSLUCENT);
			Graphics2D g = scaledBI.createGraphics();
			g.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
			g.dispose();
			return scaledBI;
		
	}
}
