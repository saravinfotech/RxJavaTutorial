package com.learning.rxjavatutorial;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.learning.rxjavatutorial.todolist.models.Task;
import com.learning.rxjavatutorial.todolist.util.DataSource;

import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity.jaav";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleRxIntroduciton();

        sampleFlowableUnboundedBackPressure();
    }

    private void sampleFlowableUnboundedBackPressure() {
        Flowable.range(0, 1000000)
                .onBackpressureBuffer()
                .observeOn(Schedulers.computation())
                .subscribe(new FlowableSubscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe: called");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: called " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError: called " + t.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: called");
                    }
                });
    }

    private void simpleRxIntroduciton() {
        Observable<Task> taskObservable = Observable
                .fromIterable(DataSource.createTaskList())
                .filter(new Predicate<Task>() {
                    @Override
                    public boolean test(Task task) throws Exception {
                        Thread.sleep(1000);
                        return task.isComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());


        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: called.");
            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "onNext: called"+ Thread.currentThread().getName());
                Log.d(TAG, "onNext: called"+ task.getDescription());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: called" + e.getLocalizedMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: called");
            }
        });
    }
}
