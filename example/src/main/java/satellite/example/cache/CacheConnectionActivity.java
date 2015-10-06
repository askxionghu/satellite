package satellite.example.cache;

import android.os.Bundle;
import android.widget.TextView;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import satellite.ChannelType;
import satellite.example.BaseLaunchActivity;
import satellite.example.R;
import satellite.util.RxNotification;

public class CacheConnectionActivity extends BaseLaunchActivity {

    public static final int CONNECTION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_satellite);
        ((TextView)findViewById(R.id.title)).setText("Cache result connection");

        findViewById(R.id.launch).setOnClickListener(v -> launch(CONNECTION_ID, ExampleCacheRestartableFactory.argument(10)));
        findViewById(R.id.drop).setOnClickListener(v -> dismiss(CONNECTION_ID));
    }

    @Override
    protected Subscription onConnect() {
        return new CompositeSubscription(super.onConnect(),

            restartable(CONNECTION_ID, ChannelType.LATEST, new ExampleCacheRestartableFactory())
                .subscribe(RxNotification.split(
                    value -> {
                        log("CACHE: onNext " + value);
                        onNext(value);
                    },
                    throwable -> log("CACHE: onError " + throwable))));
    }
}
