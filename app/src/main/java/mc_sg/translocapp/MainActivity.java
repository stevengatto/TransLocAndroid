package mc_sg.translocapp;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import mc_sg.translocapp.model.Agency;
import mc_sg.translocapp.model.AgencyRouteMap;
import mc_sg.translocapp.model.Response;
import mc_sg.translocapp.network.ApiUtil;

public class MainActivity extends AppCompatActivity  {

    Button btnGet;
    ListView responseList;
    Activity context;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        buildGoogleApiClient();

        btnGet = (Button) findViewById(R.id.btn_get);
        responseList = (ListView) findViewById(R.id.lv_response);

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UNCOMMENT ONE WEB REQUEST TO TEST
//                ApiUtil.getTransLocApi().getAgencies(null, null, new GetAgenciesCallback(context));

                List<Integer> agencies = new ArrayList<Integer>();
                agencies.add(16);
                ApiUtil.getTransLocApi().getRoutes(ApiUtil.formatAgencyList(agencies), null, new GetRoutesCallback(context));
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        GoogleApiConnectionCallbacks callback = new GoogleApiConnectionCallbacks();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(callback)
                .addOnConnectionFailedListener(callback)
                .addApi(LocationServices.API)
                .build();
    }

    private String getGeoArea() {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (lastLocation != null) {
            // get bounding box with this location using the GeoLocation class
        }
        return ApiUtil.formatGeoArea(0.0,0.0,0.0,0.0); // will use real values soon
    }

    private class GoogleApiConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnected(Bundle bundle) {
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
            List<Agency> agencies = listResponse.data;
            List<String> descriptions = new ArrayList<>();
            for(Agency agency : agencies) {
                descriptions.add(agency.agencyId + " - " + agency.shortName);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, descriptions);

            responseList.setAdapter(adapter);
        }
    }

    private class GetRoutesCallback extends ApiUtil.RetroCallback<Response<AgencyRouteMap>> {

        public GetRoutesCallback(Context context) {
            super(context);
        }

        @Override
        public void success(Response<AgencyRouteMap> routesResponse, retrofit.client.Response response) {
            List<String> descriptions = new ArrayList<>();
            for(String key : routesResponse.data.keySet()) {
                for (AgencyRouteMap.Route route : routesResponse.data.get(key)) {
                    descriptions.add(route.agencyId + " - " + route.routeId + " - " + route.longName);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, descriptions);

            responseList.setAdapter(adapter);
        }
    }
}
