/**
 * Created by Hoan Tran @ http://hoantran-it.blogspot.com
 *
 * Any modifications to this file must keep this entire header intact.
 *
 */
package com.github.hoantran.lib.aws.ec2;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hoantran.lib.aws.ec2.EC2Service;

/**
 * @author hoan.tran
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EC2ServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(EC2ServiceTest.class);

    private static EC2Service ec2Service;

    @BeforeClass
    public static void initialize() {
        ec2Service = new EC2Service();
    }

    @Test
    public void test_01_isAMIExist() throws Exception {
        HashMap<String, List<String>> tagValueMap = new HashMap<String, List<String>>();
        tagValueMap.put("name", Arrays.asList("jenkins-ami"));
        assertTrue(ec2Service.isAMIExist(tagValueMap));
    }

}
