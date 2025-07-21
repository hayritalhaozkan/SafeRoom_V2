package com.saferoom.natghost;

import java.net.InetAddress;

public class PeerInfo {
	public String Host;
	public String Target;
	public byte signal;
	public InetAddress PublicInfo;
	public int  Port;
	
	public PeerInfo(String host, String target, byte signal, InetAddress public_info, int Port){
		this.Host = host;
		this.Target = target;
		this.signal = signal;
		this.PublicInfo = public_info;
		this.Port = Port;
		}

	}

