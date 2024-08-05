package nl.novi.LivingInSync.controller;

import nl.novi.LivingInSync.dto.UserDto;
import nl.novi.LivingInSync.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserDto userDTO) {
        try {
            String newUsername = userService.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Welcome " + userDTO.getName() + "!");
        } catch (IllegalArgumentException e) {
            logger.severe("Error creating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            logger.severe("Unexpected error creating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the user");
        }
    }

    @PutMapping
    public ResponseEntity<String> updateUser(@RequestBody UserDto userDTO, Authentication authentication) {
        try {
            String username = authentication.getName();
            userService.updateUser(username, userDTO);
            return ResponseEntity.ok("Your new user details have been saved!");
        } catch (UsernameNotFoundException e) {
            logger.warning("User not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            logger.severe("Error updating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating user details");
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUser(Authentication authentication) {
        try {
            String username = authentication.getName();
            userService.deleteUser(username);
            return ResponseEntity.ok("Your account has been deleted, goodbye!");
        } catch (UsernameNotFoundException e) {
            logger.warning("User not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            logger.severe("Error deleting user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the user");
        }
    }

    @GetMapping
    public ResponseEntity<UserDto> getUser(Authentication authentication) {
        try {
            String username = authentication.getName();
            UserDto userDTO = userService.getUser(username);
            return ResponseEntity.ok(userDTO);
        } catch (UsernameNotFoundException e) {
            logger.warning("User not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.severe("Error retrieving user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
