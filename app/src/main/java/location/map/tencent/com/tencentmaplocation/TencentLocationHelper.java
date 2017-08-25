package location.map.tencent.com.tencentmaplocation;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Toast;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

/**
 * Created by zhrl on 2017\7\28 0028.
 *
 */

public class TencentLocationHelper implements Runnable, TencentLocationListener {

    public static final int FAILURE_DELAYTIME = 12000; // 定位超时时间
    private static long LAST_LOCATION_TIME;
    private static TencentLocation LOCATION,mLocation;
    public static final int LOCATION_CACHE_TIME = 15000;
    private Handler handler = new Handler();;
    private TencentLocationManager mLocationManager;
    private Context context = null;
    private OnGetLocation onGetLocation;

    public TencentLocationHelper(Context context) {
        this.context = context;
    }

    /**
     * 开始定位
     *
     * @param onGetLocation 定位完成回调接口
     */
    public void startLocation(OnGetLocation onGetLocation) {
        this.onGetLocation = onGetLocation;
        startLocation();
    }
    /**
     * 开始定位（如果在LOCATION_CACHE_TIME之内，则获取缓存定位数据，否则进行定位）
     *
     * @param onGetLocation 定位完成回调接口
     */
    public void startOrGetCacheLocation(OnGetLocation onGetLocation) {
        this.onGetLocation = onGetLocation;
        if (LOCATION != null && LAST_LOCATION_TIME > 0 && SystemClock.elapsedRealtime() - LAST_LOCATION_TIME < LOCATION_CACHE_TIME) {
            if (onGetLocation != null) {
                onGetLocation.getLocation(LOCATION);
            }
        } else {
            startLocation();
        }
    }

    private void startLocation() {

        mLocationManager = TencentLocationManager.getInstance(context);
        // 设置坐标系为 gcj-02, 缺省坐标为 gcj-02, 所以通常不必进行如下调用
        mLocationManager.setCoordinateType(com.tencent.map.geolocation.TencentLocationManager.COORDINATE_TYPE_GCJ02);
        // 创建定位请求
        final TencentLocationRequest request = TencentLocationRequest.create();
        // 修改定位请求参数, 定位周期 3000 ms
        request.setInterval(3000);
        /*
         * 当所设的整数值大于等于1000（ms）时，定位SDK内部使用定时定位模式。调用requestLocation(
         * )后，每隔设定的时间，定位SDK就会进行一次定位。如果定位SDK根据定位依据发现位置没有发生变化，就不会发起网络请求，
         * 返回上一次定位的结果；如果发现位置改变，就进行网络请求进行定位，得到新的定位结果。
         * 定时定位时，调用一次requestLocation，会定时监听到定位结果。
         */
        mLocationManager.requestLocationUpdates(request,
               this);
    }

    /**
     * 12秒内还没有定位成功即停止定位
     */
    @Override
    public void run() {
        if (mLocation == null) {
            stopLocation(); // 销毁掉定位
            if (onGetLocation != null) {
                onGetLocation.locationFail();
            }
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }

    @Override
    public void onLocationChanged(TencentLocation location, int error, String reason) {
        String msg = null;
        if (location != null) {
          // Toast.makeText(context,"getAddress："+location.getAddress(),Toast.LENGTH_SHORT).show();
            this.mLocation = location;// 判断超时机制
            if (location.getLatitude() == 0 && location.getLongitude() == 0) {
                if (onGetLocation != null) {
                    onGetLocation.locationFail();
                }
                LOCATION = null;
            } else {
                if (onGetLocation != null) {
                    onGetLocation.getLocation(location);
                }
                LOCATION = location;
                LAST_LOCATION_TIME = SystemClock.elapsedRealtime();
            }
            stop();
        } else {
            if (onGetLocation != null) {
                onGetLocation.locationFail();
            }
        }
    }

    /**
     * 结束定位，连本次的回调任务也取消
     */
    public void stop() {
        handler.removeCallbacks(this);
        stopLocation();
    }

    public void stopLocation() {
        mLocationManager.removeUpdates(this);
    }

    /**
     * 获取位置后回调
     */
    public interface OnGetLocation {
        void getLocation(TencentLocation location);
        void locationFail();
    }

}
