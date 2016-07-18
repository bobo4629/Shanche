package bobo.shanche.myAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thinkcool.circletextimageview.CircleTextImageView;

import java.util.List;

import bobo.shanche.R;
import bobo.shanche.jsonDo.BusLine;

/**
 * Created by bobo1 on 2016/7/9.
 */
public class BusLineAdapter extends BaseAdapter {
    private Context context;
    private List<BusLine> list;
    private LayoutInflater layoutInflater;
    public BusLineAdapter(Context context, List<BusLine> list) {
        this.context =context;
        this.list=list;
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
        if(convertView==null)
            if(list.get(position).getLineName().length()>5){
                convertView = layoutInflater.inflate(R.layout.search_item_longtext,null);
            }else {
                convertView = layoutInflater.inflate(R.layout.search_item,null);
            }
        CircleTextImageView circleTextImageView_LineName = (CircleTextImageView)convertView.findViewById(R.id.view_LineName);
        TextView textView_Site = (TextView)convertView.findViewById(R.id.textView_Site);
        TextView textView_Time = (TextView)convertView.findViewById(R.id.textView_Time);
        TextView textView_Fare = (TextView)convertView.findViewById(R.id.textView_Fare);

        circleTextImageView_LineName.setText(list.get(position).getLineName());
        textView_Time.setText(list.get(position).getDownStartTime()+" - "+list.get(position).getDownEndTime());
        textView_Fare.setText("票价:"+list.get(position).getFare()+"元");

        textView_Site.setText(list.get(position).getStartEndSites().get(0).getSiteName()+" - "+ list.get(position).getStartEndSites().get(1).getSiteName());

        return convertView;
    }
}
