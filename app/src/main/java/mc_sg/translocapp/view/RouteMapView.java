package mc_sg.translocapp.view;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.List;
import java.util.zip.Inflater;

import mc_sg.translocapp.R;

public class RouteMapView extends RelativeLayout implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    ProgressCard progressCard;
    GoogleMap map;

    public RouteMapView(Context context) {
        this(context, null);
    }

    public RouteMapView(Context context, ViewGroup parent) {
        this(context, null, 0, parent);
    }

    public RouteMapView(Context context, AttributeSet attrs, int defStyleAttr, ViewGroup parent) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.view_route_map, parent);

        mapFragment = (SupportMapFragment) ((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.route_map);
        mapFragment.getMapAsync(this);

        progressCard = (ProgressCard) findViewById(R.id.route_map_progress);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        progressCard.setVisibility(INVISIBLE);
        this.map = googleMap;

        // make the map normal and disable all interaction
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setIndoorLevelPickerEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setScrollGesturesEnabled(false);
        map.getUiSettings().setTiltGesturesEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(false);
    }
}
