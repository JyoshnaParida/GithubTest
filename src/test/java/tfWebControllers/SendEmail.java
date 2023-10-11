package tfWebControllers;

import java.util.Date;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;


public class SendEmail extends MyRunner {

    public static void sendTestReportEmail() throws EmailException {
        System.out.println("==========Test Started==================");
        MultiPartEmail email = new MultiPartEmail();
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(465);
        email.setAuthenticator(new DefaultAuthenticator("anil.g@gaiansolutions.com", "Suchi@0503"
        		+ ""));
      
        email.setSSLOnConnect(true);
   email.setFrom("anil.g@gaiansolutions.com");
        email.setSubject("Auto Generated Mail : TF WEB Api Automation test report");
        email.setMsg("Dear Team,\n\n"
        		+ "Plesae find the TF WEB Api Automation test report\n\n"
        		+ "Thanks & Regards\n"
        		+ "QA Automation Team.\n\n"
        		+ "Note: For browser compatibility problems, please download and open the report");
       email.addTo("mudili.sivakumar@gaiansolutions.com");
//       email.addTo("jyoshna.parida@gaiansolutions.com ");
//       email.addTo("venkata.bhavani@gaiansolutions.com ");
//       email.addTo("mounika.gandham@gaiansolutions.com ");


       
       email.addTo("anil.g@gaiansolutions.com");
//       email.addTo("joseanitt.goli@gaiansolutions.com");
//       email.addTo("soni.uma@gaiansolutions.com");
//       email.addTo("gurpreet.gandhi@gaiansolutions.com");
       
 email.addTo("divya.bharathi@gaiansolutions.com");
//email.addTo("ramya.mahali@gaiansolutions.com");
//email.addTo("nisha.sakhre@gaiansolutions.com");
//email.addTo("shahaji.kadam@gaiansolutions.com ");
//email.addTo("farha.mehveen@gaiansolutions.com ");


email.addTo("charu.hasini@gaiansolutions.com ");
//email.addTo("shireen.kamarsu@gaiansolutions.com");
//email.addTo("sattala.prasanthi@gaiansolutions.com");
//     email.addTo("kuruva.veereshamma@gaiansolutions.com");
//     email.addTo("kuruva.lakshmi@gaiansolutions.com ");
//





        
        
//        email.addTo("third@example.com");

        // Attach the HTML file
        EmailAttachment attachment = new EmailAttachment();
        System.out.println(report1);

//        Date d = new Date();
//        String fileName = d.toString().replace(":", "_").replace(" ", "_") + ".html";
        attachment.setPath(System.getProperty("user.dir") +"/"+report1);
        
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        email.attach(attachment);
      

        email.send();
        System.out.println("==========Email sent==================");
    }
    
}