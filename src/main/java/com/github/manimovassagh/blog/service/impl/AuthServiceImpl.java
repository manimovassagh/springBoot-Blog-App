package com.github.manimovassagh.blog.service.impl;

import com.github.manimovassagh.blog.entity.Role;
import com.github.manimovassagh.blog.entity.User;
import com.github.manimovassagh.blog.exception.BlogApiException;
import com.github.manimovassagh.blog.payload.LoginDto;
import com.github.manimovassagh.blog.payload.RegisterDto;
import com.github.manimovassagh.blog.repository.RoleRepository;
import com.github.manimovassagh.blog.repository.UserRepository;
import com.github.manimovassagh.blog.service.serviceInterface.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service

public class AuthServiceImpl implements AuthService {


    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthenticationManager authenticationManager
            , UserRepository userRepository
            , RoleRepository roleRepository
            , PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public String login(LoginDto loginDto) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail()
                        , loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "User Logged in Successfully !!!";
    }

    @Override
    public String register(RegisterDto registerDto) {
        //ADD CHECK FOR USERNAME
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new BlogApiException(HttpStatus.BAD_REQUEST, "Username is already exist");
        }
        //add check for email exist in database
        if (userRepository.existsByEmail(registerDto.getUsername())) {
            throw new BlogApiException(HttpStatus.BAD_REQUEST, "Email is already exist");
        }
        User user = User.builder()
                .name(registerDto.getName())
                .username(registerDto.getUsername())
                .email(registerDto.getEmail()).password(passwordEncoder.encode(registerDto.getPassword())).build();
        Set<Role> roles = new HashSet<>();

        Role userRole = roleRepository.findByName("ROLE_USER").get();
        roles.add(userRole);
        user.setRoles(roles);
        userRepository.save(user);

        return "User Registered Successfully !!!";
    }
}
