package mc_sg.translocapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 *  {
 *      "long_name": "Triangle Transit",
 *      "language": "en",
 *      "position": {
 *          "lat": 35.87451,
 *          "lng": -78.83801
 *      },
 *      "name": "tt",
 *      "short_name": "Triangle Transit",
 *      "phone": null,
 *      "url": "http://www.triangletransit.org",
 *      "timezone": "America/New_York",
 *      "bounding_box": [
 *          {
 *              "lat": 35.69212,
 *              "lng": -79.1111
 *          },
 *          {
 *              "lat": 36.08725,
 *              "lng": -78.32349
 *          }
 *      ],
 *      "agency_id": "12"
 *  }
 */
public class Agency {

    @SerializedName("long_name")
    public String longName;

    public String language;
    public Position position;
    public String name;

    @SerializedName("short_name")
    public String shortName;

    public String phone;
    public String url;
    public String timezone;

    @SerializedName("bounding_box")
    public ArrayList<Position> boundingBox;

    @SerializedName("agency_id")
    public String agencyId;


    public class Position {
        public double lat, lng;
    }

}
