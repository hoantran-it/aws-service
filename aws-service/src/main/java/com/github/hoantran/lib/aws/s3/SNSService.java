/**
 * Created by Hoan Tran @ http://hoantran-it.blogspot.com
 *
 * Any modifications to this file must keep this entire header intact.
 *
 */
package com.github.hoantran.lib.aws.s3;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

/**
 * @author hoan.tran
 */
public class SNSService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SNSService.class);

    private AmazonSNS snsClient;

    public SNSService() {
        this.snsClient = AmazonSNSClientBuilder.defaultClient();
    }

    public boolean sendSMS(String phoneNumber, String message) {
        try {
            Map<String, MessageAttributeValue> smsAttributes =
                    new HashMap<String, MessageAttributeValue>();
            smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
                    .withStringValue("0.50") // Sets the max price to 0.50 USD.
                    .withDataType("Number"));
            smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                    .withStringValue("Transactional") // Sets the type to Transactional.
                    .withDataType("String"));

            PublishResult result = snsClient.publish(new PublishRequest()
                    .withMessage(message)
                    .withPhoneNumber(phoneNumber)
                    .withMessageAttributes(smsAttributes));

            LOGGER.info("The SMS was sent. Message: {}", result);
            return true;
        } catch (Exception ex) {
            LOGGER.error("The SMS was not sent. Error message: {}", ex.getMessage());
            return false;
        }
    }

}
