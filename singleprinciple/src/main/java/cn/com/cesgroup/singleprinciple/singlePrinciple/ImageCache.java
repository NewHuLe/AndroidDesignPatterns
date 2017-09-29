package cn.com.cesgroup.singleprinciple.singlePrinciple;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Author        Hule  hu.le@cesgroup.com.cn
 * Date          2017/9/29 15:24
 * Description:  TODO: 图片缓存
 */

public class ImageCache {
    // lruCache图片缓存
    LruCache<String, Bitmap> bitmapLruCache;

    public ImageCache() {
        initImageCache();
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

    public void put(String url, Bitmap bitmap) {
        bitmapLruCache.put(url, bitmap);
    }

    public Bitmap get(String url) {
        return bitmapLruCache.get(url);
    }

}
