package com.payline.payment.globalpos.service.impl;

import com.payline.payment.globalpos.MockUtils;
import com.payline.payment.globalpos.bean.response.GetTitreDetailTransac;
import com.payline.payment.globalpos.exception.InvalidDataException;
import com.payline.payment.globalpos.utils.http.HttpClient;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFormUpdated;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

class PaymentServiceImplTest {

    @InjectMocks
    PaymentServiceImpl service = new PaymentServiceImpl();

    @Mock
    private HttpClient client;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void paymentRequest() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequest();
        System.out.println(request.getTransactionId());
    }

    @Test
    void step1OK() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequest();
        Mockito.doReturn(MockUtils.getTransacOK()).when(client).getTransac(any(), any());
        PaymentResponse response = service.step1(request);
        Assertions.assertEquals(PaymentResponseFormUpdated.class, response.getClass());
    }

    @Test
    void step1KO() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequest();
        Mockito.doReturn(MockUtils.getTransacKO()).when(client).getTransac(any(), any());
        PaymentResponse response = service.step1(request);
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void step1KOError60() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequest();
        Mockito.doReturn(MockUtils.getTransacKO60()).when(client).getTransac(any(), any());
        PaymentResponse response = service.step1(request);
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void step1KOError30() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequest();
        Mockito.doReturn(MockUtils.getTransacKO30()).when(client).getTransac(any(), any());
        PaymentResponse response = service.step1(request);
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void step2() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequestNoRequestContextBuilder()
                .withPaymentFormContext(MockUtils.aPaymentFormContextStep2(MockUtils.getTitre()))
                .withRequestContext(MockUtils.aRequestContextBuilderStep("STEP2", MockUtils.getNumTransac()).build())
                .build();
        Mockito.doReturn(MockUtils.getTitreTransacOK()).when(client).getTitreDetailTransac(any(), any(), any());
        PaymentResponse response = service.step2(request);
        Assertions.assertEquals(PaymentResponseFormUpdated.class, response.getClass());
    }

    @Test
    void step2NoCabTitre() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequestNoRequestContextBuilder()
                .withRequestContext(MockUtils.aRequestContextBuilderStep("STEP2", null).build())
                .withPaymentFormContext(MockUtils.aPaymentFormContextStep2(null))
                .build();

        Throwable thrown = assertThrows(InvalidDataException.class,
                () -> service.step2(request));

        Assertions.assertEquals("issues with the PaymentFormContext", thrown.getMessage());
    }

    @Test
    void step2CheckEqual() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequestCheckEqualBuilder(MockUtils.getAmountValueEqualCheck())
                .withRequestContext(MockUtils.aRequestContextBuilderStep("STEP2", MockUtils.getNumTransac()).build())
                .build();
        Mockito.doReturn(MockUtils.getTitreTransacOK()).when(client).getTitreDetailTransac(any(), any(), any());
        PaymentResponse response = service.step2(request);
        Assertions.assertEquals(PaymentResponseFormUpdated.class, response.getClass());
    }

    @Test
    void step2CheckLower() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequestCheckEqualBuilder(MockUtils.getAmountValueLowerCheck())
                .withRequestContext(MockUtils.aRequestContextBuilderStep("STEP2", MockUtils.getNumTransac()).build())
                .build();
        Mockito.doReturn(MockUtils.getTitreTransacOK()).when(client).getTitreDetailTransac(any(), any(), any());
        PaymentResponse response = service.step2(request);

        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void step2KO() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequestCheckEqualBuilder(MockUtils.getAmountValueLowerCheck())
                .withRequestContext(MockUtils.aRequestContextBuilderStep("STEP2", MockUtils.getNumTransac()).build())
                .build();
        Mockito.doReturn(MockUtils.getTitreTransacKO()).when(client).getTitreDetailTransac(any(), any(), any());
        PaymentResponse response = service.step2(request);
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void step2KOWrongAmount() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequestCheckEqualBuilder(MockUtils.getAmountValueLowerCheck())
                .withRequestContext(MockUtils.aRequestContextBuilderStep("STEP2", MockUtils.getNumTransac()).build())
                .build();
        Mockito.doReturn(MockUtils.getTitreTransacWrongAmount()).when(client).getTitreDetailTransac(any(), any(), any());

        Throwable thrown = assertThrows(NumberFormatException.class,
                () -> service.step2(request));

        Assertions.assertEquals("Amount in the check is not a valid number", thrown.getMessage());
    }

    @Test
    void step3OK() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequestNoRequestContextBuilder()
                .withPaymentFormContext(MockUtils.aPaymentFormContext())
                .withRequestContext(MockUtils.aRequestContextBuilderStep("STEP3", MockUtils.getNumTransac()).build())
                .build();
        Mockito.doReturn(MockUtils.setFinTransacOK()).when(client).setFinTransac(any(), any(), any());
        PaymentResponse response = service.step3(request, PaymentServiceImpl.STATUS.COMMIT);
        Assertions.assertEquals(PaymentResponseSuccess.class, response.getClass());
    }

    @Test
    void step3Rollback() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequestNoRequestContextBuilder()
                .withPaymentFormContext(MockUtils.aPaymentFormContext())
                .withRequestContext(MockUtils.aRequestContextBuilderStep("STEP3", MockUtils.getNumTransac()).build())
                .build();
        Mockito.doReturn(MockUtils.setFinTransacOK()).when(client).setFinTransac(any(), any(), any());
        PaymentResponse response = service.step3(request, PaymentServiceImpl.STATUS.ROLLBACK);
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void step3KO() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequestNoRequestContextBuilder()
                .withPaymentFormContext(MockUtils.aPaymentFormContext())
                .withRequestContext(MockUtils.aRequestContextBuilderStep("STEP3", MockUtils.getNumTransac()).build())
                .build();
        Mockito.doReturn(MockUtils.setFinTransacKO()).when(client).setFinTransac(any(), any(), any());
        PaymentResponse response = service.step3(request, PaymentServiceImpl.STATUS.ROLLBACK);
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void PSStep1() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequestNoRequestContextBuilder()
                .withPaymentFormContext(MockUtils.aPaymentFormContext())
                .withRequestContext(MockUtils.aRequestContextBuilderStep(null, MockUtils.getNumTransac()).build())
                .build();

        Mockito.doReturn(MockUtils.getTransacOK()).when(client).getTransac(any(), any());
        PaymentResponse response = service.paymentRequest(request);
        Assertions.assertEquals(PaymentResponseFormUpdated.class, response.getClass());
    }

    @Test
    void PSStep2() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequestNoRequestContextBuilder()
                .withPaymentFormContext(MockUtils.aPaymentFormContextStep2(MockUtils.getTitre()))
                .withRequestContext(MockUtils.aRequestContextBuilderStep("STEP2", MockUtils.getNumTransac()).build())
                .build();
        Mockito.doReturn(MockUtils.getTitreTransacOK()).when(client).getTitreDetailTransac(any(), any(), any());
        PaymentResponse response = service.paymentRequest(request);
        Assertions.assertEquals(PaymentResponseFormUpdated.class, response.getClass());
    }

    @Test
    void PSStep3() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequestNoRequestContextBuilder()
                .withPaymentFormContext(MockUtils.aPaymentFormContext())
                .withRequestContext(MockUtils.aRequestContextBuilderStep("STEP3", MockUtils.getNumTransac()).build())
                .build();
        Mockito.doReturn(MockUtils.setFinTransacOK()).when(client).setFinTransac(any(), any(), any());
        PaymentResponse response = service.paymentRequest(request);
        Assertions.assertEquals(PaymentResponseSuccess.class, response.getClass());
    }

    @Test
    void PSStepFaulse() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequestNoRequestContextBuilder()
                .withPaymentFormContext(MockUtils.aPaymentFormContext())
                .withRequestContext(MockUtils.aRequestContextBuilderStep("STEP4", MockUtils.getNumTransac()).build())
                .build();
//        Mockito.doReturn(MockUtils.setFinTransacOK()).when(client).setFinTransac(any(), any(), any());
        PaymentResponse response = service.paymentRequest(request);
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void date() throws ParseException {
        final String dateFormat = "dd/MM/yyyy";
        LocalDateTime localDateTime = LocalDateTime.now();
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        GetTitreDetailTransac response = GetTitreDetailTransac.fromXml(MockUtils.getTitreTransacOK());
        Date checkDate = new SimpleDateFormat(dateFormat).parse(response.getDateValid());

        Assertions.assertEquals(1, checkDate.compareTo(date));
    }
}