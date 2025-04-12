package com.team.score.API.weather.response

import com.naver.maps.geometry.LatLng

data class AirPollutionResponse(
    val coord: LatLng,
    val list: List<AirQualityData>
)

data class AirQualityData(
    val dt: Long,
    val main: AirQualityIndex
)

data class AirQualityIndex(
    val aqi: Int  // Possible values: 1, 2, 3, 4, 5. Where 1 = Good, 2 = Fair, 3 = Moderate, 4 = Poor, 5 = Very Poor.
)
