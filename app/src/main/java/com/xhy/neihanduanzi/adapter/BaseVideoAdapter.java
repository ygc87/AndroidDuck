package com.xhy.neihanduanzi.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.example.chaokun.neihanduanzi.R;
import com.xhy.neihanduanzi.activity.LoginActivity;
import com.xhy.neihanduanzi.callback.LoadFinishCallBack;
import com.xhy.neihanduanzi.callback.LoadResultCallBack;
import com.xhy.neihanduanzi.model.bean.Video;
import com.xhy.neihanduanzi.utils.DisplayUtils;
import com.xhy.neihanduanzi.utils.StringUtils;
import com.xhy.neihanduanzi.utils.ToastUtils;
import com.xhy.neihanduanzi.videoplayer.VideoPlayerView;
import com.xhy.neihanduanzi.view.ResizableImageView;
import com.xhy.neihanduanzi.widget.XHYPlayer;

import java.util.List;

/**
 * Created by mkt on 2018/5/30.
 */

public class BaseVideoAdapter extends BaseQuickAdapter<Video, BaseViewHolder> {

    /**
     * 免费视频
     */
    private static final int FREE_VIDEO = 100;
    /**
     * 短视频
     */
    private static final int VIDEO = 200;
    /**
     * 广告
     */
    private static final int AD = 400;


    public int downX = 0;
    public int downY = 0;
    public int tempX = 0;
    public int tempY = 0;
    public int moveX = 0;
    public int moveY = 0;

    public long currentMS = 0;

    public int page;

    private int mDataType = -1;

    private Activity mActivity;

    private Context mContext;

    protected LayoutInflater mInflater;

    public LoadResultCallBack mLoadResultCallBack;
    public LoadFinishCallBack mLoadFinisCallBack;

    //点击事件监听
    private OnItemClickListener onItemClickListener;

    private RequestOptions userOptions;

    //是否显示删除按钮
    private boolean isDeleteVisible;

    public BaseVideoAdapter(Activity activity, @Nullable List<Video> data, LoadFinishCallBack loadFinisCallBack, LoadResultCallBack loadResultCallBack) {
        super(data);
        mActivity = activity;
        mContext = mActivity.getApplicationContext();
        this.mInflater = LayoutInflater.from(mContext);
        initListener();
        mLoadResultCallBack = loadResultCallBack;
        mLoadFinisCallBack = loadFinisCallBack;
        userOptions = new RequestOptions();
        //options.fitCenter();
        userOptions.placeholder(R.drawable.default_user);
        userOptions.diskCacheStrategy(DiskCacheStrategy.ALL);

        //Step.1
        setMultiTypeDelegate(new MultiTypeDelegate<Video>() {
            @Override
            protected int getItemType(Video video) {
                if (video.getLabel() == 0) {
                    return FREE_VIDEO;
                } else if (video.getLabel() == 1) {
                    return VIDEO;
                } else {
                    return AD;
                }
            }
        });
        //Step .2
        getMultiTypeDelegate()
                .registerItemType(FREE_VIDEO, R.layout.item_free_video)//免费视频布局
                .registerItemType(VIDEO, R.layout.item_video)//视频布局
                .registerItemType(AD, R.layout.item_ad);//广告布局
    }

    public void setDeleteVisible(boolean isDeleteVisible) {
        this.isDeleteVisible = isDeleteVisible;
    }

    public int getAdapterType() {
        return mDataType;
    }

    private void setVideoCoverImage(final boolean isPayed, final String url, final BaseViewHolder holder) {
        final VideoPlayerView videoPlayerView = holder.getView(R.id.videoplayer);

        Glide.with(mContext)
                .asBitmap()
                .load(url)
                .transition(BitmapTransitionOptions.withCrossFade())
                .thumbnail(0.2f)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        if (isPayed) {
                            holder.getView(R.id.iv_payed).setVisibility(View.VISIBLE);
                        }
                        return false;
                    }
                })
                .into(videoPlayerView.getPreviewImage());
    }

    @Override
    protected void convert(BaseViewHolder h, final Video video) {
        if (h.getItemViewType() == FREE_VIDEO) {
            TextView tvTitle = h.getView(R.id.video_title);
            tvTitle.setText(video.getTitle());
            RelativeLayout llTitle = h.getView(R.id.ll_video_title);
            VideoPlayerView videoplayer = h.getView(R.id.videoplayer);
            videoplayer.setVisibility(View.VISIBLE);
            XHYPlayer xhyPlayer = new XHYPlayer(mActivity, videoplayer, h);
            xhyPlayer.setPosition(h.getAdapterPosition());
            xhyPlayer.setVideo(video);
            xhyPlayer.addView(llTitle);
            videoplayer.setTag(xhyPlayer);
            setVideoCoverImage(video.isPayed(), video.getCoverImageURI(), h);
        } else if (h.getItemViewType() == VIDEO) {
            VideoPlayerView videoplayer = h.getView(R.id.videoplayer);
            videoplayer.setVisibility(View.VISIBLE);
            XHYPlayer xhyPlayer = new XHYPlayer(mActivity, videoplayer, h);
            xhyPlayer.setPosition(h.getAdapterPosition());
            xhyPlayer.setVideo(video);
            videoplayer.setTag(xhyPlayer);
            TextView tvTitle = h.getView(R.id.tv_title);
            tvTitle.setText(video.getTitle());

            TextView tvComment = h.getView(R.id.tv_comment_count);
            tvComment.setText(video.getCommentCount());

            if (video.getUser() != null) {
                TextView tvAuthor = h.getView(R.id.tv_author);
                ImageView ivAvatar = h.getView(R.id.iv_user_icon);
                tvAuthor.setText(video.getUser().getName());
                Glide.with(mContext)
                        .asBitmap()
                        .load(video.getUser().getAvatar())
                        .thumbnail(0.2f)
                        .into(ivAvatar);
            }

            ImageView ivPayed = h.getView(R.id.iv_payed);
            final boolean isPayed = video.isPayed();
            if (isPayed) {
                ivPayed.setVisibility(View.VISIBLE);
            } else {
                ivPayed.setVisibility(View.GONE);
            }
            //在播放器中是否显示
            xhyPlayer.addView(ivPayed);
            TextView tvDigg = h.getView(R.id.tv_digg);
            tvDigg.setText(video.getDiggCount());

            if (video.isDigg() == 1) {
                Drawable drawableLeft = mContext.getResources().getDrawable(
                        R.drawable.icon_like_light);
                tvDigg.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                        null, null, null);
                tvDigg.setTextColor(Color.parseColor("#f66467"));
            } else {
                Drawable drawableLeft = mContext.getResources().getDrawable(
                        R.drawable.icon_like_nor);
                tvDigg.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                        null, null, null);
                tvDigg.setTextColor(Color.parseColor("#999999"));
            }

            TextView tvCollected = h.getView(R.id.tv_collected);
            tvCollected.setText(video.getUserLikeCount());

            if (video.isCollected() == 1) {
                Drawable drawableLeft = mContext.getResources().getDrawable(
                        R.drawable.icon_favorite_light);
                tvCollected.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                        null, null, null);
                tvCollected.setTextColor(Color.parseColor("#f66467"));
            } else {
                Drawable drawableLeft = mContext.getResources().getDrawable(
                        R.drawable.icon_favorite_gray_nor);
                tvCollected.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                        null, null, null);
                tvCollected.setTextColor(Color.parseColor("#999999"));
            }

            ImageView imDelete = h.getView(R.id.im_delete);

            if (isDeleteVisible) {
                imDelete.setVisibility(View.VISIBLE);
            } else {
                imDelete.setVisibility(View.GONE);
            }

            tvDigg.setTag(h);
            tvCollected.setTag(h);
            imDelete.setTag(h);
            tvComment.setTag(h);

            LinearLayout item = h.getView(R.id.item_layout);

            final int position = h.getAdapterPosition();

            item.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    if (view.getId() == R.id.item_layout) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                downX = (int) event.getX();
                                downY = (int) event.getY();
                                moveX = 0;
                                moveY = 0;
                                currentMS = System.currentTimeMillis();
                                break;
                            case MotionEvent.ACTION_MOVE:
                                tempX = (int) event.getX();
                                tempY = (int) event.getY();
                                moveX += Math.abs(event.getX() - downX);//X轴距离
                                moveY += Math.abs(event.getY() - downY);//y轴距离
                                downX = (int) event.getX();
                                downY = (int) event.getY();
                                break;
                            case MotionEvent.ACTION_UP:
                                long moveTime = System.currentTimeMillis() - currentMS;//移动时间
                                //判断是否继续传递信号
                                if (moveTime < 200 && (moveX < 20 || moveY < 20)) {
                                    onItemClickListener.onItemClick(null, view, position);
                                    return true; //不再执行后面的事件，在这句前可写要执行的触摸相关代码。点击事件是发生在触摸弹起后
                                }
                                break;
                        }
                        return true;
                    }
                    return false;
                }
            });

            tvDigg.setOnClickListener(onClickListener);
            tvCollected.setOnClickListener(onClickListener);
            tvComment.setOnClickListener(onClickListener);
            imDelete.setOnClickListener(onClickListener);
            setVideoCoverImage(video.isPayed(), video.getCoverImageURI(), h);
        } else if (h.getItemViewType() == AD) {
            final ResizableImageView imAd = h.getView(R.id.ad_item);

            if(!StringUtils.isEmpty(video.getAdButtonText())){
                TextView tvDownLodaButton = h.getView(R.id.tv_ad_button);
                tvDownLodaButton.setText(video.getAdButtonText());
                tvDownLodaButton.setVisibility(View.VISIBLE);
                tvDownLodaButton.setTag(h);
                tvDownLodaButton.setOnClickListener(onClickListener);
            }

            if (video.getUser() != null) {
                TextView tvAuthor = h.getView(R.id.tv_author);
                ImageView ivAvatar = h.getView(R.id.iv_user_icon);
                tvAuthor.setText(video.getUser().getName());
                Glide.with(mContext)
                        .asBitmap()
                        .load(video.getUser().getAvatar())
                        .thumbnail(0.2f)
                        .into(ivAvatar);
            }

            TextView tvTitle = h.getView(R.id.tv_title);
            tvTitle.setText(video.getTitle());
            imAd.setTag(h);
            final FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams) imAd.getLayoutParams();
            linearParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
            Glide.with(mContext)
                    .asBitmap()
                    .load(video.getContent())
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .thumbnail(0.2f)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                            Drawable drawable = new BitmapDrawable(bitmap);
                            imAd.setBackgroundDrawable(drawable);
                            int width = DisplayUtils.getDisplayWidth(mContext);
                            linearParams.height = (width * bitmap.getHeight()) / bitmap.getWidth();
                        }
                    });
            imAd.setOnClickListener(onClickListener);
        }
    }

    @Override
    public void onViewRecycled(BaseViewHolder holder) {
        BaseViewHolder h = holder;
        if (h != null) {
            ImageView ad = h.getView(R.id.ad_item);
            if (ad != null) {
                ad.setBackground(mContext.getResources().getDrawable(R.drawable.player_preview));
            }
        }
        super.onViewRecycled(holder);
    }

    public void callLoginPage() {
        ToastUtils.showToast(mContext, "请登录");
        LoginActivity.show(mContext);
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
            BaseViewHolder holder = (BaseViewHolder) v.getTag();
            if (holder != null) {
                onClick(v, holder.getAdapterPosition(), holder.getItemId());
            }
        }

        public abstract void onClick(View view, int position, long itemId);
    }
}
