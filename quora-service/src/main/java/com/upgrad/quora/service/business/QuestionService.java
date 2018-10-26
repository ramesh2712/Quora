package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class QuestionService {

    @Autowired
    UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity) {

        return userDao.createQuestion(questionEntity);
    }

    public UserEntity getUser(final String accessToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001" ,"User has not signed in");
        }
        // Check if user logout or not ...
        ZonedDateTime userLogoutTime = userAuthEntity.getLogoutAt();
        if (userLogoutTime == null){

            final UserEntity userEntity = userAuthEntity.getUser();
            return userEntity;
        }
        throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
    }
}
