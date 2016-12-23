package com.megvii.landmarklib.util;

import java.io.File;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class IMediaRecorder {

	public ICamera mICamera;
	private Context mContext;
	private MediaRecorder mRecorder;
	// 系统的视频文件
	private File videoFile;
	// 记录是否正在进行录制
	private boolean isRecording = false;

	public IMediaRecorder(Context context, ICamera iCamera) {
		this.mContext = context;
		this.mICamera = iCamera;
	}

	public void start() {
		if (!Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			Toast.makeText(mContext, "SD卡不存在，请插入SD卡！", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		try {
			// 创建保存录制视频的视频文件
			videoFile = new File(Environment.getExternalStorageDirectory()
					.getCanonicalFile()
					+ "/megviivideo/+"
					+ System.currentTimeMillis() + ".mp4");
			// 创建MediaPlayer对象
			if (mRecorder == null) {
				mRecorder = new MediaRecorder(); // 创建mediarecorder的对象
			}
			mICamera.mCamera.unlock();
			mRecorder.setCamera(mICamera.mCamera);
			mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);// 这两项需要放在setOutputFormat之前
			mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);// 设置录制视频源为Camera(相机)

			mRecorder.reset();
			// 设置从麦克风采集声音
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 设置从摄像头采集图像
			mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mRecorder.setVideoEncodingBitRate(1048576 * 40);
			// 设置视频文件的输出格式
			// 必须在设置声音编码格式、图像编码格式之前设置
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			// 设置声音编码的格式
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			// 设置图像编码的格式
			mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
			mRecorder.setVideoSize(mICamera.cameraWidth, mICamera.cameraHeight);
			mRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
			// 每秒 4帧
			mRecorder.setVideoFrameRate(50);
			mRecorder.setOutputFile(videoFile.getAbsolutePath());
			// 指定使用SurfaceView来预览视频
			//mRecorder.setPreviewDisplay(sView.getHolder().getSurface()); // ①
			mRecorder.prepare();
			// 开始录制
			mRecorder.start();
			Log.w("ceshi", "---recording---");
			isRecording = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void stop(){
		// 如果正在进行录制
		if (isRecording){
			// 停止录制
			mRecorder.stop();
			// 释放资源
			mRecorder.release();
			mRecorder = null;
			mContext = null;
			mICamera = null;
		}
	}
}
