package com.example.gcls.prj_demo;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.*;
import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.megvii.facepp.sdk.Facepp;
//import com.example.gcls.prj_demo.R;
import com.example.gcls.prj_demo.OpenglActivity;
import com.example.gcls.prj_demo.util.ConUtil;
import com.example.gcls.prj_demo.util.DialogUtil;
import com.example.gcls.prj_demo.util.ICamera;
import com.example.gcls.prj_demo.util.Util;
import com.megvii.licencemanage.sdk.LicenseManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FaceActivity extends Activity {

    private Button debugBtn, cameraBtn, RecorderBtn, TopDetectBtn, PointNumberBtn, facePropertyBtn;
    private boolean isDebug = false, isBackCamera= false, isStartRecorder= false, isROIDetect= false, is106Points= false, isFaceProperty= false;
    private int min_face_size = 100;
    private int detection_interval = 25;
    private EditText faceSizeEdit, intervalEdit, resolutionEdit;
    private ArrayList<HashMap<String, Integer>> cameraSize;
    private RelativeLayout contentRel;
    private LinearLayout barLinear;
    private TextView WarrantyText;
    private ProgressBar WarrantyBar;
    private Button againWarrantyBtn;
    private RelativeLayout mListRel;
    private ListView mListView;
    private ListAdapter mListAdapter;
    private LayoutInflater mInflater;
    private boolean isShowListView;
    private HashMap<String, Integer> resolutionMap;
    private TextView versionText;
    private LicenseManager licenseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);
        System.out.println("fafa");
        init();
        System.out.println("fafa");
        initData();
        System.out.println("fafa");
        onClickListener();
        System.out.println("fafa");
        getCameraSizeList();
        System.out.println("fafa");
        network();
    }

    private void init() {
        licenseManager = new LicenseManager(this);
        mInflater = LayoutInflater.from(this);
        //versionText = (TextView) findViewById(R.id.loading_layout_versionText);
        contentRel = (RelativeLayout) findViewById(R.id.loading_layout_contentRel);
        barLinear = (LinearLayout) findViewById(R.id.loading_layout_barLinear);
        WarrantyText = (TextView) findViewById(R.id.loading_layout_WarrantyText);
        WarrantyBar = (ProgressBar) findViewById(R.id.loading_layout_WarrantyBar);
        againWarrantyBtn = (Button) findViewById(R.id.loading_layout_againWarrantyBtn);
        mListRel = (RelativeLayout) findViewById(R.id.loading_layout_listRel);
        mListView = (ListView) findViewById(R.id.loading_layout_list);
        mListView.setVerticalScrollBarEnabled(false);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);
        resolutionEdit = (EditText) findViewById(R.id.load_layout_resolutionEdit);
        if (resolutionMap == null)
            resolutionEdit.setText("640*480");

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(resolutionEdit.getWindowToken(), 0);
        resolutionEdit.setInputType(0);
        faceSizeEdit = (EditText) findViewById(R.id.load_layout_faceSizeEdit);
        intervalEdit = (EditText) findViewById(R.id.load_layout_intervalEdit);
        debugBtn = (Button) findViewById(R.id.load_layout_debugBtn);
        cameraBtn = (Button) findViewById(R.id.load_layout_cameraBtn);
        RecorderBtn = (Button) findViewById(R.id.load_layout_recorderBtn);
        TopDetectBtn = (Button) findViewById(R.id.load_layout_TopDetectBtn);
        PointNumberBtn = (Button) findViewById(R.id.load_layout_pointNumBtnBtn);
        facePropertyBtn = (Button) findViewById(R.id.load_layout_facePropertyBtn);
    }

    private void initData() {
        if (Util.API_KEY == null || Util.API_SECRET == null) {
            if (!ConUtil.isReadKey(this)) {
                DialogUtil mDialogUtil = new DialogUtil(this);
                mDialogUtil.showDialog("请填写API_KEY和API_SECRET");
            }
        }
    }

    private void network() {
        contentRel.setVisibility(View.GONE);
        barLinear.setVisibility(View.VISIBLE);
        againWarrantyBtn.setVisibility(View.GONE);
        WarrantyText.setText("正在联网授权中...");
        WarrantyBar.setVisibility(View.VISIBLE);
        licenseManager.setAuthTime(Facepp.getApiExpication(this) * 1000);
        // licenseManager.setAgainRequestTime(againRequestTime);

        String uuid = ConUtil.getUUIDString(FaceActivity.this);
        long[] apiName = {Facepp.getApiName()};
        String content = licenseManager.getContent(uuid, LicenseManager.DURATION_30DAYS, apiName);

        String errorStr = licenseManager.getLastError();
        Log.w("ceshi", "getContent++++errorStr===" + errorStr);

        boolean isAuthSuccess = licenseManager.authTime();
        Log.w("ceshi", "isAuthSuccess===" + isAuthSuccess);
        if (isAuthSuccess) {
            authState(true);
        } else {
            AsyncHttpClient mAsyncHttpclient = new AsyncHttpClient();
            String url = "https://api.megvii.com/megviicloud/v1/sdk/auth";
            RequestParams params = new RequestParams();
            params.put("api_key", Util.API_KEY);
            params.put("api_secret", Util.API_SECRET);
            params.put("auth_msg", content);
            Log.w("ceshi", "content:" + content);
            mAsyncHttpclient.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseByte) {
                    String successStr = new String(responseByte);
                    boolean isSuccess = licenseManager.setLicense(successStr);
                    if (isSuccess)
                        authState(true);
                    else
                        authState(false);

                    String errorStr = licenseManager.getLastError();
                    Log.w("ceshi", "setLicense++++errorStr===" + errorStr);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    error.printStackTrace();
                    authState(false);
                }
            });
        }

    }

    private void authState(boolean isSuccess) {
        //versionText.setText(Facepp.getVersion() + " ; " + ConUtil.getFormatterDate(Facepp.getApiExpication(this) * 1000));

        if (isSuccess) {
            barLinear.setVisibility(View.GONE);
            WarrantyBar.setVisibility(View.GONE);
            againWarrantyBtn.setVisibility(View.GONE);
            contentRel.setVisibility(View.VISIBLE);
        } else {
            barLinear.setVisibility(View.VISIBLE);
            WarrantyBar.setVisibility(View.GONE);
            againWarrantyBtn.setVisibility(View.VISIBLE);
            contentRel.setVisibility(View.GONE);
            WarrantyText.setText("联网授权失败！请检查网络或找服务商");
        }

    }

    private void getCameraSizeList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int cameraId = 1;
                if (isBackCamera)
                    cameraId = 0;
                cameraSize = ICamera.getCameraPreviewSize(cameraId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void isShowListView() {
        isShowListView = !isShowListView;
        if (isShowListView)
            mListRel.setVisibility(View.GONE);
        else
            mListRel.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.ACTION_DOWN == event.getAction() && mListRel.getVisibility() != View.GONE) {
            isShowListView();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (cameraSize == null)
                return 0;
            return cameraSize.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHoder hoder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.cameralist_item1, null);
                hoder = new ViewHoder();
                hoder.name = (TextView) convertView.findViewById(R.id.cameralist_item_nameText);
                convertView.setTag(hoder);
            } else {
                hoder = (ViewHoder) convertView.getTag();
            }

            HashMap<String, Integer> map = cameraSize.get(position);

            hoder.name.setText(map.get("width") + " * " + map.get("height"));
            return convertView;
        }

        class ViewHoder {
            TextView name, time, num;
        }

    }

    private void onClickListener() {
        System.out.println("sdggs");
        againWarrantyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                network();
            }
        });

        mListRel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowListView();
            }
        });
        System.out.println("sdggs");
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isShowListView();
                resolutionMap = cameraSize.get(position);

                resolutionEdit.setText(resolutionMap.get("width") + "*" + resolutionMap.get("height"));
            }
        });
        resolutionEdit.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    ConUtil.isGoneKeyBoard(FaceActivity.this);
                    isShowListView();
                }
                return false;
            }
        });
        System.out.println("sdggs");
        findViewById(R.id.loading_layout_rootRel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ConUtil.isGoneKeyBoard(FaceActivity.this);
            }
        });
        debugBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isDebug = !isDebug;
            }
        });

        cameraBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isBackCamera = !isBackCamera;

                getCameraSizeList();
            }
        });
        System.out.println("sdggs");
        RecorderBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartRecorder = !isStartRecorder;
            }
        });

        TopDetectBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isROIDetect = !isROIDetect;
            }
        });
        System.out.println("dsa");
        PointNumberBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                is106Points = !is106Points;
                if (is106Points) {
                    Facepp.facePoints = Facepp.FPP_GET_LANDMARK106;
                } else {
                    Facepp.facePoints = Facepp.FPP_GET_LANDMARK81;
                }
            }
        });
        System.out.println("dsa");
        facePropertyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isFaceProperty = !isFaceProperty;
            }
        });
        System.out.println("dsa");
        findViewById(R.id.load_layout_intoBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                min_face_size = Integer.parseInt(faceSizeEdit.getText().toString());
                detection_interval = Integer.parseInt(intervalEdit.getText().toString());
                System.out.println("dsds");
                startActivity(new Intent(FaceActivity.this, OpenglActivity.class).putExtra("isdebug", isDebug)
                        .putExtra("isBackCamera", isBackCamera).putExtra("isStartRecorder", isStartRecorder)
                        .putExtra("faceSize", min_face_size).putExtra("interval", detection_interval)
                        .putExtra("resolution", resolutionMap).putExtra("ROIDetect", isROIDetect)
                        .putExtra("isFaceProperty", isFaceProperty));
            }
        });
    }
}
