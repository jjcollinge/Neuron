package com.neuron.api.components;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.components.dal.AbstractDAOFactory;
import com.neuron.api.components.dal.DAOFactoryProducer;
import com.neuron.api.components.dal.DeviceDAO;
import com.neuron.api.connectors.ConnectorFactory;
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
	private List<String> proxyClassNames;
	private Configuration config;
	
	/**
	 * Initialises the collections
	 */
	public Application() {
		messengerClassNames = new ArrayList<String>();
		proxyClassNames = new ArrayList<String>();
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
	 * Register a new proxy class name. Multiple class
	 * names can be provided for each implementation.
	 * Each implementation should ideally be for a single
	 * protocol.
	 * @param classname The classname of the proxy
	 */
	public void registerProxyClassName(String classname) {
		proxyClassNames.add(classname);
	}
	
	/**
	 * Loads the configuration file and attempts to register
	 * the implementation details with the relevant system
	 * components.
	 * @param configFile A file path to the applications configuration file
	 * @return boolean If setup was successful;
	 */
	protected boolean setup(String configFile, String home) {
		
		if(databaseClassName == null || messengerClassNames.isEmpty() || proxyClassNames.isEmpty()) {
			log.log(Level.WARNING, "You must register a data access "
					+ "object implementation class name, atleast 1 "
					+ "messenger implementation class name and atleast"
					+ "1 proxy implementation class name before"
					+ "starting an application. Stopping"
					+ "setup now");
			return false;
		}
			
		/**
		 * Load config file to get key value pairs for system
		 * configuration.
		 */
		
		if(home == null) {
			home = "";
			configFile = "default-config.json";
			log.log(Level.INFO, "Enviroment home not set, using default config");
		}
		
		ConfigurationLoader loader = new ConfigurationLoader();
		config = loader.loadConfiguration(configFile, home);
		
		String databaseHostname = config.getProperty("database_hostname");
		String databasePort = 	  config.getProperty("database_port");
		String databaseType = 	  config.getProperty("database_type");
		String databaseName = 	  config.getProperty("database_name");
		String brokerHostname =   config.getProperty("broker_host");
		String brokerPort =       config.getProperty("broker_port");
		String brokerType = 	  config.getProperty("broker_type");
		InputStream input = null;
		
		log.log(Level.INFO, "Launching application with the following properties:\n"
				+ "....................................." + "\n"
				+ "Database:\n"
				+ "database_host: " + databaseHostname + "\n"
				+ "database_port: " + databasePort + "\n"
				+ "database_type: " + databaseType + "\n"
				+ "database_name: " + databaseName + "\n"
				+ "Protocols:\n"
				+ "broker_host: " + brokerHostname + "\n"
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
					databaseHostname,
					Integer.valueOf(databasePort),
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
		 * n.b. would need to change for multiple protocols
		 */
		ConnectorFactory connectorFactory = new ConnectorFactory();
		for(String messengerClassName : messengerClassNames) {
			ConnectorConfiguration brokerConfig;
			try {
				brokerConfig = new ConnectorConfiguration(
						brokerHostname,
						Integer.valueOf(brokerPort),
						brokerType,
						Class.forName(messengerClassName));
				connectorFactory.registerConnector(brokerConfig);
			} catch (ClassNotFoundException e) {
				log.log(Level.WARNING, "Failed to find class for provided messenger class name, stopping setup now", e);
				return false;
			}	
		}
		
		/**
		 * Load Proxy factory with all supported protocol types
		 */
		DeviceProxyFactory proxyFactory = new DeviceProxyFactory();
		for(String proxyClassName : proxyClassNames) {
			try {
				proxyFactory.registerProxy(brokerType, Class.forName(proxyClassName));
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
	
	public Configuration getConfig() {
		return this.config;
	}
	
}
