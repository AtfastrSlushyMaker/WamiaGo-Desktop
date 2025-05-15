package entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDetailsResponse {
    private Payment payment;

    public static class Payment {
        // Champs principaux
        private String id;
        private String status;
        private int amountDue;
        private int reachedAmount;
        private int amount;
        private String token;
        private int convertedAmount;
        private double exchangeRate;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        private Date expirationDate;

        private String shortId;
        private String link;
        private String webhook;
        private String successUrl;
        private String failUrl;
        private String orderId;
        private String type;
        private String details;
        private List<String> acceptedPaymentMethods;
        private Object receiverWallet;

        // Nouveaux champs ajoutés
        private int failedTransactions;
        private int successfulTransactions;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        private Date createdAt;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        private Date updatedAt;

        // Transactions avec structure détaillée
        private List<Transaction> transactions;

        // --- Classes imbriquées ---

        public static class Transaction {
            private String type;
            private String method;
            private String status;
            private String token;
            private int amount;
            private String ext_payment_ref;
            private String from;
            private int amountAfterFee;
            private ExtSenderInfo extSenderInfo;
            private double feeRate;
            private int totalFee;
            private String id;

            // Getters/Setters
            public String getType() { return type; }
            public void setType(String type) { this.type = type; }

            public String getMethod() { return method; }
            public void setMethod(String method) { this.method = method; }

            public String getStatus() { return status; }
            public void setStatus(String status) { this.status = status; }

            public String getToken() { return token; }
            public void setToken(String token) { this.token = token; }

            public int getAmount() { return amount; }
            public void setAmount(int amount) { this.amount = amount; }

            public String getExt_payment_ref() { return ext_payment_ref; }
            public void setExt_payment_ref(String ext_payment_ref) { this.ext_payment_ref = ext_payment_ref; }

            public String getFrom() { return from; }
            public void setFrom(String from) { this.from = from; }

            public int getAmountAfterFee() { return amountAfterFee; }
            public void setAmountAfterFee(int amountAfterFee) { this.amountAfterFee = amountAfterFee; }

            public ExtSenderInfo getExtSenderInfo() { return extSenderInfo; }
            public void setExtSenderInfo(ExtSenderInfo extSenderInfo) { this.extSenderInfo = extSenderInfo; }

            public double getFeeRate() { return feeRate; }
            public void setFeeRate(double feeRate) { this.feeRate = feeRate; }

            public int getTotalFee() { return totalFee; }
            public void setTotalFee(int totalFee) { this.totalFee = totalFee; }

            public String getId() { return id; }
            public void setId(String id) { this.id = id; }
        }

        public static class ExtSenderInfo {
            private String pan;
            private String approvalCode;
            private String expiration;
            private String paymentSystem;
            private String name;
            private String regionType;
            private String email;
            private BankInfo bankInfo;

            // Getters/Setters
            public String getPan() { return pan; }
            public void setPan(String pan) { this.pan = pan; }

            public String getApprovalCode() { return approvalCode; }
            public void setApprovalCode(String approvalCode) { this.approvalCode = approvalCode; }

            public String getExpiration() { return expiration; }
            public void setExpiration(String expiration) { this.expiration = expiration; }

            public String getPaymentSystem() { return paymentSystem; }
            public void setPaymentSystem(String paymentSystem) { this.paymentSystem = paymentSystem; }

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }

            public String getRegionType() { return regionType; }
            public void setRegionType(String regionType) { this.regionType = regionType; }

            public String getEmail() { return email; }
            public void setEmail(String email) { this.email = email; }

            public BankInfo getBankInfo() { return bankInfo; }
            public void setBankInfo(BankInfo bankInfo) { this.bankInfo = bankInfo; }
        }

        public static class BankInfo {
            private String bankName;
            private String bankCountryCode;
            private String bankCountryName;

            // Getters/Setters
            public String getBankName() { return bankName; }
            public void setBankName(String bankName) { this.bankName = bankName; }

            public String getBankCountryCode() { return bankCountryCode; }
            public void setBankCountryCode(String bankCountryCode) { this.bankCountryCode = bankCountryCode; }

            public String getBankCountryName() { return bankCountryName; }
            public void setBankCountryName(String bankCountryName) { this.bankCountryName = bankCountryName; }
        }

        // --- Getters/Setters pour la classe Payment ---
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public int getAmountDue() { return amountDue; }
        public void setAmountDue(int amountDue) { this.amountDue = amountDue; }

        public int getReachedAmount() { return reachedAmount; }
        public void setReachedAmount(int reachedAmount) { this.reachedAmount = reachedAmount; }

        public int getAmount() { return amount; }
        public void setAmount(int amount) { this.amount = amount; }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public int getConvertedAmount() { return convertedAmount; }
        public void setConvertedAmount(int convertedAmount) { this.convertedAmount = convertedAmount; }

        public double getExchangeRate() { return exchangeRate; }
        public void setExchangeRate(double exchangeRate) { this.exchangeRate = exchangeRate; }

        public Date getExpirationDate() { return expirationDate; }
        public void setExpirationDate(Date expirationDate) { this.expirationDate = expirationDate; }

        public String getShortId() { return shortId; }
        public void setShortId(String shortId) { this.shortId = shortId; }

        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }

        public String getWebhook() { return webhook; }
        public void setWebhook(String webhook) { this.webhook = webhook; }

        public String getSuccessUrl() { return successUrl; }
        public void setSuccessUrl(String successUrl) { this.successUrl = successUrl; }

        public String getFailUrl() { return failUrl; }
        public void setFailUrl(String failUrl) { this.failUrl = failUrl; }

        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getDetails() { return details; }
        public void setDetails(String details) { this.details = details; }

        public List<String> getAcceptedPaymentMethods() { return acceptedPaymentMethods; }
        public void setAcceptedPaymentMethods(List<String> acceptedPaymentMethods) {
            this.acceptedPaymentMethods = acceptedPaymentMethods;
        }

        public Object getReceiverWallet() { return receiverWallet; }
        public void setReceiverWallet(Object receiverWallet) { this.receiverWallet = receiverWallet; }

        public List<Transaction> getTransactions() { return transactions; }
        public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }

        public int getFailedTransactions() { return failedTransactions; }
        public void setFailedTransactions(int failedTransactions) {
            this.failedTransactions = failedTransactions;
        }

        public int getSuccessfulTransactions() { return successfulTransactions; }
        public void setSuccessfulTransactions(int successfulTransactions) {
            this.successfulTransactions = successfulTransactions;
        }

        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

        public Date getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    }

    // Getters/Setters pour la classe racine
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
}