package com.api.mysushistory.infra.persistence.repository;

import com.api.mysushistory.infra.persistence.entity.ShareTokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShareTokenRepository extends JpaRepository<ShareTokenEntity, Long> {

  Optional<ShareTokenEntity> findByAccessToken(final String token);
}
