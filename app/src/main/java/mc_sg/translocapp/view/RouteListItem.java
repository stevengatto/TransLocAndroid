package mc_sg.translocapp.view;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.List;
import java.util.Random;
import java.util.Set;

import mc_sg.translocapp.R;
import mc_sg.translocapp.model.SegmentMap;

public class RouteListItem extends RelativeLayout implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    TextView tvTitle, tvDesc;
    ImageView ivFavorite, ivNoMap;
    CardView card;
    Context context;

    FrameLayout innerMapFrame;
    SegmentMap segmentMap;
    MapFragment mapFrag;
    GoogleMap map;
    int polylineColor;

    public RouteListItem(Context context) {
        this(context, -1, null);
    }

    public RouteListItem(Context context, AttributeSet attrs) {
        this(context, -1, null);
    }

    public RouteListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, -1, null);
    }

    public RouteListItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, -1, null);
    }

    public RouteListItem(Context context, int seed, String routeId) {
        super(context);
        this.context = context;

        if (seed == -1) {
            seed = (new Random()).nextInt();
        }

        ViewGroup viewGroup = (ViewGroup) inflate(context, R.layout.item_route_list, this);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        tvTitle = (TextView) viewGroup.findViewById(R.id.item_route_list_title);
        tvDesc = (TextView) viewGroup.findViewById(R.id.item_route_list_desc);
        card = (CardView) viewGroup.findViewById(R.id.item_route_list_cardview);
        ivNoMap = (ImageView) viewGroup.findViewById(R.id.item_route_list_no_image);
        ivFavorite = (ImageView) viewGroup.findViewById(R.id.item_route_list_favorite);
        ivFavorite.setTag(routeId); // tag the favorite button with the route id string


        FrameLayout outerMapFrame = (FrameLayout) viewGroup.findViewById(R.id.item_route_list_map_frame);

        innerMapFrame = new FrameLayout(context);
        innerMapFrame.setVisibility(INVISIBLE);
        int newId = (seed + 1) * 38943; // hopefully random hash? ID clash would be bad...
        innerMapFrame.setId(newId);

        GoogleMapOptions options = new GoogleMapOptions();
        options.liteMode(true)
                .mapToolbarEnabled(false)
                .ambientEnabled(false)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .scrollGesturesEnabled(false)
                .zoomControlsEnabled(false)
                .zoomGesturesEnabled(false)
                .useViewLifecycleInFragment(true);
        mapFrag = MapFragment.newInstance(options);
        mapFrag.getMapAsync(this);

        outerMapFrame.addView(innerMapFrame);
        FragmentManager fm = ((AppCompatActivity) context).getFragmentManager();
        fm.beginTransaction().add(newId, mapFrag).commit();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        googleMap.setOnMapLoadedCallback(this);

        // no clicking on the map
        if (mapFrag.getView() != null) {
            mapFrag.getView().setClickable(false);
        }

        // setup
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

        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        if (segmentMap != null) {
            initMap(segmentMap, polylineColor);
        }
    }

    public void setupMap(SegmentMap segmentMap, int polylineColor) {
        this.segmentMap = segmentMap;
        this.polylineColor = polylineColor;
        if (map != null) {
            initMap(segmentMap, polylineColor);
        }
    }

    private void initMap(SegmentMap segmentMap, int polylineColor) {
        if (mapFrag.getView() != null && mapFrag.getView().getVisibility() == INVISIBLE) {
            mapFrag.getView().setVisibility(VISIBLE);
            ivNoMap.setVisibility(INVISIBLE);
        }

        map.clear();

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float polylineWidth = 3 * (metrics.densityDpi / 160f); // 5dp

        if (segmentMap != null) {
            Set<String> segmentKeys = segmentMap.getSegmentIds();
            List<LatLng> points;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            boolean pointIncluded = false;
            for (String key : segmentKeys) {
                points = PolyUtil.decode(segmentMap.getPolyline(key));
                map.addPolyline(new PolylineOptions()
                        .addAll(points)
                        .color(polylineColor)
                        .width(polylineWidth));

                // determine bounds for map zoom and center
                for (LatLng point : points) {
                    pointIncluded = true; // flag so that the Builder doesn't crash when empty
                    builder.include(point);
                }
            }

            if (pointIncluded) {
                LatLngBounds bounds = builder.build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 20);
                map.moveCamera(cameraUpdate);
            } else if (mapFrag.getView() != null) {
                mapFrag.getView().setVisibility(INVISIBLE);
                ivNoMap.setVisibility(VISIBLE);
            }
        }
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setDesc(String desc) {
        tvDesc.setText(desc);
    }

    public void setBackgroundColor(int color) {
        card.setCardBackgroundColor(color);
    }

    public void setFavorited(boolean favorited) {
        ivFavorite.setSelected(favorited);
    }

    public boolean getFavorited() {
        return ivFavorite.isSelected();
    }

    @Override
    public void onMapLoaded() {
        // hide progress bar forever once map loads
        findViewById(R.id.item_route_list_map_progress).setVisibility(INVISIBLE);
        innerMapFrame.setVisibility(VISIBLE);
    }
}
