package bobo.shanche;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bobo.shanche.Dosth.DoNet;
import bobo.shanche.dbDo.DbHelper;
import bobo.shanche.jsonDo.BusSite;
import bobo.shanche.jsonDo.Station;
import bobo.shanche.myAdapter.HomeAdapter;

import com.pgyersdk.crash.PgyCrashManager;
/**
 * Created by bobo1 on 2016/7/8.
 */
public class DetialActivity extends AppCompatActivity {
    final private int UP=1;
    final private int DOWN=2;
    private int upDown;

    private String lineName;

    private String id;

    private HomeAdapter mAdapter;
    private Map<String,String> content;

    private List<BusSite> busSites = new ArrayList<>();
    private Station station;

    private TextView textView_StartSite;
    private TextView textView_EndSite;
    private CoordinatorLayout coordinatorLayout;
    private MenuItem item;
    private int neworrefresh;

    final static private String DbTable_R = "record";
    final static private String DbTable_C = "collection";
    final static private String DbTable_S = "settings";

    //自动刷新相关
    private long delayTime=-1;
    Handler handler = new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            upDownChange(upDown,2);
            handler.postDelayed(this, delayTime);
        }
    };

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detial, menu);
        item = menu.findItem(R.id.toolbar_Collection);
        return true;
    }


    @Override
    protected void onDestroy() {

        DbHelper db = new DbHelper(this);
        db.delete(DbTable_R,lineName);
        db.add(DbTable_R,station);
        db.close();
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
        setContentView(R.layout.acticity_detial);

        PgyCrashManager.register(this);

        Intent intent =getIntent();
        lineName = intent.getStringExtra("lineName");
        id = intent.getStringExtra("id");
        upDown = intent.getIntExtra("upDown",UP);
        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.RecyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        textView_StartSite = (TextView)findViewById(R.id.textView_Head_StartSite);
        textView_EndSite = (TextView)findViewById(R.id.textView_head_EndSite);
        ImageButton imageButton_Change = (ImageButton)findViewById(R.id.imageButton);


        imageButton_Change.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (upDown){
                                case UP:
                                    upDownChange(DOWN,2);
                                    break;
                                case DOWN:
                                    upDownChange(UP,2);
                                    break;

                }

            }
        });

        upDownChange(upDown,1);
        //设置顶栏 返回按钮 标题
        Toolbar mToolBar = (Toolbar)findViewById(R.id.toolbar_detial);
        mToolBar.setTitle(lineName);
        setSupportActionBar(mToolBar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_Detial);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upDownChange(upDown,2);
             }
        });




        mToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                collectionChange(station);
                return true;
            }
        });


    }

    private void collectionChange(Station station){
        DbHelper db = new DbHelper(this);
        if(collectionCheck()){
            db.deleteUpDown(DbTable_C,lineName,upDown);
            db.close();
            item.setIcon(R.drawable.nocollection);
        }else {
            db.add(DbTable_C,station);
            db.close();
            item.setIcon(R.drawable.collection);
        }
    }
    private boolean collectionCheck(){

        DbHelper db = new DbHelper(this);

        Cursor cursor =db.search(DbTable_C,"lineName='"+lineName+"'");
        if(!cursor.moveToFirst()){
            cursor.close();
            item.setIcon(R.drawable.nocollection);
            return false;
        }else {
            //0个就没有 一个就判断 两个是都收藏了hhh
            if(cursor.getCount()==1){
                cursor.moveToFirst();
                if(cursor.getInt(5)==upDown){
                    cursor.close();
                    db.close();
                    item.setIcon(R.drawable.collection);
                    return true;
                }else {
                    cursor.close();
                    db.close();
                    item.setIcon(R.drawable.nocollection);
                    return false;
                }
            }
            else {
                cursor.close();
                db.close();
                item.setIcon(R.drawable.collection);
                return true;

            }
        }

    }


    private void upDownChange(final int upOrDown, int neworrefresh){//1 new 2 refresh
        if(!isNetworkConnected()){
            coordinatorLayout=(CoordinatorLayout)findViewById(R.id.CoordinatorLayout);
            Snackbar.make(coordinatorLayout,"暂无网络连接，请稍后再试",Snackbar.LENGTH_LONG).show();
        }else {
            this.neworrefresh = neworrefresh;
            content = new HashMap<String, String>();
            content.put("lineId", id);
            content.put("upDown", Integer.toString(upOrDown));
            content.put("siteId", "");
            upDown = upOrDown;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DoNet doPost = new DoNet();
                    try {
                        String back = doPost.post("http://183.232.33.171/IntelligentBusService.asmx/GetStationLicense", content);
                        station = doJson(back);
                        busSites = station.getList();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView_StartSite.setText(busSites.get(0).getSiteName());
                                textView_EndSite.setText(busSites.get(busSites.size() - 1).getSiteName());
                                //RecyclerView的配置
                                if(DetialActivity.this.neworrefresh==1){
                                    RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
                                    mRecyclerView.setLayoutManager(new LinearLayoutManager(DetialActivity.this));
                                    mAdapter = new HomeAdapter(busSites);
                                    mRecyclerView.setAdapter(mAdapter);
                                    mAdapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
                                        @Override
                                        public void onClick(View view, int position) {
                                            Intent intent = new Intent(DetialActivity.this,RemindActivity.class);
                                            intent.putExtra("id",busSites.get(position).getLineId());
                                            intent.putExtra("upDown",upDown);
                                            intent.putExtra("siteNow",busSites.get(position).getSiteName());
                                            intent.putExtra("position",position);
                                            startActivity(intent);
                                        }
                                    });
                                }else {
                                    mAdapter.setList(busSites);
                                        for(int i=0;i<mAdapter.getItemCount();i++){
                                            mAdapter.notifyItemChanged(i);
                                    }
                                }
                                collectionCheck();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.removeCallbacks(runnable);
                    String time =PreferenceManager.getDefaultSharedPreferences(DetialActivity.this).getString("refresh","10000");
                    delayTime= Long.parseLong(time);
                    handler.postDelayed(runnable,delayTime);

                }


            }).start();



        }
    }

    public Station doJson(String json){
        Station Station = new Gson().fromJson(json,new TypeToken<Station>(){}.getType());
        return Station;
    }
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }


}
