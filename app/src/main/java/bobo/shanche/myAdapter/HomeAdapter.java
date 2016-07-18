package bobo.shanche.myAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import bobo.shanche.R;
import bobo.shanche.jsonDo.BusSite;

/**
 * Created by bobo1 on 2016/7/8.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ListItemViewHolder>{

    public void setList(List<BusSite> list) {
        this.list = list;
    }

    private List<BusSite> list;
    public HomeAdapter(List<BusSite> busSiteList) {
        list=busSiteList;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.detial_item,parent,false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {
        holder.textView_busStop.setText(list.get(position).getSiteName());
        if(list.get(position).getBusList()!=null){
            if(list.get(position).getBusList().isEmpty()){
                holder.imageView.setImageResource(R.drawable.nobus);
            }else {
                holder.imageView.setImageResource(R.drawable.bus);
            }
        }else {
            holder.imageView.setImageResource(R.drawable.nobus);
        }

    }

    @Override
    public int getItemCount() {
        return list.isEmpty()?0:list.size();
    }
    public class ListItemViewHolder extends RecyclerView.ViewHolder{
        TextView textView_busStop;
        ImageView imageView;
        public ListItemViewHolder(View itemView){
            super(itemView);
            textView_busStop = (TextView)itemView.findViewById(R.id.textView_BusStop);
            imageView = (ImageView)itemView.findViewById(R.id.imageView);
        }
    }

}

