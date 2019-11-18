package com.lu.portable.detect;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import com.kernal.demo.plateid.R;
import com.lu.portable.detect.configtools.ConfigTools;
import com.lu.portable.detect.configtools.InstrumentConfig;
import com.lu.portable.detect.model.ScaleDevice;
import com.lu.portable.detect.util.LogUtil;
import com.lu.portable.detect.util.SharedPreferencesUtil;
import com.lu.portable.detect.util.SocketUtil;
import com.lu.portable.detect.util.WifiUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import static com.lu.portable.detect.configtools.InstrumentConfig.ASK_OPEN_OR_NOT;
import static com.lu.portable.detect.configtools.InstrumentConfig.CAMERA_ADDRESS;
import static com.lu.portable.detect.configtools.InstrumentConfig.CAMERA_LIGHT_PORT;
import static com.lu.portable.detect.configtools.InstrumentConfig.CONTINUE_DATA_ACQUISITION;
import static com.lu.portable.detect.configtools.InstrumentConfig.FRAME_HEADER;
import static com.lu.portable.detect.configtools.InstrumentConfig.FRAME_TAIL;
import static com.lu.portable.detect.configtools.InstrumentConfig.HEART_HEAD;
import static com.lu.portable.detect.configtools.InstrumentConfig.INSTRUMENT_IP;
import static com.lu.portable.detect.configtools.InstrumentConfig.INSTRUMENT_PORT;
import static com.lu.portable.detect.configtools.InstrumentConfig.RECEIVE_SINGAL_HEAD;
import static com.lu.portable.detect.configtools.InstrumentConfig.SDDT;
import static com.lu.portable.detect.configtools.InstrumentConfig.SINGAL_HEAD;
import static com.lu.portable.detect.configtools.InstrumentConfig.SLEEP;
import static com.lu.portable.detect.configtools.InstrumentConfig.TEST_IP;
import static com.lu.portable.detect.configtools.InstrumentConfig.TEST_PORT;
import static com.lu.portable.detect.configtools.InstrumentConfig.cameraDevice;
import static com.lu.portable.detect.configtools.InstrumentConfig.getAddressByHeart;
import static com.lu.portable.detect.configtools.InstrumentConfig.instrumentDevice;
import static com.lu.portable.detect.configtools.InstrumentConfig.isCheckbtnClick;
import static com.lu.portable.detect.configtools.InstrumentConfig.keepScaleOpenCommand;
import static com.lu.portable.detect.configtools.InstrumentConfig.scaleArr;
import static com.lu.portable.detect.configtools.InstrumentConfig.setScaleConnectStatus;
import static com.lu.portable.detect.util.SocketUtil.bytesToHexString;


public class SocketActivity extends BaseActivity {
    public final static int MSG_KEEP_SCLAE_OPEN = 1003;
    public final static int AXIS_ERROR = 2001;
    public final static int OVERTIME_ERROR = 2002;
    public final static int DETECT_SUCCESS = 2003;
    private final static String TAG = "SocketActivity";
    private final static int IntervalByThirtySecond = 30 * 1000;
    public static boolean isDetectOver = true;
    public static boolean staticWeightStatus;
    public static boolean isStopCheckingBtnClick;
    public static int scaleSocketConnectStatus = 0;
    public static boolean clearAllData = false;
    //    OutputStream outputStream4;
    public static boolean cancelStatus = false;
    private static Socket instrumentSocket;
    private static Socket scaleSocket;
    private static Socket cameraSocket;
    public static boolean testStatus = false;
    public boolean isDeviceConnected = false;
    static String tempScaleCommands = "";
    static String tempCameraCommands = "";
    static String tempCommands = "";
    static InputStream instrumentIs;
    static InputStream cameraIs;
    static InputStream is1;
    static String response;
    private static String currentCheckAddress = "";
    private static int currentCheckTimes = 0;
    private static String currentSleepAddress = "";
    private static int currentSleepTimes = 0;
    static String instrumentResponse;
    static String cameraResponse;
    static OutputStream outputStream;
    private static Socket testSocket;
    static OutputStream outputStream2;
    static OutputStream outputStream3;
    static OutputStream outputStream4;
    public static long lastTime = 0;
    public static long currentTime = 0;
    public static Queue<String> checkCommandAddressesList = new ArrayDeque<>();
    public static Queue<String> stopAdrressList = new ArrayDeque<>();
    private ProgressDialog progressDialog;
    int sixAxises = 0;
    public static final int half_second = 1500;
    public static final int CLEAR_ALL_DATA = 2004;
    Timer timer;
    public Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //  Log.d(TAG, "MSG_KEEP_CONNECT" + msg.what);
            switch (msg.what) {
                case MSG_KEEP_SCLAE_OPEN:
                    postDelayed(() -> keepScaleOpen(), IntervalByThirtySecond);
                    break;
                case AXIS_ERROR:
                case OVERTIME_ERROR:
                case DETECT_SUCCESS:
                    generateDetectResult(msg.what);
                    break;
                case CLEAR_ALL_DATA:
                    handleClearHint();
                    break;
                default:
                    break;
            }
        }
    };


    public void updateListView() {
    }

    public void closeInstrument() {
        if (instrumentDevice.getHeart() <= 0) {
            showToast(R.string.IDS_not_connected_instrument);
            return;
        }
        String closeCameraCommand = InstrumentConfig.closeCommand(cameraDevice.getAddress());
        LogUtil.d(TAG, "closeCameraCommand" + closeCameraCommand);
        sendCameraMsg(closeCameraCommand);
        new Thread(() -> {
            try {
                String closeInstrumentCommand = InstrumentConfig.closeCommand(instrumentDevice.getAddress());
                LogUtil.d(TAG, "closeInstrument" + closeInstrumentCommand);
                sendMsg(closeInstrumentCommand);
                instrumentDevice.setHeart(0);
                for (ScaleDevice scaleDevice : InstrumentConfig.scaleArr) {
                    scaleDevice.setHeart(0);
                }
                updateListView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void playHightLight() {
        Log.e(TAG, "playHightLight");
    }

    public void closeScale(final int index) {
        LogUtil.d(TAG, "close scale index:" + index);
        if (InstrumentConfig.isScaleConnected()) {
            final String address = InstrumentConfig.scaleArr[index].getAddress();
            if (address.length() == 2) {
                final String closeCommand = InstrumentConfig.closeScaleCommand(address);
                LogUtil.d(TAG, "close scale" + closeCommand);
                new Thread(() -> sendScaleMsg(closeCommand)).start();
                setHeartZero(InstrumentConfig.scaleArr[index]);
            }
        }
    }

    private void setHeartZero(final ScaleDevice scaleDevice) {
        if (scaleDevice.getHeart() > 0) {
            scaleDevice.setHeart(0);
            mMainHandler.postDelayed(() -> {
                if (scaleDevice.getHeart() > 0) {
                    setHeartZero(scaleDevice);
                }
            }, 500);
        }

    }

    public void keepInstrumentOpen() {
        sendMsg(InstrumentConfig.keepOpenCommand(instrumentDevice.getAddress()));
        sendMsg(InstrumentConfig.generateSendMsg("04", "F2", InstrumentConfig.TSBV_INSTRUMENT));
    }

    private void updateInstrumentHeart() {
        InstrumentConfig.instrumentDevice.setHeart(5);
    }

    public void processMsg(String msg) {
        updateInstrumentHeart();
        if(!InstrumentConfig.isCameraPowerLoginClose){
            InstrumentConfig.isCameraPowerLoginClose =true;
            sendMsg(InstrumentConfig.generateSendMsg("04", "F5", InstrumentConfig.CLOSE_CAMERA_POWER));
        }
        tempCommands += msg;
        try {
            while (tempCommands.length() > 22) {
                // LogUtil.d(TAG, "tempCommands"+tempCommands);
                if (tempCommands.startsWith(HEART_HEAD)) {
                    InstrumentConfig.instrumentDevice.setAddress(getAddressByHeart(tempCommands.substring(4, 6)));
                    tempCommands = tempCommands.substring(6);
                } else if (SocketUtil.isFrameCommand(tempCommands)) {
                    //55 04 f1 61 6b 6b 70 00 00 9c 5a
                    LogUtil.d(TAG, tempCommands);
                    String command = tempCommands.substring(0, 22);
                    if (command.equals("5504F1616B6B7000009C5A")) {
                        keepInstrumentOpen();
                    } else if (command.startsWith("5504F4")) {
                        Log.e(TAG, "Instrument Battery:" + command);
                        ConfigTools.writeBatteryLog(new Date().toLocaleString() +"仪表电量内码:"+command);
                        //InstrumentConfig.updateInstrumentBattery(command.substring(6, 8));//8,10,10,12
                        InstrumentConfig.updateInstrumentBattery(command.substring(10, 12));
                    } else {
                        Log.e(TAG, "Instrument other command:" + command);
                    }
                    if (tempCommands.length() >= command.length()) {
                        tempCommands = tempCommands.substring(command.length());
                    } else {
                        tempCommands = tempCommands.substring(2);
                    }
                } else {
                    if (tempCommands.length() > 2) {
                        tempCommands = tempCommands.substring(2);
                    } else {
                        tempCommands = "";
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void processScaleMsg(String msg) {
        updateInstrumentHeart();
        if (!isDeviceConnected) {
            updateListView();
            isDeviceConnected = true;
        }
        tempScaleCommands = tempScaleCommands + msg;
        Log.d(TAG, "msg++++" + msg);
        String START_AXIS_FULLL = "5504F106ABBCCDEA7274195A";
        while (tempScaleCommands.length() >= 50 || SocketUtil.isHeart(tempScaleCommands)) {
            if (SocketUtil.isHeart(tempScaleCommands)) {
                // 687440
                if (tempScaleCommands.length() >= 6) {
                    String address = getAddressByHeart(tempScaleCommands.substring(4, 6));
                    setScaleConnectStatus(address);
                    if (tempScaleCommands.length() >= 6) {
                        tempScaleCommands = tempScaleCommands.substring(6);
                    }
                }
            } else if (tempScaleCommands.startsWith("FFFFFFFF")) {
                tempScaleCommands = tempScaleCommands.substring("FFFFFFFF".length());
            } else if (tempScaleCommands.startsWith(RECEIVE_SINGAL_HEAD)) {
                // byte signalLength = 2;
                //command = tempScaleCommands.substring(0, RECEIVE_SINGAL_HEAD.length() + signalLength);
                //tempScaleCommands = tempScaleCommands.substring(command.length());
                //LogUtil.d(TAG, "singal::" + command);
                //  InstrumentConfig.updateScaleSingal(signalQueue.poll(), command.substring(8, 10));
                // command = "";
                tempScaleCommands = tempScaleCommands.substring(RECEIVE_SINGAL_HEAD.length() + 2);
            } else if (tempScaleCommands.startsWith(START_AXIS_FULLL)) {
                tempScaleCommands = "";
                sixAxises++;
                LogUtil.e(TAG, "AXIS_FULL" + "stopCaculate()");
                if (SharedPreferencesUtil.getScaleMode() == 4) {
                    if (stopAdrressList.size() == 0) {
                        isDetectOver = false;
                        Log.e("AXIS_FULL", "testStatus:" + testStatus);
                        if (testStatus) {
                            showToast("满六轴现在是测试是大件车");
                        } else {
                            stopCaculate();
                        }
                    } else {
                        ConfigTools.writeLog("满六轴现在是测试是大件车 queue.size():" + stopAdrressList.size() + "scaleMode:" + SharedPreferencesUtil.getScaleMode());
                    }
                } else {
                    //showToast("满六轴现在是测试是标准车");
                    ConfigTools.writeLog("满六轴现在是测试是标准车:");
                }
            } else {
                Log.e(TAG, "SocketUtil.isScaleFrameCommand" + SocketUtil.isScaleFrameCommand(tempScaleCommands) + ":" + tempScaleCommands);
                if (SocketUtil.isScaleFrameCommand(tempScaleCommands)) {
                    handleScaleFrameCommand();
                } else {
                    Log.e(TAG, "other command++++" + tempScaleCommands);
                    if (tempScaleCommands.length() > 2) {
                        tempScaleCommands = tempScaleCommands.substring(2);
                    }
                }
            }
        }
    }

    private void handleScaleFrameCommand() {
        byte addressLength = 2;
        byte commandLength = 2;
        byte dataLenLength = 2;
        byte checkSumLength = 2;
        int dataLenStartIndex = FRAME_HEADER.length() + addressLength + commandLength;
        int dataLenEndIndex = dataLenStartIndex + dataLenLength;
        int length = SocketUtil.computeCommandLength(tempScaleCommands.substring(dataLenStartIndex, dataLenEndIndex));
        //5502F106616B6B700000A05A
        int comandEndIndex = FRAME_HEADER.length() + addressLength + commandLength + dataLenLength + length * 2 + checkSumLength + FRAME_TAIL.length();
        String address = tempScaleCommands.substring(2, 4);
        String command = tempScaleCommands.substring(0, comandEndIndex);
        //  LogUtil.e("command",address +"::setScaleConnectStatus");
        if (tempScaleCommands.length() >= comandEndIndex && tempScaleCommands.substring(comandEndIndex - 2, comandEndIndex).equals(FRAME_TAIL)) {
            setScaleConnectStatus(address);
            if (tempScaleCommands.length() >= comandEndIndex) {
                tempScaleCommands = tempScaleCommands.substring(comandEndIndex);
            }
            //55 04 F4 06 65 72 72 6F 72 74 B6 5A
            // LogUtil.e("command+++", logCommand + "length:" + command.length() + ":" + command.substring(4, 6));
            if (command.contains(ASK_OPEN_OR_NOT)) {
                sendScaleMsg(keepScaleOpenCommand(address));
                if (isDetectOver && stopAdrressList.size() == 0) {
                    if (InstrumentConfig.getScaleByAddress(address) != null) {
                        if (InstrumentConfig.getScaleByAddress(address).getBatteryFlag() == 10) {
                            sendScaleMsg(InstrumentConfig.generateSendMsg(address, "F2", InstrumentConfig.TSBV));
                        } else {
                            InstrumentConfig.getScaleByAddress(address).setBatteryFlag(InstrumentConfig.getScaleByAddress(address).getBatteryFlag() + 1);
                        }
                    }
                }
            } else if (command.contains(InstrumentConfig.WEIGHT_ERROR_AXIS)) {
                if (isStopCheckingBtnClick) {
                    InstrumentConfig.resultAddressList.add(command.substring(2, 4));
                }
                handleErrorResult(AXIS_ERROR);
            } else if (command.contains(SDDT)) {
                showToast("开始静态称重了");
            } else if (command.substring(4, 8).equals("F406")) {
                ConfigTools.writeBatteryLog(new Date().toLocaleString()+InstrumentConfig.getScaleByAddress(address).getName() +"称台电量内码:"+command);
                InstrumentConfig.updateScaleBattery(address, command.substring(8, 10));
            } else if (command.substring(4, 20).equals(InstrumentConfig.AXIS_FULL)) {
                sixAxises++;
                isDetectOver = false;
                LogUtil.e(TAG, "AXIS_FULL" + "stopCaculate()");
                if (SharedPreferencesUtil.getScaleMode() == 4) {
                    if (stopAdrressList.size() == 0) {
                        showToast("满六轴现在是测试是大件车");
                        stopCaculate();
                    } else {
                        ConfigTools.writeLog("stopAdrressList.size():" + stopAdrressList.size() + "scaleMode:" + SharedPreferencesUtil.getScaleMode());
                    }
                } else {
                    showToast("满六轴现在是测试是标准车");
                }
            } else if (command.contains("F106ABBCCDEA0000")) {
                tempScaleCommands = tempScaleCommands.substring(command.length());
            } else if (command.length() == 24 && command.contains(InstrumentConfig.NOTIFY_PHONE_RECEIVE_DATA)) {
                //下位机收到检测命令
                LogUtil.e(TAG, "NOTIFY_PHONE_RECEIVE_DATA:" + command + "checkCommandAddressesList.size():" + checkCommandAddressesList.size());
                ConfigTools.writeLog(command.substring(2, 4) + "称台清除数据 checkCommandAddressesList.size:" + checkCommandAddressesList.size());
                if (checkCommandAddressesList.size() > 0) {
                    if (command.substring(2, 4).equals(checkCommandAddressesList.peek())) {
                        checkCommandAddressesList.poll();
                    }
                    if (checkCommandAddressesList.size() > 0) {
                        sendCheckFirstTime(checkCommandAddressesList.peek());
                    } else {
                        mMainHandler.sendEmptyMessage(CLEAR_ALL_DATA);
                    }
                }
            } else if (command.length() == 24 && command.substring(4, 4 + InstrumentConfig.RCSF.length()).equals(InstrumentConfig.RCSF)) {
                LogUtil.e(TAG, "RCSF+tempScaleCommands:" + tempScaleCommands);
                pollStopList(command.substring(2, 4));
                ConfigTools.writeLog(command.substring(2, 4) + "称台反馈停止采集");
            } else if (command.length() == 24 && command.substring(4, 8).equals("F406")) {
                InstrumentConfig.updateScaleBattery(address, command.substring(8, 10));
            } else if (command.length() == 24 && command.substring(4, 8).equals("F306")) {
                if (command.substring(4, InstrumentConfig.WEIGHT_OVER_TIME.length() + 4).equals(InstrumentConfig.WEIGHT_OVER_TIME)) {
                    // pollStopList(command.substring(2, 4));
                    if (isStopCheckingBtnClick) {
                        InstrumentConfig.resultAddressList.add(command.substring(2, 4));
                    }
                    handleErrorResult(OVERTIME_ERROR);
                } else if (command.contains("ABBCCDEA")) {
                    Log.e(TAG, "error command" + command);
                } else {
                    if (staticWeightStatus) {
                        int count = Integer.parseInt(command.substring(16, 20), 16);
                        InstrumentConfig.setInnerCode(command);
                        Log.e(TAG, "count:" + count);
                    } else {
                        sendSleep(command.substring(2, 4));
                    }
                }
            } else if (command.length() > 24 && command.substring(4, 6).equals("F3")) {
                Log.e(TAG, "hello!" + command);
                Log.e(TAG, "hello! cancelStatus:" + cancelStatus + "isStopCheckingBtnClick:" + isStopCheckingBtnClick);
                if (cancelStatus && !isStopCheckingBtnClick) {
                    ConfigTools.writeLog("cancelStatus:" + cancelStatus + "isStopCheckingBtnClick:" + isStopCheckingBtnClick);
                } else {
                    hanldeDetectData(command);
                }
            }
        }
    }

    private void handleClearHint() {
        if (!clearAllData) {
            playHightLight();
            clearAllData = true;
        }
    }

    private void handleErrorResult(int errcode) {
        if (!staticWeightStatus) {
            Log.e("handleErrorResult", "stopAdrressList.size()" + stopAdrressList.size());
            if (stopAdrressList.size() > 0) {
                sendSleepFirstTime(stopAdrressList.peek());
            } else {
                mMainHandler.sendEmptyMessage(errcode);
            }
        }
    }

    private void pollStopList(String address) {
        if (stopAdrressList.size() > 0 && stopAdrressList.peek().equals(address)) {
            stopAdrressList.poll();
        }
    }

    private void hanldeDetectData(String command) {
        if (testStatus) {
            return;
        }
        InstrumentConfig.updateDetectInfo(command);
        if (isStopCheckingBtnClick) {
            InstrumentConfig.resultAddressList.add(command.substring(2, 4));
        }
        Log.e(TAG, "hanldeDetectData stopAdrressList.size():" + stopAdrressList.size());
        ConfigTools.writeLog("hanldeDetectData stopAdrressList.size():" + stopAdrressList.size());
        if (stopAdrressList.size() > 0) {
            sendSleepFirstTime(stopAdrressList.peek());
        } else {
            if (isDetectOver && InstrumentConfig.allResultBack()) {
                mMainHandler.sendEmptyMessage(DETECT_SUCCESS);
            } else {
                if (SharedPreferencesUtil.getScaleMode() == 4 && command.contains("F332")) {
                    sendCheckList();
                }
            }
        }
    }

    public void sendCheckList() {
        clearAllData = false;
        keepScaleOpenAnyway();
        checkCommandAddressesList.clear();
        Log.e("sendCheckList", "clear checkCommandAddressesList");
        for (ScaleDevice scaleDevice : InstrumentConfig.scaleArr) {
            if (scaleDevice.getHeart() > 0 && scaleDevice.getAddress().length() > 0) {
                checkCommandAddressesList.add(scaleDevice.getAddress());
            }
        }
        Log.e(TAG, "checkCommandAddressesList" + checkCommandAddressesList.size());
        ConfigTools.writeLog("checkCommandAddressesList" + checkCommandAddressesList.size());
        if (checkCommandAddressesList.size() > 0) {
            sendCheckFirstTime(checkCommandAddressesList.peek());
            ConfigTools.writeLog("checkCommandAddressesList" + checkCommandAddressesList.size());
            //Log.d(TAG, "checkCommandAddressesList" + checkCommandAddressesList.size());
        } else {
            showToast(R.string.IDS_no_scale);
        }
    }

    public void keepScaleOpen() {
        if (!isCheckbtnClick) {
            keepScaleOpenAnyway();
        }
    }

    private void keepScaleOpenAnyway() {
        keepScaleOpenCommand("01");
        keepScaleOpenCommand("02");
        keepScaleOpenCommand("03");
        keepScaleOpenCommand("04");
    }

    public void beginConnect() {
        if (timer != null) {
            timer.cancel();
        } else {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startDetectHeart();
            }
        }, 0, half_second);


    }

    public void startDetectHeart() {
        currentTime = System.nanoTime();
        if (lastTime == 0) {
            lastTime = currentTime;
        }
        //185747000
        if (currentTime - lastTime > 1.4 * 1000 * 1000 * 1000) {
            Log.e(TAG, "startDetectHeart" + "lastTime" + lastTime + "currentTime" + currentTime + ":" + (currentTime - lastTime) / 1000 / 1000 / 1000);
            lastTime = currentTime;
            if (!staticWeightStatus && isDetectOver) {
                // Log.e(TAG,"updateScaleHeart");
                InstrumentConfig.updateScaleHeart();
            }
            if (InstrumentConfig.instrumentDevice.getHeart() <= 0) {
                if (isDeviceConnected) {
                    isDeviceConnected = false;
                }
                if (InstrumentConfig.isLogin) {
                    connectInstrumentSocket();
                }
            } else {
                if (instrumentDevice.getHeart() > 0) {
                    InstrumentConfig.instrumentDevice.setHeart(instrumentDevice.getHeart() - 1);
                }
                if (cameraDevice.getHeart() > 0) {
                    InstrumentConfig.cameraDevice.setHeart(cameraDevice.getHeart() - 1);
                }
            }
            updateListView();
        }
        // mMainHandler.sendEmptyMessage(MSG_KEEP_CONNECT);
    }

    public void connectInstrumentSocket() {
        LogUtil.d("connectInstrumentSocket", "connectInstrumentSocket");
        if (WifiUtil.isSameNetWithInstrument(this)) {
            new Thread(this::connectSocket).start();
        } else {
            showToast(R.string.IDS_not_connected_instrument_net);
            instrumentDevice.setHeart(instrumentDevice.getHeart() - 1);
            cameraDevice.setHeart(0);
            cameraDevice.setHeart(instrumentDevice.getHeart() - 1);
            instrumentDevice.setHeart(instrumentDevice.getHeart() - 1);
            for (ScaleDevice scaleDevice : scaleArr) {
                scaleDevice.setHeart(0);
                instrumentDevice.setHeart(instrumentDevice.getHeart() - 1);
            }
            scaleSocketConnectStatus = 0;
            instrumentDevice.setHeart(instrumentDevice.getHeart() - 1);
        }
    }

    private void receiveMsg() {
        new Thread(() -> {
            try {
                // 步骤1：创建输入流对象InputStream
                while (instrumentSocket.isConnected()) {
                    instrumentIs = instrumentSocket.getInputStream();
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = instrumentIs.read(buf)) != -1) {
                        instrumentResponse = bytesToHexString(buf, 0, len);
                        processMsg(instrumentResponse);
                    }
                }
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void receiveScaleMsg() {
        new Thread(() -> {
            try {
                // 步骤1：创建输入流对象InputStream
                while (true) {
                    scaleSocketConnectStatus = 3;
                    is1 = scaleSocket.getInputStream();
                    scaleSocket.setKeepAlive(true);
                    byte[] buf1 = new byte[1024];
                    int len;
                    while ((len = is1.read(buf1)) != -1) {
                        response = bytesToHexString(buf1, 0, len);
                        //LogUtil.d("receiveScaleMsg", len + ":" + response);
                        //  ConfigTools.writeLog("receiveScaleMsg:"+response);
                        processScaleMsg(response);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    //debug ctrl +D
    public void connectScaleSocket() throws IOException {
        Log.e(TAG, "connectScaleSocket");
        scaleSocket = new Socket(INSTRUMENT_IP, SharedPreferencesUtil.getScalePort());
        scaleSocketConnectStatus = 3;
        receiveScaleMsg();
    }

    public void connectSocket() {
        if (isDeviceConnected) {
            return;
        }
        try {
            instrumentSocket = new Socket(INSTRUMENT_IP, INSTRUMENT_PORT);
            receiveMsg();
            connectScaleSocket();
            Log.e(TAG, "connectScaleSocket");
            //判断客户端和服务器是否连接成功
            //LogUtil.d("socket.isConnected()", "instrumentSocket.isConnected():" + instrumentSocket.isConnected() + "scaleSocket" + scaleSocket.isConnected());
            connectCameraSocket();
        } catch (IOException e) {
            e.printStackTrace();
            //showToast(R.string.failed_connected_sensor_sever);
        }
    }

    private void processCameraMsg(String msg) {
        updateInstrumentHeart();
        tempCameraCommands += msg;
        while (tempCameraCommands.length() > 6) {
            try {
                if (tempCameraCommands.startsWith(HEART_HEAD)) {
                    tempCameraCommands = tempCameraCommands.substring(6);
                } else if (tempCameraCommands.startsWith("5503F1616B6B7000009B5A")) {
                    LogUtil.e(TAG, "5503F1616B6B7000009B5A");
                    keepCameraWiFiOpen();
                    tempCameraCommands = "";
                    break;
                } else {
                    tempCameraCommands = tempCameraCommands.substring(2);
                }
            } catch (Exception e) {
                e.printStackTrace();
                tempCameraCommands="";
            }
        }
    }

    private void keepCameraWiFiOpen() {
        String openCameraWiFiCommand = InstrumentConfig.keepOpenCommand(CAMERA_ADDRESS);
        // LogUtil.d(TAG, "openInstrument" + openInstrumentCommand);
        sendCameraMsg(openCameraWiFiCommand);
    }

    public void connectCameraSocket() {
        try {
            LogUtil.d("connectCameraSocket", "connectCameraSocket");
            cameraSocket = new Socket(INSTRUMENT_IP, InstrumentConfig.CAMERA_LIGHT_PORT);
            receiveCameraMsg();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void receiveCameraMsg() {
        new Thread(() -> {
            try {
                // 步骤1：创建输入流对象InputStream
                while (cameraSocket != null && cameraSocket.isConnected()) {
                    if (cameraSocket != null) {
                        cameraIs = cameraSocket.getInputStream();
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = cameraIs.read(buf)) != -1) {
                            cameraResponse = bytesToHexString(buf, 0, len);
                            // LogUtil.d(TAG,"cameraResponse:"+cameraResponse);
                            cameraDevice.setHeart(3);
                            processCameraMsg(cameraResponse);
                        }
                    }
                }
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        ).start();
    }

    public void sendTestCheck(final String address) {
        LogUtil.d("TAG", "sendTestCheck:" + address);
        new Thread(() -> {
            String checkCommand = InstrumentConfig.generateSendMsg(address, "F2", InstrumentConfig.CALCULATE_DATA_ACQUISITION);
            sendScaleMsg(checkCommand);
        }).start();
    }

    public void sendCheck(final String address) {
        if (address.equals(checkCommandAddressesList.peek()) && currentCheckAddress.equals(address)) {
            new Thread(() -> {
                LogUtil.d("sendCheck", "address;" + address);
                String checkCommand = InstrumentConfig.generateSendMsg(address, "F2", InstrumentConfig.CALCULATE_DATA_ACQUISITION);
                LogUtil.d("sendCheck", "address;" + address + " checkCommand:" + checkCommand);
                sendScaleMsg(checkCommand);
            }).start();
            mMainHandler.postDelayed(sendCheckRunnable, half_second);
        }
    }

    Runnable sendCheckRunnable = () -> {
        if (currentCheckTimes < 2) {
            if (checkCommandAddressesList.size() > 0) {
                sendCheck(checkCommandAddressesList.peek());
                currentCheckTimes++;
            }
        }
    };

    public void sendMsg(final String msg) {
        final String tempMsg = msg.replace(" ", "").toUpperCase();
        LogUtil.e("sendMsg", tempMsg);
        if (instrumentSocket == null || instrumentSocket.isClosed()) {
            connectSocket();
            return;
        }
        try {
            outputStream = instrumentSocket.getOutputStream();
            outputStream.write(SocketUtil.hexStringToByte(tempMsg));
            outputStream.flush();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void sendCameraMsg(final String msg) {
        final String tempMsg = msg.replace(" ", "").toUpperCase();
        LogUtil.e("sendCameraMsg", tempMsg);
        new Thread(() -> {
            try {
                if (InstrumentConfig.isLogin && (cameraSocket == null || !cameraSocket.isConnected())) {
                    cameraSocket = new Socket(INSTRUMENT_IP, CAMERA_LIGHT_PORT);
                }
                outputStream3 = cameraSocket.getOutputStream();
                outputStream3.write(SocketUtil.hexStringToByte(tempMsg));
                outputStream3.flush();
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void sendTestMsg(final String msg) {
        if (msg.length() > 0) {
            return;
        }
        final String tempMsg = msg.replace(" ", "").toUpperCase();
        LogUtil.e("sendTestMsg", tempMsg);
        new Thread(() -> {
            try {
                if (InstrumentConfig.isLogin && (testSocket == null || !testSocket.isConnected())) {
                    testSocket = new Socket(TEST_IP, TEST_PORT);
                }
                outputStream4 = testSocket.getOutputStream();
                outputStream4.write(SocketUtil.hexStringToByte(tempMsg));
                outputStream4.flush();
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void stopCaculate() {
        //lcj
        stopAdrressList.clear();
        for (ScaleDevice scaleDevice : InstrumentConfig.scaleArr) {
            ConfigTools.writeLog(scaleDevice.getAddress() + ":scaleDevice.getHeart()" + scaleDevice.getHeart());
            if (scaleDevice.getHeart() > 0 && scaleDevice.getAddress().length() > 0) {
                stopAdrressList.add(scaleDevice.getAddress());
            }
        }
        if (stopAdrressList.size() > 0) {
            sendSleepFirstTime(stopAdrressList.peek());
        } else {
            showToast(R.string.IDS_no_scale);
        }
    }

    private void sendCheckFirstTime(String address) {
        currentCheckTimes = 0;
        currentCheckAddress = address;
        Log.d("sendCheckFirstTime", "address:" + address);
        sendCheck(currentCheckAddress);
    }

    private void sendSleepFirstTime(String address) {
        currentSleepTimes = 0;
        currentSleepAddress = address;
        sendSleep(currentSleepAddress);
    }

    public void sendStopMine() {
        new Thread(this::sendSleep).start();
    }

    public void sendSleep() {
        for (ScaleDevice scaleDevice : InstrumentConfig.scaleArr) {
            if (scaleDevice.getHeart() > 0) {
                sendScaleMsg(InstrumentConfig.generateSendMsg(scaleDevice.getAddress(), "F2", InstrumentConfig.SLEEP));
            }
        }
    }

    public void sendTestSleep(final String address) {
        Log.e(TAG, "sendTestSleep" + address);
        new Thread(() -> sendScaleMsg(InstrumentConfig.generateSendMsg(address, "F2", InstrumentConfig.SLEEP))).start();

    }

    //lcj 2019-7-27
    public void sendSleep(final String address) {
        new Thread(() -> sendScaleMsg(InstrumentConfig.generateSendMsg(address, "F2", InstrumentConfig.SLEEP))).start();
        mMainHandler.postDelayed(sendSleepRunnable, half_second);
    }

    Runnable sendSleepRunnable = () -> {
        if (currentSleepTimes < 2) {
            if (stopAdrressList.size() > 0 && currentSleepAddress.equals(stopAdrressList.peek())) {
                sendSleep(currentSleepAddress);
                currentSleepTimes++;
            }
        }
    };


    public void sendScaleMsg(final String msg) {
        if (msg.length() < 24) {
            return;
        }
        final String tempmsg = msg.replaceAll(" ", "").toUpperCase();
        StringBuilder sendScaleLogBuffer = new StringBuilder(tempmsg.substring(0, 2) + " ");
        for (int i = 2; i < tempmsg.length(); i = i + 2) {
            if (tempmsg.length() >= i + 2) {
                sendScaleLogBuffer.append(tempmsg.substring(i, i + 2)).append(" ");
            } else {
                sendScaleLogBuffer.append(tempmsg.substring(i, i + 1)).append(" ");
            }
        }
        if (sendScaleLogBuffer.toString().contains(InstrumentConfig.CALCULATE_DATA_ACQUISITION)) {
            LogUtil.e("sendScaleMsg", "CALCULATE_DATA_ACQUISITION:" + sendScaleLogBuffer);
            ConfigTools.writeLog("sendScaleMsg" + sendScaleLogBuffer.toString());
        } else if (sendScaleLogBuffer.toString().contains(SLEEP)) {
            LogUtil.d("sendScaleMsg", "SLEEP " + sendScaleLogBuffer);
            ConfigTools.writeLog("SLEEP " + sendScaleLogBuffer);
        } else if (sendScaleLogBuffer.toString().contains(InstrumentConfig.KEEP_OPEN)) {
            LogUtil.d("sendScaleMsg", "KEEP_OPEN " + sendScaleLogBuffer);
        } else if (sendScaleLogBuffer.toString().contains(InstrumentConfig.CLOSE)) {
            LogUtil.e("sendScaleMsg", "CLOSE " + sendScaleLogBuffer);
        } else if (sendScaleLogBuffer.toString().contains(InstrumentConfig.TSBV)) {
            LogUtil.d("sendScaleMsg", "TSBV " + sendScaleLogBuffer);
        } else if (sendScaleLogBuffer.toString().contains(SINGAL_HEAD)) {
            LogUtil.d("sendScaleMsg", "SINGAL_HEAD " + sendScaleLogBuffer);
        } else if (sendScaleLogBuffer.toString().contains(CONTINUE_DATA_ACQUISITION)) {
            LogUtil.d("sendScaleMsg", "CONTINUE_DATA_ACQUISITION " + sendScaleLogBuffer);
            ConfigTools.writeLog("CONTINUE_DATA_ACQUISITION" + sendScaleLogBuffer.toString());
        }
        sendTestMsg(tempmsg);
        try {
            if (scaleSocket == null) {
                Log.e(TAG, "connectScaleSocket");
                connectScaleSocket();
            }
            outputStream2 = scaleSocket.getOutputStream();
            outputStream2.write(SocketUtil.hexStringToByte(tempmsg));
            outputStream2.flush();
        } catch (IOException e) {
            e.printStackTrace();
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (NetworkOnMainThreadException e) {
            e.printStackTrace();
            new Thread(() -> sendScaleMsg(msg)).start();
        }
    }


    public void closeSocket() {
        try {
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
            if (outputStream2 != null) {
                outputStream2.close();
                outputStream2 = null;
            }
            if (outputStream3 != null) {
                outputStream3.close();
                outputStream3 = null;
            }
            if (instrumentIs != null) {
                instrumentIs.close();
                instrumentIs = null;
            }
            if (cameraIs != null) {
                cameraIs.close();
                cameraIs = null;
            }
            if (is1 != null) {
                is1.close();
                is1 = null;
            }

            if (instrumentSocket != null && instrumentSocket.isConnected()) {
                Log.e(TAG, "close instrumentSocket");
                instrumentSocket.close();
                instrumentSocket = null;
            }
            if (scaleSocket != null && scaleSocket.isConnected()) {
                Log.e(TAG, "close scaleSocket");
                scaleSocket.close();
                scaleSocket = null;
            }
            if (cameraSocket != null && cameraSocket.isConnected()) {
                Log.e(TAG, "close cameraSocket");
                cameraSocket.close();
                cameraSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initProgressDialog(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(false);//循环滚动
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);//false不能取消显示，true可以取消显示
        progressDialog.show();
    }

    public void closeProgressDialog(int msgID) {
        if (progressDialog != null && progressDialog.isShowing()) {
            showToast(msgID);
            progressDialog.dismiss();
        }
    }

    public void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    protected void generateDetectResult(int errorCode) {
        LogUtil.e(TAG, "errorCode" + errorCode);
        if (staticWeightStatus) {
            return;
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        switch (errorCode) {
            case DETECT_SUCCESS:
            case AXIS_ERROR:
                if (InstrumentConfig.allResultBack()) {
                    startResultActivity();
                }
                break;
            case OVERTIME_ERROR:
                showToast(R.string.IDS_error_overtime_error);
                break;
            default:
                break;

        }
    }

    public void startResultActivity() {
        Log.e(TAG, "startResultActivity");
    }
}
