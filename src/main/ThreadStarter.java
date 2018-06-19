package main;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import connections.Connection;
import connections.ConnectionHost;
import connections.DataRequester;
import consumers.Consumer;
import consumers.ConsumerBlue;
import consumers.ConsumerHostRelay;
import consumers.ConsumerRelay;

public class ThreadStarter {
	
	Controller controller;
	
	private String nitroAddress = "127.0.0.1";
	private String de1socAddress = "192.168.1.123";
	private String slaveAddress;
	private int nitroPort = 7171;
	private int de1socPort = 7070;
	private int bluetoothPort = 2020;
	private int netMonPort = 1337;
	private int raspPort = 6789;
	
	private String de1socKey1 = "{KEY:1337}\0";
	//private String de1socKey2 = "{KEY:2424}\0";
	//private String de1socKey3 = "{KEY:9090}\0";
	
	public Semaphore semaphore = new Semaphore(1);
	
	private ConnectionHost networkMonitoringHostInboundOnly;
	
	public void startThreads(String slaveAddress, Controller controller) throws InterruptedException {
		this.slaveAddress = slaveAddress;
		this.controller = controller;
		
		networkMonitoringHostInboundOnly = new ConnectionHost("networkMonitoringHostInboundOnly", netMonPort);
		networkMonitoringHostInboundOnly.start();
		
		startRaspRelay();
		//Delays is added, as the de1soc doens't like it when multiple connections are made at the same time.
		TimeUnit.SECONDS.sleep(3);
		startArduinoRelay1();
		TimeUnit.SECONDS.sleep(3);
		startArduinoRelay2();
	}
	private void startRaspRelay()
	{
		
		Connection nitrogenConnect = new Connection("nitrogenConnect", semaphore, nitroAddress, nitroPort);
		Consumer nitrogenConsume = new Consumer("nitrogenConsume", nitrogenConnect, controller);
		
		Connection de1socConnect = new Connection("de1socConnect", semaphore, de1socAddress, de1socPort);
		Consumer de1socConsume = new Consumer("de1socConsume", de1socConnect, controller);
		DataRequester de1socRequest = new DataRequester("de1socRequest", de1socConnect);
		
		ConnectionHost bluetoothConnect = new ConnectionHost("bluetoothConnect", bluetoothPort);
		ConsumerBlue bluetoothConsume = new ConsumerBlue("bluetoothConsume", bluetoothConnect, controller, nitrogenConnect, de1socConnect);
		

		
		nitrogenConnect.start();
		nitrogenConsume.start();
		
		de1socConnect.putToQueue(de1socKey1);
		de1socConnect.start();
		de1socConsume.start();
		de1socRequest.start();
		
		bluetoothConnect.start();
		bluetoothConsume.start();
		
	}
	
	private void startArduinoRelay1() {

		ConnectionHost arduinoConnect = new ConnectionHost("arduinoConnect (1)", 6161);
		Connection de1socConnect = new Connection("de1socConnect (1)", semaphore, de1socAddress, de1socPort);
		Connection nitroConnect = new Connection("nitroConnect", semaphore, nitroAddress, nitroPort);
		
		ConsumerHostRelay hostConsumer = new ConsumerHostRelay("Consumer (1)", arduinoConnect, nitroConnect, de1socConnect);
		ConsumerRelay de1socConsumer = new ConsumerRelay("de1socConsumer (1)", de1socConnect, arduinoConnect);
		ConsumerRelay nitroConsumer = new ConsumerRelay("nitroConsumer (1)", nitroConnect, arduinoConnect);
		
		Connection slaveConnect = new Connection("slaveConnect (1)", semaphore, slaveAddress, raspPort);
		ConsumerRelay slaveConsume = new ConsumerRelay("slaveConsume (1)", slaveConnect, arduinoConnect);
		
		
		arduinoConnect.start();
		
		de1socConnect.start();
		nitroConnect.start();
		
		hostConsumer.start();
		de1socConsumer.start();
		nitroConsumer.start();
		
		slaveConnect.start();
		slaveConsume.start();
	}
	private void startArduinoRelay2() {
		
		ConnectionHost arduinoConnect = new ConnectionHost("arduinoConnect (2)", 6161);
		Connection de1socConnect = new Connection("de1socConnect (2)", semaphore, de1socAddress, de1socPort);
		Connection nitroConnect = new Connection("nitroConnect", semaphore, nitroAddress, nitroPort);
		
		ConsumerHostRelay hostConsumer = new ConsumerHostRelay("Consumer (2)", arduinoConnect, nitroConnect, de1socConnect);
		ConsumerRelay de1socConsumer = new ConsumerRelay("de1socConsumer (2)", de1socConnect, arduinoConnect);
		ConsumerRelay nitroConsumer = new ConsumerRelay("nitroConsumer (2)", nitroConnect, arduinoConnect);
		
		Connection slaveConnect = new Connection("slaveConnect (2)", semaphore, slaveAddress, raspPort);
		ConsumerRelay slaveConsume = new ConsumerRelay("slaveConsume (2)", slaveConnect, arduinoConnect);
		
		
		arduinoConnect.start();
		
		de1socConnect.start();
		nitroConnect.start();
		
		hostConsumer.start();
		de1socConsumer.start();
		nitroConsumer.start();
		
		slaveConnect.start();
		slaveConsume.start();
	}
}
