package mc_sg.translocapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by steven on 10/23/15.
 */
public class AgencyVehicleMap extends HashMap<String, List<AgencyVehicleMap.Vehicle>> {

    public Set<String> getAgencyIds() {
        return this.keySet();
    }

    public List<AgencyVehicleMap.Vehicle> getVehicles(String agencyId) {
        return this.get(agencyId);
    }

    public AgencyVehicleMap.Vehicle getVehicle(String agencyId, String routeId) {
        List<AgencyVehicleMap.Vehicle> vehicles = this.get(agencyId);
        if (vehicles != null) {
            for (AgencyVehicleMap.Vehicle vehicle : vehicles) {
                if (vehicle.routeId.equals(routeId)) {
                    return vehicle;
                }
            }
        }
        return null;
    }

    /**
     *  {
     *      "description": null,
     *      "passenger_load": null,
     *      "standing_capacity": null,
     *      "seating_capacity": null,
     *      "last_updated_on": "2014-01-03T21:12:47.570000+00:00",
     *      "call_name": "4281",
     *      "speed": 0,
     *      "vehicle_id": "4002320",
     *      "segment_id": "4043935",
     *      "route_id": "4000098",
     *      "view_arrival_estimates": [],
     *      "tracking_status": "up",
     *      "location": {
     *          "lat": 35.78867,
     *          "lng": -78.67292
     *      },
     *      "heading": 101
     *  }
     */
    public class Vehicle {

        public String description;

        @SerializedName("passenger_load")
        public int passengerLoad;

        @SerializedName("standing_capacity")
        public int standingCapacity;

        @SerializedName("seating_capacity")
        public int seatingCapacity;

        @SerializedName("last_updated_on")
        public String lastUpdatedOn;

        @SerializedName("call_name")
        public String callName;

        public double speed;

        @SerializedName("vehicle_id")
        public String vehicleId;

        @SerializedName("segment_id")
        public String segmentId;

        @SerializedName("route_id")
        public String routeId;

        @SerializedName("arrival_estimates")
        public List<Estimate> arrivalEstimates;

        @SerializedName("tracking_status")
        public String trackingStatus;

        public Location location;

        public class Location {
            public String lat, lng;
        }

        public class Estimate {
            @SerializedName("route_id")
            public String routeId;

            @SerializedName("arrival_at")
            public String arrivalAt;

            @SerializedName("stop_id")
            public String stopId;

        }

    }

}
