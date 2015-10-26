package ca.uqac.histositesmaps;

import android.location.Address;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import android.view.inputmethod.EditorInfo;
import android.location.Geocoder;
import android.widget.EditText;
import android.widget.TextView;
import android.view.KeyEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Geocoder gc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gc = new Geocoder(this);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location=null;
        try {
            location = locationManager.getLastKnownLocation(provider);
        } catch (SecurityException se){

        }

        // On positionne sur Chicoutimi si impossible d'obtenir l'emplacement actuel
        if(location!=null){
            setLocationWithPos(new LatLng(location.getLatitude(), location.getLongitude()), "Ici");
        } else {
            setLocationWithPos(new LatLng(48.368, -71.07), "Chicoutimi");
        }
        // Init edittext composant
        setInputText();
    }

    /**
     * Permet de positionner sur la carte à partir d'un nom d'endroit
     * @param newLocation
     */
    private void setNewLocation(String newLocation) {
        if (Geocoder.isPresent()) {
            try {
                String location;
                if (newLocation == null)
                    location = ((EditText)findViewById(R.id.editText)).getText().toString();
                else
                    location = newLocation;

                List<Address> addresses = gc.getFromLocationName(location, 1); // get the found Address Objects

                for (Address a : addresses) {
                    if (a.hasLatitude() && a.hasLongitude()) {
                        LatLng newPos = new LatLng(a.getLatitude(), a.getLongitude());
                        setLocationWithPos(newPos, location);
                    }
                }
            } catch (IOException e) {
                // handle the exception
            }
        }
    }

    /**
     * Ajoute un marker à la position <code>newPos</code>, identifié par la titre (param2)
     * Déplace la "caméra" à cet endroit et efface le composant d'entrée de texte
     * @param newPos
     * @param title
     */
    private void setLocationWithPos(LatLng newPos, String title){
        mMap.addMarker(new com.google.android.gms.maps.model.MarkerOptions().position(newPos).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newPos));
        ((EditText)findViewById(R.id.editText)).setText("");
    }

    /**
     * Défini le listener sur le edittext afin que celui-ci modifie l'emplacement sur la carte après
     * entrée d'un nouvel emplacement
     */
    private void setInputText(){
        ((EditText)findViewById(R.id.editText)).setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (!event.isShiftPressed()) {
                                // the user is done typing.
                                setNewLocation(null);
                                return true; // consume.
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                });
    }
}