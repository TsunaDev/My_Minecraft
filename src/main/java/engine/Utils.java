package engine;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Utils {
    public static String loadResource(String file) throws Exception {
        String result;

        try (InputStream in = Utils.class.getResourceAsStream(file); Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }

    static public int[] merge(final int[] ...arrays ) {
        int size = 0;
        for ( int[] a: arrays )
            size += a.length;

        int[] res = new int[size];

        int destPos = 0;
        for ( int i = 0; i < arrays.length; i++ ) {
            if ( i > 0 ) destPos += arrays[i-1].length;
            int length = arrays[i].length;
            System.arraycopy(arrays[i], 0, res, destPos, length);
        }

        return res;
    }

    static public float[] fromPixelsToPercentage(float[] pixelsPos) {
        float[] result = new float[pixelsPos.length];

        for (int i = 0; i < pixelsPos.length; i++) {
            if (i % 2 == 0)
                result[i] = pixelsPos[i] * 16f / 1024f;
            else
                result[i] = pixelsPos[i] * 16f / 512f;
        }
        return result;
    }

}
