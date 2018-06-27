/**
 * Created by Hoan Tran @ http://hoantran-it.blogspot.com
 *
 * Any modifications to this file must keep this entire header intact.
 *
 */
package com.github.hoantran.lib.aws.s3;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

/**
 * @author hoan.tran
 */
public class SESService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SESService.class);

    public boolean sendMail(String region, String fromEmail, String toEmail, String subject, String htmlBody, String textBody) throws IOException {
        try {
            AmazonSimpleEmailService client =
                    AmazonSimpleEmailServiceClientBuilder.standard()
                            .withRegion(region).build();
            SendEmailRequest request = new SendEmailRequest()
                    .withSource(fromEmail)
                    .withDestination(new Destination().withToAddresses(toEmail))
                    .withMessage(new Message()
                            .withBody(new Body()
                                    .withHtml(new Content()
                                            .withCharset("UTF-8").withData(htmlBody))
                                    .withText(new Content()
                                            .withCharset("UTF-8").withData(textBody)))
                            .withSubject(new Content()
                                    .withCharset("UTF-8").withData(subject)));
            client.sendEmail(request);
            return true;
        } catch (Exception ex) {
            LOGGER.error("The email was not sent. Error message: {}", ex.getMessage());
            return false;
        }
    }

    public boolean sendMail(String region, String fromEmail, Collection<String> toEmails, String subject, String htmlBody, String textBody) throws IOException {
        try {
            AmazonSimpleEmailService client =
                    AmazonSimpleEmailServiceClientBuilder.standard()
                            .withRegion(region).build();
            SendEmailRequest request = new SendEmailRequest()
                    .withSource(fromEmail)
                    .withDestination(new Destination().withToAddresses(toEmails))
                    .withMessage(new Message()
                            .withBody(new Body()
                                    .withHtml(new Content()
                                            .withCharset("UTF-8").withData(htmlBody))
                                    .withText(new Content()
                                            .withCharset("UTF-8").withData(textBody)))
                            .withSubject(new Content()
                                    .withCharset("UTF-8").withData(subject)));
            client.sendEmail(request);
            return true;
        } catch (Exception ex) {
            LOGGER.error("The email was not sent. Error message: {}", ex.getMessage());
            return false;
        }
    }

}
