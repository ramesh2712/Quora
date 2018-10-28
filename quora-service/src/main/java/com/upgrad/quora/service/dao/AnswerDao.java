package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    EntityManager entityManager;

    // Answer ....
    public AnswerEntity createAnswer(final AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }
    public AnswerEntity getAnswerByAnswerId(final String answerID) {
        try {
            return entityManager.createNamedQuery("answerByUuid", AnswerEntity.class).setParameter("uuid", answerID).getSingleResult();

        } catch (NoResultException nre) {
            return null;
        }
    }

    public void editAnswerContent(final AnswerEntity answerEntity) {
        entityManager.merge(answerEntity);
    }

    public void deleteAnswer(final AnswerEntity answerEntity) {
        entityManager.remove(answerEntity);
    }

    public List<AnswerEntity> getAllAnswerByQuestionID(final QuestionEntity questionEntity) throws NoResultException {

        return entityManager.createNamedQuery("getAllAnswerByQuestionID", AnswerEntity.class).setParameter("questionID", questionEntity).getResultList();
    }

}
