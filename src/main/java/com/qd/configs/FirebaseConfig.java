package com.qd.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.InputStream;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {
   @PostConstruct
   public void initializeFirebase() {
       try {
           InputStream serviceAccount = this.getClass().getClassLoader()
                   .getResourceAsStream("travelvista-firebase.json");

                if (serviceAccount == null) {
                throw new RuntimeException("Không tìm thấy file ");
            }
           FirebaseOptions options = FirebaseOptions.builder()
                   .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                   .setDatabaseUrl("https://travelvista-6fea1-default-rtdb.asia-southeast1.firebasedatabase.app/") 
                   .build();

           if (FirebaseApp.getApps().isEmpty()) {
               FirebaseApp.initializeApp(options);
               System.out.println("Kết nối Cloud Google thành công!");
           }
       } catch (Exception e) {
           System.err.println("Lỗi khởi tạo Firebase: " + e.getMessage());
       }
   }

//   try {
//            getFirebaseAppInstance();
//        } catch (Exception e) {
//            System.err.println("🚨 Cảnh báo khởi tạo Firebase lúc đầu: " + e.getMessage());
//        }
//    }

    // public static synchronized FirebaseApp getFirebaseAppInstance() throws Exception {
    //     if (FirebaseApp.getApps().isEmpty()) {
    //         InputStream serviceAccount = FirebaseConfig.class.getClassLoader()
    //                 .getResourceAsStream("travelvista-firebase.json");

    //         if (serviceAccount == null) {
    //             throw new RuntimeException("Không tìm thấy file travelvista-firebase.json trong tài nguyên resources!");
    //         }

    //         FirebaseOptions options = FirebaseOptions.builder()
    //                 .setCredentials(GoogleCredentials.fromStream(serviceAccount))
    //                 .setDatabaseUrl("https://tên-dự-án-của-ông-giáo-rtdb.firebaseio.com/") // 🚨 Nhớ thay URL chính chủ xịn vào đây nhen!
    //                 .build();

    //         System.out.println("🎉 [Firebase Admin SDK] Đang kích nổ đúc ứng dụng [DEFAULT] lên RAM...");
    //         return FirebaseApp.initializeApp(options);
    //     }
    //     return FirebaseApp.getInstance(); // Nếu có rồi thì trả về luôn, ko sợ lỗi trùng lặp Bean
    // }
}
