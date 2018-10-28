package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    EntityManager entityManager;

    // Question ....
    public QuestionEntity createQuestion(final QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestions(final UserEntity user) throws NoResultException {

        return entityManager.createNamedQuery("questionByAccessToken", QuestionEntity.class).setParameter("user", user).getResultList();
    }

    public QuestionEntity getQuestionByQuestionId(final String questionId) {
        try {
            return entityManager.createNamedQuery("questionByUuid", QuestionEntity.class).setParameter("uuid", questionId).getSingleResult();

        } catch (NoResultException nre) {
            return null;
        }
    }

    public void editQuestionContent(final QuestionEntity questionEntity) {
        entityManager.merge(questionEntity);
    }

    public void deleteQuestion(final QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
    }

    public UserEntity getUserByQuestionUserId(final Integer userID) {

        try {
            return entityManager.createNamedQuery("userByUserId", UserEntity.class).setParameter("id", userID).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
