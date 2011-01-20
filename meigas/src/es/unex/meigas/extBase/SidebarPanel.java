package es.unex.meigas.extBase;

import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class SidebarPanel extends JPanel {

	ImageIcon m_Image;
	
	public SidebarPanel(){
		
		super();	
		
		m_Image = new ImageIcon(getClass().getClassLoader()
				.getResource("images/witch.png"));
		
	}
	
	public void paint(Graphics g){
		
		int x =  (this.getWidth() - m_Image.getIconWidth()) / 2;
		int y =  (this.getHeight() - m_Image.getIconHeight()) / 2;
		
		m_Image.paintIcon(this, g, x, y);	
		
	}

}