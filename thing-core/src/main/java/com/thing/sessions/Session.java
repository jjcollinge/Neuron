package com.thing.sessions;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.thing.model.ActiveDevice;

public class Session {
	
	private int deviceId;
	private long timestamp;
	private ActiveDevice device;
	private String protocol;
	private String format;
	private boolean lockOnSend;
	private boolean lock;
	private byte[] hash;
	
	public Session(ActiveDevice device, String protocol, String format) {
		this.deviceId = IdGenerator.generateId();
		this.device = device;
		
		this.protocol = protocol;
		this.format = format;
		this.timestamp = System.currentTimeMillis() / 1000L;
		this.hash = generateHash();
		this.lock = false;
		this.lockOnSend = false;
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
	public ActiveDevice getDevice() {
		return this.device;
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
	public boolean isLocked() {
		return this.lock;
	}
	public boolean shouldLockOnSend() {
		return this.lockOnSend;
	}
	public synchronized void lockOnSend() {
		this.lockOnSend = true;
	}
	public synchronized void unlock() {
		this.lock = false;
		this.lockOnSend = false;
	}
	public synchronized void lock() {
		this.lock = true;
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
