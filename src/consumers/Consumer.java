package consumers;

import java.sql.SQLException;

import connections.Connection;
import log.Log;
import main.Controller;

public class Consumer extends Thread {
	private Connection connection;
	private String name;
	private Controller controller;
	private String inbound;
	private String tag;
	
	private boolean lock=true;
	
	/**
	 * Creates the consumer thread. It will consume messages from the queue of its given connection.
	 * @param name
	 * @param connection
	 * @param controller
	 */
	public Consumer(String name, Connection connection, Controller controller){
		this.connection = connection;
		this.name = name;
		this.controller = controller;
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
			//Run the consume function over and over...
			consume();
		}
	}
	
	public void consume() {
		//Take from queue and check if it contains information
		inbound = connection.takeFromQueue();
		if(inbound != "" && inbound != null){
			Log.log("Tagged info Received: " + inbound);
			
			try {
				tag = inbound.substring(inbound.indexOf("{")+1, inbound.indexOf(":"));
				inbound = inbound.substring(inbound.indexOf(":")+1, inbound.indexOf("}"));
				
			}catch(IndexOutOfBoundsException e) {
				tag = "";
			}
			
			//Compare tag to a number of cases. Currently everything gets sent to the SQL Server.
			if(tag.equals("BER") || tag.equals("ERR")|| tag.equals("SYN")
					|| tag.equals("UTI")) {
				try {
					controller.dataToServer(inbound, tag);
				} catch (SQLException e) {
					Log.important("Could not send data to server!");
				}
				Log.log("Data marked: " + tag + " was sent to SQL");
			}else if(tag.equals("MOD") || tag.equals("FPS")|| tag.equals("VCL")
					|| tag.equals("RES")) {
				try {
					controller.settingToServer(tag, inbound);
				} catch (SQLException e) {
					Log.important("Could not send setting to server!");
				}
				Log.log("Setting marked: " + tag + " was sent to SQL");
			}else {
				Log.important("Received something Illegal!");
			}
		}
	}
}
