package mc_sg.translocapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 *  {
 *      "code": "231",
 *      "description": "",
 *      "url": "",
 *      "parent_station_id": null,
 *      "agency_ids": [
 *          "16",
 *          "192"
 *      ],
 *      "station_id": null,
 *      "location_type": "stop",
 *      "location": {
 *          "lat": 35.7845,
 *          "lng": -78.67627
 *      },
 *      "stop_id": "4128450",
 *      "routes": [
 *          "4003686",
 *          "4003706"
 *      ],
 *      "name": "Dan Allen Dr at Lee Lot"
 *  }
 */
public class Stop {

    public String code;
    public String description;
    public String url;

    @SerializedName("parent_station_id")
    public String parentStationId;

    @SerializedName("agency_ids")
    public List<String> agencyIds;

    @SerializedName("station_id")
    public String stationId;

    @SerializedName("location_type")
    public String locationType;

    public Location location;

    @SerializedName("stop_id")
    public String stopId;
    public List<String> routes;
    public String name;

    public class Location {
        String lat, lng;
    }

}
