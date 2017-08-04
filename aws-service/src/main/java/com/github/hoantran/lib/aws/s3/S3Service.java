/**
 * Created by Hoan Tran @ http://hoantran-it.blogspot.com
 *
 * Any modifications to this file must keep this entire header intact.
 *
 */
package com.github.hoantran.lib.aws.s3;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
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
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.StringUtils;

/**
 * @author hoan.tran
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
            logError(ase);
        } catch (AmazonClientException ace) {
            logError(ace);
        } catch (Exception ace) {
            LOGGER.error("Error Message: {}", ace.getMessage());
        }
        return s3ObjectList;
    }

    public InputStream getS3Object(String bucket, String key) {
        InputStream stream = null;
        try {
            S3Object s3object = s3Client.getObject(new GetObjectRequest(
                    bucket, key));
            if (s3object != null) {
                return s3object.getObjectContent();
            }
        } catch (AmazonServiceException ase) {
            logError(ase);
        } catch (AmazonClientException ace) {
            logError(ace);
        }
        return stream;
    }

    public Map<String, String> getS3ObjectMetadata(String bucket, String key) {
        Map<String, String> metadata = null;
        try {
            S3Object s3object = s3Client.getObject(new GetObjectRequest(
                    bucket, key));
            if (s3object != null) {
                return s3object.getObjectMetadata().getUserMetadata();
            }
        } catch (AmazonServiceException ase) {
            logError(ase);
        } catch (AmazonClientException ace) {
            logError(ace);
        }
        return metadata;
    }

    public void uploadS3ObjectMultipart(String bucket, String key, InputStream input, Map<String, String> userMetaData) {
        try {
            TransferManager tm = TransferManagerBuilder.defaultTransferManager();
            byte[] bytes = IOUtils.toByteArray(input);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(bytes.length);
            metadata.setUserMetadata(userMetaData);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            PutObjectRequest request = new PutObjectRequest(bucket, key, byteArrayInputStream, metadata);
            Upload upload = tm.upload(request);
            upload.waitForCompletion();
        } catch (AmazonClientException ace) {
            logError(ace);
        } catch (IOException e) {
            LOGGER.error("Upload fail: {}", e);
        } catch (InterruptedException e) {
            LOGGER.error("Upload fail: {}", e);
        }
    }

    public void uploadS3ObjectSingleOperation(String bucket, String key, InputStream input, Map<String, String> userMetaData) {
        try {
            LOGGER.debug("Uploading a new object to S3");
            byte[] bytes = IOUtils.toByteArray(input);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(bytes.length);
            metadata.setUserMetadata(userMetaData);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            PutObjectRequest request = new PutObjectRequest(bucket, key, byteArrayInputStream, metadata);
            s3Client.putObject(request);
        } catch (AmazonServiceException ase) {
            logError(ase);
        } catch (AmazonClientException ace) {
            logError(ace);
        } catch (IOException e) {
            LOGGER.error("Upload fail: {}", e);
        }
    }

    /**
     * Note: We must explicitly specify all the user configurable metadata,
     * even if we are only changing only one of the metadata values.
     * http://docs.aws.amazon.com/AmazonS3/latest/dev/CopyingObjectsExamples.html
     * 
     * @param bucket S3 bucket
     * @param key S3 object key
     * @param userMetaData S3 object metadata
     */
    public void updateS3ObjectMetadata(String bucket, String key, Map<String, String> userMetaData) {
        try {
            LOGGER.debug("Update metadata for object: {}", key);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setUserMetadata(userMetaData);
            CopyObjectRequest request = new CopyObjectRequest(bucket, key, bucket, key)
                    .withNewObjectMetadata(metadata);
            s3Client.copyObject(request);
        } catch (AmazonServiceException ase) {
            logError(ase);
        } catch (AmazonClientException ace) {
            logError(ace);
        }
    }

    public void deleteS3Object(String bucket, String key) {
        try {
            s3Client.deleteObject(new DeleteObjectRequest(bucket, key));
        } catch (AmazonServiceException ase) {
            logError(ase);
        } catch (AmazonClientException ace) {
            logError(ace);
        }
    }

    public void logError(AmazonServiceException ase) {
        LOGGER.error("Caught an AmazonServiceException, which means your request made it to Amazon S3, but was rejected with an error response for some reason.");
        LOGGER.error("Error Message: {}", ase.getMessage());
        LOGGER.error("HTTP Status Code: {}", ase.getStatusCode());
        LOGGER.error("AWS Error Code: {}", ase.getErrorCode());
        LOGGER.error("Error Type: {}", ase.getErrorType());
        LOGGER.error("Request ID: {}", ase.getRequestId());
    }

    public void logError(AmazonClientException ace) {
        LOGGER.error("Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
        LOGGER.error("Error Message: {}", ace.getMessage());
    }
}
