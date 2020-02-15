package com.xhy.neihanduanzi.lockdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xhy.neihanduanzi.activity.MainActivity;
import com.xhy.neihanduanzi.base.BaseActivity;
import com.xhy.neihanduanzi.lockdemo.util.LockPatternUtil;
import com.xhy.neihanduanzi.lockdemo.util.cache.ACache;
import com.xhy.neihanduanzi.lockdemo.util.constant.Constant;
import com.xhy.neihanduanzi.lockdemo.widget.LockPatternView;
import com.example.chaokun.neihanduanzi.R;
import com.lidroid.xutils.exception.DbException;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sym on 2015/12/24.
 */
public class GestureLoginActivity extends BaseActivity {

    @BindView(R.id.lockPatternView)
    LockPatternView lockPatternView;
    @BindView(R.id.messageTv)
    TextView messageTv;
    @BindView(R.id.forgetGestureBtn)
    Button forgetGestureBtn;

    private ACache aCache;
    private static final long DELAYTIME = 600l;
    private byte[] gesturePassword;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_gesture_login);
//        ButterKnife.bind(this);
//        this.init();
//    }

    @Override
    protected int getContentView() {
        return 0;
    }

    private void init() {
        aCache = ACache.get(GestureLoginActivity.this);
        //得到当前用户的手势密码
        gesturePassword = aCache.getAsBinary(Constant.GESTURE_PASSWORD);
        lockPatternView.setOnPatternListener(patternListener);
        updateStatus(Status.DEFAULT);
    }

    private LockPatternView.OnPatternListener patternListener = new LockPatternView.OnPatternListener() {

        @Override
        public void onPatternStart() {
            lockPatternView.removePostClearPatternRunnable();
        }

        @Override
        public void onPatternComplete(List<LockPatternView.Cell> pattern) {
            if (pattern != null) {
                if (LockPatternUtil.checkPattern(pattern, gesturePassword)) {
                    updateStatus(Status.CORRECT);
                    Intent intent = new Intent(GestureLoginActivity.this, MainActivity.class);
                    intent.putExtra("open_locksreen", true);
                    startActivity(intent);
                    finish();
                } else {
                    updateStatus(Status.ERROR);
                }
            }
        }
    };

    /**
     * 更新状态
     *
     * @param status
     */
    private void updateStatus(Status status) {
        messageTv.setText(status.strId);
        messageTv.setTextColor(getResources().getColor(status.colorId));
        switch (status) {
            case DEFAULT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case ERROR:
                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockPatternView.postClearPatternRunnable(DELAYTIME);
                break;
            case CORRECT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                loginGestureSuccess();
                break;
        }
    }

    /**
     * 手势登录成功（去首页）
     */
    private void loginGestureSuccess() {
        Toast.makeText(GestureLoginActivity.this, "success", Toast.LENGTH_SHORT).show();
    }

    /**
     * 忘记手势密码（去账号登录界面）
     */
    @OnClick(R.id.forgetGestureBtn)
    void forgetGesturePasswrod() {
        Intent intent = new Intent(GestureLoginActivity.this, OpenLockScreenActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_gesture_login;
    }

    @Override
    public void initView(View view) throws DbException {
        System.out.println("######### GestureLoginActivity init #############");
        init();
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    private enum Status {
        //默认的状态
        DEFAULT(R.string.gesture_default, R.color.grey_a5a5a5),
        //密码输入错误
        ERROR(R.string.gesture_error, R.color.red_f4333c),
        //密码输入正确
        CORRECT(R.string.gesture_correct, R.color.grey_a5a5a5);

        private Status(int strId, int colorId) {
            this.strId = strId;
            this.colorId = colorId;
        }

        private int strId;
        private int colorId;
    }

}
