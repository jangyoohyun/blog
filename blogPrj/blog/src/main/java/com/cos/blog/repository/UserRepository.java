package com.cos.blog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.blog.model.User;

// DAO
// 자동으로 bean등록이 된다.
// @Repository 생략 가능하다.
public interface UserRepository extends JpaRepository<User, Integer> {

	// select * from user where username =1?;
	Optional<User> findByUsername(String username);
	
	// JPA Naming 쿼리 전략
	// findby 뒤에 UsernameAndPassword 를 적으면 
	// select * from user where username = ?1 AND password = ?2
	// 이런 쿼리가 실행된다.

	// 스프링 전통적인 방식의 로그인 Repository
	// User findByUsernameAndPassword(String username, String password);
	
	// @Query(value = "select * from user where username = ?1 AND password = ?2", nativeQuery = true)
	// User login(String username, String password);

}
