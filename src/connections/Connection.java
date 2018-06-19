package connections;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

import log.Log;

public class Connection extends Thread {
	private String transmitterName;
	private int portNumber;
	private Thread transmitter;
	private String server;
	private String inbound;
	private Semaphore semaphore = null;
	
	ArrayBlockingQueue<String> inboundQueue = new ArrayBlockingQueue<String>(20);
	ArrayBlockingQueue<String> outboundQueue = new ArrayBlockingQueue<String>(20);
	
	/**
	 * Creates thread handling TCP Connection, using an inbound and outbound queue.
	 * @param transmitterName
	 * @param server
	 * @param portNumber
	 */
	public Connection(String transmitterName, Semaphore semaphore, String server, int portNumber) throws NullPointerException{
		if (transmitterName == null || semaphore == null || server == null) {
	        throw new NullPointerException();
	    }
		this.transmitterName = transmitterName;
		this.portNumber = portNumber;
		this.server = server;
		this.semaphore = semaphore;
		
		System.out.println("Thread created: " + transmitterName);
	}
	/**
	 * Connects to specified host.
	 * Everything it receives from host is put into inboundQueue.
	 * Everything in the outbound queue is sent to host.
	 */
	public void run(){
		while(true) {
			Socket sock = null;
			BufferedReader buffRead = null;
			OutputStream out = null;
			OutputStreamWriter outW = null;
			BufferedWriter outBW = null;
			
			
			try {
				sock = new Socket(server, portNumber);
				
				buffRead = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				out = sock.getOutputStream();
				outW = new OutputStreamWriter(out);
				outBW = new BufferedWriter(outW);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			runningConnection(sock, buffRead,out,outW,outBW,this.inboundQueue,this.outboundQueue);
		}
	}
	
	public void runningConnection(Socket sock, BufferedReader buffRead, OutputStream out, OutputStreamWriter outW, BufferedWriter outBW,ArrayBlockingQueue<String> inboundQueue, ArrayBlockingQueue<String> outboundQueue){
		try{		
			//The send/receive loop.
			while(sock.isConnected()) {

				Thread.sleep(100);
				if (!outboundQueue.isEmpty()) {
					try {
						semaphore.acquire();
						
						String a = outboundQueue.take();
						outBW.write(a);
						Thread.sleep(100);
						semaphore.release();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					outBW.flush();
				}
				Thread.sleep(100);
				if (buffRead.ready()) {
					try {
						inboundQueue.put(buffRead.readLine());
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
			Log.log("Socket no longer connected");
			sock.close();
		} catch(IOException | InterruptedException e){
			Log.important("IOException or InterruptedException!");
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e1) {
				Log.important("You Interrupted my SLEEP!");
			}
		}
	}
	/**
	 * Puts data into queue. Everything in this queue is sent to the connected host.
	 * @param toQueue
	 */
	public void putToQueue(String toQueue)
	{
		try {
			this.outboundQueue.put(toQueue + "\r\n");
		} catch (InterruptedException e) {
			Log.log("failed to put data into queue.");
		}
	}
	/**
	 * Takes received data from queue.
	 * @return String
	 */
	public String takeFromQueue(){
		String get = "";
		try {
			if (!inboundQueue.isEmpty()) {
				get = inboundQueue.take();
			}
			} catch (InterruptedException e) {
				Log.log("failed to get data from queue.");
				get = "Failed to get data from queue.";
			}
		return get;
	}


	
}