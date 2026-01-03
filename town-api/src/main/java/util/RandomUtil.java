package util;

import java.util.Random;

public class RandomUtil {
    private static Random random = new Random();

    public static String RandomCode(int len)
    {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++){
            sb.append(random.nextInt(6));
        }
        return sb.toString();
    }
}
