package org.recap.service.partnerservice;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 7/7/17.
 */
public class NullHostnameVerifierUT extends BaseTestCase{

    @Mock
    NullHostnameVerifier nullHostnameVerifier;

    @Test
    public void testNullHostnameVerifier(){
        Mockito.when(nullHostnameVerifier.verify(Mockito.any(),Mockito.any())).thenCallRealMethod();
        boolean isVerified = nullHostnameVerifier.verify(Mockito.any(),Mockito.any());
        assertTrue(isVerified);
    }

}