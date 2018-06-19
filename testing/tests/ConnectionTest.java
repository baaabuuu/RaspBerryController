package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import connections.Connection;
import log.Log;


@RunWith(MockitoJUnitRunner.class)
/**
 * Tests board behavior.
 * @author s164154 Emil Haugaard
 */
public class ConnectionTest
{	

	//@Mock
	Socket sock; 
	BufferedReader buffRead;
	OutputStream out;
	OutputStreamWriter outW;
	BufferedWriter outBW;
	ArrayBlockingQueue<String> inboundQueue;
	ArrayBlockingQueue<String> outboundQueue;

	@SuppressWarnings("unchecked")
	@Before
	public void setupBoard()
	{
		MockitoAnnotations.initMocks(this);
		sock = mock(Socket.class);
		buffRead = mock(BufferedReader.class);
		out = mock(OutputStream.class);
		outW = mock(OutputStreamWriter.class);
		outBW = mock(BufferedWriter.class);
		inboundQueue = mock(ArrayBlockingQueue.class);
		outboundQueue = mock(ArrayBlockingQueue.class);
	}
	
	@Test(expected=NullPointerException.class)
	public void testNullInput1() {
		Semaphore semaphore = new Semaphore(1);
		new Connection(null,semaphore,"1",1);
	}
	
	@Test(expected=NullPointerException.class)
	public void testNullInput2() {
		new Connection("1",null,"1",1);
	}
	
	@Test(expected=NullPointerException.class)
	public void testNullInput3() {
		Semaphore semaphore = new Semaphore(1);
		new Connection("1",semaphore,null,1);
	}
	
	@Test
	public void testContructer(){
		Semaphore semaphore = new Semaphore(1);
		Connection m1 = new Connection("Test",semaphore,"127.0.0.1",7171);
		Connection m3 = new Connection("Test",semaphore,"127.0.0.1",7070);
	
	    assertNotEquals(m1, m3);
	    assertNotEquals(m3, m1);
	    assertNotEquals(m1, null);
	}
	
	@Test
	public void testSocketClientException(){
		when(sock.isConnected()).thenThrow(IOException.class);
		Semaphore semaphore = new Semaphore(1);
		Connection test = new Connection("testInvalidInput", semaphore,"a",1);
		test.runningConnection(sock,buffRead,out,outW,outBW,inboundQueue,outboundQueue);
	}
	
	@Test
	public void testSocketClient() throws IOException{
	
		when(sock.isConnected()).thenReturn(true).thenReturn(false);
		when(buffRead.ready()).thenReturn(true);
		when(outboundQueue.isEmpty()).thenReturn(false);
		
		Semaphore semaphore = new Semaphore(1);
		Connection test = new Connection("", semaphore,"a",1);
		
		
		test.runningConnection(sock,buffRead,out,outW,outBW,inboundQueue,outboundQueue);
		
		when(sock.isConnected()).thenReturn(true).thenReturn(false);
		when(buffRead.ready()).thenReturn(false);
		when(outboundQueue.isEmpty()).thenReturn(true);
		
		test.runningConnection(sock,buffRead,out,outW,outBW,inboundQueue,outboundQueue);
	}
	
	@Test
	public void testTakeFromQueue() throws InterruptedException, IOException{
		when(sock.isConnected()).thenReturn(true).thenReturn(false);
		when(buffRead.ready()).thenReturn(true);
		when(outboundQueue.isEmpty()).thenReturn(true);
		when(buffRead.readLine()).thenReturn("test");
		
		Semaphore semaphore = new Semaphore(1);
		Connection test = new Connection("", semaphore,"",1);
		
		test.runningConnection(sock,buffRead,out,outW,outBW,inboundQueue,outboundQueue);
		
		test.takeFromQueue();
		
	}
	
	@Test 
	public void testPutToQueue() {
		Semaphore semaphore = new Semaphore(1);
		Connection test = new Connection("", semaphore,"",1);
		
		test.putToQueue("");
	}
	
	
	/*@Test(expected=InterruptedException.class)
	public void testTakeFromQueueException(){
		when(inboundQueue.isEmpty()).thenThrow(InterruptedException.class);
		
		Semaphore semaphore = new Semaphore(1);
		Connection test = new Connection("", semaphore,"",1);
		test.setOutboundInbound(20, 20);
		
		test.takeFromQueue();
	}*/
	
	
}
