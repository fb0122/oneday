package com.example.fb0122.oneday.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.fb0122.oneday.R;
import com.example.fb0122.oneday.utils.TimeCalendar;
import com.example.fb0122.oneday.view.AtyEditCustom;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import db_oneday.OneDaydb;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.fb0122.oneday.view.AtyEditCustom.TIMEPICKER_TAG;

/**
 * Created by fb on 2017/9/1.
 */

public class MyAdapter extends BaseExpandableListAdapter
    implements TimePickerDialog.OnTimeSetListener, View.OnClickListener {

  private static final int SELECTED_WEEK = 1;
  private static final int UNSELECTED_WEEK = 0;

  private Context mContext;
  private Cursor c;
  private boolean flag;
  private Calendar calendar = Calendar.getInstance();
  private ViewHolder holder =  new ViewHolder();
  private ChildViewHolder child = new ChildViewHolder();
  final TimePickerDialog timePickerDialog =
      TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY),
          calendar.get(Calendar.MINUTE), false, false);
  private FragmentManager fragmentManager;
  private ArrayList<String> changedWeek = new ArrayList<>();
  private OneDaydb db;   // dedb供删除使用的数据库
  private OnChildrenListener listener;

  public MyAdapter(Context context, Cursor c, FragmentManager fragmentManager, OneDaydb db, OnChildrenListener listener) {
    this.mContext = context;
    this.c = c;
    this.fragmentManager = fragmentManager;
    this.db = db;
    this.listener = listener;
  }

  public MyAdapter(Context context, Cursor c) {
    this.mContext = context;
    this.c = c;
  }

  public void removeSelectedItem(int position) {
    c.moveToPosition(position);
    int itemId = c.getInt(c.getColumnIndex("_id"));
    db.delete(itemId);
    refresh();
  }

  @Override public int getGroupCount() {
    return c.getCount();
  }

  @Override public int getChildrenCount(int i) {
    return 1;
  }

  @Override public Object getGroup(int groupPosition) {
    return groupPosition;
  }

  @Override public Object getChild(int groupPosition, int childPosition) {
    return childPosition;
  }

  @Override public long getGroupId(int groupPosition) {
    return groupPosition;
  }

  @Override public long getChildId(int groupPosition, int childPosition) {
    return childPosition;
  }

  @Override public boolean hasStableIds() {
    return true;
  }

  @Override
  public View getGroupView(int groupPosition, boolean b, View convertView, ViewGroup parent) {
    final ViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item, parent, false);
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
      holder =
          (ViewHolder) convertView.getTag();   //当convertView第一次使用时，我们需要创建它。当第二次使用时，就可以使用getTag()直接使用
    }
    c.moveToPosition(groupPosition);
    this.holder = holder;
    setData(holder, null);
    return convertView;
  }

  @Override
  public View getChildView(final int groupPosition, int childPosition, boolean b, View convertView,
      ViewGroup viewGroup) {
    ChildViewHolder childHolder;
    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.expand_layout, null);
      childHolder = new ChildViewHolder();
      childHolder.expandPlanText = (EditText) convertView.findViewById(R.id.text_expand_plan);
      childHolder.expandRelativeLayout =
          (LinearLayout) convertView.findViewById(R.id.relative_expand);
      childHolder.week_select = (RelativeLayout) convertView.findViewById(R.id.week_select);
      childHolder.fromTimeExpandText =
          (TextView) convertView.findViewById(R.id.text_expand_from_time);
      childHolder.fromTimeExpandText.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          flag = true;
          timePicker();
        }
      });
      childHolder.toTimeExpandText = (TextView) convertView.findViewById(R.id.text_expand_to_time);
      childHolder.toTimeExpandText.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          flag = false;
          timePicker();
        }
      });
      childHolder.reminderExpandImg =
          (ImageView) convertView.findViewById(R.id.img_expand_reminder);
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
    childHolder.cancelExpandBtn.setTag(childPosition);
    childHolder.sureExpandBtn.setTag(childPosition);
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
        holder.tvSc.setTextColor(mContext.getResources().getColor(R.color.shadow));
        holder.tvTime.setTextColor(mContext.getResources().getColor(R.color.shadow));
        holder.tvTo.setTextColor(mContext.getResources().getColor(R.color.shadow));
        holder.line.setTextColor(mContext.getResources().getColor(R.color.shadow));
      } else {
        holder.addLine.setVisibility(View.GONE);
        holder.tvSc.setTextColor(mContext.getResources().getColor(R.color.text_color));
        holder.tvTime.setTextColor(mContext.getResources().getColor(R.color.text_color));
        holder.tvTo.setTextColor(mContext.getResources().getColor(R.color.text_color));
        holder.line.setTextColor(mContext.getResources().getColor(R.color.text_color));
      }
    } else {
      childViewHolder.expandPlanText.setText(c.getString(1));
      childViewHolder.fromTimeExpandText.setText(c.getString(2));
      childViewHolder.toTimeExpandText.setText(c.getString(3));
      checkedWeek(childViewHolder, c.getString(1));
    }
  }

  //查询当前计划的重复星期
  private void checkedWeek(ChildViewHolder childViewHolder, String plan) {
    resetWeekState(childViewHolder);
    SQLiteDatabase readWeek = db.getReadableDatabase();
    Cursor weekCursor = readWeek.rawQuery("select week from "
        + OneDaydb.TABLE_NAME
        + " where "
        + OneDaydb.COLUMN_PLAN
        + " = '"
        + plan
        + "'", null);
    if (weekCursor != null && weekCursor.moveToFirst()) {
      String week;
      do {
        week = weekCursor.getString(0);
        switch (week) {
          case "周一":
            childViewHolder.Mon.setBackground(
                mContext.getResources().getDrawable(R.drawable.double_click));
            childViewHolder.Mon.setTag(SELECTED_WEEK);
            changedWeek.add("一");
            break;
          case "周二":
            childViewHolder.Tue.setBackground(
                mContext.getResources().getDrawable(R.drawable.double_click));
            childViewHolder.Tue.setTag(SELECTED_WEEK);
            changedWeek.add("二");
            break;
          case "周三":
            childViewHolder.Wed.setBackground(
                mContext.getResources().getDrawable(R.drawable.double_click));
            childViewHolder.Wed.setTag(SELECTED_WEEK);
            changedWeek.add("三");
            break;
          case "周四":
            childViewHolder.Thu.setBackground(
                mContext.getResources().getDrawable(R.drawable.double_click));
            childViewHolder.Thu.setTag(SELECTED_WEEK);
            changedWeek.add("四");
            break;
          case "周五":
            childViewHolder.Fri.setBackground(
                mContext.getResources().getDrawable(R.drawable.double_click));
            childViewHolder.Fri.setTag(SELECTED_WEEK);
            changedWeek.add("五");
            break;
          case "周六":
            childViewHolder.Sat.setBackground(
                mContext.getResources().getDrawable(R.drawable.double_click));
            childViewHolder.Sat.setTag(SELECTED_WEEK);
            changedWeek.add("六");
            break;
          case "周日":
            childViewHolder.Sun.setBackground(
                mContext.getResources().getDrawable(R.drawable.double_click));
            childViewHolder.Sun.setTag(SELECTED_WEEK);
            changedWeek.add("日");
            break;
        }
      } while ((weekCursor.moveToNext()));
    }
    if (weekCursor != null) {
      weekCursor.close();
    }
  }

  //重置展开星期状态
  private void resetWeekState(ChildViewHolder childHolder) {
    childHolder.Sun.setBackground(mContext.getResources().getDrawable(R.drawable.week_click));
    childHolder.Mon.setBackground(mContext.getResources().getDrawable(R.drawable.week_click));
    ;
    childHolder.Tue.setBackground(mContext.getResources().getDrawable(R.drawable.week_click));
    childHolder.Wed.setBackground(mContext.getResources().getDrawable(R.drawable.week_click));
    childHolder.Thu.setBackground(mContext.getResources().getDrawable(R.drawable.week_click));
    childHolder.Fri.setBackground(mContext.getResources().getDrawable(R.drawable.week_click));
    childHolder.Sat.setBackground(mContext.getResources().getDrawable(R.drawable.week_click));
  }

  @Override public boolean isChildSelectable(int i, int i1) {
    return false;
  }

  public void timePicker() {
    timePickerDialog.show(fragmentManager, AtyEditCustom.TIMEPICKER_TAG);
  }

  @Override public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
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
  @Override public void onClick(View view) {
    if (view.getId() == R.id.btn_expand_cancel) {
      if (listener != null){
        listener.onCancel((int)view.getTag());
      }
      return;
    }
    if (view.getId() == R.id.btn_expand_sure) {
      if (listener != null){
        listener.onSure((int)view.getTag(), child, holder);
      }
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
  public static class ViewHolder {
    public RelativeLayout hSView, other1, other2, delete1;
    public View content;
    public TextView tvTime, line, tvTo;
    public TextView tvSc;
    public EditText scEdit;
    public TextView addLine;
    public RelativeLayout ll_intent;
  }

  public static class ChildViewHolder {
    public EditText expandPlanText;
    public LinearLayout expandRelativeLayout;
    public RelativeLayout week_select;
    public TextView fromTimeExpandText, toTimeExpandText;
    public ImageView reminderExpandImg;
    public Spinner tipsSpinner;
    public Button cancelExpandBtn, sureExpandBtn;
    public TextView Sun, Mon, Tue, Wed, Thu, Fri, Sat;
  }

  public void refresh() {
    String week = TimeCalendar.getTodayWeek();
    c = null;
    c = db.Query(week);
    notifyDataSetChanged();
  }

  public interface OnChildrenListener {
    void onSure(int position, ChildViewHolder childHolder, ViewHolder holder);
    void onCancel(int position);
  }
}
