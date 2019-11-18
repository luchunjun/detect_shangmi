package com.lu.portable.detect.eparking;

import android.util.Log;

import com.lu.portable.detect.configtools.InstrumentConfig;
import com.lu.portable.detect.ui.HomeActivity;
import com.lu.portable.detect.util.LogUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;


public abstract class KHTSample implements Runnable {
    private final static String TAG="KHTSample";
    private HomeActivity m_act;
    protected Socket socket;
    private InetAddress addr;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean runFlag;

    /**
     * 实例化
     *
     * @param socket 已经建立连接的socket
     */
    public KHTSample(Socket socket, HomeActivity tcp_m_act, String m_ip) {
        this.socket = socket;
        this.addr = socket.getInetAddress();
        this.m_act = tcp_m_act;
    }

    /**
     * 功能：转换bytes格式为int
     * 参数：
     * src：byte[]类型 要转换的byte[]
     * offset:从数组的第几位开始
     * 返回值：
     * value：转换的结果
     */
    public static int bytesToInt(byte[] src, int offset) {
        int value = ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    /**
     * 开启Socket收发
     * <p>
     * 如果开启失败，会断开连接并回调{@code onDisconnect()}
     */

    public void start() {
        runFlag = true;
        new Thread(this).start();
    }

    /**
     * 断开连接(主动)
     * <p>
     * 连接断开后，会回调{@code onDisconnect()}
     */
    public void stop() {
        runFlag = false;
        try {
            socket.shutdownInput();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送字符串
     *
     * @param s 字符串
     * @return 发送成功返回true
     */
    public boolean send(String s) {
        if (out != null) {
            try {
                out.writeUTF(s);
                out.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 发送字符串
     *
     * @param s 字符串
     * @return 发送成功返回true
     */
    public boolean sendBytes(String s) {
        if (out != null) {
            try {
                out.writeBytes(s);
                out.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 接收结果的线程
     */

    /**
     * 发送字符串
     *
     * @param bytes 字符串
     * @return 发送成功返回true
     */
    public boolean sendBytes(byte[] bytes) {
        if (out != null) {
            try {
                out.write(bytes);
                out.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 监听Socket接收的数据(新线程中运行)
     */
    @Override
    public void run() {
        try {
            in = new DataInputStream(this.socket.getInputStream());
            out = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            runFlag = false;
        }
        int m_len = 0, m_type = 0;

        while (runFlag) {
            try {
                byte[] heat = new byte[8];
                int result =in.read(heat, 0, 8);
                if (('E' == heat[0]) && ('P' == heat[1])) {
                    m_len = bytesToInt(heat, 4);
                    m_type = heat[2];
                }
                switch (m_type) {
                    case 1:
                        System.out.println("json数据格式长度" + m_len);
                        byte[] b = new byte[m_len];
                        int r = in.read(b, 0, m_len);
                        if (r > -1) {
                            m_act.showCarPlateNumber(b);
                        }
                        break;
                    case 2:
                        LogUtil.d(TAG,"big识别结果长度：" + m_len);
                        byte[] m_pictrue = new byte[m_len];
                        in.readFully(m_pictrue, 0, m_len);
                        m_act.showCarPlatePhoto(m_pictrue);
                        break;
                    case 3:
                        LogUtil.d(TAG,"mini识别结果长度：" + m_len);
                        byte[] mini_pictrue = new byte[m_len];
                        in.readFully(mini_pictrue, 0, m_len);
                        m_act.showMiniCarPlatePhoto(mini_pictrue);
                        break;
                    case 4:
                        LogUtil.d(TAG,"other识别结果长度：" + m_len);
                        break;
                }
            } catch (IOException e) {
                //连接状态改为断开
                runFlag = false;
            }
        }

        //断开连接
        try {
            in.close();
            out.close();
            socket.close();
            in = null;
            out = null;
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.onDisconnect(addr);
    }

    /**
     * 功能：转换bytes格式为int
     * 参数：
     *  src：byte[]类型 要转换的byte[]
     *  offset:从数组的第几位开始
     *返回值：
     *  value：转换的结果
     */

    /**
     * 接收到数据
     * <p>
     * 注意：此回调是在新线程中执行的
     *
     * @param addr 连接到的Socket地址
     * @param s    收到的字符串
     */
    public abstract void onReceive(InetAddress addr, String s);

    /**
     * 连接断开
     * <p>
     * 注意：此回调是在新线程中执行的
     *
     * @param addr 连接到的Socket地址
     */
    public abstract void onDisconnect(InetAddress addr);

    /**
     * Created by Chen on 2017/9/1.
     */

    public static class FuncActivity {

        final TcpSocket c1 = new TcpSocket() {

            @Override
            public void onReceive(KHTSample st, String s) {
                System.out.println("Client Receive: " + s);
            }

            @Override
            public void onDisconnect(KHTSample st) {

            }

            @Override
            public void onConnect(KHTSample transceiver) {
                System.out.println("Client Connect");
            }

            @Override
            public void onConnectFailed() {
                System.out.println("Client Connect Failed");
            }
        };

        static void delay(int a) {
            try {
                Thread.sleep(a);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void Disconnect() {
            c1.disconnect();
        }
      // 192.168.88.55
        //判断是否连接
        public int FConnect(String ip1, HomeActivity _m_act) {
            //c1.connect(ip1, 9740, _m_act);
            c1.connect(ip1, 8050, _m_act);
            delay(500);
            if (c1.isConnected() == true) {
                return 1;
            } else {
                return -1;
            }
        }

        public void JosnOrBin() {
            JosnOrBin_Thread josnOrBin_thread = new JosnOrBin_Thread();
            josnOrBin_thread.start();
        }

        public void SetResu() {
            SetResu_Thread setResu_thread = new SetResu_Thread();
            setResu_thread.start();
        }

        public void MyThread() {
            MyThread1 myThread1 = new MyThread1();
            myThread1.start();
        }

        public void Up_Button() {
            UP_button_Thread my_up_button = new UP_button_Thread();
            my_up_button.start();
        }

        public void Trigger_Button() {
            Trigger_Button_Thread trigger_button_thread = new Trigger_Button_Thread();
            trigger_button_thread.start();
        }

        //心跳
        class MyThread extends Thread {
            public void run() {
                while (c1.isConnected()) {
                    System.out.println("发送心跳包");
                    byte[] bytes_KeepAlive = new byte[8];
                    bytes_KeepAlive[0] = 'E';
                    bytes_KeepAlive[1] = 'P';
                    bytes_KeepAlive[2] = 5;
                    bytes_KeepAlive[3] = 0;
                    bytes_KeepAlive[4] = 0;
                    bytes_KeepAlive[5] = 0;
                    bytes_KeepAlive[6] = 0;
                    bytes_KeepAlive[7] = 0;
                    c1.getTransceiver().sendBytes(bytes_KeepAlive);
                    delay(5000);
                }
            }
        }

        //设置接受数据模式BIN ？JSON？
        class JosnOrBin_Thread extends Thread {
            public void run() {
                if (c1.isConnected()) {
                    //System.out.println("设置识别模式");
                    byte[] bytes_JSON = new byte[27];
                    bytes_JSON[0] = 'E';
                    bytes_JSON[1] = 'P';
                    bytes_JSON[2] = 6;
                    bytes_JSON[3] = 0;
                    bytes_JSON[4] = 19;
                    bytes_JSON[5] = 0;
                    bytes_JSON[6] = 0;
                    bytes_JSON[7] = 0;
                    bytes_JSON[8] = '[';
                    bytes_JSON[9] = 's';
                    bytes_JSON[10] = 'e';
                    bytes_JSON[11] = 't';
                    bytes_JSON[12] = 'f';
                    bytes_JSON[13] = 'm';
                    bytes_JSON[14] = 't';
                    bytes_JSON[15] = ']';
                    bytes_JSON[16] = '\n';
                    bytes_JSON[17] = 'f';
                    bytes_JSON[18] = 'm';
                    bytes_JSON[19] = 't';
                    bytes_JSON[20] = '=';
                    bytes_JSON[21] = 'j';
                    bytes_JSON[22] = 's';
                    bytes_JSON[23] = 'o';
                    bytes_JSON[24] = 'n';
                    bytes_JSON[25] = '\n';
                    c1.getTransceiver().sendBytes(bytes_JSON);
                }

            }
        }

        //接收格式
        class SetResu_Thread extends Thread {
            public void run() {
                //设置接受数据模式BIN ？JSON？
                if (c1.isConnected()) {
                   // byte[] bytes_resu = new byte[38];
                    byte[] bytes_resu={'E','P',7,0,30,0,0,0,'[','s','e','t','r','e','s','u',']',
                            '\n','f','u','l','l','=','1','\n',
                            'm','i','n','i','=','1','\n',
                            't','w','o','=','1','\n'};
                    Log.e(TAG,"bytes_resu:"+bytes_resu.length);
                    c1.getTransceiver().sendBytes(bytes_resu);
                }
            }
        }

        //心跳
        class MyThread1 extends Thread {
            public void run() {
                while (c1.isConnected()) {
                   // System.out.println("发送心跳包");
                    byte[] bytes_KeepAlive = new byte[8];
                    bytes_KeepAlive[0] = 'E';
                    bytes_KeepAlive[1] = 'P';
                    bytes_KeepAlive[2] = 5;
                    bytes_KeepAlive[3] = 0;
                    bytes_KeepAlive[4] = 0;
                    bytes_KeepAlive[5] = 0;
                    bytes_KeepAlive[6] = 0;
                    bytes_KeepAlive[7] = 0;
                    try {
                        c1.getTransceiver().sendBytes(bytes_KeepAlive);
                    }catch (NullPointerException e){
                        if(c1!=null) {
                            Log.e(TAG,"c1.getTransceiver()"+ c1.getTransceiver());
                        }
                        e.printStackTrace();
                    }
                    delay(5000);
                }
            }
        }

        //抬杆
        class UP_button_Thread extends Thread {
            public void run() {
                byte[] bytes_KeepAlive = new byte[8];
                bytes_KeepAlive[0] = 'E';
                bytes_KeepAlive[1] = 'P';
                bytes_KeepAlive[2] = 10;
                bytes_KeepAlive[3] = 0;
                bytes_KeepAlive[4] = 0;
                bytes_KeepAlive[5] = 0;
                bytes_KeepAlive[6] = 0;
                bytes_KeepAlive[7] = 0;
                c1.getTransceiver().sendBytes(bytes_KeepAlive);
            }
        }

        class Trigger_Button_Thread extends Thread {
            public void run() {
                byte[] bytes_resu = new byte[8];
                bytes_resu[0] = 'E';
                bytes_resu[1] = 'P';
                bytes_resu[2] = 9;
                bytes_resu[3] = 0;
                bytes_resu[4] = 0;
                bytes_resu[5] = 0;
                bytes_resu[6] = 0;
                bytes_resu[7] = 0;
                LogUtil.d("Trigger_Button_Thread:","c1"+c1);
                try {
                    c1.getTransceiver().sendBytes(bytes_resu);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

