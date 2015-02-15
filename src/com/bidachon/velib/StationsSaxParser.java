package com.bidachon.velib;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class StationsSaxParser {

	
	private ArrayList<Station> item;
	Station currentStation;
	
	public ArrayList<Station> parseStations(InputStream input)
	{
		item = new ArrayList<Station>();
		
    	try {
 		   
 		   SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
 		   SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
 		   XMLReader myXMLReader = mySAXParser.getXMLReader();
 		   StationListHandler myStationHandler = new StationListHandler();
 		   myXMLReader.setContentHandler(myStationHandler);
 		   
 		   InputSource inputSource = new InputSource(input);//new InputSource(getInputStreamFromUrl(activity.getString(R.string.VelibUrl)));
 		   myXMLReader.parse(inputSource);
 		   
 		   
 		  } catch (ParserConfigurationException e) {
 		   // TODO Auto-generated catch block
 		   e.printStackTrace();
 		  } catch (SAXException e) {
 		   // TODO Auto-generated catch block
 		   e.printStackTrace();
 		  } catch (IOException e) {
 		   // TODO Auto-generated catch block
 		   e.printStackTrace();
 		  }
 	
 	return item;
	}
	
	public Station parseStationData(InputStream input)
	{
		currentStation = new Station();
		
    	try {
 		   
 		   SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
 		   SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
 		   XMLReader myXMLReader = mySAXParser.getXMLReader();
 		   StationHandler myStationHandler = new StationHandler();
 		   myXMLReader.setContentHandler(myStationHandler);
 		   
 		   InputSource inputSource = new InputSource(input);//new InputSource(getInputStreamFromUrl(activity.getString(R.string.VelibUrl)));
 		   myXMLReader.parse(inputSource);
 		   
 		   
 		  } catch (ParserConfigurationException e) {
 		   // TODO Auto-generated catch block
 		   e.printStackTrace();
 		  } catch (SAXException e) {
 		   // TODO Auto-generated catch block
 		   e.printStackTrace();
 		  } catch (IOException e) {
 		   // TODO Auto-generated catch block
 		   e.printStackTrace();
 		  }
 	
 	return currentStation;
	}
	
	   public InputStream getInputStreamFromUrl(String url) {
			InputStream content = null;
			try {
				HttpGet httpGet = new HttpGet(url);
				HttpClient httpclient = new DefaultHttpClient();
				// Execute HTTP Get Request
				HttpResponse response = httpclient.execute(httpGet);
				
				content = response.getEntity().getContent();
	                }
	   catch (MalformedURLException e) {
	 		   // TODO Auto-generated catch block
	 		   e.printStackTrace();
	   }catch (Exception e) {
	        Log.e("getInputStreamFromUrl",e.toString());
				//handle the exception !
			}
			return content;
	}
	
	  private class StationListHandler extends DefaultHandler
	   {
	    final int stateUnknown = 0;
	    final int stateMarker = 1;
	    int state = stateUnknown;
	    
	  @Override
	  public void startDocument() throws SAXException {
	   // TODO Auto-generated method stub
		  Log.d("AroundMeActivity", "startDocument");
	  }

	  @Override
	  public void endDocument() throws SAXException {
	   // TODO Auto-generated method stub
	  }

	  @Override
	  public void startElement(String uri, String localName, String qName,
	    Attributes attributes) throws SAXException {
	   // TODO Auto-generated method stub
	   if (localName.equalsIgnoreCase("marker"))
	   {
	    state = stateMarker;
	    Station s = new Station();
	    
	    String tmp = attributes.getValue("name");
	    String tmp2 = tmp.substring(tmp.indexOf('-')+1, tmp.length());
	    s.setName(tmp2.trim());
	    s.setAddress(attributes.getValue("address"));
	    s.setFullAddress(attributes.getValue("fullAddress"));
	    s.setLocation(
	    		s.new Location(
	    		Double.parseDouble(attributes.getValue("lat")),	
	    		Double.parseDouble(attributes.getValue("lng"))
	    		)
	    );
	    s.setStatus(Boolean.parseBoolean(attributes.getValue("open")));
	    s.setNumber(Integer.parseInt(attributes.getValue("number")));
	    item.add(s);
	   }
	   else
	   {
	    state = stateUnknown;
	   }
	  }

	  @Override
	  public void endElement(String uri, String localName, String qName)
	    throws SAXException {
	   // TODO Auto-generated method stub
	   state = stateUnknown;
	  }

	  @Override
	  public void characters(char[] ch, int start, int length)
	    throws SAXException {
	   // TODO Auto-generated method stub
	  }
	    
	   }
	  
	  private class StationHandler extends DefaultHandler
	   {
	    final int stateUnknown = 0;
	    final int stateAvailable = 1;
	    final int stateFree = 2;
	    final int stateTotal = 3;
	    final int stateTicket = 4;
	    int state = stateUnknown;
	    
	    
	  @Override
	  public void startDocument() throws SAXException {
	   // TODO Auto-generated method stub
	  }

	  @Override
	  public void endDocument() throws SAXException {
	   // TODO Auto-generated method stub
	  }

	  @Override
	  public void startElement(String uri, String localName, String qName,
	    Attributes attributes) throws SAXException {
	   // TODO Auto-generated method stub
	   if (localName.equalsIgnoreCase("available"))
	   {
	    state = stateAvailable;

	   }
	   else if (localName.equalsIgnoreCase("free"))
	   {
		   state = stateFree;
	   }
	   else if (localName.equalsIgnoreCase("total"))
	   {
		   state = stateTotal;
	   }
	   else if (localName.equalsIgnoreCase("ticket"))
	   {
		   state = stateTicket;
	   }
	   else
	   {
		   state = stateUnknown;
	   }
	  }

	  @Override
	  public void endElement(String uri, String localName, String qName)
	    throws SAXException {
	   // TODO Auto-generated method stub
	   state = stateUnknown;
	  }

	  @Override
	  public void characters(char[] ch, int start, int length)
	    throws SAXException {
	   // TODO Auto-generated method stub
		  String tmp = new String(ch,start, length);
		  switch(state)
		  {
		  case stateAvailable:
			  currentStation.setBikes(Integer.parseInt(tmp));
			  break;
		  case stateFree:
			  currentStation.setStands(Integer.parseInt(tmp));
			  break;
		  case stateTotal:
			  currentStation.setTotal(Integer.parseInt(tmp));
			  break;
		  case stateTicket:
			  currentStation.setTicket(Integer.parseInt(tmp));
			  break;
		  }
	  }
	    
	   }
	
}
