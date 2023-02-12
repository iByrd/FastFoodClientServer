package fastfood;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class FastFoodClient {
	// setup variables
	private JTextField burgerQuantity;
	private int sendBurger;
	private JTextField friesQuantity;
	private int sendFries;
	private JTextField shakeQuantity;
	private int sendShake;
	private JTextArea receipt;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private String host;
	private int port;
	private Socket socket;
	private boolean connection = false;

	// Constructor
	public FastFoodClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	// build run method, make frame, panel, and components
	public void run() throws IOException {
		// make frame
		JFrame frame = new JFrame("Byrd's Nest Order Form");
		frame.setSize(600, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// make panel
		JPanel panel = new JPanel();
		GridLayout layout = new GridLayout(8, 2);
		panel.setLayout(layout);

		// add header with images
		BufferedImage bangingBurger = ImageIO.read(new File("bangingBurger.jpg"));
		panel.add(new JLabel(new ImageIcon(bangingBurger)));
		BufferedImage landslideShake = ImageIO.read(new File("landslideShake.jpg"));
		panel.add(new JLabel(new ImageIcon(landslideShake)));

		// add Greeting
		JLabel greeting = new JLabel("Welcome to Byrd's Nest", SwingConstants.CENTER);
		greeting.setFont(new Font("Serif", Font.ITALIC, 26));
		panel.add(greeting);
		JLabel greeting2 = new JLabel("Famous Burgers and Shakes", SwingConstants.CENTER);
		greeting2.setFont(new Font("Serif", Font.ITALIC, 22));
		panel.add(greeting2);

		// add menu and quantity label
		JLabel menu = new JLabel("Our Menu", SwingConstants.CENTER);
		menu.setFont(new Font("Serif", Font.BOLD, 28));
		panel.add(menu);
		JLabel quantity = new JLabel("Quantity Desired", SwingConstants.CENTER);
		quantity.setFont(new Font("Serif", Font.BOLD, 22));
		panel.add(quantity);

		// add burger and quantity text box
		JLabel burger = new JLabel(
				"<html>Banging Burger @ $5.00<br/>Over-easy egg & bacon<br/>lettuce & Banging Sauce<html>",
				SwingConstants.CENTER);
		burger.setFont(new Font("Serif", Font.ITALIC, 16));
		panel.add(burger);
		burgerQuantity = new JTextField();
		burgerQuantity.setFont(new Font("Serif", Font.BOLD, 38));
		burgerQuantity.setHorizontalAlignment(JTextField.CENTER);
		panel.add(burgerQuantity);

		// add fries
		JLabel fries = new JLabel(
				"<html>Dynomite Fries @ $2.00<br/>Dashed with himalayan salt<br/>& Dynomite seasoning<html>",
				SwingConstants.CENTER);
		fries.setFont(new Font("Serif", Font.ITALIC, 16));
		panel.add(fries);
		friesQuantity = new JTextField();
		friesQuantity.setFont(new Font("Serif", Font.BOLD, 38));
		friesQuantity.setHorizontalAlignment(JTextField.CENTER);
		panel.add(friesQuantity);

		// add shake
		JLabel shake = new JLabel(
				"<html>Landslide Shake @ $3.50<br/>Imported dark chocolate<br/>Whipped cream & cherry<html>",
				SwingConstants.CENTER);
		shake.setFont(new Font("Serif", Font.ITALIC, 16));
		panel.add(shake);
		shakeQuantity = new JTextField();
		shakeQuantity.setFont(new Font("Serif", Font.BOLD, 38));
		shakeQuantity.setHorizontalAlignment(JTextField.CENTER);
		panel.add(shakeQuantity);

		// ask user to submit
		JLabel pleaseSubmit = new JLabel("Please submit order when ready ->", SwingConstants.CENTER);
		pleaseSubmit.setFont(new Font("Serif", Font.BOLD, 16));
		panel.add(pleaseSubmit);

		// make button, wire it, add to panel
		JButton submit = new JButton("Submit Order");
		submit.setFont(new Font("Arial", Font.BOLD, 28));

		// for wire, see SubmitButtonListener below
		ActionListener listener = new SubmitButtonListener();
		submit.addActionListener(listener);
		panel.add(submit);

		// add receipt to panel
		JLabel orderConfirmation = new JLabel("Order Confirmation", SwingConstants.CENTER);
		orderConfirmation.setFont(new Font("Serif", Font.ITALIC, 20));
		panel.add(orderConfirmation);
		receipt = new JTextArea(20, 20);
		receipt.setFont(new Font("Serif", Font.BOLD, 20));
		JScrollPane scroll = new JScrollPane(receipt, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scroll);

		// place panel on frame and make visible
		frame.add(panel);
		frame.setVisible(true);

		// protect close behavior
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				try {
					if (connection) {
						// tell server to close connection via negative values
						outputStream.writeInt(-1);
						outputStream.writeInt(-1);
						outputStream.writeInt(-1);
						// close resources
						outputStream.close();
						inputStream.close();
						socket.close();
					}
				} catch (IOException e1) {
				}
			};
		});

	}

	// program action listener for button
	class SubmitButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {

			try {
				// establish connect event
				if (!connection) {
					socket = new Socket(host, port);
					inputStream = new DataInputStream(socket.getInputStream());
					outputStream = new DataOutputStream(socket.getOutputStream());
					connection = true;
				}
				// verify input prior to sending to server, sets value to 0 if blank or
				// non-number, see method below
				sendBurger = verifyTextFields(burgerQuantity, 0);
				sendFries = verifyTextFields(friesQuantity, 0);
				sendShake = verifyTextFields(shakeQuantity, 0);

				// pass verified data to FastFoodServer
				outputStream.writeInt(sendBurger);
				outputStream.writeInt(sendFries);
				outputStream.writeInt(sendShake);
				outputStream.flush();

				// read response
				String report = inputStream.readUTF();
				receipt.append(report + "\n");
			} catch (Exception e) {
				receipt.append(e.getMessage());
			}

		}

		public int verifyTextFields(JTextField inputText, int zero) {
			if (inputText == null || inputText.getText().trim().isEmpty()) {
				return zero;
			} else {
				try {
					return Integer.parseInt(inputText.getText());
				} catch (NumberFormatException e) {
					return zero;
				}
			}
		}

	}

	public static void main(String[] args) throws IOException {
		// provide connection detail and start client
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		FastFoodClient client = new FastFoodClient(host, port);
		client.run();

	}

}
