package com.api.mysushistory.presenter;

import com.api.mysushistory.core.domain.ShareToken;
import com.api.mysushistory.presenter.response.ShareTokenPresenterResponse;
import org.springframework.stereotype.Component;

@Component
public class ShareTokenPresenter {

  public ShareTokenPresenterResponse parseToResponse(final ShareToken shareToken) {
    return ShareTokenPresenterResponse.builder()
        .id(shareToken.getId())
        .accessToken(shareToken.getAccessToken())
        .expiresInMinutes(shareToken.getExpiresInMinutes())
        .createdAt(shareToken.getCreatedAt())
        .patientId(shareToken.getPatientId())
        .build();
  }
}
