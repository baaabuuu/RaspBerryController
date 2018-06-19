package consumers;

import connections.Connection;
import connections.ConnectionHost;
import log.Log;
import main.Controller;

public class ConsumerHostRelay extends Thread {
	private String inbound;
	private String tag;
	private ConnectionHost connection;
	private String name;
	private Connection nitro;
	private Connection de1;
	
	/**
	 * Creates the consumer thread. It will consume messages from the queue of its given connection.
	 * @param name
	 * @param connection
	 * @param controller
	 */
	public ConsumerHostRelay(String name, ConnectionHost connection, Connection nitro, Connection de1){
		this.connection = connection;
		this.name = name;
		this.nitro = nitro;
		this.de1 = de1;
	}
	/**
	 * Runs the consumer.
	 */
	public void run(){
		while(true){
			//To spend less cpu than it needs to.
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Log.important("A Thread named: " + name + " has died!");
			}
			consumeHost();
		}
	}
	public void consumeHost() {
		//Take from queue and check if it contains information
		inbound = connection.takeFromQueue();
		
		if(inbound != "" && inbound != null){
			Log.log("Tagged info Received: " + inbound);
			//Get the tag
			try {
				tag = inbound.substring(inbound.indexOf("{")+1,inbound.indexOf(":"));
				if(tag.equals("REQ")) {
					tag = inbound.substring(inbound.indexOf(":")+1,inbound.indexOf("}"));
				}
			}catch(IndexOutOfBoundsException e) {
				tag = "";
			}
			
			
			//Send the message to where it needs to go
			if(
					tag.equals("ALL") ||
					tag.equals("DAT") ||
					tag.equals("BER") ||
					tag.equals("ERR") ||
					tag.equals("SYN") ||
					tag.equals("UTI") ||
					tag.equals("MOD") ||
					tag.equals("KEY")
					) {
				de1.putToQueue(inbound+"\0");
			}else if(
					tag.equals("FPS") ||
					tag.equals("VCL") ||
					tag.equals("RES")
					){
				nitro.putToQueue(inbound);
			}else {
				Log.log("Received tag was not on the list");
			}
		}
	}
}
