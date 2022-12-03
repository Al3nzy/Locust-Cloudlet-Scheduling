import java.awt.Color;
import java.awt.Graphics;

// ^---Provides interfaces and classes for dealing with different types of events fired by AWT components.
import javax.swing.JFrame;
import javax.swing.JPanel;

// ^---Provides a set of "lightweight" (all-Java language) components that, to the maximum degree possible, work the same on all platforms.
public class CopyOfColoring2 extends JPanel {
	int j = 1;
	int i = 0;
	int x = 50;
	int y = 50;
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;

// ^---Extends JPanel to use the frame

	public void paintComponent(Graphics g) { // ^---You need a paintComponent method to paint pictures

		super.paintComponent(g); // ^---You need to equip the variable g into super.paintComponent to draw
									// pictures
		this.setBackground(Color.PINK); // ^---This sets the background color

		g.setColor(Color.BLUE); // ^---This sets the color you want to use
		g.drawRect(x, y, 50, 50); // ^---This draws a rectangle using the color you selected
		System.out.println("first rect time no " + i);

		g.setColor(new Color(190, 81, 215));
		g.drawRect(x + 100, y, 50, 50);
		System.out.println("second rect time no " + i);
		i++;
		g.setColor(Color.RED);
		g.drawString(Integer.toString(j), 21, 21); // ^---This displays a string using the color you selected

	}

	public static void main(String[] args) {

		JFrame f = new JFrame("Title"); // ^---This creates a frame to hold the graphics
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ^---This exits the program when you close the window
		CopyOfColoring2 graphics = new CopyOfColoring2(); // ^---This creates an object so you can access methods
															// outside of the main method
		f.add(graphics); // ^---This adds the object to the frame
							// ^---I think since paintComponent is the only method outside of the main
							// method...
							// ^-...the objects finds it, and adds it the the frame...or something...
							// ^---This part kind of confuse me. It creates the object, but then it add
							// the...
							// ^---object to the frame without telling it which method to use.

		f.setSize(300, 300); // ^---This sets the size of the frame
		f.setVisible(true); // ^---This sets the frame so it's visible

	}
}
