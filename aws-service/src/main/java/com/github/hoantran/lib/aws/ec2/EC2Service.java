/**
 * Created by Hoan Tran @ http://hoantran-it.blogspot.com
 *
 * Any modifications to this file must keep this entire header intact.
 *
 */
package com.github.hoantran.lib.aws.ec2;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.Filter;

/**
 * @author hoan.tran
 */
public class EC2Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(EC2Service.class);

    private AmazonEC2 ec2Client;

    public EC2Service() {
        this.ec2Client = getEC2Connection();
    }

    public EC2Service(String profile) {
        this.ec2Client = getEC2Connection(profile);
    }

    private AmazonEC2 getEC2Connection() {
        AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
        return ec2Client;
    }

    private AmazonEC2 getEC2Connection(String profile) {
        AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider(profile))
                .build();
        return ec2Client;
    }

    public boolean isAMIExist(Map<String, List<String>> tagValueMap) throws Exception {
        final DescribeImagesRequest request = new DescribeImagesRequest();
        for (Map.Entry<String, List<String>> tagValue : tagValueMap.entrySet()) {
            request.withFilters(new Filter().withName(tagValue.getKey()).withValues(tagValue.getValue()));
        }
        try {
            final DescribeImagesResult result = ec2Client.describeImages(request);
            return result.getImages().size() > 0;
        } catch (final Exception e) {
            LOGGER.error("Error when checking ami existence", e);
            return false;
        }
    }

}
