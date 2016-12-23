package com.example.gcls.prj_demo.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

public class FaceMask extends View {
	public static final int Threshold = 30;
	Paint localPaint = null;
	RectF mFaceRect = new RectF();
	RectF mDrawRect = null;
	private int normal_colour = 0xff00b4ff;
	private int high_colour = 0xffff0000;
	private boolean isFraontalCamera = true;
	private boolean isRect = true;
	private int[] points;
	private ArrayList<PointBean> pointList;
	private PointBean mPointBean;

	public FaceMask(Context context, AttributeSet atti) {
		super(context, atti);
		mDrawRect = new RectF();
		localPaint = new Paint();
		localPaint.setColor(normal_colour);
		localPaint.setStyle(Paint.Style.STROKE);
	}

	public void setFrontal(boolean isFrontal) {
		this.isFraontalCamera = isFrontal;
	}

	public void setIsRect(boolean isRect) {
		this.isRect = isRect;
	}

	public void setPhotRect(RectF rectf) {
		mFaceRect = rectf;
		postInvalidate();
	}

	public void setPhots(int[] points) {
		this.points = points;
		postInvalidate();
	}

	public void setPhotList(ArrayList<PointBean> pointList) {
		this.pointList = pointList;
		postInvalidate();
	}

	public void setFacePlace(PointBean pointBean) {
		this.mPointBean = pointBean;
		postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawFacePlace(canvas);
	}

	private void drawFacePlace(Canvas canvas) {
		if (mPointBean == null)
			return;

		if (isFraontalCamera) {
			localPaint.setStrokeWidth(5);
			for (int i = 0; i < mPointBean.rectfs.size(); i++) {
				RectF faceRect = mPointBean.rectfs.get(i);
				RectF drawRect = new RectF();
				drawRect.set(getWidth() * (1 - faceRect.right), getHeight()
						* faceRect.top, getWidth() * (1 - faceRect.left),
						getHeight() * faceRect.bottom);
//				float left = getWidth() * (1 - faceRect.right);
//				float top = getHeight() * (1 - faceRect.bottom);
//				float right = getWidth() * (1 - faceRect.left);
//				float bottom = getHeight() * (1 - faceRect.top);
//				float top =  getHeight() * (1 - faceRect.right);
//				float left = getWidth() * (1 - faceRect.bottom);
//				float bottom = getHeight() * (1 - faceRect.left);
//				float right = getWidth() * (1 - faceRect.top);
//				Log.w("ceshi", "getWidth()===" + getWidth() + ", getHeight()===" + getHeight());
//				Log.w("ceshi", "faceRect===" + faceRect);
//				Log.w("ceshi", "left===" + left + ", top===" + top + ", right===" + right + ", bottom===" + bottom);
//				drawRect.set(left, top, right, bottom);
				canvas.drawRect(drawRect, localPaint);
			}
			localPaint.setStrokeWidth(10);
			for (int c = 0; c < mPointBean.points.size(); c++) {
				ArrayList<HashMap<String, Float>> point = mPointBean.points
						.get(c);
				for (int i = 0; i < point.size(); i++) {
					HashMap<String, Float> map = point.get(i);
					canvas.drawPoint(getWidth() * (1 - map.get("x")),
							getHeight() * (map.get("y")), localPaint);// 画点，参数一水平x轴，参数二垂直y轴，第三个参数为Paint对象。
				}
			}

		} else {
			localPaint.setStrokeWidth(5);
			for (int i = 0; i < mPointBean.rectfs.size(); i++) {
				RectF faceRect = mPointBean.rectfs.get(i);
				RectF drawRect = new RectF();
				drawRect.set(getWidth() * faceRect.left, getHeight()
						* faceRect.top, getWidth() * faceRect.right,
						getHeight() * faceRect.bottom);
				canvas.drawRect(drawRect, localPaint);
			}
			localPaint.setStrokeWidth(10);
			for (int c = 0; c < mPointBean.points.size(); c++) {
				ArrayList<HashMap<String, Float>> point = mPointBean.points
						.get(c);
				for (int i = 0; i < point.size(); i++) {
					HashMap<String, Float> map = point.get(i);
					canvas.drawPoint(getWidth() * (map.get("x")),
							getHeight() * map.get("y"), localPaint);// 画点，参数一水平x轴，参数二垂直y轴，第三个参数为Paint对象。
				}
			}
		}

	}

	private void drawPoint(Canvas canvas) {
		if (pointList == null)
			return;
		for (int i = 0; i < pointList.size(); i++) {
			PointBean pointBean = pointList.get(i);
			canvas.drawPoint(getWidth() * (1 - pointBean.x), getHeight()
					* pointBean.y, localPaint);// 画点，参数一水平x轴，参数二垂直y轴，第三个参数为Paint对象。
		}
		// for (int i = 0; i < points.length / 2; i++) {
		// int x = points[i * 2];
		// int y = points[i * 2 + 1];
		// Log.w("ceshi", " x ===== " + x + ", " + y);
		// canvas.drawPoint(x, y, localPaint);//
		// 画点，参数一水平x轴，参数二垂直y轴，第三个参数为Paint对象。
		// }
	}

	private void drawRect(Canvas canvas) {
		if (mFaceRect == null)
			return;
		// Log.w("ceshi", "mFaceRect===" + mFaceRect + ", " + isFraontalCamera);
		if (isFraontalCamera) {
			mDrawRect.set(getWidth() * (1 - mFaceRect.right), getHeight()
					* mFaceRect.top, getWidth() * (1 - mFaceRect.left),
					getHeight() * mFaceRect.bottom);

			// mDrawRect.set(getWidth() - mFaceRect.right, mFaceRect.top,
			// getWidth() - mFaceRect.left, mFaceRect.bottom);
		} else {
			mDrawRect.set(getWidth() * mFaceRect.left, getHeight()
					* mFaceRect.top, getWidth() * mFaceRect.right, getHeight()
					* mFaceRect.bottom);
		}
		canvas.drawRect(mDrawRect, localPaint);
	}
}
