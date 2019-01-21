package com.example.orange.myapplication

import com.example.orange.myapplication.Interface.ClientRetrofit
import com.example.orange.myapplication.Interface.GoogleAPIInterface
import com.example.orange.myapplication.Modal.ResultModal

object Common {

    private val GO0GLE_URL="https://maps.googleapis.com/"

    var currentResults: ResultModal? = null


    val googleAPIservices:GoogleAPIInterface

    get()=ClientRetrofit.getClient(GO0GLE_URL).create(GoogleAPIInterface::class.java)



}