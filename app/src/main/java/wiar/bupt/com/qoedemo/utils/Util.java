package wiar.bupt.com.qoedemo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2017/7/25.
 */

public class Util {

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvaliable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) (context
                .getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        return !(networkinfo == null || !networkinfo.isAvailable());
    }

    /**
     * 判断网络类型是否为wifi
     *
     * @param context
     * @return
     */
    public static boolean isWifiNetwrokType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if (info != null && info.isAvailable()) {
            if (info.getTypeName().equalsIgnoreCase("wifi")) {
                return true;
            }
        }
        return false;
    }

}
