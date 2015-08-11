package satellite.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.StringBuilderPrinter;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.internal.util.SubscriptionList;
import satellite.MissionControlCenter;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

public class BaseLaunchActivity extends AppCompatActivity {

    private static final String CONTROL_CENTER = "control_center";

    private MissionControlCenter controlCenter;
    private boolean isFirstOnCreate = true;
    private boolean isFirstOnResume = true;
    private boolean isDestroyed = false;
    private SubscriptionList subscriptions = new SubscriptionList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            controlCenter = savedInstanceState.getParcelable(CONTROL_CENTER);
        else
            controlCenter = new MissionControlCenter();
        isFirstOnCreate = savedInstanceState == null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CONTROL_CENTER, controlCenter);
    }

    @Override
    protected void onDestroy() {
        isDestroyed = true;
        super.onDestroy();
        subscriptions.unsubscribe();
        if (isFinishing())
            controlCenter.dismiss();
    }

    public boolean isFirstOnCreate() {
        return isFirstOnCreate;
    }

    public boolean isFirstOnResume() {
        return isFirstOnResume;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public MissionControlCenter controlCenter() {
        return controlCenter;
    }

    public void add(Subscription subscription) {
        subscriptions.add(subscription);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstOnResume) {
            controlCenter.restoreSatellites();

            add(Observable.interval(500, 500, TimeUnit.MILLISECONDS, mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long ignored) {
                        StringBuilder builder = new StringBuilder();
                        controlCenter().printSpaceStation(new StringBuilderPrinter(builder));
                        TextView report = (TextView)findViewById(R.id.stationReport);
                        report.setText(builder.toString());
                    }
                }));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFirstOnResume = false;
    }

    protected void log(String message) {
        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText(textView.getText() + "\n" + message);
        final ScrollView scrollView = (ScrollView)findViewById(R.id.scrollView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
}