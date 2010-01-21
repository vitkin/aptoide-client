package cm.aptoide.pt;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

public class NewServerRssHandler extends DefaultHandler{
	
	Context mctx;
	
	private Vector<String> servers = new Vector<String>();
	
	private boolean new_serv_lst = false;
	private boolean new_serv = false;
	
	public NewServerRssHandler(Context ctx){
		mctx = ctx;
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		if(new_serv){
			servers.add(new String(ch).substring(start, start + length));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		if(localName.trim().equals("newserver")){
			new_serv_lst = false;
		}else if(localName.trim().equals("server")){
			new_serv = false;
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
		if(localName.trim().equals("newserver")){
			new_serv_lst = true;
		}else if(localName.trim().equals("server")){
			new_serv = true;
		}
	}
	
	public Vector<String> getNewSrvs(){
		return servers;
	}

}
