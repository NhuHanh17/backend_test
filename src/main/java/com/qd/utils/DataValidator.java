/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.utils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ADMIN
 */
public class DataValidator {
    private static final String PHONE_REGEX = "^0\\d{9,10}$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()_\\\\-]).{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg", "image/webp");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    
    public static boolean isValidVietnamesePhone(String phone) {
        if (phone == null) return false;
        return PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }


    public static boolean isValidFileType(MultipartFile file) {
        if (file == null || file.isEmpty()) return false;
        
        String contentType = file.getContentType();
        boolean isImageContent = contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase());
        
        String fileName = file.getOriginalFilename(); 
        boolean hasValidExtension = false;
        if (fileName != null) {
            String lowerName = fileName.toLowerCase();
            hasValidExtension = lowerName.endsWith(".png") || 
                                 lowerName.endsWith(".jpg") || 
                                 lowerName.endsWith(".jpeg") || 
                                 lowerName.endsWith(".webp");
        }
        
        return isImageContent && hasValidExtension;
    }

    public static boolean isValidFileSize(MultipartFile file) {
        if (file == null || file.isEmpty()) return true; // Không có file thì không bàn, nếu có thì check size
        return file.getSize() <= MAX_FILE_SIZE;
    }

    public static boolean isValidUsername(String username) {
        if (username == null || username.isBlank()) return false;
        return username.length() >= 3 && username.length() <= 50;
    }
}
