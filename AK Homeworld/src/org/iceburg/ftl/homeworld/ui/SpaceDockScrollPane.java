package org.iceburg.ftl.homeworld.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.iceburg.ftl.homeworld.resource.ResourceClass;

//THis scroll pane allows Spacedock ui to scroll through ships while keeping the background from scaling with the scroll (getting really tall/distorted)
//I'd like to update it so that the Launch, refresh, and saves folder buttons are static instead of scrolling
public class SpaceDockScrollPane extends JPanel {
	public SpaceDockScrollPane(JPanel panel) {
		setLayout(new BorderLayout(0, 0));
		JScrollPane js = new JScrollPane(panel);
		js.setOpaque(false);
		js.getViewport().setOpaque(false);
		js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		js.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(js);
	}
	@Override
	public void paintComponent(Graphics g) {
		// bg = ImageIO.read(new File("./resource/SpaceDockSplash.png")); //this guy worked in eclipse but not in jar
		ImageIcon img = new ImageIcon((new ResourceClass()).getClass().getResource("SpaceDockSplash.png"));
		BufferedImage bg = new BufferedImage(img.getIconWidth(), img.getIconHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics gg = bg.createGraphics();
		img.paintIcon(null, gg, 0, 0);
		gg.dispose();
		g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
	}
}
