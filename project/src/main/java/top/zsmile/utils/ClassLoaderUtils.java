package top.zsmile.utils;

import java.util.HashMap;
import java.util.Map;

public class ClassLoaderUtils {
    static class decodeClass {

        public static byte[] checkDecode(String decodeType, byte[] code) {
            if (decodeType == null) {
                return code;
            }
            byte[] decodeData = null;
            switch (decodeType) {
                case "base64":
                    decodeData = base64Decode(code);
                    break;
            }
            return decodeData;
        }

        private static byte[] base64Decode(byte[] code) {
            byte[] decodeBytes = new byte[code.length];
            for (int i = 0; i < code.length; i++) {
                decodeBytes[i] = (byte) (255 - code[i]);
            }
            return decodeBytes;
        }
    }
}
