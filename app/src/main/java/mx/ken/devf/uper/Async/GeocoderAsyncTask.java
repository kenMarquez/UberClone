package mx.ken.devf.uper.Async;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import mx.ken.devf.uper.Interfaces.GeocoderCallBack;

/**
 * Created by Ken on 05/03/2015.
 */

public class GeocoderAsyncTask extends AsyncTask<String, Void, String> {

    // JSON Node names
    private static final String TAG_RESULTS = "results";
    private static final String TAG_ADDRESS_COMPONENST = "address_components";
    private static final String TAG_LONG_NAME = "long_name";
    private static final String TAG_SHORT_NAME = "short_name";
    private static final String TAG_TYPES = "types";
    private static final String TAG_FORMATTED_ADDRESS = "formatted_address";
    private static final String TAG_GEOMETRY = "geometry";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LNG = "lng";
    private static final String TAG_STATUS = "status";


    private static final String URL_BASE = "http://maps.googleapis.com/maps/api/geocode";
    private static final String OUTPUT = "/json";
    private GeocoderCallBack geocoderCallBack;

    public GeocoderAsyncTask(GeocoderCallBack geocoderCallBack) {
        this.geocoderCallBack = geocoderCallBack;
    }

    public GeocoderAsyncTask() {
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        StringBuilder jsonResults = new StringBuilder();


        try {
            String address = params[0].replace(" ", "%20");
            StringBuilder sb = new StringBuilder(URL_BASE + OUTPUT);
            sb.append("?sensor=false");
            sb.append("&address=" + address);

            URL url = new URL(sb.toString());

            connection = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(connection.getInputStream());
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }

            Log.i("myLog", "JSON ");
            JSONObject jsonObject = new JSONObject(jsonResults.toString());
            JSONArray results = jsonObject.getJSONArray(TAG_RESULTS);
            JSONObject result = results.getJSONObject(0);
            JSONObject geometry = result.getJSONObject(TAG_GEOMETRY);
            JSONObject location = geometry.getJSONObject(TAG_LOCATION);

            double lat = location.getDouble(TAG_LAT);
            double lon = location.getDouble(TAG_LNG);

            Log.i("myLog", "Lat : " + lat);
            Log.i("myLog", "Lon : " + lon);


            return jsonResults.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s != null) { // respues correcta s es el json a tratar

        }

    }
}
