package mc_sg.translocapp;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

import mc_sg.translocapp.model.Agency;
import mc_sg.translocapp.model.AgencyRouteMap;
import mc_sg.translocapp.model.Response;
import mc_sg.translocapp.network.ApiUtil;

public class RoutesActivity extends AppCompatActivity {

    private static final String AGENCY_ID = "243";

    private ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ApiUtil.getTransLocApi().getRoutes(AGENCY_ID, null, new RoutesCallback(this));

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
            List<AgencyRouteMap.Route> routes = agencyRouteMapResponse.data.getRoutes(AGENCY_ID);
            expandableListView.setAdapter(new RouteAdapter(context, 0, 0, 0, routes));
        }
    }

    private class RouteAdapter implements ExpandableListAdapter {

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

            convertView = inflater.inflate(isExpanded ? getGroupExpandedView() : getGroupClosedView(), parent, false);
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
    }

}
