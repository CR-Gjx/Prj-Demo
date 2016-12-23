package com.megvii.landmarklib.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

public class NetWorkUtil {

	public static boolean netWorkIsConnect(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager == null) {
			return false;
		} else {
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					System.out.println(i + "===状态==="
							+ networkInfo[i].getState());
					System.out.println(i + "===类型==="
							+ networkInfo[i].getTypeName());
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static void httpUrlConnection(Context context, String data,
			Handler handler) {
		try {
			
			if(!netWorkIsConnect(context)) {
				//无网
				Message msg = new Message();
				msg.obj = -1;
				msg.what = 1;
				handler.sendMessage(msg);
				return;
			}

			// 请求的地址
			String spec = "https://api.faceid.com/faceid/v1/sdk/authm";
			// 根据地址创建URL对象
			URL url = new URL(spec);
			// 根据URL对象打开链接
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			// 设置请求的方式
			urlConnection.setRequestMethod("POST");
			// 设置请求的超时时间
			urlConnection.setReadTimeout(10000);
			urlConnection.setConnectTimeout(10000);
			// 设置请求的头
			urlConnection.setRequestProperty("Content-Type", "text/plain");

			urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
			urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
											// setDoInput的默认值就是true
			// 获取输出流
			OutputStream os = urlConnection.getOutputStream();
			os.write(data.getBytes());
			os.flush();
			os.close();
			int code = urlConnection.getResponseCode();
			Log.w("ceshi", "code===" + code);
			if (code == 200) {
				// 获取响应的输入流对象
				InputStream is = urlConnection.getInputStream();
				// 创建字节输出流对象
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// 定义读取的长度
				int len = 0;
				// 定义缓冲区
				byte buffer[] = new byte[1024];
				// 按照缓冲区的大小，循环读取
				while ((len = is.read(buffer)) != -1) {
					// 根据读取的长度写入到os对象中
					baos.write(buffer, 0, len);
				}
				// 释放资源
				is.close();
				baos.close();
				// 返回字符串
				final String result = new String(baos.toByteArray());
				Message msg = new Message();
				msg.obj = result;
				msg.what = 0;
				handler.sendMessage(msg);
			} else {
				Message msg = new Message();
				msg.obj = code;
				msg.what = 1;
				handler.sendMessage(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Message msg = new Message();
			msg.obj = -1;
			msg.what = 1;
			handler.sendMessage(msg);
		}
	}
}