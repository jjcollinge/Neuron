package com.neuron.registration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.neuron.api.components.dal.ObjectMapperStrategy;

public class RegistrationDeserializer {

	private HashMap<String, ObjectMapperStrategy<Registration, String>> supportedFormats;

	public RegistrationDeserializer() {
		supportedFormats = new HashMap<String, ObjectMapperStrategy<Registration, String>>();
	}

	public void addFormat(String format, ObjectMapperStrategy<Registration, String> mapper) {
		supportedFormats.put(format, mapper);
	}

	public void removeFormat(String format) {
		supportedFormats.remove(format);
	}

	/**
	 * Cycle through all supported format mappers and try
	 * and deserialize the registration.
	 * @param datastream
	 * @return
	 */
	public Registration deserialize(String datastream) {

		Registration registration = null;
		boolean done = false;

		Iterator iter = supportedFormats.entrySet().iterator();
		while (iter.hasNext() && !done) {
			Map.Entry entry = (Map.Entry) iter.next();
			ObjectMapperStrategy<Registration, String> mapper = (ObjectMapperStrategy) entry.getValue();
			registration = (Registration) mapper.deserialize(datastream);
			if(registration != null) {
				done = true;
				registration.addProperty("format", (String)entry.getKey());
			}
		}
		return registration;
	}

}
