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
import java.util.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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

    @Override
    @Transactional(readOnly = true) 
    public Map<String, Object> previewCartItems(List<Long> cartItemIds) {
        Map<String, Object> response = new HashMap<>();
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            response.put("success", false);
            response.put("message", "Vui lòng chọn ít nhất 1 sản phẩm từ giỏ hàng!");
            return response;
        }
        List<CartItems> list = cartRepository.findCartItemsForPreview(cartItemIds);
        if (list.isEmpty()) {
            response.put("success", false);
            response.put("message", "Không tìm thấy dữ liệu giỏ hàng hợp lệ!");
            return response;
        }

        List<Map<String, Object>> validatedItems = new ArrayList<>();
        Set<Long> providerIds = new HashSet<>();
        boolean isAllAvailable = true;
        BigDecimal totalCartPrice = BigDecimal.ZERO;
        for (CartItems cartItem : list) {
            Map<String, Object> itemMap = new HashMap<>();
            SellableItems sellable = cartItem.getItemId();
            Services service = sellable.getServiceId();
            Providers provider = service.getProviderId();

            int requestedQty = cartItem.getQuantity();
            int availableSlots = sellable.getAvailableSlots();

            boolean isAvailable = (requestedQty <= availableSlots) && (sellable.getItemStatus() == ItemStatus.AVAILABLE);
            if (!isAvailable) {
                isAllAvailable = false;
            }

            providerIds.add(provider.getId());
            BigDecimal itemPrice = sellable.getPrice() != null ? sellable.getPrice() : BigDecimal.ZERO;
            BigDecimal itemTotalPrice = itemPrice.multiply(BigDecimal.valueOf(requestedQty));
            totalCartPrice = totalCartPrice.add(itemTotalPrice);

            String thumbnailUrl = "";
            if (service.getServiceImagesSet() != null) {
                thumbnailUrl = service.getServiceImagesSet().stream()
                        .filter(img -> img.getIsThumbnail() != null && img.getIsThumbnail())
                        .map(ServiceImages::getImageUrl)
                        .findFirst()
                        .orElse("");
            
                if (thumbnailUrl.isEmpty() && !service.getServiceImagesSet().isEmpty()) {
                    thumbnailUrl = service.getServiceImagesSet().iterator().next().getImageUrl();
                }
            }

            itemMap.put("cartItemId", cartItem.getId());
            itemMap.put("sellableItemId", sellable.getId());
            itemMap.put("serviceName", service.getName());
            itemMap.put("price", itemPrice);
            itemMap.put("requestedQuantity", requestedQty);
            itemMap.put("availableSlots", availableSlots);
            itemMap.put("isAvailable", isAvailable);
            itemMap.put("thumbnailUrl", thumbnailUrl);
            itemMap.put("itemTotalPrice", itemTotalPrice);
            itemMap.put("providerId", provider.getId());
            itemMap.put("providerName", provider.getCompanyName());

            validatedItems.add(itemMap);
        }

        boolean isSameProvider = (providerIds.size() == 1);

        response.put("success", true);
        response.put("isAllAvailable", isAllAvailable);   
        response.put("isSameProvider", isSameProvider);   
        response.put("totalCartPrice", totalCartPrice);
        response.put("totalUniqueProviders", providerIds.size());
        response.put("items", validatedItems);

        if (!isAllAvailable) {
            response.put("warningMessage", "Có dịch vụ đã hết chỗ hoặc không đủ slot đáp ứng!");
        } else if (!isSameProvider) {
            response.put("warningMessage", "Đơn hàng chọn dịch vụ từ nhiều Nhà cung cấp khác nhau!");
        } else {
            response.put("message", " Đơn hàng sẵn sàng thanh toán!");
        }
        return response;
    }
   
}