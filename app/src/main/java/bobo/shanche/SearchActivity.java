package bobo.shanche;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bobo.shanche.Dosth.DoNet;
import bobo.shanche.jsonDo.BusLine;
import bobo.shanche.jsonDo.BusSite;
import bobo.shanche.jsonDo.SiteLine;
import bobo.shanche.myAdapter.BusLineAdapter;
import bobo.shanche.myAdapter.SiteAdapter;

import com.pgyersdk.crash.PgyCrashManager;
/**
 * Created by bobo1 on 2016/7/9.
 */
public class SearchActivity extends AppCompatActivity {

    private String busLineName;
    private String postBack;


    private List<BusLine> lists_BusLine;
    private List<SiteLine> lists_BusSite;

    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        PgyCrashManager.register(this);

        final EditText editText = (EditText)findViewById(R.id.editText_search);
        final ListView listView = (ListView)findViewById(R.id.listView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                busLineName = editText.getText().toString();
                if(busLineName.isEmpty()==false){

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                DoNet doPost = new DoNet();
                                if(!isNetworkConnected()){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SearchActivity.this,"暂无网络连接，请稍后再试",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }else {
                                    Map<String,String> content = new HashMap<>();
                                    if(Character.isDigit(busLineName.charAt(0))){
                                        content.put("lineName",busLineName);
                                        postBack = doPost.post("http://183.232.33.171/IntelligentBusService.asmx/GetLines", content);
                                        doJson(postBack);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                BusLineAdapter adapter = new BusLineAdapter(SearchActivity.this,lists_BusLine);
                                                listView.setAdapter(adapter);
                                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        BusLine line = lists_BusLine.get(position);
                                                        Intent intent =new Intent(SearchActivity.this,DetialActivity.class);
                                                        intent.putExtra("id",line.getId());
                                                        intent.putExtra("lineName",line.getLineName());
                                                        intent.putExtra("startTime",line.getDownStartTime());
                                                        intent.putExtra("endTime",line.getDownEndTime());
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                        });
                                    }else{
                                        content.put("siteName",busLineName);
                                        postBack = doPost.post("http://183.232.33.171/IntelligentBusService.asmx/GetSites?op=GetSites ", content);
                                        doJson_Site(postBack);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                SiteAdapter adapter = new SiteAdapter(SearchActivity.this,lists_BusSite);
                                                listView.setAdapter(adapter);
                                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        SiteLine site = lists_BusSite.get(position);
                                                        Intent intent =new Intent(SearchActivity.this,SiteActivity.class);
                                                        intent.putExtra("siteId",site.getId());
                                                        intent.putExtra("siteName",site.getSiteName());
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                        });
                                    }

                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        PgyCrashManager.unregister();
        super.onDestroy();
    }

    private void doJson(String json) {
        lists_BusLine = new Gson().fromJson(json, new TypeToken<List<BusLine>>() {}.getType());
    }
    private void doJson_Site(String json){
        lists_BusSite = new Gson().fromJson(json, new TypeToken<List<SiteLine>>() {}.getType());
    }
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
}

