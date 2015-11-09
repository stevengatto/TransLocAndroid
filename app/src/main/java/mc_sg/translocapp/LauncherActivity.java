package mc_sg.translocapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.Set;


public class LauncherActivity extends Activity {

    public static final String KEY_AGENCY_ID = "key_launcher_activity_agency_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences agencyPrefs = getSharedPreferences(HomeAgencyActivity.PREFS_HOME_AGENCY, MODE_PRIVATE);
        String agencyId = agencyPrefs.getString(HomeAgencyActivity.KEY_PREFS_AGENCY_ID, null);

        SharedPreferences favRoutePrefs = getSharedPreferences(ActiveRoutesActivity.PREFS_FAVORITES, MODE_PRIVATE);
        Set<String> favRouteIds = favRoutePrefs.getStringSet(ActiveRoutesActivity.KEY_PREFS_FAV_ROUTES, null);

        if (agencyId == null) {
            // wipe out old favorites
            favRoutePrefs.edit().putStringSet(ActiveRoutesActivity.KEY_PREFS_FAV_ROUTES, null).apply();

            Intent mapIntent = new Intent(this, HomeAgencyActivity.class);
            startActivity(mapIntent);
        } else if (favRouteIds == null) {
            // launch list activity with param of agency id
            Intent routesIntent = new Intent(this, ActiveRoutesActivity.class);
            Bundle data = new Bundle();
            data.putString(KEY_AGENCY_ID, agencyId);
            routesIntent.putExtras(data);
            startActivity(routesIntent);
        } else {
            Intent mapIntent = new Intent(this, FavoriteRoutesActivity.class);
            startActivity(mapIntent);
        }

        finish();
    }
}
