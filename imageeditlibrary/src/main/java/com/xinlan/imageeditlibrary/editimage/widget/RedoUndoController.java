package com.xinlan.imageeditlibrary.editimage.widget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouch;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by panyi on 2017/11/15.
 * <p>
 * 我的孩子叫好帅
 * 前一步 后一步操作类
 */
public class RedoUndoController implements View.OnClickListener {
    private View mRootView;
    private View mUndoBtn;//撤销按钮
    private View mRedoBtn;//重做按钮
    private EditImageActivity mActivity;
    private EditCache mEditCache = new EditCache();//保存前一次操作内容 用于撤销操作
    private ImageViewTouch imgview;//图片
    private boolean booleanConnect = false;
    private EditCache.ListModify mObserver = new EditCache.ListModify() {
        @Override
        public void onCacheListChange(EditCache cache) {
            updateBtns();
        }
    };
    private boolean longPress;
    private Bitmap lastBitmap;
    private Bitmap preBitmap;

    float atime;
    float aX, aY;
    boolean mPressBreak = false;
    public RedoUndoController(EditImageActivity activity, ImageViewTouch mainImage, View panelView) {
        this.mActivity = activity;
        this.mRootView = panelView;
        this.imgview = mainImage;
        longPress = false;
        mUndoBtn = mRootView.findViewById(R.id.uodo_btn);
        mRedoBtn = mRootView.findViewById(R.id.redo_btn);

        mUndoBtn.setOnClickListener(this);
        mRedoBtn.setOnClickListener(this);

        imgview.setLongClickable(true);

        imgview.setOnClickListener(null);
        //动作
        imgview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                LongTouchSendCmd(mActivity, event);
                return true;
            }
        });
        updateBtns();
        mEditCache.addObserver(mObserver);
    }
    //长按返回原图
    private void LongTouchSendCmd(final Activity activity, MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                atime = (event.getEventTime() - event.getDownTime()) / 1000;
                if ((Math.abs((event.getX() - aX)) > 10) || (Math.abs(event.getY() - aY) > 10)) {
                    atime = 0;
                    mPressBreak = true;
                }
                if (!mPressBreak) {
                    switch ((int)atime){
                        case 1:
                            if (!longPress){
                                longPress = true;
                                undoClick(1);
                            }
                            break;
                    }
                }
                 break;
            case MotionEvent.ACTION_DOWN: {
                aX = event.getX();
                aY = event.getY();
                 mPressBreak = false;
                break;
            }
            case MotionEvent.ACTION_UP:{
                if (longPress){
                    longPress = false;
                    redoClick(1);
                }

            }
        }

    }
    private Runnable r = new Runnable() {
        @Override
        public void run() {

        }
    };


    /**
     * * 判断是否有长按动作发生 * @param lastX 按下时X坐标 * @param lastY 按下时Y坐标 *
     *
     * @param thisX
     *            移动时X坐标 *
     * @param thisY
     *            移动时Y坐标 *
     * @param lastDownTime
     *            按下时间 *
     * @param thisEventTime
     *            移动时间 *
     * @param longPressTime
     *            判断长按时间的阀值
     */
    static boolean isLongPressed(float lastX, float lastY, float thisX, float thisY, long lastDownTime, long thisEventTime, long longPressTime) {
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        long intervalTime = thisEventTime - lastDownTime;
        if (offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime) {
            return true;
        }
        return false;
    }
    public void switchMainBit(Bitmap mainBitmap, Bitmap newBit) {
        if (mainBitmap == null || mainBitmap.isRecycled())
            return;

        mEditCache.push(mainBitmap);
        mEditCache.push(newBit);
    }


    //长按撤销，松开取消撤销

    @Override
    public void onClick(View v) {
        if (v == mUndoBtn) {
            //撤销
            undoClick(2);
        } else if (v == mRedoBtn) {
            // 取消撤销
            redoClick(2);
        }//end if
    }


    /**
     * 撤销操作
     */
    protected void undoClick(int i) {
        //System.out.println("Undo!!!");
//        Bitmap lastBitmap = mEditCache.getNextCurrentBit();
        if (i==2){
            lastBitmap = mEditCache.getNextCurrentBit();
        }else {
            lastBitmap = mEditCache.getnitialCurrentBit();
        }

        if (lastBitmap != null && !lastBitmap.isRecycled()) {
            mActivity.changeMainBitmap(lastBitmap, false);
        }
    }

    /**
     * 取消撤销
     */
    protected void redoClick(int i) {
        //System.out.println("Redo!!!");
        if (i==2){
            preBitmap = mEditCache.getPreCurrentBit();
        }else {
            preBitmap = mEditCache.getIcPreCurrentBit();
        }
        if (preBitmap != null && !preBitmap.isRecycled()) {
            mActivity.changeMainBitmap(preBitmap, false);
        }
    }

    /**
     * 根据状态更新按钮显示
     */
    public void updateBtns() {
        //System.out.println("缓存Size = " + mEditCache.getSize() + "  current = " + mEditCache.getCur());
        //System.out.println("content = " + mEditCache.debugLog());
        mUndoBtn.setVisibility(mEditCache.checkNextBitExist() ? View.VISIBLE : View.INVISIBLE);
        mRedoBtn.setVisibility(mEditCache.checkPreBitExist() ? View.VISIBLE : View.INVISIBLE);
    }

    public void onDestroy() {
        if (mEditCache != null) {
            mEditCache.removeObserver(mObserver);
            mEditCache.removeAll();
        }
    }

}//end class
