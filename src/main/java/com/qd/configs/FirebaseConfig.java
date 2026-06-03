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

}
