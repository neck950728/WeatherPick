package com.neck.weatherpick.server.client.geo.dto.request;

public record LonLat(String addressName, String placeName, double lon, double lat) {} // lon(경도), lat(위도)