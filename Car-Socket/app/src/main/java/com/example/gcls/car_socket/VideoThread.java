package com.example.gcls.car_socket;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import android.hardware.Camera.Size;
import android.hardware.Camera.CameraInfo;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Created by shiyu on 15/12/18.
 */
public class VideoThread extends Thread {

    private MainActivity main;
    private Button ButtonBegin;
    public TextView TestText;
    public SurfaceHolder surfaceHolder;
    private boolean isPreview;
    private final static int CAMERA_PORT = 8686;
    private final static int PREVIEW_WIDTH = 200, PREVIEW_HEIGHT = 200;
    private static int counter = 0;
    private static final int PERIOD = 5;
    private android.hardware.Camera camera;
    private static final int MAX_FACES = 1;
    //private FaceRecognizer faceRecognizer;
    private Bitmap bitmap;
    private Bitmap tmpBmp;
    private byte[] byteArray;

    boolean detected;
    RectF faceRects[] = new RectF[MAX_FACES];
    int detectedFaces = 0;
    //private String clientIP;

    public VideoThread(MainActivity main) {
        this.main = main;
    }

    @Override
    public void run() {
        if(Looper.myLooper() == null) {
            Looper.prepare();
        }
        TestText = (TextView)main.findViewById(R.id.test);
        ButtonBegin = (Button)main.findViewById(R.id.Begin);

        surfaceHolder = ((SurfaceView) main.findViewById(R.id.surfaceView)).getHolder();

        //faceRecognizer = new FaceRecognizer(null, main);
        TestText.setText("SKDJ");
        surfaceHolder.setKeepScreenOn(true);
        isPreview = false;
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                //initCamera();
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initCamera();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @SuppressLint("NewApi")
    private void initCamera() {
        if (!isPreview) {
            TestText.setText("ss");
            camera = android.hardware.Camera.open(1);
        }
        if (camera != null && !isPreview) {
            try {
                Camera.Parameters params = camera.getParameters();
                android.hardware.Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
                parameters.setPreviewFpsRange(20, 30);
                parameters.setPictureFormat(ImageFormat.NV21);
                parameters.setPictureSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
                camera.setDisplayOrientation(90);
                camera.setPreviewDisplay(surfaceHolder);
                camera.setParameters(params);
                camera.setPreviewCallback(new StreamIt(main.clientIP));
                camera.startPreview();
                camera.autoFocus(null);

            } catch (Exception e) {
                e.printStackTrace();
            }
            isPreview = true;
        }
    }

    class StreamIt implements android.hardware.Camera.PreviewCallback {
        private String ipname;

        public StreamIt(String ipname) {
            this.ipname = ipname;
        }

        @Override
        public void onPreviewFrame(byte[] data, android.hardware.Camera camera) {
            android.hardware.Camera.Size size = camera.getParameters().getPreviewSize();
            try {

                YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);

                if (image != null) {
                    ByteArrayOutputStream outstream = new ByteArrayOutputStream();

                    image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, outstream);
                    Thread th = new SendVideoThread(outstream, ipname);
                    th.start();
                    outstream.flush();
                }
            } catch (Exception ex) {
                Log.e("Sys", "Error:" + ex.getMessage());
            }
        }
    }

    class SendVideoThread extends Thread {
        private byte byteBuffer[] = new byte[1024];
        private OutputStream outsocket;
        private ByteArrayOutputStream myoutputstream;
        private String ipname;

        public SendVideoThread(ByteArrayOutputStream myoutputstream, String ipname) {
            this.myoutputstream = myoutputstream;
            this.ipname = ipname;

//            try {
//                myoutputstream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

        public void run() {
            //TestText.setText("dsdsafa");
            Socket socket = null;
            try {

                //byte data[] = new byte[204800];
                InetAddress serverAddr = InetAddress.getByName("10.189.151.255");// TCPServer.SERVERIP
                Log.d("TCP", "C: Connecting...");



                // 应用Server的IP和端口建立Socket对象
                socket = new Socket(serverAddr, CAMERA_PORT);
                byteArray = myoutputstream.toByteArray();
                //TestText.setText("sffd");
                ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
                outsocket = socket.getOutputStream();
                TestText.setText("ddd");
                int amount, num = 0;
                while ((amount = inputStream.read(byteBuffer)) != -1) {
                    //TestText.setText("ddd");
                    outsocket.write(byteBuffer, 0, amount);
                    //TestText.setText("fsddffds");
                    //System.arraycopy(byteBuffer, 0, data, num, amount);
                    //num += amount;
                }
                myoutputstream.flush();
                myoutputstream.close();
                socket.close();

            }
            catch(UnknownHostException e)
            {
                Log.e("unknown", "192.168.1.100 is unkown server!");
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                TestText.setText("fds");

//                try {
//                    socket.close();
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }

    }
}


