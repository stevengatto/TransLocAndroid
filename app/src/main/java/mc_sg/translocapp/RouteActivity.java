package mc_sg.translocapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mc_sg.translocapp.model.AgencyRouteMap;
import mc_sg.translocapp.model.AgencyVehicleMap;
import mc_sg.translocapp.model.ArrivalEstimate;
import mc_sg.translocapp.model.Response;
import mc_sg.translocapp.network.ApiUtil;
import mc_sg.translocapp.view.ArrivalEstimateView;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String KEY_ROUTE = "KEY_ROUTE";
    public static final String KEY_ROUTE_SEGMENTS = "KEY_ROUTE_SEGMENTS";
    public static final String KEY_COLOR = "key_color";

    private Context context;
    private AgencyRouteMap.Route route;
    private int color;
    private String agencyId;
    private HashMap<String,String> segments;
    private GoogleMap map;
    private FloatingActionButton favorite;
    private CardView cardView;
    private ListView stopList;

    private ArrayList<Marker> markers = new ArrayList<>();
    private List<LatLng> polyPoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_route);

        route = (AgencyRouteMap.Route) getIntent().getExtras().getSerializable(KEY_ROUTE);
        segments = (HashMap<String,String>) getIntent().getExtras().getSerializable(KEY_ROUTE_SEGMENTS);
        color = getIntent().getExtras().getInt(KEY_COLOR);
        agencyId = Integer.valueOf(route.agencyId).toString();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle((route.shortName == null
                || route.shortName.isEmpty() ? route.longName : route.shortName));
        setSupportActionBar(toolbar);

        ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.single_route_map_fragment)).getMapAsync(this);

        cardView = (CardView) findViewById(R.id.single_route_card);
        cardView.setCardBackgroundColor(color);

        favorite = (FloatingActionButton) findViewById(R.id.fab);
        favorite.setOnClickListener(new FavoriteClickListener());
        favorite.setSelected(route.following);

        stopList = (ListView) findViewById(R.id.single_route_info_list);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        // setup
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setIndoorLevelPickerEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setRotateGesturesEnabled(true);
        map.getUiSettings().setScrollGesturesEnabled(true);
        map.getUiSettings().setTiltGesturesEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(true);

        final GoogleMap map = googleMap;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float polylineWidth = 6 * (metrics.densityDpi / 160f); // 5dp

        if (segments != null) {
            Set<String> segmentKeys = segments.keySet();
            List<LatLng> points;
            final LatLngBounds.Builder builder = new LatLngBounds.Builder();
            boolean pointIncluded = false;
            for (String key : segmentKeys) {
                points = PolyUtil.decode(segments.get(key));
                map.addPolyline(new PolylineOptions()
                        .addAll(points)
                        .color(color)
                        .width(polylineWidth));

                // determine bounds for map zoom and center
                for (LatLng point : points) {
                    polyPoints.add(point);
                    pointIncluded = true; // flag so that the Builder doesn't crash when empty
                    builder.include(point);
                }
            }

            if (pointIncluded) {
                map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition arg0) {
                        // Move camera.
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 50);
                        map.moveCamera(cameraUpdate);
                        // Remove listener to prevent position reset on camera move.
                        map.setOnCameraChangeListener(null);

                        // Get Vehicle locations
                        ApiUtil.getTransLocApi().getVehicles(agencyId, null, route.routeId,
                                new VehiclesCallback(context));

                        // Get arrival estimates
                        ApiUtil.getTransLocApi().getArrivalEstimates(agencyId, route.routeId,
                                ApiUtil.formatCsv(route.stops), new ArrivalCallback(context));
                    }
                });
            }
        }
    }

    private class VehiclesCallback extends ApiUtil.RetroCallback<Response<AgencyVehicleMap>> {

        public VehiclesCallback(Context context) {
            super(context);
        }

        @Override
        public void success(Response<AgencyVehicleMap> vehicleResponse, retrofit.client.Response response) {
            // clear markers
            for (Marker marker : markers) {
                marker.remove();
            }
            markers = new ArrayList<>();

            ArrayList<LatLng> vehiclePoints = new ArrayList<>(); // needed for bounds
            List<AgencyVehicleMap.Vehicle> vehicles = vehicleResponse.data.getVehicles(agencyId);
            if (vehicles != null) {
                for (AgencyVehicleMap.Vehicle vehicle : vehicles) {
                    if (vehicle.routeId.equals(route.routeId)) { // always should but the API has surprised me before
//                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.bus_marker);
                        LatLng latLng = new LatLng(Double.parseDouble(vehicle.location.lat),
                                Double.parseDouble(vehicle.location.lng));
                        vehiclePoints.add(latLng);

                        Marker marker = map.addMarker(new MarkerOptions().position(latLng));
                        markers.add(marker);
                    }
                }
            }

            boolean someOffScreen = false;
            boolean allOffScreen = true;
            LatLngBounds visibleBounds = map.getProjection().getVisibleRegion().latLngBounds;
            for (LatLng point : vehiclePoints) {
                if (!visibleBounds.contains(point)) {
                    someOffScreen = true;
                } else {
                    allOffScreen = false;
                }
            }

            if (allOffScreen) {
                Toast.makeText(context, "All buses are located off-screen", Toast.LENGTH_SHORT).show();
            } else if (someOffScreen) {
                Toast.makeText(context, "Some buses are located off-screen", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class ArrivalCallback extends ApiUtil.RetroCallback<Response<List<ArrivalEstimate>>> {

        public ArrivalCallback(Context context) {
            super(context);
        }

        @Override
        public void success(Response<List<ArrivalEstimate>> listResponse, retrofit.client.Response response) {
            stopList.setAdapter(new ArrivalAdapter(context, listResponse.data));
        }
    }

    private class ArrivalAdapter extends BaseAdapter {

        private final List<ArrivalEstimate> arrivals;
        private final Context context;

        public ArrivalAdapter(Context context, List<ArrivalEstimate> arrivals) {
            this.arrivals = arrivals;
            this.context = context;
        }

        protected List<ArrivalEstimate> getArrivals() {
            return arrivals;
        }

        @Override
        public int getCount() {
            return getArrivals().size();
        }

        @Override
        public Object getItem(int position) {
            return getArrivals().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView =  new ArrivalEstimateView(context);
            }

            ArrivalEstimate estimate = arrivals.get(position);
            ArrivalEstimateView arrival = (ArrivalEstimateView) convertView;

            arrival.setName("Stop name");
            arrival.setRealTime(estimate.arrivals.get(0).toString()); // possible NPE
            arrival.setRealTime(estimate.arrivals.get(0).toString()); // possible NPE

            return arrival;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEmpty() {
            return getArrivals().isEmpty();
        }
    }

    private class FavoriteClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            SharedPreferences prefs = context.getSharedPreferences(RoutesActivity.PREFS_FAVORITES, Context.MODE_PRIVATE);
            Set<String> routes = prefs.getStringSet(RoutesActivity.KEY_PREFS_FAV_ROUTES, new HashSet<String>());

            if (!routes.isEmpty() && routes.contains(route.routeId)) {
                routes.remove(route.routeId);
                Toast.makeText(context, "Route has been removed!", Toast.LENGTH_SHORT).show();
                route.following = false;
                view.setSelected(false);

            } else {
                routes.add(route.routeId);
                Toast.makeText(context, "Route has been added!", Toast.LENGTH_SHORT).show();
                route.following = true;
                view.setSelected(true);
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putStringSet(RoutesActivity.KEY_PREFS_FAV_ROUTES, routes);
            editor.apply();
        }
    }

}
