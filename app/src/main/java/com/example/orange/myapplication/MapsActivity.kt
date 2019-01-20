package com.example.orange.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.orange.myapplication.Interface.GoogleAPIInterface
import com.example.orange.myapplication.Modal.PlacesModal
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@SuppressLint("ByteOrderMark")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var latitude:Double=0.toDouble()
    private var longitude:Double=0.toDouble()
    private lateinit var lastLocation:Location
    private var mMarker: Marker?=null

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    internal lateinit var currentPlace: PlacesModal
   // val markerOptions:MarkerOptions?=null



    companion object {
        private const val RequestCode:Int = 1000
    }
lateinit var mService: GoogleAPIInterface

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_maps)
        val actionBar = supportActionBar
        actionBar!!.title = "Kotlin"
        actionBar.subtitle = "Search Restaurant ->"

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        mService=Common.googleAPIservices
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
          if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
              if(checkPermissionLoc()){

                  mLocationRequest()
                  mLocationCallBack()
                //  getNearByPlaces("restaurant")

                  fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);
                  fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());


              }
          }
  else{
              mLocationRequest()
              mLocationCallBack()
              fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);
              fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());

          }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menurest, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.actionRest -> {


                nearByPlaces("restaurant")



            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun mLocationCallBack() {


       locationCallback=object :LocationCallback(){
           override fun onLocationResult(p0: LocationResult?) {
              // super.onLocationResult(p0)

               lastLocation=p0!!.locations.get(p0!!.locations.size-1)//getlast location

               if(mMarker!=null){

                       mMarker!!.remove()

               }

                   latitude=lastLocation.latitude
                   longitude=lastLocation.longitude

               val latLng=LatLng(latitude,longitude)
               val markerOptions=MarkerOptions()
                       .position(latLng)
                       .title("position")
                       .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))

               mMarker=mMap!!.addMarker(markerOptions)

               mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
               mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11f))



           }
       }
    }

    private fun mLocationRequest() {

        locationRequest= LocationRequest()
        locationRequest.priority= LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval=6000
        locationRequest.fastestInterval=3000
        locationRequest.smallestDisplacement=10f

    }

    private fun checkPermissionLoc():Boolean {

        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        if(permission != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION))
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    RequestCode)
            else
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        RequestCode)

            return false
        }
    return true

    }

    override fun onStop() {

        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
       // super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){

            RequestCode -> {
                if(grantResults.size>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    val permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    if (permission1 == PackageManager.PERMISSION_GRANTED) {

                        if (checkPermissionLoc()) {
                            mLocationRequest()
                            mLocationCallBack()

                            fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());

                            mMap.isMyLocationEnabled = true
                        }

                    }
                   /* else{
                        mMap.isMyLocationEnabled = true
                    }*/
                }
                else{
                    Log.i("","Access Denied")
                }
            }

        }


    }

    private fun nearByPlaces(typePlace :String){
     /*   mMap.clear()
        mMap = googleMap*/
        mMap.clear()
        val url=getURL(latitude,longitude,typePlace)
        mService.getNearByPlaces(url)
                .enqueue(object : Callback<PlacesModal> {
            override fun onFailure(call: Call<PlacesModal>?, t: Throwable?) {
                //
            }

            override fun onResponse(call: Call<PlacesModal>?, response: Response<PlacesModal>?) {



              currentPlace=response!!.body()
                if(response!!.isSuccessful){
                    var latLng:LatLng?=null
                    for(i in 0 until response!!.body()!!.results!!.size){
                        val markerOptions=MarkerOptions()
                        val googlePlace=response.body()!!.results!![i]
                        val mlat=googlePlace.geometry!!.location!!.lat
                        val mlong=googlePlace.geometry!!.location!!.lng
                        val placeName=googlePlace.name

                         latLng =LatLng(mlat,mlong)


                        markerOptions.position(latLng)
                        markerOptions.title(placeName)

                        if(typePlace.equals("restaurant")){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_resto))
                        }
                       markerOptions.snippet(i.toString())

                        mMap!!.addMarker(markerOptions)

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))
                    }
                }
            }
        })

    }
private fun getURL(latitude :Double,longitude:Double,typePlace: String):String{

val googlePlaceUrl= StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
googlePlaceUrl.append("?location=$latitude ,$longitude"+"&radius=1000"+"&type=$typePlace"+"key=AIzaSyCBy0_NfE2GxeFc20OxGu5BQzMN8r9B3bY")

    return googlePlaceUrl.toString()

}
    @SuppressLint("MissingPermission", "ByteOrderMark")



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            val permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            if (permission1 == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true


            }

        }

        else{
            mMap.isMyLocationEnabled = true
        }
       // val sydney = LatLng(-34.0, 151.0)
       // mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap.uiSettings.isZoomControlsEnabled=true
    }


}
