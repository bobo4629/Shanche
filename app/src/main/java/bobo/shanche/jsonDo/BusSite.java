package bobo.shanche.jsonDo;

import java.util.List;

/**
 * Created by bobo1 on 2016/7/6.
 */
public class BusSite {

    private String LineId;
    private int UpDown;
    private String SiteId;
    private int Seq;
    private String LineName;
    private String SiteName;
    private float Longitude;
    private float Latitude;
    private List<String> BusList;

    public List<String> getBusList() {
        return BusList;
    }
    public String getLineId() {
        return LineId;
    }
    public int getUpDown() {
        return UpDown;
    }
    public String getSiteId() {
        return SiteId;
    }
    public int getSeq() {
        return Seq;
    }
    public String getLineName() {
        return LineName;
    }
    public String getSiteName() {
        return SiteName;
    }
    public float getLongitude() {
        return Longitude;
    }
    public float getLatitude() {
        return Latitude;
    }
}
