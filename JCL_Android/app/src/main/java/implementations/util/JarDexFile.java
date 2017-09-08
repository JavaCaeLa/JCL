package implementations.util;

import android.os.Environment;
import android.util.Log;

//import com.hpc.jcl_android.SuperContext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import dalvik.system.DexClassLoader;
import interfaces.kernel.JCL_message_register;
import javassist.android.DexFile;

/**
 * Created by estevao on 31/10/16.
 */
public class JarDexFile {
    private static final String rootPath = Environment.getExternalStorageDirectory().toString() + File.separatorChar + "jclAndroid";


    private static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    public void loadingClassOnAndroid(JCL_message_register msgR){
        loadingClassOnAndroid(msgR,msgR.getClassName(),false);
        //io.protostuff.runtime.RuntimeEnv r = null;

    }

    public Class loadingClassOnAndroid(JCL_message_register msgR, String className1){
        return loadingClassOnAndroid(msgR,className1,true);
    }

    private Class loadingClassOnAndroid(JCL_message_register msgR, String className1, boolean returnClassName) {
        String DEX_FILE_NAME_MYCLASSES="";
        if (returnClassName)
            DEX_FILE_NAME_MYCLASSES = "classesJ.dex";
        else
            DEX_FILE_NAME_MYCLASSES = className1+".dex";

        try {

            boolean foundClass=false;

            final File dexFile = new File(JCL_ApplicationContext.getContext().getFilesDir(), DEX_FILE_NAME_MYCLASSES);

            final DexFile df = new DexFile();
            final String dexFilePath = dexFile.getAbsolutePath();

            int size = msgR.getJars().length;

            File fout0 = new File(rootPath + "/user_jars");
            if (!fout0.exists())
                fout0.mkdirs();


            for (int i = 0; i < size; i++) {


                String name = msgR.getJarsNames()[i];
                FileOutputStream fout = new FileOutputStream(rootPath + "/user_jars/" + name);
                fout.write(msgR.getJars()[i]);
                fout.flush();
                fout.close();

                JarFile jar = new JarFile(rootPath + "/user_jars/" + name);


                for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
                    JarEntry entry = entries.nextElement();
                    String file = entry.getName();


                    if (file.endsWith(".class")) {

                        if (returnClassName && !foundClass &&  file.endsWith(className1 + ".class")) {
                            className1 = file.replace('.', '/').substring(0, file.length() - 6);
                            foundClass = true;
                        }

                        //String classname = file.replace("/",".").substring(0, file.length() - 6);

                        InputStream inputStream = jar.getInputStream(entry);
                        byte[] b = getBytes(inputStream);


                        String name1 = null;
                        int lasIndex = file.lastIndexOf("/")+1;
                        if (lasIndex < file.length() && lasIndex > 0)
                            name1 = file.substring(lasIndex);
                        else
                            name1 = file;

                        Log.e("Name class", name1);


                        df.addClass(name1, b);
                    }
                }
            }
            df.writeFile(dexFilePath);
            //df.writeFile(android.os.Environment.getExternalStorageState()+"/dex2/mail.dex");



            final DexClassLoader dcl = new DexClassLoader(
                    dexFile.getAbsolutePath(),
                    JCL_ApplicationContext.getContext().getCacheDir().getAbsolutePath(),
                    JCL_ApplicationContext.getContext().getApplicationInfo().nativeLibraryDir,
                    JCL_ApplicationContext.getContext().getClassLoader());
            if (returnClassName) {
                Class cla = dcl.loadClass(className1.replace(".", "/"));
                return cla;
            }else
                return null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}

