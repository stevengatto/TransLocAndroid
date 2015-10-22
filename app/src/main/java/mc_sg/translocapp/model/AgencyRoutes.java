package mc_sg.translocapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 *  {
 *      "description": "",
 *      "short_name": "12",
 *      "route_id": "4003694",
 *      "color": "e7b908",
 *      "segments": [
 *          [
 *              "4001278",
 *              "forward"
 *          ],
 *          [
 *              "4055011",
 *              "backward"
 *          ],
 *          [
 *              "4055055",
 *              "backward"
 *          ],
 *          [
 *              "4059275",
 *              "backward"
 *          ]
 *      ],
 *      "is_active": false,
 *      "agency_id": 16,
 *      "text_color": "FFFFFF",
 *      "long_name": "Nightwolf",
 *      "url": "",
 *      "is_hidden": false,
 *      "type": "bus",
 *      "stops": [
 *          "4099310",
 *          "4099314",
 *          "4100050",
 *          "4100162",
 *          "4099706",
 *          "4100178",
 *          "4099298"
 *      ]
 *  }
 */
public class AgencyRoutes {

    public String description;

    @SerializedName("short_name")
    public String shortName;

    @SerializedName("route_id")
    public String routeId;

    public String color;

    public ArrayList<ArrayList<String>> segments;

    @SerializedName("is_active")
    public boolean isActive;

    @SerializedName("agency_id")
    public int agencyId;

    @SerializedName("text_color")
    public String textColor;

    @SerializedName("long_name")
    public String longName;

    public String url;

    @SerializedName("is_hidden")
    public boolean isHidden;

    public String type;

    public ArrayList<String> stops;

}
