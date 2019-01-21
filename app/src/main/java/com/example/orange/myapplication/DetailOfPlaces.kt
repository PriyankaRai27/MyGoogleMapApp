package com.example.orange.myapplication

import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.orange.myapplication.Interface.GoogleAPIInterface
import com.example.orange.myapplication.Modal.DetailPlacesModal
import com.example.orange.myapplication.Modal.PlacesModal
import com.example.orange.myapplication.R.id.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.detailplaces.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailOfPlaces : AppCompatActivity() {

    internal lateinit var mService: GoogleAPIInterface

    var placesdetail: DetailPlacesModal? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailplaces)


        mService = Common.googleAPIservices



        places_address.text = ""
        places_name.text = ""
        places_open_hour.text = ""


        if (Common.currentResults!!.photos != null && Common.currentResults!!.photos!!.size > 0) {

            Picasso.with(this)
                    .load(getPhotoPlace(Common.currentResults!!.photos!![0].photo_reference!!, 1000))
                    .into(photo)

        }

        if (Common.currentResults!!.rating != null) {
            ratingBar.rating = Common.currentResults!!.rating.toFloat()
        }



        if (Common.currentResults!!.opening_hours != null) {
            places_open_hour.text = "Open Now  " + Common.currentResults!!.opening_hours!!.open_now
        }
        val url = getPlaceDeatilsUrl(Common.currentResults!!.place_id!!)

        mService.getDetailPlace(url)
                .enqueue(object : retrofit2.Callback<DetailPlacesModal> {


                    override fun onFailure(call: Call<DetailPlacesModal>?, t: Throwable?) {
                        Toast.makeText(applicationContext, "Connection fail", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<DetailPlacesModal>?, response: Response<DetailPlacesModal>?) {


                        if (response!!.isSuccessful) {
                            placesdetail = response.body()


                            places_address.text = (placesdetail!!.result!!.formatted_address)
                            places_name.text = (placesdetail!!.result!!.name)
                        }
                    }

                })


    }


    private fun getPlaceDeatilsUrl(placeid: String): String {

        val url = StringBuilder("https://maps.googleapis.com/maps/api/place/details/json")
        url.append("?placeid=$placeid" + "&key=AIzaSyCBy0_NfE2GxeFc20OxGu5BQzMN8r9B3bY")

        return url.toString()

    }

    private fun getPhotoPlace(photoreference: String, maxmWid: Int): String {

        val urlPhoto = StringBuilder("https://maps.googleapis.com/maps/api/place/photo")
        urlPhoto.append("?maxidth=$maxmWid" + "&photorefrence=$photoreference" + "&key=AIzaSyCBy0_NfE2GxeFc20OxGu5BQzMN8r9B3bY")

        return urlPhoto.toString()


    }
}