package com.xinlan.imageeditlibrary.editimage.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.xinlan.imageeditlibrary.FileUtils;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.inter.ImageEditInte;
import com.xinlan.imageeditlibrary.editimage.inter.SaveCompletedInte;
import com.xinlan.imageeditlibrary.editimage.task.StickerTask;
import com.xinlan.imageeditlibrary.editimage.view.PaintModeView;
import com.xinlan.imageeditlibrary.editimage.view.mosaic.MosaicUtil;
import com.xinlan.imageeditlibrary.editimage.view.mosaic.MosaicView;

import java.util.HashMap;

//马赛克
public class MosaicFragment extends BaseEditFragment implements View.OnClickListener, ImageEditInte {

    public static final int INDEX = ModuleConfig.INDEX_MOSAI;
    private MosaicView mMosaicView;
    private SaveMosaicPaintTask mSaveMosaicPaintImageTask;
    private View action_base;
    private View action_ground_glass;
    private View action_flower;
    private View mRevokeView;
    private View preMosaicButton;
    private HashMap<MosaicUtil.Effect, Bitmap> mosaicResMap;
    private View backToMenu;// 返回主菜单
    private SaveMosaicPaintTask mSavePaintImageTask;
    private LinearLayout ll_pop;//画笔大小
    private View popView;
    private SeekBar mStokenWidthSeekBar;
    private PaintModeView mPaintModeView;
    public MosaicFragment() {
    }

    public static MosaicFragment newInstance(EditImageActivity activity) {
        MosaicFragment fragment = new MosaicFragment();
        fragment.activity = activity;
        fragment.mMosaicView = activity.mMosaicView;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_mosaic, container, false);
        action_base = mainView.findViewById(R.id.action_base);
        action_ground_glass = mainView.findViewById(R.id.action_ground_glass);
        action_flower = mainView.findViewById(R.id.action_flower);
        mRevokeView = mainView.findViewById(R.id.paint_revoke);
        backToMenu = mainView.findViewById(R.id.back_to_main);
        ll_pop = mainView.findViewById(R.id.ll_pop);
        mPaintModeView = (PaintModeView) mainView.findViewById(R.id.paint_thumb);
        return mainView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        action_base.setOnClickListener(this);
        action_ground_glass.setOnClickListener(this);
        action_flower.setOnClickListener(this);
        mRevokeView.setOnClickListener(this);
        backToMenu.setOnClickListener(this);// 返回主菜单
        mPaintModeView.setOnClickListener(this);
        initStokeWidthPopWindow();
    }


    private void initStokeWidthPopWindow() {
        popView = LayoutInflater.from(activity).inflate(R.layout.view_set_stoke_width, null);

//        setStokenWidthWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_TOAST);
        int width = ll_pop.getLayoutParams().width;
        int height = ll_pop.getLayoutParams().height;
        lp.width=width;
        lp.height=height;
        ll_pop.addView(popView,lp);
        mStokenWidthSeekBar = (SeekBar) popView.findViewById(R.id.stoke_width_seekbar);
        mStokenWidthSeekBar.setProgress(20);

       /* Drawable vipDrawable1 = getActivity().getResources().getDrawable(color);
        vipDrawable1.setBounds(mStokenWidthSeekBar.getProgressDrawable().getBounds());//要设置之前的bounds不然会显示不出新的进度条颜色
        mStokenWidthSeekBar.setProgressDrawable(vipDrawable1);
        mStokenWidthSeekBar.setThumb(getActivity().getResources().getDrawable(color));*/

        mStokenWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("TAG","===-=画笔"+progress);
                mPaintModeView.setPaintStrokeWidth(progress);
                mMosaicView.setMosaicBrushWidth(progress);//马赛克画刷的宽度
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mPaintModeView.setPaintStrokeColor(Color.RED);
        mPaintModeView.setPaintStrokeWidth(20);

    }

    @Override
    public void onClick(View v) {
        Bitmap mainBitmap = activity.getMainBit();
        int i = v.getId();
        if (v == backToMenu) {//back button click
            backToMain();
        }else if (i == R.id.action_base) {

            if (hasMosaicRes(MosaicUtil.Effect.MOSAIC)) {
                Bitmap bit = MosaicUtil.getMosaic(activity.getMainBit());
                mosaicResMap.put(MosaicUtil.Effect.MOSAIC, bit);
                mMosaicView.setMosaicResource(mosaicResMap);
            }
            mMosaicView.setMosaicEffect(MosaicUtil.Effect.MOSAIC);
            changeToolsSelector(MosaicUtil.Effect.MOSAIC);
        } else if (i == R.id.action_ground_glass) {

            if (hasMosaicRes(MosaicUtil.Effect.BLUR)) {
                Bitmap blur = MosaicUtil.getBlur(activity.getMainBit());
                mosaicResMap.put(MosaicUtil.Effect.BLUR, blur);
                mMosaicView.setMosaicResource(mosaicResMap);
            }
            mMosaicView.setMosaicEffect(MosaicUtil.Effect.BLUR);
            changeToolsSelector(MosaicUtil.Effect.BLUR);
        } else if (i == R.id.action_flower) {

            if (hasMosaicRes(MosaicUtil.Effect.FLOWER)) {
                Bitmap bit = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.hi4);
                bit = FileUtils.ResizeBitmap(bit, mainBitmap.getWidth(), mainBitmap.getHeight());

                mosaicResMap.put(MosaicUtil.Effect.FLOWER, bit);
                mMosaicView.setMosaicResource(mosaicResMap);
            }
            mMosaicView.setMosaicEffect(MosaicUtil.Effect.FLOWER);
            changeToolsSelector(MosaicUtil.Effect.FLOWER);
        } else if (i == R.id.paint_revoke) {
            mMosaicView.undo();
        } else {
        }
    }

    private boolean hasMosaicRes(MosaicUtil.Effect effect) {
        if (mosaicResMap.containsKey(effect)) {
            Bitmap bitmap = mosaicResMap.get(effect);
            if (bitmap != null) {
                return true;
            }
        }
        return false;
    }

    private void changeToolsSelector(MosaicUtil.Effect effect) {
        View v = null;
        switch (effect) {
            case MOSAIC:
                v = action_base;
                break;
            case BLUR:
                v = action_ground_glass;
                break;
            case FLOWER:
                v = action_flower;

        }
        View toolsButton = ((ViewGroup) v).getChildAt(0);

        if (preMosaicButton != null) {
            preMosaicButton.setSelected(false);
        }
        //更新按钮状态
        toolsButton.setSelected(true);
        preMosaicButton = toolsButton;

    }

    /**
     * 设置马赛克的样式和粗细
     */
    private void initSetting() {

        mMosaicView.setMosaicBackgroundResource(activity.getMainBit());
        Bitmap bit = MosaicUtil.getMosaic(activity.getMainBit());
        Bitmap blur = MosaicUtil.getBlur(activity.getMainBit());

        mosaicResMap = new HashMap<>();
        mosaicResMap.put(MosaicUtil.Effect.MOSAIC, bit);
        mosaicResMap.put(MosaicUtil.Effect.BLUR, blur);
        mMosaicView.setMosaicResource(mosaicResMap);

        mMosaicView.setMosaicBrushWidth(20);

        MosaicUtil.Effect mosaicEffect = mMosaicView.getMosaicEffect();
        //默认选中基础模式
        changeToolsSelector(mosaicEffect);

    }

    /**
     * 返回主菜单
     */
    public void backToMain() {
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
        activity.mainImage.setVisibility(View.VISIBLE);
        activity.bannerFlipper.showPrevious();
        this.mMosaicView.setVisibility(View.GONE);
        mMosaicView.setIsOperation(false);
        activity.title.setText("");
    }


    public void onShow() {
        activity.mode = EditImageActivity.MODE_MOSAI;
        activity.mainImage.setImageBitmap(activity.getMainBit());
        this.mMosaicView.setVisibility(View.VISIBLE);
        activity.bannerFlipper.showNext();
        mMosaicView.setIsOperation(true);
        initSetting();
    }

    @Override
    public void appleEdit(SaveCompletedInte inte) {
        if (mSaveMosaicPaintImageTask != null && !mSaveMosaicPaintImageTask.isCancelled()) {
            mSaveMosaicPaintImageTask.cancel(true);
        }

        mSaveMosaicPaintImageTask = new SaveMosaicPaintTask(activity);
        mSaveMosaicPaintImageTask.execute(activity.getMainBit());
    }

    @Override
    public void method2() {

    }

    @Override
    public void method3() {

    }
    /**
     * 保存涂鸦
     */
    public void saveMosaiImage() {
        if (mSaveMosaicPaintImageTask != null && !mSaveMosaicPaintImageTask.isCancelled()) {
            mSavePaintImageTask.cancel(true);
        }

        mSavePaintImageTask = new SaveMosaicPaintTask(activity);
        mSavePaintImageTask.execute(activity.getMainBit());
    }

    /**
     * 保存马赛克图片
     */
    private final class SaveMosaicPaintTask extends StickerTask {
        public SaveMosaicPaintTask(EditImageActivity activity) {
            super(activity);
        }

       /* public SaveMosaicPaintTask(EditImageActivity activity, SaveCompletedInte inte) {
            super(activity, inte);
        }*/

        @Override
        public void handleImage(Canvas canvas, Matrix m) {

//            float[] f = new float[9];
//            m.getValues(f);
//            int dx = (int) f[Matrix.MTRANS_X];
//            int dy = (int) f[Matrix.MTRANS_Y];
//            float scale_x = f[Matrix.MSCALE_X];
//            float scale_y = f[Matrix.MSCALE_Y];
            canvas.save();
//            canvas.translate(dx, dy);
//            canvas.scale(scale_x, scale_y);

            if (mMosaicView.getMosaicBit() != null) {
                canvas.drawBitmap(mMosaicView.getMosaicBit(), 0, 0, null);
            }
            canvas.restore();
        }

        @Override
        public void onPostResult(Bitmap result) {
            mMosaicView.reset();
            activity.changeMainBitmap(result , true);
            backToMain();
        }
    }
}
