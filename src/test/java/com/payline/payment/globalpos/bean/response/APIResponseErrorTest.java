package com.payline.payment.globalpos.bean.response;

import com.payline.payment.globalpos.MockUtils;
import com.payline.payment.globalpos.bean.response.APIResponseError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class APIResponseErrorTest {

    @Test
    void fromXml() {
        APIResponseError value = APIResponseError.fromXml(MockUtils.getTransacKO());
        Assertions.assertNotNull(value.getHttpStatus());
        Assertions.assertNotNull(value.getError());
        Assertions.assertNotNull(value.getMessage());
        Assertions.assertEquals("200", value.getHttpStatus());
        Assertions.assertEquals("-50", value.getError());
        Assertions.assertEquals("Magasin inconnu de l'enseigne", value.getMessage());
    }
}