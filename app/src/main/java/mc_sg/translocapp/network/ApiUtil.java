package mc_sg.translocapp.network;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.model.VisibleRegion;

import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;

/**
 * Created by steven on 10/22/15.
 */
public class ApiUtil {

    /**
     * Trans Loc API base URL
     */
    private static final String BASE_API_URL = "https://transloc-api-1-2.p.mashape.com";

    /**
     * Test and production mash ape API token
     */
    private static final String API_KEY_TEST = "OrIyZFqgWNmsh0eSBVTFkPbis77yp165Xrnjsnnid9JtALLmcP";
    private static final String API_KEY_PROD = "iSHBDWYVI9mshz8sJZD2r8B1I9Rlp1t7cGpjsnZDSANwCBjDw7";

    /**
     * The HTTP header key for the mash ape API token
     */
    private static final String KEY_API_TOKEN = "X-Mashape-Key";

    public static TransLocApi getTransLocApi() {
        // Create request interceptor to add headers to retrofit web calls
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader(KEY_API_TOKEN, API_KEY_PROD);
            }
        };

        // Create Rest adapter using the OkHttp library as a client
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_API_URL)
                .setClient(new OkClient())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(requestInterceptor)
                .build();

        // Create trans loc api to interact with the server as a Java interface
        return restAdapter.create(TransLocApi.class);
    }

    /**
     * Retrofit callback wrapper class that handles all callback errors.
     */
    public static abstract class RetroCallback<T> implements Callback<T> {

        private Context context;

        public RetroCallback(Context context){
            this.context = context;
        }

        /**
         * Handles general retrofit response failures
         *
         * @param retrofitError
         */
        @Override
        public void failure(RetrofitError retrofitError) {
            String errorMsg = "An error has occurred";
            switch(retrofitError.getKind()) {
                case HTTP:
                    errorMsg = "Error: " + retrofitError.getResponse().getStatus();
                    break;
                case CONVERSION:
                    errorMsg = "Conversion Error";
                    break;
                case NETWORK:
                    errorMsg = "Network Error";
                    break;
                case UNEXPECTED:
                    errorMsg = "Unexpected Error";
                    break;
            }
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Get the formatted geo_area for the trans loc api from the visible region of
     * a Google Map
     *
     * @param region Visible region of a Google Map
     * @return Formatted string representing the bounding box visible on the map
     */
    public static String getGeoArea(VisibleRegion region) {
        return region.farRight.latitude + "," + region.farRight.longitude + "|"
                + region.nearLeft.latitude + "," + region.nearLeft.longitude;
    }

    public static String formatAgencyList(List<Integer> agencyIds) {
        if (agencyIds == null) {
            return "";
        }

        String formattedList = "";
        for (Integer id : agencyIds) {
            formattedList += (id + ",");
        }
        // remove last comma
        if (formattedList.endsWith(",")) {
            formattedList = formattedList.substring(0, formattedList.length() - 1);
        }
        return formattedList;
    }

}
