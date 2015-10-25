package mc_sg.translocapp.network;

import java.util.List;

import mc_sg.translocapp.model.Agency;
import mc_sg.translocapp.model.AgencyRouteMap;
import mc_sg.translocapp.model.AgencyVehicleMap;
import mc_sg.translocapp.model.ArrivalEstimate;
import mc_sg.translocapp.model.Response;
import mc_sg.translocapp.model.SegmentMap;
import mc_sg.translocapp.model.Stop;
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
                   ApiUtil.RetroCallback<Response<AgencyRouteMap>> callback);

    @GET("/segments.json")
    void getSegments(@Query("agencies") String agencies,
                     @Query("geo_area") String geo_area,
                     @Query("routes") String routes,
                     ApiUtil.RetroCallback<Response<SegmentMap>> callback);

    @GET("/stops.json")
    void getStops(@Query("agencies") String agencies,
                  @Query("geo_area") String geo_area,
                  ApiUtil.RetroCallback<Response<List<Stop>>> callback);

    @GET("/vehicles.json")
    void getVehicles(@Query("agencies") String agencies,
                     @Query("geo_area") String geo_area,
                     @Query("routes") String routes,
                     ApiUtil.RetroCallback<Response<AgencyVehicleMap>> callback);

}