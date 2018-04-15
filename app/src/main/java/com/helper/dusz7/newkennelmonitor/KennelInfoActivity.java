package com.helper.dusz7.newkennelmonitor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class KennelInfoActivity extends AppCompatActivity {

    private final String TAG = KennelInfoActivity.class.getName();

    final String[] quarters = new String[] {"-25min", "-20min", "-15min", "-10min", "-5min", "now" };

    public static final String KENNEL_ID = "KENNEL_ID";

    private ImageView editNickname;
    private TextView kennelIdTextView;

    private TextView temperatureTextView;
    private TextView humidityTextView;

    private LineChart lineChart;
    RefreshLayout refreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kennel_info);

        Intent intent = getIntent();
        final String kenenlId = intent.getStringExtra(KENNEL_ID);

        getBmobData(kenenlId);

        Kennel kennelNow = new Kennel(kenenlId);

        List<Kennel> kennelsNow = DataSupport.where("kennelId = ?", kenenlId).find(Kennel.class);
        if (kennelsNow.size() == 0) {
            kennelNow.save();
        } else {
            kennelNow = kennelsNow.get(0);
        }

        kennelIdTextView = findViewById(R.id.id_textview);
        String kennelShowId = kenenlId;
        if (kennelNow.getNickname() != null && !kennelNow.getNickname().equals("")) {
            kennelShowId = kennelNow.getNickname() + " - " + kenenlId;
        }
        kennelIdTextView.setText(kennelShowId);

        editNickname = (ImageView) findViewById(R.id.edit_imageview);
        editNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(KennelInfoActivity.this);
                dialog.setTitle("Edit this Kennel's nickname");

                final EditText showNickname = new EditText(KennelInfoActivity.this);
                List<Kennel> kennels = DataSupport.where("kennelId = ?", kenenlId).find(Kennel.class);
                if (kennels.size() == 0) {
                    showNickname.setHint("kennel's nickname");
                } else {
                    if (kennels.get(0).getNickname() == null || kennels.get(0).getNickname().equals("")) {
                        showNickname.setHint("kennel's nickname");
                    } else {
                        showNickname.setHint(kennels.get(0).getNickname());
                    }
                }
                dialog.setView(showNickname);

                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (showNickname.getText().toString().length() > 0) {
                            Kennel kennel1 = new Kennel();
                            kennel1.setNickname(showNickname.getText().toString());
                            kennel1.updateAll("kennelId = ?", kenenlId);
                            kennelIdTextView.setText(showNickname.getText().toString() + " - " + kenenlId);
                        }
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                dialog.show();
            }
        });

        temperatureTextView = findViewById(R.id.sensor1_t_textview);
        humidityTextView = findViewById(R.id.sensor1_h_textview);

        lineChart = findViewById(R.id.lc_history_data);

        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(1000/*,false*/);//传入false表示刷新失败
                getBmobData(kenenlId);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(0/*,false*/);//传入false表示加载失败
            }
        });
    }

    private void getBmobData(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("kennelId", id);
                    Headers headers = new Headers.Builder()
                            .add("X-Bmob-Application-Id", getResources().getString(R.string.appKey))
                            .add("X-Bmob-REST-API-Key", getResources().getString(R.string.restKey))
                            .add("Content-Type", "application/json")
                            .build();
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .headers(headers)
                            .url(getResources().getString(R.string.bmob_class_url) + "?where=" + jsonObject.toString())
                            .build();
                    Response response = client.newCall(request).execute();
                    JSONObject jsonRes = new JSONObject(response.body().string());
                    JSONArray jsonRes1 = jsonRes.getJSONArray("results");
                    jsonRes = jsonRes1.getJSONObject(0);
                    String objectId = jsonRes.getString("objectId");
                    Log.d(TAG, objectId);

                    BmobQuery<KennelState> bmobQuery = new BmobQuery<KennelState>();
                    bmobQuery.getObject(objectId, new QueryListener<KennelState>() {
                        @Override
                        public void done(KennelState object, BmobException e) {
                            if (e == null) {
                                Log.d(TAG, "查询成功");
                                updateUI(object);
                            } else {
                                Log.d(TAG, "查询失败：" + e.getMessage());
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void updateUI(final KennelState kennelState) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                temperatureTextView.setText(kennelState.getTemperature6() + " C");
                humidityTextView.setText(kennelState.getHumidity6() + " %");

                List<Entry> tEntries = new ArrayList<Entry>();
                tEntries.add(new Entry(0f, kennelState.getTemperature1()));
                tEntries.add(new Entry(1f, kennelState.getTemperature2()));
                tEntries.add(new Entry(2f, kennelState.getTemperature3()));
                tEntries.add(new Entry(3f, kennelState.getTemperature4()));
                tEntries.add(new Entry(4f, kennelState.getTemperature5()));
                tEntries.add(new Entry(5f, kennelState.getTemperature6()));

                List<Entry> hEntries = new ArrayList<Entry>();
                hEntries.add(new Entry(0f, kennelState.getHumidity1()));
                hEntries.add(new Entry(1f, kennelState.getHumidity2()));
                hEntries.add(new Entry(2f, kennelState.getHumidity3()));
                hEntries.add(new Entry(3f, kennelState.getHumidity4()));
                hEntries.add(new Entry(4f, kennelState.getHumidity5()));
                hEntries.add(new Entry(5f, kennelState.getHumidity6()));

                LineDataSet tDataSet = new LineDataSet(tEntries, "Temperature (C)");
                tDataSet.setColor(Color.RED);
                tDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

                LineDataSet hDataSet = new LineDataSet(hEntries, "Humidity (%)");
                hDataSet.setColor(Color.CYAN);
                hDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

                LineData lineData = new LineData(tDataSet, hDataSet);

                IAxisValueFormatter formatter = new IAxisValueFormatter() {

                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return quarters[(int) value];
                    }

                    // we don't draw numbers, so no decimal digits needed
//                    @Override
//                    public int getDecimalDigits() {  return 0; }
                };

                lineChart.setData(lineData);
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                xAxis.setValueFormatter(formatter);
                lineChart.invalidate();
            }
        });
    }
}
