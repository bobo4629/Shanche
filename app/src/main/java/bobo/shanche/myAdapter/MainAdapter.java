package bobo.shanche.myAdapter;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thinkcool.circletextimageview.CircleTextImageView;

import java.util.List;

import bobo.shanche.Dosth.MainBus;
import bobo.shanche.R;
import bobo.shanche.jsonDo.BusSite;

/**
 * Created by bobo1 on 2016/7/13.
 */
public class MainAdapter extends BaseAdapter {
    private List<MainBus> list;
    private LayoutInflater layoutInflater;

    public void setLocation(Location location) {
        this.location = location;
    }

    private Location location;
    private double distance_Earth =6378137;



    private String carString;

    public MainAdapter(Context context, List<MainBus> mainBusList) {
        this.list = mainBusList;
        this.layoutInflater =layoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return list.isEmpty()?0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView ==null){
            if(list.get(position).getLineName().length()>5){
                convertView = layoutInflater.inflate(R.layout.main_item_longtext,null);
            }else {
                convertView = layoutInflater.inflate(R.layout.main_item,null);
            }
        }

        CircleTextImageView circleTextImageView = (CircleTextImageView) convertView.findViewById(R.id.view_busLine);
        TextView textView_BusSite = (TextView)convertView.findViewById(R.id.textView_Collection_BusSite);
        TextView textView_IsBus = (TextView)convertView.findViewById(R.id.textView_Collection_IsBus);

        java.util.Random random=new java.util.Random();
        int result=random.nextInt(9);// 返回[0,9)集合中的整数，不包括9
        int color = 0;
        switch (result){
            case 0:
                color = R.color.a;
                break;
            case 1:
                color = R.color.b;
                break;
            case 2:
                color=R.color.c;
                break;
            case 3:
                color=R.color.d;
                break;
            case 4:
                color=R.color.e;
                break;
            case 5:
                color=R.color.f;
                break;
            case 6:
                color=R.color.g;
                break;
            case 7:
                color=R.color.h;
                break;
            case 8:
                color=R.color.i;
                break;
        }
        MainBus mainBus=list.get(position);
        circleTextImageView.setText(mainBus.getLineName());
        circleTextImageView.setFillColorResource(color);

        textView_BusSite.setText(mainBus.getStartSite()+" - "+mainBus.getEndSite());
        if(mainBus.getIsbusList()!=null){
            if(mainBus.getIsbusList().isEmpty()){
                carString="没有车辆正在运营";
            }else {
                if(location==null){
                    carString="有车:";
                    for(String string:mainBus.getIsbusList()){
                        carString+=string+";";
                    }
                }else {
                    String nestestStop=null;
                    double distance_set=distance_Earth;
                     for(BusSite busSite:mainBus.getBusSiteList()){
                        double distance_now =Distance(busSite.getLongitude(),busSite.getLatitude(),location.getLongitude(),location.getLatitude());
                        if(distance_now<distance_set){
                            distance_set=distance_now;
                            nestestStop=busSite.getSiteName();
                        }
                        carString ="最近的车在:"+nestestStop;
                    }
                }

            }
        }
        textView_IsBus.setText(carString);


        return convertView;
    }
    public static double Distance(double long1, double lat1, double long2, double lat2) {
        double a, b, R;
        R = 6378137; // 地球半径
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - long2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2
                * R
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
        return d;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return super.areAllItemsEnabled();
    }
}
