package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UserDao {

    @PersistenceContext
    EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity) {

        entityManager.persist(userEntity);
        return userEntity;
    }

    public UserEntity getUserByEmail(final String email) {

        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserEntity getUserByUserName(final String username) {

        try {
            return entityManager.createNamedQuery("userByUsername", UserEntity.class).setParameter("userName", username).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserAuthEntity createAuthTokan(final UserAuthEntity userAuthEntity) {
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }

    public UserAuthEntity getUserAuthToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void updateUser(final UserAuthEntity updateUserAuthEntity){
        entityManager.merge(updateUserAuthEntity);
    }

    public UserEntity getUserByUuid(final String userUuid) {

        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", userUuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    // Question ....
    public QuestionEntity createQuestion(final QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestions(final UserEntity user) throws NoResultException {

        return entityManager.createNamedQuery("questionByAccessToken", QuestionEntity.class).setParameter("user", user).getResultList();
    }
}
