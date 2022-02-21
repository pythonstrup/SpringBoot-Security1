package com.cos.security1.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.security1.model.User;

// CRUD 함수를  JpaRepository가 들고 있다.
// @Repository라는 어노테이션이 없어도 IoC 된다.
public interface UserRepository extends JpaRepository<User, Integer> {

	// findBy 규칙 -> Username 문법
	// select * from user where username = 1?
	public User findByUsername(String username); // Jpa Query methods 검색해서 공부하기

}
