package mc_sg.translocapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class LauncherActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String KEY_AGENCY_ID = "key_launcher_activity_agency_id";
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int coarseLocPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int fineLocPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        ArrayList<String> requiredPermissions = new ArrayList<>();
        if (coarseLocPermission != PackageManager.PERMISSION_GRANTED
                && fineLocPermission != PackageManager.PERMISSION_GRANTED) {
            // If we need permissions request them.
            requiredPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            requestPermissions(requiredPermissions);
        } else {
            // Otherwise launch the app.
            continueCreate();
        }
    }

    private void continueCreate() {
        SharedPreferences agencyPrefs = getSharedPreferences(HomeAgencyActivity.PREFS_HOME_AGENCY, MODE_PRIVATE);
        String agencyId = agencyPrefs.getString(HomeAgencyActivity.KEY_PREFS_AGENCY_ID, null);

        SharedPreferences favRoutePrefs = getSharedPreferences(ActiveRoutesActivity.PREFS_FAVORITES, MODE_PRIVATE);
        Set<String> favRouteIds = favRoutePrefs.getStringSet(ActiveRoutesActivity.KEY_PREFS_FAV_ROUTES, null);

        if (agencyId == null || agencyId.isEmpty()) {
            // wipe out old favorites
            favRoutePrefs.edit().putStringSet(ActiveRoutesActivity.KEY_PREFS_FAV_ROUTES, null).apply();

            Intent mapIntent = new Intent(this, HomeAgencyActivity.class);
            startActivity(mapIntent);
        } else if (favRouteIds == null || favRouteIds.isEmpty()) {
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
    }

    private void requestPermissions(List<String> permissions) {
        String[] permissionStrings = new String[permissions.size()];
        for (int i = 0; i < permissions.size(); i++) {
            permissionStrings[i] = permissions.get(i);
        }
        ActivityCompat.requestPermissions(this, permissionStrings, PERMISSION_REQUEST_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            // If permission was denied close the app, there's really nothing else we can do.
            finish();
            System.exit(0);
        }
        // Otherwise launch the app.
        continueCreate();
    }
}
