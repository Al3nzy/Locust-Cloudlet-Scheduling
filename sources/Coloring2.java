import java.awt.Color;
import java.awt.Graphics;

// ^---Provides interfaces and classes for dealing with different types of events fired by AWT components.
import javax.swing.JFrame;
import javax.swing.JPanel;

// ^---Provides a set of "lightweight" (all-Java language) components that, to the maximum degree possible, work the same on all platforms.
public class Coloring2 extends JPanel {
	public Coloring2() {
	}

	int n = 1;
	int j = 1;
	int i = 0;
	int x = 0;
	int y = 50;
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;

// ^---Extends JPanel to use the frame

	public void paintComponent(Graphics g) { // ^---You need a paintComponent method to paint pictures
		x = 0;
		j = 1;
		y = 0;
		super.paintComponent(g); // ^---You need to equip the variable g into super.paintComponent to draw
									// pictures
		this.setBackground(Color.PINK); // ^---This sets the background color
		/**
		 * for (i=0; i<=8; i++){ g.setColor(Color.BLUE); // ^---This sets the color you
		 * want to use g.drawRect(x+50, y, 50, 50); // ^---This draws a rectangle using
		 * the color you selected g.setColor(Color.RED);
		 * g.drawString(Integer.toString(j), x+60, y); x=x+50; j++; }
		 */
		// rect 1
		y = y + 50;
		g.setColor(Color.BLUE); // ^---This sets the color you want to use
		g.drawRect(x + 50, y, 50, 50); // ^---This draws a rectangle using the color you selected
		g.setColor(Color.RED);
		g.drawString(Integer.toString(j), x + 70, y + 30);
		x = x + 200;
		j++;

		// rect2
		g.setColor(Color.BLUE); // ^---This sets the color you want to use
		g.drawRect(x, y, 50, 50); // ^---This draws a rectangle using the color you selected
		g.setColor(Color.RED);
		g.drawString(Integer.toString(j), x + 20, y + 30);
		x = x + 150;
		j++;

		// rect3
		g.setColor(Color.BLUE); // ^---This sets the color you want to use
		g.drawRect(x, y, 50, 50); // ^---This draws a rectangle using the color you selected
		g.setColor(Color.RED);
		g.drawString(Integer.toString(j), x + 20, y + 30);
		x = 70;
		y = y + 150;
		j++;

		// rect 4 second row
		g.setColor(Color.BLUE); // ^---This sets the color you want to use
		g.drawRect(x, y, 50, 50); // ^---This draws a rectangle using the color you selected
		g.setColor(Color.RED);
		g.drawString(Integer.toString(j), x + 20, y + 30);
		x = x + 135;
		j++;

		// rect 5
		g.setColor(Color.BLUE); // ^---This sets the color you want to use
		g.drawRect(x, y, 50, 50); // ^---This draws a rectangle using the color you selected
		g.setColor(Color.RED);
		g.drawString(Integer.toString(j), x + 20, y + 30);
		x = x + 130;
		j++;

		// rect 6
		g.setColor(Color.BLUE); // ^---This sets the color you want to use
		g.drawRect(x, y, 50, 50); // ^---This draws a rectangle using the color you selected
		g.setColor(Color.RED);
		g.drawString(Integer.toString(j), x + 20, y + 30);
		x = 180;
		y = y + 150;
		j++;

		// rect 7 new row
		g.setColor(Color.BLUE); // ^---This sets the color you want to use
		g.drawRect(x, y, 50, 50); // ^---This draws a rectangle using the color you selected
		g.setColor(Color.RED);
		g.drawString(Integer.toString(j), x + 20, y + 30);
		x = x + 50;
		j++;

		// rect 8
		g.setColor(Color.BLUE); // ^---This sets the color you want to use
		g.drawRect(x, y, 50, 50); // ^---This draws a rectangle using the color you selected
		g.setColor(Color.RED);
		g.drawString(Integer.toString(j), x + 20, y + 30);
		x = x + 70;
		j++;

		// rect 9
		g.setColor(Color.RED); // ^---This sets the color you want to use
		g.drawRect(x, y + 30, 70, 0); // ^---This draws a rectangle using the color you selected
		x = x + 70;
		j++;

		System.out.println("first rect time no " + n);
		n++;
		/**
		 * g.setColor(new Color(190,81,215)); g.drawRect(x+100, y, 50, 50);
		 * System.out.println("second rect time no " + i ); i++; g.setColor(Color.RED);
		 * g.drawString(Integer.toString(j), 21, 21); // ^---This displays a string
		 * using the color you selected
		 */
	}

	public static void main(String[] args) {

		JFrame f = new JFrame("Title"); // ^---This creates a frame to hold the graphics
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ^---This exits the program when you close the window
		Coloring2 graphics = new Coloring2(); // ^---This creates an object so you can access methods outside of the
												// main method
		f.getContentPane().add(graphics); // ^---This adds the object to the frame
		// ^---I think since paintComponent is the only method outside of the main
		// method...
		// ^-...the objects finds it, and adds it the the frame...or something...
		// ^---This part kind of confuse me. It creates the object, but then it add
		// the...
		// ^---object to the frame without telling it which method to use.

		f.setSize(800, 500); // ^---This sets the size of the frame
		f.setVisible(true); // ^---This sets the frame so it's visible
		f.setLocation(200, 200);

	}
}
