package py.com.reclamospy;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import model.Reclamo;

/**
 * Created by Edwin on 15/02/2015.
 */
public class Tab3 extends Fragment implements View.OnClickListener{
    Toolbar toolbar;
    Reclamo reclamo;
    ImageButton agua;
    ImageButton energia;
    ImageButton viaPublica;
    ImageButton emergencia;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.report_type_selection, container,
                false);

        toolbar = (Toolbar) v.findViewById(R.id.tool_bar);
        reclamo = new Reclamo();
        reclamo.setFecha(new Date());
        //GET IMEI
        TelephonyManager mngr = (TelephonyManager) this.getActivity().getSystemService(getActivity().TELEPHONY_SERVICE);
        reclamo.setImei(mngr.getDeviceId());
        agua = (ImageButton)v.findViewById(R.id.aguaButton);
        agua.setOnClickListener(this);
        energia = (ImageButton)v.findViewById(R.id.energiaButton);
        energia.setOnClickListener(this);
        viaPublica = (ImageButton)v.findViewById(R.id.viapublicaButton);
        viaPublica.setOnClickListener(this);
        return v;
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
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.aguaButton:
                reclamo.setCategoria("AGUA");
                Bundle bundle = new Bundle();
                bundle.putSerializable("reclamo",reclamo);
                Intent intent = new Intent(getActivity(), ReportSubTypeSelection.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.energiaButton:
                System.out.println("ENERGIA PRESSED");
                break;
            case R.id.viapublicaButton:
                System.out.println("VIA PUBLICA PRESSED");
                break;
        }

    }
}