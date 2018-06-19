package connections;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

import log.Log;
import main.Controller;

public class ConnectionHost extends Thread {
	String transmitterName;
	int portNumber;
	Thread transmitter;
	String inbound;
	
	Controller controller;
	
	ArrayBlockingQueue<String> inboundQueue = new ArrayBlockingQueue<String>(20);
	ArrayBlockingQueue<String> outbound = new ArrayBlockingQueue<String>(20);
	
	public ConnectionHost(String transmitterName, int portNumber)
	{
		this.transmitterName = transmitterName;
		this.portNumber = portNumber;
		System.out.println("Thread created: " + transmitterName);
	}
	
	public void run(){
		ServerSocket welcomeSocket;
		Socket sock;
		while(true) {
			
			try{
				welcomeSocket = new ServerSocket(portNumber);
				
				sock = welcomeSocket.accept();
				
				BufferedReader buffRead = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				OutputStream out = sock.getOutputStream();
				OutputStreamWriter outW = new OutputStreamWriter(out);
				BufferedWriter outBW = new BufferedWriter(outW);
				
				//The send/receive loop.
				while(sock.isConnected()) {
					Thread.sleep(250);
					if (!outbound.isEmpty()) {
						try {
							String a = outbound.take();
							outBW.write(a);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						outBW.flush();
					}
					if (buffRead.ready()) {
						try {
							inboundQueue.put(buffRead.readLine());
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				welcomeSocket.close();
			} catch(IOException | InterruptedException e){
				Log.important("IOException or InterruptedException!");
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e1) {
					Log.important("You Interrupted my SLEEP!");
				}
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
			outbound.put(toQueue + "\r\n");
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