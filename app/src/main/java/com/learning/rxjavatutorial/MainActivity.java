package com.learning.rxjavatutorial;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.learning.rxjavatutorial.todolist.models.Task;
import com.learning.rxjavatutorial.todolist.util.DataSource;

import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity.jaav";
    CompositeDisposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        disposable = new CompositeDisposable();

        simpleRxIntroduciton();

        sampleFlowableUnboundedBackPressure();
        simpleRxDisposable();
    }

    //Simple sample to demonstrate the usage of Flowable.
    //Flowable helps over come the Backpressure issue. Observables are not aware but Flowables are.
    //Flowables are alternative to Observables to avoid Backpressure.
    //Backpressure is when the Observable emits more data than what the Observers can handle. This leads to Out Of memory exception
    private void sampleFlowableUnboundedBackPressure() {
        Observable<Integer> observable = Observable.just(0, 1000000);
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

    //Simple method to demonstrate Observables and Observers
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


    //Usage of disposable , same as simRxIntroduction method except this one adds the disposable method to the
    //onSubscribe method.
    //Disposable helps keep a track of all the observers used and clear them when no longer needed
    private void simpleRxDisposable() {
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
                //Adding the current observer to the disposable
                //clear to be done on destroy method
                disposable.add(d);
            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "onNext: called" + Thread.currentThread().getName());
                Log.d(TAG, "onNext: called" + task.getDescription());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear(); //recommended way to clear the disposable as the observer can be reused.
        //disposable.dispose(); //Not a recommended way, this also clears the Observer but this Observable cannot be reused after clearing.

    }

    //Demonstration of converting Observables to Flowables
    //When converting Observable to Flowable a backpressure strategy is required.
    public void convertObservableToFlowable() {
        Observable<Integer> observable = Observable
                .just(1, 2, 3, 4, 5);

        Flowable<Integer> flowable = observable.toFlowable(BackpressureStrategy.BUFFER);
    }

    //Demonstration of converting Observables to flowables and from flowables back to
    //Observables.
    //When converting Observable to Flowable a backpressure strategy is required.
    public void ObservableToFlowableAndBackToObservable() {
        Observable<Integer> observable = Observable
                .just(1, 2, 3, 4, 5);

        Flowable<Integer> flowable = observable.toFlowable(BackpressureStrategy.BUFFER);

        Observable<Integer> backToObservable = flowable.toObservable();
    }
}
