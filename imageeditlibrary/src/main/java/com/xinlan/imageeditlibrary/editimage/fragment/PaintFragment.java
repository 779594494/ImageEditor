package com.xinlan.imageeditlibrary.editimage.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.adapter.ColorListAdapter;
import com.xinlan.imageeditlibrary.editimage.task.StickerTask;
import com.xinlan.imageeditlibrary.editimage.ui.ColorPicker;
import com.xinlan.imageeditlibrary.editimage.view.ColorPickerView;
import com.xinlan.imageeditlibrary.editimage.view.CustomPaintView;
import com.xinlan.imageeditlibrary.editimage.view.PaintModeView;


/**
 * 用户自由绘制模式 操作面板
 * 可设置画笔粗细 画笔颜色
 * custom draw mode panel
 *
 * @author 我的孩子叫好帅
 */
public class PaintFragment extends BaseEditFragment implements View.OnClickListener, ColorListAdapter.IColorListAction {
    public static final int INDEX = ModuleConfig.INDEX_PAINT;
    public static final String TAG = PaintFragment.class.getName();

    private View mainView;
    private View backToMenu;// 返回主菜单
    private PaintModeView mPaintModeView;
    private RecyclerView mColorListView;//颜色列表View
    private ColorListAdapter mColorAdapter;
    private View popView;

    private CustomPaintView mPaintView;

    private ColorPicker mColorPicker;//颜色选择器

    private PopupWindow setStokenWidthWindow;
    private SeekBar mStokenWidthSeekBar;

    private ImageView mEraserView;

    public boolean isEraser = false;//是否是擦除模式

    private SaveCustomPaintTask mSavePaintImageTask;

    private ColorPickerView left;//颜色选择器


    public int[] mPaintColors = {
             Color.GRAY,Color.BLACK, Color.LTGRAY, Color.WHITE,
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA};

    public int[] mPaintColors1 = {R.drawable.ncolor2,
            R.drawable.abc_scrubber_control_to_pressed_mtrl_005,
            R.drawable.ncolor3, R.drawable.ncolor4,
            R.drawable.ncolor5, R.drawable.ncolor6,R.drawable.ncolor7,
            R.drawable.ncolor8, R.drawable.ncolor9,
            R.drawable.ncolor10};
    /* public int[] mPaintColors={R.color.icon_color_1,R.color.icon_color_2,R.color.icon_color_3,
            R.color.icon_color_4,R.color.icon_color_5,R.color.icon_color_6,R.color.icon_color_7,
            R.color.icon_color_8,R.color.icon_color_9,R.color.icon_color_10,R.color.icon_color_11,
            R.color.icon_color_12,R.color.icon_color_13,R.color.icon_color_14,R.color.icon_color_15,
            R.color.icon_color_16,R.color.icon_color_17,R.color.icon_color_18};
    public int[] mPaintColors1 = {R.drawable.icon_color_1,R.drawable.icon_color_2,
            R.drawable.icon_color_3,R.drawable.icon_color_4,R.drawable.icon_color_5,
            R.drawable.icon_color_6,R.drawable.icon_color_7,R.drawable.icon_color_8,
            R.drawable.icon_color_9,R.drawable.icon_color_10,R.drawable.icon_color_11,
            R.drawable.icon_color_12,R.drawable.icon_color_13,R.drawable.icon_color_14,
            R.drawable.icon_color_15,R.drawable.icon_color_16,R.drawable.icon_color_17,
            R.drawable.icon_color_18};*/
    private LinearLayout ll_pop;
    private int color;


    public static PaintFragment newInstance() {
        PaintFragment fragment = new PaintFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_paint, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       /* left = (ColorPickerView) mainView.findViewById(R.id.colorPickerView3);
        left.setOnColorPickerChangeListener(l);*/
        mPaintView = (CustomPaintView)getActivity().findViewById(R.id.custom_paint_view);
        backToMenu = mainView.findViewById(R.id.back_to_main);
        mPaintModeView = (PaintModeView) mainView.findViewById(R.id.paint_thumb);
        mColorListView = (RecyclerView) mainView.findViewById(R.id.paint_color_list);
        mEraserView = (ImageView) mainView.findViewById(R.id.paint_eraser);
        ll_pop = mainView.findViewById(R.id.ll_pop);
        backToMenu.setOnClickListener(this);// 返回主菜单

        mColorPicker = new ColorPicker(getActivity(), 255, 0, 0);
        Log.e("TAG","=====2222222222222");
        initColorListView();
        Log.e("TAG","=====3333333333333");
        mPaintModeView.setOnClickListener(this);

        initStokeWidthPopWindow();
        Log.e("TAG","=====44444444444444");
        mEraserView.setOnClickListener(this);
        updateEraserView();
        Log.e("TAG","=====555555555555555");

    }
    /*ColorPickerView.OnColorPickerChangeListener l = new ColorPickerView.OnColorPickerChangeListener() {
        @Override
        public void onColorChanged(ColorPickerView picker, int color1) {
            color=color1;
            left.setIndicatorColor(color1);
            setPaintColor(color1);

        }

        @Override
        public void onStartTrackingTouch(ColorPickerView picker) {

        }

        @Override
        public void onStopTrackingTouch(ColorPickerView picker) {

        }
    };*/
    /**
     * 初始化颜色列表
     */
    private void initColorListView() {

        mColorListView.setHasFixedSize(false);

        LinearLayoutManager stickerListLayoutManager = new LinearLayoutManager(activity);
        stickerListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mColorListView.setLayoutManager(stickerListLayoutManager);
        mColorAdapter = new ColorListAdapter(getActivity(),mPaintColors, mPaintColors1, this);
        mColorListView.setAdapter(mColorAdapter);


    }

    @Override
    public void onClick(View v) {
        if (v == backToMenu) {//back button click
            backToMain();
        } else if (v == mPaintModeView) {//设置绘制画笔粗细
            setStokeWidth();
        } else if (v == mEraserView) {
            toggleEraserView();
        }//end if
    }

    /**
     * 返回主菜单
     */
    public void backToMain() {
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
        activity.mainImage.setVisibility(View.VISIBLE);
        activity.bannerFlipper.showPrevious();
        activity.title.setText("");
        this.mPaintView.setVisibility(View.GONE);
    }

    public void onShow() {
        activity.mode = EditImageActivity.MODE_PAINT;
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.bannerFlipper.showNext();
        this.mPaintView.setVisibility(View.VISIBLE);
    }

    //更换画笔颜色
    @Override
    public void onColorSelected(int position, int color1) {
        color = color1;
        setPaintColor(color);
    }

    //马赛克
    @Override
    public void onMoreSelected(int position) {

       /* MosaicFragment mMosaicFragment = MosaicFragment.newInstance((EditImageActivity) getActivity());
        activity.onTitle("马赛克");
        activity.bannerFlipper.showNext();
        activity.bottomGallery.setCurrentItem(MosaicFragment.INDEX);
        activity.mMosaicFragment.onShow();*/
//        setPaintMosaic();
//        mColorPicker.show();
        /*Button okColor = (Button) mColorPicker.findViewById(R.id.okColorButton);
        okColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//*setPaintColor(mColorPicker.getColor());
                mColorPicker.dismiss();*//*
                setPaintMosaic();
            }
        });*/
    }

    /**
     * 设置画笔颜色
     *
     * @param paintColor
     */
    protected void setPaintColor(final int paintColor) {

        mPaintModeView.setPaintStrokeColor(paintColor);

        updatePaintView();
    }
    /**
     * 设置画笔马赛克
     *
     */
   /* protected void setPaintMosaic() {

        mPaintModeView.setPaintStrokeMosaic();
        updatePaintView();
    }*/
    private void updatePaintView() {
        isEraser = false;
        updateEraserView();

        this.mPaintView.setColor(mPaintModeView.getStokenColor());
        this.mPaintView.setWidth(mPaintModeView.getStokenWidth());
//        setGradients(mStokenWidthSeekBar,mPaintModeView.getStokenColor());

    }

    private void setGradients(SeekBar mStokenWidthSeekBar, int stokenColor) {
        int[] mColor=new int[]{R.color.hint_foreground_material_dark,stokenColor};
        GradientDrawable drawable=new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,mColor);
        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        drawable.setCornerRadius(0);
        drawable.setStroke(1,-1);
        mStokenWidthSeekBar.setProgressDrawable(drawable);
    }

    /**
     * 设置画笔粗细
     * show popwidnow to set paint width
     */
    protected void setStokeWidth() {
        if (popView.getMeasuredHeight() == 0) {
            popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        }

        mStokenWidthSeekBar.setMax(mPaintModeView.getMeasuredHeight());

        mStokenWidthSeekBar.setProgress((int) mPaintModeView.getStokenWidth());

        mStokenWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("TAG","===-=画笔"+progress);
                mPaintModeView.setPaintStrokeWidth(progress);
                updatePaintView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        int[] locations = new int[2];
        activity.bottomGallery.getLocationOnScreen(locations);
//        setStokenWidthWindow.showAtLocation(activity.bottomGallery, Gravity.NO_GRAVITY, 0, locations[1] - popView.getMeasuredHeight());
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
                updatePaintView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
/*
        setStokenWidthWindow.setFocusable(true);
        setStokenWidthWindow.setOutsideTouchable(true);
        setStokenWidthWindow.setBackgroundDrawable(new BitmapDrawable());
        setStokenWidthWindow.setAnimationStyle(R.style.popwin_anim_style);*/


        mPaintModeView.setPaintStrokeColor(Color.RED);
        mPaintModeView.setPaintStrokeWidth(20);

        updatePaintView();
    }

    private void toggleEraserView() {
        isEraser = !isEraser;
        updateEraserView();
    }

    private void updateEraserView() {
        mEraserView.setImageResource(isEraser ? R.drawable.eraser_seleced : R.drawable.eraser_normal);
        mPaintView.setEraser(isEraser);
    }

    /**
     * 保存涂鸦
     */
    public void savePaintImage() {
        if (mSavePaintImageTask != null && !mSavePaintImageTask.isCancelled()) {
            mSavePaintImageTask.cancel(true);
        }

        mSavePaintImageTask = new SaveCustomPaintTask(activity);
        mSavePaintImageTask.execute(activity.getMainBit());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSavePaintImageTask != null && !mSavePaintImageTask.isCancelled()) {
            mSavePaintImageTask.cancel(true);
        }
    }

    /**
     * 文字合成任务
     * 合成最终图片
     */
    private final class SaveCustomPaintTask extends StickerTask {

        public SaveCustomPaintTask(EditImageActivity activity) {
            super(activity);
        }

        @Override
        public void handleImage(Canvas canvas, Matrix m) {
            float[] f = new float[9];
            m.getValues(f);
            int dx = (int) f[Matrix.MTRANS_X];
            int dy = (int) f[Matrix.MTRANS_Y];
            float scale_x = f[Matrix.MSCALE_X];
            float scale_y = f[Matrix.MSCALE_Y];
            canvas.save();
            canvas.translate(dx, dy);
            canvas.scale(scale_x, scale_y);

            if (mPaintView.getPaintBit() != null) {
                canvas.drawBitmap(mPaintView.getPaintBit(), 0, 0, null);
            }
            canvas.restore();
        }

        @Override
        public void onPostResult(Bitmap result) {
            mPaintView.reset();
            activity.changeMainBitmap(result , true);
            backToMain();
        }
    }//end inner class

}// end class
