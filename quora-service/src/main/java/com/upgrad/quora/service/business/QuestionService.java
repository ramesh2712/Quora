package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity) {

        return userDao.createQuestion(questionEntity);
    }

    public UserEntity getUser(final String accessToken) throws AuthorizationFailedException {

        // Check for user sign in ...

        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        // Check if user logout or not ...
        ZonedDateTime userLogoutTime = userAuthEntity.getLogoutAt();
        if (userLogoutTime == null) {

            final UserEntity userEntity = userAuthEntity.getUser();
            return userEntity;
        }
        throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
    }

    public List<QuestionEntity> getAllQuestions(final String accessToken) throws AuthorizationFailedException {

        // Check for user sign in ...
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Check if user logout or not ...
        ZonedDateTime userLogoutTime = userAuthEntity.getLogoutAt();
        if (userLogoutTime == null) {

            UserEntity userEntity = userAuthEntity.getUser();

            return userDao.getAllQuestions(userEntity);
        }
        throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(final String accessToken, final String questionId, final String content) throws AuthorizationFailedException, InvalidQuestionException {

        // Check for user sign in ...
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Check if user logout or not ...
        ZonedDateTime userLogoutTime = userAuthEntity.getLogoutAt();
        if (userLogoutTime == null) {

            // Check for Question exist in Database ....
            UserEntity userEntity = userAuthEntity.getUser();
            final QuestionEntity questionEntity = userDao.getQuestionByQuestionId(questionId);
            if (questionEntity == null) {
                throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
            }

            // check of owner of question .....
            final UserEntity userOfQuestion = questionEntity.getUser();
            if (userOfQuestion.getUuid() == userEntity.getUuid()) {

                // update content in database ...
                userDao.editQuestionContent(questionEntity);
                questionEntity.setContent(content);
                return questionEntity;
            }
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
        throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(final String accessToken, final String questionId) throws AuthorizationFailedException, InvalidQuestionException {

        // Check for user sign in ...
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Check if user logout or not ...
        ZonedDateTime userLogoutTime = userAuthEntity.getLogoutAt();
        if (userLogoutTime == null) {

            // Check for Question exist in Database ....
            final UserEntity userEntity = userAuthEntity.getUser();
            QuestionEntity questionEntity = userDao.getQuestionByQuestionId(questionId);
            if (questionEntity == null) {
                throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
            }

            // check of owner of the question .....
            final UserEntity userOfQuestion = questionEntity.getUser();
            if (userOfQuestion.getUuid().equals(userEntity.getUuid()) || userEntity.getRole().equals("admin")) {

                userDao.deleteQuestion(questionEntity);
                return questionEntity;
            }
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }
        throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");

    }

    public List<QuestionEntity> getAllQuestionsByUserId(final String accessToken, final String uuid) throws AuthorizationFailedException, UserNotFoundException {

        // Check for user sign in ...
        final UserAuthEntity userAuthEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Check if user logout or not ...
        ZonedDateTime userLogoutTime = userAuthEntity.getLogoutAt();
        if (userLogoutTime == null) {

            final UserEntity userEntity = userDao.getUserByUuid(uuid);
            if(userEntity != null ) {

                return userDao.getAllQuestions(userEntity);
            }
            throw new UserNotFoundException("USR-001" , "User with entered uuid whose question details are to be seen does not exist");

        }
        throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
    }
    public  QuestionEntity getQuestionByQuestionId(final String questionId) throws InvalidQuestionException {
        // Check for Question exist in Database ....
        QuestionEntity questionEntity = userDao.getQuestionByQuestionId(questionId);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }
        return questionEntity;
    }
}
