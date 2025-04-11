package com.team.score.Utils

import android.location.Location
import android.location.LocationManager
import com.naver.maps.geometry.LatLng

object DistanceUtil {

    fun onLocationUpdate(location: Location) {
        val newPoint = LatLng(location.latitude, location.longitude)

        var lastLatLng = MyApplication.locationList.lastOrNull()

        // 5미터 이상 움직였을 때만 저장
        lastLatLng?.let { last ->
            val distance = getDistance(last.latitude, last.longitude, newPoint.latitude, newPoint.longitude) // 단위: m
            if (distance > 1) {
                MyApplication.locationList.add(newPoint)
                lastLatLng = newPoint
                MyApplication.totalDistance += distance.toInt()
            }
        } ?: run {
            // 첫 번째 위치는 무조건 저장
            MyApplication.locationList.add(newPoint)
            lastLatLng = newPoint
        }
    }

    /*
    fun onLocationUpdate(location: Location) {
        val newPoint = LatLng(location.latitude, location.longitude)
        locationList.add(newPoint)

        // 거리 계산
        if (locationList.size >= 2) {
            val last = locationList[locationList.size - 2]
            val distance = getDistance(last.latitude, last.longitude, newPoint.latitude, newPoint.longitude) // 단위: m
            MyApplication.totalDistance += distance.toInt()
        }
    }
     */

    fun getDistance( lat1: Double, lng1:Double, lat2:Double, lng2:Double) : Float{

        val myLoc = Location(LocationManager.NETWORK_PROVIDER)
        val targetLoc = Location(LocationManager.NETWORK_PROVIDER)
        myLoc.latitude= lat1
        myLoc.longitude = lng1

        targetLoc.latitude= lat2
        targetLoc.longitude = lng2

        return myLoc.distanceTo(targetLoc)
    }

    fun calculateKcal(weight: Int, hour: Double) : Int {
        return (1.05 * 6 * weight * hour).toInt()
    }
}