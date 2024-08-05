package nl.novi.LivingInSync.security;

import nl.novi.LivingInSync.dto.UserDto;
import nl.novi.LivingInSync.model.Authority;
import nl.novi.LivingInSync.model.User;
import nl.novi.LivingInSync.repository.UserRepository;
import nl.novi.LivingInSync.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final UserService userService;
//
//    public CustomUserDetailsService(UserService userService) {
//        this.userService = userService;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        UserDto userDto = userService.getUser(username);
//
//        if (userDto == null) {
//            throw new UsernameNotFoundException("User not found with username: " + username);
//        }
//
//        String password = userDto.getPassword();
//        Set<Authority> authorities = userDto.getAuthorities();
//        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
//        for (Authority authority : authorities) {
//            grantedAuthorities.add(new SimpleGrantedAuthority(authority.getAuthority()));
//        }
//
//        return new org.springframework.security.core.userdetails.User(username, password, grantedAuthorities);
//    }
//}
//
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                getAuthorities(user)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList());
    }
}
