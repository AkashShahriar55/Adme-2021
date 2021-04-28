package com.cookietech.namibia.adme.utils

import android.graphics.Color
import android.location.Location
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.facebook.FacebookSdk
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

object GoogleMapUtils {
    private lateinit var dest: LatLng
    private lateinit var origin: LatLng
    val API_KEY = "AIzaSyDyl7C2Zp73Vcebld9tPgUuM6HvZ9cE3rc"

     fun getDistanceInMiles(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val loc1 = Location("")
        loc1.latitude = lat1
        loc1.longitude = lng1
        val loc2 = Location("")
        loc2.latitude = lat2
        loc2.longitude = lng2
        val distanceInMeters = loc1.distanceTo(loc2)
        return distanceInMeters / 1609.34
    }

    var mMap:GoogleMap? = null
    fun populateRoute(origin: LatLng,dest: LatLng,map:GoogleMap?){
        mMap = map
        downloadJson(getDirectionsUrl(origin,dest))
    }

    fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {


        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude

        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude

        // Sensor enabled
        val sensor = "sensor=false"
        val mode = "mode=driving"

        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$sensor&$mode"

        // Output format
        val output = "json"

        // Building the url to the web service
        val url = "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=$API_KEY"
        this.origin = origin
        this.dest = dest
        return url
    }

    fun downloadJson(Url: String) {
        val downloadTask = DownloadTask()
        downloadTask.execute(Url)
    }

    class DownloadTask : AsyncTask<String?, Void?, String>() {
        override fun doInBackground(vararg params: String?): String {
            var data = ""
            try {
                data = downloadUrl(params[0])
            } catch (e: Exception) {
                Log.d("Background Task", e.toString())
            }
            return data
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            val parserTask: ParserTask =
                ParserTask()
            parserTask.execute(result)
            Log.i(
                "route_debug",
                "onPostExecute: $result"
            )
        }
    }


    @Throws(IOException::class)
    fun downloadUrl(strUrl: String?): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpsURLConnection? = null
        try {
            val url = URL(strUrl)
            urlConnection = url.openConnection() as HttpsURLConnection
            urlConnection.connect()
            iStream = urlConnection.inputStream
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = ""
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            data = sb.toString()
            br.close()
        } catch (e: java.lang.Exception) {
            Log.d("Exception", e.toString())
        } finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data
    }

    class ParserTask :
        AsyncTask<String?, Int?, List<List<HashMap<String, String>>>?>() {
        // Parsing the data in non-ui thread
        protected override fun doInBackground(vararg params: String?): List<List<HashMap<String, String>>>? {
            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null
            try {
                jObject = JSONObject(params[0])
                routes = parse(jObject)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return routes
        }

        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
            var points: ArrayList<*>? = null
            var lineOptions: PolylineOptions? = null
            val markerOptions = MarkerOptions()
            try {
                for (i in result!!.indices) {
                    points = ArrayList<LatLng>()
                    lineOptions = PolylineOptions()
                    val path = result[i]
                    for (j in path.indices) {
                        val point = path[j]
                        val lat = point["lat"]!!.toDouble()
                        val lng = point["lng"]!!.toDouble()
                        val position = LatLng(lat, lng)
                        points.add(position)
                    }
                    lineOptions.add(origin)
                    lineOptions.addAll(points)
                    lineOptions.add(dest)
                    lineOptions.width(10f)
                    lineOptions.color(Color.parseColor("#3f5aa6"))
                    lineOptions.geodesic(true)
                }

                // Drawing polyline in the Google Map for the i-th route
                mMap?.addPolyline(lineOptions)
            } catch (e: java.lang.Exception) {
                Log.d(
                    "route_debug",
                    "downloadJson: cannot download Map route. May not connect to internet. $e"
                )
                Toast.makeText(
                    FacebookSdk.getApplicationContext(),
                    e.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    /** Receives a JSONObject and returns a list of lists containing latitude and longitude  */
    fun parse(jObject: JSONObject): List<List<HashMap<String, String>>> {
        val routes: MutableList<List<HashMap<String, String>>> = ArrayList()
        var jRoutes: JSONArray? = null
        var jLegs: JSONArray? = null
        var jSteps: JSONArray? = null
        try {
            jRoutes = jObject.getJSONArray("routes")
            /** Traversing all routes  */
            for (i in 0 until jRoutes.length()) {
                jLegs = (jRoutes[i] as JSONObject).getJSONArray("legs")
                val path = ArrayList<HashMap<String, String>>()
                /** Traversing all legs  */
                for (j in 0 until jLegs.length()) {
                    jSteps = (jLegs[j] as JSONObject).getJSONArray("steps")
                    /** Traversing all steps  */
                    for (k in 0 until jSteps.length()) {
                        var polyline = ""
                        polyline =
                            ((jSteps[k] as JSONObject)["polyline"] as JSONObject)["points"] as String
                        val list: List<*> = PolyUtil.decode(polyline)
                        /** Traversing all points  */
                        for (l in list.indices) {
                            val hm = HashMap<String, String>()
                            hm["lat"] = java.lang.Double.toString((list[l] as LatLng).latitude)
                            hm["lng"] = java.lang.Double.toString((list[l] as LatLng).longitude)
                            path.add(hm)
                        }
                    }
                    routes.add(path)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: java.lang.Exception) {
        }
        return routes
    }

}