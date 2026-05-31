/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.qd.service;

import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface CartService {
    Map<String, Object> getMyCart(String username, Map<String, String> params);
    void addItemToCart(String username, Long sellableItemId, int quantity);
    void updateCartItemQuantity(Long cartItemId, int quantity);
    void removeCartItem(Long cartItemId);
    // String processCheckout(String username, Map<String, Object> body);
}
