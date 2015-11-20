package nl.mvdv.internetradio;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by voorenmi on 17-11-2015.
 */
public class Util {

    private Util() {
    }

    public static void toast(Context ctx, String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context ctx, int resId) {
        Toast.makeText(ctx, resId, Toast.LENGTH_SHORT).show();
    }
}
