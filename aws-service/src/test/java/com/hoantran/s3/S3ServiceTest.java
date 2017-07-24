/**
 * Created by Hoan Tran @ http://hoantran-it.blogspot.com
 *
 * Any modifications to this file must keep this entire header intact.
 *
 */
package com.hoantran.s3;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;

/**
 * @author hoan.tran
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class S3ServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3ServiceTest.class);

    private static S3Service s3Service;

    private final static String bucketName = "aws-service-demo";

    /**
     * We have to have slash (/) at the end because aws use it to identify it is folder or not
     */
    private final static String directoryPath = "test-data/";

    private final static String objectKey = directoryPath + "sample-text";

    private final static String sampleText = "AWS sample text";

    // Number of file will be put to S3
    private final static int n = 3;

    private final static String metaDataKey = "file-type";
    private final static String metaDataValue = "text";

    @BeforeClass
    public static void initialize() {
        s3Service = new S3Service();
        List<S3Object> s3ObjectList = s3Service.getS3ObjectList(bucketName, directoryPath);
        for (S3Object s3Object : s3ObjectList) {
            s3Service.deleteS3Object(bucketName, s3Object.getKey());
        }
    }

    @Test
    public void test_01_putS3Object() {
        InputStream input = new ByteArrayInputStream(sampleText.getBytes());
        s3Service.uploadS3ObjectSingleOperation(bucketName, objectKey, input, null);
    }

    @Test
    public void test_02_getS3Object() throws IOException {
        InputStream is = s3Service.getS3Object(bucketName, objectKey);
        String content = IOUtils.toString(is);
        assertEquals(content, sampleText);
    }

    @Test
    public void test_03_putMoreS3Object() {
        for (int i = 1; i <= n; i++) {
            InputStream input = new ByteArrayInputStream(sampleText.getBytes());
            s3Service.uploadS3ObjectMultipart(bucketName, objectKey + i, input, null);
        }
        List<S3Object> s3ObjectList = s3Service.getS3ObjectList(bucketName, directoryPath);
        assertEquals(n + 1, s3ObjectList.size());
    }

    @Test
    public void test_04_updateS3ObjectMetadata() {
        Map<String, String> userMetaData = new HashMap<>();
        userMetaData.put(metaDataKey, metaDataValue);
        s3Service.updateS3ObjectMetadata(bucketName, objectKey, userMetaData);
    }

    @Test
    public void test_05_getS3ObjectMetadata() {
        Map<String, String> userMetaData = s3Service.getS3ObjectMetadata(bucketName, objectKey);
        assertEquals(userMetaData.get(metaDataKey), metaDataValue);
    }

    @Test
    public void test_06_deleteS3Object() {
        s3Service.deleteS3Object(bucketName, objectKey);
        List<S3Object> s3ObjectList = s3Service.getS3ObjectList(bucketName, directoryPath);
        assertEquals(n, s3ObjectList.size());

        for (int i = 1; i <= n; i++) {
            s3Service.deleteS3Object(bucketName, objectKey + i);
        }

        s3ObjectList = s3Service.getS3ObjectList(bucketName, directoryPath);
        assertEquals(0, s3ObjectList.size());
    }

}
