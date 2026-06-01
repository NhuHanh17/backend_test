package com.qd.dto.customer;

import java.util.List;

public class CheckoutRequest {
    private List<CartItemRequest> items;
    private Long paymentMethodId;
    private String transactionReference;

    public CheckoutRequest() {}

    /**
     * @return the items
     */
    public List<CartItemRequest> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(List<CartItemRequest> items) {
        this.items = items;
    }

    /**
     * @return the paymentMethodId
     */
    public Long getPaymentMethodId() {
        return paymentMethodId;
    }

    /**
     * @param paymentMethodId the paymentMethodId to set
     */
    public void setPaymentMethodId(Long paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    /**
     * @return the transactionReference
     */
    public String getTransactionReference() {
        return transactionReference;
    }

    /**
     * @param transactionReference the transactionReference to set
     */
    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    
}
