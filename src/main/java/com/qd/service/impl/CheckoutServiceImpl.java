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
import com.qd.pojo.OrderDetails;
import com.qd.pojo.Orders;
import com.qd.pojo.PaymentMethods;
import com.qd.pojo.Providers;
import com.qd.pojo.SellableItems;
import com.qd.pojo.Services;
import com.qd.pojo.Users;
import com.qd.repository.impl.CheckoutRepositoryImpl;
import com.qd.service.CheckoutService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ADMIN
 */
@Service
public class CheckoutServiceImpl implements CheckoutService{

    @Autowired
    private CheckoutRepositoryImpl checkoutRepository;

    @Transactional(readOnly = true)
    public BigDecimal calculateSummaryAndValidate(List<CartItemRequest> itemRequests) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        Long targetProviderId = null;

        for (CartItemRequest req : itemRequests) {
            SellableItems item = checkoutRepository.findSellableItemById(req.getItemId());
            if (item == null) {
                throw new RuntimeException("Sản phẩm có ID " + req.getItemId() + " không tồn tại!");
            }

            if (item.getItemStatus() != ItemStatus.AVAILABLE) {
                throw new RuntimeException("Sản phẩm '" 
                        + item.getServiceId().getName() + "' hiện đã ngưng bán hoặc cháy vé! (Trạng thái: " + item.getItemStatus() + ")!");
            }

            Services service = item.getServiceId();
            Providers currentProvider = service.getProviderId();
            Long currentProviderId = currentProvider.getId();

            if (targetProviderId == null) {
                targetProviderId = currentProviderId; 
            } else if (!targetProviderId.equals(currentProviderId)) {
                throw new RuntimeException("Hệ thống phát hiện có sản phẩm lệch Provider trong giỏ! Vui lòng tách đơn!");
            }

            if (item.getAvailableSlots() < req.getQuantity()) {
                throw new RuntimeException("Dịch vụ '" + service.getName() + "' chỉ còn" + item.getAvailableSlots() + " chỗ trống!");
            }

            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(req.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        return totalAmount;
    }


    @Transactional
    public Orders placeOrder(Users buyer, CheckoutRequest req) {
        BigDecimal finalTotalAmount = calculateSummaryAndValidate(req.getItems());

        // 🛑 Bước B: Kiểm chứng hình thức thanh toán
        PaymentMethods paymentMethod = checkoutRepository.findPaymentMethodById(req.getPaymentMethodId());
        if (paymentMethod == null) {
            throw new RuntimeException("Phương thức thanh toán không hợp lệ!");
        }
        
        SellableItems firstItem = checkoutRepository.findSellableItemById(req.getItems().get(0).getItemId());
        Providers orderProvider = firstItem.getServiceId().getProviderId();

        Orders order = new Orders();
        order.setUserId(buyer);
        order.setProviderId(orderProvider);
        order.setTotalAmount(finalTotalAmount);
        order.setPaymentStatus(PaymentStatus.PENDING); 
        order.setTransactionReference(req.getTransactionReference());
        order.setCreatedAt(new Date());

        checkoutRepository.saveOrder(order); 

        for (CartItemRequest itemReq : req.getItems()) {
            SellableItems item = checkoutRepository.findSellableItemById(itemReq.getItemId());
            Services service = item.getServiceId();

            int newSlots = item.getAvailableSlots() - itemReq.getQuantity();
            item.setAvailableSlots(newSlots);
            
            if (newSlots == 0) {
                item.setItemStatus(ItemStatus.OUT_OF_STOCK);
            }
            checkoutRepository.updateSellableItem(item); 

            OrderDetails detail = new OrderDetails();
            detail.setOrderId(order);
            detail.setItemId(item);
            detail.setQuantity(itemReq.getQuantity());
            detail.setPrice(item.getPrice()); 
            detail.setBookingStatus(BookingStatus.BOOKED);
            detail.setServiceNameSnapshot(service.getName());
            detail.setProviderNameSnapshot(orderProvider.getCompanyName());
            detail.setCreatedAt(new Date());

            checkoutRepository.saveOrderDetail(detail);
        }

        return order;
    }
}

