package top.zsmile.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassLoaderUtils {
    static class decodeClass {

        public static byte[] checkDecode(String decodeType, JarFile jarFile, JarEntry jarEntry) {
            byte[] decodeData = null;
            InputStream inputStream = null;
            try {
                inputStream = jarFile.getInputStream(jarEntry);
                int available = inputStream.available();
                byte[] bytes = new byte[available];
                inputStream.read(bytes);
                decodeData = checkDecode(decodeType, bytes);
                return decodeData;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                        inputStream = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

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
