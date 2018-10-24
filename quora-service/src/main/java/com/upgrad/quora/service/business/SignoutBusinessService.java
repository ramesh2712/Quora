package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class SignoutBusinessService {

    @Autowired
    UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity signoutUser(final String accessToken) throws SignOutRestrictedException {

        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthEntity == null) {
             throw new SignOutRestrictedException("SGR-001","User is not Signed in");
        }
        final ZonedDateTime now = ZonedDateTime.now();
        userDao.updateUser(userAuthEntity);
        userAuthEntity.setLogoutAt(now);

        return userAuthEntity;
    }
}
