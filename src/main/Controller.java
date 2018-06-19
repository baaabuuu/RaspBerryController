package main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import log.Log;
import sql.SQLConnection;

public class Controller {
	
	private String user = "None";
	
	private SQLConnection server;
	private String slaveAddress;
	
	private SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat formatterTime = new SimpleDateFormat("hh:mm:ss.S");
	private SimpleDateFormat formatterDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
	
	private boolean loggedIn = false;
	private boolean PLT = false;
	private boolean PDT = false;
	private boolean JIT = false;
	
	/**
	 * Simple setter for value
	 * @param formatterDateTime
	 * @author s164166
	 */
	public void setFormDateTime(SimpleDateFormat formatterDateTime)
	{
		this.formatterDateTime = formatterDateTime;
	}
	
	/**
	 * Simple setter for value
	 * @param formatterDateTime
	 * @author s164166
	 */
	public void setFormTime(SimpleDateFormat formatterTime)
	{
		this.formatterTime = formatterTime;
	}
	
	/**
	 * Simple setter for value
	 * @param formatterDateTime
	 * @author s164166
	 */
	public void setFormDate(SimpleDateFormat formatterDate)
	{
		this.formatterDate = formatterDate;
	}

	/**
	 * Sets the server to something specific
	 * @author Patrick
	 * @param server
	 */
	public void setServer(SQLConnection server)
	{
		this.server = server;
	}
	
	/**
	 * Gets the slave Address
	 * @throws SQLException
	 * @author Christian && Patrick
	 */
	public String receiveAndSetSlaveAddress() throws SQLException
	{
		slaveAddress = server.slaveAddress();
		return slaveAddress;
	}
	
	
	/**
	 * Initiate all other threads, each of which handles a TCP connection.
	 * @throws InterruptedException 
	 */
	public void createConnections() throws InterruptedException
	{
		//Class that creates and initializes all threads for handling an arduino relay, or a raspberry relay.
		ThreadStarter threads = new ThreadStarter();
		threads.startThreads(slaveAddress, this);
		
	}
	
	/**
	 * Attempts to log into server.
	 * @param user
	 * @param password
	 * @return String stating success or failure
	 */
	public String loginToServer(String user, String password)
	{
		try {
			if(server.loginToServer(user, password))
			{
				Log.log("logged in");
				this.user = user;
				loggedIn = true;
				logToServer("Successful Login");
				
				return "LOGINOK";
			}
			else
			{
				Log.log("NOT logged in");
				logToServer("Failed login");
				
				return "LOGINNO";
			}
			
		} catch (SQLException e) {
			return "Error";
		}
	}
	
	/**
	 * Stores data and its tag on the SQL server together with time and date received.
	 * @param data
	 * @param tag
	 * @throws SQLException 
	 */
	public void dataToServer(String data, String tag) throws SQLException
	{
		server.dataToServer(data, tag);
	}
	/**
	 * Sends setting to server
	 * @param tag
	 * @param setting
	 * @throws SQLException 
	 */
	public void settingToServer(String tag, String setting) throws SQLException
	{
		server.settingToServer(tag, setting);
	}
	/**
	 * Sends a log to the SQL Server.
	 * @param user
	 * @param message
	 * @throws SQLException 
	 */
	public void logToServer(String message){
		try {
			server.logToServer(user, message);
		} catch (SQLException e) {
			Log.important("Could not send log to server!");
		}
	}
	
	/**
	 * Gets the data needed for creating an Error Graph on the App.
	 * @param date
	 * @return
	 */
	public String getErrorGraphData(String date)
	{
		try
		{
			Date daytime;
			daytime = formatterDateTime.parse(date);
			String day = formatterDate.format(daytime)  + "";
			String time = formatterTime.format(daytime) + "";
			
			//logToServer(user, "Requested all data after: " + date);
			Map<String, List<String[]>> map = new HashMap<String, List<String[]>>();
			ResultSet rs = server.selectSingleDataBiggerThan(day, time, "ERR");
			addToMap(rs, map);
			rs = server.selectSingleDataBiggerThan(day, time, "BER");
			addToMap(rs, map);
			rs = server.selectSingleSettingBiggerThan(day, time, "FPS");
			addToMap(rs, map);
			
			return new JSONObject(map).toString();
		}
		catch (ParseException | SQLException e)
		{
			return "Error";
		}
	}
	/**
	 * Gets all data relevant for keeping the app up-to-date. Had to get changed last minute, due to sudden format mismatch.
	 * @param date
	 * @return
	 */
	public String getUpdateData(String date) {
		try {
			Date dayTime;
			dayTime = formatterDateTime.parse(date);
			String dayF = formatterDate.format(dayTime)+"";
			String timeF = formatterTime.format(dayTime)+"";
			
			
			ResultSet rs = server.selectSingleDataBiggerThan(dayF, timeF, "BER");
			
			String tag;
			String data=""+0;
			String day;
			String time;
			
			while(rs.next()) {
				tag = rs.getString("Tag");
				data = rs.getString("Data");
				day = rs.getString("Date");
				time = rs.getString("Time");
			}
			
			String a = "\"BER\":\""+data+"\"";
			
			rs = server.selectSingleDataBiggerThan(dayF, timeF, "SYN");
			
			data=""+0;
			while(rs.next()) {
				tag = rs.getString("Tag");
				data = rs.getString("Data");
				day = rs.getString("Date");
				time = rs.getString("Time");
			}
			a = a + ", \"SYN\":\""+data+"\"";
			
			rs = server.selectSingleDataBiggerThan(dayF, timeF, "UTI");
			
			data=""+0;
			while(rs.next()) {
				tag = rs.getString("Tag");
				data = rs.getString("Data");
				day = rs.getString("Date");
				time = rs.getString("Time");
			}
			
			a = a + ", \"UTI\":\""+data+"\"";
			
			rs = server.selectSingleSettingBiggerThan(dayF, timeF, "FPS");
			
			data=""+0;
			while(rs.next()) {
				tag = rs.getString("Tag");
				data = rs.getString("Data");
				day = rs.getString("Date");
				time = rs.getString("Time");
			}
			
			a = a + ", \"FPS\":\""+data+"\"";
			
			rs = server.selectSingleSettingBiggerThan(dayF, timeF, "MOD");
			
			data=""+0;
			while(rs.next()) {
				tag = rs.getString("Tag");
				data = rs.getString("Data");
				day = rs.getString("Date");
				time = rs.getString("Time");
			}
			
			a = a + ", \"MOD\":\""+data+"\"";
			
			if(getPLT()) {
				a = a + ", \"PLT\":\""+1+"\"";
			}else {
				a = a + ", \"PLT\":\""+0+"\"";
			}
			if(getPDT()) {
				a = a + ", \"PDT\":\""+1+"\"";
			}else {
				a = a + ", \"PDT\":\""+0+"\"";
			}
			if(getJIT()) {
				a = a + ", \"JIT\":\""+1+"\"";
			}else {
				a = a + ", \"JIT\":\""+0+"\"";
			}
			
			return a;
		} catch (ParseException | SQLException e) {
			return "Error";
		}
	}

	/**
	 * Creates a Json structure from a SQL Resultset.
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public String createJson(ResultSet rs) throws SQLException {
		Map<String,List<String[]>> map = new HashMap<String, List<String[]>>();
		
		String tag;
		String data;
		String day;
		String time;
		
		List<String[]> list;
		
		//Place all data from the result-set into a map and then to JSONObject format.
		while(rs.next()) {
			
			list = new ArrayList<String[]>();
			tag = rs.getString("Tag");
			data = rs.getString("Data");
			day = rs.getString("Date");
			time = rs.getString("Time");
			
			if(map.containsKey(tag)) {
				list = map.get(tag);
				
				list.add(new String[]{data,day+" "+time});
				map.put(tag, list);
			}else {
				list.add(new String[]{data,day+" "+time});
				map.put(tag, list);
			}
		}
		
		JSONObject json = new JSONObject(map);
		
		return json.toString();
	}
	/**
	 * Add resultset to a map
	 * @param rs
	 * @param map
	 * @return
	 * @throws SQLException
	 */
	public Map<String,List<String[]>> addToMap(ResultSet rs, Map<String,List<String[]>> map) throws SQLException {
		
		String tag;
		String data;
		String day;
		String time;
		
		List<String[]> list;
		
		//Place all data from the result-set into a map and then to JSONObject format.
		while(rs.next()) {
			
			list = new ArrayList<String[]>();
			tag = rs.getString("Tag");
			data = rs.getString("Data");
			day = rs.getString("Date");
			time = rs.getString("Time");
			
			if(map.containsKey(tag)) {
				list = map.get(tag);
				
				list.add(new String[]{data,day+" "+time});
				map.put(tag, list);
			}else {
				list.add(new String[]{data,day+" "+time});
				map.put(tag, list);
			}
		}
		return map;
	}
	
	
	public boolean getLoggedIn()
	{
		return loggedIn;
	}

	public boolean getPLT()
	{
		return PLT;
	}
	public boolean getPDT()
	{
		return PDT;
	}
	public boolean getJIT()
	{
		return JIT;
	}
	
	public void setPLT(boolean PLT)
	{
		this.PLT = PLT;
	}
	public void setPDT(boolean PDT)
	{
		this.PDT = PDT;
	}
	public void setJIT(boolean JIT)
	{
		this.JIT = JIT;
	}
}
