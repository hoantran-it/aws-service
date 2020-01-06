/**
 * Created by Hoan Tran @ http://hoantran-it.blogspot.com
 *
 * Any modifications to this file must keep this entire header intact.
 *
 */
package com.github.hoantran.lib.aws.ses;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.amazonaws.regions.Regions;
import com.github.hoantran.lib.aws.ses.SESService;

/**
 * @author hoan.tran
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SESServiceTest {

    private static SESService sesService;

    static final String FROM = "sender@example.com";

    static final String TO = "recipient@example.com";

    static final String SUBJECT = "Amazon SES test (AWS SDK for Java)";

    static final String HTMLBODY = "<h1>Amazon SES test (AWS SDK for Java)</h1>"
            + "<p>This email was sent with <a href='https://aws.amazon.com/ses/'>"
            + "Amazon SES</a> using the <a href='https://aws.amazon.com/sdk-for-java/'>"
            + "AWS SDK for Java</a>";

    static final String TEXTBODY = "This email was sent through Amazon SES "
            + "using the AWS SDK for Java.";

    @BeforeClass
    public static void initialize() {
        sesService = new SESService();
    }

    @Test
    public void test_01_sendMail() throws IOException {
        assertTrue(sesService.sendMail(Regions.US_WEST_2.toString(), FROM, TO, SUBJECT, HTMLBODY, TEXTBODY));
    }

}
