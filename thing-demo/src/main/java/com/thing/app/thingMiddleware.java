package com.thing.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thing.api.components.ServiceContainer;
import com.thing.api.model.DeviceDAO;
import com.thing.connectors.impl.ConnectorFactoryImpl;
import com.thing.connectors.impl.MqttMessenger;
import com.thing.registration.RegistrationController;
import com.thing.sessions.SessionController;
import com.thing.storage.DeviceDAOFactory;

public class thingMiddleware {

	private static final Logger log = Logger.getLogger(thingMiddleware.class
			.getName());
	
	private static class Application implements Runnable {
		
		/**
		 * Application startup sequence
		 */
		public void run() {
			
			Properties prop = new Properties();
			String databaseHost   = "";
			String databaseName   = "";
			String databaseType   = "";
			String mqttServerHost = "";
			String mqttServerPort = "";
			InputStream input = null;
			
			try {
				input = new FileInputStream("config.properties");
				prop.load(input);
				
				databaseHost = prop.getProperty("database_host");
				databaseName = prop.getProperty("database_name");
				databaseType = prop.getProperty("database_type");
				mqttServerHost = prop.getProperty("mqtt_host");
				mqttServerPort = prop.getProperty("mqtt_port");
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(input != null)
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			
			log.log(Level.INFO, "Launching application with the following properties:\n"
					+ "....................................." + "\n"
					+ "database_host: " + databaseHost + "\n"
					+ "database_name: " + databaseName + "\n"
					+ "database_type: " + databaseType + "\n"
					+ "mqtt_host: " + mqttServerHost + "\n"
					+ "mqtt_port: " + mqttServerPort + "\n"
					+ ".....................................");
			
			DeviceDAOFactory deviceStorageFactory = new DeviceDAOFactory();
			DeviceDAO dao = deviceStorageFactory.getDeviceDAO(databaseType, databaseHost, databaseName);
			
			
			// Set any supported protocol server information
			ConnectorFactoryImpl connectorFactory = new ConnectorFactoryImpl();
			connectorFactory.MQTT_SERVER_HOSTNAME = mqttServerHost;
			connectorFactory.MQTT_SERVER_PORT = Integer.valueOf(mqttServerPort);
			
			ServiceContainer container = new ServiceContainer();	
			container.addService(SessionController.getInstance());
			container.addService(new RegistrationController());
			container.startServices();
			
		}
	}
	
	/**
	 * Main entry point of program, used to read any command line args
	 * and set the relevant properties.
	 * @param args
	 */
	public static void main(String[] args) {
		// Start application thread
		new Thread(new Application()).start();
	}

}
