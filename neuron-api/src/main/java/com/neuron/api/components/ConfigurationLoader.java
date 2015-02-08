package com.neuron.api.components;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * Loads a configuration file which is used to
 * initialise the application at runtime.
 * @author JC
 *
 */
public class ConfigurationLoader {

	private static final Logger log = Logger
			.getLogger(ConfigurationLoader.class.getName());

	/**
	 * Load the configuration file into a POJO
	 * @param filename File to load
	 * @return Configuration The configuration encapsulated in a POJO
	 */
	public Configuration loadConfiguration(String filename, String appPath) {		
		Configuration config = new Configuration();
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(appPath);
			sb.append("/");
			sb.append(filename);
			String url = sb.toString();
			
			//TODO: Move the configuration file location to the application
			log.log(Level.INFO, "Config Path: " + url);
			InputStream input = ConfigurationLoader.class
					.getResourceAsStream(url);
			String jsonTxt = IOUtils.toString(input);
			if(jsonTxt != null){
				JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonTxt);
	
				Iterator<String> keys = json.keys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					String value = (String) json.get(key);
					config.addProperty(key, value);
				}
			}

		} catch (IOException e) {
			log.log(Level.SEVERE, "Couldn't load config", e);
		}
		return config;
	}
}
