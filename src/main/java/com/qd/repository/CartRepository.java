/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.qd.repository;

import java.util.List;
import java.util.Map;

import com.qd.pojo.CartItems;
import com.qd.pojo.Carts;
import com.qd.pojo.SellableItems;

/**
 *
 * @author ADMIN
 */
public interface CartRepository {
    void saveCart(Carts cart);

    Carts findCartByUsername(String username);
    List<CartItems> getCartItemsPaged(Long cartId, Map<String, String> params);
    Long countCartItems(Long cartId);
    CartItems findCartItemByCartAndSellableItem(Long cartId, Long sellableItemId);
    void saveCartItem(CartItems cartItem);
    void updateCartItem(CartItems cartItem);
    void deleteCartItem(CartItems cartItem);
    CartItems findCartItemById(Long id);

    SellableItems findSellableItemForUpdate(Long id);
    void updateSellableItem(SellableItems item);
    void createOrder(com.qd.pojo.Orders order);
    com.qd.pojo.Categories getCategoryById(Long id);
    List<CartItems> findCartItemsForPreview(List<Long> cartItemIds);
}
