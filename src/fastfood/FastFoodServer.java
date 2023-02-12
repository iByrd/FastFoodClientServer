package fastfood;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FastFoodServer {
	// order number, lock, and port
	private static int orderNumber = 1000;
	private Lock orderNumberLock;
	private int port;

	// server constructor
	public FastFoodServer(int port) {
		this.port = port;
		this.orderNumberLock = new ReentrantLock();
	}

	// method getter for OrderNumber protected with lock
	public int getOrderNumber() {
		orderNumberLock.lock();
		try {
			return orderNumber;
		} finally {
			orderNumberLock.unlock();
		}
	}

	// set server run method, listen for connections, and start threads
	public void runForever() {
		try (ServerSocket serverSocket = new ServerSocket(this.port)) {

			while (true) {
				Socket socket = serverSocket.accept();

				// make and start thread
				Thread clientThread = new Thread(new ClientTask(socket));
				clientThread.start();

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// method to update orderNumber and protect with lock
	public void updateOrderNumber() {
		orderNumberLock.lock();
		try {
			orderNumber++;
		} finally {
			orderNumberLock.unlock();
		}
	}

	// method to run client threads, gather client input, build OrderForm object,
	// write order log, and respond to client
	public class ClientTask extends FastFoodServer implements Runnable {
		private Socket socket = null;

		public ClientTask(Socket socket) {
			super(port);
			this.socket = socket;
		}

		@Override
		public void run() {

			try {
				// setup input and output
				DataInputStream input = new DataInputStream(socket.getInputStream());
				DataOutputStream output = new DataOutputStream(socket.getOutputStream());

				while (true) {
					// gather variables
					int burgerQuantity = input.readInt();
					int friesQuantity = input.readInt();
					int shakeQuantity = input.readInt();

					// detect client exit
					if (burgerQuantity < 0 && friesQuantity < 0 && shakeQuantity < 0) {
						break;
					}

					// make object and receipt
					OrderForm orderIn = new OrderForm(burgerQuantity, friesQuantity, shakeQuantity, getOrderNumber());
					orderIn.calculateTotalAmount();
					String receipt = orderIn.toString();

					// ready receipt reader and writer
					PrintWriter writer = new PrintWriter(new FileOutputStream(new File("orderlog.txt"), true));
					Scanner orderReader = new Scanner(receipt);

					// write client order to log and close resources
					while (orderReader.hasNextLine()) {
						String temp = orderReader.nextLine();
						writer.println(temp);
					}
					writer.println(socket.getRemoteSocketAddress());
					writer.flush();
					writer.close();
					orderReader.close();

					// send receipt to client
					output.writeUTF(receipt);
					output.flush();

					// increment order number using lock protected method
					updateOrderNumber();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public static void main(String[] args) throws IOException {
		// start server
		int port = Integer.parseInt(args[0]);
		FastFoodServer server = new FastFoodServer(port);
		server.runForever();

	}

}
