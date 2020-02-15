package com.xhy.neihanduanzi.utils.glide;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

/**
 * Description: ImageLoader
 * Creator: yxc
 * date: 2016/9/21 9:53
 */
public class ImageLoader {

    public static void load(Context context, String url, ImageView iv) {
        Glide.with(context).asBitmap()
                .load(url)
                .thumbnail(0.2f)
                .into(iv);
    }

    public static void load(Context context, String url, ImageView iv, RequestOptions options) {
        Glide.with(context).asBitmap()
                .apply(options)
                .load(url)
                .thumbnail(0.2f)
                .into(iv);
    }

    public static void load(Activity activity, String url, ImageView iv) {    //使用Glide加载圆形ImageView(如头像)时，不要使用占位图
        Glide.with(activity).asBitmap()
                .load(url)
                .thumbnail(0.2f)
                .into(iv);
    }

    public static void load(Activity activity, String url, ImageView iv, RequestOptions options) {
        Glide.with(activity).asBitmap()
                .apply(options)
                .load(url)
                .thumbnail(0.2f)
                .into(iv);
    }

    public static void loadImage(final Activity activity, final String url, ImageView iv, RequestOptions options) {
        Glide.with(activity)
                .asBitmap()
                .load(url)
                .apply(options)
                .transition(BitmapTransitionOptions.withCrossFade())
                .thumbnail(0.2f)
//                .listener(new RequestListener<Bitmap>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                        PicSizeEntity picSizeEntity = map.get(url);
//                        if (picSizeEntity == null || picSizeEntity.isNull()) {
//                            int width = DisplayUtils.getDisplayWidth(activity);
//                            int height = (width * resource.getHeight() / resource.getWidth());
//                            map.put(url, new PicSizeEntity(width, height));
//                        }
//                        return false;
//                    }
//                })
                .into(iv);
    }


    public static void loadGif(Context context, int resource, ImageView iv) {
        Glide.with(context)
                .asGif()
                .load(resource)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(iv);
    }

    public static void loadGif(Activity activity, int resource, ImageView iv) {    //使用Glide加载圆形ImageView(如头像)时，不要使用占位图
        Glide.with(activity)
                .asGif()
                .load(resource)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(iv);
    }
}
