package implementations.util.IoT;
import com.android.dx.rop.cst.Constant;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

//import javax.xml.stream.XMLEventReader;
//import javax.xml.stream.XMLInputFactory;
//import javax.xml.stream.XMLStreamConstants;
//import javax.xml.stream.events.Characters;
//import javax.xml.stream.events.EndElement;
//import javax.xml.stream.events.StartElement;
//import javax.xml.stream.events.XMLEvent;

import commom.Constants;

public class ReadXML {

	MqttBroker broker = new MqttBroker();
	List<Subscribe> list = new ArrayList<Subscribe>(); 
	
	public void start(){
		broker.connect();
		broker.subscribeToTopics(list);
	}
	
	public void readFile(){
		try{
			// can't use properties because there are repeated keys
			BufferedReader br = new BufferedReader(new FileReader(Constants.Environment.JCLRoot()+"config.txt"));
			String currentLine;
			
			Subscribe sub = null;
			while ( (currentLine = br.readLine()) != null ){
				if (currentLine.equals("") || currentLine.startsWith("#"))
					continue;
				String[] parts = currentLine.split("=", 2);
				parts[0] = parts[0].trim();
				parts[1] = parts[1].trim();
				if ( parts[0].equals("brokerIP") )
					broker.setIp(parts[1]);
				else if ( parts[0].equals("brokerPort") )
					broker.setPort(parts[1]);
				else if ( parts[0].equals("clientID") )
					broker.setClientID(parts[1]);
				else if ( parts[0].equals("topic") ){
					if (sub != null)
						list.add(sub);
					sub = new Subscribe();
					sub.setTopic(parts[1]);
				}
				else if ( parts[0].equals("class") )
					sub.setClassName(parts[1]);
				else if ( parts[0].equals("methodName") )
					sub.setMethodName(parts[1]);
				else if (parts[0].equals("nickname"))
					sub.setClassNickname(parts[1]);
				else if (parts[0].equals("threshold"))
					sub.setThreshold(parts[1]);
				else if (parts[0].equals("operator"))
					sub.setOperator(parts[1]);
				else if (parts[0].equals("valueType"))
					sub.setType(parts[1]);
			}
			if (sub != null)
				list.add(sub);
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
//	public void readXMLFile() throws Exception {
//		XMLInputFactory factory = XMLInputFactory.newInstance();
//		XMLEventReader eventReader = factory.createXMLEventReader(new FileReader("../jcl_conf/config.xml"));
//
//		Subscribe sub = null;
//
//		int opt = 0;
//
//		while(eventReader.hasNext()){
//			XMLEvent event = eventReader.nextEvent();
//			switch(event.getEventType()){
//				case XMLStreamConstants.START_ELEMENT:
//					StartElement startElement = event.asStartElement();
//					String qName = startElement.getName().getLocalPart();
//					if (qName.equalsIgnoreCase("ip"))
//						opt = 1;
//					else if (qName.equalsIgnoreCase("port"))
//						opt = 2;
//					else if (qName.equalsIgnoreCase("subscribe"))
//						opt = 3;
//					else if (qName.equalsIgnoreCase("topic"))
//						opt = 4;
//					else if (qName.equalsIgnoreCase("class"))
//						opt = 5;
//					else if (qName.equalsIgnoreCase("classNickname"))
//						opt = 6;
//					else if (qName.equalsIgnoreCase("methodName"))
//						opt = 7;
//					else if (qName.equalsIgnoreCase("param"))
//						opt = 8;
//					else if (qName.equalsIgnoreCase("threshold"))
//						opt = 9;
//					else if (qName.equalsIgnoreCase("clientID"))
//						opt = 10;
//					break;
//
//				case XMLStreamConstants.CHARACTERS:
//					Characters characters = event.asCharacters();
//					switch(opt){
//						case 1: broker.setIp(characters.getData());
//								break;
//						case 2: broker.setPort(characters.getData());
//								break;
//						case 3: sub = new Subscribe();
//								break;
//						case 4: sub.setTopic(characters.getData());
//								break;
//						case 5: sub.setClassName(characters.getData());
//								break;
//						case 6: sub.setClassNickname(characters.getData());
//								break;
//						case 7: sub.setMethodName(characters.getData());
//								break;
//						case 8: sub.setParam(characters.getData());
//								break;
//						case 9: sub.setThreshold(characters.getData());
//								break;
//						case 10: broker.setClientID(characters.getData());
//								break;
//					}
//					opt = 0;
//					break;
//				case  XMLStreamConstants.END_ELEMENT:
//					EndElement endElement = event.asEndElement();
//					if(endElement.getName().getLocalPart().equalsIgnoreCase("subscribe"))
//						list.add(sub);
//					break;
//			}
//		}
//	}
}
