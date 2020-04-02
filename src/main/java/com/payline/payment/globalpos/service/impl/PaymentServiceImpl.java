package com.payline.payment.globalpos.service.impl;

import com.payline.payment.globalpos.bean.AmountParse;
import com.payline.payment.globalpos.bean.configuration.RequestConfiguration;
import com.payline.payment.globalpos.bean.response.APIResponseError;
import com.payline.payment.globalpos.bean.response.GetTitreDetailTransac;
import com.payline.payment.globalpos.bean.response.GetTransac;
import com.payline.payment.globalpos.bean.response.SetFinTransac;
import com.payline.payment.globalpos.exception.InvalidDataException;
import com.payline.payment.globalpos.exception.PluginException;
import com.payline.payment.globalpos.utils.Constants;
import com.payline.payment.globalpos.utils.PluginUtils;
import com.payline.payment.globalpos.utils.http.HttpClient;
import com.payline.payment.globalpos.utils.i18n.I18nService;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.RequestContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFormUpdated;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.bean.paymentform.bean.field.*;
import com.payline.pmapi.bean.paymentform.bean.form.CardForm;
import com.payline.pmapi.bean.paymentform.bean.form.CustomForm;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentService;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

public class PaymentServiceImpl implements PaymentService {
    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);
    private HttpClient httpClient = HttpClient.getInstance();

    private I18nService i18n = I18nService.getInstance();
    private static final String STEP2 = "STEP2";
    private static final String STEP3 = "STEP3";

    // the status for finalize the transaction can only be COMMIT or ROLLBACK
    public enum STATUS {
        COMMIT, ROLLBACK
    }

    @Override
    public PaymentResponse paymentRequest(PaymentRequest request) {
        try {
            if (request.getRequestContext() == null) {
                throw new InvalidDataException("RequestContext is missing");
            }
            String step = request.getRequestContext().getRequestData().get(Constants.RequestContextKeys.CONTEXT_DATA_STEP);
            if (step == null || step.equals("")) {
                // ask for begin a transaction, and take the PartnerTransactionId
                // return a PaymentResponseFormUpdated() to ask the cabTitre
                return step1(request);
            } else if (STEP2.equals(step)) {
                // ask for the detail of a cabTitre
                // return a PaymentResponseFormUpdated() to show information to the shopper
                return step2(request);
            } else if (STEP3.equals(step)) {
                // finalise the transaction if we are here, the STATUS is COMMIT
                // return a PaymentResponse
                return step3(request, STATUS.COMMIT);
            } else {
                // should never append
                String errorMessage = "Unknown step";
                LOGGER.error(errorMessage);
                return PaymentResponseFailure.PaymentResponseFailureBuilder
                        .aPaymentResponseFailure()
                        .withErrorCode(errorMessage)
                        .withFailureCause(FailureCause.INVALID_DATA)
                        .build();
            }
        } catch (PluginException e) {
            return e.toPaymentResponseFailureBuilder()
                    .build();
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected plugin error", e);
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(PluginException.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();
        }
    }

    /**
     * Step1 of the transaction, ask the NumTransaction to GlobalPOS
     *
     * @param request the PaymentRequest
     * @return PaymentResponseFormUpdated to ask the CabTitre to the chopper
     */
    public PaymentResponse step1(PaymentRequest request) {
        final RequestConfiguration configuration = RequestConfiguration.build(request);
        // should never append
        if (request.getTransactionId() == null || PluginUtils.isEmpty(request.getTransactionId())) {
            throw new InvalidDataException("TransactionId is missing");
        }
        String stringResponse = httpClient.getTransac(configuration, request.getTransactionId());
        GetTransac response = GetTransac.fromXml(stringResponse);

        // 1 if response is OK, else is null
        if (response.getCodeErreur() != null) {
            // everything is ok
            Map<String, String> requestContextMap = new HashMap<>();
            // the next request, we don't want to do the same thing, so we tell us we want to go to the next step
            requestContextMap.put(Constants.RequestContextKeys.CONTEXT_DATA_STEP, STEP2);
            requestContextMap.put(Constants.RequestContextKeys.NUMTRANSAC, response.getNumTransac());

            RequestContext requestContext = RequestContext.RequestContextBuilder.aRequestContext()
                    .withRequestData(requestContextMap)
                    .build();

            return PaymentResponseFormUpdated.PaymentResponseFormUpdatedBuilder.aPaymentResponseFormUpdated()
                    .withPaymentFormConfigurationResponse(formCabTitre(request.getLocale()))
                    .withRequestContext(requestContext)
                    .build();
        } else {
            // something is wrong return a PaymentResponseFailure
            return this.responseFailure(APIResponseError.fromXml(stringResponse));
        }
    }


    /**
     * Step 2 of the transaction, show the information of a check
     *
     * @param request the PaymentRequest
     * @return PaymentResponseFailure if the amount is incorrect, or if the date is expired
     * PaymentResponseFormUpdated else
     */
    public PaymentResponse step2(PaymentRequest request) {
        final RequestConfiguration configuration = RequestConfiguration.build(request);

        if (request.getPaymentFormContext() == null || request.getPaymentFormContext().getPaymentFormParameter() == null
                || request.getPaymentFormContext().getPaymentFormParameter().get(Constants.FormConfigurationKeys.CABTITRE) == null) {
            throw new InvalidDataException("issues with the PaymentFormContext");
        }
        if (request.getRequestContext() == null ||
                request.getRequestContext().getRequestData().get(Constants.RequestContextKeys.NUMTRANSAC) == null) {
            throw new InvalidDataException("issues with the numTransac in the requestContext");
        }

        String numTransac = request.getRequestContext().getRequestData().get(Constants.RequestContextKeys.NUMTRANSAC);
        String stringResponse = httpClient.getTitreDetailTransac(configuration, numTransac,
                request.getPaymentFormContext().getPaymentFormParameter().get(Constants.FormConfigurationKeys.CABTITRE));
        GetTitreDetailTransac response = GetTitreDetailTransac.fromXml(stringResponse);

        // 1 if response is OK, else is null
        if (response.getCodeErreur() != null) {
            if (request.getAmount() == null || request.getAmount().getAmountInSmallestUnit() == null
                    || request.getAmount().getCurrency() == null) {
                throw new InvalidDataException("issues with the requestAmount");
            }
            if (response.getMontant() == null) {
                throw new InvalidDataException("Amount is missing on the check");
            }

            try {
                BigDecimal gpAmount = new BigDecimal(response.getMontant());
                BigDecimal paylineAmount = AmountParse.splitDecimal(request.getAmount());
                PaymentFormConfigurationResponseSpecific recap;

                switch (gpAmount.compareTo(paylineAmount)) {
                    // 0 if gpAmount = paylineAmount
                    // the best scenario, return the form for the summary
                    case 0:
                        recap = formRecapPayment(response, request.getLocale());
                        break;
                    // -1 if gpAmount < paylineAmount
                    // ok, but return the form for the credit card
                    case -1:
                        CustomForm form = CardForm.builder()
                                .withSchemes(new ArrayList<>())
                                .withDescription(i18n.getMessage("customFormDescCardForm.description", request.getLocale()))
                                .withCustomFields(new ArrayList<>())
                                .build();

                        recap = PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder
                                .aPaymentFormConfigurationResponseSpecific()
                                .withPaymentForm(form)
                                .build();
                        break;
                    // 1 if gpAmount > paylineAmount
                    // wrong => return an error
                    case 1:
                    default:
                        return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                                .withErrorCode("The amount of your check is above the amount ouf your transaction")
                                .withFailureCause(FailureCause.INVALID_DATA)
                                .build();
                }

                // everything is ok
                Map<String, String> requestContextMap = new HashMap<>();
                // the next request, we don't want to do the same thing, so we tell us we want to go to the next step
                requestContextMap.put(Constants.RequestContextKeys.CONTEXT_DATA_STEP, STEP3);
                requestContextMap.put(Constants.RequestContextKeys.NUMTRANSAC, numTransac);

                RequestContext requestContext = RequestContext.RequestContextBuilder.aRequestContext()
                        .withRequestData(requestContextMap)
                        .build();

                return PaymentResponseFormUpdated.PaymentResponseFormUpdatedBuilder.aPaymentResponseFormUpdated()
                        .withRequestContext(requestContext)
                        .withPaymentFormConfigurationResponse(recap)
                        .build();

            } catch (NumberFormatException e) {
                throw new NumberFormatException("Amount in the check is not a valid number");
            }

        } else {
            // something is wrong return a PaymentResponseFailure
            return this.responseFailure(APIResponseError.fromXml(stringResponse));
        }
    }


    /**
     * Finalize the payment
     *
     * @param request
     * @param status  COMMIT: ok, end it
     *                ROLLBACK: something went wrong abort it
     * @return PaymentResponseSuccess or Failure, depend on the case
     */
    public PaymentResponse step3(PaymentRequest request, STATUS status) {
        if (request.getRequestContext() == null ||
                request.getRequestContext().getRequestData().get(Constants.RequestContextKeys.NUMTRANSAC) == null) {
            throw new InvalidDataException("issues with the numTransac in the requestContext");
        }

        final RequestConfiguration configuration = RequestConfiguration.build(request);
        String numTransac = request.getRequestContext().getRequestData().get(Constants.RequestContextKeys.NUMTRANSAC);
        String stringResponse = httpClient.setFinTransac(configuration, numTransac, status);
        SetFinTransac response = SetFinTransac.fromXml(stringResponse);

        // 1 if response is OK, else is null
        if (response.getCodeErreur() != null) {
            // the request worked
            if (status == STATUS.COMMIT) {
                // everything is ok, end the transaction
                Map<String, String> requestContextMap = new HashMap<>();
                requestContextMap.put(Constants.RequestContextKeys.NUMTRANSAC, numTransac);

                RequestContext requestContext = RequestContext.RequestContextBuilder.aRequestContext()
                        .withRequestData(requestContextMap)
                        .build();

                return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                        .withPartnerTransactionId(numTransac)
                        .withRequestContext(requestContext)
                        .withTransactionDetails(new EmptyTransactionDetails())
                        .withStatusCode(response.getCodeErreur())
                        .build();
            } else if (status == STATUS.ROLLBACK) {
                // if status = ROLLBACK abort the transaction
                return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                        .withFailureCause(FailureCause.CANCEL)
                        .withErrorCode("Transaction aborted")
                        .build();
            } else {
                // something else than COMMIT or ROLLBACK
                return responseFailure(APIResponseError.fromXml(stringResponse));
            }
        } else {
            // in each case if something went wrong it's the same error
            return responseFailure(APIResponseError.fromXml(stringResponse));
        }
    }


    /**
     * Create the Formulaire for the end of the step1
     * The chopper will be able to enter his check
     *
     * @param locale to know the language
     * @return PaymentFormConfigurationResponseSpecific
     */
    public PaymentFormConfigurationResponseSpecific formCabTitre(Locale locale) {
        List<PaymentFormField> listForm = new ArrayList();

        // Pattern: regexp for exactly 44 numbers
        PaymentFormInputFieldText form = PaymentFormInputFieldText.PaymentFormFieldTextBuilder.aPaymentFormFieldText()
                .withRequiredErrorMessage(i18n.getMessage("formCabTitre.requiredErrorMessage", locale))
                .withValidation(Pattern.compile("^[0-9]{44}$"))
                .withValidationErrorMessage(i18n.getMessage("formCabTitre.validationErrorMessage", locale))
                .withPlaceholder("")
                .withKey(Constants.FormConfigurationKeys.CABTITRE)
                .withLabel(i18n.getMessage("formCabTitre.label", locale))
                .withRequired(true)
                .withInputType(InputType.NUMBER)
                .withFieldIcon(FieldIcon.CARD)
                .build();
        listForm.add(form);

        CustomForm customForm = CustomForm.builder()
                .withCustomFields(listForm)
                .withDescription(i18n.getMessage("customFormDescTitre.description", locale))
                .build();

        return PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder
                .aPaymentFormConfigurationResponseSpecific()
                .withPaymentForm(customForm)
                .build();
    }

    /**
     * Create the form for the summaries of the check between the step2 and 3
     *
     * @param response the response of the getTitreDetailTransac request
     * @return PaymentFormConfigurationResponseSpecific with a list of PaymentFormDisplayFieldText
     */
    public PaymentFormConfigurationResponseSpecific formRecapPayment(GetTitreDetailTransac response, Locale locale) {
        List<PaymentFormField> listForm = new ArrayList();

        PaymentFormDisplayFieldText titre = PaymentFormDisplayFieldText.PaymentFormDisplayFieldTextBuilder
                .aPaymentFormDisplayFieldText()
                .withContent(response.getTitre())
                .build();
        listForm.add(titre);

        PaymentFormDisplayFieldText transmitter = PaymentFormDisplayFieldText.PaymentFormDisplayFieldTextBuilder
                .aPaymentFormDisplayFieldText()
                .withContent(response.getEmetteur())
                .build();
        listForm.add(transmitter);

        PaymentFormDisplayFieldText amount = PaymentFormDisplayFieldText.PaymentFormDisplayFieldTextBuilder
                .aPaymentFormDisplayFieldText()
                .withContent(response.getMontant())
                .build();
        listForm.add(amount);

        PaymentFormDisplayFieldText dateValid = PaymentFormDisplayFieldText.PaymentFormDisplayFieldTextBuilder
                .aPaymentFormDisplayFieldText()
                .withContent(response.getDateValid())
                .build();
        listForm.add(dateValid);

        CustomForm customForm = CustomForm.builder()
                .withCustomFields(listForm)
                .withDescription(i18n.getMessage("formRecap.description", locale))
                .build();

        return PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder
                .aPaymentFormConfigurationResponseSpecific()
                .withPaymentForm(customForm)
                .build();
    }


    /**
     * Create the ResponseFailure
     *
     * @param response the responseError return by the API GlobalPOS
     * @return PaymentResponseFailure
     */
    public PaymentResponseFailure responseFailure(APIResponseError response) {
        String errorCode = "errorCode: " + response.getError() + "\nMessage: " + response.getMessage() + "\nDetails: " + response.getDetail();
        return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                .withErrorCode(errorCode)
                .withFailureCause(PluginUtils.getFailureCause(response.getError()))
                .build();
    }
}
