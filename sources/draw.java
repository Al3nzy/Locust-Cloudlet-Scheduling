import java.awt.Graphics;
import java.awt.Label;

import javax.swing.JPanel;

public class draw extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int x = 0;
	int y = 0;
	int hight = 0;
	int width = 0;
	Label l = new Label("1");

	public void drawing() {
		repaint();
	}

	public void paintComponent(Graphics g) {
		x = 50;
		y = 50;
		hight = 50;
		width = 50;
		super.paintComponents(g);
		int j = 1;
		// color for the shape must be giving firstly
		// g.setColor(getBackground());
		// g.fillRect(x, y, hight, width); // to fill the reg with the selected color
		// g.setColor(getBackground());

		// g.setColor(getBackground());
		g.drawRect(x, y, hight, width);
		l.setSize(5, 5);
		l.setBounds(x, y, hight, width);
		l.setText(Integer.toString(j));
		j = j + 1;
		x = x + 70;

		g.drawRect(x, y, hight, width);
		l.setSize(5, 5);
		l.setBounds(x, y, hight, width);
		l.setText(Integer.toString(j));
		j = j + 1;
		x = x + 70;

		g.drawRect(x, y, hight, width);
		l.setSize(5, 5);
		l.setBounds(x, y, hight, width);
		l.setText(Integer.toString(j));
		j = j + 1;
		x = x + 70;
		/**
		 * g.drawRect(x, y, hight, width); l.setSize(5, 5); l.setBounds(x, y, hight,
		 * width); l.setText(Integer.toString(j)); j= j+1; x=x+70;
		 * 
		 * g.drawRect(x, y, hight, width); l.setSize(5, 5); l.setBounds(x, y, hight,
		 * width); l.setText(Integer.toString(j)); j= j+1; x=x+70;
		 * 
		 * g.drawRect(x, y, hight, width); l.setSize(5, 5); l.setBounds(x, y, hight,
		 * width); l.setText(Integer.toString(j)); j= j+1; x=x+70;
		 * 
		 * g.drawRect(x, y, hight, width); l.setSize(5, 5); l.setBounds(x, y, hight,
		 * width); l.setText(Integer.toString(j)); j= j+1; x=x+70;
		 * 
		 * g.drawRect(x, y, hight, width); l.setSize(5, 5); l.setBounds(x, y, hight,
		 * width); l.setText(Integer.toString(j)); j= j+1; x=x+70;
		 * 
		 */
		// g.fillOval(x, y, hight, width); // to draw circle
	}
}
