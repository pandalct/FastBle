package com.clj.blesample.operation;


import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.clj.blesample.R;
import com.clj.blesample.comm.CRCUtil;
import com.clj.blesample.comm.Observer;
import com.clj.blesample.comm.ObserverManager;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.util.ArrayList;
import java.util.List;

public class BoxActivity extends AppCompatActivity implements Observer, View.OnClickListener {

    public static final String KEY_DATA = "key_data";

    private BleDevice bleDevice;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic characteristic;
    private int charaProp;

    private Toolbar toolbar;
    private List<Fragment> fragments = new ArrayList<>();
    private int currentPage = 0;
    private String[] titles = new String[3];

    private Button btn_flash, btn_boxInfo, btn_charge, btn_off, btn_sendReadDataNotice, btn_readXTInfo, btn_readData, btn_deleteBloodData, btn_snRead, btn_snWrite, btn_update, btn_crcTest;
    private EditText et_SN, et_Message, edt_crc;
    private String uuidService = "0000fff0-0000-1000-8000-00805f9b34fb";
    private String uuidNotifyCharacteristic = "0000fff1-0000-1000-8000-00805f9b34fb";
    private String uuidEndpointCharacteristic = "0000fff2-0000-1000-8000-00805f9b34fb";
    private String deviceType;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_box);
        initData();
        connectDevice();

        initView();
//        initPage();
//notify
//        ObserverManager.getInstance().addObserver(this);
    }

    private void connectDevice() {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {

            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                sendMessage("设备连接失败！" + exception.getDescription());
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                sendMessage("设备连接成功！");
                enableNotify();
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                sendMessage("断开连接！");
            }
        });
    }

    private void enableNotify() {
        BleManager.getInstance().notify(
                bleDevice,
                uuidService,
                uuidNotifyCharacteristic,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        Log.d(TAG, "onNotifySuccess");
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        Log.d(TAG, "onNotifyFailure:" + exception.getDescription());

                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        String result = byteArray2String(data);
                        Log.d(TAG, "接收到回发数据：" + result);
                        sendMessage("接收到回发数据：" + result);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().clearCharacterCallback(bleDevice);
        ObserverManager.getInstance().deleteObserver(this);
    }

    @Override
    public void disConnected(BleDevice device) {
        if (device != null && bleDevice != null && device.getKey().equals(bleDevice.getKey())) {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentPage != 0) {
                currentPage--;
                changePage(currentPage);
                return true;
            } else {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {

        btn_flash = findViewById(R.id.btn_flash);
        btn_boxInfo = findViewById(R.id.btn_boxInfo);
        btn_charge = findViewById(R.id.btn_charge);
        btn_off = findViewById(R.id.btn_off);
        btn_sendReadDataNotice = findViewById(R.id.btn_sendReadDataNotice);
        btn_readXTInfo = findViewById(R.id.btn_readXTInfo);
        btn_readData = findViewById(R.id.btn_readData);
        btn_deleteBloodData = findViewById(R.id.btn_deleteBloodData);
        btn_snRead = findViewById(R.id.btn_snRead);
        btn_snWrite = findViewById(R.id.btn_snWrite);
        et_SN = findViewById(R.id.edt_sn);
        btn_update = findViewById(R.id.btn_update);
        et_Message = findViewById(R.id.edt_message);
        edt_crc = findViewById(R.id.edt_crc);
        btn_crcTest = findViewById(R.id.btn_crcTest);


        btn_crcTest.setOnClickListener(this);
        btn_flash.setOnClickListener(this);
        btn_boxInfo.setOnClickListener(this);
        btn_charge.setOnClickListener(this);
        btn_off.setOnClickListener(this);
        btn_sendReadDataNotice.setOnClickListener(this);
        btn_readXTInfo.setOnClickListener(this);
        btn_readXTInfo.setEnabled(false);
        btn_readData.setOnClickListener(this);
        btn_deleteBloodData.setOnClickListener(this);
        btn_snRead.setOnClickListener(this);
        btn_snWrite.setOnClickListener(this);
        btn_update.setOnClickListener(this);
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

    private void initData() {
        bleDevice = getIntent().getParcelableExtra(KEY_DATA);
        if (bleDevice == null)
            finish();
    }

    private void initPage() {

        changePage(0);
    }

    private void sendMessage(String str) {
        et_Message.append(str + "\n");
    }

    public void changePage(int page) {
        currentPage = page;
        toolbar.setTitle(titles[page]);

        if (currentPage == 1) {
            ((CharacteristicListFragment) fragments.get(1)).showData();
        } else if (currentPage == 2) {
            ((CharacteristicOperationFragment) fragments.get(2)).showData();
        }
    }

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public BluetoothGattService getBluetoothGattService() {
        return bluetoothGattService;
    }

    public void setBluetoothGattService(BluetoothGattService bluetoothGattService) {
        this.bluetoothGattService = bluetoothGattService;
    }

    public BluetoothGattCharacteristic getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }

    public int getCharaProp() {
        return charaProp;
    }

    public void setCharaProp(int charaProp) {
        this.charaProp = charaProp;
    }

    private String TAG = "BoxActivity";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_flash:
                //闪灯三次
                writeData("AA 4A 03");
                sendMessage("写入：AA 4A 03");
                break;
            case R.id.btn_boxInfoRead:
                //读取盒子信息
                writeData("AA 4B");
                sendMessage("写入：AA 4B");
                break;
            case R.id.btn_sendReadDataNotice:
                //通知读取血糖仪信息
                writeData("AA 4c 30 03 30 30 35 30");
                sendMessage("写入：AA 4c 30 03 30 30 35 30");
                break;
            case R.id.btn_readXTInfo:
                //读取血糖仪信息
                writeData("AA 4D " + deviceType);
                sendMessage("AA 4D " + deviceType);
                break;
            case R.id.btn_readData:
                //读取血糖数据50条
                writeData("AA 4E 30 35 30");
                sendMessage("AA 4E 30 35 30");
                break;
            case R.id.btn_deleteBloodData:
                //删除血糖数据
                writeData("AA 49");
                sendMessage("AA 49");
            case R.id.btn_off:
                //关机
                writeData("AA 50");
                sendMessage("AA 50");
                break;
            case R.id.btn_update:
                //升级
                new AlertDialog.Builder(this).setTitle("确定要开启升级模式？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                writeData("AA 51");
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
                    }
                }).show();

                break;
            case R.id.btn_snRead:
                //写入SN
                //writeData("AA 4E 30 35 30");
                break;
            case R.id.btn_snWrite:
                //写入SN
                //writeData("AA 4E 30 35 30");
                break;
            case R.id.btn_crcTest:
                String hexString = edt_crc.getText().toString().replace(" ","");
                byte[] input = hexStringToByte(hexString);
                byte[] crc = intToByteArray(CRCUtil.do_crc(input));
                String out = byteArray2String(crc);
                sendMessage("输入：" + byteArray2String(input) + ",CRC校验后输出：" + out);
                break;
        }
    }


    public void writeData(String hexString) {

        hexString = hexString.replace(" ", "");
        byte[] input = hexStringToByte(hexString);
        byte[] crc = intToByteArray(CRCUtil.do_crc(input));
        byte[] data = CRCUtil.concatAll(input,crc);
        BleManager.getInstance().write(
                bleDevice,
                uuidService,
                uuidEndpointCharacteristic,
                data,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.d(TAG, "写入数据成功，onWriteSuccess" + current + "_" + total + "_" + justWrite.toString());
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Log.d(TAG, "onWriteFailure:" + exception.getDescription());
                    }
                });
    }

    ;

    // 从十六进制字符串到字节数组转换
    public static byte[] hexStringToByte(String hex) {
        hex = hex.toUpperCase();
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }


    private static char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String byteArray2String(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            stringBuilder.append(HEX_CHAR[(data[i] & 0xf0) >>> 4]);
            stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);
            if (i < data.length - 1)
                stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    /**
     * int到byte[] 由高位到低位
     * @param i 需要转换为byte数组的整行值。
     * @return byte数组
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }


}
