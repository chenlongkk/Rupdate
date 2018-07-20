package com.cck.debug.utils;

import java.io.*;

public class IOUtils {
    public static void closeQuiet(Closeable closeable) {
        if(closeable == null) {
            return;
        }
        try{
            closeable.close();
        }catch (Exception e) {
            Log.e(e.getMessage());
        }
    }

    public static byte[] toByteArray(File source) {
        byte[] bytes = new byte[0];
        FileInputStream in = null;
        try{
            in = new FileInputStream(source);
            long expectedSize = in.getChannel().size();
            if(expectedSize > Integer.MAX_VALUE) {
                Log.i("expected size is bigger than max integer");
                return bytes;
            }
            bytes = new byte[(int)expectedSize];
            int read = in.read(bytes,0,(int)expectedSize);
            Log.i("readSize:"+read);
        }catch (IOException e) {
            Log.e((e));
        }finally {
            closeQuiet(in);
        }
        return bytes;
    }

    public static void copy(byte[] in,File target) {
        if(target == null) {
            return ;
        }
        target.deleteOnExit();
        FileOutputStream out = null;
        try{
            out = new FileOutputStream(target);
            out.write(in);
        }catch (IOException e) {
            Log.e(e);
        }finally {
            closeQuiet(out);
        }
    }
}
