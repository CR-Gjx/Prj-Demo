package com.example.gcls.prj_demo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class Bluetooth extends Service {
    public Bluetooth() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }
}
