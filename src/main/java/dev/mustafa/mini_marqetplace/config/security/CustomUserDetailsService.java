package dev.mustafa.mini_marqetplace.config.security;

import dev.mustafa.mini_marqetplace.exception.NotFoundException;
import dev.mustafa.mini_marqetplace.model.entity.User;
import dev.mustafa.mini_marqetplace.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("user with %s email not found".formatted(username)));
        return new CustomUserDetails(user);
    }
}
