package com.qd.dto.customer;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CheckoutRequest {
    @JsonProperty("buyNow")
    private boolean isBuyNow; 
    
    private Long itemId;
    private Integer quantity;
    private List<Long> cartItemIds;
    private Long paymentMethodId;

    public CheckoutRequest() {}

    /**
     * @return the isBuyNow
     */
    public boolean getIsBuyNow() {
        return isBuyNow;
    }

    
    /**
     * @param isBuyNow the isBuyNow to set
     */
    public void setIsBuyNow(boolean isBuyNow) {
        this.isBuyNow = isBuyNow;
    }

    /**
     * @return the itemId
     */
    public Long getItemId() {
        return itemId;
    }

    /**
     * @param itemId the itemId to set
     */
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    /**
     * @return the quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the cartItemIds
     */
    public List<Long> getCartItemIds() {
        return cartItemIds;
    }

    /**
     * @param cartItemIds the cartItemIds to set
     */
    public void setCartItemIds(List<Long> cartItemIds) {
        this.cartItemIds = cartItemIds;
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

    
    
}
