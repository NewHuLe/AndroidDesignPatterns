package cn.com.cesgroup.singleprinciple.singlePrinciple;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author        Hule  hu.le@cesgroup.com.cn
 * Date          2017/9/29 15:23
 * Description:  TODO: 单一职责
 */

public class ImageLoaderSP {

    private ImageCache imageCache;
    // 创建缓存池跟系统的cpu数量一致
    ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private ImageLoaderSP() {
        imageCache = new ImageCache();
    }

    public static ImageLoaderSP newInstance() {
        return ImageLoaderSP.ImageLoader1Holder.imageLoader1;
    }

    /**
     * 静态内部类的实现方式
     */
    private static class ImageLoader1Holder {
        private static final ImageLoaderSP imageLoader1 = new ImageLoaderSP();
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
                    imageCache.put(url, bitmap);
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
