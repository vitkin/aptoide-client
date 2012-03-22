/**
 * MyappParser, 	auxiliary class to Aptoide's ServiceData
 * Copyright (C) 2011 Duarte Silveira
 * duarte.silveira@caixamagica.pt
 * 
 * derivative work of previous Aptoide's RssHandler with
 * Copyright (C) 2009  Roberto Jacinto
 * roberto.jacinto@caixamagica.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package cm.aptoide.pt.data.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import cm.aptoide.pt.data.cache.ViewCache;

/**
 * MyappParser, handles myapp xml Sax parsing
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class LatestVersionInfoParser extends DefaultHandler{
	private ManagerXml managerXml = null;
	
	private ViewCache cachelatestVersionInfo;
	private ViewLatestVersionInfo viewLatestVersionInfo = null;
	
	private EnumXmlTagsLatestVersionInfo tag = EnumXmlTagsLatestVersionInfo.aptoide;
	
	private StringBuilder tagContentBuilder;
	
		
	public LatestVersionInfoParser(ManagerXml managerXml, ViewCache myapp){
		this.managerXml = managerXml;
		this.cachelatestVersionInfo = myapp;
	}
	
	@Override
	public void characters(final char[] chars, final int start, final int length) throws SAXException {
		super.characters(chars, start, length);
		
		tagContentBuilder.append(new String(chars, start, length).trim());
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		
		tag = EnumXmlTagsLatestVersionInfo.safeValueOf(localName.trim());		
		
		switch (tag) {
			case versionCode:
				viewLatestVersionInfo = new ViewLatestVersionInfo(Integer.parseInt(tagContentBuilder.toString()));
				break;
			case uri:
				viewLatestVersionInfo.setRemotePath(tagContentBuilder.toString());
				break;
			case md5:
				viewLatestVersionInfo.setMd5sum(tagContentBuilder.toString());
				break;
				
			case aptoide:
				Log.d("Aptoide-LatestVersionInfoParser", "latest version info: "+viewLatestVersionInfo);
				break;
				
				
			default:
				break;
		}		
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		tagContentBuilder = new StringBuilder();
		
	}
	
	
	
	
	@Override
	public void startDocument() throws SAXException {	//TODO refacto Logs
		Log.d("Aptoide-LatestVersionInfoParser","Started parsing XML from " + cachelatestVersionInfo.getLocalPath() + " ...");
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		Log.d("Aptoide-LatestVersionInfoParser","Done parsing XML from " + cachelatestVersionInfo.getLocalPath() + " !");

		managerXml.parsingLatestVersionInfoFinished(viewLatestVersionInfo);
		super.endDocument();
	}


}
