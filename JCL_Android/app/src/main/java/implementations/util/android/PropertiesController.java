package implementations.util.android;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import implementations.util.JCL_ApplicationContext;

/**
 * Created by estevaoresende on 25/08/17.
 */

public final class PropertiesController {

    public static final void createPropertiesWithNoExists(){
        String rootPath = Environment.getExternalStorageDirectory().toString();
        File file = new File(rootPath + "/jcl_conf/config.properties");
        if (!file.exists()) {
            writeHPCProperties("127.0.0.1", 6969+"");
        }
    }

    public static final void writeHPCProperties(String serverIp, String serverPort) {
        try {


            String rootPath = Environment.getExternalStorageDirectory().toString();

            Properties properties = new Properties();
            File file = new File(rootPath + "/jcl_conf/config.properties");
            if (file.exists()) {
                properties.load(new FileInputStream(rootPath + "/jcl_conf/config.properties"));
            }//caso não exita, cria um com os campos padrão
            else {
                File mediaStorageDir = new File(rootPath + "/jcl_conf/");
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        return;
                    }
                }
                String pro = "###################################################\n" +
                        "#               JCL config file                   #\n" +
                        "###################################################\n" +
                        "# Config JCL type\n" +
                        "# true => Pacu version\n" +
                        "# false => Lambari version\n" +
                        "distOrParell = true\n" +
                        "serverMainPort = 6969\n" +
                        "superPeerMainPort = 6868\n" +
                        "routerMainPort = 7070\n" +
                        "serverMainAdd = localhost\n" +
                        "hostPort = 5151\n" +
                        "nic = \n" +
                        "simpleServerPort = 4949\n" +
                        "timeOut = 5000\n" +
                        "byteBuffer = 10000000\n" +
                        "routerLink = 5\n" +
                        "enablePBA = false\n" +
                        "PBAsize=50\n" +
                        "delta=0\n" +
                        "PGTerm = 10\n" +
                        "twoStep = false\n" +
                        "useCore=100\n" +
                        "deviceID = Host1\n" +
                        "enableDinamicUp = false\n" +
                        "findServerTimeOut = 1000\n" +
                        "findHostTimeOut = 1000\n" +
                        "enableFaultTolerance = false\n" +
                        "verbose = true\n"+
                        "encryption = false\n"+
                        "deviceType = 3\n";
                PrintWriter writer = new PrintWriter(rootPath + "/jcl_conf/config.properties", "UTF-8");
                writer.print(pro);
                writer.close();
                properties.load(new FileInputStream(rootPath + "/jcl_conf/config.properties"));

            }

            //seta as configurações setadas


            properties.setProperty("serverMainPort", serverPort.trim());
            properties.setProperty("serverMainAdd", serverIp.trim());

            FileOutputStream fileOut = new FileOutputStream(file);
            properties.store(fileOut, "");
            fileOut.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
