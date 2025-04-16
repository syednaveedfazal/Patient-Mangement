package com.expense.tracking.repository;

import com.expense.tracking.entities.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserInfo,Long> {
    public UserInfo findByUserName(String userName);
}
