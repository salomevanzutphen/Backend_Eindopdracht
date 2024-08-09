package nl.novi.LivingInSync.service;

import nl.novi.LivingInSync.dto.UserDto;
import nl.novi.LivingInSync.model.Authority;
import nl.novi.LivingInSync.model.User;
import nl.novi.LivingInSync.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static nl.novi.LivingInSync.security.SecurityConfig.passwordEncoder;

@Service
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String createUser(UserDto userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User newUser = toUser(userDTO);
        userRepository.save(newUser);

        return newUser.getUsername();
    }

    public void updateUser(String username, UserDto userDTO) {
        User currentUser = findUserByUsername(username);
        updateUserDetails(currentUser, userDTO);

        userRepository.save(currentUser);
    }

    public void deleteUser(String username) {
        User currentUser = findUserByUsername(username);
        userRepository.delete(currentUser);
    }

    public UserDto getUser(String username) {
        User currentUser = findUserByUsername(username);
        return toUserDTO(currentUser);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private User toUser(UserDto userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder().encode(userDTO.getPassword()));
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setBirthday(userDTO.getBirthday());

        // Default role of everyone who registers is user
        Set<Authority> authorities = new HashSet<>();
        authorities.add(new Authority(userDTO.getUsername(), "ROLE_USER"));
        user.setAuthorities(authorities);

        return user;
    }

    private UserDto toUserDTO(User user) {
        UserDto userDTO = new UserDto();
        userDTO.setUsername(user.getUsername());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setBirthday(user.getBirthday());
        userDTO.setAuthorities(user.getAuthorities());
        // Do not set the password for output
        return userDTO;
    }

    private void updateUserDetails(User user, UserDto userDTO) {
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setBirthday(userDTO.getBirthday());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder().encode(userDTO.getPassword()));
        }
    }
}
