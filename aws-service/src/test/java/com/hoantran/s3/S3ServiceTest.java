/**
 * Created by Hoan Tran @ http://hoantran-it.blogspot.com
 *
 * Any modifications to this file must keep this entire header intact.
 *
 */
package com.hoantran.s3;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
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

    private final String bucketName = "i-think";

    private final String objectKey = "images/contributor/avatar.jpg";

    private final String uploadKey = "images/text.txt";

    private final String sampleText = "AWS sample text";

    /**
     * We have to have slash (/) at the end because aws use it to identify it is folder or not
     */
    private final String directoryPath = "now-platform/";

    @Test
    public void getS3ObjectList() {
        S3Service s3Service = new S3Service();
        List<S3Object> s3ObjectList = s3Service.getS3ObjectList(bucketName);
        LOGGER.info("Print S3 object list inside {} bucket", bucketName);
        printOutS3ObjectKey(s3ObjectList);
    }
    
    @Test
    public void getS3ObjectListInsideFolder() {
        S3Service s3Service = new S3Service();
        List<S3Object> s3ObjectList = s3Service.getS3ObjectList(bucketName, directoryPath);
        LOGGER.info("Print S3 object list inside {} bucket, folder {}", bucketName, directoryPath);
        printOutS3ObjectKey(s3ObjectList);
    }

    @Test
    public void getS3Object() {
        S3Service s3Service = new S3Service();
        S3Object s3Object = s3Service.getS3Object(bucketName, objectKey);
        LOGGER.info("Print S3 object with key {} inside {} bucket", bucketName, objectKey);
        printOutS3ObjectKey(Arrays.asList(s3Object));
    }

    @Test
    public void putS3ObjectFile() {
        S3Service s3Service = new S3Service();
        InputStream input = new ByteArrayInputStream(sampleText.getBytes());
        s3Service.putS3ObjectFile(bucketName, uploadKey, input, null);
    }

    private void printOutS3ObjectKey(List<S3Object> s3ObjectList) {
        for(S3Object s3Object:s3ObjectList){
            LOGGER.info("Object key: {}", s3Object.getKey());
        }
    }

}
