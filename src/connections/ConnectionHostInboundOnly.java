package connections;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import log.Log;
import main.Controller;

public class ConnectionHostInboundOnly extends Thread {
	String transmitterName;
	int portNumber;
	Thread transmitter;
	String inbound;
	Connection A1;
	Connection A2;
	
	Controller controller;
	
	public ConnectionHostInboundOnly(String transmitterName, int portNumber, Connection A1, Connection A2, Controller controller)
	{
		this.transmitterName = transmitterName;
		this.portNumber = portNumber;
		this.controller = controller;
		this.A1 = A1;
		this.A2 = A2;
		
		
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
				
				//The send/receive loop.
				while(sock.isConnected()) {
					Thread.sleep(1000);
					if (buffRead.ready()) {
						String input = buffRead.readLine();
						
						A1.putToQueue(input);
						A2.putToQueue(input);
						
						switch(input) {
							case "{PLT:1}": controller.setPLT(true);
								break;
							case "{PLT:0}": controller.setPLT(false);
								break;
							case "{PDT:1}": controller.setPDT(true);
								break;
							case "{PDT:0}": controller.setPDT(false);
								break;
							case "{JIT:1}": controller.setJIT(true);
								break;
							case "{JIT:0}": controller.setJIT(false);
								break;
							default:
								break;
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
}