package com.example.fb0122.oneday.view;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.fb0122.oneday.R;
import com.example.fb0122.oneday.SlideListView;
import com.example.fb0122.oneday.TimeHandler;
import com.example.fb0122.oneday.adapter.MyAdapter;
import com.example.fb0122.oneday.utils.TimeCalendar;
import db_oneday.OneDaydb;
import java.util.ArrayList;
import oneday.Alarm.Config;

public class AtyDay extends Fragment implements SlideListView.RefreshPlanListener,
    MyAdapter.OnChildrenListener {

  public SlideListView lvDay;
  private OneDaydb db;   // dedb供删除使用的数据库
  public Context mContext;
  public Cursor c; //dec供删除使用的Cursor
  public MyAdapter adapter;
  private AtyFinish.ChangeHandler handler = new AtyFinish.ChangeHandler(Looper.myLooper());
  private TimeHandler timeHandler;

  public AtyDay(Context context) {
    super();
    this.mContext = context;
    timeHandler = new TimeHandler(Looper.getMainLooper(), mContext);
  }

  public AtyDay() {

  }

  @Override public void onRefresh() {
    totalRefresh();
  }

  @Override public void onPlanFinish(int visible, String plan) {
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

  private void changePlanSure(int clickButtonPosition, MyAdapter.ChildViewHolder child,
      MyAdapter.ViewHolder viewHolder) {
    boolean change = false;
    ArrayList<String> changedWeek = new ArrayList<>();
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
      Cursor weekCursor = readWeek.rawQuery("select week from "
          + OneDaydb.TABLE_NAME
          + " where "
          + OneDaydb.COLUMN_PLAN
          + " = '"
          + plan
          + "'", null);
      String oriWeek;
      int count = weekCursor.getCount();
      if (weekCursor.moveToFirst()) {
        int i = 0;
        int j = 0;
        do {
          oriWeek = weekCursor.getString(0);
          if (changedWeek.size() < count && i < changedWeek.size()) {
            cv.put(OneDaydb.COLUMN_WEEK, "周" + changedWeek.get(i));
            db.updataData(OneDaydb.TABLE_NAME, cv, OneDaydb.COLUMN_WEEK, oriWeek, plan);
            cv.clear();
          } else if (changedWeek.size() > count && j < count) {
            for (j = count; j < changedWeek.size(); j++) {
              db.insertData(plan, time, to, "周" + changedWeek.get(j));
            }
          } else if (i < changedWeek.size()) {
            cv.put(OneDaydb.COLUMN_WEEK, "周" + changedWeek.get(i));
            db.updataData(OneDaydb.TABLE_NAME, cv, OneDaydb.COLUMN_WEEK, oriWeek, plan);
            cv.clear();
          }
          i++;
        } while (weekCursor.moveToNext());
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

  //通过position取得当前滑动的item的view
  public void getLinePosition(int position) {
    int firstPosition = lvDay.getFirstVisiblePosition();
    int lastPosition = lvDay.getLastVisiblePosition();
    if (position >= firstPosition && position <= lastPosition) {
      View view = lvDay.getChildAt(position - lvDay.getFirstVisiblePosition());
      if (view.getTag() instanceof MyAdapter.ViewHolder) {
        MyAdapter.ViewHolder lineHold = (MyAdapter.ViewHolder) view.getTag();
        lvDay.saddLine(lineHold.addLine, lineHold.scEdit, lineHold.tvSc, db,
            lineHold.tvSc.getText().toString());
      }
    }
    lvDay.postInvalidate();
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

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
    adapter = new MyAdapter(getActivity(), c, getFragmentManager(), db, this);

    //为slidelistview传入当前activity的adapter。让其在滑动的时候动态更新adapter
    lvDay.getAda(adapter);
    lvDay.setAdapter(adapter);
        /*
         * 调用接口删除数据
		 */

    lvDay.setRemoveListener(removeListener);
    lvDay.setRefreshPlanListener(this);

    btnAdd.setOnClickListener(new OnClickListener() {

      @Override public void onClick(View v) {
        Intent i = new Intent();
        i.setClass(getActivity(), AtyEditCustom.class);
        startActivityForResult(i, 0);
        getActivity().overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
      }
    });
    return view;
  }

  @Override public void onResume() {
    super.onResume();
  }

  @Override public void onStop() {
    super.onStop();
  }

  @Override public void onDestroy() {
    c.close();
    super.onDestroy();
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case 0:
        if (resultCode == Config.CHANGE_DATA) {
          totalRefresh();
        }
        break;
    }
  }

  @Override public void onScrollChangePlan(int position) {
    notifyOnFinishRefresh();
  }

  @Override public void addLine(int position) {
    getLinePosition(position);
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

  @Override public void onSure(int position, MyAdapter.ChildViewHolder childHolder, MyAdapter.ViewHolder holder) {
    changePlanSure(position, childHolder, holder);
  }

  @Override public void onCancel(int position) {
    lvDay.collapseGroup(position);
  }
}

