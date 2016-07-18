package bobo.shanche.jsonDo;

/**
 * Created by bobo1 on 2016/7/6.
 */
public class Station {

    private String LineId;
    private String LineName;
    private int UpDown;
    private String SiteId;
    private String SiteName;
    private int FirstBus;
    private int SecondBus;
    private boolean IsUseGPS;
    private String Remark;
    private java.util.List<BusSite> List;


    public String getLineId() {
        return LineId;
    }

    public String getLineName() {
        return LineName;
    }

    public int getUpDown() {
        return UpDown;
    }

    public String getSiteId() {
        return SiteId;
    }

    public String getSiteName() {
        return SiteName;
    }

    public int getFirstBus() {
        return FirstBus;
    }

    public int getSecondBus() {
        return SecondBus;
    }

    public boolean isUseGPS() {
        return IsUseGPS;
    }

    public String getRemark() {
        return Remark;
    }

    public java.util.List<BusSite> getList() {
        return List;
    }
}
