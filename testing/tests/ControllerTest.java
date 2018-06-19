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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.mockito.junit.MockitoJUnitRunner;

import log.Log;
import main.Controller;
import sql.SQLConnection;


@RunWith(MockitoJUnitRunner.Silent.class)
/**
 * SQLConnection behavior
 * A little slow due to using specific behavior
 * @author s164166
 */
public class ControllerTest
{	
	Controller control;
	//@Mock
	SQLConnection sqlcMock;
	ResultSet resultSetMock;
	SimpleDateFormat dateTimeFormatMock;
	SimpleDateFormat dateFormatMock;
	SimpleDateFormat timeFormatMock;
	Date dateMock;

	
	@Before
	public void setupStuff()
	{
		control = new Controller();
		MockitoAnnotations.initMocks(this);
		resultSetMock = mock(ResultSet.class);
		sqlcMock = mock(SQLConnection.class);
		dateMock= mock(Date.class);
		timeFormatMock = mock(SimpleDateFormat.class);
		dateFormatMock = mock(SimpleDateFormat.class);
		dateTimeFormatMock = mock(SimpleDateFormat.class);
	}

	
	@Test
	public void receiveAndSetSlaveAddress() throws SQLException
	{
		when(sqlcMock.slaveAddress()).thenReturn("fish");
		control.setServer(sqlcMock);
		String output = control.receiveAndSetSlaveAddress();
		assertEquals(output, "fish");
	}
	
	@Test
	public void loginToServerTrue() throws SQLException
	{
		when(sqlcMock.loginToServer(any(String.class), any(String.class))).thenReturn(true);
		control.setServer(sqlcMock);
		String output = control.loginToServer("fish", "fish");
		assertEquals(output, "LOGINOK");
	}
	
	@Test
	public void loginToServerFalse() throws SQLException
	{
		when(sqlcMock.loginToServer(any(String.class), any(String.class))).thenReturn(false);
		control.setServer(sqlcMock);
		String output = control.loginToServer("fish", "fish");
		assertEquals(output, "LOGINNO");
	}
	
	@Test 
	public void loginToServerException() throws SQLException
	{
		control.setServer(sqlcMock);
		when(sqlcMock.loginToServer(any(String.class), any(String.class))).thenThrow(SQLException.class);
		String output = control.loginToServer("fish", "fish");
		assertEquals(output, "Error");
	}
	
	@Test 
	public void dataToServerNoException() throws SQLException
	{
		control.setServer(sqlcMock);
		control.dataToServer("fish", "fish");
	}
	
	@Test(expected = SQLException.class) 
	public void dataToServerException() throws SQLException
	{
		Mockito.doThrow(SQLException.class).when(sqlcMock).dataToServer(any(String.class), any(String.class));
		control.setServer(sqlcMock);
		control.dataToServer("fish", "fish");
	}
	
	@Test 
	public void settingToServerNoException() throws SQLException
	{
		control.setServer(sqlcMock);
		control.settingToServer("fish", "fish");
	}
	
	@Test(expected = SQLException.class) 
	public void settingToServerException() throws SQLException
	{
		Mockito.doThrow(SQLException.class).when(sqlcMock).settingToServer(any(String.class), any(String.class));
		control.setServer(sqlcMock);
		control.settingToServer("fish", "fish");
	}
	
	@Test 
	public void logToServerNoException() throws SQLException
	{
		control.setServer(sqlcMock);
		control.logToServer("fish");
	}
	
	@Test 
	public void logToServerException() throws SQLException
	{
		Mockito.doThrow(SQLException.class).when(sqlcMock).logToServer(any(String.class), any(String.class));
		control.setServer(sqlcMock);
		control.logToServer( "fish");
	}
	
	@Test //TODO
	public void createJson() throws SQLException, ParseException
	{	
		when(resultSetMock.next()).thenReturn(true).thenReturn(true).thenReturn(false);
		when(resultSetMock.getString(any(String.class))).thenReturn("");

		control.createJson(resultSetMock);
	}

	
	
}
