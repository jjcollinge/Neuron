package com.neuron.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.components.dal.AbstractDAOFactory;
import com.neuron.api.components.dal.DAOFactoryProducer;
import com.neuron.api.components.dal.DeviceDAO;
import com.neuron.api.components.services.ServiceContainer;
import com.neuron.api.data.ConnectorConfiguration;
import com.neuron.api.data.DatabaseConfiguration;
import com.neuron.api.data.ServiceConfiguration;
import com.neuron.connectors.ConnectorFactoryImpl;
import com.neuron.dal.MongoDBDeviceDAO;
import com.neuron.messaging.MqttMessenger;
import com.neuron.registration.RegistrationController;
import com.neuron.sessions.SessionController;

public class NueronApp {

	private static final Logger log = Logger.getLogger(NueronApp.class
			.getName());
	
	private static class Application implements Runnable {
		
		/**
		 * Application startup sequence
		 */
		public void run() {
			
			/**
			 * Load config.properties file to get key value pairs for system
			 * configuration.
			 */
			Properties prop = new Properties();
			String databaseHost = "";
			int databasePort = 0;
			String databaseType = "";
			String databaseName = "";
			String brokerHost = "";
			int brokerPort = 0;
			String brokerType = "";
			InputStream input = null;
			
			try {
				input = new FileInputStream("config.properties");
				prop.load(input);
				
				databaseHost = prop.getProperty("database_host");
				databasePort = Integer.valueOf(prop.getProperty("database_port"));
				databaseType = prop.getProperty("database_type");
				databaseName = prop.getProperty("database_name");
				brokerHost = prop.getProperty("broker_host");
				brokerPort = Integer.valueOf(prop.getProperty("broker_port"));
				brokerType = prop.getProperty("broker_type");
				
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
			
			log.log(Level.INFO, "Launching Neuron application with the following properties:\n"
					+ "....................................." + "\n"
					+ "Database:\n"
					+ "database_host: " + databaseHost + "\n"
					+ "database_port: " + databasePort + "\n"
					+ "database_type: " + databaseType + "\n"
					+ "database_name: " + databaseName + "\n"
					+ "Protocols:\n"
					+ "broker_host: " + brokerHost + "\n"
					+ "broker_port: " + brokerPort + "\n"
					+ "broker_type: " + brokerType + "\n"
					+ ".....................................");
			
			/**
			 * Load DAO factory with provided database type
			 */
			AbstractDAOFactory deviceFactory = DAOFactoryProducer.getFactory("device");
			DatabaseConfiguration databaseConfig  = new DatabaseConfiguration(
					databaseHost,
					databasePort,
					databaseType,
					databaseName,
					MongoDBDeviceDAO.class);	// This should be injected at run time
			deviceFactory.registerDAO(databaseConfig);
			
			/**
			 * Load Connector factory with all supported protocol types 
			 */
			ConnectorFactoryImpl connectorFactory = new ConnectorFactoryImpl();
			ConnectorConfiguration brokerConfig  = new ConnectorConfiguration(
					brokerHost,
					brokerPort,
					brokerType,
					MqttMessenger.class);		// This should be injected at run time
			connectorFactory.registerConnector(brokerConfig);
			
			/**
			 * Clear any stale devices left in database from previous runs
			 */
			DeviceDAO deviceDAO = deviceFactory.getDeviceDAO(databaseType);
			deviceDAO.clear();
			
			/**
			 * Create a service configuration informing each service of the 
			 * available protocols and database
			 */
			ServiceConfiguration serviceConfig = new ServiceConfiguration();
			serviceConfig.registerDatabaseType(databaseType);
			serviceConfig.registerConnectorType(brokerType);
			
			/**
			 * Create a service container to handle the setup and tear down of services
			 */
			ServiceContainer container = new ServiceContainer(serviceConfig);	
			container.addService(SessionController.getInstance());
			container.addService(RegistrationController.getInstance());
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
