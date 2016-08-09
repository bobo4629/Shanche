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
import bobo.shanche.myAdapter.BusLineAdapter;

/**
 * Created by bobo1 on 2016/7/9.
 */
public class SearchActivity extends AppCompatActivity {

    private String busLineName;
    private String postBack;

    private List<String> id = new ArrayList<String>();
    private List<String> lineName= new ArrayList<String>();
    private List<String> downStartTime= new ArrayList<String>();
    private List<String> downEndTime= new ArrayList<String>();
    private List<BusLine> lists;

    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
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
                                    content.put("lineName",busLineName);
                                    postBack = doPost.post("http://183.232.33.171/IntelligentBusService.asmx/GetLines", content);
                                    doJson(postBack);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            BusLineAdapter adapter = new BusLineAdapter(SearchActivity.this,lists);
                                            listView.setAdapter(adapter);
                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    BusLine line = lists.get(position);
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

    private void doJson(String json) {
        lists = new Gson().fromJson(json, new TypeToken<List<BusLine>>() {}.getType());
        for (int i=0;i<lists.size();i++)
        {
            lineName.add(lists.get(i).getLineName());
            id.add(lists.get(i).getId());
            downEndTime.add(lists.get(i).getDownEndTime());
            downStartTime.add(lists.get(i).getDownStartTime());
        }
    }
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
}

