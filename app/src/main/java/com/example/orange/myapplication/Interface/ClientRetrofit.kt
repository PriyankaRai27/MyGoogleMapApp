package com.example.orange.myapplication.Interface

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ClientRetrofit
{
    private var retrofit:Retrofit?=null

    fun getClient(baseUrl1:String):Retrofit{
        if(retrofit==null) {
            retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl1)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

        }
        return retrofit!!
    }
}