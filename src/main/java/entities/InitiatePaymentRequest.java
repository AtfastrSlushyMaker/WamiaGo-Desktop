package entities;

import com.fasterxml.jackson.annotation.JsonProperty;

// InitiatePaymentRequest.java
public class InitiatePaymentRequest {
    @JsonProperty("receiverWalletId")
    private String receiverWalletId;

    @JsonProperty("amount")
    private Number amount;

    // Add other optional fields with setters

    public InitiatePaymentRequest(String receiverWalletId, Number amount) {
        this.receiverWalletId = receiverWalletId;
        this.amount = amount;
    }

    public String getReceiverWalletId() { return receiverWalletId; }
    public void setReceiverWalletId(String receiverWalletId) { this.receiverWalletId = receiverWalletId; }



    public Number getAmount() { return amount; }
    public void setAmount(Number amount) { this.amount = amount; }


}






