package com.neck.weatherpick.server.client.kma.dto.response;

public interface KmaApiResponse<R extends KmaInnerResponse<?>> {
    R response();
}