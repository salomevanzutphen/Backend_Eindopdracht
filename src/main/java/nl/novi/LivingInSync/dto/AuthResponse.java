package nl.novi.LivingInSync.dto;

import java.util.List;

public class AuthResponse {

    private final String jwt;
    private String username;
    private String email;
    private String name;
    private List<String> roles;

    public AuthResponse(String jwt, String username, String email, String name, List<String> roles) {
        this.jwt = jwt;
        this.username = username;
        this.email = email;
        this.name = name;
        this.roles = roles;
    }

    public String getJwt() {
        return jwt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
