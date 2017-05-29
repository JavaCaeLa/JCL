package implementations.test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import implementations.collections.JCLHashMap;
import implementations.dm_kernel.MessageImpl;
import implementations.dm_kernel.MessageListTaskImpl;
import implementations.dm_kernel.MessageMetadataImpl;
import implementations.dm_kernel.MessageSensorImpl;
import implementations.dm_kernel.CPuser.JCL_CPFacadeImpl;
import implementations.dm_kernel.IoTuser.JCL_IoTFacadeImpl;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import implementations.util.JavaToProto;
import interfaces.kernel.JCL_CPfacade;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.JCL_Sensor;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_message_sensor;
import interfaces.kernel.datatype.Device;
import interfaces.kernel.datatype.Sensor;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
//import sun.audio.AudioData;
//import sun.audio.AudioDataStream;
//import sun.audio.AudioPlayer;

public class MainTest {

	public MainTest() {
		// TODO Auto-generated constructor stub
	//	test1();
	//	test2();
	//	teste2();
	//	teste3();
	//	teste4();
	//	teste5();
		teste6();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MainTest();
	}
	
	public void test1(){
		
		MessageMetadataImpl msg1 = new MessageMetadataImpl();
		msg1.setType(-1);
		
		Map<String,String> metadados = new HashMap<String, String>();
		metadados.put("IP", "sfsdfdsfds");
		metadados.put("PORT", "515");
		msg1.setMetadados(metadados);
		
		LinkedBuffer buffer = LinkedBuffer.allocate(1048576);
		Schema<MessageMetadataImpl> sc1 = RuntimeSchema.getSchema(MessageMetadataImpl.class);
		byte[] Out1 = ProtobufIOUtil.toByteArray(msg1,sc1, buffer);
		System.out.println(Arrays.toString(Out1));
		
		for(int i = 0; i<Out1.length;i++){
			System.out.println(Out1[i]);			
			System.out.println((char)Out1[i]);
		}
		
		buffer.clear();
		MessageSensorImpl msg = new MessageSensorImpl();
		int type = 27;
//		byte[] bytes = ByteBuffer.allocate(4).putInt(type).array();
		msg.setType(type);
		msg.setDevice("Andre Luis B Almeid");
	//	msg.setValue(new Integer(10));
		
		Schema<MessageSensorImpl> sc = RuntimeSchema.getSchema(MessageSensorImpl.class);
	//	System.out.println(sc.messageFullName());
	//	System.out.println(sc.getFieldName(4));
		byte[] Out = ProtobufIOUtil.toByteArray(msg,sc, buffer);
		System.out.println(Out.toString());
		
		for(int i = 0; i<Out.length;i++){
			System.out.println("dados:"+i);
			
			System.out.println((char)Out[i]);
//			System.out.println(getByteBinaryString(Out[i]));
		}
//		System.out.println("type:"+Out[1]);
		System.out.println(Arrays.toString(Out));		
	//	System.out.println(Arrays.toString(bytes));	
		
		byte[] OutI = new byte[2];
		OutI[0]= 8;
		OutI[1]= (byte)40;
//		OutI[2]= 3;

//		System.out.println(Arrays.toString(OutI));	
		
		MessageSensorImpl msgR = new MessageSensorImpl();
		ProtobufIOUtil.mergeFrom(OutI, msgR, sc);
		
//		System.out.println("type:"+msgR.getType());
		
	//	String content = Generators.newProtoGenerator().generate();
//		System.out.println(content);

	}
	public void test2(){
		JavaToProto jpt = new JavaToProto(MessageSensorImpl.class);
	    String protoFile = jpt.toString();  
	    System.out.println(protoFile);
	}
	
	
	public String getByteBinaryString(byte b) {
	    StringBuilder sb = new StringBuilder();
	    for (int i = 7; i >= 0; --i) {
	        sb.append(b >>> i & 1);
	    }
	    return sb.toString();
	}
	
	public void teste2(){
		//RegistryListener listener;
		JCLHashMap<Integer, MessageSensorImpl> values = new JCLHashMap<Integer, MessageSensorImpl>("E4:90:7E:3F:61:B2515112_value");
		
		for (int i = 1;i<values.size();i++){
		//	System.out.println(Arrays.toString((float[])values.get(i).getValue()));
//			AudioData audiodata = new AudioData((byte[])values.get(i).getValue());
//			AudioDataStream audioStream = new AudioDataStream(audiodata);
//			// Play the sound
//			AudioPlayer.player.start(audioStream);
		}

	}
	
	public void teste3(){
		
	byte a = 10;
	byte b = -50;
	
	int c = 50;
	int d = 16;
	
	byte cB = (byte) c; 
	byte dB = (byte) d;

	int ai = a;
	int ab = b;
	
	System.out.println("valor a:"+cB+" valor b:"+dB);
	
	
	byte byt = -16;
	System.out.println(byt);
	byte byt2 = (byte)~byt;	
	System.out.println(byt2);
	System.out.println(~byt2);
	
	System.out.println(Integer.toBinaryString(byt));
	
	
	Short mac = null;
	
	if (mac == null)
		System.out.println("nulo 1");
		
	mac = 2;
	if (mac==null)
		System.out.println("nulo 2");
	
	mac = null;
	
	if (mac==null)
		System.out.println("nulo 3");
	
	ByteBuffer msgRet =  ByteBuffer.allocateDirect(2048);
	byte[] bbb= new byte[10];
	bbb[0] = -1;
	bbb[1] = -2;
	bbb[2] = -3;
	bbb[3] = -4;
	bbb[4] = -5;
	bbb[5] = -6;
	bbb[6] = -7;
	bbb[7] = -8;
	bbb[8] = -9;
	bbb[9] = -10;
	 msgRet.put(bbb);
	System.out.println(msgRet.position());
	System.out.println(msgRet.limit());
	msgRet.flip();
	System.out.println(msgRet.position());
	System.out.println(msgRet.limit());
	System.out.println(msgRet.get(9));
	msgRet.get();
	System.out.println(msgRet.position());
	System.out.println(msgRet.limit());
	
	byte[] obj = new byte[(msgRet.limit()-msgRet.position())-1];
	msgRet.get(obj);
	System.out.println(Arrays.toString(obj));
	
	int m = 3;
	
	System.out.println((m == 0.0) ? 0 : m-1);
	
    byte firstNumber = 3;
    byte secondNumber = 20;
    final byte bothNumbers = (byte) ((firstNumber << 6) | secondNumber);

    // Retreive the original numbers
    byte firstNumber2 = (byte) ((bothNumbers >> 6) & (byte) 0x03);
    byte secondNumber2 = (byte) (bothNumbers & 0x3F);
    
    System.out.println(firstNumber2);
    System.out.println(secondNumber2);
    
//    byte lower = (byte) (firstNumber & 0x3F); 
//    byte higher = (byte) (secondNumber >> 6) 0x03;
//    byte fina  = (byte) (lower + (higher << 4));
    
	
	
	}
	
//	public void teste4(){
//		
//		
//		//Criar instancia do jclIoT
//		JCL_IoTfacade jclIoT = JCL_IoTFacadeImpl.getInstance();
//		
//		//Chamar metodo Pacu
//		System.out.println(jclIoT.Pacu.getHosts());		
//
//		//Listar todos os devices (Pcs)
//		List<Entry<String, String>> devidesL = jclIoT.getDevices();
//
//		
//		for(Entry<String, String> d:devidesL){
//			System.out.println("Key: "+d.getKey()+"  Valor: "+d.getValue());
//		}
//		
//		//Lista todos os sensing Devices
//		List<Entry<String, String>> devides = jclIoT.getSensingDevices();
//		
//		
//		for (Entry<String, String> d:devides){
//			System.out.println("Key: "+d.getKey()+"  Valor: "+d.getValue());
//			
//			
////			System.out.println("restart:"+jclIoT.restart(d));
////
////			
////			try {
////				Thread.sleep(10000);
////			} catch (InterruptedException e1) {
////				// TODO Auto-generated catch block
////				e1.printStackTrace();
////			}
////			
//			
//			
//			
//			//Lista todos os sensores de um device
//			List<Entry<String, String>> se = jclIoT.getSensors(d);
//			System.out.println(se);
//			
//			for (Entry<String, String> s:se){
//				
//				//Mostra o ultimo dado do divice d do sensor s 
//			//	if (!d.getValue().equals("SONY D5106")){
//				jclIoT.getlastsensingdata(d, s).getValue().showData();
//			//	}
//				//		System.out.println("Last"+jclIoT.getlastsensingdata(d, s).getValue());
//
//				//Mostra os 10 ultimo dado do divice d do sensor s 
//				Map<Integer,JCL_Sensor> valores = jclIoT.getsensingdata(d,s);
//				for(JCL_Sensor ss:valores.values()){
//					ss.showData();
//				//	System.out.println(ss);
//				}				
//			
//				System.out.println("Get Sensor Now:"+jclIoT.getsensingdatanow(d, s, null));
//			}
//			
//			try {
//			
//				System.out.println("standBy:"+jclIoT.standBy(d));
//				Thread.sleep(5000);
//			System.out.println("turnOn:"+jclIoT.turnOn(d));			
//				Thread.sleep(5000);			
//			 System.out.println("restart:"+jclIoT.restart(d));
//				Thread.sleep(10000);
//				
//				Map<String,String> meta = jclIoT.getMetadata(d);	
//				System.out.println(meta);
//				meta.put("ENABLE_SENSOR","4;8");
//				meta.put("SENSOR_ALIAS_4","Type_ligth");
//				meta.put("SENSOR_SIZE_4","5");
//				meta.put("SENSOR_SAMPLING_4","3");
//				meta.put("SENSOR_ALIAS_8","Type_pro");
//				meta.put("SENSOR_SIZE_8","5");
//				meta.put("SENSOR_SAMPLING_8","3");
//				System.out.println("Novo:"+meta);
//				System.out.println("Enable Meta:"+jclIoT.setMetadata(d, meta));
//				Thread.sleep(10000);
//				System.out.println("Set Sensor1:"+jclIoT.setSensor(d,"Teste",0, 5, 2));
//				System.out.println("Set Sensor2:"+jclIoT.setSensor(d,"Teste",4, 5, 10));
//				System.out.println("Set Sensor3:"+jclIoT.setSensor(d,"Teste",8, 5, 15));
//				System.out.println("Set Sensor4:"+jclIoT.setSensor(d,"Teste",14, 5, 15));
//
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		jclIoT.Pacu.destroy();
//	}
public void teste5(){
	Long ini = System.nanoTime();
	System.nanoTime();
	System.out.println(System.nanoTime() - ini);
}

public void teste10(){
	JCL_CPfacade jcl = JCL_CPFacadeImpl.getInstance();
	System.out.println(jcl.getServerTime());
	System.out.println(jcl.getServerMemory());
	System.out.println(jcl.getServerCpuUsage());
	List<Device> ds = jcl.PacuHPC.getDevices();
	
	for(Device d:ds){
		System.out.println(jcl.getDeviceTime(d));
		System.out.println(jcl.getDeviceMemory(d));
		System.out.println(jcl.getDeviceCpuUsage(d));
	}
	
	jcl.PacuHPC.destroy();
}
public void teste6(){
	Long ini = System.nanoTime();
    JCL_IoTfacade jclIoT = JCL_IoTFacadeImpl.getInstance();
    List<Device> de = jclIoT.getIoTDevices();
    Device ddd = de.get(0);
    System.out.println(ddd);
    List<Sensor> dd = jclIoT.getSensors(ddd);
    for(Sensor s:dd){
    	System.out.println(s);
    	Map<Integer, JCL_Sensor> ds = jclIoT.getAllSensingData(ddd, s);
    	for(Entry<Integer, JCL_Sensor> son:ds.entrySet()){
    		System.out.println("Mais Um");
    		son.getValue().showData();
    	}
    	
    	
    }  
}

public void teste11(){
	Long ini = System.nanoTime();
    JCL_facade jcl = JCL_FacadeImpl.getInstance();
    int var = 10;
    jcl.instantiateGlobalVar("Teste1",var);
    System.out.println(jcl.getValue("Teste1").getCorrectResult());
    jcl.setValueUnlocking("Teste1",(((int)jcl.getValue("Teste1").getCorrectResult())+1));
    System.out.println(jcl.getValue("Teste1").getCorrectResult());
    jcl.destroy();
}

public void teste12(){
	 

		
	    File file = new File("target-file_JCL.wav");
	    if(file.exists()) {
	        try {
	            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);

	            AudioFormat audioFormat = audioInputStream.getFormat();

	            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
	        	for (AudioFormat lineFormat : info.getFormats())System.out.println(lineFormat);

	            SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info);
	            sourceLine.open(audioFormat);

	            sourceLine.start();

	            int nBytesRead = 0;
	            byte[] abData = new byte[128000];
	            while (nBytesRead != -1) {
	                try {
	                    nBytesRead = audioInputStream.read(abData, 0, abData.length);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	                if (nBytesRead >= 0) {
	                    sourceLine.write(abData, 0, nBytesRead);
	                }
	            }

	            sourceLine.drain();
	            sourceLine.close();

	        } catch (UnsupportedAudioFileException | IOException e) {
	            e.printStackTrace();
	        } catch (LineUnavailableException e) {
	            e.printStackTrace();
	        }
	    } else {
	        System.err.println("The selected file doesn't exist!");
	    }
		
}
}
