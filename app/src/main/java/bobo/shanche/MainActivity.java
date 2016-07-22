package bobo.shanche;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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

    private Location location;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String provider;

    private static Boolean isExit = false;
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TCAgent.LOG_ON = true;
        TCAgent.init(this, "AE41A509E5E28A3312D6794C567F16E0", "same");
        TCAgent.setReportUncaughtExceptions(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mainView = (CoordinatorLayout) findViewById(R.id.MainView);
        //配置tabLayout
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTabLayout = (TabLayout) findViewById(R.id.tabLaout);
        PagerAdapter pagerAdapter = new bobo.shanche.myAdapter.PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onDestroy() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try{
            locationManager.removeUpdates(locationListener);
        }catch (Exception e){

        }

        super.onDestroy();
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
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initCollection() {
        if (!isNetworkConnected()) {
            Snackbar.make(mainView, "暂无网络连接，请稍后再试", Snackbar.LENGTH_LONG).show();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DoNet doNet = new DoNet();
                    DbHelper db = new DbHelper(MainActivity.this);
                    Cursor cursor = db.search(DbTable_C, null);
                    collectionList.clear();

                    if (cursor.moveToFirst()) {
                        do {
                            String id = cursor.getString(1);
                            String lineName = cursor.getString(2);
                            String startSite = cursor.getString(3);
                            String endSite = cursor.getString(4);
                            int upDown = cursor.getInt(5);
                            MainBus mainBus = new MainBus();
                            mainBus.setBusID(id);
                            mainBus.setLineName(lineName);
                            mainBus.setStartSite(startSite);
                            mainBus.setEndSite(endSite);
                            mainBus.setUpDown(upDown);

                            Map<String, String> content = new HashMap<String, String>();
                            content.put("lineId", id);
                            content.put("upDown", Integer.toString(upDown));
                            content.put("siteId", "");
                            try {
                                String back = doNet.post("http://183.232.33.171/IntelligentBusService.asmx/GetStationLicense", content);
                                List<BusSite> busSiteList = doJson(back);
                                List<BusSite> isBusStopList = new ArrayList<BusSite>();
                                List<String> isBusList  = new ArrayList<String>();
                                for (BusSite object : busSiteList) {
                                    if (object.getBusList() != null) {
                                        if (!object.getBusList().isEmpty()) {
                                            isBusList .add(object.getSiteName());
                                            isBusStopList.add(object);
                                        }
                                    }
                                }
                                mainBus.setIsbusList(isBusList);
                                mainBus.setBusSiteList(isBusStopList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            collectionList.add(mainBus);
                        } while (cursor.moveToNext());


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final ListView listView_Collection = (ListView) findViewById(R.id.listView_Collection);
                                collectionAdapter = new MainAdapter(MainActivity.this, collectionList);
                                listView_Collection.setAdapter(collectionAdapter);
                                listView_Collection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        MainBus mainBus = (MainBus) listView_Collection.getItemAtPosition(position);
                                        Intent intent = new Intent(MainActivity.this, DetialActivity.class);
                                        intent.putExtra("upDown", mainBus.getUpDown());
                                        intent.putExtra("id", mainBus.getBusID());
                                        intent.putExtra("lineName", mainBus.getLineName());
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

    public void initRecord() {
        if (!isNetworkConnected()) {
            Snackbar.make(mainView, "暂无网络连接，请稍后再试", Snackbar.LENGTH_LONG).show();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DoNet doNet = new DoNet();
                    DbHelper db = new DbHelper(MainActivity.this);
                    Cursor cursor = db.search(DbTable_R, null);
                    recordList.clear();

                    if (cursor.moveToFirst()) {
                        do {
                            String id = cursor.getString(1);
                            String lineName = cursor.getString(2);
                            String startSite = cursor.getString(3);
                            String endSite = cursor.getString(4);
                            int upDown = cursor.getInt(5);

                            MainBus mainBus = new MainBus();
                            mainBus.setBusID(id);
                            mainBus.setLineName(lineName);
                            mainBus.setStartSite(startSite);
                            mainBus.setEndSite(endSite);
                            mainBus.setUpDown(upDown);

                            Map<String, String> content = new HashMap<String, String>();
                            content.put("lineId", id);
                            content.put("upDown", Integer.toString(upDown));
                            content.put("siteId", "");
                            try {
                                String back = doNet.post("http://183.232.33.171/IntelligentBusService.asmx/GetStationLicense", content);
                                List<BusSite> busSiteList = doJson(back);
                                List<String> isBusList = new ArrayList<String>();
                                List<BusSite> isBusStopList = new ArrayList<BusSite>();
                                for (BusSite object : busSiteList) {
                                    if (object.getBusList() != null) {
                                        if (!object.getBusList().isEmpty()) {
                                            isBusList.add(object.getSiteName());
                                            isBusStopList.add(object);
                                        }
                                    }
                                }
                                mainBus.setBusSiteList(isBusStopList);
                                mainBus.setIsbusList(isBusList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            recordList.add(mainBus);
                        } while (cursor.moveToNext());
                        db.close();
                        cursor.close();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final ListView listView_Record = (ListView) findViewById(R.id.listView_Record);
                                recordAdapter = new MainAdapter(MainActivity.this, recordList);
                                listView_Record.setAdapter(recordAdapter);
                                listView_Record.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        MainBus mainBus = (MainBus) listView_Record.getItemAtPosition(position);
                                        Intent intent = new Intent(MainActivity.this, DetialActivity.class);
                                        intent.putExtra("id", mainBus.getBusID());
                                        intent.putExtra("lineName", mainBus.getLineName());
                                        intent.putExtra("upDown", mainBus.getUpDown());
                                        startActivity(intent);
                                    }
                                });
                                initLocation();
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
        Station Station = new Gson().fromJson(json, new TypeToken<Station>() {
        }.getType());
        return Station.getList();
    }

    public void initLocation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //定位
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    //无权
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(mainView, "无定位权限，无法查看最近车辆。", Snackbar.LENGTH_LONG).show();
                        }
                    });
                    return;

                }
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                List<String> locationList = locationManager.getProviders(true);

                if (locationList.contains(LocationManager.GPS_PROVIDER)) {
                    provider = LocationManager.GPS_PROVIDER;
                } else if (locationList.contains(LocationManager.NETWORK_PROVIDER)) {
                    provider = LocationManager.NETWORK_PROVIDER;
                } else {
                    //无可用
                      runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(mainView, "无可用定位服务，请检查是否打开定位服务。", Snackbar.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        MainActivity.this.location =location;
                        closeLocation();
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        }else {
                            if(provider!=null){
                            locationManager.requestLocationUpdates(provider, 3000, 1, locationListener);
                            }
                        }
                    }
                });
            }

        }).start();

    }
    private void closeLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try{
            locationManager.removeUpdates(locationListener);
            location = locationManager.getLastKnownLocation(provider);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recordAdapter.setLocation(location);
                    collectionAdapter.setLocation(location);
                    collectionAdapter.notifyDataSetChanged();
                    recordAdapter.notifyDataSetChanged();

                }
            });
        }catch (Exception e){
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            exitBy2Click(); //调用双击退出函数
        }
        return false;
    }
    /**
     * 双击退出函数
     */


    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
            System.exit(0);
        }
    }
}

