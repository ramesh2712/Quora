package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.business.QuestionService;
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
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    AnswerService answerService;
    @Autowired
    QuestionService questionService;
    @Autowired
    CommonBusinessService commonBusinessService;

    @RequestMapping(method = RequestMethod.POST , path = "/question/{questionId}answer/create" , consumes = MediaType.APPLICATION_JSON_UTF8_VALUE , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest, @PathVariable("questionId") final String questionId,
                                                       @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {


        final UserEntity userEntity = commonBusinessService.getUser(accessToken);
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

}
