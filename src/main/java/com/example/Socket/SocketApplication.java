package com.example.Socket;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static java.lang.Integer.parseInt;

@SpringBootApplication
public class SocketApplication {
	static String message6;
	static String rsp = "<STX>8<SOH>PING_RES<ETX>";

	static final char STX = (char) 0x02;
	static final char SOH = (char) 0x01;
	static final char ETX = (char) 0x03;

	final static int port = 9637;
	private Socket socket;
	Attr messageClass;
	Attr messagetransactionID;
	Attr materialId;
	Attr materialQty;
	Attr routeName;
	Attr routeID;

	String xmlResponse = "<message>\n"
			+ "<header messageClass=\"510\" transactionID=\"%s\" reply=\"1\">\n"
			+ "<location routeID=\"%s\" routeName=\"%s\" equipmentID=\"-1\" equipmentName=\"\"\n"
			+ " zoneID=\"-1\" zonePos=\"-1\" zoneName=\"\" laneNo=\"0\"/>\n"
			+ "</header>\n"
			+ "<body>\n"
			+ "<result errorCode=\"0\" errorText=\"\" actionCode=\"0\"/>\n"
			+ "<material id=\"%s\" state=\"0\" prevReelId=\"\" splicedReelId=\"\" >\n"
			+ "<detail part=\"%s\" qty=\"%s\" lot=\"%s\" vendor=\"%s\" userdata=\"%s\" userdata2=\"%s\" partclass=\"R100\"/>\n"
			+ "</material>\n"
			+ "</body>\n"
			+ "</message>";

	String XmlGenericMesError = "<message>\n"
			+ "<header messageClass=\"510\" transactionID=\"%s\" reply=\"1\">\n"
			+ "<location routeID=\"%s\" routeName=\"%s\" equipmentID=\"-1\" equipmentName=\"\"\n"
			+ "zoneID=\"-1\" zonePos=\"-1\" zoneName=\"\" laneNo=\"0\"/>\n"
			+ "</header>\n"
			+ "<body>\n"
			+ "<result errorCode=\"1\" errorText=\"%s\" actionCode=\"0\"/>\n"
			+ "</body>\n"
			+ "</message>";

	Logger logger =  Logger.getLogger("MyLog");
	FileHandler fh;

	public static void main(String[] args) {
		try {
			ServerSocket socketServeur = new ServerSocket(port);
			// AT (G557937) le 18/12/2018 "Pour la Reception Conducteur"
			System.out.println("Lancement du serveur sur le port " + port);
			System.out.println("Connexion avec le client : " + socketServeur.getInetAddress() + " sur le port " + port);

			while (true) {

				Socket socketClient = socketServeur.accept();
				socketClient.setKeepAlive(true);
				SocketApplication t = new SocketApplication(socketClient);
//				t.start()
			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	public SocketApplication(Socket socket) {

		this.socket = socket;

	}

	public void run() throws IOException {

		traitements();

	}

	private void traitements() throws IOException {
		try {

			String message = "";
			String message2 = "";
			int limit = 5000000; // 5 Mb

			fh = new FileHandler("C:/Windows/Temp/SocketPANA/SocketPANA.log", limit, 1, true);
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);

			logger.info("Connexion avec le client : " + socket.getInetAddress() + " sur le port " + port);

			System.out.println("Connexion avec le client : " + socket.getInetAddress() + " sur le port " + port);

			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			PrintStream out = new PrintStream(socket.getOutputStream());
			DataOutputStream dout = new DataOutputStream(out);

			while (true) {
				try {

					StringBuilder b = new StringBuilder();

					char[] buffer = new char[1024 * 4];
					int n = 0;
					while (-1 != (n = in.read(buffer))) {

						b.append(buffer);

						break;
					}

					message = b.toString();
					if (!message.isEmpty() && (message != null)) {
						System.out.println("message input: " + message);
					}
					int first = 0;

					if (message.contains("PING_REQ")) {

						dout.writeByte(STX);
						dout.writeBytes("8");
						dout.writeByte(SOH);
						dout.writeBytes("PING_RSP");
						dout.writeByte(ETX);

					} else if (!message.isEmpty() && (message != null)) {

						//here we will define all the treatement when ever received a scanning message
						int beginmessage = message.indexOf('<');
						int endmessage = message.lastIndexOf('>');
						//if(beginmessage < 0 && endmessage < 0 ){}
						message2 = message.substring(beginmessage, endmessage + 1);
						message2.substring(0, message2.length() - 2);
						System.out.println("message2: " + message2);
						org.w3c.dom.Document finalXmlResult = loadXMLFromString(message2);

						Element rootElement = finalXmlResult.getDocumentElement();

						NodeList nodeList = rootElement.getElementsByTagName("*");
						for (int i = 0; i < nodeList.getLength(); i++) {
							// Get element
							Element element = (Element) nodeList.item(i);
							System.out.println(element.getNodeName());

							if (element.getNodeName().equalsIgnoreCase("header")) {

								messageClass = element.getAttributeNode("messageClass");

								if (messageClass.getValue().equals("510")) {

									messagetransactionID = element.getAttributeNode("transactionID");
									logger.info("transactionID: " + messagetransactionID.getValue());
									System.out.println("transactionID: " + messagetransactionID.getValue());
								} else {

									//return error message
								}
							}
							if (element.getNodeName().equalsIgnoreCase("location")) {
								routeName = element.getAttributeNode("routeName");
								System.out.println("routeName " + routeName.getValue());
								logger.info("routeName: " + routeName.getValue());

								routeID = element.getAttributeNode("routeID");
								System.out.println("routeID " + routeID.getValue());
								logger.info("routeID: " + routeID.getValue());
							}
							if (element.getNodeName().equalsIgnoreCase("material")) {
								materialId = element.getAttributeNode("id");
								System.out.println("materialId " + materialId.getValue());
								logger.info("materialId: " + materialId.getValue());
								// AT le 27/03/2018
								materialQty = element.getAttributeNode("qty");
								System.out.println("materialQty " + materialQty.getValue());
								logger.info("materialQty: " + materialQty.getValue());
							}
						}

						int qty = parseInt(materialQty.getValue());
						System.out.println("materialQty int: " + qty);

						USPANA usresp = new USPANA();

//						if (routeID.getValue().equals("-1")) {
//							usresp = getMaterielReadyWithParsingPreSetupAVS(materialId.getValue(), routeName.getValue(), qty);
//						} else {
//							usresp = getMaterielReadyWithParsingConducteur(materialId.getValue(), routeName.getValue(), qty);
//						}
						if (usresp.getErrorCode().equalsIgnoreCase("0")) {

							String finalXmlMessage = String.format(xmlResponse,
									messagetransactionID.getValue(), routeID.getValue(),
									routeName.getValue(), materialId.getValue(), usresp.getPart(),
									usresp.getQty(), usresp.getLot(), usresp.getVendor(),
									usresp.getUserdata(), usresp.getUserdata2());

							System.out.println(finalXmlMessage);
							logger.info("finalXmlMessage: " + finalXmlMessage);
							dout.writeByte(STX);
							Integer len = finalXmlMessage.length();

							String lens = len.toString();
							dout.writeBytes(lens);
							dout.writeByte(SOH);

							dout.writeBytes(finalXmlMessage);

							dout.writeByte(ETX);

						} else {
							String finalXmlMessage = String.format(XmlGenericMesError,
									messagetransactionID.getValue(), routeID.getValue(), routeName.getValue(), usresp.getErrorText());

							System.out.println(finalXmlMessage);
							logger.info("finalXmlMessage:\n " + finalXmlMessage);

							dout.writeByte(STX);
							Integer len = finalXmlMessage.length();

							String lens = len.toString();
							dout.writeBytes(lens);
							dout.writeByte(SOH);

							dout.writeBytes(finalXmlMessage);

							dout.writeByte(ETX);

						}

					}

				} catch (IOException e) {
					logger.info("IOException: " + e.getMessage());
					break;
				}

			}

			//   socket.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("EXCEPTION: " + e.getMessage());
		}
	}

	public static Document loadXMLFromString(String xmlString) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(xmlString)));
		return document;
	}

}