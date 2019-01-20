package com.example.orange.myapplication

import com.example.orange.myapplication.Interface.ClientRetrofit
import com.example.orange.myapplication.Interface.GoogleAPIInterface

object Common {

    private val GO0GLE_URL="https://maps.googleapis.com/"

    val googleAPIservices:GoogleAPIInterface
    get()=ClientRetrofit.getClient(GO0GLE_URL).create(GoogleAPIInterface::class.java)

}