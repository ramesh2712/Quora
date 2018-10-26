package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
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

    public List<QuestionEntity> getAllQuestions(final String accessToken) throws AuthorizationFailedException {

        // Check for user sign in ...
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001" ,"User has not signed in");
        }

        // Check if user logout or not ...
        ZonedDateTime userLogoutTime = userAuthEntity.getLogoutAt();
        if (userLogoutTime == null){

            UserEntity userEntity = userAuthEntity.getUser();

            return userDao.getAllQuestions(userEntity);
        }
        throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(final String accessToken, final String questionId ,final String content) throws AuthorizationFailedException, InvalidQuestionException {

        // Check for user sign in ...
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001" ,"User has not signed in");
        }

        // Check if user logout or not ...
        ZonedDateTime userLogoutTime = userAuthEntity.getLogoutAt();
        if (userLogoutTime == null){

            // Check for Question exist in Database ....
            UserEntity userEntity = userAuthEntity.getUser();
            final QuestionEntity questionEntity = userDao.getQuestionByQuestionId(questionId);
            if (questionEntity == null) {
                throw new InvalidQuestionException("QUES-001" ,"Entered question uuid does not exist");
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
}
