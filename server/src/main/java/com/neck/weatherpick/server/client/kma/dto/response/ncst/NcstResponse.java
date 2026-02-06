package com.neck.weatherpick.server.client.kma.dto.response.ncst;

import com.neck.weatherpick.server.client.kma.dto.response.KmaApiResponse;

public record NcstResponse(NcstInnerResponse response) implements KmaApiResponse<NcstInnerResponse> {}