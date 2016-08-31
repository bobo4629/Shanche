package bobo.shanche;


import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bobo.shanche.Dosth.DoNet;
import bobo.shanche.jsonDo.BusSite;
import bobo.shanche.jsonDo.Station;
import bobo.shanche.myAdapter.HomeAdapter;
import bobo.shanche.myAdapter.remindAdapter;

import com.pgyersdk.crash.PgyCrashManager;
/**
 * Created by bobo1 on 2016/7/24.
 */

public class RemindActivity extends AppCompatActivity {

    private HomeAdapter mAdapter;
    private Map<String,String> content;
    private List<BusSite> busSites = new ArrayList<>();
    private Station station;
    final private int UP=1;
    final private int DOWN=2;
    private int upDown;
    private String lineName;
    private String id;
    private int neworrefresh;
    private TextView textView_NestestBus;
    private TextView textView_NextNestestBus;
    private String siteNow;
    private int position;
    private String nestestBus;
    private int remindWay;
    private int nestestBusPosition;
    private Vibrator vibrator;
    private SharedPreferences sharedPreferences;


    //自动刷新相关
    private long delayTime=5201314;
    Handler handler = new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            upDownChange(2);
            handler.postDelayed(this, delayTime);
        }
    };
    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    protected void onDestroy() {
        PgyCrashManager.unregister();
        try {
            handler.removeCallbacks(runnable);
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind);

        PgyCrashManager.register(this);

        Toolbar mToolBar = (Toolbar)findViewById(R.id.toolbar_remind);
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        id=intent.getStringExtra("id");
        upDown = intent.getIntExtra("upDown",1);
        siteNow = intent.getStringExtra("siteNow");
        position = intent.getIntExtra("position",0);

        TextView textView_SiteNow = (TextView)findViewById(R.id.textView_SiteNow);
        textView_NestestBus = (TextView)findViewById(R.id.textView_SiteNext);
        textView_SiteNow.setText("当前车站："+siteNow);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        final String[] strs = getResources().getStringArray(R.array.remindWay);
        Spinner mSpinner = (Spinner)findViewById(R.id.spinner_RemindWay);
        mSpinner.setAdapter(new remindAdapter(this,strs));
        remindWay=Integer.parseInt(sharedPreferences.getString("remind","0"));
        mSpinner.setSelection(remindWay);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("remind",String.valueOf(position));
                editor.commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton_remind);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upDownChange(2);
            }
        });

        upDownChange(1);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    private void upDownChange(int neworrefresh){//1 new 2 refresh

            this.neworrefresh = neworrefresh;
            content = new HashMap<String, String>();
            content.put("lineId", id);
            content.put("upDown", Integer.toString(upDown));
            content.put("siteId", "");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DoNet doPost = new DoNet();
                    try {
                        String back = doPost.post("http://183.232.33.171/IntelligentBusService.asmx/GetStationLicense", content);
                        station = doJson(back);
                        busSites = station.getList();

                        nestestBus=null;
                        for(nestestBusPosition=position;nestestBusPosition>=0;nestestBusPosition--){
                            BusSite busSite = busSites.get(nestestBusPosition);
                            if(busSite.getBusList()!=null)
                                if(!busSite.getBusList().isEmpty()){
                                    nestestBus=busSite.getSiteName();
                                    break;
                                }
                        }
                        if(nestestBus==null)
                            nestestBus="尚未发车";
                        remindWay=Integer.parseInt(sharedPreferences.getString("remind","0"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int vTime=200 ;
                                boolean isCar =false;
                                if(position - nestestBusPosition<=1){
                                    vTime=200;
                                    isCar=true;
                                }else if(position == nestestBusPosition) {
                                    vTime = 500;
                                    isCar=true;
                                }

                                if(isCar)
                                    switch (remindWay){
                                        case 0:
                                            vibrator.vibrate(vTime);
                                            break;
                                        case 1:
                                            try {
                                                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                                r.play();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        case 2:
                                            vibrator.vibrate(vTime);
                                            try {
                                                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                                r.play();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                    }

                                textView_NestestBus.setText("最近公交："+nestestBus);
                                //RecyclerView的配置
                                if (RemindActivity.this.neworrefresh == 1) {
                                    RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView_Remind);
                                    mRecyclerView.setLayoutManager(new LinearLayoutManager(RemindActivity.this));
                                    mAdapter = new HomeAdapter(busSites);
                                    mRecyclerView.setAdapter(mAdapter);
                                } else {
                                    mAdapter.setList(busSites);
                                    for (int i = 0; i < mAdapter.getItemCount(); i++) {
                                        mAdapter.notifyItemChanged(i);
                                    }
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.removeCallbacks(runnable);
                    String time =PreferenceManager.getDefaultSharedPreferences(RemindActivity.this).getString("refresh","10000");
                    delayTime= Long.parseLong(time);
                    handler.postDelayed(runnable,delayTime);

                }
            }).start();
    }

    public Station doJson(String json){
        Station Station = new Gson().fromJson(json,new TypeToken<Station>(){}.getType());
        return Station;
    }
}