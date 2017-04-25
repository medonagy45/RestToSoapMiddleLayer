package com.UPS;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Node;

import com.predic8.schema.Element;
import com.predic8.schema.Schema;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;

public class callingClass {

	public static String soapMessageToString(SOAPMessage message) {
		String result = null;
		if (message != null) {
			ByteArrayOutputStream baos = null;
			try {
				baos = new ByteArrayOutputStream();
				message.writeTo(baos);
				result = baos.toString();
			} catch (Exception e) {
			} finally {
				if (baos != null) {
					try {
						baos.close();
					} catch (IOException ioe) {
					}
				}
			}
		}
		return result;
	}

	public static String nodesToString(Object element) {
		DOMSource source = new DOMSource((Node) element);
		StringWriter stringResult = new StringWriter();
		try {
			TransformerFactory.newInstance().newTransformer()
					.transform(source, new StreamResult(stringResult));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stringResult.toString();
	}

	public static String call_webService(String srequest, String url) {
		try {
			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory
					.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory
					.createConnection();

			// Send SOAP Message to SOAP Server
			// String url =
			// "http://172.16.10.24:8080/axis2/services/WSIUICMS_MobileAPP?wsdl";
			InputStream is = new ByteArrayInputStream(srequest.getBytes());
			SOAPMessage request = MessageFactory.newInstance().createMessage(
					null, is);
			SOAPMessage soapResponse = soapConnection.call(request, url);

			// Process the SOAP Response
			// printSOAPResponse(soapResponse);
			Node node = soapResponse.getSOAPBody().getFirstChild();
			String nodeString = nodesToString(node);
//			System.out.println("3 " + nodeString);

			soapConnection.close();
			return nodeString;
		} catch (Exception e) {
			System.err
					.println("Error occurred while sending SOAP Request to Server");
			e.printStackTrace();
			return "";
		}
	}

void orderingJsonObject(String url,JSONObject jsonObject){
	WSDLParser parser1 = new WSDLParser();
	Definitions defs = parser1
			.parse("http://172.16.10.24:8080/axis2/services/PBS_Comms?wsdl");
	for (int i = 0; i < defs.getSchemas().size(); i++) {
		String apiName= (String) jsonObject.keySet().toArray()[0];;
    	parseSchema(defs.getSchemas().get(i),apiName);
	}
	
}
public static void parseSchema(Schema schema,String apiName){

    for (Element e : schema.getAllElements()) {
        if (e.getName() != null) {
            /*
             * schema.getType() delivers a TypeDefinition (SimpleType orComplexType)
             * object.
             */
        	e.getRequestTemplate();
            
        }
    }
    }
	public  String prepareAndCall(String string,int counter) throws JSONException {

		/***************************************/
		// convert json to xml
		JSONObject json = new JSONObject(string);

		logger.info("Recieved Request"+counter +":"+json.toString(4));
		System.out.println("json : " + json.toString());
		String urlKey = "wsi:service_url";
		String serviceURL = json.getString(urlKey);
		json.remove(urlKey);
		orderingJsonObject(urlKey,json);
		String serviceName = serviceURL.substring(
				serviceURL.lastIndexOf("/") + 1, serviceURL.indexOf("?"));
		System.out.println("json after : " + json.toString());
		String xml = XML.toString(json);
		/********************************************/
//PREPARING THE XML AND CALLING THE WEBSERVICE 
		String headerOfXml = // "<?xml version='1.0' encoding='utf-8'?>\r\n"+
		"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:wsi=\"http://"
				+ serviceName
				+ "/\">\r\n"
				+ "   <soapenv:Header/>\r\n"
				+ "   <soapenv:Body>";
		String footerOfXml = "   </soapenv:Body>\r\n" + "</soapenv:Envelope>";
		String request = headerOfXml + xml + footerOfXml;
		System.out.println("2 " + request);
		String response = call_webService(request, serviceURL);

		/**********************************************/
		// convert xml to json
		JSONObject xmlJSONObj = XML.toJSONObject(response);
		logger.info("Response"+counter +":"+xmlJSONObj.toString(4));
		// System.out.println("size : "+);
		xmlJSONObj.getJSONObject(xmlJSONObj.keys().next()).remove("xmlns:ns1");
		String temp = xmlJSONObj.toString().replace("ns1:", "");
//		System.out.println(temp);
		return temp;
	}

	/**********************************************/

	private Logger logger = Logger.getLogger(UPSRESTService.class.getName());
	

}
