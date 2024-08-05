//package nl.novi.LivingInSync.service;
//
//import nl.novi.LivingInSync.model.Authority;
//import nl.novi.LivingInSync.model.User;
//import nl.novi.LivingInSync.repository.UserRepository;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//import org.springframework.boot.CommandLineRunner;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import static nl.novi.LivingInSync.security.SecurityConfig.passwordEncoder;
//
//@Component
//public class AdminAccountInitializer {
//
//    @Bean
//    CommandLineRunner init(UserRepository userRepository) {
//        return args -> {
//            if (!userRepository.existsByUsername("admin")) {
//                User admin = new User();
//                admin.setUsername("admin");
//                admin.setPassword(passwordEncoder().encode("AdminPassword"));
//                admin.setName("Administrator");
//                admin.setEmail("livinginsync@gmail.com");
//
//                Set<Authority> authorities = new HashSet<>();
//                authorities.add(new Authority("admin", "ROLE_ADMIN"));
//                admin.setAuthorities(authorities);
//
//                userRepository.save(admin);
//            }
//        };
//    }
//}