package apk.tool.patcher.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {
    public static byte[] readBytes(InputStream is) throws IOException {
        byte[] buf = new byte[10240];
        int num;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((num = is.read(buf)) != -1)
            baos.write(buf, 0, num);
        byte[] b = baos.toByteArray();
        baos.close();
        return b;
    }
}
