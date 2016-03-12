package com.cynosure.cynosure_jwt_server.dao;

import com.cynosure.cynosure_jwt_server.entity.User;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

@Repository("userDao")
public class UserRepositoryImpl  implements UserRepository {//extends AbstractDao<Integer, User1>  implements UserRepository {

        @Autowired
    private SessionFactory sessionFactory;
    
        
        @Override
	public User findByUsername(String username) {
                Session session = sessionFactory.openSession();
                Transaction transaction = session.beginTransaction();

            
                Criteria crit = session.createCriteria(User.class);
		crit.add(Restrictions.eq("username", username));    
		User user = (User) crit.uniqueResult();
                transaction.commit();
                return user;
	}


        
}
