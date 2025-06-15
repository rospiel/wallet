package com.wallet.repository;

import com.wallet.entity.SystemParameterEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemParameterRepository extends JpaRepository<SystemParameterEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SystemParameterEntity> findByName(String name);
}
