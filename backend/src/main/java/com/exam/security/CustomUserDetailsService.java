package com.exam.security;

import com.exam.entity.User;
import com.exam.repository.UserRepository;
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

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        // Allow inactive users to authenticate so we can show proper error message
        // Only block locked users at this level
        boolean enabled = user.getStatus() != User.Status.locked;
        boolean accountNonLocked = user.getStatus() != User.Status.locked;
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                enabled,
                true,
                true,
                accountNonLocked,
                getAuthorities(user)
        );
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name().toUpperCase()));
        return authorities;
    }
}
