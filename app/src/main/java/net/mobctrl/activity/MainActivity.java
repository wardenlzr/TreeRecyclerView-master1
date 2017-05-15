package net.mobctrl.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.mobctrl.adapter.RecyclerAdapter;
import net.mobctrl.interfaces.OnScrollToListener;
import net.mobctrl.model.ItemData;
import net.mobctrl.treerecyclerview.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "BG";
    private RecyclerView recyclerView;
    private EditText etName;
    private Button btQuery;
    private String pathAllUser = "http://114.55.255.170:6223/UserService.asmx/GetUnitUserAllList?";
    private String pathJigou = "http://10.32.1.120:2099/TaskService.asmx/GetJiGouList?";
    private RecyclerAdapter myAdapter;
    private Context mContext;

    private LinearLayoutManager linearLayoutManager;
    private List<ItemData> listUsers;
    private List<ItemData> listJiGou;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
        initData();
    }

    private void initData() {
//        listJiGou = myAdapter.getChildrenByParent("/", 0);
//        myAdapter.addAll(listJiGou, 0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getUsers();
                getJiHou(0);

            }
        }).start();
    }

    private void getJiHou(int biaoshi) {
        listJiGou = new ArrayList<>();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("BiaoShi", biaoshi);
        String result = requestGet(pathJigou, hashMap);
        try {
            JSONObject jsonObject = new JSONObject(result);
            String code = jsonObject.getString("Code");
            String message = jsonObject.getString("Message");
            Log.i(TAG, code + " message:" + message);
            JSONArray array = jsonObject.getJSONArray("Message");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject1 = array.getJSONObject(i);
                int jiLuLeiXing = jsonObject1.getInt("JiLuLeiXing");
                int Level = jsonObject1.getInt("Level");
                ItemData itemData = new ItemData(jiLuLeiXing == 2 ? ItemData.ITEM_TYPE_CHILD:ItemData.ITEM_TYPE_PARENT,
                        jsonObject1.getString("MingCheng"),
                        jsonObject1.getString("BiaoShi"),
                        UUID.randomUUID().toString(), Level, null);
                listJiGou.add(itemData);
            }
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    myAdapter.addAll(listJiGou, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_query:
                search();
                /**
                 *   运行到主线程 //Looper.getMainLooper()
                    new Handler(mContext.getMainLooper()).post(new Runnable() {
                   @Override
                   public void run() {
                   myAdapter.clear();
                   myAdapter.addAll(listUsers,0);
                   }
                   });
                 */
                break;
        }
    }

    private void getUsers() {
        listUsers = new ArrayList<>();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("unitId", "400000481");
        String result = requestGet(pathAllUser, hashMap);
        try {
            JSONObject jsonObject = new JSONObject(result);
            String code = jsonObject.getString("Code");
            String message = jsonObject.getString("Message");
            Log.i(TAG, code + " message:" + message);
            JSONArray array = jsonObject.getJSONArray("Message");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject1 = array.getJSONObject(i);
                ItemData itemData = new ItemData(ItemData.ITEM_TYPE_CHILD, jsonObject1.getString("name"), jsonObject1.getString("biaoshi"), UUID
                        .randomUUID().toString(), 0, null);
                listUsers.add(itemData);
//                UserInfo userInfo = new UserInfo(jsonObject1.getString("name"),jsonObject1.getString("biaoshi"),jsonObject1.getString("fubiaoshi"));
//                userInfoList.add(userInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        etName = (EditText) findViewById(R.id.et_name);
        btQuery = (Button) findViewById(R.id.bt_query);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.getItemAnimator().setAddDuration(100);
        recyclerView.getItemAnimator().setRemoveDuration(100);
        recyclerView.getItemAnimator().setMoveDuration(200);
        recyclerView.getItemAnimator().setChangeDuration(100);

        myAdapter = new RecyclerAdapter(this);
        recyclerView.setAdapter(myAdapter);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btQuery.setOnClickListener(this);
        myAdapter.setOnScrollToListener(new OnScrollToListener() {

            @Override
            public void scrollTo(int position) {
                recyclerView.scrollToPosition(position);
            }
        });
    }

    private void search() {
        String name = etName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            myAdapter.clear();
            myAdapter.addAll(listJiGou, 0);
        } else {
            myAdapter.clear();
            List<ItemData> list = new ArrayList<>();
            for (int i = 0; i < getUserList().size(); i++) {
                ItemData itemData = getUserList().get(i);
                if (itemData.getText().contains(name)){
                    list.add(itemData);
                }
            }
            myAdapter.addAll(list, 0);
        }
    }

    public String requestGet(String baseUrl, HashMap<String, Object> paramsMap) {
        String result = "";
        try {
            StringBuilder tempParams = new StringBuilder();
            tempParams.append(String.format("%s=%s", "jsoncors", "jsoncors"));
            for (String key : paramsMap.keySet()) {
                tempParams.append("&");
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key).toString(), "utf-8")));
            }//jsoncors=jsoncors&unitId=400000481

            String requestUrl = baseUrl + tempParams.toString();
            Log.e(TAG, "Get方式请求，requestUrl--->" + requestUrl);
            // 新建一个URL对象
            URL url = new URL(requestUrl);
            // 打开一个HttpURLConnection连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            // 设置连接主机超时时间
            urlConn.setConnectTimeout(10 * 1000);
            //设置从主机读取数据超时
            urlConn.setReadTimeout(10 * 1000);
            // 设置是否使用缓存  默认是true
            urlConn.setUseCaches(true);
            // 设置为Post请求
            urlConn.setRequestMethod("GET");
            //urlConn设置请求头信息
            //设置请求中的媒体类型信息。
            //urlConn.setRequestProperty("Content-Type", "application/json");
            //设置客户端与服务连接类型
            //urlConn.addRequestProperty("Connection", "Keep-Alive");
            // 开始连接
            urlConn.connect();
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                // 获取返回的数据
                result = streamToString(urlConn.getInputStream());
                Log.e(TAG, "Get方式请求成功，result--->" + result);
            } else {
                Log.e(TAG, "Get方式请求失败");
            }
            // 关闭连接
            urlConn.disconnect();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return e.toString();
        }
        return result;
    }

    /**
     * 将输入流转换成字符串
     *
     * @param is 从网络获取的输入流
     * @return
     */
    public String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    public List<ItemData> getUserList() {
        while (listUsers == null || listUsers.size() == 0) {
            SystemClock.sleep(500);
        }
        return listUsers;
    }
    private void log(String str) {
        Log.e(TAG, str);
    }
}
