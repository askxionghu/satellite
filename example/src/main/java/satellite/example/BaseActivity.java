package satellite.example;

import android.app.Activity;
import android.os.Bundle;

import rx.Notification;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import satellite.Launcher;
import satellite.RestartableConnection;
import satellite.RestartableConnectionSet;
import satellite.RestartableFactory;
import satellite.RestartableFactoryNoArg;
import satellite.util.SubjectFactory;

/**
 * This is an example activity that eliminates code duplication when dealing with
 * {@link RestartableConnection} and {@link RestartableConnectionSet}.
 */
public class BaseActivity extends Activity implements Launcher {

    private RestartableConnectionSet connections;
    private Subscription subscription;
    private boolean connect = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connections = savedInstanceState == null ? new RestartableConnectionSet() : new RestartableConnectionSet(savedInstanceState.getParcelable("connections"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("connections", connections.instanceState());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (connect) {
            subscription = onConnect();
            connect = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
        if (isFinishing())
            connections.unsubscribe();
    }

    /**
     * This method is being called during the first {@link #onResume()} call.
     * The returned {@link Subscription} will be unsubscribed during {@link #onDestroy()}.
     *
     * You can combine multiple subscriptions with {@link Subscriptions#from(Subscription...)}.
     */
    protected Subscription onConnect() {
        return Subscriptions.empty();
    }

    public <T> Observable<Notification<T>> connection(int id, RestartableFactoryNoArg<T> restartableFactory) {
        return connections.connection(id, SubjectFactory.behaviorSubject(), restartableFactory);
    }

    public <A, T> Observable<Notification<T>> connection(int id, RestartableFactory<A, T> restartableFactory) {
        return connections.connection(id, SubjectFactory.behaviorSubject(), restartableFactory);
    }

    @Override
    public <T> Observable<Notification<T>> connection(int id, SubjectFactory<Notification<T>> subjectFactory, RestartableFactoryNoArg<T> restartableFactory) {
        return connections.connection(id, subjectFactory, restartableFactory);
    }

    @Override
    public <A, T> Observable<Notification<T>> connection(int id, SubjectFactory<Notification<T>> subjectFactory, RestartableFactory<A, T> restartableFactory) {
        return connections.connection(id, subjectFactory, restartableFactory);
    }

    @Override
    public void launch(int id, Object missionStatement) {
        connections.launch(id, missionStatement);
    }

    @Override
    public void launch(int id) {
        connections.launch(id);
    }

    @Override
    public void dismiss(int id) {
        connections.dismiss(id);
    }
}
