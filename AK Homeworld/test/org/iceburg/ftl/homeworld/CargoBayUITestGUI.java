package org.iceburg.ftl.homeworld;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class CargoBayUITestGUI extends JPanel 
		implements MouseMotionListener {
	
	//coordinate testing for absolute layout
	private JLabel labelx;
    private JLabel labely;
    
	BufferedImage bg  = null;
	ImageIcon img = null;
	

	 @Override
	    public void paintComponent(Graphics g) {
			//bg = ImageIO.read(new File("./resource/SpaceDockSplash.png")); //this guy worked in eclipse but not in jar
			img = new ImageIcon(this.getClass().getResource("resource/CargoBaySplash.png"));
			bg = new BufferedImage(
			img.getIconWidth(),
			img.getIconHeight(),
			BufferedImage.TYPE_INT_RGB);
			Graphics gg = bg.createGraphics();
			img.paintIcon(null, gg, 0,0);
			gg.dispose();
	        g.drawImage(bg, 0, 0, img.getIconWidth(), img.getIconHeight(), this);
	    }
  
    
	public CargoBayUITestGUI() {
		addMouseMotionListener(this);
		setLayout(null);
		setPreferredSize(new Dimension(1280, 720));
		JLabel lblTestLabel = new JLabel("GUI Test Label");
		lblTestLabel.setForeground(Color.WHITE);
		lblTestLabel.setBounds(587, 5, 70, 14);
		
		labelx = new JLabel("x: ");
		labelx.setBounds(680, 200, 60, 14);
		labelx.setForeground(Color.WHITE);
		labely = new JLabel("y: ");
		labely.setBounds(680, 220, 60, 14);
		labely.setForeground(Color.WHITE);

		add(lblTestLabel);
		add(labelx);
		add(labely);
	}
	
	public void mouseDragged(MouseEvent e) {
		
	}
	public void mouseMoved(MouseEvent e) {
       int x = e.getX();
        int y = e.getY();
        labelx.setText("x: " + x);
        labely.setText("y: " + y);
	}
}
