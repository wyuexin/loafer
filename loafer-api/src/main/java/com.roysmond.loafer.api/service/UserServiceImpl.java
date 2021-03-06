package com.roysmond.loafer.api.service;

import com.roysmond.loafer.api.domain.Authority;
import com.roysmond.loafer.api.domain.User;
import com.roysmond.loafer.api.repository.AuthorityRepository;
import com.roysmond.loafer.api.repository.UserRepository;
import com.roysmond.loafer.api.security.AuthoritiesConstants;
import com.roysmond.loafer.api.utils.RandomUtil;
import com.roysmond.loafer.api.web.models.VMUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by wuyuexin on 2017/5/17.
 */
@Service
public class UserServiceImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    private final UserRepository userRepository;

    public UserServiceImpl(PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(VMUser userVM) {
        User newUser = new User();
        Authority authority = authorityRepository.findOne(AuthoritiesConstants.USER);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);

        String encryptedPassword = passwordEncoder.encode(userVM.getPassword());
        newUser.setActivated(false);
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        newUser.setLogin(userVM.getEmail());
        newUser.setEmail(userVM.getEmail());
        String[] names = userVM.getUsername().split(" ");
        if (names.length > 1) {
            newUser.setFirstName(names[0]);
            newUser.setLastName(userVM.getUsername().replaceFirst(names[0], "").trim());
        } else {
            newUser.setLastName(userVM.getUsername());
        }
        newUser.setPassword(encryptedPassword);
        newUser.setAuthorities(authorities);

        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }
}
