package com.service.util;

/**
 * @author Sumit Kumar Maji
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyService extends Service
{
	public void onCreate() {
    }

    @Override
    public void onDestroy() {
    }
    public int onStartCommand(Intent intent, int flags, int startId) {

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
