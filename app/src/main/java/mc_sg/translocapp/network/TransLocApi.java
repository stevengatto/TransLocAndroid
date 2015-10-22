package mc_sg.translocapp.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mc_sg.translocapp.model.Agency;
import mc_sg.translocapp.model.Route;
import mc_sg.translocapp.model.ArrivalEstimate;
import mc_sg.translocapp.model.Response;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * API to interact with the Trans Loc Api as a Java interface
 */
public interface TransLocApi {

    @GET("/agencies.json")
    void getAgencies(@Query("agencies") String agencies,
                     @Query("geo_area") String geo_area,
                     ApiUtil.RetroCallback<Response<List<Agency>>> callback);

    @GET("/arrival-estimates.json")
    void getArrivalEstimates(@Query("agencies") String agencies,
                             @Query("routes") String routes,
                             @Query("stops") String stops,
                             ApiUtil.RetroCallback<Response<List<ArrivalEstimate>>> callback);

    @GET("/routes.json")
    void getRoutes(@Query("agencies") String agencies,
                   @Query("geo_area") String geo_area,
                   ApiUtil.RetroCallback<Response<Map<String, ArrayList<Route>>>> callback);
}