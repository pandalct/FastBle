package com.clj.blesample.operation;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.clj.blesample.R;
import com.clj.blesample.comm.CRCUtil;

import static com.clj.blesample.operation.BoxActivity.byteArray2String;
import static com.clj.blesample.operation.BoxActivity.hexStringToByte;
import static com.clj.blesample.operation.BoxActivity.intToByteArray;

public class CRCActivity extends AppCompatActivity implements View.OnClickListener  {

    private Button  btn_crcTest;
    private EditText et_Message, edt_crc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crc);
        initView();
    }

    private void initView() {

        et_Message = findViewById(R.id.edt_message);
        edt_crc = findViewById(R.id.edt_crc);
        btn_crcTest = findViewById(R.id.btn_crcTest);


        btn_crcTest.setOnClickListener(this);

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle(titles[0]);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (currentPage != 0) {
//                    currentPage--;
//                    changePage(currentPage);
//                } else {
//                    finish();
//                }
//            }
//        });
    }
    private void sendMessage(String str) {
        et_Message.append(str + "\n");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.btn_crcTest:
                String hexString = edt_crc.getText().toString().replace(" ","");
                byte[] input = hexStringToByte(hexString);
                byte[] crc = intToByteArray(CRCUtil.do_crc(input));
                String out = byteArray2String(crc);
                sendMessage("输入：" + byteArray2String(input) + ",CRC校验后输出：" + out);
                break;
        }
    }
}
