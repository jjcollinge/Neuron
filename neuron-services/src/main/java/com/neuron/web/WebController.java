package com.neuron.web;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.servlet.ServletContainer;

import com.neuron.api.components.services.Service;

public class WebController implements Service {

	private static final Logger log = Logger.getLogger(WebController.class
			.getName());

	private static WebController instance;

	private Tomcat server;
	private volatile boolean running;

	private WebController() {
	}

	public static WebController getInstance() {
		if (instance == null) {
			instance = new WebController();
		}
		return instance;
	}

	public void setup() {
		try {
			// Define a folder to hold web application contents.
			String webappDirLocation = "../neuron-web/WebContent/";
			server = new Tomcat();

			// Define port number for the web application
			String webPort = System.getenv("PORT");
			if (webPort == null || webPort.isEmpty()) {
				webPort = "9998";
			}
			// Bind the port to Tomcat server
			server.setPort(Integer.valueOf(webPort));

			// Define a web application context.
			Context context = server.addWebapp("/api", new File(
					webappDirLocation).getAbsolutePath());

			// Add servlet that will register Jersey REST resources
			Tomcat.addServlet(context, "jersey-container-servlet",
					new ServletContainer(new ApplicationConfig()));
			context.addServletMapping("/api/*", "jersey-container-servlet");

			// Define and bind web.xml file location.
			// File configFile = new File(webappDirLocation +
			// "WEB-INF/web.xml");
			// log.log(Level.INFO, "Locating web.xml at " + webappDirLocation +
			// "WEB-INF/web.xml");
			// context.setConfigFile(configFile.toURI().toURL());

		} catch (ServletException e) {
			log.log(Level.SEVERE, "Failed to add servlet");
		}/*
		 * catch (MalformedURLException e) { log.log(Level.SEVERE,
		 * "Failed to parse URL"); }
		 */
	}

	public void start() {
		new Thread(new Runnable() {
			public void run() {

				try {
					server.start();
					running = true;
					log.log(Level.INFO, "Web server now running");
				} catch (LifecycleException e) {
					e.printStackTrace();
				}
				while (running) {
					server.getServer().await();
				}
				try {
					server.stop();
					log.log(Level.INFO, "Web server now stopped");
				} catch (LifecycleException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void stop() {
		running = false;
	}

}
