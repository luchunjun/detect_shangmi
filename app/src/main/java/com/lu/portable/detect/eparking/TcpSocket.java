package com.lu.portable.detect.eparking;
import com.lu.portable.detect.ui.HomeActivity;

import java.net.InetAddress;
import java.net.Socket;

public abstract class TcpSocket  implements Runnable {

	private int port;
	private String hostIP;
	private boolean connect = false;
	private KHTSample transceiver;
    public HomeActivity tcp_m_act;
	/**
	 * 建立连接
	 * <p>
	 * 连接的建立将在新线程中进行
	 * <p>
	 * 连接建立成功，回调{@code onConnect()}
	 * <p>
	 * 连接建立失败，回调{@code onConnectFailed()}
	 *
	 * @param hostIP
	 *            服务器主机IP
	 * @param port
	 *            端口
	 */
	public void connect(String hostIP, int port, HomeActivity _m_act) {
		this.hostIP = hostIP;
		this.port = port;
		this.tcp_m_act=_m_act;
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
			Socket socket = new Socket(hostIP, port);
			transceiver = new KHTSample(socket,this.tcp_m_act,this.hostIP) {
				@Override
				public void onReceive(InetAddress addr, String s) {
					TcpSocket.this.onReceive(this, s);
				}

				@Override
				public void onDisconnect(InetAddress addr) {
					connect = false;
					TcpSocket.this.onDisconnect(this);
				}
			};
			transceiver.start();
			connect = true;
			this.onConnect(transceiver);
		} catch (Exception e) {
			e.printStackTrace();
			this.onConnectFailed();
		}
	}

	/**
	 * 断开连接
	 * <p>
	 * 连接断开，回调{@code onDisconnect()}
	 */
	public void disconnect() {
		if (transceiver != null) {
			transceiver.stop();
			transceiver = null;


		}
	}

	/**
	 * 判断是否连接
	 *
	 * @return 当前处于连接状态，则返回true
	 */
	public boolean isConnected() {
		return connect;
	}

	/**
	 * 获取Socket收发器
	 *
	 * @return 未连接则返回null
	 */
	public KHTSample getTransceiver() {
		return isConnected() ? transceiver : null;
	}

	/**
	 * 连接建立
	 *
	 * @param transceiver
	 *            SocketTransceiver对象
	 */
	public abstract void onConnect(KHTSample transceiver);

	/**
	 * 连接建立失败
	 */
	public abstract void onConnectFailed();

	/**
	 * 接收到数据
	 * <p>
	 * 注意：此回调是在新线程中执行的
	 *
	 * @param transceiver
	 *            SocketTransceiver对象
	 * @param s
	 *            字符串
	 */
	public abstract void onReceive(KHTSample transceiver, String s);

	/**
	 * 连接断开
	 * <p>
	 * 注意：此回调是在新线程中执行的
	 *
	 * @param transceiver
	 *            SocketTransceiver对象
	 */
	public abstract void onDisconnect(KHTSample transceiver);
}

