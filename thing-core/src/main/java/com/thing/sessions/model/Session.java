package com.thing.sessions.model;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.thing.sessions.IdGenerator;

/**
 * Name: Session
 * ---------------------------------------------------------------
 * Desc: The Session class holds information about a devices
 * 		 current state in the system.
 * 
 * @author jcollinge
 *
 */
public class Session {
	
	private int deviceId;
	private long timestamp;
	private String protocol;
	private String format;
	private byte[] hash;
	
	public Session(int deviceId, String protocol, String format) {
		this.deviceId = deviceId;
		this.protocol = protocol;
		this.format = format;
		this.timestamp = System.currentTimeMillis() / 1000L;
		this.hash = generateHash();
	}
	public int getDeviceId() {
		return this.deviceId;
	}
	public String getProtocol() {
		return this.protocol;
	}
	public String getFormat() {
		return this.format;
	}
	public byte[] getHash() {
		return this.hash;
	}
	public long getTimeStamp() {
		return this.timestamp;
	}
	public void updateTimeStamp() {
		this.timestamp = System.currentTimeMillis() / 1000L;
	}
	public boolean after(long timestamp) {
		return (this.timestamp > timestamp);
	}
	public boolean before(long timestamp) {
		return (this.timestamp < timestamp);
	}
	public String getPingAddress() {
		return "devices/"+deviceId+"/ping";
	}
	private byte[] generateHash() {
		MessageDigest digest = null;
		try {
			digest = java.security.MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte[] t = ByteBuffer.allocate(8).putLong(timestamp).array();
		byte[] p = protocol.getBytes();
		byte[] f = format.getBytes();
		byte[] instance = ByteBuffer.allocate(t.length + p.length + f.length).put(t).put(p).put(f).array();
	    digest.update(instance);
	    byte[] hash = digest.digest();
	    return hash;
	}

}
