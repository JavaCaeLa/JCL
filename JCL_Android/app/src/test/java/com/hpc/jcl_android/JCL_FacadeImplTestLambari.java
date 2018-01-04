package com.hpc.jcl_android;


import android.app.Activity;
import android.content.pm.ApplicationInfo;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import implementations.util.JCL_ApplicationContext;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;
import interfaces.kernel.JCL_task;

import static org.junit.Assert.assertEquals;
public class JCL_FacadeImplTestLambari {
	private JCL_facade test = JCL_FacadeImpl.getInstance();
	private File f = new File("../jcl_useful_jars/userServices.jar"); // getTaskTime
	private File[] arg0 = {f};										  // getTaskTime
	private File[] arg1 = {new File("../jcl_useful_jars/UserType.jar")};
	private Object[] args1 = {new Integer("1"), new Integer("100"), new Integer(10)};
	private Object[] args2 = {new Integer("10"), new Integer("1"), new Integer("14")};

	private Object[][] args3 = {args2};

	
	private List<Future<JCL_result>> jclResultsFuture = new ArrayList<Future<JCL_result>>();
	private List<Future<JCL_result>> jclResultsUnFuture = new ArrayList<Future<JCL_result>>();
	
	private List<JCL_result> jclResults = new ArrayList<JCL_result>();
	private List<JCL_result> jclResultsUn = new ArrayList<JCL_result>();
	
	private List<Entry<String, String>> devices = new ArrayList<Entry<String,String>>();
	private Entry<String,String> singleDevice;
	private Map<String, String> deviceMeta = new HashMap<String,String>();
	
	private Calendar c = Calendar.getInstance();
	private List<Integer> l = new ArrayList<>();
	private List<Integer> ordl = new ArrayList<>();
	private String GlobalVar = new String();
	File [] UserJar = {new File("../jcl_useful_jars/UserType.jar")};
	Integer [] userParams = {1,2};
	private HashMap<Entry<String,String>,Integer> cores = new HashMap<Entry<String,String>,Integer>();
	String GlobalVar1 = new String();
	String GlobalVar2 = new String();
	String GlobalVar3 = new String();
	String GlobalVar4 = new String();	
	
	private Object[] ArrayL = {l};
	private Object[][] args4 = {ArrayL,ArrayL,ArrayL,ArrayL};
	private Object[][] args5 = {ArrayL};
	private JCL_task jclTask;

	@Before
	public void setUp() throws Exception {
		Activity activity = Mockito.mock(Activity.class);
		ApplicationInfo applicationInfo = new ApplicationInfo();
		applicationInfo.sourceDir = "../mocked_dex/userServices.dex";

		Mockito.when(activity.getApplicationInfo()).thenReturn(applicationInfo);
		JCL_ApplicationContext.setContext(activity);

		c.setLenient(false);
		Boolean b = test.register(UserServices.class, "UserServices");
		
		devices = test.getDevices();
		singleDevice = devices.get(0);
		cores =  (HashMap<Entry<String,String>,Integer>) test.getAllDevicesCores();		
		deviceMeta = test.getDeviceMetadata(singleDevice);
		
		l.add(123);
		l.add(321);
		l.add(43);
		l.add(53);
		l.add(13);
		
		ordl.add(13);
		ordl.add(43);
		ordl.add(53);
		ordl.add(123);
		ordl.add(321);
		
		GlobalVar = "GlobalVarTest";
		test.instantiateGlobalVar(GlobalVar, "GlobalVarTest");
		
	}

	@Test
	public void testRegisterClassOfQString() {
		assertEquals(true, test.register(UserServices.class, "Userservices"));
	}
	
	@Test
	public void testRegisterFileArrayString() {
		assertEquals(true, test.register(arg1, "UserType")); 
	}
	
	@Test
	public void testInstantiateGlobalVarOnDeviceEntryOfStringStringStringObjectFileArrayObjectArray() {
		
		boolean b = test.instantiateGlobalVarOnDevice(singleDevice,"VarTeste", "implementations.test.UserType", UserJar, userParams);
		System.out.println(b);
		assertEquals(true,b);
	}

	@Test
	public void testInstantiateGlobalVarOnDeviceEntryOfStringStringObjectObject() {
		assertEquals(true,test.instantiateGlobalVarOnDevice(singleDevice,"GVTESTEONDEVICE", GlobalVar2));
	}

	@Test
	public void testInstantiateGlobalVarAsyObjectStringFileArrayObjectArray() {
		try {
			assertEquals(true, test.instantiateGlobalVarAsy("UserType", "implementations.test.UserType",UserJar, userParams).get());
		} catch (InterruptedException | ExecutionException e){
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
		
		boolean b = test.instantiateGlobalVar(GlobalVar1,"GlobalVar1");
		System.out.println(b);
		assertEquals(true, b);
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
			assertEquals(true, ordl.equals(test.execute("UserServices", "ordena", l).get().getCorrectResult()));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testExecuteStringObjectArray() {
		try {
			assertEquals(true,test.execute("UserServices", args1).get().getCorrectResult().equals(505));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testExecuteAllStringObjectArray() {
		try {
			assertEquals(true,test.executeAll("UserServices", args1).get(0).get().getCorrectResult().equals(505));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testExecuteOnDeviceEntryOfStringStringStringObjectArray() {
		try {
			assertEquals(true,test.executeOnDevice(singleDevice, "UserServices", args1).get().getCorrectResult().equals(505));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testExecuteAllStringStringObjectArray() {
		try {
			assertEquals(true,ordl.equals(test.executeAll("UserServices", "ordena", l).get(0).get().getCorrectResult()));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testExecuteAllStringObjectArrayArray() {
		try {
			assertEquals(true, test.executeAll("UserServices", args3).get(0).get().getCorrectResult().equals(77));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testExecuteAllStringStringObjectArrayArray() {
		try {
			assertEquals(true, ordl.equals(test.executeAll("UserServices","ordena", args5).get(0).get().getCorrectResult()));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
  	@Test
	public void testExecuteOnDeviceEntryOfStringStringStringStringObjectArray() {
		try {
			assertEquals(true,ordl.equals(test.executeOnDevice(singleDevice, "UserServices", "ordena", l).get().getCorrectResult()));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testExecuteAllCoresStringStringObjectArray() {
		try {
		  assertEquals(true, ordl.equals(test.executeAllCores("UserServices","ordena", l).get(0).get().getCorrectResult()));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testExecuteAllCoresStringObjectArray() {
		try {
			assertEquals(true,test.executeAllCores("UserServices", args1).get(0).get().getCorrectResult().equals(505));
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
			assertEquals(true, ordl.equals(test.executeAllCores("UserServices","ordena", args4).get(0).get().getCorrectResult()));
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
//		assertEquals(true, test.removeResult(jclResult1)); //BUG
//	}

	@Test
	public void testGetDeviceConfig() {
		assertEquals(true, test.getDeviceConfig(singleDevice).size()>0); 
	}

	@Test
	public void testSetDeviceConfig() {
		assertEquals(false,test.setDeviceConfig(singleDevice, null));
	}

    @Test
	public void testCleanEnvironment() {
    	boolean b = test.cleanEnvironment();
		assertEquals(true,b);	
	}

	@Test
	public void testUnRegister() {
		assertEquals(true, test.unRegister("UserServices"));
	}
	
//	@Test
//	public void testDestroy() {
//		System.out.println("Void Return");
//	}
}
