package com.expense.tracking.repository;

import com.expense.tracking.entities.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
     Optional<RefreshToken> findByToken(String token);
}
