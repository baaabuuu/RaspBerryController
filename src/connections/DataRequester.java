package connections;

import log.Log;

public class DataRequester extends Thread {
	
	private Connection connection1;
	private String name;
	/**
	 * Creates the DataRequester thread.
	 * Requests data in its given interval.
	 * @param name
	 * @param connection1
	 * @param updateInterval
	 */
	public DataRequester(String name, Connection connection1) {
		this.connection1 = connection1;
		this.name = name;
	}
	public void run(){
		
		int a = 0;
		while(true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Log.important("A Thread named: " + name + " has died!");
			}
			//Request all data from the connected board.
			connection1.putToQueue("{REQ:DAT}\0");
//			if(a==0) {
//				Log.log("Requested modulation change to 1");
//				connection1.putToQueue("{MOD:1}\0");
//				a=1;
//			}else if(a==1) {
//				Log.log("Requested modulation change to 2");
//				connection1.putToQueue("{MOD:2}\0");
//				a=2;
//			}else {
//				Log.log("Requested modulation change to 0");
//				connection1.putToQueue("{MOD:0}\0");
//				a=0;
//			}
		}
		
		
	}
	
}
