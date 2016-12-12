package com.example.fb0122.oneday;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fb0122.oneday.utils.TimeCalendar;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import db_oneday.OneDaydb;
import oneday.Alarm.Config;

import static com.example.fb0122.oneday.AtyEditCustom.TIMEPICKER_TAG;


public class AtyDay extends Fragment implements SlideListView.RefreshPlanListener {

    private static final int SELECTED_WEEK = 1;
    private static final int UNSELECTED_WEEK = 0;

    public SlideListView lvDay;
    private OneDaydb db;   // dedb供删除使用的数据库
    public Context mContext;
    public Cursor c; //dec供删除使用的Cursor
    public MyAdapter adapter;
    private AtyFinish.ChangeHandler handler = new AtyFinish.ChangeHandler(Looper.myLooper());
    private TimeHandler timeHandler ;
    private static int clickButtonPosition;
    private MyAdapter.ViewHolder viewHolder;

    public AtyDay(Context context) {
        super();
        this.mContext = context;
        timeHandler = new TimeHandler(Looper.getMainLooper(),mContext);
    }

    public AtyDay() {

    }

    @Override
    public void onRefresh() {
        totalRefresh();
    }

    @Override
    public void onPlanFinish(int visible, String plan) {
        switch (visible) {
            case View.VISIBLE:
                db.planDone(plan, TimeCalendar.getWeekInYear(), 1);
                adapter.refresh();
                break;
            case View.GONE:
                db.planDone(plan, TimeCalendar.getWeekInYear(), 0);
                adapter.refresh();
                break;
        }
    }



    class MyAdapter extends BaseExpandableListAdapter implements TimePickerDialog.OnTimeSetListener, OnClickListener {

        private Context mContext;
        private Cursor c;
        private boolean flag;
        private Calendar calendar = Calendar.getInstance();
        private ChildViewHolder child = new ChildViewHolder();
        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
        private FragmentManager fragmentManager;
        private ArrayList<String> changedWeek = new ArrayList<>();

        public MyAdapter(Context context, Cursor c, FragmentManager fragmentManager) {
            this.mContext = context;
            this.c = c;
            this.fragmentManager = fragmentManager;
        }

        public MyAdapter(Context context, Cursor c) {
            this.mContext = context;
            this.c = c;
        }

        private void removeSelectedItem(int position) {
            c.moveToPosition(position);
            int itemId = c.getInt(c.getColumnIndex("_id"));
            db.delete(itemId);
            refresh();
        }


        //通过position取得当前滑动的item的view
        public void getLinePosition(int position) {
            int firstPosition = lvDay.getFirstVisiblePosition();
            int lastPosition = lvDay.getLastVisiblePosition();
            if (position >= firstPosition && position <= lastPosition) {
                View view = lvDay.getChildAt(position - lvDay.getFirstVisiblePosition());
                if (view.getTag() instanceof ViewHolder) {
                    ViewHolder lineHold = (ViewHolder) view.getTag();
                    lvDay.saddLine(lineHold.addLine, lineHold.scEdit, lineHold.tvSc, db, lineHold.tvSc.getText().toString());
                }
            }
            lvDay.postInvalidate();
        }


        @Override
        public int getGroupCount() {
            return c.getCount();
        }

        @Override
        public int getChildrenCount(int i) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupPosition;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean b, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item, parent, false);
                convertView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        lvDay.initSlideMode(SlideListView.MOD_BOTH, mContext);
                        return false;
                    }
                });
                holder = new ViewHolder();
                holder.hSView = (RelativeLayout) convertView.findViewById(R.id.hsv);
                holder.ll_intent = (RelativeLayout) convertView.findViewById(R.id.ll_intent);
                holder.tvSc = (TextView) convertView.findViewById(R.id.tvSc);
                holder.scEdit = (EditText) convertView.findViewById(R.id.edit_Sc);
                holder.scEdit.setVisibility(View.GONE);
                holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
                holder.tvTo = (TextView) convertView.findViewById(R.id.tvTo);
                holder.line = (TextView) convertView.findViewById(R.id.line);
                holder.other1 = (RelativeLayout) convertView.findViewById(R.id.other1);
                holder.other2 = (RelativeLayout) convertView.findViewById(R.id.other2);
                holder.delete1 = (RelativeLayout) convertView.findViewById(R.id.delete1);
                holder.addLine = (TextView) convertView.findViewById(R.id.Line);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();   //当convertView第一次使用时，我们需要创建它。当第二次使用时，就可以使用getTag()直接使用
            }

            c.moveToPosition(groupPosition);
            setData(holder, null);
            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
            ChildViewHolder childHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.expand_layout, null);
                convertView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        lvDay.initSlideMode(SlideListView.MOD_FORBID, mContext);
                        return true;
                    }
                });
                childHolder = new ChildViewHolder();
                childHolder.expandPlanText = (EditText) convertView.findViewById(R.id.text_expand_plan);
                childHolder.expandRelativeLayout = (LinearLayout) convertView.findViewById(R.id.relative_expand);
                childHolder.week_select = (RelativeLayout) convertView.findViewById(R.id.week_select);
                childHolder.fromTimeExpandText = (TextView) convertView.findViewById(R.id.text_expand_from_time);
                childHolder.fromTimeExpandText.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        flag = true;
                        timePicker();
                    }
                });
                childHolder.toTimeExpandText = (TextView) convertView.findViewById(R.id.text_expand_to_time);
                childHolder.toTimeExpandText.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        flag = false;
                        timePicker();
                    }
                });
                childHolder.reminderExpandImg = (ImageView) convertView.findViewById(R.id.img_expand_reminder);
                childHolder.tipsSpinner = (Spinner) convertView.findViewById(R.id.spinner_expand_tips);
                childHolder.cancelExpandBtn = (Button) convertView.findViewById(R.id.btn_expand_cancel);
                childHolder.sureExpandBtn = (Button) convertView.findViewById(R.id.btn_expand_sure);
                childHolder.Sun = (TextView) convertView.findViewById(R.id.Sun);
                childHolder.Mon = (TextView) convertView.findViewById(R.id.Mon);
                childHolder.Tue = (TextView) convertView.findViewById(R.id.Tue);
                childHolder.Wed = (TextView) convertView.findViewById(R.id.Wed);
                childHolder.Thu = (TextView) convertView.findViewById(R.id.Thu);
                childHolder.Fri = (TextView) convertView.findViewById(R.id.Fri);
                childHolder.Sat = (TextView) convertView.findViewById(R.id.Sat);

                childHolder.cancelExpandBtn.setOnClickListener(this);
                childHolder.sureExpandBtn.setOnClickListener(this);

                childHolder.Sun.setOnClickListener(this);
                childHolder.Mon.setOnClickListener(this);
                childHolder.Tue.setOnClickListener(this);
                childHolder.Wed.setOnClickListener(this);
                childHolder.Thu.setOnClickListener(this);
                childHolder.Fri.setOnClickListener(this);
                childHolder.Sat.setOnClickListener(this);
                convertView.setTag(childHolder);
            } else {
                childHolder = (ChildViewHolder) convertView.getTag();
            }
            child = childHolder;
            setData(null, childHolder);

            return convertView;
        }

        private void setData(ViewHolder holder, ChildViewHolder childViewHolder) {
            if (holder != null) {
                holder.tvSc.setText(c.getString(1));            // 计划名称
                holder.tvTime.setText(c.getString(2));          //开始时间
                holder.tvTo.setText(c.getString(3));            //结束时间
                if (c.getInt(6) == 1) {                         //计划是否被标记为完成
                    holder.addLine.setVisibility(View.VISIBLE);
                    holder.tvSc.setTextColor(getResources().getColor(R.color.shadow));
                    holder.tvTime.setTextColor(getResources().getColor(R.color.shadow));
                    holder.tvTo.setTextColor(getResources().getColor(R.color.shadow));
                    holder.line.setTextColor(getResources().getColor(R.color.shadow));
                } else {
                    holder.addLine.setVisibility(View.GONE);
                    holder.tvSc.setTextColor(getResources().getColor(R.color.text_color));
                    holder.tvTime.setTextColor(getResources().getColor(R.color.text_color));
                    holder.tvTo.setTextColor(getResources().getColor(R.color.text_color));
                    holder.line.setTextColor(getResources().getColor(R.color.text_color));
                }
            } else {
                childViewHolder.expandPlanText.setText(c.getString(1));
                childViewHolder.fromTimeExpandText.setText(c.getString(2));
                childViewHolder.toTimeExpandText.setText(c.getString(3));
                checkedWeek(childViewHolder, c.getString(1));
            }
        }

        //查询当前计划的重复星期
        private void checkedWeek(ChildViewHolder childViewHolder,String plan){
            resetWeekState(childViewHolder);
            SQLiteDatabase readWeek = db.getReadableDatabase();
            Cursor weekCursor = readWeek.rawQuery("select week from " + OneDaydb.TABLE_NAME + " where " +
                    OneDaydb.COLUMN_PLAN + " = '" + plan + "'", null);
            if (weekCursor != null && weekCursor.moveToFirst()){
                String week;
                do {
                    week = weekCursor.getString(0);
                    switch (week) {
                        case "周一":
                            childViewHolder.Mon.setBackground(mContext.getResources().getDrawable(R.drawable.double_click));
                            childViewHolder.Mon.setTag(SELECTED_WEEK);
                            changedWeek.add("一");
                            break;
                        case "周二":
                            childViewHolder.Tue.setBackground(mContext.getResources().getDrawable(R.drawable.double_click));
                            childViewHolder.Tue.setTag(SELECTED_WEEK);
                            changedWeek.add("二");
                            break;
                        case "周三":
                            childViewHolder.Wed.setBackground(mContext.getResources().getDrawable(R.drawable.double_click));
                            childViewHolder.Wed.setTag(SELECTED_WEEK);
                            changedWeek.add("三");
                            break;
                        case "周四":
                            childViewHolder.Thu.setBackground(mContext.getResources().getDrawable(R.drawable.double_click));
                            childViewHolder.Thu.setTag(SELECTED_WEEK);
                            changedWeek.add("四");
                            break;
                        case "周五":
                            childViewHolder.Fri.setBackground(mContext.getResources().getDrawable(R.drawable.double_click));
                            childViewHolder.Fri.setTag(SELECTED_WEEK);
                            changedWeek.add("五");
                            break;
                        case "周六":
                            childViewHolder.Sat.setBackground(mContext.getResources().getDrawable(R.drawable.double_click));
                            childViewHolder.Sat.setTag(SELECTED_WEEK);
                            changedWeek.add("六");
                            break;
                        case "周日":
                            childViewHolder.Sun.setBackground(mContext.getResources().getDrawable(R.drawable.double_click));
                            childViewHolder.Sun.setTag(SELECTED_WEEK);
                            changedWeek.add("日");
                            break;

                    }
                }while ((weekCursor.moveToNext()));
            }
            if (weekCursor != null) {
                weekCursor.close();
            }
        }

        //重置展开星期状态
        private void resetWeekState(ChildViewHolder childHolder){
            childHolder.Sun.setBackground(mContext.getResources().getDrawable(R.drawable.week_click));
            childHolder.Mon.setBackground(mContext.getResources().getDrawable(R.drawable.week_click));;
            childHolder.Tue.setBackground(mContext.getResources().getDrawable(R.drawable.week_click));
            childHolder.Wed.setBackground(mContext.getResources().getDrawable(R.drawable.week_click));
            childHolder.Thu.setBackground(mContext.getResources().getDrawable(R.drawable.week_click));
            childHolder.Fri.setBackground(mContext.getResources().getDrawable(R.drawable.week_click));
            childHolder.Sat.setBackground(mContext.getResources().getDrawable(R.drawable.week_click));
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }

        public void timePicker() {
            timePickerDialog.show(fragmentManager, TIMEPICKER_TAG);
        }

        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
            if (flag) {
                if (minute < 10) {
                    String minute_str = "0" + minute;
                    child.fromTimeExpandText.setText(hourOfDay + ":" + minute_str);
                } else {
                    child.fromTimeExpandText.setText(hourOfDay + ":" + minute + "");
                }
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

            } else {
                if (minute < 10) {
                    String minute_str = "0" + minute;
                    child.toTimeExpandText.setText(hourOfDay + ":" + minute_str);
                } else {
                    child.toTimeExpandText.setText(hourOfDay + ":" + minute + "");
                }
            }
        }


        //想一下listView局部刷新吧
        @Override
        public void onClick(View view) {
            boolean change = false;
            if (view.getId() == R.id.btn_expand_cancel) {
                lvDay.collapseGroup(clickButtonPosition);
                return;
            }
            if (view.getId() == R.id.btn_expand_sure) {
                View v = lvDay.getChildAt(clickButtonPosition);
                c.moveToPosition(clickButtonPosition);
                ContentValues cv = new ContentValues();
                String plan = child.expandPlanText.getText().toString();
                String time = child.fromTimeExpandText.getText().toString();
                String to = child.toTimeExpandText.getText().toString();
                if (!plan.equals(c.getString(1))) {
                    String oriStr = viewHolder.tvSc.getText().toString();
                    viewHolder.tvSc = (TextView) v.findViewById(R.id.tvSc);
                    cv.put(OneDaydb.COLUMN_PLAN, plan);
                    db.updateData(OneDaydb.TABLE_NAME, cv, oriStr);
                    change = true;
                    cv.clear();
                }
                if (!time.equals(c.getString(2))) {
                    String oriTime = viewHolder.tvTime.getText().toString();
                    cv.put(OneDaydb.COLUMN_FROM_TIME, time);
                    db.updataData(OneDaydb.TABLE_NAME, cv, OneDaydb.COLUMN_FROM_TIME, oriTime, plan);
                    change = true;
                    cv.clear();
                }
                if (!to.equals(c.getString(3))) {
                    String toTime = viewHolder.tvTo.getText().toString();
                    cv.put(OneDaydb.COLUMN_TO_TIME, to);
                    db.updataData(OneDaydb.TABLE_NAME, cv, OneDaydb.COLUMN_TO_TIME, toTime, plan);
                    change = true;
                    cv.clear();
                }

                if (changedWeek.size() != 0) {
                    SQLiteDatabase readWeek = db.getReadableDatabase();
                    Cursor weekCursor = readWeek.rawQuery("select week from " + OneDaydb.TABLE_NAME + " where " +
                            OneDaydb.COLUMN_PLAN + " = '" + plan + "'", null);
                    String oriWeek;
                    int count = weekCursor.getCount();
                    if (weekCursor.moveToFirst()) {
                        int i = 0;
                        int j = 0;
                        do {
                            oriWeek = weekCursor.getString(0);
                            if (changedWeek.size() < count && i < changedWeek.size()){
                                cv.put(OneDaydb.COLUMN_WEEK, "周" + changedWeek.get(i));
                                db.updataData(OneDaydb.TABLE_NAME, cv, OneDaydb.COLUMN_WEEK, oriWeek, plan);
                                cv.clear();
                            } else if (changedWeek.size() > count && j < count) {
                                for (j = count; j < changedWeek.size(); j++) {
                                    db.insertData(plan, time, to, "周" + changedWeek.get(j));
                                }
                            }else if (i < changedWeek.size()){
                                cv.put(OneDaydb.COLUMN_WEEK, "周" + changedWeek.get(i));
                                db.updataData(OneDaydb.TABLE_NAME, cv, OneDaydb.COLUMN_WEEK, oriWeek, plan);
                                cv.clear();
                            }
                            i++;
                        }while (weekCursor.moveToNext());
                    }
                    weekCursor.close();
                    change = true;
                    changedWeek.clear();
                }
                if (change) {
                    totalRefresh();
                }
                lvDay.collapseGroup(clickButtonPosition);
                return;

            }
            TextView tv = (TextView) view;
            if (tv.getTag() != null && (int) tv.getTag() == SELECTED_WEEK) {
                tv.setBackground(mContext.getResources().getDrawable(R.drawable.week_click));
                changedWeek.remove(tv.getText().toString());
                tv.setTag(UNSELECTED_WEEK);
            } else {
                tv.setBackground(mContext.getResources().getDrawable(R.drawable.double_click));
                changedWeek.add(tv.getText().toString());
                tv.setTag(SELECTED_WEEK);
            }
        }

        //这里使用到了ViewHolder 的复用
        class ViewHolder {

            RelativeLayout hSView, other1, other2, delete1;
            public View content;
            TextView tvTime, line, tvTo;
            TextView tvSc;
            private EditText scEdit;
            private TextView addLine;
            RelativeLayout ll_intent;

        }

        class ChildViewHolder {
            EditText expandPlanText;
            LinearLayout expandRelativeLayout;
            RelativeLayout week_select;
            TextView fromTimeExpandText, toTimeExpandText;
            ImageView reminderExpandImg;
            Spinner tipsSpinner;
            Button cancelExpandBtn, sureExpandBtn;
            private TextView Sun, Mon, Tue, Wed, Thu, Fri, Sat;
        }

        private void refresh(){
            String week = TimeCalendar.getTodayWeek();
            c = null;
            c = db.Query(week);
            notifyDataSetChanged();
        }

    }
    /*
     * 删除数据时的回调接口
     */

    public SlideListView.RemoveListener removeListener = new SlideListView.RemoveListener() {

        @Override
        public void removeItem(SlideListView.RemoveDirection reDirection, int position, View itemView) {
            adapter.removeSelectedItem(position);
            lvDay.setAdapter(adapter);       //不加会报错
            notifyOnFinishRefresh();
        }
    };

    private void getData() {
        String week = TimeCalendar.getTodayWeek();
        c = db.Query(week);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.aty_day, null);
        lvDay = (SlideListView) view.findViewById(R.id.lvDay);
        lvDay.initSlideMode(SlideListView.MOD_BOTH, getActivity());
        //为listview添加头部分割线,必须要添加headerview才会显示
        View view_header = new View(getContext());
        lvDay.addHeaderView(view_header, null, true);
        lvDay.setHeaderDividersEnabled(true);
        ImageButton btnAdd = (ImageButton) view.findViewById(R.id.btnAdd);
        DisplayMetrics dm = new DisplayMetrics();
        db = new OneDaydb(getActivity(), "oneday");
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getData();
        adapter = new MyAdapter(getActivity(), c, getFragmentManager());

        //为slidelistview传入当前activity的adapter。让其在滑动的时候动态更新adapter
        lvDay.getAda(adapter);
        lvDay.setAdapter(adapter);
        /*
         * 调用接口删除数据
		 */

        lvDay.setRemoveListener(removeListener);
        lvDay.setRefreshPlanListener(this);
        lvDay.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                viewHolder = (MyAdapter.ViewHolder) view.getTag();
                clickButtonPosition = i;
                return false;
            }
        });
        btnAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(getActivity(), AtyEditCustom.class);
                startActivityForResult(i, 0);
                getActivity().overridePendingTransition(R.anim.zoom_in,R.anim.zoom_out);
            }
        });
        return view;


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        c.close();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Config.CHANGE_DATA) {
                    totalRefresh();
                }
                break;
        }
    }

    @Override
    public void onScrollChangePlan(int position) {
        notifyOnFinishRefresh();
    }

    public void totalRefresh() {
        adapter.refresh();
        notifyOnFinishRefresh();
        Message msg = timeHandler.obtainMessage();
        msg.what = Config.ADD_NOTIFY;
        timeHandler.sendMessage(msg);
    }

    public void notifyOnFinishRefresh() {
        Message msg = handler.obtainMessage();
        msg.what = Config.CHANGE_WEEK_VIEW;
        handler.sendMessage(msg);
    }

}

