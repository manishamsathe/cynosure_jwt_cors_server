package com.cynosure.cynosure_jwt_server.dao;

import com.cynosure.cynosure_jwt_server.entity.User;

public interface UserRepository {

	User findByUsername(String username);
	
}

