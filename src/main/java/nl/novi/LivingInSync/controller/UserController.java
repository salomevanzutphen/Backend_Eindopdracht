package nl.novi.LivingInSync.controller;

import nl.novi.LivingInSync.dto.UserDto;
import nl.novi.LivingInSync.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserDto userDTO) {
        String newUsername = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Welcome " + userDTO.getName() + "!");
    }

    @PutMapping
    public ResponseEntity<String> updateUser(@RequestBody UserDto userDTO, Authentication authentication) {
        String username = authentication.getName();
        userService.updateUser(username, userDTO);
        return ResponseEntity.ok("Your new user details have been saved!");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUser(Authentication authentication) {
        String username = authentication.getName();
        userService.deleteUser(username);
        return ResponseEntity.ok("Your account has been deleted, goodbye!");
    }

    @GetMapping
    public ResponseEntity<UserDto> getUser(Authentication authentication) {
        String username = authentication.getName();
        UserDto userDTO = userService.getUser(username);
        return ResponseEntity.ok(userDTO);
    }
}
