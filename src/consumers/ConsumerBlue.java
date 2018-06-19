package consumers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import connections.Connection;
import connections.ConnectionHost;
import log.Log;
import main.Controller;

public class ConsumerBlue extends Thread {
	private String inbound;
	private ConnectionHost connection;
	private String name;
	private Controller controller;
	private Connection nitrogenConnect;
	private Connection de1socConnect;
	private JsonObject obj;
	private String tag;
	
	/**
	 * Modified Consumer class, to be used for the TCP connection with the bluetooth program. Creates the bluetooth consumer.
	 * @param name
	 * @param connection
	 * @param controller
	 */
	public ConsumerBlue(String name, ConnectionHost connection, Controller controller,
			Connection nitrogenConnect, Connection de1socConnect){
		this.connection = connection;
		this.name = name;
		this.controller = controller;
		this.nitrogenConnect = nitrogenConnect;
		this.de1socConnect = de1socConnect;
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
			//Do the thing...
			consumeBlue();
		}
	}
	/**
	 * 
	 */
	public void consumeBlue() {
		//Take from queue and check if it's empty
		inbound = connection.takeFromQueue();
		if(inbound != "" && inbound != null){
			Log.log("Received json String: " + inbound);
			
			JsonParser parser = new JsonParser();
			try {
				obj = parser.parse(inbound).getAsJsonObject();
				tag = obj.get("request").getAsString();
				
				//Compare tag with a set of options
				switch(tag){
				case "setSetting":
					connection.putToQueue("ACK");
					if(controller.getLoggedIn()) {
						//Compare received setting to a switch case, so the setting change is sent to the correct place.
						switch(obj.get("setting").getAsString()) {
							case "MOD":
								de1socConnect.putToQueue("{MOD:"+obj.get("value").getAsString()+"}");
								controller.logToServer("Requested the setting 'MOD' changed to " +
										obj.get("value").getAsString());
								break;
							case "FPS":
							case "VCL":
							case "RES":
								nitrogenConnect.putToQueue("{"+ obj.get("setting").getAsString()+
										":"+obj.get("value").getAsString()+"}");
								controller.logToServer("Requested the setting '"+ obj.get("setting").getAsString()
											+"' changed to " + obj.get("value").getAsString());
								break;
							default:
								break;
						}
					}
					break;
				case "LoginInformation": connection.putToQueue(controller.loginToServer(obj.get("username").
						getAsString(),obj.get("password").getAsString()));
					break;
				case "getErrors":
					connection.putToQueue(controller.getErrorGraphData(obj.get("date").getAsString()));
					controller.logToServer("Requested ErrorGraphData");
					break;
				case "update":
					connection.putToQueue(controller.getUpdateData(obj.get("date").getAsString()));
					controller.logToServer("Requested UpdateData");
					break;
				default: 
					break;
				}
			}catch(IllegalStateException | NullPointerException e){
				Log.important("A message with wrong JsonSyntax was received!");
			}
			
		}
	}
}
