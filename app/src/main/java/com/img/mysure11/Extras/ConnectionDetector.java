package com.img.mysure11.Extras;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class ConnectionDetector {

    private Context _context;

    public ConnectionDetector(Context context){
        this._context = context;
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager cn=(ConnectivityManager)_context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cn!=null)
        {
            NetworkInfo info=cn.getActiveNetworkInfo();
            if(info!=null && info.isConnected())
            {
                return true;

              /*  Runtime runtime = Runtime.getRuntime();
                try {

                    Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
                    int     exitValue = ipProcess.waitFor();
                    return (exitValue == 0);

                } catch (IOException e)          { e.printStackTrace(); }
                catch (InterruptedException e) { e.printStackTrace(); }

                return false;
*/
            }

        }
        return false;
    }



}
