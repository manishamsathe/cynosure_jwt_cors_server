package com.cynosure.cynosure_jwt_server.service;

import com.cynosure.cynosure_jwt_server.dao.UserRepository;
import com.cynosure.cynosure_jwt_server.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    public final User loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }
        detailsChecker.check(user);
        return user;

    }

}
