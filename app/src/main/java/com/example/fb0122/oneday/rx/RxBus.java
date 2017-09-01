package com.example.fb0122.oneday.rx;

import android.support.annotation.NonNull;
import com.example.fb0122.oneday.utils.LogUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * courtesy: https://gist.github.com/benjchristensen/04eef9ca0851f3a5d7bf
 */
public class RxBus {
    public static String TAG1 = "RxBus";
    public static String OPEN_DRAWER = "RxBus_opendrawer";
    public static String BUS_REFRESH = "RxBus_refresh";
    public static String BUS_DEFAUT = "RxBus_default";
    public static String BUS_CHOOSEPIC = "RxBus_choosepic";
    private static String TAG = "RxBus";
    private static RxBus _RxBus;
    // If multiple threads are going to emit events to this
    // then it must be made thread-safe like this instead
    private final Subject<Object, Object> _bus = new SerializedSubject<>(PublishSubject.create());
    //private final PublishSubject<Object> _bus = PublishSubject.create();
    private ConcurrentHashMap<Object, List<Subject>> subjectMapper = new ConcurrentHashMap<>();

    public static RxBus getBus() {
        if (_RxBus == null) {
            //      synchronized (_RxBus){
            //        if (_RxBus==null){
            _RxBus = new RxBus();
            //        }
            //      }

        }
        return _RxBus;
    }

    //    public void send(Object o) {
    //        _bus.onNext(o);
    //    }

    public Observable<Object> toObserverable() {
        return _bus;
    }

    public boolean hasObservers() {
        return _bus.hasObservers();
    }

    public <T> Observable<T> register(@NonNull Object tag, @NonNull Class<T> clazz) {
        List<Subject> subjectList = subjectMapper.get(tag);
        if (null == subjectList) {
            subjectList = new ArrayList<>();
            subjectMapper.put(tag, subjectList);
        }

        Subject<T, T> subject;
        subjectList.add(subject = PublishSubject.create());
        LogUtil.d(TAG, "[register]subjectMapper: " + subjectMapper);
        return subject;
    }

    public <T> Observable<T> register(@NonNull Object tag) {
        List<Subject> subjectList = subjectMapper.get(tag);
        if (null == subjectList) {
            subjectList = new ArrayList<>();
            subjectMapper.put(tag, subjectList);
        }

        Subject<T, T> subject;
        subjectList.add(subject = PublishSubject.create());
        LogUtil.d(TAG, "[register]subjectMapper: " + subjectMapper);
        return subject;
    }

    public <T> Observable<T> register(@NonNull Class<T> clazz) {
        String tag = clazz.getName();
        List<Subject> subjectList = subjectMapper.get(tag);
        if (null == subjectList) {
            subjectList = new ArrayList<>();
            subjectMapper.put(tag, subjectList);
        }

        Subject<T, T> subject;
        subjectList.add(subject = PublishSubject.create());
        LogUtil.d(TAG, "[register]subjectMapper: " + subjectMapper);
        return subject;
    }

    public void unregister(@NonNull String tag, @NonNull Observable observable) {
        List<Subject> subjects = subjectMapper.get(tag);
        if (null != subjects) {
            subjects.remove((Subject) observable);
            if (subjects.size() == 0) {
                subjectMapper.remove(tag);
                LogUtil.d(TAG, "[unregister]subjectMapper: " + subjectMapper);
            }
        }
    }

    public void post(@NonNull String content) {
        post(content, "");
    }

    public void post(@NonNull Object o) {
        post(o.getClass().getName(), o);
    }

    @SuppressWarnings("unchecked") public void post(@NonNull Object tag, @NonNull Object content) {
        List<Subject> subjectList = subjectMapper.get(tag);
        if (subjectList == null) return;

        if (subjectList.size() > 0) {
            for (Subject subject : subjectList) {
                subject.onNext(content);
            }
        }
        LogUtil.d(TAG, "[send]subjectMapper: " + subjectMapper);
    }
}