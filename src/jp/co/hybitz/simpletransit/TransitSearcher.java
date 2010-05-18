/**
 * Copyright (C) 2010 Hybitz.co.ltd
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * 
 */
package jp.co.hybitz.simpletransit;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import jp.co.hybitz.simpletransit.model.Transit;
import jp.co.hybitz.simpletransit.model.TransitQuery;
import jp.co.hybitz.simpletransit.parser.TransitParser;
import jp.co.hybitz.simpletransit.parser.TransitParser20100517;

import org.xmlpull.v1.XmlPullParserException;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class TransitSearcher {
	private static final String GOOGLE = "http://www.google.co.jp/m/directions";
	
	public List<Transit> search(TransitQuery query) {
		InputStream in = null;

		try {
			URL url = new URL(GOOGLE + createQueryString(query));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			in = con.getInputStream();
			return createParser().parse(in);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) { try {in.close();} catch (IOException e){} }
		}
		
		return null;
	}
	
	private TransitParser createParser() {
		return new TransitParser20100517();
	}
	
	private String createQueryString(TransitQuery query) {
		StringBuilder sb = new StringBuilder();
		sb.append("?saddr=").append(URLEncoder.encode(query.getFrom()));
		sb.append("&daddr=").append(URLEncoder.encode(query.getTo()));
		sb.append("&time=&ttype=dep&ie=UTF8&f=d&dirmode=transit&num=3&dirflg=r");
		return sb.toString();
	}
}
