package entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InitiatePaymentResponse {
    private final String payUrl;
    private final String paymentRef;

    @JsonCreator
    public InitiatePaymentResponse(
            @JsonProperty("payUrl") String payUrl,
            @JsonProperty("paymentRef") String paymentRef) {
        this.payUrl = payUrl;
        this.paymentRef = paymentRef;
    }

    // Getters
    public String getPayUrl() { return payUrl; }
    public String getPaymentRef() { return paymentRef; }
}