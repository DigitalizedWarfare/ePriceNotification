package sendmail;
/**
 *
 * @author Prajyot
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
 
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
 
public class HTMLParserExample1
{
	public static void main(String[] args)
	{
		System.out.println(args[0].toString()+"------"+args[1].toString());
		Document doc;
		try
		{
						// need http protocol
			doc = Jsoup.connect(args[0].toString()).timeout(40000).get();//.get()
						// get Item header name
		    String itemHeadName = doc.select("h1").text();
                    	// get Item name
		    String itemName = doc.select("meta[name=og_title]").first().attr("content");
						// get Item price
            String itemPrice = doc.select("meta[itemprop=price]").first().attr("content");
            			// get item image
            String imageURL = doc.select("meta[name=og_image]").first().attr("content");
            
            String imagePath = downloadImage(imageURL);
            			// Mail boody
            String msg = "Price of \""+itemName+"\" is "+itemPrice+"Rs.";
            System.out.println("Price of \""+itemName+"\" is "+itemPrice+"Rs.----"+itemHeadName);
            String to = args[1].toString();

            			// Sender's email ID needs to be mentioned
            String from = "epricenotification@gmail.com";
            String pass = "zxcvbnm.1";           

		    			// Get system properties
		    Properties props = System.getProperties();
		    props.put("mail.smtp.starttls.enable", true); // added this line
		    props.put("mail.smtp.host", "smtp.gmail.com");
		    props.put("mail.smtp.user", from);
		    props.put("mail.smtp.password", pass);
		    props.put("mail.smtp.port", "587");
		    props.put("mail.smtp.auth", false);

		    			// Get the default Session object.
		    Session session = Session.getDefaultInstance(props,null);
		    MimeMessage message = new MimeMessage(session);

	         			// Set From: header field of the header.
	        message.setFrom(new InternetAddress(from));

	        			// Set To: header field of the header.
	        message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));

	        			// Set Subject: header field
	        message.setSubject(itemHeadName+" at "+itemPrice+"Rs.");

	        MimeMultipart multipart = new MimeMultipart();
	        BodyPart messageBodyPart = new MimeBodyPart();
	        String htmlText = "<H1>"+msg+"</H1><img src=\"cid:image\">";
	        messageBodyPart.setContent(htmlText, "text/html");
	        				// add it
         	multipart.addBodyPart(messageBodyPart);
         				// second part (the image)
         	messageBodyPart = new MimeBodyPart();
         	DataSource fds = new FileDataSource(imagePath);
         	messageBodyPart.setDataHandler(new DataHandler(fds));
         	messageBodyPart.setHeader("Content-ID", "<image>");
         				// add image to the multipart
         	multipart.addBodyPart(messageBodyPart);
         				// put everything together
         	message.setContent(multipart);
         			// Send the actual HTML message, as big as you like
	        			// Send message
	        Transport transport = session.getTransport("smtp");
	        transport.connect("smtp.gmail.com", from, pass);
	        System.out.println("Transport: "+transport.toString());
	        transport.sendMessage(message, message.getAllRecipients());
         
	        System.out.println(" Message sent successfully....");
	        deleteFile(imagePath);
		}
		catch (MessagingException ex)
		{
            Logger.getLogger(HTMLParserExample1.class.getName()).log(Level.SEVERE, null, ex);
        }
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static String downloadImage(String imgURL) throws IOException
	{
		String imgSrc = imgURL.substring(imgURL.lastIndexOf("/") + 1);
        String imgPath = null;
        imgPath = "D:/" + imgSrc;
		URL url = new URL(imgURL);
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(imgPath);
		byte[] b = new byte[2048];
		int length;
		while ((length = is.read(b)) != -1)
		{
			os.write(b, 0, length);
		}
		is.close();
		os.close();
		return imgPath;
	}
	
	// To delete the downloaded file
	public static void deleteFile(String imgPath)
	{
		File file = new File(imgPath);
		file.delete();
		System.out.println("Image File deleted!!");
	}
}