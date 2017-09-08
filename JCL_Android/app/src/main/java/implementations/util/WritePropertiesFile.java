package implementations.util;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

/**
 * Created by estevao on 22/11/16.
 */
public class WritePropertiesFile {

    public void write(Map<String, String> values, Vector<String> sortingKeys){
        try {
            String rootPath = Environment.getExternalStorageDirectory().toString();

            Properties properties = new Properties();
            File file = new File(rootPath+"/jcl_conf/config.properties");

            if (file.exists())
                properties.load(new FileInputStream(rootPath+"/jcl_conf/config.properties"));

            for (String key: sortingKeys)
                properties.setProperty(key, values.get(key));
            FileOutputStream fileOut = new FileOutputStream(file);

            if (!file.exists())
                properties.store(fileOut,
                    "###################################################\n" +
                    "#               JCL config file                   #\n" +
                    "###################################################\n" +
                    "# Config JCL type\n" +
                    "# true => Pacu version\n" +
                    "# false => Lambari version\n");
            else
                properties.store(fileOut,"");
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
