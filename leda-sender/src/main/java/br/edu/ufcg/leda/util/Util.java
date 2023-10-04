package br.edu.ufcg.leda.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Pattern;

public class Util {

	public static InetAddress getLocalIP() throws SocketException {
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		InetAddress result = null;
		Pattern IPADDRESS_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
		
		while (interfaces.hasMoreElements()){
		    NetworkInterface current = interfaces.nextElement();
		    //System.out.println(current);
		    if (!current.isUp() || current.isLoopback() || current.isVirtual()) continue;
		    Enumeration<InetAddress> addresses = current.getInetAddresses();
		    while (addresses.hasMoreElements()){
		        InetAddress current_addr = addresses.nextElement();
		        if(current.toString().contains("127")) {
		        	result = current_addr;		        	
		        }
		        if (current_addr.isLoopbackAddress()) {
		        	continue;
		        }else {
		        	
		        	//System.out.println(current_addr.getHostAddress());
		        	if(current_addr.toString().contains("150.165")) {
		        		return current_addr;
		        	}else {
		        		if(IPADDRESS_PATTERN.matcher(current_addr.getHostAddress()).matches()) {
		        			result = current_addr;
		        		}
		        	}
		        }
		    }
		}
		return result;
	}
}
