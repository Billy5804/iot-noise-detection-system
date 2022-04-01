package com.billy5804.iotnoisedetectionbackend.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class EmailService {
	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
	
	final Email noReplyEmail = new Email("noreply@iot.alexroyle.com");
	
	public String sendEmail(Mail mail) throws IOException {
		mail.setFrom(new Email("noreply@iot.alexroyle.com"));
	    
		SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
	    Request request = new Request();
	    try {
	      request.setMethod(Method.POST);
	      request.setEndpoint("mail/send");
	      request.setBody(mail.build());
	      Response response = sg.api(request);
	      logger.info("" + response.getStatusCode());
	      logger.info(response.getHeaders().toString());
	      logger.info(response.getBody());
	      return response.getBody();
	    } catch (IOException ex) {
	      throw ex;
	    }
	}
}
