package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity createUser(final UserEntity userEntity) throws SignUpRestrictedException {

        // Check for username exist ...
        final String username = userEntity.getUserName();
        final UserEntity fetchedUserByName =  userDao.getUserByUserName(username);
        if (fetchedUserByName != null) {

            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
        }

        // Check for email exist ...
        final String email = userEntity.getEmail();
        final UserEntity fetchedUserByEmail = userDao.getUserByEmail(email);

        if(fetchedUserByEmail != null) {
            throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
        }

        String password = userEntity.getPassword();
        String[] encryptedText = passwordCryptographyProvider.encrypt(password);
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        return userDao.createUser(userEntity);
    }
}
