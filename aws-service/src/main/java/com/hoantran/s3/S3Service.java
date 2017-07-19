/**
 * Created by Hoan Tran @ http://hoantran-it.blogspot.com
 *
 * Any modifications to this file must keep this entire header intact.
 *
 */
package com.hoantran.s3;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.util.StringUtils;

/**
 * @author hoan.tran
 *
 */
public class S3Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3Service.class);

    private AmazonS3 s3Client;

    public S3Service() {
        this.s3Client = getS3Connection();
    }

    public S3Service(String profile) {
        this.s3Client = getS3Connection(profile);
    }

    public S3Service(String accessKeyId, String secretAccessKey) {
        this.s3Client = getS3Connection(accessKeyId, secretAccessKey);
    }

    private AmazonS3 getS3Connection() {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
        return s3Client;
    }

    private AmazonS3 getS3Connection(String profile) {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider(profile))
                .build();
        return s3Client;
    }

    private AmazonS3 getS3Connection(String accessKeyId, String secretAccessKey) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        return new AmazonS3Client(credentials);
    }

    private boolean isImmediateDescendant(String parent, String child) {
        // If directory path null or empty, always return true
        if (StringUtils.isNullOrEmpty(parent)) {
            return true;
        }
        if (!child.startsWith(parent)) {
            LOGGER.warn("Invalid child {} for parent {}", child, parent);
            return false;
        }
        final int parentLen = parent.length();
        final String childWithoutParent = child.substring(parentLen);
        if (StringUtils.isNullOrEmpty(childWithoutParent) || childWithoutParent.contains("/")) {
            return false;
        }
        return true;
    }

    public List<S3Object> getS3ObjectList(String bucketName) {
        return getS3ObjectList(bucketName, null);
    }

    public List<S3Object> getS3ObjectList(String bucketName, String directoryPath) {
        List<S3Object> s3ObjectList = new ArrayList<S3Object>();
        try {
            ListObjectsV2Request req = new ListObjectsV2Request()
                    .withBucketName(bucketName);
            if (!StringUtils.isNullOrEmpty(directoryPath)) {
                req.withPrefix(directoryPath);
            }
            ListObjectsV2Result result;
            do {
                result = s3Client.listObjectsV2(req);
                S3Object s3object = null;
                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                    if (isImmediateDescendant(directoryPath, objectSummary.getKey())) {
                        s3object = s3Client.getObject(new GetObjectRequest(bucketName, objectSummary.getKey()));
                        s3ObjectList.add(s3object);
                    }
                }
            } while (result.isTruncated());

        } catch (AmazonServiceException ase) {
            LOGGER.warn("Caught an AmazonServiceException, which means your request made it to Amazon S3, but was rejected with an error response for some reason.");
            LOGGER.warn("Error Message: {}", ase.getMessage());
            LOGGER.warn("HTTP Status Code: {}", ase.getStatusCode());
            LOGGER.warn("AWS Error Code: {}", ase.getErrorCode());
            LOGGER.warn("Error Type: {}", ase.getErrorType());
            LOGGER.warn("Request ID: {}", ase.getRequestId());
        } catch (AmazonClientException ace) {
            LOGGER.warn("Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
            LOGGER.warn("Error Message: {}", ace.getMessage());
        } catch (Exception ace) {
            LOGGER.warn("Error Message: {}", ace.getMessage());
        }
        return s3ObjectList;
    }

    public S3Object getS3Object(String bucket, String key) {
        S3Object s3object = null;
        try {
            s3object = s3Client.getObject(new GetObjectRequest(
                    bucket, key));
        } catch (AmazonServiceException ase) {
            LOGGER.warn("Caught an AmazonServiceException, which means your request made it to Amazon S3, but was rejected with an error response for some reason.");
            LOGGER.warn("Error Message: {}", ase.getMessage());
            LOGGER.warn("HTTP Status Code: {}", ase.getStatusCode());
            LOGGER.warn("AWS Error Code: {}", ase.getErrorCode());
            LOGGER.warn("Error Type: {}", ase.getErrorType());
            LOGGER.warn("Request ID: {}", ase.getRequestId());
        } catch (AmazonClientException ace) {
            LOGGER.warn("Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
            LOGGER.warn("Error Message: {}", ace.getMessage());
        }
        return s3object;
    }

    public InputStream getS3ObjectFile(String bucket, String key) {
        return getS3Object(bucket, key).getObjectContent();
    }

    public void putS3ObjectFile(String bucket, String key, InputStream input, Map<String, String> userMetaData) {
        TransferManager tm = TransferManagerBuilder.defaultTransferManager();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setUserMetadata(userMetaData);
        PutObjectRequest request = new PutObjectRequest(bucket, key, input, metadata);
        Upload upload = tm.upload(request);
        try {
            upload.waitForCompletion();
        } catch (AmazonClientException | InterruptedException amazonClientException) {
            LOGGER.error("Unable to upload file, upload aborted.");
            amazonClientException.printStackTrace();
        }
    }
}
