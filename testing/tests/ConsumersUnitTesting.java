package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.*;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.JsonParser;

import connections.Connection;
import connections.ConnectionHost;
import connections.DataRequester;
import consumers.Consumer;
import consumers.ConsumerBlue;
import consumers.ConsumerHostRelay;
import consumers.ConsumerRelay;
import main.Controller;


@RunWith(MockitoJUnitRunner.Silent.class)
/**
 * Tests board behavior.
 * @author s164166
 */
public class ConsumersUnitTesting
{	
	//@Mock
	Connection connMock;
	Controller contMock;
	ConnectionHost connHostMock;
	
	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		
		connMock = mock(Connection.class);
		contMock = mock(Controller.class);
		connHostMock = mock(ConnectionHost.class);
	}
	
	
	//Consumer
	@Test
	public void triggerSQLException1() throws SQLException
	{
		when(connMock.takeFromQueue()).thenReturn("{BER:1}");
		Mockito.doThrow(SQLException.class).when(contMock).dataToServer(any(String.class), any(String.class));
		
		Consumer c = new Consumer("test", connMock, contMock);
		c.consume();
	}
	@Test
	public void triggerSQLException2() throws SQLException
	{
		when(connMock.takeFromQueue()).thenReturn("{RES:1}");
		Mockito.doThrow(SQLException.class).when(contMock).settingToServer(any(String.class), any(String.class));
		
		Consumer c = new Consumer("test", connMock, contMock);
		c.consume();
	}
	@Test 
	public void BERConsumed()
	{
		when(connMock.takeFromQueue()).thenReturn("{BER:1}");
		
		Consumer c = new Consumer("test", connMock, contMock);
		c.consume();
	}
	@Test 
	public void ERRConsumed()
	{
		when(connMock.takeFromQueue()).thenReturn("{ERR:1}");
		
		Consumer c = new Consumer("test", connMock, contMock);
		c.consume();
	}
	@Test 
	public void SYNConsumed()
	{
		when(connMock.takeFromQueue()).thenReturn("{SYN:1}");
		
		Consumer c = new Consumer("test", connMock, contMock);
		c.consume();
	}
	@Test 
	public void UTIConsumed()
	{
		when(connMock.takeFromQueue()).thenReturn("{UTI:1}");
		
		Consumer c = new Consumer("test", connMock, contMock);
		c.consume();
	}
	@Test 
	public void MODConsumed()
	{
		when(connMock.takeFromQueue()).thenReturn("{MOD:1}");
		
		Consumer c = new Consumer("test", connMock, contMock);
		c.consume();
	}
	@Test 
	public void FPSConsumed()
	{
		when(connMock.takeFromQueue()).thenReturn("{FPS:1}");
		
		Consumer c = new Consumer("test", connMock, contMock);
		c.consume();
	}
	@Test 
	public void VCLConsumed()
	{
		when(connMock.takeFromQueue()).thenReturn("{VCL:1}");
		
		Consumer c = new Consumer("test", connMock, contMock);
		c.consume();
	}
	@Test 
	public void RESConsumed()
	{
		when(connMock.takeFromQueue()).thenReturn("{RES:1}");
		
		Consumer c = new Consumer("test", connMock, contMock);
		c.consume();
	}
	@Test
	public void inputOutOfBounds()
	{
		when(connMock.takeFromQueue()).thenReturn("AmIBreakingSomething?");
		
		Consumer c = new Consumer("test", connMock, contMock);
		c.consume();
	}
	@Test 
	public void emptyInbound()
	{
		when(connMock.takeFromQueue()).thenReturn("");
		
		Consumer c = new Consumer("test", connMock, contMock);
		c.consume();
	}
	@Test 
	public void nullInbound()
	{
		when(connMock.takeFromQueue()).thenReturn(null);
		
		Consumer c = new Consumer("test", connMock, contMock);
		c.consume();
	}
	
	//DataRequester
	@Test 
	public void dataRequester()
	{
		DataRequester d = new DataRequester("test", connMock);
	}
	
	//ConsumerBlue
	@Test 
	public void nullInboundBlue()
	{
		when(connMock.takeFromQueue()).thenReturn(null);
		
		ConsumerBlue c = new ConsumerBlue("test", connHostMock, contMock, connMock, connMock);
		c.consumeBlue();
	}
	@Test 
	public void emptyInboundBlue()
	{
		when(connMock.takeFromQueue()).thenReturn("");
		
		
		ConsumerBlue c = new ConsumerBlue("test", connHostMock, contMock, connMock, connMock);
		c.consumeBlue();
	}
	@Test 
	public void triggerIllegalStateException()
	{
		when(connHostMock.takeFromQueue()).thenReturn("hello");
		
		
		ConsumerBlue c = new ConsumerBlue("test", connHostMock, contMock, connMock, connMock);
		c.consumeBlue();
	}
	@Test 
	public void triggerNullPointerException()
	{
		when(connHostMock.takeFromQueue()).thenReturn("{\"Derp:\":[[\"test\"],[]]}");
		ConsumerBlue c = new ConsumerBlue("test", connHostMock, contMock, connMock, connMock);
		c.consumeBlue();
	}
	@Test 
	public void setSettingNotLoggedIn()
	{
		when(contMock.getLoggedIn()).thenReturn(false);
		when(connHostMock.takeFromQueue()).thenReturn("{\"request\":\"setSetting\", \"setting\":\"MOD\", \"value\":\"1\"}");
		ConsumerBlue c = new ConsumerBlue("test", connHostMock, contMock, connMock, connMock);
		c.consumeBlue();
	}
	@Test 
	public void setSettingFPSLoggedIn()
	{
		when(contMock.getLoggedIn()).thenReturn(true);
		when(connHostMock.takeFromQueue()).thenReturn("{\"request\":\"setSetting\", \"setting\":\"FPS\", \"value\":\"1\"}");
		ConsumerBlue c = new ConsumerBlue("test", connHostMock, contMock, connMock, connMock);
		c.consumeBlue();
	}
	@Test 
	public void setSettingVCLLoggedIn()
	{
		when(contMock.getLoggedIn()).thenReturn(true);
		when(connHostMock.takeFromQueue()).thenReturn("{\"request\":\"setSetting\", \"setting\":\"VCL\", \"value\":\"1\"}");
		ConsumerBlue c = new ConsumerBlue("test", connHostMock, contMock, connMock, connMock);
		c.consumeBlue();
	}
	@Test 
	public void setSettingRESLoggedIn()
	{
		when(contMock.getLoggedIn()).thenReturn(true);
		when(connHostMock.takeFromQueue()).thenReturn("{\"request\":\"setSetting\", \"setting\":\"RES\", \"value\":\"1\"}");
		ConsumerBlue c = new ConsumerBlue("test", connHostMock, contMock, connMock, connMock);
		c.consumeBlue();
	}
	@Test 
	public void setSettingMODLoggedIn()
	{
		when(contMock.getLoggedIn()).thenReturn(true);
		when(connHostMock.takeFromQueue()).thenReturn("{\"request\":\"setSetting\", \"setting\":\"MOD\", \"value\":\"1\"}");
		ConsumerBlue c = new ConsumerBlue("test", connHostMock, contMock, connMock, connMock);
		c.consumeBlue();
	}
	@Test 
	public void setSettingDefaultingLoggedIn()
	{
		when(contMock.getLoggedIn()).thenReturn(true);
		when(connHostMock.takeFromQueue()).thenReturn("{\"request\":\"setSetting\", \"setting\":\"yoMama\", \"value\":\"1\"}");
		ConsumerBlue c = new ConsumerBlue("test", connHostMock, contMock, connMock, connMock);
		c.consumeBlue();
	}
	@Test 
	public void login()
	{
		when(contMock.getLoggedIn()).thenReturn(true);
		when(connHostMock.takeFromQueue()).thenReturn("{\"request\":\"LoginInformation\", \"username\":\"MOD\", \"password\":\"1\"}");
		ConsumerBlue c = new ConsumerBlue("test", connHostMock, contMock, connMock, connMock);
		c.consumeBlue();
	}
	@Test 
	public void getErrors()
	{
		when(contMock.getLoggedIn()).thenReturn(true);
		when(connHostMock.takeFromQueue()).thenReturn("{\"request\":\"getErrors\", \"date\":\"hello\"}");
		ConsumerBlue c = new ConsumerBlue("test", connHostMock, contMock, connMock, connMock);
		c.consumeBlue();
	}
	@Test 
	public void update()
	{
		when(contMock.getLoggedIn()).thenReturn(true);
		when(connHostMock.takeFromQueue()).thenReturn("{\"request\":\"update\", \"date\":\"hello\"}");
		ConsumerBlue c = new ConsumerBlue("test", connHostMock, contMock, connMock, connMock);
		c.consumeBlue();
	}
	
	
	
	
	//ConsumerHostRelay
	@Test 
	public void nullInboundHostRelay()
	{
		when(connHostMock.takeFromQueue()).thenReturn(null);
		ConsumerHostRelay c = new ConsumerHostRelay("test", connHostMock, connMock, connMock);
		c.consumeHost();
	}
	@Test 
	public void emptyInboundHostRelay()
	{
		when(connHostMock.takeFromQueue()).thenReturn("");
		ConsumerHostRelay c = new ConsumerHostRelay("test", connHostMock, connMock, connMock);
		c.consumeHost();
	}
	@Test
	public void inputOutOfBoundsConsumerHost()
	{
		when(connHostMock.takeFromQueue()).thenReturn("AmIBreakingSomething?");
		ConsumerHostRelay c = new ConsumerHostRelay("test", connHostMock, connMock, connMock);
		c.consumeHost();
	}
	@Test 
	public void BERConsumedConsumerHost()
	{
		when(connHostMock.takeFromQueue()).thenReturn("{BER:1}");
		ConsumerHostRelay c = new ConsumerHostRelay("test", connHostMock, connMock, connMock);
		c.consumeHost();
	}
	@Test 
	public void ERRConsumedConsumerHost()
	{
		when(connHostMock.takeFromQueue()).thenReturn("{ERR:1}");
		ConsumerHostRelay c = new ConsumerHostRelay("test", connHostMock, connMock, connMock);
		c.consumeHost();
	}
	@Test 
	public void SYNConsumedConsumerHost()
	{
		when(connHostMock.takeFromQueue()).thenReturn("{SYN:1}");
		ConsumerHostRelay c = new ConsumerHostRelay("test", connHostMock, connMock, connMock);
		c.consumeHost();
	}
	@Test 
	public void UTIConsumedConsumerHost()
	{
		when(connHostMock.takeFromQueue()).thenReturn("{UTI:1}");
		ConsumerHostRelay c = new ConsumerHostRelay("test", connHostMock, connMock, connMock);
		c.consumeHost();
	}
	@Test 
	public void MODConsumedConsumerHost()
	{
		when(connHostMock.takeFromQueue()).thenReturn("{MOD:1}");
		ConsumerHostRelay c = new ConsumerHostRelay("test", connHostMock, connMock, connMock);
		c.consumeHost();
	}
	@Test 
	public void FPSConsumedConsumerHost()
	{
		when(connHostMock.takeFromQueue()).thenReturn("{FPS:1}");
		ConsumerHostRelay c = new ConsumerHostRelay("test", connHostMock, connMock, connMock);
		c.consumeHost();
	}
	@Test 
	public void VCLConsumedConsumerHost()
	{
		when(connHostMock.takeFromQueue()).thenReturn("{VCL:1}");
		ConsumerHostRelay c = new ConsumerHostRelay("test", connHostMock, connMock, connMock);
		c.consumeHost();
	}
	@Test 
	public void RESConsumedConsumerHost()
	{
		when(connHostMock.takeFromQueue()).thenReturn("{RES:1}");
		ConsumerHostRelay c = new ConsumerHostRelay("test", connHostMock, connMock, connMock);
		c.consumeHost();
	}
	@Test 
	public void ALLConsumedConsumerHost()
	{
		when(connHostMock.takeFromQueue()).thenReturn("{ALL:1}");
		ConsumerHostRelay c = new ConsumerHostRelay("test", connHostMock, connMock, connMock);
		c.consumeHost();
	}
	@Test 
	public void DATConsumedConsumerHost()
	{
		when(connHostMock.takeFromQueue()).thenReturn("{DAT:1}");
		ConsumerHostRelay c = new ConsumerHostRelay("test", connHostMock, connMock, connMock);
		c.consumeHost();
	}
	@Test 
	public void REQConsumedConsumerHost()
	{
		when(connHostMock.takeFromQueue()).thenReturn("{REQ:RES}");
		ConsumerHostRelay c = new ConsumerHostRelay("test", connHostMock, connMock, connMock);
		c.consumeHost();
	}
	
	//ConsumerRelay
	@Test 
	public void nullInboundConsumerRelay()
	{
		when(connMock.takeFromQueue()).thenReturn(null);
		ConsumerRelay c = new ConsumerRelay("test", connMock, connHostMock);
		c.consumeRelay();
	}
	@Test 
	public void emptyInboundConsumerRelay()
	{
		when(connMock.takeFromQueue()).thenReturn("");
		ConsumerRelay c = new ConsumerRelay("test", connMock, connHostMock);
		c.consumeRelay();
	}
	@Test 
	public void anyInboundConsumerRelay()
	{
		when(connMock.takeFromQueue()).thenReturn("HelloWorld");
		ConsumerRelay c = new ConsumerRelay("test", connMock, connHostMock);
		c.consumeRelay();
	}
	
	
	
}
