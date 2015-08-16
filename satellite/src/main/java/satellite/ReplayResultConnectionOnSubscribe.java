package satellite;

import android.os.Bundle;

import rx.Notification;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func0;
import rx.subjects.ReplaySubject;

public class ReplayResultConnectionOnSubscribe<T> implements MissionControlCenter.SessionTypeOnSubscribe<T> {

    private final String key;
    private final SatelliteFactory<T> factory;
    private final Bundle missionStatement;

    public ReplayResultConnectionOnSubscribe(String key, SatelliteFactory<T> factory, Bundle missionStatement) {
        this.key = key;
        this.factory = factory;
        this.missionStatement = missionStatement;
    }

    @Override
    public void call(Subscriber<? super Notification<T>> subscriber) {

        Observable<Notification<T>> subject = SpaceStation.INSTANCE
            .provide(key + "/subject", new Func0<Observable<Notification<T>>>() {
                @Override
                public Observable<Notification<T>> call() {

                    ReplaySubject<Notification<T>> subject = ReplaySubject.create();

                    SpaceStation.INSTANCE.put(key + "/subscription", factory.call(missionStatement)
                        .materialize()
                        .subscribe(subject));

                    return subject;
                }
            });

        subscriber.add(subject.subscribe(subscriber));
    }

    public void recycle() {
        SpaceStation.INSTANCE.remove(key + "/subject");
        Subscription subscription = SpaceStation.INSTANCE.get(key + "/subscription");
        if (subscription != null) {
            subscription.unsubscribe();
            SpaceStation.INSTANCE.remove(key + "/subscription");
        }
    }
}
