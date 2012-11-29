/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt;

import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

enum Elements {
	
	MYAPP,GETAPP,NAME,PNAME,MD5SUM,INTSIZE,NEWSERVER,SERVER, NOTFOUND, GET;
	
	public static Elements lookup(String element){
		try{
			return Elements.valueOf(element);
		}catch (Exception e) {
			return Elements.NOTFOUND;
		}
		
		
	}
};

public class MyappHandler extends DefaultHandler {
	HashMap<String, String> app;
	ArrayList<String> servers = new ArrayList<String>();
	StringBuilder sb = new StringBuilder();
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		super.startElement(uri, localName, qName, attributes);
		sb.setLength(0);
		switch (Elements.lookup(localName.toUpperCase().trim())) {
		case GETAPP:
			app = new HashMap<String, String>();
			
			break;
		case NEWSERVER:
			break;
		default:
			break;
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		
		super.characters(ch, start, length);
		sb.append(ch,start,length);
		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		switch (Elements.lookup(localName.toUpperCase().trim())) {
		case GET:
			app.put("path", sb.toString());
			break;
		case GETAPP:
			
			break;
		case INTSIZE:
			app.put("size", sb.toString());
			break;
			
		case MD5SUM:
			app.put("md5sum", sb.toString());
			break;

		case MYAPP:
			break;

		case NAME:
			app.put("name", sb.toString());
			break;

		case NEWSERVER:
			break;

		case NOTFOUND:
			break;

		case PNAME:
			app.put("apkid", sb.toString());
			break;

		case SERVER:
			servers.add(sb.toString());
			break;

		default:
			break;
		}
		super.endElement(uri, localName, qName);
	}

	public HashMap<String, String> getApp() {
		return app;
	}

	public ArrayList<String> getServers() {
		return servers;
	}
	
	
	
	
}
