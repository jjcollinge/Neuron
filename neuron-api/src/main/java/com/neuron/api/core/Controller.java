package com.neuron.api.core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.configuration.Configuration;

/**
 * Controls a collection of activities. Is responsible
 * for ensuring that a activities setup method is called
 * before the start method.
 * @author JC
 *
 */
public class Controller {

	private static final Logger log = Logger.getLogger(Controller.class
			.getName());

	private HashMap<String, Activity> activities;
	private volatile boolean running;
	private Configuration config;
	private static Controller instance;

	/**
	 * Initialise collections
	 */
	private Controller() {
		this.activities = new LinkedHashMap<String, Activity>();
		this.running = false;

	}
	
	/**
	 * Give singleton access to the running application
	 */
	public static Controller getApplication() {
		if(instance == null) {
			instance = new Controller();
		}
		return instance;
	}
	
	/**
	 * Sets the activity containers configuration
	 * @param config
	 */
	public void setConfiguration(Configuration config) {
		this.config = config;
	}

	/**
	 * Add a new activities to the activity collection.
	 * Will ONLY succeed if the activities are stopped.
	 * @param activity to add
	 */
	public void addActivity(final Activity activity) {
		log.log(Level.INFO, "Adding new activity " + activity.getClass().getSimpleName());
		if (!running) {
			activity.setup(config);
			activities.put(activity.getName(), activity);
		} else {
			log.log(Level.INFO, "Cannot add a activity whilst the container is running");
		}
	}
	
	public Activity getActivity(final String key) {
		return activities.get(key);
	}

	/**
	 * Remove a current activity from the activity
	 * collection. Will ONLY succeed if the activities
	 * are stopped.
	 * @param activity to remove
	 */
	public void removeActivity(Activity activity) {
		log.log(Level.INFO, "Removing activity " + activity.getClass().getSimpleName());
		if (!running) {
			activities.remove(activity);
		} else {
			log.log(Level.INFO, "Cannot remove activity whilst container is running");
		}
	}

	/**
	 * Start all activities currently in the activity
	 * collection. Must start from a stopped state.
	 */
	public void startActivities() {
		log.log(Level.INFO, "Starting activities");
		if(!running) {
			for(Activity activity : activities.values()) {
				activity.start();
			}
			log.log(Level.INFO, this.toString());
			running = true;
		} else {
			log.log(Level.INFO, "activities are already running");
		}
	}

	/**
	 * Stop all activities currently in the activity
	 * collection. Can only stop activities if
	 * the activities are already running.
	 */
	public void stopActivities() {
		log.log(Level.INFO, "Stopping activities");
		if(running) {
			for (Activity activity : activities.values()) {
				activity.stop();
			}
			log.log(Level.INFO, this.toString());
			running = false;
		} else {
			log.log(Level.INFO, "Cannot stop activities as they are not running");
		}
	}

	/**
	 * Produces a human readable representation of the
	 * running activities.
	 */
	public String toString() {
		log.log(Level.INFO, "Printing activities");
		String tmp = "";
		tmp += "\n#####################################\n";
		for(Activity activity : activities.values()) {
			tmp += "activity name: " + activity.getClass().getSimpleName() + "\n";
			tmp += "activity status: ";
			if(activity.isAlive()){
				tmp += " RUNNING\n";
			} else {
				tmp += " STOPPED\n";
			}
			tmp += "-------------------------------------\n";
		}
		tmp += "#####################################\n";
		return tmp;
	}
}
