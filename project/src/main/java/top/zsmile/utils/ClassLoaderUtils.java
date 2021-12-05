package top.zsmile.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassLoaderUtils {

    public static void loopFiles(ClassLoaderManager.CommonClassloader loader, File file) throws MalformedURLException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File tempFile : files) {
                loopFiles(loader, tempFile);
            }
        } else {
            String absolutePath = file.getAbsolutePath();
            if (absolutePath.endsWith(".jar")) {
                loader.urls.add(new URL("jar:file:/" + absolutePath + "!/"));
            } else if (absolutePath.endsWith(".class")) {
                loader.urls.add(new URL("file:/" + absolutePath));
            }
        }
    }

    static class codes {

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
