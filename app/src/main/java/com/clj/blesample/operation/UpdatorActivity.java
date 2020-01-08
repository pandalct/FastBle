package com.clj.blesample.operation;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.clj.blesample.R;
import com.clj.blesample.comm.CRCUtil;
import com.clj.blesample.comm.Observer;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.clj.blesample.operation.OperationActivity.KEY_DATA;

public class UpdatorActivity extends AppCompatActivity implements Observer, View.OnClickListener {

    private BleDevice bleDevice;
    private Button  btn_snRead, btn_snWrite, btn_update,btn_off;
    private TextView txt_message;
    private EditText et_SN;
    private String TAG = "UpdatorActivity";
    private String destFileDir ;
    private String destFileName ;
    private String uuidService = "0000fff0-0000-1000-8000-00805f9b34fb";
    private String uuidNotifyCharacteristic = "0000fff1-0000-1000-8000-00805f9b34fb";
    private String uuidEndpointCharacteristic = "0000fff2-0000-1000-8000-00805f9b34fb";
    private File downloadFile;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updator);

        destFileDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        destFileName = UUID.randomUUID() + ".bin";

        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted(permissions -> {
                    // Storage permission are allowed.
                    BleManager.getInstance().init(getApplication());
                    BleManager.getInstance()
                            .enableLog(true)
                            .setReConnectCount(1, 5000)
                            .setConnectOverTime(20000)
                            .setOperateTimeout(5000);
                    initData();
                    initView();
                    connectDevice();
                })
                .onDenied(permissions -> {
                    // Storage permission are not allowed.
                    finish();
                })
                .start();

    }

    private void initData() {
        bleDevice = getIntent().getParcelableExtra(KEY_DATA);
        if (bleDevice == null)
            finish();
    }

    private void initView() {

        btn_snRead = findViewById(R.id.btn_snRead);
        btn_snWrite = findViewById(R.id.btn_snWrite);
        et_SN = findViewById(R.id.edt_sn);
        btn_update = findViewById(R.id.btn_update);
        txt_message = findViewById(R.id.txt_message);
        btn_off = findViewById(R.id.btn_off);


        btn_off.setOnClickListener(this);
        btn_snRead.setOnClickListener(this);
        btn_snWrite.setOnClickListener(this);
        btn_update.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                //upgradeBox();
                readBatteryInfo();
                break;
            case R.id.btn_snRead:
                readSN();
                break;
            case R.id.btn_snWrite:
                writeSN();
                break;
            case  R.id.btn_off:
                offBGM();
                break;
        }
    }
    private Handler handler = new Handler();
    private void downloadBin(OnDownloadListener listener){
        show("开始下载");
        downloadFile = null;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://www.hn-ihealth.com/apk/update.bin").build();
         okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败监听回调
                Log.e(TAG,e.getMessage());
                listener.onDownloadFailed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                File dir = new File(destFileDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, destFileName);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中更新进度条
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    // 下载完成
                    //show("下载完成");
                    listener.onDownloadSuccess(file);
                } catch (Exception e) {
                    Log.e(TAG,e.getMessage());
                } finally {
                    try {
                        if (is != null)
                            is.close();

                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        Log.e(TAG,e.getMessage());
                    }
                }
            }
        });
    }
    public interface OnDownloadListener{

        /**
         * 下载成功之后的文件
         */
        void onDownloadSuccess(File file);

        /**
         * 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载异常信息
         */

        void onDownloadFailed(Exception e);
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
                        if(data.length<2) return;
                        switch (data[1]){
                            case 0x4B:
                                downloadFile();
                                break;
                            case 0x51:
                                compareBoxVersion();
                                break;
                            case 0x52:
                                reciveUpgradeNotification(data[2]);
                                break;
                        }

                    }
                });
    }

    private void reciveUpgradeNotification(byte status) {
        switch (status){
            case 0x30:
                sendData();
                return;
            case 0x31:
                show("数据长度有误");
                break;
            case 0x32:
                show("CRC校验错误");
                break;
            case 0x33:
                show("本次传输取消");
                break;
        }
        bufIndex = 0;
    }

    private byte[] buffer;
    private int bufIndex;
    private int packageLength = 128;
    private void sendData() {
        if(bufIndex >= buffer.length) return;
        int buffEndIndex = ((bufIndex + 1) * packageLength < buffer.length) ? (bufIndex + 1) * packageLength : (buffer.length - 1);
        byte[] splitPackage = Arrays.copyOfRange(buffer, bufIndex, buffEndIndex);
        String hexData = byteArray2String(splitPackage);
        String hexIndex = byteArray2String(intToByteArray(bufIndex));
        hexData = String.format("AA 52 %s %s", hexIndex, hexData);
        writeData(hexData);
        sendMessage(String.format("发送第%s包:%s",hexIndex,hexData));
        bufIndex++;
    }
    private void readUpgradePackage(){
        try {
            FileInputStream fi = new FileInputStream(downloadFile);
            buffer = new byte[(int) downloadFile.length()];
            int offset = 0;
            int numRead = 0;
            while (true) {
                if (!(offset < buffer.length
                        && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0))
                    break;
                offset += numRead;
            }
            sendData();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void compareBoxVersion() {
        if (true) { //对比版本号，大于则更新
            if (downloadFile == null) {
                sendMessage("文件为 null");
                return;
            }
            readUpgradePackage();
        }
    }

    private void downloadFile() {
        //读取到电池信息
//                            if(data[6]<20){
//                                show("电池电量过低，不能升级，请充电后再试。");
//                                return;
//                            }
        downloadBin(new OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                downloadFile = file;
                sendMessage(String.format("文件下载完成，地址：%s", file.getAbsolutePath()));
                handler.postDelayed(() -> upgradeBox(),1000);
            }

            @Override
            public void onDownloading(int progress) {
                sendMessage(String.format("文件下载进度：%d%%", progress));
            }

            @Override
            public void onDownloadFailed(Exception e) {
                sendMessage(String.format("下载失败：%s", e.getMessage()));
            }
        });
    }
    private void offBGM() {
        //关机
        writeData("AA 50");
        sendMessage("写入关机：AA 50");
    }

    private void show(String s){
        handler.postDelayed(() -> Toast.makeText(getApplication(),s,Toast.LENGTH_SHORT).show(),0);

    }
    private void writeSN() {

        String inputStr = et_SN.getText().toString();
        if (inputStr.length() != 11){
            show("序列号格式错误");
            return;
        }
        if(inputStr.isEmpty()) return;
        int index =  Integer.parseInt(inputStr);
        String command = "AA 91 " + Integer.toHexString(index);
        writeData(command);
        sendMessage("写入SN："+command);
    }

    private void readSN() {
        //读取SN
        writeData("AA 4D 33");
        sendMessage("写入读取SN：AA 4D 33");
    }

    private void readBatteryInfo(){
        writeData("AA 4B");
        sendMessage("写入读取电池信息：AA 4B");
    }
    private void upgradeBox() {
        //升级指令
        writeData("AA 51");
        sendMessage("写入：AA 51");
    }
    StringBuilder sb = new StringBuilder();
    private void sendMessage(String str) {
        String msg = str + "\n";
        sb.insert(0,msg);
        txt_message.setText(sb);

    }

    @Override
    public void disConnected(BleDevice bleDevice) {

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
        byte[] result = new byte[2];
        result[0] = (byte)((i >> 8) & 0xFF);
        result[1] = (byte)(i & 0xFF);
        return result;
    }
}
