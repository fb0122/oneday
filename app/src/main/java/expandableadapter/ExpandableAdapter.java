package expandableadapter;//package com.tt.expandableadapter;
//
///**
// * 鍙睍寮�殑閫傞厤鍣�<鍔熻兘绠�堪> <Br>
// * <鍔熻兘璇︾粏鎻忚堪> <Br>
// * 
// * @author Kyson
// */
//public abstract class ExpandableAdapter<T> extends BaseAdapter  {
//
//
//	protected List<T> mDatas = new ArrayList<T>();
//    protected Context mContext;
//    protected LayoutInflater mInflater;
//    private int mLayoutRes;
//    private int mAboveRes;
//    private int mExpandRes;
//    //灞曞紑鐨刬tem 浣嶇疆
//    protected int mExpandedPosition = -1;
//
//    //灞曞紑鐨剉iew
//    private View mExpandView = null;
//    private Cursor cc;
//    
//    
//    public ExpandableAdapter(Context context, int layoutRes, int aboveRes,
//            int expandRes,Cursor c) {
//    	super();
//        this.mContext = context;
//        mInflater = (LayoutInflater) mContext
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        this.mLayoutRes = layoutRes;
//        this.mAboveRes = aboveRes;
//        this.mExpandRes = expandRes;
//        this.cc = c;
//    }
//    
//    
//
//
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder = null;
//        if (convertView == null) {
//            viewHolder = new ViewHolder();
//            convertView = (ViewGroup) mInflater.inflate(mLayoutRes, parent,
//                    false);
////            convertView = createConvertView(position);
//            viewHolder.aboveContainer = (ViewGroup) convertView
//                    .findViewById(mAboveRes);
//            viewHolder.expandContainer = (ViewGroup) convertView
//                    .findViewById(mExpandRes);
////            getSlideModeInPosition(position);
////            getLeftBackViewId(position);
////            getRightBackViewId(position);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        View aboveV = getAboveView(position, viewHolder.above,
//                viewHolder.aboveContainer);
//        if (viewHolder.aboveContainer.getChildCount() > 0) {
//            viewHolder.aboveContainer.removeAllViews();
//        }
//        viewHolder.aboveContainer.addView(aboveV);
//        
//        aboveV.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View arg0, MotionEvent arg1) {
//				
//				return false;
//			}
//		});
//        
//        View expandV = getExpandView(position, viewHolder.expand,
//                viewHolder.expandContainer);
//        if (viewHolder.expandContainer.getChildCount() > 0) {
//            viewHolder.expandContainer.removeAllViews();
//        }
//        viewHolder.expandContainer.addView(expandV);
//
//        if (mExpandView == convertView && mExpandedPosition != position) {
//        }
//        if (mExpandedPosition == position) {
//            viewHolder.expandContainer.setVisibility(View.VISIBLE);
//            mExpandView = convertView;
//        } else {
//            viewHolder.expandContainer.setVisibility(View.GONE);
//        }
//        viewHolder.aboveContainer.setOnClickListener(new ExpandOnClickListener(
//                convertView, position));
//        return convertView;
//    }
//    
////    public class ExpandOnTouchEvent implements OnTouchListener{
////    	
////    	private View tConvertView;
////    	private int tposition;
////    	public ExpandOnTouchEvent(View view,int position) {
////    		this.tConvertView = view;
////    		this.tposition = position;
////		}
////    	
////		@Override
////		public boolean onTouch(View arg0, MotionEvent arg1) {
////			 if (mExpandedPosition == tposition) {
////	                close(getExView(tConvertView));
////	                mExpandView = null;
////	                mExpandedPosition = -1;
////	            } else {
////	                close(getExView(mExpandView));
////	                open(getExView(tConvertView));
////	                //                mExpandView = mConvertView;
////	                mExpandedPosition = tposition;
////	            }
////			return true;
////		}
////    	
////    	
////    }
//
//    public class ExpandOnClickListener implements OnClickListener {
//
//        private View mConvertView;
//
//        private int mPosition;
//
//        public ExpandOnClickListener(View view, int position) {
//            this.mConvertView = view;
//            this.mPosition = position;
//        }
//
//        
//        public void Expand(View v){
//        	  if (mExpandedPosition == mPosition) {
//                  close(getExView(mConvertView));
//                  mExpandView = null;
//                  mExpandedPosition = -1;
//              } else {
//                  close(getExView(mExpandView));
//                  open(getExView(mConvertView));
//                  //                mExpandView = mConvertView;
//                  mExpandedPosition = mPosition;
//              }
//        	
//        }
//        
//        @Override
//        public void onClick(View v) {
//            if (mExpandedPosition == mPosition) {
//                close(getExView(mConvertView));
//                mExpandView = null;
//                mExpandedPosition = -1;
//            } else {
//                close(getExView(mExpandView));
//                open(getExView(mConvertView));
//                //                mExpandView = mConvertView;
//                mExpandedPosition = mPosition;
//            }
//        }
//    }
//
//    /**
//     * 鏍规嵁convertview寰楀埌灞曞紑鐨剉iew <鍔熻兘绠�堪>
//     * 
//     * @param cv
//     * @return
//     */
//    private static View getExView(View cv) {
//        if (cv == null) {
//            return null;
//        }
//        ViewHolder viewHolder = (ViewHolder) cv.getTag();
//        return viewHolder.expandContainer;
//    }
//
//    private static int getHeight(View view) {
//        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0,
//                View.MeasureSpec.UNSPECIFIED);
//        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0,
//                View.MeasureSpec.UNSPECIFIED);
//        view.measure(widthSpec, heightSpec);
//        return view.getMeasuredHeight();
//    }
//    
//    private void open(final View view) {
//        if (view == null) {
//            return;
//        }
//Log.e("open expandview", "open");
//        view.setVisibility(View.VISIBLE);
//        int start = view.getLayoutParams().height < 0 ? 0 : view
//                .getLayoutParams().height;
//        ValueAnimator valueAnimator = ValueAnimator.ofInt(start,
//                getHeight(view));
//        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
//
//            @Override
//            public void onAnimationUpdate(ValueAnimator animator) {
//                int currentValue = (Integer) animator.getAnimatedValue();
//                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//                layoutParams.height = currentValue;
//                view.setLayoutParams(layoutParams);
//            }
//        });
//        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//        valueAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                notifyDataSetChanged();
//            }
//        });
//        valueAnimator.setDuration(500).start();
//    }
//
//    /**
//     * 鏀惰捣鑿滃崟 <鍔熻兘绠�堪>
//     */
//    private void close(final View view) {
//        if (view == null) {
//            return;
//        }
//        view.setVisibility(View.VISIBLE);
//        int start = view.getLayoutParams().height < 0 ? 0 : view
//                .getLayoutParams().height;
//        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, 0);
//        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
//
//            @Override
//            public void onAnimationUpdate(ValueAnimator animator) {
//                int currentValue = (Integer) animator.getAnimatedValue();
//                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//                layoutParams.height = currentValue;
//                view.setLayoutParams(layoutParams);
//            }
//        });
//        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//        valueAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                notifyDataSetChanged();
//            }
//        });
//        valueAnimator.setDuration(500).start();
//    }
//
//    protected abstract View getAboveView(int position, View convertView,
//            ViewGroup parent);
//
//    protected abstract View getExpandView(int position, View convertView,
//            ViewGroup parent);
//
//    public static class ViewHolder {
//        ViewGroup aboveContainer;
//        ViewGroup expandContainer;
//        View above;
//        View expand;
//    }
//
//    @Override
//    public int getCount() {
//        return cc.getCount();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return position;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
////    public void setList(List<T> list) {
////        this.mDatas = list;
////    }
//
//}
