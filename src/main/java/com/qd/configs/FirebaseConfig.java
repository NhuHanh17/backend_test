package com.qd.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() {
        try {
            InputStream serviceAccount;

            String firebaseConfig = System.getenv("FIREBASE_CONFIG");

            if (firebaseConfig != null && !firebaseConfig.isBlank()) {
                serviceAccount = new ByteArrayInputStream(
                        firebaseConfig.getBytes(StandardCharsets.UTF_8));
            } else {
                serviceAccount = this.getClass()
                        .getClassLoader()
                        .getResourceAsStream("travelvista-firebase.json");
            }

            if (serviceAccount == null) {
                throw new RuntimeException("Không tìm thấy cấu hình Firebase");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId("travelvista-6fea1")
                    .setDatabaseUrl("https://travelvista-6fea1-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Kết nối Firebase thành công!");
            }

        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo Firebase: " + e.getMessage());
        }
    }
}