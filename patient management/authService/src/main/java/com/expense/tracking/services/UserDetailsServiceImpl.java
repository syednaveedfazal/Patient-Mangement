package com.expense.tracking.services;

import com.expense.tracking.entities.UserInfo;
import com.expense.tracking.model.UserInfoDTO;
import com.expense.tracking.repository.UserRepository;
import com.expense.tracking.util.UserValidation;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Data
@Component
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = userRepository.findByUserName(username);
        if (userInfo == null) {
            throw new UsernameNotFoundException("User is not found");
        }
        return new CustomUserDetails(userInfo);
    }

    public UserInfo checkIfUserAlreadyExists(UserInfoDTO userInfoDTO) {
        return userRepository.findByUserName(userInfoDTO.getUserName());
    }

    public Boolean signUpUser(UserInfoDTO userInfoDTO) {
        if (UserValidation.validateUserCredentials(userInfoDTO) == false) {
            return false;
        }
        userInfoDTO.setPassword(passwordEncoder.encode(userInfoDTO.getPassword()));
        if (Objects.nonNull(checkIfUserAlreadyExists(userInfoDTO))) {
            return false;
        }
        String userId = UUID.randomUUID().toString();
        userRepository.save(UserInfo.builder()
                .userId(userId)
                .userName(userInfoDTO.getUserName())
                .email(userInfoDTO.getEmail())
                .password(userInfoDTO.getPassword())
                .roles(new HashSet<>())
                .build());
        return true;
    }
}
