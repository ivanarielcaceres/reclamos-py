package py.com.reclamospy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import model.Reclamo;

/**
 * Created by ivan on 9/8/15.
 */
public class MapView extends ActionBarActivity implements GoogleMap.OnMarkerDragListener,GoogleMap.OnMapLongClickListener,GoogleMap.OnMapClickListener, View.OnClickListener{
    GoogleMap googleMap;
    Toolbar toolbar;
    Reclamo reclamo;
    FloatingActionButton cameraBtn;
    FloatingActionButton sendBtn;
    LocationManager locationManager;
    double lat;
    double lng;
    boolean isFirsChangeListen;
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.map_view);
        isFirsChangeListen = true;
        cameraBtn = (FloatingActionButton)findViewById(R.id.add_camera_icon);
        cameraBtn.setOnClickListener(this);
        sendBtn = (FloatingActionButton)findViewById(R.id.add_send_icon);
        sendBtn.setOnClickListener(this);

        reclamo = (Reclamo) getIntent().getSerializableExtra("reclamo");
        lat = -25.516666700000000000;
        lng = -54.616666699999996000;
        reclamo.setLat(lat + "");
        reclamo.setLng(lng + "");
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        googleMap.setMyLocationEnabled(true);



        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat,lng))
                .draggable(true)
                .title("Ubicacion del relcamo"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 13));

        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        Toast.makeText(getBaseContext(), "Picture! inicio", Toast.LENGTH_LONG).show();

        if (googleMap!= null) {


            googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                @Override
                public void onMyLocationChange(Location arg0) {
                    // TODO Auto-generated method stub
                    if (isFirsChangeListen) {
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                        reclamo.setLat(arg0.getLatitude() + "");
                        reclamo.setLng(arg0.getLongitude() + "");
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getLatitude(), arg0.getLongitude()), 13));

                        Toast.makeText(getBaseContext(), "Lat+Lng: " + reclamo.getLat() + " " + reclamo.getLng(), Toast.LENGTH_LONG).show();
                        isFirsChangeListen = false;
                    }

                }
            });

        }
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng dragPosition = marker.getPosition();
        double dragLat = dragPosition.latitude;
        double dragLong = dragPosition.longitude;
        marker.setTitle("Imei:"+reclamo.getImei()+" Image: "+reclamo.getFoto());
        reclamo.setLat(dragLat+"");
        reclamo.setLng(dragLong+"");
    }

    @Override
    public void onMapClick(LatLng latLng) {

        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).title("It's Me!"));
        reclamo.setLat(latLng.latitude+ "");
        reclamo.setLng(latLng.longitude + "");
        Toast.makeText(getBaseContext(), "Lat+Lng: " + reclamo.getLat() + " " + reclamo.getLng(), Toast.LENGTH_LONG).show();
        isFirsChangeListen = false;
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.add_camera_icon:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent,2);
                break;
            case R.id.add_send_icon:
                new HttpAsyncTask().execute("http://192.168.1.107/");
                break;
        }

    }
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if (requestCode == 2 && resultCode == RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG,100,bos);
            byte[] bArray = bos.toByteArray();

//                reclamo.setFoto(B);
            Toast.makeText(getBaseContext(), "Picture: "+photo.toString(), Toast.LENGTH_LONG).show();


            /*try {
                reclamo.getFoto().setBytes(1,bArray);
            } catch (SQLException e) {
                e.printStackTrace();
            }*/
        }
    }

    public static String POST(String url, Reclamo reclamo){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("RP_Category", reclamo.getCategoria());
            jsonObject.accumulate("fecha", reclamo.getFecha());
            jsonObject.accumulate("latitud", reclamo.getLat());
            jsonObject.accumulate("longitud",reclamo.getLng());
            jsonObject.accumulate("imei",reclamo.getImei());
            jsonObject.accumulate("RP_Group",reclamo.getSubcategoria());
            jsonObject.accumulate("img",reclamo.getFoto());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            System.out.println("Json Object POST :"+json);

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0],reclamo);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }
}
