package com.payline.payment.globalpos.bean.response;

import com.payline.payment.globalpos.MockUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SetFinTransacTest {

    @Test
    void fromXml() {
        SetFinTransac value = SetFinTransac.fromXml(MockUtils.setFinTransacOK());
        Assertions.assertNotNull(value.getCodeErreur());
        Assertions.assertEquals("1", value.getCodeErreur());
    }
}