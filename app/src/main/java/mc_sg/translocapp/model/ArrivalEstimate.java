package mc_sg.translocapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

//{
//    "arrivals":[
//        {
//            "route_id":"4000100",
//            "vehicle_id":"4000844",
//            "arrival_at":"2014-01-03T17:06:33-05:00",
//            "type":"vehicle-based"
//        }
//    ],
//    "agency_id":"16",
//    "stop_id":"4099542"
//}
public class ArrivalEstimate {

    public List<Arrival> arrivals;

    @SerializedName("agency_id")
    public String agencyId;

    @SerializedName("stop_id")
    public String stopId;

    public class Arrival {

        @SerializedName("route_id")
        public String routeId;

        @SerializedName("vehicle_id")
        public String vehicleId;

        @SerializedName("arrival_at")
        public Date arrivalAt;

        public String type;
    }
}
