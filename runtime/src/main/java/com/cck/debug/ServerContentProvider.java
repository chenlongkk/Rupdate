package com.cck.debug;

import android.app.ActivityManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.widget.Toast;
import com.cck.debug.command.CommandReceiver;
import com.cck.debug.common.MessageType;
import com.cck.debug.server.Server;
import com.cck.debug.utils.Log;

public class ServerContentProvider extends ContentProvider implements CommandReceiver {
    private Handler mHandler = new Handler();
    @Override
    public boolean onCreate() {
        Log.i("Server start");
        if(isMainProcess()) {
            Server.create(this,getContext().getPackageName()+"_plugin").startServer();
        }
        return true;
    }


    private boolean isMainProcess() {
        // TODO(dancol): unconditionally start the IR server for _every_ process and just make
        // the host decide the process to which it wants to talk.  That way, it's more regular,
        // and we don't need to schlep over to ActivityManagerService via binder just for
        // getRunningAppProcesses().
        String packageName = getContext().getPackageName();
        boolean isMainProcess = false;
        if (packageName != null) {
            boolean foundPackage = false;
            int pid = Process.myPid();
            ActivityManager manager = (ActivityManager) getContext().getSystemService(
                    Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
                if (packageName.equals(processInfo.processName)) {
                    foundPackage = true;
                    if (processInfo.pid == pid) {
                        isMainProcess = true;
                        break;
                    }
                }
            }
            if (!isMainProcess && !foundPackage) {
                // If for some reason we didn't even find the main package, consider this
                // process the main process anyway. This safeguards against apps doing
                // strange things with the process name.
                isMainProcess = true;
            }
        }
        return isMainProcess;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    @WorkerThread
    public int handlePing() {
        return MessageType.MSG_RESULT_SUCCESS;
    }

    @Override
    @WorkerThread
    public int handleToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
            }
        });
        return MessageType.MSG_RESULT_SUCCESS;
    }

    @Override
    @WorkerThread
    public int handleRestart() {
        return 0;
    }

    @Override
    @WorkerThread
    public int handleUpdate(final String pluginName, final int pluginVersion, byte[] data, int len) {
        if(TextUtils.isEmpty(pluginName) || len == 0)  {
            return MessageType.MSG_RESULT_FAIL;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),pluginName+":"+pluginVersion,Toast.LENGTH_SHORT).show();
            }
        });
        return MessageType.MSG_RESULT_SUCCESS;
    }

    private void runOnUiThread(final Runnable runnable) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try{
                    if(runnable != null) {
                        runnable.run();
                    }
                }catch (Exception ignore){
                    Log.e(ignore);
                }
            }
        });
    }
}
