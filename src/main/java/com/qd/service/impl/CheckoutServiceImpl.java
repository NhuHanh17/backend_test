/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.service.impl;

import com.qd.dto.customer.CartItemRequest;
import com.qd.dto.customer.CheckoutRequest;
import com.qd.enums.BookingStatus;
import com.qd.enums.ItemStatus;
import com.qd.enums.PaymentStatus;
import com.qd.pattern.payment.PaymentStrategy;
import com.qd.pattern.payment.PaymentStrategyFactory;
import com.qd.pojo.CartItems;
import com.qd.pojo.OrderDetails;
import com.qd.pojo.Orders;
import com.qd.pojo.PaymentMethods;
import com.qd.pojo.Providers;
import com.qd.pojo.SellableItems;
import com.qd.pojo.Users;
import com.qd.repository.CheckoutRepository;
import com.qd.service.CheckoutService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ADMIN
 */
@Service
public class CheckoutServiceImpl implements CheckoutService {

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Transactional
    @Override
    public Map<String, Object> executeCheckout(Users buyer, CheckoutRequest req) {
        List<CartItemRequest> normalizedItems = new ArrayList<>();
        List<CartItems> cartItemsToDelete = new ArrayList<>();
         if (req.getIsBuyNow()) {
            if (req.getItemId() == null || req.getQuantity() == null || req.getQuantity() <= 0) 
                throw new RuntimeException("Thiếu dữ liệu sản phẩm hoặc số lượng!");
            
            CartItemRequest itemReq = new CartItemRequest();
            itemReq.setItemId(req.getItemId());
            itemReq.setQuantity(req.getQuantity());
            normalizedItems.add(itemReq);
        } else {
            if (req.getCartItemIds() == null || req.getCartItemIds().isEmpty()) {
                throw new RuntimeException("Danh sách chọn thanh toán từ giỏ hàng trống!");
            }
            for (Long cartItemId : req.getCartItemIds()) {
                CartItems cartItem = checkoutRepository.findCartItemById(cartItemId);
                if (cartItem == null) {
                    throw new RuntimeException("item trong giỏ hàng không tồn tại id " + cartItemId);
                }
                CartItemRequest itemReq = new CartItemRequest();
                itemReq.setItemId(cartItem.getItemId().getId());
                itemReq.setQuantity(cartItem.getQuantity());
                normalizedItems.add(itemReq);
                cartItemsToDelete.add(cartItem);
            }
        }
        BigDecimal totalAmount = BigDecimal.ZERO;
        Long targetProviderId = null;
        Providers orderProvider = null;

        for (CartItemRequest itemReq : normalizedItems) {
            SellableItems item = checkoutRepository.findSellableItemById(itemReq.getItemId());
            if (item == null)
                throw new RuntimeException("item không tồn tại! " + itemReq.getItemId());
            if (item.getItemStatus() != ItemStatus.AVAILABLE) 
                throw new RuntimeException("Sản phẩm không khả dụng để đặt'" + item.getServiceId().getName());
            
            Long currentProviderId = item.getServiceId().getProviderId().getId();
            if (targetProviderId == null) {
                targetProviderId = currentProviderId;
                orderProvider = item.getServiceId().getProviderId();
            } else if (!targetProviderId.equals(currentProviderId)) 
                throw new RuntimeException("Các sản phẩm không cùng một nhà cung cấp! ");
            
            if (item.getAvailableSlots() < itemReq.getQuantity()) 
                throw new RuntimeException("Dịch vụ không đủ số lượng " + item.getServiceId().getName());

            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }
   
        PaymentMethods paymentMethod = null;
        boolean isFree = totalAmount.compareTo(BigDecimal.ZERO) == 0;

        if (isFree) {
            paymentMethod = checkoutRepository.findPaymentMethodById(6L);
        } else {
            if (req.getPaymentMethodId() == null) throw new RuntimeException("Đơn hàng có phí bắt buộc chọn Phương thức thanh toán!");
            paymentMethod = checkoutRepository.findPaymentMethodById(req.getPaymentMethodId());
            if (paymentMethod == null) throw new RuntimeException("Hãy chọn phương thức thanh toán hợp lệ!");
        }
        Orders order = new Orders();
        order.setUserId(buyer);
        order.setProviderId(orderProvider);
        order.setTotalAmount(totalAmount);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setPaymentMethodId(paymentMethod); 
        order.setCreatedAt(new Date());
        
        checkoutRepository.saveOrder(order); 

        for (CartItemRequest itemReq : normalizedItems) {
            SellableItems item = checkoutRepository.findSellableItemById(itemReq.getItemId());
            int newSlots = item.getAvailableSlots() - itemReq.getQuantity();
            item.setAvailableSlots(newSlots);
            if (newSlots == 0)  item.setItemStatus(ItemStatus.OUT_OF_STOCK);
            checkoutRepository.updateSellableItem(item);
            OrderDetails detail = new OrderDetails();
            detail.setOrderId(order);
            detail.setItemId(item);
            detail.setQuantity(itemReq.getQuantity());
            detail.setPrice(item.getPrice());

            detail.setBookingStatus(BookingStatus.BOOKED);
            detail.setServiceNameSnapshot(item.getServiceId().getName());
            detail.setProviderNameSnapshot(orderProvider.getCompanyName());
            detail.setCreatedAt(new Date());

            checkoutRepository.saveOrderDetail(detail);
        }

        if (!cartItemsToDelete.isEmpty()) {
            for (CartItems cartItem : cartItemsToDelete) {
                checkoutRepository.deleteCartItem(cartItem);
            }
        }
        PaymentStrategy strategy=PaymentStrategyFactory.getStrategy(isFree, paymentMethod);
        Map<String, Object> strategyResult = strategy.processPayment(order);

        Map<String, Object> finalResult = new HashMap<>(strategyResult);
        finalResult.put("orderId", order.getId());
        finalResult.put("totalAmount", order.getTotalAmount());
        finalResult.put("paymentStatus", order.getPaymentStatus().toString());
        return finalResult;
    }

    @Transactional
    @Override
    public void fulfillOrderAfterPayment(Long orderId, String transactionRef) {
        Orders order = checkoutRepository.findOrderById(orderId);
        if (order == null) {
            throw new RuntimeException(
                    "Không tìm thấy đơn hàng ID để hoàn tất thanh toán" + orderId );
        }
        order.setPaymentStatus(PaymentStatus.PAY);
        order.setTransactionReference(transactionRef);
        checkoutRepository.updateOrder(order);
    }
}
