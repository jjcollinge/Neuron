package com.thing.messaging;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


public class SerialMessanger extends Messanger implements SerialPortEventListener {

	private static final Logger log = Logger.getLogger( SerialMessanger.class.getName() );
	
	private Stack<Character> characterBuffer;
	
	SerialPort serialPort;
  
    /** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
			"/dev/tty.usbserial-A9007UX1", 	// Mac OS X
	                    "/dev/ttyACM0", 	// Raspberry Pi
			"/dev/ttyUSB0", 				// Linux
			"COM3", 						// Windows
	};
	
	/** The input stream to the port */
	private BufferedReader input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int BAUD_RATE = 9600;
	
	private ArrayList<String> subscriptions;
	private boolean connected = false;
	
	public SerialMessanger(int id) {
		
		super("SERIAL", id);
		subscriptions = new ArrayList<String>();
		characterBuffer = new Stack<Character>();
	}
	
	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
		connected = false;
	}
	
	@Override
	public void connect(String host, String port) {
		// the next line is for Raspberry Pi and 
        // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
        System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		
		//First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			log.log(Level.WARNING, "Couldn't not find the COM port for the Serial device");
			return;
		}
		
		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);
		
			// set port parameters
			serialPort.setSerialPortParams(BAUD_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
		
			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();
		
			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			
			connected = true;
			log.log(Level.INFO, "Successfully connected to Serial device");
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	@Override
	public void send(MessagePayload message) {
		
		if(message.getData() == null || message.getTopic() == null) {
			log.log(Level.WARNING, "Message not sent, message payload was not complete");
			return;
		}
		if(!connected) {
			log.log(Level.WARNING, "Message not sent because the messanger is not connected");
			return;
		}
		
		try {
			String jsonMessage = null;
			try {
				Gson gson = new Gson();
				jsonMessage = gson.toJson(message);
			} catch(JsonSyntaxException e) {
				log.log(Level.SEVERE, "Couldn't transform out going message to JSON format", e);
				return;
			}
			output.write(jsonMessage.getBytes());
			output.write('\n');
			log.log(Level.INFO, "Sent message " + jsonMessage);
		} catch (IOException e) {
			log.log(Level.SEVERE, "An exception has been thrown", e);
			connected = false;
		}
		
	}

	@Override
	public void subscribe(String topic, int qos) {
		this.subscriptions.add(topic);
	}

	@Override
	public void unsubscribe(String topic) {
		this.subscriptions.remove(topic);
	}

	@Override
	public boolean isConnected() {
		return connected;
	}
	
	private synchronized String readSerialStream() {
		int c;
		StringBuilder sb = new StringBuilder();
		
		while(!this.characterBuffer.isEmpty()) {
			sb.append(this.characterBuffer.pop());
		}

		try {
			while ((c = input.read()) != -1) {
				if(c == '\n' || c == '\r') {
					// Terminators
					int next = input.read();
					while((next == '\n') || (next == '\r')) { 
						// continue to read and drop any additional new lines or carriage returns...
						if(input.ready()) {
							next = input.read();
						} else {
							break;
						}
					}
					if(next != '\n' && next != '\r') {
						this.characterBuffer.push((char)next);
					}
					// finally
					break;
				} else {
					// Valid character
					sb.append( (char)c ); 
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public void serialEvent(SerialPortEvent e) {
		
		if (e.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			
			String msg = readSerialStream();
			if(msg.isEmpty()) return;
			
			boolean forward = false;
			
			msg = "{ \"messangerId\": " + this.getId() + ", \"payload\": " + msg + " }";
			
			/** WARNING : Will log all serial correspondance **/
			log.log(Level.INFO, msg);
			Message message = null;
			
			try {
				Gson gson = new Gson();
				message = gson.fromJson(msg, Message.class);
			} catch(JsonSyntaxException e1) {
				log.log(Level.FINEST, "Couldn't transform incoming SERIAL message to internal message");
				return;
			}
			
			String topic = message.getPayload().getTopic();
			for(String subscription : this.subscriptions) {
				if(topic.equals(subscription)){
					forward = true;
				}
			}
			
			if(forward) {
				log.log(Level.INFO, "Forwarding message");
				MessageEvent event = new MessageEvent(message);
				this.fireChangeEvent(event);
			}
		
		}
		
	}

}