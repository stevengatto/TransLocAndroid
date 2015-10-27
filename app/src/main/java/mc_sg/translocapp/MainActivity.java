package mc_sg.translocapp;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Activity context;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap map;
    private MapWrapperLayout mapWrapper;
    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private Button infoButton;
    private View.OnTouchListener infoWindowListener;
    private boolean mapInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        buildGoogleApiClient();

        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
        mapWrapper = (MapWrapperLayout) findViewById(R.id.map_wrapper);
        infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.view_map_info_window, null);
        infoTitle = (TextView) infoWindow.findViewById(R.id.title);
        infoSnippet = (TextView) infoWindow.findViewById(R.id.snippet);
        infoButton = (Button) infoWindow.findViewById(R.id.button);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(null, "marker button clicked");
            }
        });

        Button btnGet = (Button) findViewById(R.id.btn_get);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String geoArea = null;
                if (map != null) {
                    geoArea = ApiUtil.getGeoArea(map.getProjection().getVisibleRegion());
                }
                ApiUtil.getTransLocApi().getAgencies(null, geoArea, new GetAgenciesCallback(context));
            }
        });

        MapFragment mapFragment = new MapFragment();
        getFragmentManager().beginTransaction().replace(R.id.map_frame, mapFragment).commit();
        mapFragment.getMapAsync(this);
    }

    protected synchronized void buildGoogleApiClient() {
        GoogleApiConnectionCallbacks callback = new GoogleApiConnectionCallbacks();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(callback)
                .addOnConnectionFailedListener(callback)
                .addApi(LocationServices.API)
                .build();
    }

    private Location getLastLocation() {
        Location location = null;
        if (mGoogleApiClient.isConnected()) {
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        return location;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
                infoSnippet.setText(marker.getSnippet());

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapper.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });
    }

    private void initMapPosition() {
        Location lastLocation = getLastLocation();
        if (lastLocation != null && map != null && !mapInitialized) {
            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            mapInitialized = true;
        }
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

    private class GetAgenciesCallback extends ApiUtil.RetroCallback<Response<List<Agency>>> {

        public GetAgenciesCallback(Context context) {
            super(context);
        }

        @Override
        public void success(Response<List<Agency>> listResponse, retrofit.client.Response response) {
            if (map != null) {
                map.clear();
                List<Agency> agencies = listResponse.data;
                if (!agencies.isEmpty()) {
                    for (Agency agency : agencies) {
                        map.addMarker(new MarkerOptions()
                                        .snippet(agency.shortName)
                                        .title(agency.longName)
                                        .position(new LatLng(agency.position.lat, agency.position.lng))
                        );
                    }
                } else {
                    Toast.makeText(context, "No agencies found in this region.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

//    private class GetRoutesCallback extends ApiUtil.RetroCallback<Response<AgencyRouteMap>> {
//
//        public GetRoutesCallback(Context context) {
//            super(context);
//        }
//
//        @Override
//        public void success(Response<AgencyRouteMap> routesResponse, retrofit.client.Response response) {
//            List<String> descriptions = new ArrayList<>();
//            for(String key : routesResponse.data.keySet()) {
//                for (AgencyRouteMap.Route route : routesResponse.data.get(key)) {
//                    descriptions.add(route.agencyId + " - " + route.routeId + " - " + route.longName);
//                }
//            }
//
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
//                    android.R.layout.simple_list_item_1, descriptions);
//
//            responseList.setAdapter(adapter);
//        }
//    }
}
