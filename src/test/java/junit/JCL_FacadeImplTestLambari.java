package junit;


import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;

import commom.JCL_taskImpl;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;
import interfaces.kernel.JCL_task;

public class JCL_FacadeImplTestLambari {
	private JCL_facade test = JCL_FacadeImpl.getInstance();
	private File f = new File("../jcl_useful_jars/userServices.jar"); // getTaskTime
	private File[] arg0 = {f};										  // getTaskTime
	private File[] arg1 = {new File("../jcl_useful_jars/UserType.jar")};
	private Object[] args1 = {new Integer("1"), new Integer("100"), new Integer(10)};
	private Object[] args2 = {new Integer("10"), new Integer("1"), new Integer("14"), new Integer("100"), new Integer("56"), new Integer("12")};
	private Object[][] args3 = {args2};
	
	private List<Future<JCL_result>> jclResultsFuture = new ArrayList<Future<JCL_result>>();
	private List<Future<JCL_result>> jclResultsUnFuture = new ArrayList<Future<JCL_result>>();
	
	private List<JCL_result> jclResults = new ArrayList<JCL_result>();
	private List<JCL_result> jclResultsUn = new ArrayList<JCL_result>();
	
	private List<Entry<String, String>> devices = new ArrayList<Map.Entry<String,String>>();
	private Entry<String,String> singleDevice;
	private Map<String, String> deviceMeta = new HashMap<String,String>();
	
	private Calendar c = Calendar.getInstance();
	private List<Integer> l = new ArrayList<>();
	private String GlobalVar = new String();
	File [] UserJar = {new File("../jcl_useful_jars/UserType.jar")};
	Integer [] userParams = {1,2};
	private HashMap<Entry<String,String>,Integer> cores = new HashMap<Entry<String,String>,Integer>();
	String GlobalVar1 = new String();
	String GlobalVar2 = new String();
	String GlobalVar3 = new String();
	String GlobalVar4 = new String();
	
	private JCL_result jclRemoveResult;
	private Future<JCL_result> jclResult1;
	private Future<JCL_result> jclResult2;
	private Future<JCL_result> jclResult3;
	private List<Future<JCL_result>> jclResult4;
	private List<Future<JCL_result>> jclResult5;
	private List<Future<JCL_result>> jclResult6;
	private List<Future<JCL_result>> jclResult7;
	private Future<JCL_result> jclResult8;
	private Future<JCL_result> jclResult9;
	private List<Future<JCL_result>> jclResult10;
	private List<Future<JCL_result>> jclResult11;
	private List<Future<JCL_result>> jclResult12;
	private List<Future<JCL_result>> jclResult13;
	
	private JCL_task jclTask;
	
	@Before
	public void setUp() throws Exception {
		c.setLenient(false);
		Boolean b = test.register(UserServices.class, "UserServices");
		System.err.println(b);
		
		devices = test.getDevices();
		singleDevice = devices.get(0);
		cores =  (HashMap<Entry<String,String>,Integer>) test.getAllDevicesCores();		
		deviceMeta = test.getDeviceMetadata(singleDevice);
		
		l.add(123);
		l.add(321);
		l.add(43);
		l.add(53);
		l.add(13);
		jclRemoveResult = test.removeResult(jclResult1);
		jclResult2 = test.execute("UserServices","ordena", l);
		jclResult3 = test.execute("UserServices",args1);
		jclResult4 = test.executeAll("UserServices","ordena", args2);
		jclResult5 = test.executeAll("UserServices", args1);
		jclResult6 = test.executeAll("UserServices", args3);
		jclResult7 = test.executeAll("UserServices","ordena", args1);
		jclResult8 = test.executeOnDevice(singleDevice, "UserServices", args1);
		jclResult9 = test.executeOnDevice(singleDevice, "UserServices", "ordena", l);
		jclResult10 = test.executeAllCores("UserServices","ordena", l);
		jclResult11 = test.executeAllCores("UserServices", args1);
		jclResult12 =test.executeAllCores("UserServices", args3);
		jclResult13 =test.executeAllCores("UserServices","ordena", args3);
		
		GlobalVar = "GlobalVarTest";
		test.instantiateGlobalVar(GlobalVar, "GlobalVarTest");
		
		jclResults = test.getAllResultBlocking(jclResultsFuture);
		
		jclResultsUn = test.getAllResultUnblocking(jclResultsUnFuture);
	}

	/*@Test
	public void testJCL_FacadeImpl() {
		System.out.println("Class constructor");
	}

	@Test
	public void testCreateTicket() {
		System.out.println("Protectd Method");
	}

	@Test
	public void testCoresAutoDetect() {
		System.out.println("protected Method");
	}

	@Test
	public void testUpdateTicket() {
		System.out.println("protected Method");
	}
	
	@Test
	public void testGetInstance() {
		System.out.println("Not yet implemented");
	}*/
	
	/*@Test
	public void testVersion() {
		assertEquals("Lambari", test.version());
	}*/

	@Test
	public void testRegisterClassOfQString() {
		assertEquals(true, test.register(UserServices.class, "Userservices"));
	}
	
	/** retornam false sem explicação **/
	@Test
	public void testRegisterFileArrayString() {
		assertEquals(true, test.register(arg1, "UserType")); //BUG
	}
	
	@Test
	public void testInstantiateGlobalVarOnDeviceEntryOfStringStringStringObjectFileArrayObjectArray() {
		assertEquals(true,test.instantiateGlobalVarOnDevice(singleDevice, "UserType", GlobalVar1, UserJar, userParams));
	}

	@Test
	public void testInstantiateGlobalVarOnDeviceEntryOfStringStringObjectObject() {
		System.out.println("Andre"+test.instantiateGlobalVarOnDevice(singleDevice, GlobalVar2, "GlobalVar2"));
		assertEquals(true,test.instantiateGlobalVarOnDevice(singleDevice, GlobalVar2, "GlobalVar2"));
	}

	@Test
	public void testInstantiateGlobalVarAsyObjectStringFileArrayObjectArray() {
		try {
			assertEquals(true, test.instantiateGlobalVarAsy("UserType", "UserType",UserJar, userParams).get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} 
	}
	//*********************************//*
	
	@Test
	public void testInstantiateGlobalVarAsyObjectObject() {
		try {
			assertEquals(true, test.instantiateGlobalVarAsy("GlobalVar3",GlobalVar3).get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testInstantiateGlobalVarObjectStringFileArrayObjectArray() {
		assertEquals(false, test.instantiateGlobalVar("GlobalVar6", "UserServices", arg0, null));
	}

	@Test
	public void testInstantiateGlobalVarObjectObject() {
		assertEquals(true, test.instantiateGlobalVar(GlobalVar1,"GlobalVar1"));
	}

	@Test
	public void testGetValueLocking() {
		assertEquals(GlobalVar, test.getValueLocking(GlobalVar).getCorrectResult());
	}

	@Test
	public void testGetValue() {
		assertEquals(GlobalVar, test.getValue(GlobalVar).getCorrectResult());
	}
	
	@Test
	public void testSetValueUnlocking() {
		assertEquals(true, test.setValueUnlocking(GlobalVar, "NewValue"));
	}
	
	@Test
	public void testContainsGlobalVar() {
		assertEquals(true, test.containsGlobalVar(GlobalVar));
	}

	@Test
	public void testIsLock() {
		assertEquals(false, test.isLock(GlobalVar));
	}
		
	@Test
	public void testDeleteGlobalVar() {
		assertEquals(true, test.deleteGlobalVar(GlobalVar));
	}

	@Test
	public void testExecuteStringStringObjectArray() {
		try {
			assertEquals(true, jclResult2.get().getCorrectResult().equals(test.execute("UserServices", "ordena", l).get().getCorrectResult()));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testExecuteStringObjectArray() {
		try {
			assertEquals(true, jclResult3.get().equals(test.execute("UserServices", args1).get()));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testExecuteAllStringObjectArray() {
		try {
			assertEquals(true, jclResult5.get(0).get().equals(test.executeAll("UserServices", args1).get(0).get()));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testExecuteOnDeviceEntryOfStringStringStringObjectArray() {
		try {
			assertEquals(true,jclResult8.get().getCorrectResult().equals(test.executeOnDevice(singleDevice, "UserServices", args1).get().getCorrectResult()));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	///* Produz Method invalid:ordena e NullPointer
	@Test
	public void testExecuteAllStringStringObjectArray() { //BUG
		try {
			assertEquals(true,jclResult4.get(0).get().getCorrectResult().equals(test.executeAll("UserServices", "ordena", l).get(0).get().getCorrectResult()));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testExecuteAllStringObjectArrayArray() {
		try {
			assertEquals(true, jclResult6.get(0).get().equals(test.executeAll("UserServices", args3).get(0).get()));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testExecuteAllStringStringObjectArrayArray() {
		try {
			assertEquals(true, jclResult7.get(0).get().getCorrectResult().equals(test.executeAll("UserServices","ordena", args3).get(0).get()..getCorrectResult()));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
  	@Test
	public void testExecuteOnDeviceEntryOfStringStringStringStringObjectArray() {
		try {
			assertEquals(true, jclResult9.get().getCorrectResult().equals(test.executeOnDevice(singleDevice, "UserServices", "ordena", l).get().getCorrectResult()));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testExecuteAllCoresStringStringObjectArray() {
		try {
		  assertEquals(true, jclResult10.get(0).get().getCorrectResult().equals(test.executeAllCores("UserServices","ordena", l).get(0).get().getCorrectResult()));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testExecuteAllCoresStringObjectArray() {
		try {
			assertEquals(true, jclResult11.get(0).get().equals(test.executeAllCores("UserServices", args1).get(0).get()));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	//**
	 
	/*@Test
	public void testExecuteAllCoresStringObjectArrayArray() {
		try {
			assertEquals(jclResult12.get(0@Test
					public void testGetDeviceTime() {
				c.setTimeInMillis(test.getDeviceTime());
				try {
					c.getTime();
				} catch (Exception e) {
					System.err.println("Couldn't create a valid Date from Device Time");
				}
			}

			 @Test
			public void testGetSuperPeerTime() {
				c.setTimeInMillis(test.getTgetSuperPeerTime());
				try {
					c.getTime();
				} catch (Exception e) {
					System.err.println("Couldn't create a valid Date from SuperPeer Time");
				}
			}

			@Test 
			public void testGetServerTime() {
				c.setTimeInMillis(test.getServerTime());
				try {
					c.getTime();;
				} catch (Exception e) {
					System.err.println("Couldn't create a valid Date from Server Time");
				}
			}).get(), test.executeAllCores("UserServices", args3).get(0).get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}*/
	
	@Test
	public void testExecuteAllCoresStringStringObjectArrayArray() {
		try {
			assertEquals(true, jclResult13.get(0).get().getCorrectResult().equals(test.executeAllCores("UserServices","ordena", args3).get(0).get().getCorrectResult()));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testContainsTask() {
		assertEquals(false, test.containsTask("taskinexistente"));
	}

	@Test
	public void testGetAllResultBlocking() {
		assertEquals(jclResults, test.getAllResultBlocking(jclResultsFuture));
	}

	@Test
	public void testGetAllResultUnblocking() {
		assertEquals(jclResultsUn, test.getAllResultUnblocking(jclResultsUnFuture));
	}
	
	/*@Test
	public void testGetDeviceTime() {
		c.setTimeInMillis(test.getDeviceTime());
		try {
			c.getTime();
		} catch (Exception e) {
			System.err.println("Couldn't create a valid Date from Device Time");
		}
	}

	 @Test
	public void testGetSuperPeerTime() {
		c.setTimeInMillis(test.getTgetSuperPeerTime());
		try {
			c.getTime();
		} catch (Exception e) {
			System.err.println("Couldn't create a valid Date from SuperPeer Time");
		}
	}

	@Test 
	public void testGetServerTime() {
		c.setTimeInMillis(test.getServerTime());
		try {
			c.getTime();;
		} catch (Exception e) {
			System.err.println("Couldn't create a valid Date from Server Time");
		}
	}*/
	
	@Test
	public void testGetDevices() {
		assertEquals(devices,test.getDevices());
	}
	
	@Test
	public void testGetDeviceCore() {
		assertEquals(true, test.getDeviceCore(singleDevice) != 0);
	}

	@Test
	public void testGetAllDevicesCores() {
		assertEquals(true, test.getAllDevicesCores() != null);
	}

	@Test
	public void testGetClusterCores() {
		assertEquals(true, test.getClusterCores() != 0);
	}

//	@Test
//	public void testRemoveResult() {
//		assertEquals(true, jclRemoveResult.equals(test.removeResult(jclResult1))); //BUG
//	}

	@Test
	public void testGetDeviceConfig() {
		assertEquals(deviceMeta, test.getDeviceConfig(singleDevice)); 
	}

	@Test
	public void testSetDeviceConfig() {
		assertEquals(false,test.setDeviceConfig(singleDevice, null));
	}

//    @Test
//	public void testCleanEnvironment() {
//		assertEquals(true, test.cleanEnvironment());	
//	}

//	@Test
//	public void testUnRegister() {
//		assertEquals(true, test.unRegister("UserServices"));
//	}
	
//	@Test
//	public void testDestroy() {
//		System.out.println("Void Return");
//	}
}
