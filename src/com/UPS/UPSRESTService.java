package com.UPS;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;




import javax.xml.transform.stream.StreamResult;









//import org.apache.el.parser.Node;
//import org.apache.el.util.MessageFactory;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Node;

import com.predic8.schema.Element;
import com.predic8.schema.Schema;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;

@Path("/")
public class UPSRESTService {

	/**********************************************/
	static int counter= 0 ;
	private Logger logger = Logger.getLogger(UPSRESTService.class.getName());
	@POST
	@Path("/restService")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response UPSREST(InputStream incomingData) {
		logger.info("Recieved Request"+counter);
		StringBuilder UPSBuilder = new StringBuilder();
		String response = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				UPSBuilder.append(line);
			}
			/*********************************/

//			System.out.println("Data Received: " + UPSBuilder.toString());

//			logger.info("Request"+counter+" is:"+UPSBuilder.toString());
			response = new callingClass().prepareAndCall(UPSBuilder.toString(),counter);

			/***********************************/
		} catch (Exception e) {
			System.out.println("Error Parsing: - ");
			e.printStackTrace();
			response = "Error Parsing: - " + e.getMessage();
		}

		// return HTTP response 200 in case of success

		logger.info("ending"+counter++
				+"\n===================================================================================================");
		return Response.status(200).entity(response).build();
	}

	@GET
	@Path("/verify")
	@Produces(MediaType.TEXT_PLAIN)
	public Response verifyRESTService(InputStream incomingData) {
		String result = "RESTService Successfully started..";

		// return HTTP response 200 in case of success
		return Response.status(200).entity(result).build();
	}

}