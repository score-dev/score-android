package com.team.score.API.map.response

data class ReverseGeocodingResponse(
    val results: List<ReverseGeocodeResult>
)

data class ReverseGeocodeResult(
    val region: Region
)

data class Region(
    val area1: Area,
    val area2: Area,
    val area3: Area
)

data class Area(
    val name: String
)
