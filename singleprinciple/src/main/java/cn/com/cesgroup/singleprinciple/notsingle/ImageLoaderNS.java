package cn.com.cesgroup.singleprinciple.notsingle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author        Hule  hu.le@cesgroup.com.cn
 * Date          2017/9/29 11:07
 * Description:  TODO: 图片加载类，非单一职责
 */

public class ImageLoaderNS {

    // lruCache图片缓存
    LruCache<String, Bitmap> bitmapLruCache;
    // 创建缓存池跟系统的cpu数量一致
    ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private ImageLoaderNS() {
        initImageCache();
    }

    public static ImageLoaderNS newInstance() {
        return ImageLoader1Holder.IMAGE_LOADER_NS;
    }

    /**
     * 静态内部类的实现方式
     */
    private static class ImageLoader1Holder {
        private static final ImageLoaderNS IMAGE_LOADER_NS = new ImageLoaderNS();
    }

    /**
     * 初始化
     */
    private void initImageCache() {
        // 计算可使用的最大内存
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 取四分之一可用的作为缓存
        final int cacheSize = maxMemory / 4;
        bitmapLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    public void displayImage(final String url, final ImageView imageView) {
        imageView.setTag(url);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = downLoadImage(url);

                if (bitmap == null) {
                    return;
                }
                if (imageView.getTag().equals(url)) {
                    imageView.setImageBitmap(bitmap);
                    bitmapLruCache.put(url, bitmap);
                }

            }
        });
    }

    private Bitmap downLoadImage(String imageUrl) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            bitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());
            urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
