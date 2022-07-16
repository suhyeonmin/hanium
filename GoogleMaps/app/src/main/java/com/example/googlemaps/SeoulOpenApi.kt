package com.example.googlemaps

import android.telecom.Call
import com.example.googlemaps.data.Library
import retrofit2.http.GET
import retrofit2.http.Path

class SeoulOpenApi {
    companion object{
        val DOMAIN = "http://openapi.seoul.go.kr:8088/"
        val API_KEY = "45566d55666d696e35324771435574"
    }
}

interface SeoulOpenService{
    @GET("{api_key}/json/SeoulPublicLibraryInfo/1/{end}")
    fun getLibraries(@Path("api_key") key:String, @Path("end") limit:Int) : retrofit2.Call<Library>
}