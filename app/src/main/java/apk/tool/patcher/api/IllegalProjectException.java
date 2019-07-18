package apk.tool.patcher.api;


import android.util.Log;

/**
 * Класс кастомного исключения, используется в элементах API
 */
class IllegalProjectException extends IllegalArgumentException {
    IllegalProjectException(Class c, String s) {
        super(s);
        Log.wtf(c.getSimpleName(), String.format("Class %s trowed an exception %s", c.getSimpleName(), s));
    }
}
