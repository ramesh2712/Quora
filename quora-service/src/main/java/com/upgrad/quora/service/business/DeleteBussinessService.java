package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class DeleteBussinessService {


    @Autowired
    UserDao userDao;

    UserEntity userEntity;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity deleteUser(final String authToken, String uname) throws AuthorizationFailedException, UserNotFoundException {


        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authToken);

        //check if the user is signed in
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");

            //check if user has signed out
        } else if (userAuthEntity.getLogoutAt()!=null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");

            //check if user is admin
        } else if (userAuthEntity.getUser().getRole().equalsIgnoreCase("admin")) {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }

        // check if username to be deleted exits in database
        else if (userDao.getUserByUserName(uname) == null) {

            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");

        } else {

            //delete user if everything above fails
            userEntity = userDao.getUserByUserName(uname);
            userDao.deleteUser(userEntity);
        }

        return userEntity;

    }
}