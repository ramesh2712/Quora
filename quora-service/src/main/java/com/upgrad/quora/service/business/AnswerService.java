package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class AnswerService {

    @Autowired
    UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final AnswerEntity answerEntity) {

        return userDao.createAnswer(answerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswerContent(final String accessToken, final String answerId, final String content) throws AuthorizationFailedException, AnswerNotFoundException {

        // Check for user sign in ...
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Check if user logout or not ...
        ZonedDateTime userLogoutTime = userAuthEntity.getLogoutAt();
        if (userLogoutTime == null) {

            // Check for Answer exist in Database ....
            UserEntity userEntity = userAuthEntity.getUser();
            final AnswerEntity answerEntity  = userDao.getAnswerByAnswerId(answerId);
            if (answerEntity == null) {
                throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
            }

            // check of owner of answer .....
            final UserEntity userOfAnswer = answerEntity.getUser();
            if (userOfAnswer.getUuid() == userEntity.getUuid()) {

                // update content in database ...
                userDao.editAnswerContent(answerEntity);
                answerEntity.setAnswer(content);
                return answerEntity;
            }
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }
        throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
    }
}
