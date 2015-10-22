package mc_sg.translocapp.model;

import com.google.gson.annotations.SerializedName;

/**
 *  {
 *      "rate_limit": 0,
 *      "expires_in": 300,
 *      "api_latest_version": "1.2",
 *      "generated_on": "2014-02-12T19:54:00+00:00",
 *      "data": [
 *          <T>
 *      ],
 *   "api_version": "1.2"
 *  }
 */
public class Response<T> {

    @SerializedName("rate_limit")
    public int rateLimit;

    @SerializedName("expires_in")
    public int expiresIn;

    @SerializedName("api_latest_version")
    public String apiLatestVersion;

    // use Response<List<model>> for arrays of data
    // use Response<model> for object
    public T data;

    @SerializedName("api_version")
    public String apiVersion;

}
