package mc_sg.translocapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.List;

import mc_sg.translocapp.model.AgencyRouteMap;
import mc_sg.translocapp.model.Response;
import mc_sg.translocapp.network.ApiUtil;

public class RoutesActivity extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private String agencyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
    }

    private class RoutesCallback extends ApiUtil.RetroCallback<Response<AgencyRouteMap>> {

        private Context context;
        public RoutesCallback(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        public void success(Response<AgencyRouteMap> agencyRouteMapResponse, retrofit.client.Response response) {
            List<AgencyRouteMap.Route> routes = agencyRouteMapResponse.data.getRoutes(agencyId);

            List<AgencyRouteMap.Route> activeRoutes = new ArrayList<>();
            for (AgencyRouteMap.Route route : routes) {
                if (route.isActive) {
                    activeRoutes.add(route);
                }
            }

            expandableListView.setAdapter(new RouteAdapter(context, 0, 0, 0, activeRoutes));
        }
    }

    private class RouteAdapter extends BaseExpandableListAdapter {

        private final List<AgencyRouteMap.Route> routes;

        private final Context context;

        private final DataSetObservable dataSetObservable = new DataSetObservable();

        private final LayoutInflater inflater;

        private final Integer groupExpandedView;

        private final Integer childView;

        private final Integer groupClosedView;

        public RouteAdapter(Context context, int groupClosedView, int groupExpandedView, int childView, List<AgencyRouteMap.Route> routes) {
            this.routes = routes;
            this.context = context;
            this.groupExpandedView = groupExpandedView;
            this.childView = childView;
            this.groupClosedView = groupClosedView;
            this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        protected List<AgencyRouteMap.Route> getRoutes() {
            return routes;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            this.getDataSetObservable().registerObserver(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            this.getDataSetObservable().unregisterObserver(observer);
        }

        @Override
        public int getGroupCount() {
            return getRoutes().size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_route_list, parent, false);

                holder = new GroupHolder();
                holder.title = (TextView) convertView.findViewById(R.id.item_route_list_title);
                holder.desc = (TextView) convertView.findViewById(R.id.item_route_list_desc);
                holder.icon = (ImageView) convertView.findViewById(R.id.item_route_list_icon);

                convertView.setTag(holder);
            }

            holder = (GroupHolder) convertView.getTag();
            AgencyRouteMap.Route currentRoute = routes.get(groupPosition);

            if (currentRoute.shortName == null || currentRoute.shortName.isEmpty()) {
                holder.title.setText(currentRoute.longName);
            } else {
                holder.title.setText(currentRoute.shortName);
            }

            holder.desc.setText(currentRoute.stops.size() + " stops");

            TextDrawable icon = TextDrawable.builder().buildRound((""+(groupPosition+1)), ColorGenerator.MATERIAL.getColor(groupPosition));
            holder.icon.setImageDrawable(icon);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return getRoutes().isEmpty();
        }

        @Override
        public void onGroupExpanded(int groupPosition) {

        }

        @Override
        public void onGroupCollapsed(int groupPosition) {

        }

        @Override
        public long getCombinedChildId(long groupId, long childId) {
            return 0;
        }

        @Override
        public long getCombinedGroupId(long groupId) {
            return 0;
        }

        public DataSetObservable getDataSetObservable() {
            return dataSetObservable;
        }

        public Integer getGroupExpandedView() {
            return groupExpandedView;
        }

        public Integer getChildView() {
            return childView;
        }

        public Integer getGroupClosedView() {
            return groupClosedView;
        }

        private class GroupHolder {
            TextView title;
            TextView desc;
            ImageView icon;
        }
    }

}
