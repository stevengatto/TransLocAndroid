package mc_sg.translocapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class RouteActivity extends AppCompatActivity {

    public static final String KEY_ROUTE_ID = "KEY_ROUTE_ID";
    public static final String KEY_ROUTE_NAME = "KEY_ROUTE_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        Bundle extras = getIntent().getExtras();
        String routeId = extras.getString(KEY_ROUTE_ID);
        String routeName = extras.getString(KEY_ROUTE_NAME);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle((routeName == null || routeName.isEmpty()) ? "Route" : routeName);
        setSupportActionBar(toolbar);

        ((TextView) findViewById(R.id.single_route_info_1)).setText(routeId);
    }


}
