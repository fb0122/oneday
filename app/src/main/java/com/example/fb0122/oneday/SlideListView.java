package com.example.fb0122.oneday;


import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fb0122.oneday.utils.DataSetUtil;
import com.example.fb0122.oneday.utils.PreferenceUtils;
import com.example.fb0122.oneday.weidget.MyEditText;

import db_oneday.OneDaydb;

/**
 * 侧向滑出菜单的ListView
 * 使用请注意与ListView的Item的布局配合，
 * 该效果的实现是基于在Item的布局中通过设置PaddingLeft和PaddingRight来隐藏左右菜单的，
 * 所以使用此ListView时，请务必在布局Item时使用PaddingLeft和PaddingRight；
 * 或者自己改写此ListView，已达到想要的实现方式
 *
 * @author zhangshuo
 */
public class SlideListView extends ExpandableListView implements TextView.OnEditorActionListener{

    public static String TAG = "SlideListView";

    /**
     * 禁止侧滑模式
     */
    public static int MOD_FORBID = 0;
    /**
     * 从左向右滑出菜单模式
     */
    public static int MOD_LEFT = 1;
    /**
     * 从右向左滑出菜单模式
     */
    public static int MOD_RIGHT = 2;
    /**
     * 左右均可以滑出菜单模式
     */
    public static int MOD_BOTH = 3;
    /**
     * 当前的模式
     */
    private int mode = MOD_FORBID;
    /**
     * 左侧菜单的长度
     */
    private int leftLength = 0;
    /**
     * 右侧菜单的长度
     */
    private int rightLength = 0;

    /**
     * 当前滑动的ListView　position
     */
    private int slidePosition;
    /**
     * 手指按下X的坐标
     */
    private int downY;
    /**
     * 手指按下Y的坐标
     */
    private int downX;
    /**
     * ListView的item
     */
    private View itemView;
    /**
     * 滑动类
     */
    private Scroller scroller;
    /**
     * 认为是用户滑动的最小距离
     */
    private int mTouchSlop;

    /**
     * 判断是否可以侧向滑动
     */
    private boolean canMove = false;
    /**
     * 标示是否完成侧滑
     */
    private boolean isSlided = false;

    public static boolean isDelete = false;
    public static int VISIT = 1;

    private boolean isChanged = false;    //是否改变了习惯
    public Context context;
    private TextView Line, tvSc;
    public MyEditText changeTextView;

    AtyDay.MyAdapter adapter;
    public int screenWidth;

    private RemoveListener mRemoveListener;
    private RefreshPlan refreshPlanListener;
    public RemoveDirection removeDirection;
    private OneDaydb db;
    private String time;
    private String editTextContent;         // 习惯Text内的内容,因为 可能随时需要编辑.
    private String planChange;
    private ViewGroup shadowEdit;

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE){
            planChange = textView.getText().toString();
            InputMethodManager im = (InputMethodManager)textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (im.isActive()){
                im.hideSoftInputFromWindow(textView.getApplicationWindowToken(),0);
            }
            editTextContent = changeTextView.getText().toString();
            if (editTextContent.equals("")) {
                editTextContent = textView.getText().toString();
            }else {
                editTextContent = changeTextView.getText().toString();
            }
            tvSc.setVisibility(VISIBLE);
            tvSc.setText(editTextContent);
            tvSc.setTextColor(Color.BLACK);
            changeTextView.setVisibility(GONE);
//            shadowEdit.setBackgroundColor(getResources().getColor(R.color.day_background));
            ContentValues contentValues = DataSetUtil.updateData(OneDaydb.COLUMN_PLAN, editTextContent);
            db.updateData(OneDaydb.TABLE_NAME, contentValues, time,editTextContent);
            refreshPlanListener.refresh();
        }
        return false;
    }


    public enum RemoveDirection {
        RIGHT, LEFT;
    }

    public SlideListView(Context context) {
        this(context, null);
    }

    public SlideListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        screenWidth = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        scroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public void setRemoveListener(RemoveListener removeListener) {
        this.mRemoveListener = removeListener;
    }


    /**
     * 初始化菜单的滑出模式
     *
     * @param mode
     */
    public void initSlideMode(int mode, Context context) {
        this.mode = mode;
        this.context = context;
    }

    //得到当前滑动item的line
    public void saddLine(TextView textview, MyEditText changeTextView, TextView tvSc,OneDaydb db,String time) {
        this.Line = textview;
        this.changeTextView = changeTextView;
        this.tvSc = tvSc;
        this.db = db;
        this.time = time;
        if (this.changeTextView.getMyContext() == null) {
            this.changeTextView.setContext(context);
            this.changeTextView.setTextView(tvSc);
        }
    }

    //得到adapter
    public void getAda(AtyDay.MyAdapter LineAdapter) {
        this.adapter = LineAdapter;
    }

    /**
     * 处理我们拖动ListView item的逻辑
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        int lastX = (int) ev.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (changeTextView != null && canMove) {
                    setFocusable(false);
                    setFocusableInTouchMode(false);
                    tvSc.setVisibility(VISIBLE);
                    tvSc.setTextColor(Color.BLACK);
                    changeTextView.setVisibility(GONE);
                    editTextContent = tvSc.getText().toString();
//                    changeTextView.setStatus(false);
                    ContentValues contentValues = DataSetUtil.updateData(OneDaydb.COLUMN_PLAN, editTextContent);
                    db.updateData(OneDaydb.TABLE_NAME, contentValues, time,planChange);
                    refreshPlanListener.refresh();
                }
            /*当前模式不允许滑动，则直接返回，交给ListView自身去处理*/
                if (this.mode == MOD_FORBID) {
                    return super.onTouchEvent(ev);
                }

                // 如果处于侧滑完成状态，侧滑回去，并直接返回
                if (isSlided) {
                    scrollBack();
                    return false;
                }
                // 假如scroller滚动还没有结束，我们直接返回
                if (!scroller.isFinished()) {
                    return false;
                }
                downX = (int) ev.getX();
                downY = (int) ev.getY();

                slidePosition = pointToPosition(downX, downY);
                adapter.getLinePosition(slidePosition);
                // 无效的position, 不做任何处理
                if (slidePosition == AdapterView.INVALID_POSITION) {
                    return super.onTouchEvent(ev);
                }

                // 获取点击的item view
                itemView = getChildAt(slidePosition - getFirstVisiblePosition());
            /*此处根据设置的滑动模式，自动获取左侧或右侧菜单的长度*/
                if (this.mode == MOD_BOTH) {
                    this.leftLength = -itemView.getPaddingLeft();
                    this.rightLength = -itemView.getPaddingRight();
                } else if (this.mode == MOD_LEFT) {
                    this.leftLength = -itemView.getPaddingLeft();
                } else if (this.mode == MOD_RIGHT) {
                    this.rightLength = -itemView.getPaddingRight();
                }

                break;
            case MotionEvent.ACTION_MOVE:

                MotionEvent cancelEvent = MotionEvent.obtain(ev);
                cancelEvent
                        .setAction(MotionEvent.ACTION_CANCEL
                                | (ev.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                onTouchEvent(cancelEvent);
                int offsetX = downX - lastX;
            /* (Math.abs(ev.getX() - downX) > mTouchSlop && Math.abs(ev
                .getY() - downY) < mTouchSlop)
				元语句中有这个判断条件
			*/
                if (slidePosition != AdapterView.INVALID_POSITION
                        ) {

                    if (offsetX > 0 && (this.mode == MOD_BOTH || this.mode == MOD_RIGHT)) {
                    /*
                     * 执行listview的item滑动删除的动作
					 */
                        if (offsetX > rightLength) {
                            isDelete = true;
                        }
					/*从右向左滑*/
                        canMove = true;
                    } else if (offsetX < 0 && (this.mode == MOD_BOTH || this.mode == MOD_LEFT)) {

                        isDelete = false;
                        itemView.scrollTo(offsetX, 0);
                        //滑动出现横线
                        clearAnimation();
                        if (offsetX > -(0.75 * leftLength)) {
                            Line.setVisibility(View.VISIBLE);
                            changeTextView.setVisibility(GONE);
                            tvSc.setVisibility(VISIBLE);
                            if (changeTextView.getHint() != null) {
                                if (!changeTextView.getText().toString().equals("")) {
                                    tvSc.setText(changeTextView.getText().toString());
                                } else {
                                    tvSc.setText(changeTextView.getHint().toString());
                                }
                            }
                            tvSc.setTextColor(getResources().getColor(R.color.shadow));
                            VISIT = View.VISIBLE;
                            canMove = true;

                        } else if ((offsetX < -(0.75 * leftLength)) && (offsetX > -(leftLength))) {
                            changeTextView.setTextColor(getResources().getColor(R.color.black));
                            changeTextView.setText(tvSc.getText().toString());
                            Line.setVisibility(GONE);
                            tvSc.setVisibility(GONE);
                            changeTextView.setVisibility(VISIBLE);
                            changeTextView.requestFocus();
                            changeTextView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                            changeTextView.setOnEditorActionListener(this);
                            changeTextView.setFocusableInTouchMode(true);
                            changeTextView.setCursorVisible(false);
                            VISIT = View.GONE;
                            canMove = true;
                        }
					/*从左向右滑*/
                    } else {
                        canMove = false;
                    }
				/*此段代码是为了避免我们在侧向滑动时同时出发ListView的OnItemClickListener时间*/

                }
                if (canMove) {
				/*设置此属性，可以在侧向滑动时，保持ListView不会上下滚动*/
                    requestDisallowInterceptTouchEvent(true);

                    // 手指拖动itemView滚动, deltaX大于0向左滚动，小于0向右滚
                    int deltaX = downX - lastX;
                    if (deltaX < 0 && (this.mode == MOD_BOTH || this.mode == MOD_LEFT)) {
					/*向左滑*/

                        itemView.scrollTo(deltaX, 0);
                    } else if (deltaX > 0 && (this.mode == MOD_BOTH || this.mode == MOD_RIGHT)) {
					/*向右滑*/
                        itemView.scrollTo(deltaX, 0);
                    } else {
                        itemView.scrollTo(0, 0);
                    }
                    return true; // 拖动的时候ListView不滚动
                }
            case MotionEvent.ACTION_UP:
                if (canMove) {
                    canMove = false;
//				scrollDelete();
                    scrollByDistanceX();
                }
                if (VISIT == View.GONE) {
                    PreferenceUtils.putBoolean(context,String.valueOf(slidePosition),false);
                } else {
                    PreferenceUtils.putBoolean(context,String.valueOf(slidePosition),true);
                }

                break;
        }

        // 否则直接交给ListView来处理onTouchEvent事件
        postInvalidate();
        return super.onTouchEvent(ev);
    }

    /**
     * 根据手指滚动itemView的距离来判断是滚动到开始位置还是向左或者向右滚动
     */

    private void scrollByDistanceX() {
		/*当前模式不允许滑动，则直接返回*/
        if (this.mode == MOD_FORBID) {
            return;
        }
        if (itemView.getScrollX() > 0 && (this.mode == MOD_BOTH || this.mode == MOD_RIGHT)) {
			/*从右向左滑*/
            if (itemView.getScrollX() >= rightLength) {
                scrollLeft();
            } else {
                // 滚回到原始位置
                scrollBack();
            }
        } else if (itemView.getScrollX() < 0 && (this.mode == MOD_BOTH || this.mode == MOD_LEFT)) {
            scrollBack();
        }

    }


    @Override
    public void removeView(View child) {
        super.removeView(child);
    }

    private void scrollDelete() {
        if (itemView.getScaleX() >= screenWidth / 2) {
            scrollLeft();
        } else {
            itemView.scrollTo(0, 0);
        }
    }

    /**
     * 往右滑动，getScrollX()返回的是左边缘的距离，就是以View左边缘为原点到开始滑动的距离，所以向右边滑动为负值
     */
    private void scrollRight() {
        removeDirection = RemoveDirection.RIGHT;
        final int delta = (leftLength + itemView.getScrollX());
        // 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
        scroller.startScroll(itemView.getScrollX(), 0, -delta, 0,
                Math.abs(delta));
//		Line.setVisibility(View.VISIBLE);
        postInvalidate(); // 刷新itemView
    }

    /**
     * 向左滑动，根据上面我们知道向左滑动为正值
     */
    private void scrollLeft() {
        removeDirection = RemoveDirection.LEFT;
        final int delta = (screenWidth - itemView.getScrollX());
        // 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
        //delta + rightLength 是还要滑动一个右边扩展菜单的距离
        scroller.startScroll(itemView.getScrollX(), 0, delta + rightLength, 0,
                Math.abs(delta));
        postInvalidate(); // 刷新itemView
    }

    /**
     * 滑动会原来的位置
     */
    private void scrollBack() {
        isSlided = false;
        scroller.startScroll(itemView.getScrollX(), 0, -itemView.getScrollX(),
                0, Math.abs(itemView.getScrollX()));
        postInvalidate(); // 刷新itemView
    }


    @Override
    public void computeScroll() {
        // 调用startScroll的时候scroller.computeScrollOffset()返回true，
        if (scroller.computeScrollOffset()) {
            // 让ListView item根据当前的滚动偏移量进行滚动
            itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
            //isDelete 防止item在删除一个之后 只要滑动就删除
            if (scroller.isFinished() && removeDirection == RemoveDirection.LEFT && isDelete) {
                itemView.setAlpha(1f - Math.abs((float) scroller.getCurrX() / screenWidth));
                if (mRemoveListener == null && isDelete) {
                    throw new NullPointerException("RemoveListener is null, we should called setRemoveListener()");
                }
                itemView.scrollTo(0, 0);
                isDelete = false;              //在这里设置isDelete为false,防止下次只要向左滑动就删除item
                mRemoveListener.removeItem(RemoveDirection.LEFT, slidePosition - 1, itemView);      //slidePosition - 1 是因为listview加了headerView
                postInvalidate();
            }
        }
    }

    /**
     * 提供给外部调用，用以将侧滑出来的滑回去
     */
    public void slideBack() {
        this.scrollBack();
    }

    public interface RemoveListener {
        void removeItem(RemoveDirection reDirection, int position, View itemView);
    }

    public interface RefreshPlan{
        void refresh();
    }

    public void setRefreshPlanListener(RefreshPlan refreshPlanListener) {
        this.refreshPlanListener = refreshPlanListener;
    }
}