/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.qd.dto.AdminActionRequest;
import com.qd.dto.AdminProviderResponse;
import com.qd.dto.AuthResponse;
import com.qd.dto.ChangePasswordRequest;
import com.qd.dto.provider.ProviderHotelDetailResponse;
import com.qd.dto.provider.ProviderTourDetailResponse;
import com.qd.dto.provider.ProviderTransportDetailResponse;
import com.qd.dto.RegisterRequest;
import com.qd.dto.UserProfile;
import com.qd.dto.provider.BaseComprehensiveRequest;
import com.qd.dto.provider.HotelComprehensiveRequest;
import com.qd.dto.provider.TourComprehensiveRequest;
import com.qd.dto.provider.TransportComprehensiveRequest;
import com.qd.enums.ItemStatus;
import com.qd.enums.ServiceStatus;
import com.qd.enums.ServiceType;
import com.qd.pojo.Categories;
import com.qd.pojo.HotelDetails;
import com.qd.pojo.HotelRoomItems;
import com.qd.pojo.Providers;
import com.qd.pojo.Roles;
import com.qd.pojo.SellableItems;
import com.qd.pojo.Services;
import com.qd.pojo.TourDetails;
import com.qd.pojo.TourItemConcs;
import com.qd.pojo.TransportDetails;
import com.qd.pojo.TransportTicketItems;
import com.qd.pojo.Users;
import com.qd.repository.ProviderRepository;
import com.qd.repository.UserRepository;
import com.qd.service.UserService;
import com.qd.utils.DataValidator;
import com.qd.utils.JwtProvider;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author User
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private Environment env;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest req) {

        if (req.getUsername() == null || req.getUsername().trim().isEmpty()
                || req.getPassword() == null || req.getPassword().trim().isEmpty()
                || req.getFullName() == null || req.getFullName().trim().isEmpty()
                || req.getEmail() == null || req.getEmail().trim().isEmpty()
                || req.getPhone() == null || req.getPhone().trim().isEmpty()) {
            return AuthResponse.builder().success(false).message("Vui lòng điền đầy đủ các thông tin!").build();
        }

        if (userRepository.isExistByUsername(req.getUsername())) {
            return AuthResponse.builder().success(false).message("Tên tài khoản này đã tồn tại!").build();
        }
        if (userRepository.isExistByEmail(req.getEmail())) {
            return AuthResponse.builder().success(false).message("Email này đã được đăng ký rồi!").build();
        }
        if (userRepository.isExistByPhone(req.getPhone())) {
            return AuthResponse.builder().success(false).message("Số điện thoại này đã được đăng ký rồi!").build();
        }
        if (!DataValidator.isValidEmail(req.getEmail())) {
            return AuthResponse.builder().success(false).message("Định dạng Email không hợp lệ! (Ví dụ: abc@gmail.com)")
                    .build();
        }
        if (!DataValidator.isValidVietnamesePhone(req.getPhone())) {
            return AuthResponse.builder().success(false)
                    .message("Số điện thoại không hợp lệ! Phải bắt đầu bằng số 0 và có từ 10 đến 11 chữ số viết liền.")
                    .build();
        }
        if (!DataValidator.isValidUsername(req.getUsername())) {
            return AuthResponse.builder().success(false).message("Username tối thiểu 3 ký tự!").build();
        }
        if (!DataValidator.isValidPassword(req.getPassword())) {
            return AuthResponse.builder().success(false).message(
                    "Mật khẩu quá yếu! Phải có tối thiểu 8 ký tự, bao gồm ít nhất 1 chữ hoa, 1 chữ thường, 1 chữ số và 1 ký tự đặc biệt.")
                    .build();
        }
        if (req.getAvatar() == null || req.getAvatar().isEmpty()) {
            return AuthResponse.builder().success(false).message("Ảnh đại diện là bắt buộc!").build();
        }
        if (!DataValidator.isValidFileType(req.getAvatar())) {
            return AuthResponse.builder().success(false)
                    .message("File ảnh không hợp lệ! Chỉ chấp nhận các định dạng: JPEG, JPG, PNG, WEBP.").build();
        }
        if (!DataValidator.isValidFileSize(req.getAvatar())) {
            return AuthResponse.builder().success(false)
                    .message("Kích thước file ảnh quá lớn! Vui lòng chọn ảnh có kích thước tối đa 5MB.").build();
        }

        boolean isProvider = "PROVIDER".equalsIgnoreCase(req.getRoleType());

        if (isProvider) {
            if (req.getCompanyName() == null || req.getCompanyName().isEmpty()
                    || req.getTaxCode() == null || req.getTaxCode().isEmpty()
                    || req.getHotline() == null || req.getHotline().isEmpty()
                    || req.getBusinessAddress() == null || req.getBusinessAddress().isEmpty()) {
                return AuthResponse.builder().success(false)
                        .message("Đăng ký đối tác bắt buộc phải nhập đủ thông tin Doanh nghiệp!").build();
            }

            if (!DataValidator.isValidVietnamesePhone(req.getHotline())) {
                return AuthResponse.builder().success(false)
                        .message("Số Hotline không hợp lệ! Phải bắt đầu bằng số 0 và có từ 10 đến 11 chữ số viết liền.")
                        .build();
            }

            if (providerRepository.isExistsByCompanyName(req.getCompanyName())) {
                return AuthResponse.builder().success(false).message("Tên Công ty/Doanh nghiệp này đã được đăng ký!")
                        .build();
            }
            if (providerRepository.isExistsByTaxCode(req.getTaxCode())) {
                return AuthResponse.builder().success(false).message("Mã số thuế đã tồn tại trên hệ thống!").build();
            }
            if (providerRepository.isExistsByHotline(req.getHotline())) {
                return AuthResponse.builder().success(false).message("Số Hotline đã tồn tại trên hệ thống!").build();
            }

        }

        Users user = new Users();
        user.setUsername(req.getUsername());
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setIsActive(true);
        user.setCreatedAt(new Date());

        // Bốc Role chính thống từ DB lên gài vào để tránh lỗi Transient Role
        long targetRoleId = isProvider ? 2L : 3L;
        Roles role = userRepository.findRoleById(targetRoleId);
        user.setRoleId(role);

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    req.getAvatar().getBytes(),
                    ObjectUtils.emptyMap());
            String cloudUrl = (String) uploadResult.get("secure_url");
            user.setAvatarUrl(cloudUrl);
        } catch (IOException e) {
            // Spring bắt buộc nếu muốn rollback tự động thì đoạn này phải ném ra
            // RuntimeException
            throw new RuntimeException("Lỗi khi upload ảnh đại diện lên Cloudinary!");
        }

        if (isProvider) {
            Providers provider = new Providers();
            provider.setCompanyName(req.getCompanyName());
            provider.setTaxCode(req.getTaxCode());
            provider.setHotline(req.getHotline());
            provider.setBusinessAddress(req.getBusinessAddress());
            provider.setIsApproved(false);
            provider.setApprovedAt(null);

            provider.setUserId(user);
            user.setProviders(provider);
        }
        userRepository.save(user);
        String msg = isProvider ? "Đăng ký hồ sơ thành công. Vui lòng đợi Admin phê duyệt!"
                : "Đăng ký tài khoản thành công!";
        return AuthResponse.builder().success(true).message(msg).build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(String username, String password) {
        Users user = userRepository.findByUsername(username);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return AuthResponse.builder().success(false)
                    .message("Tài khoản hoặc mật khẩu không chính xác!").build();
        }
        if (user.getIsActive() == null || !user.getIsActive()) {
            return AuthResponse.builder().success(false)
                    .message("Tài khoản này hiện đang bị khoá!").build();
        }
        String roleName = user.getRoleId().getRoleName();
        String token = jwtProvider.generateToken(user.getUsername(), roleName);
        return AuthResponse.builder()
                .success(true)
                .message("Đăng nhập Vista Travel thành công!")
                .token(token)
                .userDetail(user)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfile getUserProfile(String username) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng với username: " + username);
        }
        UserProfile profile = new UserProfile();
        profile.setUsername(user.getUsername());
        profile.setFullName(user.getFullName());
        profile.setEmail(user.getEmail());
        profile.setPhone(user.getPhone());
        profile.setAvatarUrl(user.getAvatarUrl());
        profile.setRoleName(user.getRoleId().getRoleName());
        profile.setAddress(user.getAddress());

        if (user.getProviders() != null) {
            Providers provider = user.getProviders();
            profile.setCompanyName(provider.getCompanyName());
            profile.setTaxCode(provider.getTaxCode());
            profile.setHotline(provider.getHotline());
            profile.setBusinessAddress(provider.getBusinessAddress());
            profile.setIsApproved(provider.getIsApproved());
            profile.setStatusReason(provider.getStatusReason());
        }

        return profile;
    }

    @Override
    @Transactional
    public String updateUserAvatar(String username, MultipartFile file) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng với username: " + username);
        }

        if (user.getIsActive() != null && !user.getIsActive()) {
            throw new RuntimeException("Tài khoản này hiện đang bị khóa hoạt động, không thể sửa đổi hồ sơ!");
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.emptyMap());
            String cloudUrl = (String) uploadResult.get("secure_url");
            user.setAvatarUrl(cloudUrl);
            userRepository.save(user);
            return cloudUrl;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload ảnh đại diện lên Cloudinary!");
        }
    }

    @Override
    @Transactional
    public AuthResponse updateUserProfile(String username, UserProfile req) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            return AuthResponse.builder().success(false).message("Không tìm thấy người dùng với username: " + username)
                    .build();
        }
        if (user.getIsActive() != null && !user.getIsActive()) {
            return AuthResponse.builder().success(false)
                    .message("Tài khoản này hiện đang bị khóa hoạt động, không thể sửa đổi hồ sơ!").build();
        }

        if (userRepository.isEmailExistForOthers(req.getEmail(), user.getId())) {
            return AuthResponse.builder().success(false).message("Email này đã được một tài khoản khác sử dụng!")
                    .build();
        }
        if (userRepository.isPhoneExistForOthers(req.getPhone(), user.getId())) {
            return AuthResponse.builder().success(false)
                    .message("Số điện thoại này đã được một tài khoản khác sử dụng!").build();
        }

        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setAddress(req.getAddress());

        try {
            userRepository.save(user);
            return AuthResponse.builder().success(true).message("Cập nhật hồ sơ thành công!").build();
        } catch (Exception e) {
            return AuthResponse.builder().success(false).message("Lỗi khi cập nhật hồ sơ: " + e.getMessage()).build();
        }
    }

    @Override
    @Transactional
    public AuthResponse changePassword(String username, ChangePasswordRequest req) {

        Users currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            return AuthResponse.builder().success(false).message("Không tìm thấy thông tin tài khoản!").build();
        }

        if (req.getOldPassword() == null || req.getOldPassword().trim().isEmpty()
                || req.getNewPassword() == null || req.getNewPassword().trim().isEmpty()) {
            return AuthResponse.builder().success(false).message("Vui lòng nhập đầy đủ mật khẩu cũ và mật khẩu mới!")
                    .build();
        }
        if (!DataValidator.isValidPassword(req.getNewPassword())) {
            return AuthResponse.builder().success(false).message(
                    "Mật khẩu mới quá yếu. Tối thiểu 8 ký tự gồm ít nhất 1 chữ hoa, 1 chữ thường, 1 chữ số và 1 ký tự đặc biệt.")
                    .build();
        }

        if (!passwordEncoder.matches(req.getOldPassword(), currentUser.getPassword())) {
            return AuthResponse.builder().success(false).message("Mật khẩu cũ không chính xác!").build();
        }
        if (passwordEncoder.matches(req.getNewPassword(), currentUser.getPassword())) {
            return AuthResponse.builder().success(false).message("Mật khẩu mới bị trùng với mật khẩu hiện tại!")
                    .build();
        }
        currentUser.setPassword(passwordEncoder.encode(req.getNewPassword()));

        try {
            userRepository.save(currentUser);
            return AuthResponse.builder().success(true)
                    .message("Đổi mật khẩu thành công! Vui lòng đăng nhập lại bằng mật khẩu mới.").build();
        } catch (Exception e) {
            return AuthResponse.builder().success(false).message("Lỗi hệ thống khi đổi mật khẩu: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAdminProvidersList(boolean isApproved, Map<String, String> params) {

        List<Providers> providers = providerRepository.getProvidersByStatus(isApproved, params);
        Long totalElementsObj = providerRepository.countProvidersByStatus(isApproved, params);
        long totalElements = totalElementsObj != null ? totalElementsObj : 0L;
        // List<Providers> providers = providerRepository.getProvidersByStatus(isApproved, params);
        //         long totalElements = providerRepository.countProvidersByStatus(isApproved);

        int pageSize = this.env.getProperty("providers.page_size", Integer.class, 20);
        int currentPage = (params != null) ? Integer.parseInt(params.getOrDefault("page", "1")) : 1;

        List<AdminProviderResponse> content = providers.stream()
                .map(com.qd.dto.AdminProviderResponse::new)
                .collect(java.util.stream.Collectors.toList());

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("content", content);
        result.put("totalElements", totalElements);
        result.put("page", currentPage);          // Trang hiện tại
        result.put("size", pageSize);             // Số dòng/trang

        return result;
    }

    @Override
    @Transactional
    public AuthResponse approveProvider(Long id) {
        Providers provider = providerRepository.getProviderWithUserById(id);
        if (provider == null) {
            return AuthResponse.builder().success(false).message("Không tìm thấy đối tác với id này!").build();
        }

        if (provider.getUserId() != null && Boolean.FALSE.equals(provider.getUserId().getIsActive())) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Không thể phê duyệt! Tài khoản đối tác này hiện đang bị KHÓA trên hệ thống.")
                    .build();
        }

        if (Boolean.TRUE.equals(provider.getIsApproved())) {
            return AuthResponse.builder().success(false).message("Hồ sơ đối tác này đã được phê duyệt từ trước!").build();
        }
        provider.setIsApproved(true);
        provider.setApprovedAt(new Date());
        if (provider.getUserId() != null) {
            provider.getUserId().setIsActive(true);
        }
        providerRepository.updateProvider(provider);
        return AuthResponse.builder().success(true).message("Đã phê duyệt đối tác thành công!").build();

    }

    @Override
    @Transactional
    public AuthResponse rejectProvider(Long id, AdminActionRequest req) {
        if (req.getReason() == null || req.getReason().trim().isEmpty()) {
            return AuthResponse.builder().success(false).message("Vui lòng nhập lý do từ chối đơn đăng ký!").build();
        }

        Providers provider = providerRepository.getProviderWithUserById(id);
        if (provider == null) {
            return AuthResponse.builder().success(false).message("Không tìm thấy đối tác với id này!").build();
        }

        provider.setIsApproved(false);
        provider.setApprovedAt(null);
        provider.setStatusReason(req.getReason());

        providerRepository.updateProvider(provider);
        return AuthResponse.builder().success(true).message("Đã từ chối đối tác thành công!").build();
    }

    @Override
    @Transactional
    public AuthResponse banProvider(Long id, AdminActionRequest req) {
        if (req.getReason() == null || req.getReason().trim().isEmpty()) {
            return AuthResponse.builder().success(false).message("Vui lòng nhập lý do khóa tài khoản").build();
        }

        Providers provider = providerRepository.getProviderWithUserById(id);
        if (provider == null) {
            return AuthResponse.builder().success(false).message("Không tìm thấy đối tác id này!").build();
        }
        provider.setIsApproved(false);
        provider.setStatusReason(req.getReason());

        if (provider.getUserId() != null) {
            provider.getUserId().setIsActive(false);
        }

        providerRepository.updateProvider(provider);
        return AuthResponse.builder().success(true).message("Đã khóa tài khoản đối tác này thành công!").build();
    }

    // @Override
    // @Transactional(readOnly = true)
    // public Map<String, Object> getMyServicesList(String username, Map<String, String> params) {
    //     Users user = userRepository.findByUsername(username);
    //     if (user == null || user.getProviders() == null) {
    //         throw new RuntimeException("Cảnh báo bảo mật: Bạn không có quyền truy cập phân hệ Đối tác!");
    //     }
    //     Long providerId = user.getProviders().getId();
    //     List<Services> servicesList = providerRepository.getProviderServicesList(providerId, params);
    //     Long totalElementsObj = providerRepository.countProviderServices(providerId, params);
    //     long totalElements = totalElementsObj != null ? totalElementsObj : 0L;

    //     int pageSize = this.env.getProperty("services.page_size", Integer.class, 10);
    //     int currentPage = (params != null) ? Integer.parseInt(params.getOrDefault("page", "1")) : 1;
    //     List<com.qd.dto.provider.ProviderServiceResponse> content = servicesList.stream()
    //             .map(com.qd.dto.provider.ProviderServiceResponse::new)
    //             .collect(java.util.stream.Collectors.toList());

    //     Map<String, Object> result = new java.util.HashMap<>();
    //     result.put("content", content);
    //     result.put("totalElements", totalElements);
    //     result.put("page", currentPage);
    //     result.put("size", pageSize);

    //     return result;
    // }

    // // @Override
    // // @Transactional(readOnly = true)
    // // public Object getMyServiceDetail(String username, Long id, String typeStr) {
    // //     Users user = userRepository.findByUsername(username);
    // //     if (user == null || user.getProviders() == null) {
    // //         throw new RuntimeException("Tài khoản không hợp lệ/ không có quyền đối tác!");
    // //     }
    // //     Long myProviderId = user.getProviders().getId(); ////Maybe sai chỗ này!!!!!!!!!!!!!!1
    // //     com.qd.enums.ServiceType type;
    // //     try {
    // //         type = com.qd.enums.ServiceType.valueOf(typeStr.toUpperCase());
    // //     } catch (Exception e) {
    // //         throw new RuntimeException("Loại hình dịch vụ hệ thống không hỗ trợ!");
    // //     }
    // //     // Gọi  hàm Repo có lệnh SQL FETCH
    // //     Services service = providerRepository.getServiceDetailByIdAndType(id, type);
    // //     if (service == null) {
    // //         throw new RuntimeException("Không tìm thấy bài viết dịch vụ có mã ID yêu cầu!");
    // //     }
    // //     if (service.getProviderId() == null || !service.getProviderId().getId().equals(myProviderId)) {
    // //         throw new RuntimeException("Vi phạm bảo mật: Bạn không có quyền sở hữu để xem chi tiết dịch vụ này!");
    // //     }
    // //     //Tùy theo loại hình bỏ vào DTO tương ứng trả về JSON
    // //     if (type == ServiceType.TOUR) {
    // //         return new ProviderTourDetailResponse(service); 
    // //     } else if (type == ServiceType.HOTEL) {
    // //         return new ProviderHotelDetailResponse(service); 
    // //     } else {
    // //         // Đối với hệ Xe (TRANSPORT), tạm thời trả về Object thô hoặc tự đúc DTO sau !!!!!
    // //         return service;
    // //     }
    // // }
    // @Override
    // @Transactional(readOnly = true)
    // public Object getMyServiceDetail(String username, Long id, String typeStr) {
    //     Users user = userRepository.findByUsername(username);
    //     if (user == null || user.getProviders() == null) {
    //         throw new RuntimeException("Tài khoản không hợp lệ hoặc không có quyền đối tác!");
    //     }

    //     ServiceType type = ServiceType.valueOf(typeStr.toUpperCase());
    //     Services service = providerRepository.getServiceDetailByIdAndType(id, type);

    //     if (service == null) {
    //         throw new RuntimeException("Không tìm thấy bài viết dịch vụ!");
    //     }

    //     if (service.getProviderId() == null || !service.getProviderId().getId().equals(user.getProviders().getId())) {
    //         throw new RuntimeException("Vi phạm bảo mật: Bạn không có quyền sở hữu dịch vụ này!");
    //     }

    //     return convertToAppropriateDto(service, type);
    // }

    // private Object convertToAppropriateDto(com.qd.pojo.Services service, ServiceType type) {
    //     switch (type) {
    //         case TOUR:
    //             return new ProviderTourDetailResponse(service);
    //         case HOTEL:
    //             return new ProviderHotelDetailResponse(service);
    //         case TRANSPORT:
    //             return new ProviderTransportDetailResponse(service);
    //         default:
    //             return service;
    //     }
    // }

    // @Override
    // @Transactional
    // public Long saveComprehensiveServiceInOneGo(String username, BaseComprehensiveRequest req) {
    //     Users user = userRepository.findByUsername(username);
    //     if (user == null || user.getProviders() == null) {
    //         throw new RuntimeException("Bạn không có quyền của Nhà cung cấp!");
    //     }
    //     ServiceType type = ServiceType.valueOf(req.getServiceType().toUpperCase());
    //     ServiceStatus status = ServiceStatus.DRAFT;
    //     boolean isPublishAction = "PUBLISH".equalsIgnoreCase(req.getAction());
    //     if (isPublishAction) {
    //         status = ServiceStatus.ACTIVATE;
    //     }
    //     Services service = new Services();
    //     service.setName(req.getName());
    //     service.setProviderId(user.getProviders());
    //     service.setDescription(req.getDescription());
    //     service.setServiceType(type);
    //     service.setStatus(status);
    //     service.setCreatedAt(new Date());

    //     if (req.getCategoryId() != null) {
    //         Categories cat = providerRepository.getCategoryById(req.getCategoryId());
    //         if (cat != null) {
    //             Set<Categories> catSet = new HashSet<>();
    //             catSet.add(cat);
    //             service.setCategoriesSet(catSet);
    //         }
    //     }
    //     providerRepository.saveService(service);

    //     if (req instanceof TourComprehensiveRequest) {
    //         TourComprehensiveRequest tourReq = (TourComprehensiveRequest) req;

    //         TourDetails tour = new TourDetails();
    //         tour.setServiceId(service.getId());
    //         tour.setDepartureLocation(tourReq.getDepartureLocation());
    //         tour.setDestinationLocation(tourReq.getDestinationLocation());
    //         tour.setDurationDays(tourReq.getDurationDays());
    //         tour.setDurationNights(tourReq.getDurationNights());
    //         tour.setTransportMode(tourReq.getTransportMode());
    //         providerRepository.saveTourDetails(tour);

    //         if (tourReq.getTourSchedules() != null && !tourReq.getTourSchedules().isEmpty()) {
    //             for (TourComprehensiveRequest.ScheduleInnerDTO sDto : tourReq.getTourSchedules()) {
    //                 TourItemConcs schedule = new TourItemConcs();
    //                 schedule.setTourDetailId(tour);
    //                 schedule.setDepartureTime(sDto.getDepartureTime());
    //                 schedule.setReturnTime(sDto.getReturnTime());
    //                 schedule.setMaxParticipants(sDto.getMaxParticipants());
    //                 providerRepository.saveTourSchedule(schedule);

    //                 if (isPublishAction && sDto.getAvailableSlots() > 0 && sDto.getPrice() != null && sDto.getPrice().compareTo(BigDecimal.ZERO) > 0) {
    //                     SellableItems sellItem = createUnifiedSellableItem(service, sDto.getPrice(), sDto.getAvailableSlots());
    //                     sellItem.setTourItemConcId(schedule);
    //                     providerRepository.saveSellableItem(sellItem);
    //                 }

    //             }
    //         }

    //     } else if (req instanceof HotelComprehensiveRequest) {
    //         HotelComprehensiveRequest hotelReq = (HotelComprehensiveRequest) req;

    //         HotelDetails hotel = new HotelDetails();
    //         hotel.setServiceId(service.getId());
    //         hotel.setStarRating(hotelReq.getStarRating());
    //         hotel.setAddress(hotelReq.getAddress());
    //         hotel.setCity(hotelReq.getCity());
    //         if (hotelReq.getCheckinTime() != null && !hotelReq.getCheckinTime().isEmpty()) {
    //             hotel.setCheckinTime(java.sql.Time.valueOf(hotelReq.getCheckinTime()));
    //         }
    //         if (hotelReq.getCheckoutTime() != null && !hotelReq.getCheckoutTime().isEmpty()) {
    //             hotel.setCheckoutTime(java.sql.Time.valueOf(hotelReq.getCheckoutTime()));
    //         }
    //         hotel.setAmenities(hotelReq.getAmenities());
    //         providerRepository.saveHotelDetails(hotel);

    //         if (hotelReq.getHotelRooms() != null && !hotelReq.getHotelRooms().isEmpty()) {
    //             for (com.qd.dto.provider.HotelComprehensiveRequest.RoomInnerDTO rDto : hotelReq.getHotelRooms()) {
    //                 HotelRoomItems room = new HotelRoomItems();
    //                 room.setHotelDetailId(hotel);
    //                 room.setRoomType(rDto.getRoomType());
    //                 room.setCapacity(rDto.getCapacity());
    //                 room.setBedType(rDto.getBedType());
    //                 room.setRoomSizeM2(rDto.getRoomSizeM2());
    //                 room.setRoomAmenities(rDto.getRoomAmenities());
    //                 providerRepository.saveHotelRoomItem(room);

    //                 if (isPublishAction && rDto.getAvailableSlots() > 0 && rDto.getPrice() != null && rDto.getPrice().compareTo(java.math.BigDecimal.ZERO) > 0) {
    //                     SellableItems sellItem = createUnifiedSellableItem(service, rDto.getPrice(), rDto.getAvailableSlots());
    //                     sellItem.setHotelRoomItemId(room);
    //                     providerRepository.saveSellableItem(sellItem);
    //                 }
    //             }
    //         }

    //     } else if (req instanceof TransportComprehensiveRequest) {
    //         com.qd.dto.provider.TransportComprehensiveRequest transReq = (TransportComprehensiveRequest) req;

    //         TransportDetails trans = new TransportDetails();
    //         trans.setServiceId(service.getId());
    //         trans.setBrandName(transReq.getBrandName());
    //         trans.setVehicleType(transReq.getVehicleType());
    //         trans.setDepartureStation(transReq.getDepartureStation());
    //         trans.setArrivalStation(transReq.getArrivalStation());
    //         providerRepository.saveTransportDetails(trans);

    //         if (transReq.getTransportTickets() != null && !transReq.getTransportTickets().isEmpty()) {
    //             for (com.qd.dto.provider.TransportComprehensiveRequest.TicketInnerDTO tDto : transReq.getTransportTickets()) {
    //                 TransportTicketItems ticket = new TransportTicketItems();
    //                 ticket.setTransportDetailId(trans);
    //                 ticket.setDepartureTime(tDto.getDepartureTime());
    //                 ticket.setArrivalTime(tDto.getArrivalTime());
    //                 ticket.setDurationMinutes(tDto.getDurationMinutes());
    //                 ticket.setSeatClass(tDto.getSeatClass());
    //                 providerRepository.saveTransportTicketItem(ticket);

    //                 if (isPublishAction && tDto.getAvailableSlots() > 0 && tDto.getPrice() != null && tDto.getPrice().compareTo(java.math.BigDecimal.ZERO) > 0) {
    //                     SellableItems sellItem = createUnifiedSellableItem(service, tDto.getPrice(), tDto.getAvailableSlots());
    //                     sellItem.setTransportTicketItemId(ticket);
    //                     providerRepository.saveSellableItem(sellItem);
    //                 }

    //             }
    //         }
    //     }

    //     return service.getId();
    // }

    // private SellableItems createUnifiedSellableItem(Services service, java.math.BigDecimal price, int slots) {
    //     SellableItems sellItem = new SellableItems();
    //     sellItem.setServiceId(service);
    //     sellItem.setPrice(price);
    //     sellItem.setAvailableSlots(slots);  //Check cho nay coi neu 0 slot phai la Out Of Stock ItemStatus
    //     sellItem.setItemStatus(ItemStatus.AVAILABLE);
    //     sellItem.setCreatedAt(new java.util.Date());
    //     return sellItem;
    // }

}
