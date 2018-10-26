package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import io.swagger.annotations.ResponseHeader;
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
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @RequestMapping(method = RequestMethod.POST , path = "/question/create" , consumes = MediaType.APPLICATION_JSON_UTF8_VALUE , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest ,
                                                          @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException {


        final UserEntity userEntity = questionService.getUser(accessToken);
        final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setUser(userEntity);
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());

        final QuestionEntity createdQuestion = questionService.createQuestion(questionEntity);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse,HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET , path = "/question/all" , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException {

        List<QuestionEntity> allQuestions = questionService.getAllQuestions(accessToken);

        List<QuestionDetailsResponse> detailsResponses = new ArrayList<QuestionDetailsResponse>();
        for (QuestionEntity object : allQuestions) {
            detailsResponses.add(new QuestionDetailsResponse().id(object.getUuid()).content(object.getContent()));
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(detailsResponses ,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT , path = "/question/edit/{questionId}" ,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(QuestionEditRequest questionEditRequest,
                                                                    @PathVariable("questionId") final String questionId,
                                                                    @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {

        final String questionContent = questionEditRequest.getContent();
        QuestionEntity questionEntity = questionService.editQuestionContent(accessToken , questionId ,questionContent);

        QuestionEditResponse detailsResponse = new QuestionEditResponse().id(questionEntity.getUuid()).status("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(detailsResponse , HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE , path = "/question/delete/{questionId}" , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String questionId,
                                                                 @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity questionEntity = questionService.deleteQuestion(accessToken,questionId);

        QuestionDeleteResponse deleteResponse = new QuestionDeleteResponse().id(questionEntity.getUuid()).status("QUESTION DELETED");

        return new ResponseEntity<QuestionDeleteResponse>(deleteResponse , HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET , path = "/all/{userId}" , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable("userId") final String userId ,
                                                                         @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, UserNotFoundException {

        List<QuestionEntity> allQuestions = questionService.getAllQuestionsByUserId(accessToken, userId);

        List<QuestionDetailsResponse> detailsResponses = new ArrayList<QuestionDetailsResponse>();
        for (QuestionEntity object : allQuestions) {
            detailsResponses.add(new QuestionDetailsResponse().id(object.getUuid()).content(object.getContent()));
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(detailsResponses , HttpStatus.OK);
    }
}
