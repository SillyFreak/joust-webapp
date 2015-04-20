package at.pria.joust.webapp.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserDetailsManager users;
    @Autowired
    private PasswordEncoder enc;

    public UserDetailsManager getUserDetailsManager() {
        return users;
    }

    public PasswordEncoder getPasswordEncoder() {
        return enc;
    }

    public List<GrantedAuthority> roles(String... roles) {
        ArrayList<GrantedAuthority> result = new ArrayList<>(roles.length);
        for (String role : roles)
            result.add(new SimpleGrantedAuthority("ROLE_" + role));
        return result;
    }

    public List<GrantedAuthority> authorities(String... authorities) {
        ArrayList<GrantedAuthority> result = new ArrayList<>(authorities.length);
        for (String authority : authorities)
            result.add(new SimpleGrantedAuthority(authority));
        return result;
    }

    //create user
    @Autowired
    private MailSender mailSender;

    @Autowired
    private SimpleMailMessage registerMailTemplate;

    public void createUser(String username, String password, List<? extends GrantedAuthority> authorities) {
        createUser(username, password, true, authorities);
    }

    public void registerUser(String username, String password, List<? extends GrantedAuthority> authorities) {
        createUser(username, password, false, authorities);

        SimpleMailMessage msg = new SimpleMailMessage(registerMailTemplate);
        msg.setTo("REDACTED");
        try {
            mailSender.send(msg);
        } catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }

    public void createUser(String username, String password, boolean enabled, List<? extends GrantedAuthority> authorities) {
        users.createUser(new User(username, enc.encode(password), enabled, true, true, true, authorities));
    }

    //activate user
    public void activateUser(String username) {
        UserDetails user = users.loadUserByUsername(username);
        UserDetails updated = new User(user.getUsername(), user.getPassword(), true, user.isAccountNonExpired(),
                user.isCredentialsNonExpired(), user.isAccountNonLocked(), user.getAuthorities());
        users.updateUser(updated);
    }

    //reset password
    public void resetPassword(String username, String newPassword) {
        UserDetails user = users.loadUserByUsername(username);
        UserDetails updated = new User(user.getUsername(), enc.encode(newPassword), user.isEnabled(), user.isAccountNonExpired(),
                user.isCredentialsNonExpired(), user.isAccountNonLocked(), user.getAuthorities());
        users.updateUser(updated);
    }

    //change password
    public void changePassword(String oldPassword, String newPassword) {
        users.changePassword(oldPassword, enc.encode(newPassword));
    }
}
