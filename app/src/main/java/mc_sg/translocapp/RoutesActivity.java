package mc_sg.translocapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.List;

import mc_sg.translocapp.model.AgencyRouteMap;
import mc_sg.translocapp.model.Response;
import mc_sg.translocapp.network.ApiUtil;
import mc_sg.translocapp.view.RouteListItem;
import retrofit.RetrofitError;

public class RoutesActivity extends AppCompatActivity {

    private ListView listView;
    private View listProgress;
    private String agencyId;
    private Context context;

    private List<AgencyRouteMap.Route> activeRoutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_routes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Routes");
        setSupportActionBar(toolbar);

        // get agency id from bundle or shared prefs
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            agencyId = extras.getString(LauncherActivity.KEY_AGENCY_ID);
        } else {
            SharedPreferences prefs = getSharedPreferences(HomeAgencyActivity.PREFS_HOME_AGENCY, MODE_PRIVATE);
            agencyId = prefs.getString(HomeAgencyActivity.KEY_PREFS_AGENCY_ID, null);
        }

        ApiUtil.getTransLocApi().getRoutes(agencyId, null, new RoutesCallback(this));

        // R aggregates xml data to interface with.
        listView = (ListView) findViewById(R.id.routes_listview);
        listView.setOnItemClickListener(new OnRouteClick());
        listProgress = findViewById(R.id.routes_list_progress_card);
    }

    private class OnRouteClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(context, "Route " + activeRoutes.get(position).routeId + " clicked", Toast.LENGTH_LONG).show();
        }
    }

    private class RoutesCallback extends ApiUtil.RetroCallback<Response<AgencyRouteMap>> {

        private Context context;
        public RoutesCallback(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            super.failure(retrofitError);
            listProgress.setVisibility(View.INVISIBLE);
        }

        @Override
        public void success(Response<AgencyRouteMap> agencyRouteMapResponse, retrofit.client.Response response) {
            listProgress.setVisibility(View.INVISIBLE);
            List<AgencyRouteMap.Route> routes = agencyRouteMapResponse.data.getRoutes(agencyId);

            activeRoutes = new ArrayList<>();
            for (AgencyRouteMap.Route route : routes) {
                if (route.isActive) {
                    activeRoutes.add(route);
                }
            }
            listView.setAdapter(new RouteAdapter(context, activeRoutes));
        }
    }

    private class RouteAdapter extends BaseAdapter {

        private final List<AgencyRouteMap.Route> routes;
        private final Context context;

        public RouteAdapter(Context context, List<AgencyRouteMap.Route> routes) {
            this.routes = routes;
            this.context = context;
        }

        protected List<AgencyRouteMap.Route> getRoutes() {
            return routes;
        }

        @Override
        public int getCount() {
            return getRoutes().size();
        }

        @Override
        public Object getItem(int position) {
            return getRoutes().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new RouteListItem(context, position);
            }
            RouteListItem routeView = (RouteListItem) convertView;
            AgencyRouteMap.Route currentRoute = routes.get(position);

            if (currentRoute.shortName == null || currentRoute.shortName.isEmpty()) {
                routeView.setTitle(currentRoute.longName);
            } else {
                routeView.setTitle(currentRoute.shortName);
            }
            routeView.setDesc(currentRoute.stops.size() + " stops");
            TextDrawable icon = TextDrawable.builder().buildRound((""+(position+1)), ColorGenerator.MATERIAL.getColor(position*10));
            routeView.setIconImageDrawable(icon);
            return routeView;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEmpty() {
            return getRoutes().isEmpty();
        }
    }

}
