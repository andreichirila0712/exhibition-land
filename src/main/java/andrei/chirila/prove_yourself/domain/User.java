package andrei.chirila.prove_yourself.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    @Column(name = "id")
    private UUID id;
    @Column(name = "name")
    private String name;
    @Column(name = "username", unique = true)
    private String accountName;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "images")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> images = new HashMap<>();
    @Column(name= "email_verified")
    private boolean emailVerified;
    //@OneToMany(mappedBy = "user")
    private Set<String> presentations; // TODO: CHANGE THIS TO PRESENTATION TYPE
    @Column(name = "role", nullable = true)
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "language")
    private String language;
    @Column(name = "theme")
    private String theme;
    @Column(name = "date_format")
    private String dateFormat;
    @Column(name = "profile_visibility")
    private String profileVisibility;
    @Column(name = "profile_discoverable")
    private String profileDiscoverable;
    @Column(name = "about")
    private String about = "";
    @Column(name = "location")
    private String location = "";
    @Column(name = "website")
    private String website = "";

    public User() {}

    public User(String name, String email) {
        this.email = email;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    public String getPassword() {
        return password;
    }

    public Map<String, String> getImages() {
        return images;
    }

    public void setImage(String key, String value) {
        this.images.put(key, value);
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Set<String> getPresentations() {
        return presentations;
    }

    public void setPresentations(Set<String> presentations) {
        this.presentations = presentations;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getProfileVisibility() {
        return profileVisibility;
    }

    public void setProfileVisibility(String profileVisibility) {
        this.profileVisibility = profileVisibility;
    }

    public String getProfileDiscoverable() {
        return profileDiscoverable;
    }

    public void setProfileDiscoverable(String profileDiscoverable) {
        this.profileDiscoverable = profileDiscoverable;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
