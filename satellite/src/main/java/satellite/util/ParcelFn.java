package satellite.util;

import android.os.Parcel;

public class ParcelFn {

    private static final ClassLoader CLASS_LOADER = ParcelFn.class.getClassLoader();

    public static <T> T clone(Object o) {
        return unmarshall(marshall(o));
    }

    public static <T> T unmarshall(byte[] array) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(array, 0, array.length);
        parcel.setDataPosition(0);
        Object value = parcel.readValue(CLASS_LOADER);
        parcel.recycle();
        return (T)value;
    }

    public static byte[] marshall(Object o) {
        Parcel parcel = Parcel.obtain();
        parcel.setDataPosition(0);
        parcel.writeValue(o);
        byte[] result = parcel.marshall();
        parcel.recycle();
        return result;
    }
}
