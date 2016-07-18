package bobo.shanche.myAdapter;

import android.content.Context;
import android.graphics.Color;
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
        circleTextImageView.setText(list.get(position).getLineName());
        circleTextImageView.setFillColorResource(color);

        textView_BusSite.setText(list.get(position).getStartSite()+" - "+list.get(position).getEndSite());
        if(list.get(position).getIsbusList()!=null){
            if(list.get(position).getIsbusList().isEmpty()){
                textView_IsBus.setText("没有车辆正在运营");
            }else {
                textView_IsBus.setText("有车：");
                for(String string:list.get(position).getIsbusList()){
                    textView_IsBus.append(string+";");
                }
            }
        }

        return convertView;
    }
}
