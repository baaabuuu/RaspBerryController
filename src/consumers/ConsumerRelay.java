package consumers;

import connections.Connection;
import connections.ConnectionHost;
import log.Log;

public class ConsumerRelay extends Thread {
	private String inbound;
	private Connection connection;
	private ConnectionHost host;
	private String name;
	
	/**
	 * Creates the consumer thread. It will consume messages from the queue of its given connection.
	 * @param name
	 * @param connection
	 * @param controller
	 */
	public ConsumerRelay(String name, Connection connection, ConnectionHost host){
		this.connection = connection;
		this.host = host;
		this.name = name;
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
			consumeRelay();
		}
	}
	public void consumeRelay() {
		//Take from queue and check if it contains information
		inbound = connection.takeFromQueue();
		if(inbound != "" && inbound != null){
			Log.log("Tagged info Received: " + inbound);
			host.putToQueue(inbound);
		}
	}
}
