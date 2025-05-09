package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.LoginRequest;
import com.ecommerce.backend.dto.RegisterRequest;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // 🔐 Giriş işlemi
    public String login(LoginRequest request) {
        System.out.println("🔐 Gelen email: " + request.getEmail());
        System.out.println("🔐 Gelen şifre: " + request.getPassword());

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        System.out.println("🔍 Kullanıcı bulundu mu? " + userOpt.isPresent());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Kullanıcı bulunamadı.");
        }

        User user = userOpt.get();

        System.out.println("📦 Veritabanı şifresi (BCrypt): " + user.getPassword());
        boolean passwordMatch = BCrypt.checkpw(request.getPassword(), user.getPassword());
        System.out.println("✅ Şifre eşleşiyor mu? " + passwordMatch);

        if (!passwordMatch) {
            throw new RuntimeException("Şifre yanlış.");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());
        System.out.println("🎫 Üretilen Token: " + token);
        return token;
    }

    // 📝 Kayıt işlemi
    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email).isPresent()) {
            throw new RuntimeException("Bu e-posta zaten kayıtlı.");
        }

        String hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt());

        User user = new User();
        user.setFullName(request.fullName);
        user.setEmail(request.email);
        user.setPassword(hashedPassword);
        user.setRole(request.role != null ? request.role : "CUSTOMER");


        return userRepository.save(user);
    }

    public User loginAndReturnUser(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Kullanıcı bulunamadı");
        }
    
        User user = userOpt.get();
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Şifre yanlış");
        }
    
        return user;
    }
    
}
