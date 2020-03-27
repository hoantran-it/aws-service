/**
 * Created by Hoan Tran @ http://hoantran-it.blogspot.com
 *
 * Any modifications to this file must keep this entire header intact.
 *
 */
package com.github.hoantran.lib.aws.sns;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author hoan.tran
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SNSServiceTest {

    private static SNSService snsService;

    @BeforeClass
    public static void initialize() {
        snsService = new SNSService();
    }

    @Test
    public void test_01_sendMessage() throws IOException {
        snsService.sendSMS("+84999999999", "Hello world!!!");
    }

    @Test
    public void test_01_sendNotification() throws IOException {
        String topicArn = "topicArn";
        String message = "Hello world!!!";
        snsService.sendNotification(topicArn, message);
    }

}
