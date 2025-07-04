package com.api.mysushistory.core.gateway;

import com.api.mysushistory.core.domain.ShareToken;
import java.util.Optional;

public interface ShareTokenGateway {

  ShareToken save(final ShareToken shareToken, final Long patientId);

  Optional<ShareToken> findByAccessToken(final String token);
}
