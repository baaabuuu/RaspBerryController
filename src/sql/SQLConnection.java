package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import log.Log;

public class SQLConnection {
	private String port						= "1433";
	private String REMOTE_DATABASE_USERNAME = "fagprojekt";
	private String DATABASE_USER_PASSWORD	= "appContr0l!";
	private String PUBLIC_DNS				= "testdb2.ciwmocpkstry.us-east-2.rds.amazonaws.com";
	private String DATABASE					= "fagprojekt";
	private Connection con;
	private Statement stmt;
	PreparedStatement createDB;
	
	private SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm:ss.S");
	private Date date;
	
	/**
	 *  Utilized to set DNS to specific thing
	 * @param input DNS for server
	 * @author Patrick
	 */
	public void setDNS(String input)
	{
		PUBLIC_DNS = input;
	}
	
	/**
	 * Utilized for setting the connection
	 * @param con
	 * @author Patrick
	 */
	public void setCON(Connection con)
	{
		this.con = con;
	}
	
	/**
	 * Creates connection to SQL Server.
	 * @throws SQLException
	 * @author Christian && Patrick
	 */
	public SQLConnection()
	{
		Log.log("Created class SQLConnection");
	}
	
	/**
	 * Logs into the SQL connection.
	 * @throws SQLException
	 * @author Patrick
	 */
	public void loginSQL() throws SQLException
	{
		Log.log("Starting connectToDatabase");
		con = DriverManager.getConnection("jdbc:sqlserver://" + PUBLIC_DNS + 
				"\\SQLEXPRESS:" + port + ";databaseName=" + DATABASE, REMOTE_DATABASE_USERNAME, DATABASE_USER_PASSWORD);
		Log.log("Connection succesfull");

	}
	
	
	/**
	 * Checks if specified user-name has a matching password, returns true if check succeeds.
	 * @param user
	 * @param password
	 * @return boolean
	 * @throws SQLException
	 * @author Christian
	 */
	public boolean loginToServer(String user, String password) throws SQLException
	{
		String sqlCreate = "SELECT Username FROM loginTable WHERE Password = '" + password + "' AND Username = '" + user + "'";
		Log.log(sqlCreate);
		
		stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(sqlCreate);
		
		return rs.isBeforeFirst();
	}
	
	/**
	 * Sends data to SQL Server in correct format.
	 * @param data
	 * @param tag
	 * @throws SQLException
	 */
	public void dataToServer(String data, String tag) throws SQLException {
		date = new Date();
		
		String sqlCreate = "INSERT INTO dataTable VALUES (" +
				"'"+tag+"'" + "," +
				"'" +data+"'" + "," +
				"'" + formatterDate.format(date) + "'" + "," +
				"'" + formatterTime.format(date) + "'" +
				")";
		Log.log(sqlCreate);
		stmt = con.createStatement();
		stmt.executeUpdate(sqlCreate);
	}
	
	/**
	 * 
	 * @param tag
	 * @param setting
	 * @throws SQLException
	 */
	public void settingToServer(String tag, String setting) throws SQLException {
		date = new Date();
		
		String sqlCreate = "INSERT INTO settingTable VALUES (" +
				"'"+tag+"'" + "," +
				"'" +setting+"'" + "," +
				"'" + formatterDate.format(date) + "'" + "," +
				"'" + formatterTime.format(date) + "'" +
				")";
		Log.log(sqlCreate);
		stmt = con.createStatement();
		stmt.executeUpdate(sqlCreate);
		
	}
	
	/**
	 * Sends log to SQL Server in correct format.
	 * @param user
	 * @param action
	 * @throws SQLException
	 */
	public void logToServer(String user, String action) throws SQLException {
		date = new Date();
		
		String sqlCreate = "INSERT INTO logTable" +
				" VALUES (" +
				user + "," +
				action + "," +
				"'" + formatterDate.format(date) + "'" + "," +
				"'" + formatterTime.format(date) + "'" +
				")";
		Log.log(sqlCreate);
		con.createStatement().executeUpdate(sqlCreate);
		
	}

	/**
	 * Queries for specified tag, after a specified data and time, from dataTable on the SQL Server.
	 * @param date
	 * @param time
	 * @param tag
	 * @return
	 * @throws SQLException
	 */
	public ResultSet selectSingleDataBiggerThan(String date, String time, String tag) throws SQLException {
		String sqlCreate = "SELECT Tag, Data, Date, Time FROM dataTable WHERE Tag = '"+ tag + "' AND Date = '" + date + "' AND Time > '" + time + "'";
		Log.log(sqlCreate);
		stmt = con.createStatement();
		
		return stmt.executeQuery(sqlCreate);
	}
	
	
	public ResultSet selectSingleSettingBiggerThan(String date, String time, String tag) throws SQLException {
		String sqlCreate = "SELECT Tag, Data, Date, Time FROM settingTable WHERE Tag = '"+ tag + "' AND Date = '" + date + "' AND Time > '" + time + "'";
		Log.log(sqlCreate);
		stmt = con.createStatement();
		
		return stmt.executeQuery(sqlCreate);
	}

	/**
	 * Get the IP Address of the Raspberry Pi Slave from the Addresstable.
	 * @return
	 * @throws SQLException
	 * @throws InterruptedException 
	 */
	public String slaveAddress() throws SQLException {
		String sqlCreate = "SELECT Address FROM Addresstable";
		Log.log(sqlCreate);
		stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("fish");
		rs.next();
		return rs.getString("Address");
	}
}
