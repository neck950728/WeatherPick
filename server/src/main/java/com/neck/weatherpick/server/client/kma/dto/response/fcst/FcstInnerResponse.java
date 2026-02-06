package com.neck.weatherpick.server.client.kma.dto.response.fcst;

import com.neck.weatherpick.server.client.kma.dto.response.KmaInnerResponse;

public record FcstInnerResponse(FcstBody body) implements KmaInnerResponse<FcstBody> {}