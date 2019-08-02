package apk.tool.patcher.util;

import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;

public class ReflectionBypass {

    public static boolean apply() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return true;
        }

        try {
            Method forName = Class.class.getDeclaredMethod("forName", String.class);
            Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);

            Class<?> vmRuntimeClass = (Class<?>) forName.invoke(null, "dalvik.system.VMRuntime");
            Method getRuntime = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", null);

            if (getRuntime == null)
                return false;

            Object sVmRuntime = getRuntime.invoke(null);
            Method setHiddenApiExemptions = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});

            if (sVmRuntime == null || setHiddenApiExemptions == null)
                return false;

            setHiddenApiExemptions.invoke(sVmRuntime, (Object) new String[]{"L"});
            return true;
        } catch (Throwable e) {
            Log.e("ReflectionBypass", "Reflection bypass applying failed:", e);
            return false;
        }
    }
}