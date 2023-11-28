package org.ppke.itk.cnctoolpathsharing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ppke.itk.cnctoolpathsharing.domain.User;
import org.ppke.itk.cnctoolpathsharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationProviderImpl implements AuthenticationProvider {

    private final UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info(String.format("User %s is being authenticated.", authentication.getName()));

        User foundUser = userRepository.findByName(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("User not found: %s", authentication.getName())));

        if (Arrays.equals(passwordEncoder.encode(
                Arrays.toString(foundUser.getPassword())).getBytes(),foundUser.getPassword())) {
            throw new BadCredentialsException("Invalid username or password.");
        }

        String userRole = foundUser.getRole();
        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                        foundUser.getName(),
                        Arrays.toString(foundUser.getPassword()),
                        true,
                        true,
                        true,
                        true,
                        Collections.singleton(new SimpleGrantedAuthority(userRole))),
                foundUser.getPassword(), Collections.singleton(new SimpleGrantedAuthority(userRole)));
    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}