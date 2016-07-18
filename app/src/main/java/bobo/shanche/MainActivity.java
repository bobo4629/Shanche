package bobo.shanche;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;

import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bobo.shanche.Dosth.DoNet;
import bobo.shanche.Dosth.MainBus;
import bobo.shanche.dbDo.DbHelper;
import bobo.shanche.jsonDo.BusSite;
import bobo.shanche.jsonDo.Station;
import bobo.shanche.myAdapter.MainAdapter;

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private CoordinatorLayout mainView;
    private MainAdapter recordAdapter;
    private MainAdapter collectionAdapter;
    final static private String DbTable_C = "collection";
    final static private String DbTable_R = "record";
    private List<MainBus> collectionList = new ArrayList<>();
    private List<MainBus> recordList = new ArrayList<>();

    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TCAgent.LOG_ON=true;
        TCAgent.init(this, "AE41A509E5E28A3312D6794C567F16E0", "same");
        TCAgent.setReportUncaughtExceptions(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mainView = (CoordinatorLayout)findViewById(R.id.MainView);
        //配置tabLayout
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        mTabLayout = (TabLayout)findViewById(R.id.tabLaout);
        PagerAdapter pagerAdapter = new bobo.shanche.myAdapter.PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

        //定位
        //LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //LocationProvider gpsProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);//1.通过GPS定位，较精确，也比较耗电
        //LocationProvider netProvider = locationManager.getProvider(LocationManager.NETWORK_PROVIDER);//2.通过网络定位，对定位精度度不高或省点情况可考虑使用

    }

    @Override
    protected void onResume() {
        initCollection();
        initRecord();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initCollection(){
        if(!isNetworkConnected()){
            Snackbar.make(mainView,"暂无网络连接，请稍后再试",Snackbar.LENGTH_LONG).show();
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DoNet doNet = new DoNet();
                    DbHelper db = new DbHelper(MainActivity.this);
                    Cursor cursor = db.search(DbTable_C,null);
                    collectionList.clear();

                    if(cursor.moveToFirst()){
                        do{
                            String id = cursor.getString(1);
                            String lineName = cursor.getString(2);
                            String startSite = cursor.getString(3);
                            String endSite = cursor.getString(4);
                            int upDown = cursor.getInt(5);
                            MainBus mainBus =  new MainBus();
                            mainBus.setBusID(id);
                            mainBus.setLineName(lineName);
                            mainBus.setStartSite(startSite);
                            mainBus.setEndSite(endSite);
                            mainBus.setUpDown(upDown);

                            Map<String,String> content = new HashMap<String, String>();
                            content.put("lineId",id);
                            content.put("upDown",Integer.toString(upDown));
                            content.put("siteId","");
                            try{
                                String back = doNet.post("http://183.232.33.171/IntelligentBusService.asmx/GetStationLicense",content);
                                List<BusSite> busSiteList = doJson(back);
                                List<String> list = new ArrayList<String>();
                                for(BusSite object:busSiteList){
                                    if(object.getBusList()!=null){
                                        if (!object.getBusList().isEmpty()){
                                            list.add(object.getSiteName());
                                        }
                                    }
                                }
                                mainBus.setIsbusList(list);
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            collectionList.add(mainBus);
                        }while (cursor.moveToNext());


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final ListView listView_Collection = (ListView)findViewById(R.id.listView_Collection);
                                collectionAdapter = new MainAdapter(MainActivity.this,collectionList);
                                listView_Collection.setAdapter(collectionAdapter);
                                listView_Collection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        MainBus mainBus =(MainBus)listView_Collection.getItemAtPosition(position);
                                        Intent intent = new Intent(MainActivity.this,DetialActivity.class);
                                        intent.putExtra("upDown",mainBus.getUpDown());
                                        intent.putExtra("id",mainBus.getBusID());
                                        intent.putExtra("lineName",mainBus.getLineName());
                                        startActivity(intent);
                                    }
                                });
                            }
                        });
                    }
                }
            }).start();
        }
    }
    public void initRecord(){
        if(!isNetworkConnected()){
            Snackbar.make(mainView,"暂无网络连接，请稍后再试",Snackbar.LENGTH_LONG).show();
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DoNet doNet = new DoNet();
                    DbHelper db = new DbHelper(MainActivity.this);
                    Cursor cursor = db.search(DbTable_R,null);
                    recordList.clear();

                    if(cursor.moveToFirst()){
                        do{
                            String id = cursor.getString(1);
                            String lineName = cursor.getString(2);
                            String startSite = cursor.getString(3);
                            String endSite = cursor.getString(4);
                            int upDown = cursor.getInt(5);

                            MainBus mainBus =  new MainBus();
                            mainBus.setBusID(id);
                            mainBus.setLineName(lineName);
                            mainBus.setStartSite(startSite);
                            mainBus.setEndSite(endSite);
                            mainBus.setUpDown(upDown);

                            Map<String,String> content = new HashMap<String, String>();
                            content.put("lineId",id);
                            content.put("upDown",Integer.toString(upDown));
                            content.put("siteId","");
                            try{
                                String back = doNet.post("http://183.232.33.171/IntelligentBusService.asmx/GetStationLicense",content);
                                List<BusSite> busSiteList = doJson(back);
                                List<String> list = new ArrayList<String>();
                                for(BusSite object:busSiteList){
                                    if(object.getBusList()!=null){
                                        if (!object.getBusList().isEmpty()){
                                            list.add(object.getSiteName());
                                        }
                                    }
                                }
                                mainBus.setIsbusList(list);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            recordList.add(mainBus);
                        }while (cursor.moveToNext());
                        db.close();
                        cursor.close();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final ListView listView_Record = (ListView)findViewById(R.id.listView_Record);
                                recordAdapter = new MainAdapter(MainActivity.this,recordList);
                                listView_Record.setAdapter(recordAdapter);
                                listView_Record.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        MainBus mainBus =(MainBus)listView_Record.getItemAtPosition(position);
                                        Intent intent = new Intent(MainActivity.this,DetialActivity.class);
                                        intent.putExtra("id",mainBus.getBusID());
                                        intent.putExtra("lineName",mainBus.getLineName());
                                        intent.putExtra("upDown",mainBus.getUpDown());
                                        startActivity(intent);
                                    }
                                });
                            }
                        });
                    }
                }
            }).start();
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
    public List<BusSite> doJson(String json) {
        Station Station = new Gson().fromJson(json, new TypeToken<Station>() {}.getType());
        return Station.getList();
    }



}
