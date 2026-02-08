package com.neck.weatherpick.server.client.geo.dto.request;

public record LatLon(String addressName, String placeName, double lat, double lon) {} // lat(위도), lon(경도)