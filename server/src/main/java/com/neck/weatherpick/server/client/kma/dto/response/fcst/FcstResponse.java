package com.neck.weatherpick.server.client.kma.dto.response.fcst;

import com.neck.weatherpick.server.client.kma.dto.response.KmaApiResponse;

public record FcstResponse(FcstInnerResponse response) implements KmaApiResponse<FcstInnerResponse> {}