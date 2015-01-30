package com.neuron.api.components;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.components.dal.AbstractDAOFactory;
import com.neuron.api.components.dal.DAOFactoryProducer;
import com.neuron.api.components.dal.DeviceDAO;
import com.neuron.api.connectors.ConnectorFactoryImpl;
import com.neuron.api.data.ConnectorConfiguration;
import com.neuron.api.data.DatabaseConfiguration;

/**
 * Any application will extend this abstract class and
 * register their implementation details. Once a data
 * access object has been registered, along with any
 * supported messenger class names, setup can be called.
 * 
 * @author JC
 *
 */
public abstract class Application {

	private static final Logger log = Logger.getLogger(Application.class
			.getName());
	
	private String databaseClassName;
	private List<String> messengerClassNames;
	
	/**
	 * Initialises the collections
	 */
	public Application() {
		messengerClassNames = new ArrayList<String>();
	}
	
	/**
	 * Registers the ONLY data access object class name.
	 * Any subsequent calls to this will overwrite the
	 * original class name.
	 * @param classname The classname of the data access object
	 */
	protected void registerDAOClassName(String classname) {
		databaseClassName = classname;
	}
	
	/**
	 * Register a new messenger class name. Multiple class
	 * names can be provided for each implementation. Each
	 * implementation should ideally be for a single protocol.
	 * @param classname The classname of the messenger
	 */
	protected void registerMessengerClassName(String classname) {
		messengerClassNames.add(classname);
	}
	
	/**
	 * Loads the configuration file and attempts to register
	 * the implementation details with the relevant system
	 * components.
	 * @return boolean If setup was successful;
	 */
	protected boolean setup() {
		
		if(databaseClassName == null || messengerClassNames.isEmpty()) {
			log.log(Level.WARNING, "You must register a data access "
					+ "object implementation class name and atleast 1 "
					+ "messenger implementation class name. Stopping"
					+ "setup now");
			return false;
		}
		
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
			log.log(Level.WARNING, "Failed to locate config.properties file, stopping setup now.");
			return false;
		} catch (IOException e) {
			log.log(Level.WARNING, "Failed to read config.properties file, stopping setup now.");
			return false;
		} finally {
			if(input != null)
				try {
					input.close();
				} catch (IOException e) {
					log.log(Level.WARNING, "Failed to close config.properties file");
				}
		}
		
		log.log(Level.INFO, "Launching application with the following properties:\n"
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
		DatabaseConfiguration databaseConfig;
		try {
			databaseConfig = new DatabaseConfiguration(
					databaseHost,
					databasePort,
					databaseType,
					databaseName,
					Class.forName(databaseClassName));
			deviceFactory.registerDAO(databaseConfig);
		} catch (ClassNotFoundException e1) {
			log.log(Level.WARNING, "Failed to locate class for provided dao class name, stopping setup now", e1);
			return false;
		}
		
		/**
		 * Load Connector factory with all supported protocol types 
		 */
		ConnectorFactoryImpl connectorFactory = new ConnectorFactoryImpl();
		for(String messengerClassName : messengerClassNames) {
			ConnectorConfiguration brokerConfig;
			try {
				brokerConfig = new ConnectorConfiguration(
						brokerHost,
						brokerPort,
						brokerType,
						Class.forName(messengerClassName));
				connectorFactory.registerConnector(brokerConfig);
			} catch (ClassNotFoundException e) {
				log.log(Level.WARNING, "Failed to find class for provided messenger class name, stopping setup now", e);
				return false;
			}	
		}
		
		/**
		 * Clear any stale devices left in database from previous runs
		 */
		DeviceDAO deviceDAO = deviceFactory.getDeviceDAO();
		deviceDAO.clear();
		
		log.log(Level.INFO, "Succesfully setup new application");
		return true;
		
	}
	
}
