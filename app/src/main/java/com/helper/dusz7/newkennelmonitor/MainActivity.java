package com.helper.dusz7.newkennelmonitor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.helper.dusz7.newkennelmonitor.adapter.KennelAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getName();

    RefreshLayout refreshLayout;

    private List<KennelState> kennelList = new ArrayList<>();

    private RecyclerView recyclerView;
    KennelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bmob.initialize(this, getResources().getString(R.string.appKey));

        updateKennels();

//        kennelList = DataSupport.findAll(Kennel.class);
//        if (kennelList.size() == 0) {
//            initFakeKennels();
//        }

        recyclerView = findViewById(R.id.kennel_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new KennelAdapter(kennelList);
        recyclerView.setAdapter(adapter);

        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(1000/*,false*/);//传入false表示刷新失败
                updateKennels();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });
    }

    private void updateKennels() {
        BmobQuery<KennelState> bmobQuery = new BmobQuery<KennelState>();
        bmobQuery.findObjects(new FindListener<KennelState>() {
            @Override
            public void done(List<KennelState> list, BmobException e) {
                if (null == e) {
                    kennelList.clear();
                    kennelList.addAll(list);
                    Log.d(TAG, "size: " + kennelList.size());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "init error: " + e);
                }
            }
        });
    }

//    private void initFakeKennels() {
//        Kennel kennel1 = new Kennel("#0001");
//        kennel1.setState(Kennel.STATE_NORMAL);
//        kennel1.save();
//        kennelList.add(kennel1);
//        Kennel kennel2 = new Kennel("#0002");
//        kennel2.setState(Kennel.STATE_NORMAL);
//        kennel2.save();
//        kennelList.add(kennel2);
//        Kennel kennel3 = new Kennel("#0003");
//        kennel3.setState(Kennel.STATE_ERROR);
//        kennel3.save();
//        kennelList.add(kennel3);
//        Kennel kennel4 = new Kennel("#0004");
//        kennel4.setState(Kennel.STATE_OPERATION);
//        kennel4.save();
//        kennelList.add(kennel4);
//        Kennel kennel5 = new Kennel("#0005");
//        kennel5.setState(Kennel.STATE_NORMAL);
//        kennel5.save();
//        kennelList.add(kennel5);
//        Kennel kennel6 = new Kennel("#0006");
//        kennel6.setState(Kennel.STATE_OPERATION);
//        kennel6.save();
//        kennelList.add(kennel6);
//        Kennel kennel7 = new Kennel("#0007");
//        kennel7.setState(Kennel.STATE_NORMAL);
//        kennel7.save();
//        kennelList.add(kennel7);
//    }
}
