package org.javacaela;

import java.util.Map.Entry;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import implementations.dm_kernel.IoTuser.JCL_IoTFacadeImpl;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.JCL_Sensor;



public class EmailSender
{
	
    String from = "myEmail@gmail.com";
    String fromPass = "myPassword";
    String to = "toEmail@gmail.com";   
    
    
    public void sendEmail(String subject, String email) throws Exception{
    	JCL_IoTfacade iot = JCL_IoTFacadeImpl.getInstance();
    	
		Entry<String, String> galileo = iot.getDeviceByName("galileo").get(0);  
		Entry<String, String> arduino = iot.getDeviceByName("arduino").get(0);
		Entry<String, String> androidPhoto = iot.getDeviceByName("android2").get(0);
		
		Entry<String, String> light = iot.getSensors(arduino).get(0);
		Entry<String, String> temperature = iot.getSensorByName(galileo, "temperature").get(0);
		Entry<String, String> potentiometer = iot.getSensorByName(galileo, "potentiometer").get(0);		
		Entry<String, String> photo = iot.getSensorByName(androidPhoto, "TYPE_PHOTO").get(0);

		iot.getSensingDataNow(androidPhoto, photo).showData();
		
		Thread.sleep(2000);
		
		email += "Light: " + iot.getSensingDataNow(arduino, light) + 
				"\nTemperature: " + iot.getSensingDataNow(galileo, temperature) +
				"\nPotentiometer: " + iot.getSensingDataNow(galileo, potentiometer);
		
    	
    	System.out.println("Sending an email");

       	Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
    	
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication()
                    {
                        return new PasswordAuthentication(from , fromPass);
                    }
                });
        session.setDebug(false);
        try {
        	JCL_Sensor sensor = iot.getSensingDataNow(androidPhoto, photo);
      	
        	MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));

            message.setRecipients(Message.RecipientType.TO, to);

            message.setSubject(subject);

            BodyPart messageBodyPart = new MimeBodyPart();

            messageBodyPart.setText(email);

            Multipart multipart = new MimeMultipart();

            multipart.addBodyPart(messageBodyPart);

            messageBodyPart = new MimeBodyPart();

            DataHandler handler = new DataHandler((byte[]) sensor.getObject(), "application/octet-stream");
            messageBodyPart.setDataHandler(handler);

            messageBodyPart.setFileName("image.jpg");

            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);
            Transport.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    
}