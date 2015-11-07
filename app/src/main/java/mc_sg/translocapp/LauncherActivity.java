package mc_sg.translocapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


public class LauncherActivity extends Activity {

    public static final String KEY_AGENCY_ID = "key_launcher_activity_agency_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(HomeAgencyActivity.PREFS_HOME_AGENCY, MODE_PRIVATE);
        String agencyId = prefs.getString(HomeAgencyActivity.KEY_PREFS_AGENCY_ID, null);
        if (agencyId != null) {
            // launch list activity with param of agency id
            Intent routesIntent = new Intent(this, RoutesActivity.class);
            Bundle data = new Bundle();
            data.putString(KEY_AGENCY_ID, agencyId);
            routesIntent.putExtras(data);
            startActivity(routesIntent);
        } else {
            Intent mapIntent = new Intent(this, HomeAgencyActivity.class);
            startActivity(mapIntent);
        }
        finish();
    }
}
