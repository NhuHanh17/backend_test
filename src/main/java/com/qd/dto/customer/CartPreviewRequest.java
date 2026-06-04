package com.qd.dto.customer;

import java.util.List;

public class CartPreviewRequest {
    private List<Long> cartItemIds;

    public List<Long> getCartItemIds() {
         return cartItemIds; 
        }
        
    public void setCartItemIds(List<Long> cartItemIds) {
         this.cartItemIds = cartItemIds; 
        }
}
