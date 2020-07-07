package com.payline.payment.globalpos;

import com.payline.payment.globalpos.utils.Constants;
import com.payline.payment.globalpos.utils.http.StringResponse;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.payment.*;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicStatusLine;
import org.mockito.internal.util.reflection.FieldSetter;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class MockUtils {


    private static String TRANSACTIONID = "123456789012345678901";
    private static String PARTNER_TRANSACTIONID = "1234";

    private static String amountValue = "1234";
    private static String amountValueEqualCheck = "1000";
    private static String amountValueLowerCheck = "800";
    private static int refundAmount = 1;


    static final Date date = new Date();
    static final String dateFormat = "yyyy-MM-dd";
    static LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

    /*------------------------------------------------------------------------------------------------------------------*/

    private static final String guid = "819ed9dc7f85075e771043072a6e8681";
    private static final String codeMagasin = "ABBEV";
    private static final String numeroCaisse = "Payline";
    private static final String titre = "25394000194000103576958078172010123902001000";

    private static final String numTransac = "5e7db72846ebd";


    /**------------------------------------------------------------------------------------------------------------------*/

    /**
     * Generate a valid {@link Environment}.
     */
    public static Environment anEnvironment() {
        return new Environment("https://example.org/store/notification",
                "https://succesurl.com/",
                "http://redirectionCancelURL.com",
                true);
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a valid {@link PartnerConfiguration}.
     */
    public static PartnerConfiguration aPartnerConfiguration() {
        Map<String, String> partnerConfigurationMap = new HashMap<>();
        Map<String, String> sensitiveConfigurationMap = new HashMap<>();
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.URL, "https://ws-recette.easy2play.fr/wstransac/");
        return new PartnerConfiguration(partnerConfigurationMap, sensitiveConfigurationMap);
    }
    /**------------------------------------------------------------------------------------------------------------------*/


    /**
     * Generate a valid {@link PaymentFormConfigurationRequest}.
     */
    public static PaymentFormConfigurationRequest aPaymentFormConfigurationRequest() {
        return aPaymentFormConfigurationRequestBuilder().build();
    }

    /**
     * Generate a builder for a valid {@link PaymentFormConfigurationRequest}.
     * This way, some attributes may be overridden to match specific test needs.
     */
    public static PaymentFormConfigurationRequest.PaymentFormConfigurationRequestBuilder aPaymentFormConfigurationRequestBuilder() {
        return PaymentFormConfigurationRequest.PaymentFormConfigurationRequestBuilder.aPaymentFormConfigurationRequest()
                .withAmount(aPaylineAmount())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withLocale(Locale.FRANCE)
                .withOrder(aPaylineOrder())
                .withPartnerConfiguration(aPartnerConfiguration());

    }

    /**
     * Generate a valid {@link PaymentFormLogoRequest}.
     */
    public static PaymentFormLogoRequest aPaymentFormLogoRequest() {
        return PaymentFormLogoRequest.PaymentFormLogoRequestBuilder.aPaymentFormLogoRequest()
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withLocale(Locale.getDefault())
                .build();
    }

    /**
     * Generate a valid, but not complete, {@link Order}
     */
    public static Order aPaylineOrder() {
        List<Order.OrderItem> items = new ArrayList<>();

        items.add(Order.OrderItem.OrderItemBuilder
                .anOrderItem()
                .withReference("foo")
                .withAmount(aPaylineAmount())
                .withQuantity((long) 1)
                .build());

        return Order.OrderBuilder.anOrder()
                .withDate(new Date())
                .withAmount(aPaylineAmount())
                .withItems(items)
                .withReference("ref-20191105153749")
                .build();
    }

    /**
     * Generate a valid Payline Amount.
     */
    public static Amount aPaylineAmount() {
        return aPaylineAmount(Integer.parseInt(amountValue));
    }

    public static Amount aPaylineAmountCheckEqual() {
        return aPaylineAmount(Integer.parseInt(amountValueEqualCheck));
    }

    public static Amount aPaylineRefundAmount() {
        return aPaylineAmount(refundAmount);
    }

    public static Amount aPaylineAmount(int amount) {
        return new Amount(BigInteger.valueOf(amount), Currency.getInstance("EUR"));
    }


    /**
     * @return a valid user agent.
     */
    public static String aUserAgent() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:67.0) Gecko/20100101 Firefox/67.0";
    }

    /**
     * Generate a valid {@link Browser}.
     */
    public static Browser aBrowser() {
        return Browser.BrowserBuilder.aBrowser()
                .withLocale(Locale.getDefault())
                .withIp("192.168.0.1")
                .withUserAgent(aUserAgent())
                .build();
    }

    /**
     * Generate a valid {@link Buyer}.
     */
    public static Buyer aBuyer() {
        return Buyer.BuyerBuilder.aBuyer()
                .withFullName(new Buyer.FullName("Marie", "Durand", "1"))
                .withBirthday(new Date())
                .withAddresses(addresses())
                .withPhoneNumbers(phoneNumbers())
                .withEmail("foo@bar.baz")
                .build();
    }

    public static Map<Buyer.AddressType, Buyer.Address> addresses() {
        Map<Buyer.AddressType, Buyer.Address> addresses = new HashMap<>();
        addresses.put(Buyer.AddressType.BILLING, anAddress());
        addresses.put(Buyer.AddressType.DELIVERY, anAddress());

        return addresses;
    }

    public static Buyer.Address anAddress() {
        return Buyer.Address.AddressBuilder
                .anAddress()
                .withStreet1("street1")
                .withStreet2("street2")
                .withCity("New York")
                .withCountry("USA")
                .withZipCode("10016")
                .withState("NY")
                .build();
    }

    public static Map<Buyer.PhoneNumberType, String> phoneNumbers() {
        Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
        phoneNumbers.put(Buyer.PhoneNumberType.HOME, "0612345678");
        phoneNumbers.put(Buyer.PhoneNumberType.WORK, "0712345678");
        phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, "0612345678");
        phoneNumbers.put(Buyer.PhoneNumberType.BILLING, "0612345678");

        return phoneNumbers;
    }

    /**
     * Generate a valid {@link PaymentFormContext}.
     */
    public static PaymentFormContext aPaymentFormContext() {
        Map<String, String> paymentFormParameter = new HashMap<>();

        return PaymentFormContext.PaymentFormContextBuilder.aPaymentFormContext()
                .withPaymentFormParameter(paymentFormParameter)
                .withSensitivePaymentFormParameter(new HashMap<>())
                .build();
    }

    public static PaymentFormContext aPaymentFormContextStep2(String titre) {
        Map<String, String> paymentFormParameter = new HashMap<>();
        paymentFormParameter.put(Constants.FormConfigurationKeys.CABTITRE, titre);

        return PaymentFormContext.PaymentFormContextBuilder.aPaymentFormContext()
                .withPaymentFormParameter(paymentFormParameter)
                .withSensitivePaymentFormParameter(new HashMap<>())
                .build();
    }

    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a valid {@link ContractParametersCheckRequest}.
     */
    public static ContractParametersCheckRequest aContractParametersCheckRequest() {
        return aContractParametersCheckRequestBuilder().build();
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a builder for a valid {@link ContractParametersCheckRequest}.
     * This way, some attributes may be overridden to match specific test needs.
     */
    public static ContractParametersCheckRequest.CheckRequestBuilder aContractParametersCheckRequestBuilder() {
        return ContractParametersCheckRequest.CheckRequestBuilder.aCheckRequest()
                .withAccountInfo(anAccountInfo())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withLocale(Locale.getDefault())
                .withPartnerConfiguration(aPartnerConfiguration());
    }

    /**
     * Generate a valid {@link PaymentRequest}.
     */
    public static PaymentRequest aPaylinePaymentRequest() {
        return aPaylinePaymentRequestBuilder().build();
    }

    /**
     * Generate a builder for a valid {@link PaymentRequest}.
     * This way, some attributes may be overridden to match specific test needs.
     */
    public static PaymentRequest.Builder aPaylinePaymentRequestBuilder() {
        return PaymentRequest.builder()
                .withAmount(aPaylineAmount())
                .withBrowser(aBrowser())
                .withBuyer(aBuyer())
                .withCaptureNow(true)
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withLocale(Locale.getDefault())
                .withOrder(aPaylineOrder())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withPaymentFormContext(aPaymentFormContext())
                .withSoftDescriptor("softDescriptor")
                .withTransactionId(TRANSACTIONID);
    }

    public static PaymentRequest.Builder aPaylinePaymentRequestNoRequestContextBuilder() {
        return PaymentRequest.builder()
                .withAmount(aPaylineAmount())
                .withBrowser(aBrowser())
                .withBuyer(aBuyer())
                .withCaptureNow(true)
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withLocale(Locale.getDefault())
                .withOrder(aPaylineOrder())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withSoftDescriptor("softDescriptor")
                .withTransactionId(TRANSACTIONID);
    }

    public static PaymentRequest.Builder aPaylinePaymentRequestCheckEqualBuilder(String amount) {
        return PaymentRequest.builder()
                .withAmount(aPaylineAmount(Integer.parseInt(amount)))
                .withBrowser(aBrowser())
                .withBuyer(aBuyer())
                .withCaptureNow(true)
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withLocale(Locale.getDefault())
                .withOrder(aPaylineOrder())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withPaymentFormContext(aPaymentFormContextStep2(titre))
                .withSoftDescriptor("softDescriptor")
                .withTransactionId(TRANSACTIONID);
    }

    public static RefundRequest aPaylineRefundRequest() {
        return aPaylineRefundRequestBuilder().build();
    }

    public static RefundRequest.RefundRequestBuilder aPaylineRefundRequestBuilder() {
        return RefundRequest.RefundRequestBuilder.aRefundRequest()
                .withAmount(aPaylineRefundAmount())
                .withOrder(aPaylineOrder())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withTransactionId(TRANSACTIONID)
                .withPartnerTransactionId(PARTNER_TRANSACTIONID)
                .withRequestContext(aRequestContext())
                .withPartnerConfiguration(aPartnerConfiguration());
    }


    public static ResetRequest aPaylineResetRequest() {
        return aPaylineResetRequestBuilder().build();
    }

    public static ResetRequest.ResetRequestBuilder aPaylineResetRequestBuilder() {
        return ResetRequest.ResetRequestBuilder.aResetRequest()
                .withAmount(aPaylineAmount())
                .withOrder(aPaylineOrder())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withTransactionId(TRANSACTIONID)
                .withPartnerTransactionId(PARTNER_TRANSACTIONID)
                .withRequestContext(aRequestContext())
                .withPartnerConfiguration(aPartnerConfiguration());
    }

    public static NotificationRequest aPaylineNotificationRequest() {
        return aPaylineNotificationRequestBuilder().build();
    }

    public static NotificationRequest.NotificationRequestBuilder aPaylineNotificationRequestBuilder() {
        return NotificationRequest.NotificationRequestBuilder.aNotificationRequest()
                .withHeaderInfos(new HashMap<>())
                .withPathInfo("transactionDeId=1234567890123")
                .withHttpMethod("POST")
                .withContractConfiguration(aContractConfiguration())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withContent(new ByteArrayInputStream("".getBytes()))
                .withEnvironment(anEnvironment());
    }


    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a valid accountInfo, an attribute of a {@link ContractParametersCheckRequest} instance.
     */
    public static Map<String, String> anAccountInfo() {
        return anAccountInfo(aContractConfiguration());
    }
    /**------------------------------------------------------------------------------------------------------------------*/

    /**
     * Generate a valid accountInfo, an attribute of a {@link ContractParametersCheckRequest} instance,
     * from the given {@link ContractConfiguration}.
     *
     * @param contractConfiguration The model object from which the properties will be copied
     */
    public static Map<String, String> anAccountInfo(ContractConfiguration contractConfiguration) {
        Map<String, String> accountInfo = new HashMap<>();
        for (Map.Entry<String, ContractProperty> entry : contractConfiguration.getContractProperties().entrySet()) {
            accountInfo.put(entry.getKey(), entry.getValue().getValue());
        }
        return accountInfo;
    }

    /**
     * Generate a valid {@link ContractConfiguration}.
     */
    public static ContractConfiguration aContractConfiguration() {
        Map<String, ContractProperty> contractProperties = new HashMap<>();
        contractProperties.put(Constants.ContractConfigurationKeys.GUID, new ContractProperty(guid));
        contractProperties.put(Constants.ContractConfigurationKeys.CODEMAGASIN, new ContractProperty(codeMagasin));
        contractProperties.put(Constants.ContractConfigurationKeys.NUMEROCAISSE, new ContractProperty(numeroCaisse));
        return new ContractConfiguration("globalpos", contractProperties);
    }

    /**
     * Generate an invalid {@link ContractConfiguration}.
     */
    public static ContractConfiguration aContractConfigurationNoMap(Map<String, ContractProperty> contractProperties) {
        return new ContractConfiguration("globalpos", contractProperties);
    }


    /**
     * Generate a valid {@link RedirectionPaymentRequest}.
     */
    public static RedirectionPaymentRequest aRedirectionPaymentRequest() {
        return RedirectionPaymentRequest.builder()
                .withAmount(aPaylineAmount())
                .withBrowser(aBrowser())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withOrder(aPaylineOrder())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withTransactionId(TRANSACTIONID)
                .build();
    }

    public static RedirectionPaymentRequest aPaylineRedirectionPaymentRequest() {
        return aRedirectionPaymentRequestBuilder().build();
    }

    public static PaymentRequest.Builder<RedirectionPaymentRequest> aRedirectionPaymentRequestBuilder() {
        return (PaymentRequest.Builder<RedirectionPaymentRequest>) RedirectionPaymentRequest.builder()
                .withAmount(aPaylineAmount())
                .withBrowser(aBrowser())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withOrder(aPaylineOrder())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withTransactionId(TRANSACTIONID);
    }


    /**
     * Generate a valid {@link TransactionStatusRequest}.
     */
    public static TransactionStatusRequest aTransactionStatusRequest() {
        return TransactionStatusRequest.TransactionStatusRequestBuilder.aNotificationRequest()
                .withAmount(aPaylineAmount())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withOrder(aPaylineOrder())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withTransactionId(PARTNER_TRANSACTIONID)
                .build();
    }


    /**
     * Moch a StringResponse with the given elements.
     *
     * @param statusCode    The HTTP status code (ex: 200, 403)
     * @param statusMessage The HTTP status message (ex: "OK", "Forbidden")
     * @param content       The response content as a string
     * @param headers       The response headers
     * @return A mocked StringResponse
     */
    public static StringResponse mockStringResponse(int statusCode, String statusMessage, String content, Map<String, String> headers) {
        StringResponse response = new StringResponse();

        try {
            if (content != null && !content.isEmpty()) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("content"), content);
            }
            if (headers != null && headers.size() > 0) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("headers"), headers);
            }
            if (statusCode >= 100 && statusCode < 600) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("statusCode"), statusCode);
            }
            if (statusMessage != null && !statusMessage.isEmpty()) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("statusMessage"), statusMessage);
            }
        } catch (NoSuchFieldException e) {
            // This would happen in a testing context: spare the exception throw, the test case will probably fail anyway
            return null;
        }

        return response;
    }

    /**
     * Mock an HTTP APIResponseError with the given elements.
     *
     * @param statusCode    The status code (ex: 200)
     * @param statusMessage The status message (ex: "OK")
     * @param content       The response content/body
     * @return A mocked HTTP response
     */
    public static CloseableHttpResponse mockHttpResponse(int statusCode, String statusMessage, String content, Header[] headers) {
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        doReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), statusCode, statusMessage))
                .when(response).getStatusLine();
        doReturn(new StringEntity(content, StandardCharsets.UTF_8)).when(response).getEntity();
        if (headers != null && headers.length >= 1) {
            doReturn(headers).when(response).getAllHeaders();
        } else {
            doReturn(new Header[]{}).when(response).getAllHeaders();
        }
        return response;
    }


    /**
     * build a valid request context for template
     *
     * @return RequestContext
     */
    public static RequestContext aRequestContext() {
        return aRequestContextBuilder().build();
    }


    /**
     * RequestContextBuilder valid, can be completed for other issues
     *
     * @return RequestContextBuilder
     */
    public static RequestContext.RequestContextBuilder aRequestContextBuilder() {
        Map<String, String> requestSensitiveData = new HashMap<>();
        Map<String, String> requestData = new HashMap<>();
        return RequestContext.RequestContextBuilder.aRequestContext()
                .withRequestData(requestData)
                .withSensitiveRequestData(requestSensitiveData);
    }

    public static RequestContext.RequestContextBuilder aRequestContextBuilderStep(String contextData, String numTransac) {
        Map<String, String> requestSensitiveData = new HashMap<>();
        Map<String, String> requestData = new HashMap<>();
        requestData.put(Constants.RequestContextKeys.CONTEXT_DATA_STEP, contextData);
        requestData.put(Constants.RequestContextKeys.NUMTRANSAC, numTransac);
        return RequestContext.RequestContextBuilder.aRequestContext()
                .withRequestData(requestData)
                .withSensitiveRequestData(requestSensitiveData);
    }


    public static String getTransacOK() {
        return "<xml>\n" +
                "  <codeErreur>1</codeErreur>\n" +
                "  <NumTransac>5e7db72846ebd</NumTransac>\n" +
                "</xml>";
    }

    public static String getTransacKO() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<xml>\n" +
                "  <http_status>200</http_status>\n" +
                "  <error>-50</error>\n" +
                "  <message>Magasin inconnu de l'enseigne</message>\n" +
                "  <detail/>\n" +
                "</xml>";
    }

    public static String getTransacKO60() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<xml>\n" +
                "  <http_status>200</http_status>\n" +
                "  <error>-60</error>\n" +
                "  <message>un message</message>\n" +
                "  <detail/>\n" +
                "</xml>";
    }

    public static String noXml() {
        return "pas du xml";
    }

    public static String getTransacKO30() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<xml>\n" +
                "  <http_status>200</http_status>\n" +
                "  <error>-30</error>\n" +
                "  <message>un message</message>\n" +
                "  <detail/>\n" +
                "</xml>";
    }

    public static String getTitreTransacOK() {
        return "<xml>\n" +
                "  <codeErreur>1</codeErreur>\n" +
                "  <titre>INCO</titre>\n" +
                "  <emetteur>940001</emetteur>\n" +
                "  <montant>10</montant>\n" +
                "  <dateValid>12/10/2020</dateValid>\n" +
                "  <numTitre>3135057060</numTitre>\n" +
                "  <ID>5e81e6db55962</ID>\n" +
                "</xml>";
    }

    public static String getTitreTransacWrongAmount() {
        return "<xml>\n" +
                "  <codeErreur>1</codeErreur>\n" +
                "  <titre>INCO</titre>\n" +
                "  <emetteur>940001</emetteur>\n" +
                "  <montant>1a</montant>\n" +
                "  <dateValid>12/10/2020</dateValid>\n" +
                "  <numTitre>3135057060</numTitre>\n" +
                "  <ID>5e81e6db55962</ID>\n" +
                "</xml>";
    }

    public static String getTitreTransacKO() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<xml>\n" +
                "  <http_status>200</http_status>\n" +
                "  <error>-100</error>\n" +
                "  <message>La transaction actuelle n'existe pas</message>\n" +
                "  <detail/>\n" +
                "</xml>\n";
    }

    public static String setFinTransacOK() {
        return "<xml>\n" +
                "  <codeErreur>1</codeErreur>\n" +
                "</xml>";
    }

    public static String setFinTransacKO() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<xml>\n" +
                "  <http_status>200</http_status>\n" +
                "  <error>-100</error>\n" +
                "  <message>La transaction actuelle n'existe pas</message>\n" +
                "  <detail/>\n" +
                "</xml>";
    }

    public static String getTRANSACTIONID() {
        return TRANSACTIONID;
    }

    public static String getAmountValue() {
        return amountValue;
    }

    public static int getRefundAmount() {
        return refundAmount;
    }

    public static Date getDate() {
        return date;
    }

    public static String getDateFormat() {
        return dateFormat;
    }

    public static LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public static String getGuid() {
        return guid;
    }

    public static String getCodeMagasin() {
        return codeMagasin;
    }

    public static String getNumeroCaisse() {
        return numeroCaisse;
    }

    public static String getTitre() {
        return titre;
    }

    public static String getNumTransac() {
        return numTransac;
    }

    public static String getAmountValueEqualCheck() {
        return amountValueEqualCheck;
    }

    public static String getAmountValueLowerCheck() {
        return amountValueLowerCheck;
    }
}