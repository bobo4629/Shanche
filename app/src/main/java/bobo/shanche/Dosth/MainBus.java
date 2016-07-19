package bobo.shanche.Dosth;

import java.util.List;

import bobo.shanche.jsonDo.BusSite;

/**
 * Created by bobo1 on 2016/7/13.
 */
public class MainBus {
    private String busID;
    private String lineName;
    private String startSite;
    private String endSite;
    private int upDown;
    private List<String> isbusList;

    public List<BusSite> getBusSiteList() {
        return busSiteList;
    }

    public void setBusSiteList(List<BusSite> busSiteList) {
        this.busSiteList = busSiteList;
    }

    private List<BusSite> busSiteList;

    public List<String> getIsbusList() {
        return isbusList;
    }

    public void setIsbusList(List<String> isbusList) {
        this.isbusList = isbusList;
    }




    public int getUpDown() {
        return upDown;
    }

    public void setUpDown(int upDown) {
        this.upDown = upDown;
    }

    public String getBusID() {
        return busID;
    }

    public void setBusID(String busID) {
        this.busID = busID;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getStartSite() {
        return startSite;
    }

    public void setStartSite(String startSite) {
        this.startSite = startSite;
    }

    public String getEndSite() {
        return endSite;
    }

    public void setEndSite(String endSite) {
        this.endSite = endSite;
    }


}
