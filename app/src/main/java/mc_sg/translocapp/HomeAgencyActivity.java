package mc_sg.translocapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import mc_sg.translocapp.model.Agency;
import mc_sg.translocapp.model.Response;
import mc_sg.translocapp.network.ApiUtil;
import mc_sg.translocapp.view.MapWrapperLayout;

public class HomeAgencyActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String PREFS_HOME_AGENCY = "home_agency_prefs"; // key for sharedprefs
    public static final String KEY_PREFS_AGENCY_ID = "key_home_agency_id"; // key for id in sharedprefs

    private Activity context;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap map;
    private MapWrapperLayout mapWrapper;
    private ViewGroup infoWindow;
    private TextView infoTitle;
    private Button infoButton;
    private MapWrapperLayout.OnInfoWindowElemTouchListener infoWindowListener;
    private Location lastLocation;
    private List<Agency> agencyList;
    private Integer mapBottomPadding = null;
    private boolean mapInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_agency);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Agencies");
        setSupportActionBar(toolbar);

        context = this;
        buildGoogleApiClient();

        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
        mapWrapper = (MapWrapperLayout) findViewById(R.id.map_wrapper);
        infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.view_map_info_window, null);
        infoTitle = (TextView) infoWindow.findViewById(R.id.info_title);
        infoButton = (Button) infoWindow.findViewById(R.id.info_button);

        // set up map marker listeners
        infoWindowListener = new OnSetHomeClicked(infoButton);
        infoButton.setOnTouchListener(infoWindowListener);

        MapFragment mapFragment = new MapFragment();
        getFragmentManager().beginTransaction().replace(R.id.map_frame, mapFragment).commit();
        mapFragment.getMapAsync(this);

        final View textOverlay = findViewById(R.id.map_text_overlay);
        textOverlay.post(new Runnable() {
            @Override
            public void run() {
                mapBottomPadding = textOverlay.getHeight();
                if (map != null) {
                    map.setPadding(0, 0, 0, mapBottomPadding);
                }
            }
        });

        findViewById(R.id.map_get_agencies).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAgencies();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        GoogleApiConnectionCallbacks callback = new GoogleApiConnectionCallbacks();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(callback)
                .addOnConnectionFailedListener(callback)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (mapBottomPadding != null) {
            map.setPadding(0, 0, 0, mapBottomPadding);
        }

        initMapPosition();

        mapWrapper.init(map, getPixelsFromDp(this, 39 + 20));
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                infoTitle.setText(marker.getTitle());

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapper.setMarkerWithInfoWindow(marker, infoWindow);
                infoWindowListener.setMarker(marker);

                return infoWindow;
            }
        });
    }

    private void initMapPosition() {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null && map != null && !mapInitialized) {
            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            mapInitialized = true;
            getAgencies();
        }
    }

    public void getAgencies() {
        String geoArea = null;
        if (map != null) {
            geoArea = ApiUtil.getGeoArea(map.getProjection().getVisibleRegion());
        }
        ApiUtil.getTransLocApi().getAgencies(null, geoArea, new GetAgenciesCallback(context));
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    private class GoogleApiConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnected(Bundle bundle) {
            initMapPosition();
        }

        @Override
        public void onConnectionSuspended(int i) {
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            int error = connectionResult.getErrorCode();
            if (error == ConnectionResult.SERVICE_MISSING
                    || error == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED
                    || error == ConnectionResult.SERVICE_DISABLED) {
                GooglePlayServicesUtil.getErrorDialog(error, context, 1).show();
            }
        }
    }

    private class OnSetHomeClicked extends MapWrapperLayout.OnInfoWindowElemTouchListener {

        public OnSetHomeClicked(View view) {
            super(view);
        }

        @Override
        protected void onClickConfirmed(View v, Marker marker) {
            if (v.getId() == infoButton.getId()) {
                String agencyName = marker.getTitle();
                Agency foundAgency = null;
                // linear search, inefficient I know
                for (Agency agency : agencyList) {
                    if (agency.shortName.equals(agencyName)) {
                        foundAgency = agency;
                        break;
                    }
                }

                final Agency homeAgency = foundAgency; // hack to get final variable in inner class
                DialogInterface.OnClickListener dialogClickListener = null;
                if (homeAgency != null) {
                    // show a dialog just to be sure
                    dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    // update the users preferences when they set a home agency
                                    SharedPreferences prefs = context.getSharedPreferences(PREFS_HOME_AGENCY, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString(KEY_PREFS_AGENCY_ID, homeAgency.agencyId);
                                    editor.apply();
                                    startActivity(new Intent(context, RoutesActivity.class));
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    // Do nothing
                                    break;
                            }
                        }
                    };
                }

                String message = "Are you sure you want to set " + marker.getTitle()
                        + " as your home agency?";
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(message)
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show();
            }
        }
    }

    private class GetAgenciesCallback extends ApiUtil.RetroCallback<Response<List<Agency>>> {

        public GetAgenciesCallback(Context context) {
            super(context);
        }

        @Override
        public void success(Response<List<Agency>> listResponse, retrofit.client.Response response) {
            if (map != null) {
                map.clear();
                agencyList = listResponse.data;
                if (!agencyList.isEmpty()) {
                    for (Agency agency : agencyList) {
                        map.addMarker(new MarkerOptions()
                            .title(agency.shortName)
                            .position(new LatLng(agency.position.lat, agency.position.lng))
                        );
                    }
                } else {
                    Toast.makeText(context, "No agencies found in this region.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
