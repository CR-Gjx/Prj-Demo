package com.megvii.landmarklib.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import com.megvii.facepp.sdk.Facepp;

import android.content.Context;

public class FileUtil {

	private String fileName;

	public boolean creatFile(Context mContext) { 
		File mediaStorageDir = mContext.getExternalFilesDir("megviiPoint_text");

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return false;
			}
		}

		fileName = mediaStorageDir + "/" + System.currentTimeMillis() + ".txt";

		return true;
	}

	public void text(String content, int[] data, int faceSize) {
		// 存入数据的文件目录是c:\\ary.txt
		FileWriter fw = null;
		try {
			fw = new FileWriter(fileName, true);
			fw.write(content);
			int offset = (Facepp.facePoints * 2 + 4 + 1);
			for (int f = 0; f < faceSize; f++) {
				int faceOffset = offset * f;
				fw.write("\r\n");
				fw.write("face_id:" + f);
				fw.write("\r\n");
				fw.write("face_rect(left, top, right, bottom) : ");
				for (int i = 0; i < 4; i++) {
					if (i == 3)
						fw.write(data[faceOffset + i] + "");
					else
						fw.write(data[faceOffset + i] + ", ");
				}
			
				fw.write("\r\n");
				fw.write("landmarks:");
				fw.write("\r\n");
				for (int i = 0; i < Facepp.facePoints * 2; i++) {
					if (i % 2 == 1)
						fw.write(data[faceOffset + 5 + i] + " \r\n");
					else
						fw.write(data[faceOffset + 5 + i] + ", ");
				}
			}
			fw.write("\r\n");
			fw.write("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}// 输出流用完就关闭
		}

	}

	/**
	 * 根据byte数组，生成文件
	 */
	public static String saveJPGFile(Context mContext, byte[] data, String key) {
		if (data == null)
			return null;

		File mediaStorageDir = mContext.getExternalFilesDir("megviiPoint_image");

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		try {
			String jpgFileName = "_" + key + ".jpg";
			fos = new FileOutputStream(mediaStorageDir + "/" + jpgFileName);
			bos = new BufferedOutputStream(fos);
			bos.write(data);
			return mediaStorageDir.getAbsolutePath() + "/" + jpgFileName;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return null;
	}
}
