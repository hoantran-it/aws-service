/**
 * Created by Hoan Tran @ http://hoantran-it.blogspot.com
 *
 * Any modifications to this file must keep this entire header intact.
 *
 */
package com.hoantran.s3;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.model.S3Object;

/**
 * @author hoan.tran
 *
 */
public class S3ServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3ServiceTest.class);

    private final String accessKeyId = "AKIAI4TSWJGBJAI6SO7Q";

    private final String secretAccessKey = "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";

    private final String bucketName = "aws-service-demo";

    /**
     * We have to have slash (/) at the end because aws use it to identify it is folder or not
     */
    private final String directoryPath = "image/";

    @Test
    public void getS3Object() {
        S3Service s3Service = new S3Service(accessKeyId, secretAccessKey);
        List<S3Object> s3ObjectList = s3Service.getS3Object(bucketName);
        LOGGER.info("Print S3 object list inside {} bucket", bucketName);
        printOutS3Object(s3ObjectList);
    }
    
    @Test
    public void getS3ObjectInsideFolder() {
        S3Service s3Service = new S3Service(accessKeyId, secretAccessKey);
        List<S3Object> s3ObjectList = s3Service.getS3Object(bucketName, directoryPath);
        LOGGER.info("Print S3 object list inside {} bucket, folder {}", bucketName, directoryPath);
        printOutS3Object(s3ObjectList);
    }

    private void printOutS3Object(List<S3Object> s3ObjectList) {
        for(S3Object s3Object:s3ObjectList){
            LOGGER.info("Object key: {}", s3Object.getKey());
        }
    }
}
