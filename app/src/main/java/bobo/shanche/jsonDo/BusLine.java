package bobo.shanche.jsonDo;

import java.util.List;

/**
 * Created by bobo1 on 2016/7/6.
 */
public class BusLine {

    private String Id;
    private String LineName;
    private int UpMileage;
    private int DownMileage;
    private int UpSiteNum;
    private int DownSiteNum;
    private String UpStartTime;
    private String UpEndTime;
    private String DownStartTime;
    private String DownEndTime;
    private String BusCompany;
    private int IsUnityFare;
    private int Fare;
    private String ModifyTime;
    private String SiteModifyTime;
    private String PointModifyTime;
    private String BusModifyTime;
    private boolean IsResolved;
    private String Remark;
    private List<BusSite> StartEndSites;

    public List<BusSite> getStartEndSites() {
        return StartEndSites;
    }

    public String getId() {
        return Id;
    }

    public String getLineName() {
        return LineName;
    }

    public int getUpMileage() {
        return UpMileage;
    }

    public int getDownMileage() {
        return DownMileage;
    }

    public int getUpSiteNum() {
        return UpSiteNum;
    }

    public int getDownSiteNum() {
        return DownSiteNum;
    }

    public String getUpStartTime() {
        return UpStartTime;
    }

    public String getUpEndTime() {
        return UpEndTime;
    }

    public String getDownStartTime() {
        return DownStartTime;
    }

    public String getDownEndTime() {
        return DownEndTime;
    }

    public String getBusCompany() {
        return BusCompany;
    }

    public int getIsUnityFare() {
        return IsUnityFare;
    }

    public int getFare() {
        return Fare;
    }

    public String getModifyTime() {
        return ModifyTime;
    }

    public String getSiteModifyTime() {
        return SiteModifyTime;
    }

    public String getPointModifyTime() {
        return PointModifyTime;
    }

    public String getBusModifyTime() {
        return BusModifyTime;
    }

    public boolean isResolved() {
        return IsResolved;
    }

    public String getRemark() {
        return Remark;
    }


}
