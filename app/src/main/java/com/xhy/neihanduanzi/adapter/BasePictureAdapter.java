package com.xhy.neihanduanzi.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.example.chaokun.neihanduanzi.R;
import com.xhy.neihanduanzi.callback.LoadFinishCallBack;
import com.xhy.neihanduanzi.callback.LoadResultCallBack;
import com.xhy.neihanduanzi.model.bean.Picture;

import java.util.List;


/**
 * Created by mkt on 2018/6/1.
 */

public class BasePictureAdapter extends BaseQuickAdapter<Picture, BaseViewHolder> {

    /**
     * 图片
     */
    private static final int PICTURE = 100;
    /**
     * 广告
     */
    private static final int AD = 200;

    public Activity mActivity;

    private RequestOptions options;

    private boolean isDeleteVisible;

    //点击事件监听
    private OnItemClickListener onItemClickListener;

    public BasePictureAdapter(Activity activity, @Nullable List<Picture> data, LoadFinishCallBack loadFinisCallBack, LoadResultCallBack loadResultCallBack) {
        super(null);
        mActivity = activity;
        initListener();
        options = new RequestOptions();
        //options.skipMemoryCache(true);
        options.diskCacheStrategy(DiskCacheStrategy.ALL);

        //Step.1
        setMultiTypeDelegate(new MultiTypeDelegate<Picture>() {
            @Override
            protected int getItemType(Picture picture) {
                return PICTURE;
            }
        });
        //Step .2
        getMultiTypeDelegate()
                .registerItemType(PICTURE, R.layout.item_picture);//视频布局
        //.registerItemType(AD, R.layout.item_ad);//广告布局
    }

    @Override
    protected void convert(BaseViewHolder h, Picture item) {
        //h.setIsRecyclable(false);//禁止图片holder复用
        final String picUrl = Picture.Image.getCoverImagePath(item.getImages());

        TextView tvTitle = h.getView(R.id.title_picture);
        TextView tvDigg = h.getView(R.id.tv_digg);
        TextView tvCollected = h.getView(R.id.tv_collected);
        TextView tvPicCount = h.getView(R.id.tv_pic_count);

        tvTitle.setText(item.getTitle());
        tvDigg.setText(String.valueOf(item.getDiggCount()));
        tvCollected.setText(String.valueOf(item.getCollectCount()));
        tvPicCount.setText("图" + String.valueOf(item.getPicCount()));

        tvDigg.setTag(R.id.tv_digg, h);
        tvCollected.setTag(R.id.tv_collected, h);
        tvTitle.setTag(R.id.title_picture, h);

        //已经点过赞的图片
        if (item.isDigg() == 1) {
            Drawable drawableLeft = mActivity.getResources().getDrawable(
                    R.drawable.icon_like_light);
            tvDigg.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
            tvDigg.setTextColor(Color.parseColor("#f66467"));
        } else {
            Drawable drawableLeft = mActivity.getResources().getDrawable(
                    R.drawable.icon_like_nor);
            tvDigg.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
            tvDigg.setTextColor(Color.parseColor("#999999"));
        }

        //已经收藏过的图片
        if (item.isCollected()) {
            Drawable drawableLeft = mActivity.getResources().getDrawable(
                    R.drawable.icon_favorite_light);
            tvCollected.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
            tvCollected.setTextColor(Color.parseColor("#f66467"));
        } else {
            Drawable drawableLeft = mActivity.getResources().getDrawable(
                    R.drawable.icon_favorite_gray_nor);
            tvCollected.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
            tvCollected.setTextColor(Color.parseColor("#999999"));
        }
        tvDigg.setOnClickListener(onClickListener);
        tvCollected.setOnClickListener(onClickListener);
        ImageView ivContent = h.getView(R.id.content_picture);
        ImageView ivPayed = h.getView(R.id.iv_payed);
        ivContent.setOnClickListener(onClickListener);

        ivContent.setTag(R.id.content_picture, h);

        ImageView imDelete = h.getView(R.id.im_delete);
        imDelete.setTag(R.id.im_delete, h);

        if (isDeleteVisible) {
            imDelete.setVisibility(View.VISIBLE);
        } else {
            imDelete.setVisibility(View.GONE);
        }

        imDelete.setOnClickListener(onClickListener);

        loadImage(item.isPayed(), picUrl, ivContent, ivPayed);
    }

    public void setDeleteVisible(boolean isDeleteVisible) {
        this.isDeleteVisible = isDeleteVisible;
    }

    private void loadImage(final boolean isPayed, final String url, final ImageView imgView, final ImageView ivPayed) {
        Glide.with(mActivity)
                .asDrawable()
                .load(url)
                .apply(options)
                .transition(new DrawableTransitionOptions().crossFade())
                .thumbnail(0.2f)
                .listener(new RequestListener<Drawable>() {

                    @Override
                    public boolean onResourceReady(Drawable drawable, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (isPayed) {
                            ivPayed.setVisibility(View.VISIBLE);
                        } else {
                            ivPayed.setVisibility(View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imgView);
    }

    @Override
    public void onViewRecycled(BaseViewHolder holder) {
        if (holder != null) {
            ImageView ivContent = holder.getView(R.id.content_picture);
            Glide.with(mActivity).clear(ivContent);
            ImageView ivPayed = holder.getView(R.id.iv_payed);
            ivPayed.setVisibility(View.GONE);
        }
        super.onViewRecycled(holder);
    }

    private OnClickListener onClickListener;

    /**
     * 初始化listener
     */

    private void initListener() {
        final BaseQuickAdapter adapter = this;
        onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view, int position, long itemId) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(adapter, view, position);
            }
        };
    }

    /**
     * 添加项点击事件
     *
     * @param onItemClickListener the RecyclerView item click listener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 可以共用同一个listener，相对高效
     */
    public static abstract class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                BaseViewHolder holder = (BaseViewHolder) v.getTag(v.getId());
                if (holder != null) {
                    onClick(v, holder.getAdapterPosition(), holder.getItemId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public abstract void onClick(View view, int position, long itemId);
    }

}
