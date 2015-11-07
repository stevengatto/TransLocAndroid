package mc_sg.translocapp.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

/**
 *  {
 *      "4001278": "aljyEfjf_NPV",
 *      "4001360": "iukyE|}e_NWM",
 *      "4001362": "k_lyEfsf_NhHwT",
 *      "4001373": "qblyEj{h_N^A",
 *      "4001539": "s`pyE~fl_NXsEZiJXsKPiU?_G",
 *      "4001567": "a`nyE|_g_NNHrAH^C",
 *      "4001568": "}{myEl`g_N_@Oe@Ek@AQF",
 *      "4001622": "ugnyEluj_NEa@",
 *      "4001755": "{voyEvnk_Nl@J|CR",
 *      "4022731": "{gnyEjtj_Na@{E",
 *      "4023819": "_whyEzvi_NfDoA",
 *      "4035047": "e|hyEbgi_NaBoH",
 *      "4036527": "s`pyE~fl_N_@jG[`Ey@nHw@bGsA|H",
 *      "4043803": "s}iyEzqh_NZbARb@h@v@^^TRh@Z`C|@rB`@xHX`FL",
 *      "4043923": "glmyExcb_NHlU",
 *      "4043939": "wzlyExid_NWdB",
 *      "4067611": "o{lyE~ld_NWhB",
 *      "4079123": "mbnyEdik_NUy@iAqF]{Bi@oE",
 *      "4079431": "}bkyEf~f_Nl@MpBi@",
 *      "4079435": "}bkyEf~f_NaAH"
 *  }
 *
 *  Map of segments in the form:
 *
 *  {
 *      "segment_id" : "google polyline"
 *  }
 */
public class SegmentMap extends HashMap<String, String> implements Serializable {

    public String getPolyline(String key) {
        return this.get(key);
    }

    public Set<String> getSegmentIds() {
        return this.keySet();
    }

}
