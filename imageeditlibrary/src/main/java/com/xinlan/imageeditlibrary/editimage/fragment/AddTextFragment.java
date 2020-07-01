package com.xinlan.imageeditlibrary.editimage.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.adapter.ColorListAdapter;
import com.xinlan.imageeditlibrary.editimage.task.StickerTask;
import com.xinlan.imageeditlibrary.editimage.ui.ColorPicker;
import com.xinlan.imageeditlibrary.editimage.view.ColorPickerView;
import com.xinlan.imageeditlibrary.editimage.view.PaintModeView;
import com.xinlan.imageeditlibrary.editimage.view.TextStickerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 添加文本贴图
 *
 * @author 我的孩子叫好帅
 */
public class AddTextFragment extends BaseEditFragment implements TextWatcher,ColorListAdapter.IColorListAction {
    public static final int INDEX = ModuleConfig.INDEX_ADDTEXT;
    public static final String TAG = AddTextFragment.class.getName();

    private View mainView;
    private View backToMenu;// 返回主菜单
    private ImageView mFontSwitch;//字体切换

    private EditText mInputText;//输入框
    private ImageView mTextColorSelector;//颜色选择器
    private TextStickerView mTextStickerView;// 文字贴图显示控件
    private CheckBox mAutoNewLineCheck;

    private ColorPicker mColorPicker;

    private int mTextColor = Color.WHITE;
    private InputMethodManager imm;

    private SaveTextStickerTask mSaveTask;


    //字体选择器
    private RadioGroup mFilterGroup;
    private String[] texts;
    private HorizontalScrollView scrollView;
    private boolean isFont;
    private List<Typeface> list;


    private PaintModeView mPaintModeView;
    private ColorPickerView left;//颜色选择器
    private RecyclerView mColorListView;//选色

    public int[] mPaintColors = {
            Color.GRAY,
            Color.BLACK,
            Color.LTGRAY,
            Color.WHITE,
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA};

    public int[] mPaintColors1 = {R.drawable.ncolor2,
            R.drawable.abc_scrubber_control_to_pressed_mtrl_005,
            R.drawable.ncolor3, R.drawable.ncolor4,
            R.drawable.ncolor5, R.drawable.ncolor6,R.drawable.ncolor7,
            R.drawable.ncolor8, R.drawable.ncolor9,
            R.drawable.ncolor10};


     /*public int[] mPaintColors={R.color.icon_color_1,R.color.icon_color_2,R.color.icon_color_3,
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
    private ColorListAdapter mColorAdapter;

    public static AddTextFragment newInstance() {
        AddTextFragment fragment = new AddTextFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mainView = inflater.inflate(R.layout.fragment_edit_image_add_text, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFilterGroup = (RadioGroup) mainView.findViewById(R.id.filter_group);
        scrollView = mainView.findViewById(R.id.scrollView);
        mTextStickerView = (TextStickerView)getActivity().findViewById(R.id.text_sticker_panel);

        backToMenu = mainView.findViewById(R.id.back_to_main);
        mFontSwitch= mainView.findViewById(R.id.iv_font_switch);
        mInputText = (EditText) mainView.findViewById(R.id.text_input);
        mTextColorSelector = (ImageView) mainView.findViewById(R.id.text_color);

        backToMenu.setOnClickListener(new BackToMenuClick());// 返回主菜单
        mFontSwitch.setOnClickListener(new FontSwitchData());//字体切换
        mColorPicker = new ColorPicker(getActivity(), 255, 0, 0);
        mTextColorSelector.setOnClickListener(new SelectColorBtnClick());
        mInputText.addTextChangedListener(this);
        mTextStickerView.setEditText(mInputText);

        //统一颜色设置
        mTextColorSelector.setBackgroundColor(mColorPicker.getColor());
        mTextStickerView.setTextColor(mColorPicker.getColor());
        setUpFliters();
        Typeface typeface = list.get(0);
        mTextStickerView.setTypeface(typeface);


        left = (ColorPickerView) mainView.findViewById(R.id.colorPickerView3);
        left.setOnColorPickerChangeListener(l);

        mPaintModeView = (PaintModeView) mainView.findViewById(R.id.paint_thumb);
        mPaintModeView.setPaintStrokeColor(Color.RED);
        mPaintModeView.setPaintStrokeWidth(40);

        //选色
        mColorListView = (RecyclerView) mainView.findViewById(R.id.paint_color_list);
        initColorListView();
    }

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
    public void onColorSelected(int position, int color) {
        setPaintColor(color);
    }

    @Override
    public void onMoreSelected(int position) {

    }
    ColorPickerView.OnColorPickerChangeListener l = new ColorPickerView.OnColorPickerChangeListener() {
        @Override
        public void onColorChanged(ColorPickerView picker, int color1) {
            left.setIndicatorColor(color1);
            setPaintColor(color1);

        }

        @Override
        public void onStartTrackingTouch(ColorPickerView picker) {

        }

        @Override
        public void onStopTrackingTouch(ColorPickerView picker) {

        }
    };
    /**
     * 设置文本颜色
     *
     * @param paintColor
     */
    protected void setPaintColor(final int paintColor) {
        mTextColorSelector.setBackgroundColor(paintColor);
        mPaintModeView.setPaintStrokeColor(paintColor);
        mTextStickerView.setTextColor(paintColor);
        mPaintModeView.setPaintStrokeColor(paintColor);

    }
    /**
     * 字体切换
     */

    private void FontSwitch() {
        //先把键盘gone掉
        hideInput();
        //弹出字体选择框，
        if (isFont){
            isFont=false;
            scrollView.setVisibility(View.GONE);
        }else {
            isFont=true;
            scrollView.setVisibility(View.VISIBLE);
        }

    }

    private void setUpFliters() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.leftMargin = 10;
        params.rightMargin = 10;
        mFilterGroup.removeAllViews();
        int[] ifliters={
                R.drawable.ttf_selector_1,
                R.drawable.ttf_selector_2,
                R.drawable.ttf_selector_3,
                R.drawable.ttf_selector_4,
                R.drawable.ttf_selector_5,
                R.drawable.ttf_selector_6,
                R.drawable.ttf_selector_7,
                R.drawable.ttf_selector_8,
                R.drawable.ttf_selector_9,
                R.drawable.ttf_selector_10,
                R.drawable.ttf_selector_11,
                R.drawable.ttf_selector_12,
                R.drawable.ttf_selector_13,
                R.drawable.ttf_selector_14,
                R.drawable.ttf_selector_15,
                R.drawable.ttf_selector_16,
                R.drawable.ttf_selector_17,
                R.drawable.ttf_selector_18};
        //获取不同字体
        list = new ArrayList<>();
        try {
            texts = getActivity().getAssets().list("text");
            for (int i=0;i<texts.length;i++){
                Typeface mtypeface=Typeface.createFromAsset(getActivity().getAssets(),"text/"+texts[i]);
                list.add(mtypeface);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        for (int i = 0, len = ifliters.length; i < len; i++) {
            RadioButton text = new RadioButton(activity);
            Drawable drawable = getResources().getDrawable(ifliters[i]);
            text.setBackground(drawable);
            text.setButtonDrawable(null);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mFilterGroup.addView(text, params);
            if (i == 0) {
//                radioGroup.check(radioButton.getId());
                text.setChecked(true);
            }
            text.setTag(i);
            text.setOnClickListener(new FliterClick());
        }// end for i
    }



    /**
     * 选择字体效果
     */
    private final class FliterClick implements OnClickListener {


        @Override
        public void onClick(View v) {
            Typeface typeface = list.get((int) v.getTag());
            mTextStickerView.setTypeface(typeface);

        }
    }// end inner class
    @Override
    public void afterTextChanged(Editable s) {
        //mTextStickerView change
        String text = s.toString().trim();
        //控制是否显示
//        mFontSwitch.setVisibility(text.length()==0?View.GONE:View.VISIBLE);
        mTextStickerView.setText(text);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    /**
     * 颜色选择 按钮点击
     */
    private final class SelectColorBtnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mColorPicker.show();
            Button okColor = (Button) mColorPicker.findViewById(R.id.okColorButton);
            okColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeTextColor(mColorPicker.getColor());
                    mColorPicker.dismiss();
                }
            });
        }
    }//end inner class

    /**
     * 修改字体颜色
     *
     * @param newColor
     */
    private void changeTextColor(int newColor) {
        this.mTextColor = newColor;
        mTextColorSelector.setBackgroundColor(mTextColor);
        mTextStickerView.setTextColor(mTextColor);
    }

    public void hideInput() {
        if (getActivity() != null && getActivity().getCurrentFocus() != null && isInputMethodShow()) {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public boolean isInputMethodShow() {
        return imm.isActive();
    }

    /**
     * 返回按钮逻辑
     *
     * @author panyi
     */
    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }// end class
    /**
     * 字体切换按钮逻辑
     *
     * @author panyi
     */
    private final class FontSwitchData implements OnClickListener {
        @Override
        public void onClick(View v) {
            FontSwitch();
        }


    }// end class

    /**
     * 返回主菜单
     */
    @Override
    public void backToMain() {
        hideInput();
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
        activity.mainImage.setVisibility(View.VISIBLE);
        activity.bannerFlipper.showPrevious();
        mTextStickerView.setVisibility(View.GONE);
        activity.title.setText("");
    }

    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_TEXT;
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.bannerFlipper.showNext();
        mTextStickerView.setVisibility(View.VISIBLE);
//        mInputText.clearFocus();
        showSoftInputFromWindow(activity,mInputText);
    }
    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
    /**
     * 保存贴图图片
     */
    public void applyTextImage() {
        if (mSaveTask != null) {
            mSaveTask.cancel(true);
        }

        //启动任务
        mSaveTask = new SaveTextStickerTask(activity);
        mSaveTask.execute(activity.getMainBit());
    }

    /**
     * 文字合成任务
     * 合成最终图片
     */
    private final class SaveTextStickerTask extends StickerTask {

        public SaveTextStickerTask(EditImageActivity activity) {
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
            //System.out.println("scale = " + scale_x + "       " + scale_y + "     " + dx + "    " + dy);
            mTextStickerView.drawText(canvas, mTextStickerView.layout_x,
                    mTextStickerView.layout_y, mTextStickerView.mScale, mTextStickerView.mRotateAngle);
            canvas.restore();
        }

        @Override
        public void onPostResult(Bitmap result) {
            mTextStickerView.clearTextContent();
            mTextStickerView.resetView();

            activity.changeMainBitmap(result , true);
            backToMain();
        }
    }//end inner class

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSaveTask != null && !mSaveTask.isCancelled()) {
            mSaveTask.cancel(true);
        }
    }
}// end class
