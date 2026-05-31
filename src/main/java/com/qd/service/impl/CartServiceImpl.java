/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.service.impl;
import java.util.UUID;
import com.qd.enums.ItemStatus;
import com.qd.pojo.CartItems;
import com.qd.pojo.Carts;
import com.qd.pojo.Providers;
import com.qd.pojo.SellableItems;
import com.qd.pojo.ServiceImages;
import com.qd.pojo.Services;
import com.qd.repository.CartRepository;
import com.qd.service.CartService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ADMIN
 */
@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private Environment env;
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getMyCart(String username, Map<String, String> params) {
        Carts cart = cartRepository.findCartByUsername(username);
        if (cart == null) throw new RuntimeException("Bạn phải có tài khoản mới sử dụng được tính năng này. Vui lòng đăng nhập hoặc đăng ký tài khoản.");

        List<CartItems> items = cartRepository.getCartItemsPaged(cart.getId(), params);
        Long totalItems = cartRepository.countCartItems(cart.getId());

        Map<Long, Map<String, Object>> providerGroupMap = new LinkedHashMap<>();
        for (CartItems ci : items) {
            SellableItems si = ci.getItemId();
            Services s = si.getServiceId();
            Providers p = s.getProviderId(); 

            if (!providerGroupMap.containsKey(p.getId())) {
                Map<String, Object> providerBlock = new HashMap<>();
                providerBlock.put("providerId", p.getId());
                providerBlock.put("companyName", p.getCompanyName());
                providerBlock.put("hotline", p.getHotline());
                providerBlock.put("items", new ArrayList<Map<String, Object>>()); 
                
                providerGroupMap.put(p.getId(), providerBlock);
            }

           Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("cartItemId", ci.getId());
            itemMap.put("quantity", ci.getQuantity());
            itemMap.put("createdAt", ci.getCreatedAt().getTime());

            Map<String, Object> siInfo = new HashMap<>();
            siInfo.put("sellableItemId", si.getId());
            siInfo.put("currentPrice", si.getPrice());
            siInfo.put("availableSlots", si.getAvailableSlots());
            siInfo.put("itemStatus", si.getItemStatus().toString());
            siInfo.put("serviceName", s.getName());

            if (si.getHotelRoomItemId() != null) {
                siInfo.put("subItemType", si.getHotelRoomItemId().getRoomType());
            } else if (si.getTourItemConcId() != null) {
                siInfo.put("subItemType", "Vé Tour Lịch Trình");
            } else if (si.getTransportTicketItemId() != null) {
                siInfo.put("subItemType", si.getTransportTicketItemId().getSeatClass());
            }

            String thumbnail = s.getServiceImagesSet().stream()
                    .filter(img -> img.getIsThumbnail() != null && img.getIsThumbnail())
                    .map(ServiceImages::getImageUrl).findFirst().orElse("");
            siInfo.put("thumbnailUrl", thumbnail);

            itemMap.put("sellableItemInfo", siInfo);
            List<Map<String, Object>> providerItemsList = (List<Map<String, Object>>) providerGroupMap.get(p.getId()).get("items");
            providerItemsList.add(itemMap);
        }

            List<Map<String, Object>> contentGrouped = new ArrayList<>(providerGroupMap.values());

        int pageSize = Integer.parseInt(this.env.getProperty("cart.page_size", "15"));
        int currentPage = (params != null && params.containsKey("page")) ? Integer.parseInt(params.get("page")) : 1;

        Map<String, Object> result = new HashMap<>();
        result.put("cartId", cart.getId());
        result.put("totalItems", totalItems);
        result.put("content", contentGrouped); 
        result.put("page", currentPage);
        result.put("size", pageSize);
        return result;
    }
    
    @Override
    @Transactional
    public void addItemToCart(String username, Long sellableItemId, int quantity) {
        if (quantity <= 0) throw new RuntimeException("Số lượng bỏ giỏ phải lớn hơn 0");
        
        Carts cart = cartRepository.findCartByUsername(username);
        SellableItems item = cartRepository.findSellableItemForUpdate(sellableItemId);
        if (item == null) throw new RuntimeException("Sản phẩm mở bán không tồn tại!");
        
        if (item.getItemStatus() == null || !ItemStatus.AVAILABLE.equals(item.getItemStatus())) {
            throw new RuntimeException("Thao tác bị chặn: Gói dịch vụ này hiện tại đang TẠM DỪNG MỞ BÁN (SUSPENDED) hoặc đã HẾT HÀNG (OUT_OF_STOCK)!");
        }
        if (quantity > item.getAvailableSlots()) {
            throw new RuntimeException("Số lượng đặt mua vượt quá chỗ trống mở bán hiện tại (" + item.getAvailableSlots() + ")!");
        }

        CartItems existingItem = cartRepository.findCartItemByCartAndSellableItem(cart.getId(), sellableItemId);
        if (existingItem != null) {
            int newQty = existingItem.getQuantity() + quantity;
            if (newQty > item.getAvailableSlots()) {
                throw new RuntimeException("Tổng số lượng trong giỏ vượt quá số lượng dịch vụ!");
            }
            existingItem.setQuantity(newQty);
            cartRepository.updateCartItem(existingItem);
        } else {
            CartItems newItem = new CartItems();
            newItem.setCartId(cart);
            newItem.setItemId(item);
            newItem.setQuantity(quantity);
            newItem.setCreatedAt(new Date());
            cartRepository.saveCartItem(newItem);
        }
    }

    @Override
    @Transactional
    public void updateCartItemQuantity(Long cartItemId, int quantity) {
        if (quantity <= 0) throw new RuntimeException("Số lượng cập nhật tối thiểu là 1!");
        CartItems ci = cartRepository.findCartItemById(cartItemId);
        if (ci == null) throw new RuntimeException("Sản phẩm không tồn tại trong giỏ!");

        SellableItems item = cartRepository.findSellableItemForUpdate(ci.getItemId().getId());
        if (item == null) {
            throw new RuntimeException("Sản phẩm mở bán gốc đã bị xóa khỏi hệ thống!");
        }
        if (item.getItemStatus() == null || !ItemStatus.AVAILABLE.equals(item.getItemStatus())) {
            throw new RuntimeException("Thao tác bị chặn: Gói dịch vụ này hiện tại đang TẠM DỪNG MỞ BÁN (SUSPENDED) hoặc đã HẾT HÀNG (OUT_OF_STOCK)!");
        }

        if (quantity > item.getAvailableSlots()) {
            throw new RuntimeException("Không thể tăng số lượng mặt hàng vì đã vượt số lượng khả dụng (" + item.getAvailableSlots() + ")!");
        }
        ci.setQuantity(quantity);
        cartRepository.updateCartItem(ci);
    }

    @Override
    @Transactional
    public void removeCartItem(Long cartItemId) {
        CartItems item = cartRepository.findCartItemById(cartItemId);
        if (item != null) {
        Carts cartParent = item.getCartId();
        if (cartParent != null && cartParent.getCartItemsSet() != null) {
            
                Iterator<CartItems> iterator = cartParent.getCartItemsSet().iterator();
                while (iterator.hasNext()) {
                    CartItems child = iterator.next();
                    if (child.getId() != null && child.getId().equals(cartItemId)) {
                        iterator.remove(); 
                        break;
                    }
                }
            }
        cartRepository.deleteCartItem(item);
        }

    }

    
    // @Override
    // @Transactional
    // public String processCheckout(String username, Map<String, Object> body) {
    //     boolean isFromCart = body.containsKey("isFromCart") && Boolean.parseBoolean(body.get("isFromCart").toString());
    //     String paymentMethod = body.getOrDefault("paymentMethodCode", "CASH").toString();
        
    //     List<CartItems> itemsToBuy = new ArrayList<>();
    //     Carts cart = cartRepository.findCartByUsername(username);

    //     if (isFromCart) {
    //         if (cart == null) throw new RuntimeException("Không tìm thấy dữ liệu giỏ hàng!");
    //         Map<String, String> unpaged = new HashMap<>();
    //         unpaged.put("page", "1");
    //         itemsToBuy = cartRepository.getCartItemsPaged(cart.getId(), unpaged);
    //         if (itemsToBuy.isEmpty()) throw new RuntimeException("Giỏ hàng của bạn đang trống rỗng, không có gì để tính tiền!");
    //     } else {
    //         Long sellableItemId = Long.parseLong(body.get("sellableItemId").toString());
    //         int quantity = Integer.parseInt(body.get("quantity").toString());

    //         SellableItems si = cartRepository.findSellableItemForUpdate(sellableItemId);
    //         CartItems mockItem = new CartItems();
    //         mockItem.setItemId(si);
    //         mockItem.setQuantity(quantity);
    //         itemsToBuy.add(mockItem);
    //     }

    //     Long targetProviderId = itemsToBuy.get(0).getItemId().getServiceId().getProviderId().getId();
    //     for (CartItems ci : itemsToBuy) {
    //         Long currentProviderId = ci.getItemId().getServiceId().getProviderId().getId();
    //         if (!currentProviderId.equals(targetProviderId)) {
    //             throw new RuntimeException("Lỗi Checkout nghiêm trọng: Hệ thống TravelVista chỉ cho phép chốt hóa đơn cho các sản phẩm của CÙNG MỘT Nhà cung cấp trên một đơn hàng! Vui lòng dọn dẹp hoặc tách giỏ hàng thành các đơn riêng lẻ!");
    //         }
    //     }

    //     for (CartItems ci : itemsToBuy) {
    //         SellableItems realStockItem = cartRepository.findSellableItemForUpdate(ci.getItemId().getId());
    //         if (realStockItem.getAvailableSlots() < ci.getQuantity() || !ItemStatus.AVAILABLE.equals(realStockItem.getItemStatus())) {
    //             throw new RuntimeException("Thao tác bị hủy: Sản phẩm [" + realStockItem.getServiceId().getName() + "] vừa mới hết chỗ trống đột xuất! Vui lòng tải lại trang.");
    //         }
            
    //         realStockItem.setAvailableSlots(realStockItem.getAvailableSlots() - ci.getQuantity());
    //         if (realStockItem.getAvailableSlots() == 0) {
    //             realStockItem.setItemStatus(ItemStatus.OUT_OF_STOCK); // Treo bảng hết hàng
    //         }
    //         cartRepository.updateSellableItem(realStockItem);
    //     }

    //     if (isFromCart) {
    //         for (CartItems ci : itemsToBuy) {
    //             cartRepository.deleteCartItem(ci);
    //         }
    //     }

    //     if ("PAYPAL".equalsIgnoreCase(paymentMethod)) {
    //         return "https://www.paypal.com/checkoutnow?token=EC-TRAVELVISTA" + UUID.randomUUID().toString().substring(0,8);
    //     } else if ("MOMO".equalsIgnoreCase(paymentMethod)) {
    //         return "https://momo.vn/payment/qr-gateway/id=" + UUID.randomUUID().toString().substring(0,8);
    //     }
        
    //     return ""; 
    // }
}