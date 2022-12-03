import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;

public class Coloring {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();

		Coloring window = new Coloring();
		window.frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	/**
	 * Create the application.
	 */
	public Coloring() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		// frame.setBackground(Color.PINK);
		frame.setBounds(100, 100, 575, 502);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		draw serversdrawer = new draw();
		// serversdrawer.setBackground(Color.GRAY);

		frame.getContentPane().add(serversdrawer, BorderLayout.CENTER);

		serversdrawer.add(serversdrawer.l);
		serversdrawer.setLayout(new GridLayout(1, 0, 0, 0));

		serversdrawer.drawing();

	}

}
