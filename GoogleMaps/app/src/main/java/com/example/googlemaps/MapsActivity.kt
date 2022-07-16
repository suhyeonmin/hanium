package com.example.googlemaps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.googlemaps.data.Library

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.googlemaps.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.LatLngBounds
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        loadLibraries()
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        //마커
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        //카메라 위치
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
    fun loadLibraries(){
        val retrofit = Retrofit.Builder()
            .baseUrl(SeoulOpenApi.DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(SeoulOpenService::class.java)

        service.getLibraries(SeoulOpenApi.API_KEY, 200)
            .enqueue(object : Callback<Library>{
                override fun onFailure(call: Call<Library>, t: Throwable) {
                    Toast.makeText(this@MapsActivity, "데이터를 가져올 수 없습니다", Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<Library>, response: Response<Library>) {
                    val result = response.body()
                    showLibraries(result)
                }
            })
    }

    fun showLibraries(result:Library?){
        val latlngbounds = LatLngBounds.Builder()
        result?.let {
            for(library in it.SeoulPublicLibraryInfo.row) {

                val position = LatLng(library.XCNTS.toDouble(), library.YDNTS.toDouble())
                val marker = MarkerOptions().position(position).title(library.LBRRY_NAME)
                mMap.addMarker(marker)

                latlngbounds.include(position)
            }

            val bounds = latlngbounds.build()
            val padding = 0

            val camera = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            mMap.moveCamera(camera)
        }
    }
}