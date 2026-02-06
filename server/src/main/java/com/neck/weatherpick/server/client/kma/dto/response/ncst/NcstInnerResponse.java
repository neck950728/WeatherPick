package com.neck.weatherpick.server.client.kma.dto.response.ncst;

import com.neck.weatherpick.server.client.kma.dto.response.KmaInnerResponse;

public record NcstInnerResponse(NcstBody body) implements KmaInnerResponse<NcstBody> {}