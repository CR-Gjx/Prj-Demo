package com.example.gcls.prj_demo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class SpeechActivity extends AppCompatActivity {

    private static final String TAG = SpeechActivity.class.getSimpleName();
    private TextView tv;
    private Button btnStart;
    private Button btnStop;
    //private EditText mResultText;
    private String mResultText;
    private TextView instr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv = (TextView) findViewById(R.id.textView3);
        btnStart = (Button) findViewById(R.id.button3);
        btnStop = (Button) findViewById(R.id.button4);
        //mResultText = ((EditText) findViewById(R.id.editText));
        instr = (TextView) findViewById(R.id.textView4);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "请发指令：前进，后退，左转，右转，停止", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // 初始化即创建语音配置对象
        //SpeechUtility.createUtility(this, SpeechConstant.APPID +"=584ff1b0"+ SpeechConstant.FORCE_LOGIN +"=true");
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=584ff1b0");

        btnStart.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                tv.setText("目前状态：开始");
                btnVoice();
            }
        });

        btnStop.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                tv.setText("目前状态：停止");
                mResultText = "";
                instr.setText("停止");
                MainActivity.blueTooth.sendInformation("5");
            }
        });
    }

    private void btnVoice() {
        //RecognizerDialog dialog = new RecognizerDialog(this, null);
        RecognizerDialog dialog = new RecognizerDialog(this, new InitListener() {
            @Override
            public void onInit(int i) {
            }
        });
        dialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        dialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        dialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                printResult(recognizerResult);
            }
            @Override
            public void onError(SpeechError speechError) {
            }
        });
        dialog.show();

    }

    // 回调结果
    private void printResult(RecognizerResult results) {
        /*
        mResultText.setText("");
        String text = parseIatResult(results.getResultString());
        // 自动填写地址
        //mResultText.append(text);
        if (start == 1) {
            mResultText.append(text);
            if (mResultText.getText().toString().contains("前进")) {
                instr.setText("前进");
            } else if (mResultText.getText().toString().contains("后退")) {
                instr.setText("后退");
            } else if (mResultText.getText().toString().contains("左转")) {
                instr.setText("左转");
            } else if (mResultText.getText().toString().contains("右转")) {
                instr.setText("右转");
            } else if (mResultText.getText().toString().contains("停止")) {
                instr.setText("停止");
            }
            //instr.setText(mResultText.getText().toString());
            //instr.setText(results.getResultString());
        }*/
        mResultText = "";
        String text = parseIatResult(results.getResultString());

            mResultText += text;
            if (mResultText.contains("前进")) {
                MainActivity.blueTooth.sendInformation("1");
                instr.setText("前进");
            } else if (mResultText.contains("后退")) {
                MainActivity.blueTooth.sendInformation("4");
                instr.setText("后退");
            } else if (mResultText.contains("左转")) {
                MainActivity.blueTooth.TurnLeft();
                instr.setText("左转");
            } else if (mResultText.contains("右转")) {
                MainActivity.blueTooth.TurnRight();
                instr.setText("右转");
            } else if (mResultText.contains("停止")) {
                MainActivity.blueTooth.sendInformation("5");
                instr.setText("停止");
            }
            //instr.setText(mResultText.getText().toString());
            //instr.setText(results.getResultString());

        tv.setText("请按开始");
    }
    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

}
