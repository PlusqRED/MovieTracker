package com.so.movietrackerservice.repository;

import com.so.movietrackerservice.domain.db.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotUserRepository extends JpaRepository<BotUser, Long> {

}
