package main;

import java.sql.SQLException;

import log.Log;
import sql.SQLConnection;

public class Main {
	/**
	 * Starts program
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args){
		//Create Controller. nitroAddress, de1socAddress, nitroPort, de1socPort, 
		Controller controller = new Controller();
		//Start thread and by extension, all the programs functionality.
		try 
		{
			SQLConnection conn = new SQLConnection();
			conn.loginSQL();
			controller.setServer(conn);
			controller.receiveAndSetSlaveAddress();
			
			controller.createConnections();
		} catch (SQLException e1) {
			Log.important("Server is not running!");
		} catch (InterruptedException e) {
			Log.important("Something interrupted the ThreadStarter!");
		}
	}
	
}
