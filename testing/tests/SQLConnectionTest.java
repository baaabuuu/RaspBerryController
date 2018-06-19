package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.mockito.junit.MockitoJUnitRunner;

import log.Log;
import sql.SQLConnection;


@RunWith(MockitoJUnitRunner.Silent.class)
/**
 * SQLConnection behavior
 * A little slow due to using specific behavior
 * @author s164166
 */
public class SQLConnectionTest
{	
	SQLConnection conn;
	Connection connMock;
	Statement statementMock;
	ResultSet resultSetMock;

	@Before
	public void setupStuff()
	{
		MockitoAnnotations.initMocks(this);
		conn = new SQLConnection();
		connMock = Mockito.mock(Connection.class);
		statementMock = Mockito.mock(Statement.class);
		resultSetMock = Mockito.mock(ResultSet.class);
	}
	
	@Test(expected = SQLException.class) 
	public void loginSQLException() throws SQLException
	{
		conn.setDNS("FAIL");
		conn.loginSQL();
	}
	
	@Test
	public void loginSQL() throws SQLException
	{
		conn.loginSQL();
	}
	
	@Test(expected = SQLException.class) 
	public void loginToServerSQLException() throws SQLException
	{
		when(connMock.createStatement()).thenThrow(SQLException.class);
		conn.setCON(connMock);
		conn.loginToServer("hello", "world");
	}

	
	@Test 
	public void loginToServerWrongPasswordUsername() throws SQLException
	{
		when(connMock.createStatement()).thenReturn(statementMock);
		when(statementMock.executeQuery(any(String.class))).thenReturn(resultSetMock);
		when(resultSetMock.isBeforeFirst()).thenReturn(false);
		
		conn.loginSQL();
		conn.setCON(connMock);
		
		boolean result = conn.loginToServer("hello", "world");
		assertFalse(result);
	}
	
	@Test 
	public void loginToServer() throws SQLException, InterruptedException
	{
		when(resultSetMock.isBeforeFirst()).thenReturn(true);
		when(statementMock.executeQuery(any(String.class))).thenReturn(resultSetMock);
		when(connMock.createStatement()).thenReturn(statementMock);
		
		conn.loginSQL();
		conn.setCON(connMock);
		
		boolean result = conn.loginToServer("hello", "world");
		Log.important("RESULT IS: " + result);
		assertTrue(result);
	}
	
	
	@Test(expected = SQLException.class) 
	public void dataToServerException() throws SQLException
	{
		when(connMock.createStatement()).thenReturn(statementMock);
		when(statementMock.executeUpdate(any(String.class))).thenThrow(SQLException.class);
		
		
		conn.loginSQL();
		conn.setCON(connMock);
		
		conn.dataToServer("HELLO", "WORLD");
	}
	
	@Test
	public void dataToServer() throws SQLException
	{
		when(connMock.createStatement()).thenReturn(statementMock);
		when(statementMock.executeUpdate(any(String.class))).thenReturn(0);
		conn.loginSQL();
		conn.setCON(connMock);
		conn.dataToServer("HELLO", "WORLD");
	}
	
	@Test(expected = SQLException.class) 
	public void settingToServerException() throws SQLException
	{
		when(connMock.createStatement()).thenReturn(statementMock);
		when(statementMock.executeUpdate(any(String.class))).thenThrow(SQLException.class);
		conn.loginSQL();
		conn.setCON(connMock);
		conn.settingToServer("HELLO", "WORLD");
	}
	
	@Test
	public void settingToServer() throws SQLException
	{
		when(statementMock.executeUpdate(any(String.class))).thenReturn(0);
		when(connMock.createStatement()).thenReturn(statementMock);
		conn.loginSQL();
		conn.setCON(connMock);
		conn.settingToServer("HELLO", "WORLD");
	}
	
	@Test(expected = SQLException.class) 
	public void logToServerException() throws SQLException
	{
		when(connMock.createStatement()).thenReturn(statementMock);
		when(statementMock.executeUpdate(any(String.class))).thenThrow(SQLException.class);
		conn.loginSQL();
		conn.setCON(connMock);
		conn.logToServer("HELLO", "WORLD");
	}
	
	@Test
	public void logToServer() throws SQLException
	{
		when(connMock.createStatement()).thenReturn(statementMock);
		when(statementMock.executeUpdate(any(String.class))).thenReturn(0);
		conn.loginSQL();
		conn.setCON(connMock);
		conn.logToServer("HELLO", "WORLD");
	}
	

	@Test
	public void selectSingleBiggerThan() throws SQLException
	{
		when(statementMock.executeQuery(any(String.class))).thenReturn(resultSetMock);
		when(connMock.createStatement()).thenReturn(statementMock);
		conn.loginSQL();
		conn.setCON(connMock);
		conn.selectSingleDataBiggerThan("nope", "nothing", "to see here");
	}
	
	@Test(expected = SQLException.class) 
	public void selectSingleBiggerThanSQLException() throws SQLException
	{
		when(connMock.createStatement()).thenReturn(statementMock);
		when(statementMock.executeQuery(any(String.class))).thenThrow(SQLException.class);
		conn.loginSQL();
		conn.setCON(connMock);
		conn.selectSingleDataBiggerThan("nope", "nothing", "to see here");
	}
	
	@Test
	public void selectSingleSettingBiggerThan() throws SQLException
	{
		when(statementMock.executeQuery(any(String.class))).thenReturn(resultSetMock);
		when(connMock.createStatement()).thenReturn(statementMock);
		conn.loginSQL();
		conn.setCON(connMock);
		conn.selectSingleSettingBiggerThan("nope", "nothing", "to see here");
	}
	
	@Test(expected = SQLException.class) 
	public void selectSingleSettingBiggerThanSQLException() throws SQLException
	{
		when(connMock.createStatement()).thenReturn(statementMock);
		when(statementMock.executeQuery(any(String.class))).thenThrow(SQLException.class);
		conn.loginSQL();
		conn.setCON(connMock);
		conn.selectSingleSettingBiggerThan("nope", "nothing", "to see here");
	}

	@Test
	public void selectSettingBiggerThan() throws SQLException
	{
		when(statementMock.executeQuery(any(String.class))).thenReturn(resultSetMock);
		when(connMock.createStatement()).thenReturn(statementMock);
		conn.loginSQL();
		conn.setCON(connMock);
		conn.selectSingleSettingBiggerThan("nope", "nothing", "to see here");
	}
	
	@Test(expected = SQLException.class) 
	public void slaveAddressSQLException() throws SQLException
	{
		when(connMock.createStatement()).thenThrow(SQLException.class);
		conn.loginSQL();
		conn.setCON(connMock);
		conn.slaveAddress();
	}
	
	@Test
	public void slaveAddress() throws SQLException, InterruptedException
	{
		when(resultSetMock.next()).thenReturn(true);
		when(resultSetMock.getString(any(String.class))).thenReturn("HELLO");
		when(statementMock.executeQuery(any(String.class))).thenReturn(resultSetMock);
		when(connMock.createStatement()).thenReturn(statementMock);
		
		conn.loginSQL();
		conn.setCON(connMock);
		String output = conn.slaveAddress();
		assertEquals(output, "HELLO");
	}

	
	
}
