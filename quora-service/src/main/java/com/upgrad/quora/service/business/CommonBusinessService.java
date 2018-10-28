package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class CommonBusinessService {

    @Autowired
    private UserDao userDao;

    public UserEntity userProfile(final String userUuid, final String accessToken) throws UserNotFoundException, AuthorizationFailedException {

       UserAuthEntity userAuthEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Check if user logout or not ...
        ZonedDateTime userLogoutTime = userAuthEntity.getLogoutAt();
        if (userLogoutTime == null){

            final UserEntity userEntity = userDao.getUserByUuid(userUuid);

            if (userEntity == null) {
                throw new UserNotFoundException("USR-001", "'User with entered uuid does not exist");
            }
            return userEntity;
        }
        throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
    }

}
