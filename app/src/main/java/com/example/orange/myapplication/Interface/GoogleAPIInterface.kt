package com.example.orange.myapplication.Interface

import com.example.orange.myapplication.Modal.PlacesModal
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface GoogleAPIInterface

{ @GET
     fun getNearByPlaces(@Url url: String): Call<PlacesModal>
}