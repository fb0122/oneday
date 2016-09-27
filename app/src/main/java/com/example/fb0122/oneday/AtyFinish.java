package com.example.fb0122.oneday;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fb0122.oneday.utils.DimenTranslate;
import com.example.fb0122.oneday.utils.TimeCalendar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import circleprogressbar.TasksCompletedView;
import db_oneday.OneDaydb;
import oneday.Alarm.DataRefresh;

import static oneday.Alarm.Config.CHANGE_WEEK_VIEW;
import static oneday.Alarm.Config.DELETE_DATA;

public class AtyFinish extends Fragment implements View.OnTouchListener {

    public static String TAG = "AtyFinish";

    final int progress = 75;
    private TasksCompletedView circleprogressbar;
    public static RecyclerView listview;

    OneDaydb db;
    public static cursorAdapter adapter;
    static Cursor c, s, ss;
    private int mCurrentProgress;
    private int mTotalProgress;

    public static ArrayList<String> weekData = new ArrayList<>();
    static SQLiteDatabase dbreader;
    public LinearLayout finish;
    public static int mScreenHeight;
    private LinearLayout downlist;
    public static boolean isFresh = true;
    static HashSet hashSet;
    ChangeHandler changeHandler = new ChangeHandler(Looper.myLooper());
    public static int flag = 0;
    private int laterDay = 0;

    GestureDetector detector = new GestureDetector(getActivity(), new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if ((Boolean) downlist.getTag() || (e2.getRawY() - e1.getRawY()) > 0) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) downlist.getLayoutParams();
                params.setMargins(0, (int) (e2.getRawY() - e1.getRawY()), 0, 0);
                downlist.setAlpha(1.0f - (e2.getRawY() - e1.getRawY()) / mScreenHeight);
                downlist.setLayoutParams(params);
            } else if (!(Boolean) downlist.getTag() && (e2.getRawY() - e1.getRawY()) < 0) {
                downlist.setVisibility(View.VISIBLE);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) downlist.getLayoutParams();
                params.setMargins(0, mScreenHeight - (int) (e1.getRawY() - e2.getRawY()), 0, 0);
                downlist.setAlpha(0.2f + ((e1.getRawY() - e2.getRawY()) / mScreenHeight));
                finish.setAlpha(1 - (((e1.getRawY() - e2.getRawY()) / mScreenHeight) + 0.2f));
                downlist.setLayoutParams(params);
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) downlist.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            downlist.setLayoutParams(params);

            FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) finish.getLayoutParams();
            params1.setMargins(0, 0, 0, 0);
            finish.setLayoutParams(params1);

            if (e2.getRawY() - e1.getRawY() > (0.3 * mScreenHeight) && (Boolean) downlist.getTag() && e1.getRawY() < 0.4 * mScreenHeight) {
                finish.setAlpha(1.0f);
                downlist.setVisibility(View.GONE);
                downlist.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scoredetail_out));
                downlist.setTag(false);
                return true;
            }

            if (e1.getRawY() - e2.getRawY() > (0.3 * mScreenHeight) && !(Boolean) downlist.getTag()) {
                downlist.setAlpha(1.0f);
                downlist.setVisibility(View.VISIBLE);
                downlist.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scoredetail_in));
                downlist.setTag(true);
                return true;
            }
            return false;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.atypullfinish, null);
        weekData.clear();

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        mScreenHeight = wm.getDefaultDisplay().getHeight();

        circleprogressbar = (TasksCompletedView) view.findViewById(R.id.circleProgressbar);
        initProgress();

        downlist = (LinearLayout) view.findViewById(R.id.downlist);
        downlist.setTag(false);

        finish = (LinearLayout) view.findViewById(R.id.finish);
        finish.setOnTouchListener(this);
        LinearLayout dragLayout = (LinearLayout) view.findViewById(R.id.dragLayout);
        dragLayout.setOnTouchListener(this);
        listview = (RecyclerView) view.findViewById(R.id.lvWeek);
        listview.setLayoutManager(new LinearLayoutManager(getContext()));
        listview.setItemAnimator(new DefaultItemAnimator());
        listview.setHasFixedSize(true);
        //去除list中重复元素， 使用hashset
        weekData = getCursor();
        adapter = refreshWeekView(getActivity(), weekData, listview);
        listview.setAdapter(adapter);
        return view;
    }

    public void initProgress() {
        mTotalProgress = 100;
        mCurrentProgress = 0;

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isFresh) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if ((Boolean) downlist.getTag()) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) downlist.getLayoutParams();
                    downlist.setAlpha(1.0f);
                    params.setMargins(0, 0, 0, 0);
                    downlist.setLayoutParams(params);
                } else if (!(Boolean) downlist.getTag()) {
                    downlist.setVisibility(View.GONE);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) finish.getLayoutParams();
                    params.setMargins(0, 0, 0, 0);
                    finish.setAlpha(1.0f);
                    finish.setLayoutParams(params);
                }
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            }
            return detector.onTouchEvent(event);
        } else {
            return true;
        }
    }


    class cursorAdapter extends RecyclerView.Adapter<cursorAdapter.ViewHolder> implements DataRefresh {

        private Context context;
        private ArrayList<String> list = new ArrayList<>();
        LayoutInflater layoutinflate;
        public DataRefresh dataRefresh;
        private boolean isRefresh;

        public cursorAdapter(Context context, ArrayList<String> list) {       //启动时执行
            this.context = context;
            this.list = list;
            layoutinflate = LayoutInflater.from(context);
            this.setDataRefresh(this);
        }

        public void setDataRefresh(DataRefresh dataRefresh) {
            this.dataRefresh = dataRefresh;
        }

        @Override
        public void refreshView(int position) {
            if (isRefresh) {
                notifyItemRemoved(position);
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {                    //第四步执行
            public ViewHolder(View itemView) {
                super(itemView);
                tvWeek = (TextView) itemView.findViewById(R.id.tvWeek);
                tvDate = (TextView) itemView.findViewById(R.id.tvDate);
                tvPercent = (TextView) itemView.findViewById(R.id.tvPercent);
                rlWeek = (LinearLayout) itemView.findViewById(R.id.rlWeek);
                lnCard = (RelativeLayout) itemView.findViewById(R.id.ll1);
                moreImageView = (ImageView) itemView.findViewById(R.id.week_card_more);
                // TODO Auto-generated constructor stub
            }

            TextView tvWeek, tvDate, tvPercent;
            LinearLayout rlWeek;
            RelativeLayout lnCard;
            ImageView moreImageView;

        }

        //处理删除没有内容的卡片的方法
        void startHandler(int position) {
            Message msg = changeHandler.obtainMessage();
            msg.what = DELETE_DATA;
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            msg.setData(bundle);
            changeHandler.sendMessage(msg);
        }

        @Override
        public int getItemCount() {                //第二步执行

            // TODO Auto-generated method stub
            //如果删除之后就会少一个item，而读取的时候会按顺序读取星期。
            if (flag == 1 || flag == 2) {
                return weekData.size() + 1;
            } else {
                return weekData.size();
            }
        }

        //position 参数来自 getAdapterPosition
        @Override
        public void onBindViewHolder(ViewHolder right, int position) {      //第五步执行
            db = new OneDaydb(getActivity(), "oneday");
            /*
             * 统计界面每天的完成度在这里设置，可能需要新建一个表存放每天完成度统计然后从表内取出数据。该表可能需要两个字段，分别是week
			 * 和percent，根据星期取出每天的完成统计情况
			 */
//            Log.e(TAG,"flag = " + flag);
            //  根据flag判断删除数据之后的item读取position
            if (flag == 2) {
                position = 0;
            } else if (flag == 1) {
                position = position - 1;
            }
            if (position < 0 || position == weekData.size()) {
                return;
            }

            right.rlWeek.removeAllViews();
            //需要去除数据库冗余问题，以及当计划重复时，在统计界面只显示某一天的问题。
            //只能通过检查item是否重复去除冗余
            String week = weekData.get(position);
            s = dbreader.rawQuery(" select * from oneday where week=" + "'" + week + "'", null);
            if (s.getCount() == 0) {
                weekData.remove(position);
                isFresh = false;
                startHandler(position);
                isFresh = true;
                if (position == 0) {
                    flag = 2;
                } else {
                    flag = 1;
                }
            } else {
                if (flag == 2) {
                    flag = 1;
                }

                if (week.equals(TimeCalendar.getTodayWeek())) {
                    right.tvWeek.setText(week);
                    right.tvDate.setText(TimeCalendar.getLaterDate(0) + " /今天");
                    right.tvPercent.setText(String.valueOf(db.finishPercent(week)) + "%");
                    right.tvDate.setTextColor(getResources().getColor(R.color.blue));
                    right.tvPercent.setTextColor(getResources().getColor(R.color.blue));
                    for (s.moveToFirst(); !s.isAfterLast(); s.moveToNext()) {
                        RelativeLayout rl = new RelativeLayout(context);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(DimenTranslate.dp2px(context, 9), 0, DimenTranslate.dp2px(context, 9), 0);
                        rl.setLayoutParams(params);
                        TextView tv = new TextView(context);
                        TextView tv1 = new TextView(context);
                        tv.setText(s.getString(c.getColumnIndex(OneDaydb.COLUMN_PLAN)));
                        tv1.setText(s.getString(c.getColumnIndex(OneDaydb.COLUMN_FROM_TIME)) + "-" + s.getString(c.getColumnIndex(OneDaydb.COLUMN_TO_TIME)));
                        RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        RelativeLayout.LayoutParams textViewParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        textViewParams1.leftMargin = DimenTranslate.dp2px(context, 9);
                        textViewParams.rightMargin = DimenTranslate.dp2px(context, 9);
                        textViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        tv.setLayoutParams(textViewParams1);
                        tv1.setGravity(Gravity.END);
                        tv1.setLayoutParams(textViewParams);
                        rl.addView(tv);
                        rl.addView(tv1);
                        right.rlWeek.addView(rl);
                    }
                } else {
                    RelativeLayout rl = new RelativeLayout(context);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(DimenTranslate.dp2px(context, 9), 0, DimenTranslate.dp2px(context, 9), 0);
                    rl.setLayoutParams(params);
                    right.moreImageView.setVisibility(View.GONE);
                    right.tvWeek.setText(week);
                    right.tvWeek.setTextColor(getResources().getColor(R.color.shadow));
                    int skipDay = TimeCalendar.getLaterDay(week)
                            - TimeCalendar.getLaterDay(TimeCalendar.getTodayWeek());
                    if (skipDay < 0) {
                        skipDay = 5 - skipDay;
                    }
                    right.tvDate.setText(TimeCalendar.getLaterDate(skipDay) + "  /未到");
                    right.tvDate.setTextColor(getResources().getColor(R.color.shadow));
                    right.tvPercent.setText("0%");
                    right.tvPercent.setTextColor(getResources().getColor(R.color.shadow));
                    TextView tv = new TextView(context);
                    tv.setText("有" + s.getCount() + "个计划任务在列表中");
                    tv.setTextColor(getResources().getColor(R.color.shadow));
                    RelativeLayout.LayoutParams textViewParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    textViewParams1.leftMargin = DimenTranslate.dp2px(context, 9);
                    tv.setLayoutParams(textViewParams1);
                    rl.addView(tv);
                    right.rlWeek.addView(rl);
                    laterDay += 1;
                }

            }

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {                //第三步执行
            View v = LayoutInflater.from(arg0.getContext()).inflate(R.layout.item_layout, arg0, false);
            return new ViewHolder(v);
        }

    }

    public ArrayList<String> getCursor() {
        db = new OneDaydb(getActivity(), "oneday");
        c = db.Query();
        dbreader = db.getReadableDatabase();
        ss = dbreader.rawQuery(" select week from oneday", null);
        if (ss.moveToFirst()) {
            do {
                weekData.add(ss.getString(ss.getColumnIndex(OneDaydb.COLUMN_WEEK)));
            } while (ss.moveToNext());
        }
        hashSet = new HashSet(weekData);
        weekData.clear();
        weekData.addAll(hashSet);

        weekData = sortWeekCard(weekData);
        return weekData;
    }

    /**
     * 对周页面卡片布局的排序,按星期从小到大排列
     */
    private ArrayList<String> sortWeekCard(ArrayList<String> list) {
        ArrayList<String> sorted_list = new ArrayList<>();
        ArrayList<Integer> list1 = new ArrayList<>();
        String week = TimeCalendar.getTodayWeek();
        if (list.contains(week)) {
            sorted_list.add(week);
            list.remove(week);
            list1.add(TimeCalendar.getWeekMap().get(week));
            for (String s : list) {
                list1.add(TimeCalendar.getWeekMap().get(s));
            }
            return compareWeek(list1);
        } else {
            return list;
        }
    }

    /*
    *   通过对星期的比较排序完成页面卡片
    */
    private ArrayList<String> compareWeek(ArrayList<Integer> com_list) {
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        ArrayList<String> sorted_list = new ArrayList<>();
        ArrayList<Integer> list1 = new ArrayList<>();
        ArrayList<Integer> list2 = new ArrayList<>();
        int mutil = 0;
        int first = com_list.get(0);
        hashMap.put(0, first);
        for (int m : com_list) {
            mutil = m - first;
            if (m - first > 0) {
                list1.add(mutil);
            } else if (m - first < 0) {
                list2.add(Math.abs(mutil));
            }
        }
        HashMap<Integer, String> map = TimeCalendar.getWeekInMap();
        Collections.sort(list1);
        Collections.sort(list2);

        sorted_list.add(map.get(first));
        for (int i : list1) {
            sorted_list.add(map.get(first + i));
        }
        for (int j : list2) {
            sorted_list.add(map.get(j));
        }
        return sorted_list;
    }

    public cursorAdapter refreshWeekView(Context context, ArrayList<String> listdata, RecyclerView listview) {
        cursorAdapter cAdapter = new cursorAdapter(context, listdata);
        cAdapter.notifyDataSetChanged();
        return cAdapter;
    }

    @Override
    public void onStop() {
        super.onStop();
        flag = 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        //以这种方式刷新完成界面数据。。。。效率不是很高   后期需要重新考虑方法。
        laterDay = 0;
        weekData = getCursor();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}


//响应AtyDay页面删除item的Handler
class ChangeHandler extends android.os.Handler {
    public static String TAG = "ChangeHandler";

    public ChangeHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case CHANGE_WEEK_VIEW:
                AtyFinish.adapter.notifyDataSetChanged();
                AtyFinish.listview.postInvalidate();
                break;
            case DELETE_DATA:
                //对finish的view进行刷新，即没有内容的卡片会被删除
                //使flag=0，防止完成界面item重复读取
                AtyFinish.flag = 0;
                AtyFinish.adapter.notifyItemRemoved(msg.getData().getInt("position"));
                break;
        }
        super.handleMessage(msg);
    }
}

