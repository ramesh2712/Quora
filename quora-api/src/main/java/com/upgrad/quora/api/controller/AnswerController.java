package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    AnswerService answerService;

    @Autowired
    QuestionService questionService;


    @Autowired
    UserDao userDao;

    @RequestMapping(method = RequestMethod.POST , path = "/question/{questionId}/answer/create" , consumes = MediaType.APPLICATION_JSON_UTF8_VALUE , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest, @PathVariable("questionId") final String questionId,
                                                       @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {


        final UserEntity userEntity = answerService.getUser(accessToken);
        final QuestionEntity questionEntity = questionService.getQuestionByQuestionId(questionId);
        final AnswerEntity answerEntity  = new AnswerEntity();
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setUser(userEntity);
        answerEntity.setQuestionEntity(questionEntity);
        answerEntity.setAnswer(answerRequest.getAnswer());
        answerEntity.setDate(ZonedDateTime.now());


        final AnswerEntity createAnswer = answerService.createAnswer(answerEntity);
        AnswerResponse answerResponse = new AnswerResponse().id(createAnswer.getUuid()).status("ANSWER CREATED");

        return new ResponseEntity<AnswerResponse>(answerResponse,HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT , path = "/answer/edit/{answerId}" ,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editQuestion(final AnswerEditRequest answerEditRequest,
                                                                  @PathVariable("answerId") final String answerId,
                                                                  @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {

        final String questionContent = answerEditRequest.getContent();
        final AnswerEntity answerEntity = answerService.editAnswerContent(accessToken , answerId ,questionContent);

        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(answerEntity.getUuid()).status("ANSWER EDITED");

        return new ResponseEntity<AnswerEditResponse>(answerEditResponse , HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE , path = "/answer/delete/{answerId}" ,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") final String answerId,
                                                             @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {

        final AnswerEntity answerEntity = answerService.deleteAnswer(accessToken,answerId);

        AnswerDeleteResponse deleteResponse = new AnswerDeleteResponse().id(answerEntity.getUuid()).status("ANSWER DELETED");

        return new ResponseEntity<AnswerDeleteResponse>(deleteResponse , HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET , path = "/answer/all/{questionId}" , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable("questionId") final String questionId ,
                                                                               @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {


        final QuestionEntity questionEntity = userDao.getQuestionByQuestionId(questionId);
        final List<AnswerEntity> allAnswerListToQuestion = answerService.getAllAnswersToQuestion(accessToken ,questionId);

        List<AnswerDetailsResponse> detailsResponses = new ArrayList<AnswerDetailsResponse>();
        for (AnswerEntity object : allAnswerListToQuestion) {

            detailsResponses.add(new AnswerDetailsResponse().id(object.getUuid())
                                                            .questionContent(questionEntity.getContent())
                                                            .answerContent(object.getAnswer()));
        }

        return new ResponseEntity<List<AnswerDetailsResponse>>(detailsResponses , HttpStatus.OK);

    }
}
