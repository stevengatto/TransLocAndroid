package mc_sg.translocapp.view;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
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

import java.util.Random;

import mc_sg.translocapp.R;

public class RouteListItem extends RelativeLayout implements OnMapReadyCallback {

    TextView tvTitle, tvDesc;
    ImageView ivIcon;

    public RouteListItem(Context context) {
        this(context, -1);
    }

    public RouteListItem(Context context, AttributeSet attrs) {
        this(context, -1);
    }

    public RouteListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, -1);
    }

    public RouteListItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, -1);
    }

    public RouteListItem(Context context, int seed) {
        super(context);

        if (seed == -1) {
            seed = (new Random()).nextInt();
        }

        View view = inflate(context, R.layout.item_route_list, this);

        tvTitle = (TextView) view.findViewById(R.id.item_route_list_title);
        tvDesc = (TextView) view.findViewById(R.id.item_route_list_desc);
        ivIcon = (ImageView) view.findViewById(R.id.item_route_list_icon);
        FrameLayout mapFrame = (FrameLayout) view.findViewById(R.id.item_route_list_map_frame);

        FrameLayout frame = new FrameLayout(context);
        int newId = (seed+1) * 38943;
        Log.d(null, "***************** NEW ROUTE ITEM HASH: " + newId);
        frame.setId(newId);

        GoogleMapOptions options = new GoogleMapOptions();
        options.liteMode(true); //this makes it possible, otherwise your list view would be really slow
        MapFragment mapFrag = MapFragment.newInstance(options);

        mapFrag.getMapAsync(this);

        mapFrame.addView(frame);
        FragmentManager fm = ((AppCompatActivity) context).getFragmentManager();
        fm.beginTransaction().add(newId, mapFrag).commit();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setDesc(String desc) {
        tvDesc.setText(desc);
    }

    public void setIconImageDrawable(Drawable image) {
        ivIcon.setImageDrawable(image);
    }
}
