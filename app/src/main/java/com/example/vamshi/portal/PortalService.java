package com.example.vamshi.portal;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by vamshi on 16-01-2017.
 */

public class PortalService extends Service {

    static final int MSG_REGISTER_CLIENT = 1;
    static final int MSG_UNREGISTER_CLIENT = 2;
    static final int MSG_SET_VALUE = 3;
    static final int CUSTOM_MSG = 4;
    static final int CUSTOM_DELETE_MSG = 5;
    private static final String TAG = PortalService.class.getSimpleName();
    static ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    static ArrayList<String> mClientsPackages = new ArrayList<String>();
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    int mValue = 0;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: Service");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Server Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service onDestroy: killed connection");
    }

    static class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
//                case MSG_REGISTER_CLIENT:
//                    mClients.add(msg.replyTo);
//                    try {
//                        msg.replyTo.send(Message.obtain(null,
//                                CUSTOM_MSG));
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                    Log.d(TAG, "Server register client handleMessage: "+mClients.size());
//                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    try {
                        Bundle i = new Bundle();
                        i.putString("str1", "");

                        msg.replyTo.send(Message.obtain(null,
                                CUSTOM_DELETE_MSG, 1));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "Server unregister client handleMessage: " + mClients.size());
                    break;
                case CUSTOM_MSG:
//                    try {
//                        msg.replyTo.send(Message.obtain(null,
//                                CUSTOM_MSG, 1));
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
                    if (!mClients.contains(msg.replyTo)) {
                        mClients.add(msg.replyTo);
                    }
                    Log.d(TAG, "Server register client handleMessage: " + mClients.size());
                    Log.d(TAG, "Server gets client ID: " + msg.getData().get("str1"));
                    if (!mClientsPackages.contains(msg.getData().get("str1").toString())) {
                        mClientsPackages.add(msg.getData().get("str1").toString());
                    }
                    Log.d(TAG, "Server clients list: " + mClientsPackages.toString());
                    try {
                        msg.replyTo.send(Message.obtain(null, CUSTOM_MSG));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
                case CUSTOM_DELETE_MSG:
                    try {
                        msg.replyTo.send(Message.obtain(null,
                                CUSTOM_DELETE_MSG));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    if (mClients.contains(msg.replyTo)) {
                        Log.d(TAG, "Contains in mClients ");
                        mClients.remove(msg.replyTo);
                    }
                    Log.d(TAG, "Delete Package Name : " + msg.getData().get("str1"));
                    Log.d(TAG, "Before Server clients handleMessage: " + mClientsPackages.toString());
//                    for (String s : mClientsPackages) {
//                        if (s.equals(msg.getData().get("str1"))) {
//                            mClientsPackages.remove(s);
//                        }
//                    }
                    Iterator<String> i = mClientsPackages.iterator();
                    while (i.hasNext()) {
                        if (i.next().equals(msg.getData().get("str1"))) {
                            i.remove();
                        }
                    }
                    Log.d(TAG, "After Server clients handleMessage: " + mClientsPackages.toString());
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
