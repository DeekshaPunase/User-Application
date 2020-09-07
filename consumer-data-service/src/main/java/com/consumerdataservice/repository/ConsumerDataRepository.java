package com.consumerdataservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.consumerdataservice.model.UserDetail;

@Repository
public interface ConsumerDataRepository extends JpaRepository<UserDetail , Integer>{

	@Query(value="select user.fileUrl from UserDetail user where user.userId=:userId")
	String findUrlByUserId(@Param("userId") Integer userId);

}
