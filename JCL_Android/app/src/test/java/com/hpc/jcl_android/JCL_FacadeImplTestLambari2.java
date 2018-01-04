package com.hpc.jcl_android;

import android.app.Activity;
import android.content.pm.ApplicationInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import implementations.util.JCL_ApplicationContext;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;
import interfaces.kernel.JCL_task;

public class JCL_FacadeImplTestLambari2 {
	
	private JCL_facade test = JCL_FacadeImpl.getInstance();
	
	private File f = new File("../jcl_useful_jars/userServices.jar"); // getTaskTime
	private File[] arg00 = null;
	private File[] arg0 = {f};										  // getTaskTime
	private File[] arg1 = {new File("../jcl_useful_jars/UserType.jar")};
	private Object[] args1 = {new Integer("1"), new Integer("100"), new Integer(10)};
	private Object[] args2 = {new Integer("10"), new Integer("1"), new Integer("14"), new Integer("100"), new Integer("56"), new Integer("12")};
	private Object[][] args3 = {args2};
	
	private List<Future<JCL_result>> jclResultsFuture = new ArrayList<Future<JCL_result>>();
	private List<Future<JCL_result>> jclResultsUnFuture = new ArrayList<Future<JCL_result>>();
	
	private List<JCL_result> jclResults = new ArrayList<JCL_result>();
	private List<JCL_result> jclResultsUn = new ArrayList<JCL_result>();
	
	private List<Entry<String, String>> devices = new ArrayList<Entry<String,String>>();
	private Entry<String,String> singleDevice;
	private Map<String, String> deviceMeta = new HashMap<String,String>();;
	
	private Calendar c = Calendar.getInstance();
	private List<Integer> l = new ArrayList<>();
	
	private String GlobalVar = new String();
	private String GlobalVar1 = new String();
	
	File [] UserJar = {new File("../jcl_useful_jars/UserType.jar")};
	Integer [] userParams = {1,2};
	private HashMap<Entry<String,String>,Integer> cores = new HashMap<Entry<String,String>,Integer>();
	
	String GlobalVar2 = new String();
	String GlobalVar3 = new String();
	String GlobalVar4 = new String();
	
	private JCL_result jclRemoveResult;
	private Future<JCL_result> jclResult1;
	
	private JCL_task jclTask;

	@Before
	public void setUp() throws Exception {
		c.setLenient(false);

		Activity activity = Mockito.mock(Activity.class);
		ApplicationInfo applicationInfo = new ApplicationInfo();
		applicationInfo.sourceDir = "../mocked_dex/userServices.dex";

		Mockito.when(activity.getApplicationInfo()).thenReturn(applicationInfo);
		JCL_ApplicationContext.setContext(activity);

		Boolean b = test.register(UserServices.class, "UserServices");
		System.err.println(b);
		
		devices = test.getDevices();
		singleDevice = devices.get(0);
		cores =  (HashMap<Entry<String,String>,Integer>) test.getAllDevicesCores();		
		//deviceMeta = test.getDeviceMetadata(singleDevice);
		
		l.add(123);
		l.add(321);
		l.add(43);
		l.add(53);
		l.add(13);
		
		jclTask = null; // new JCL_taskImpl(null, "UserServices", args1);
		
		jclResult1 = null; //test.execute(jclTask);
		
		GlobalVar = "GlobalVarTest";
		test.instantiateGlobalVar(GlobalVar, "GlobalVarTest");
		
		jclResults = test.getAllResultBlocking(jclResultsFuture);
		
		jclResultsUn = test.getAllResultUnblocking(jclResultsUnFuture);
	}

	@Test
	public void testJCL_FacadeImpl() {
		System.out.println("Class constructor");
	}

	@Test
	public void testCreateTicket() {
		System.out.println("Protectd Method");
	}

	@Test
	public void testCoresAutoDetect() {
		System.out.println("Protectd Method");
	}

	@Test
	public void testUpdateTicket() {
		System.out.println("Protectd Method");
	}

	@Test
	public void testDeleteGlobalVar() {
		try{
			test.deleteGlobalVar(GlobalVar1);
			System.out.println("DeleteGlobalVar - OK");
		}
		catch(Exception e){
			System.out.println("DeleteGlobalVar - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testGetValue() {
		try{
			test.getValue(GlobalVar1).getCorrectResult();
			System.out.println("GetValue - OK");
		}
		catch(Exception e){
			System.out.println("GetValue - Error");
			//System.out.println("Exception: " + e);
		}
	}

//	@Test
//	public void testRegisterFileArrayString() {
//		try{
//			test.register(arg00, "UserType");
//			System.out.println("RegisterFileArrayString - OK");
//		}
//		catch(Exception e){
//			System.out.println("RegisterFileArrayString - Error");
//			//System.out.println("Exception: " + e);
//		}
//	}

	@Test
	public void testGetServerTime() {
		System.out.println("No parameters");
	}

	@Test
	public void testRegisterClassOfQString() { // Como testar erro no parametro?
		try{
			test.register(UserServices.class, "Userservices");
			System.out.println("RegisterClassOfQString - OK");
		}
		catch(Exception e){
			System.out.println("RegisterClassOfQString - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testSetValueUnlocking() {
		try{
			test.setValueUnlocking(GlobalVar1, "NewValue");
			System.out.println("SetValueUnlocking - OK");
		}
		catch(Exception e){
			System.out.println("SetValueUnlocking - Error");
			//System.out.println("Exception: " + e);
		}
	}

//	@Test
//	public void testGetValueLocking() {
//		try{
//			test.getValueLocking(GlobalVar1).getCorrectResult();
//			System.out.println("GetValueLocking - OK");
//		}
//		catch(Exception e){
//			System.out.println("GetValueLocking - Error");
//			//System.out.println("Exception: " + e);
//		}
//	}

	@Test
	public void testDestroy() {
		System.out.println("Void Return");
	}

	@Test
	public void testUnRegister() {
		try{
			test.unRegister("UserServices");
			System.out.println("UnRegister - OK");
		}
		catch(Exception e){
			System.out.println("UnRegister - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testInstantiateGlobalVarObjectStringFileArrayObjectArray() {
		try{
			test.instantiateGlobalVar("GlobalVar6", "UserServices", arg0, null);
			System.out.println("InstantiateGlobalVarObjectStringFileArrayObjectArray - OK");
		}
		catch(Exception e){
			System.out.println("InstantiateGlobalVarObjectStringFileArrayObjectArray - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testInstantiateGlobalVarObjectObject() { // Como testar erro no parametro?
		try{
			test.instantiateGlobalVar(GlobalVar1,"GlobalVar1");
			System.out.println("InstantiateGlobalVarObjectObject - OK");
		}
		catch(Exception e){
			System.out.println("InstantiateGlobalVarObjectObject - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testContainsTask() {
		try{
			if(test.containsTask("taskinexistente")) throw new Exception();
			System.out.println("ContainsTask - OK");
		}
		catch(Exception e){
			System.out.println("ContainsTask - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testContainsGlobalVar() {
		try{
			test.containsGlobalVar(0);
			System.out.println("ContainsGlobalVar - OK");
		}
		catch(Exception e){
			System.out.println("ContainsGlobalVar - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testIsLock() {
		try{
			test.isLock(GlobalVar1);
			System.out.println("IsLock - OK");
		}
		catch(Exception e){
			System.out.println("IsLock - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testCleanEnvironment() {
		System.out.println("No parameters");
	}

	@Test
	public void testGetAllResultBlocking() {
		try{
			test.getAllResultBlocking(jclResultsFuture);
			System.out.println("GetAllResultBlocking - OK");
		}
		catch(Exception e){
			System.out.println("GetAllResultBlocking - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testGetAllResultUnblocking() {
		try{
			test.getAllResultBlocking(jclResultsUnFuture);
			System.out.println("GetAllResultUnblocking - OK");
		}
		catch(Exception e){
			System.out.println("GetAllResultUnblocking - Error");
			//System.out.println("Exception: " + e);
		}
	}
	
//	@Test
//	public void testExecuteStringStringObjectArray() {
//		try{
//			test.execute("UserServices", "ordenar", l);
//			System.out.println("executeStringStringObjectArray - OK");
//		}
//		catch(Exception e){
//			System.out.println("executeStringStringObjectArray - Error");
//			//System.out.println("Exception: " + e);
//		}
//	}

//	@Test
//	public void testExecuteJCL_task() {
//		try{
//			test.execute(jclTask);
//			System.out.println("executeJCL_task - OK");
//		}
//		catch(Exception e){
//			System.out.println("executeJCL_task - Error");
//			//System.out.println("Exception: " + e);
//		}
//	}

	@Test
	public void testExecuteStringObjectArray() {
		try{
			test.execute("UserServices", args1);
			System.out.println("executeStringObjectArray - OK");
		}
		catch(Exception e){
			System.out.println("executeStringObjectArray - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testExecuteAllStringObjectArray() {
		try{
			test.executeAll("UserServices", args1);
			System.out.println("ExecuteAllStringObjectArray - OK");
		}
		catch(Exception e){
			System.out.println("ExecuteAllStringObjectArray - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testExecuteOnDeviceEntryOfStringStringStringObjectArray() {
		try{
			test.executeOnDevice(singleDevice, "UserServices", args1);
			System.out.println("ExecuteOnDeviceEntryOfStringStringStringObjectArray - OK");
		}
		catch(Exception e){
			System.out.println("ExecuteOnDeviceEntryOfStringStringStringObjectArray - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testExecuteAllStringStringObjectArray() {
		try{
			test.executeAll("UserServices", "ordena", l);
			System.out.println("ExecuteAllStringStringObjectArray - OK");
		}
		catch(Exception e){
			System.out.println("ExecuteAllStringStringObjectArray - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testExecuteOnDeviceEntryOfStringStringStringStringObjectArray() {
		try{
			test.executeOnDevice(singleDevice, "UserServices", "ordenar", l);
			System.out.println("ExecuteOnDeviceEntryOfStringStringStringStringObjectArray - OK");
		}
		catch(Exception e){
			System.out.println("ExecuteOnDeviceEntryOfStringStringStringStringObjectArray - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testExecuteAllStringObjectArrayArray() {
		try{ 
			test.executeAll("UserService", args3);
			System.out.println("ExecuteAllStringObjectArrayArray - OK");
		}
		catch(Exception e){
			System.out.println("ExecuteAllStringObjectArrayArray - Error");
			//System.out.println("Exception: " + e);
		}
	}
	
	@Test
	public void testExecuteAllCoresStringStringObjectArray() {
		try{ 
			test.executeAllCores("UserServices", "ordenar", l);
			System.out.println("ExecuteAllCoresStringStringObjectArray - OK");
		}
		catch(Exception e){
			System.out.println("ExecuteAllCoresStringStringObjectArray - Error");
			//System.out.println("Exception: " + e);
		}
	}
	
	@Test
	public void testExecuteAllCoresStringObjectArray() {
		try{ 
			test.executeAllCores("UserServices", args1);
			System.out.println("ExecuteAllCoresStringObjectArray - OK");
		}
		catch(Exception e){
			System.out.println("ExecuteAllCoresStringObjectArray - Error");
			//System.out.println("Exception: " + e);
		}
	}
	
//	@Test
//	public void testExecuteAllCoresStringObjectArrayArray() {
//		try{ 
//			test.executeAllCores("UserService", args3);
//			System.out.println("ExecuteAllCoresStringObjectArrayArray - OK");
//		}
//		catch(Exception e){
//			System.out.println("ExecuteAllCoresStringObjectArrayArray - Error");
//			//System.out.println("Exception: " + e);
//		}
//	}
	
//	@Test
//	public void testExecuteAllCoresStringStringObjectArrayArray() {
//		try{ 
//			test.executeAllCores("UserService", "ordenar", args3);
//			System.out.println("ExecuteAllCoresStringStringObjectArrayArray - OK");
//		}
//		catch(Exception e){
//			System.out.println("ExecuteAllCoresStringStringObjectArrayArray - Error");
//			//System.out.println("Exception: " + e);
//		}
//	}
	
	@Test
	public void testExecuteAllStringStringObjectArrayArray() {
		try{ 
			test.executeAll("UserServices", "ordenar", args3);
			System.out.println("ExecuteAllStringStringObjectArrayArray - OK");
		}
		catch(Exception e){
			System.out.println("ExecuteAllStringStringObjectArrayArray - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testGetDevices() {
		System.out.println("No parameters");
	}

	@Test
	public void testInstantiateGlobalVarOnDeviceEntryOfStringStringStringObjectFileArrayObjectArray() {
		try{
			test.instantiateGlobalVarOnDevice(singleDevice, "UserTypes", GlobalVar1, UserJar, userParams);
			System.out.println("InstantiateGlobalVarOnDeviceEntryOfStringStringStringObjectFileArrayObjectArray - OK");
		}
		catch(Exception e){
			System.out.println("InstantiateGlobalVarOnDeviceEntryOfStringStringStringObjectFileArrayObjectArray - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testInstantiateGlobalVarOnDeviceEntryOfStringStringObjectObject() {
		try{
			test.instantiateGlobalVarOnDevice(singleDevice, GlobalVar2, "GlobalVar2");
			System.out.println("InstantiateGlobalVarOnDeviceEntryOfStringStringObjectObject - OK");
		}
		catch(Exception e){
			System.out.println("InstantiateGlobalVarOnDeviceEntryOfStringStringObjectObject - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testVersion() {
		System.out.println("No parameters");
	}

	@Test
	public void testInstantiateGlobalVarAsyObjectObject() { // Como testar erro?
		try{
			test.instantiateGlobalVarAsy("GlobalVar3", GlobalVar3).get(); // ordem errada?
			System.out.println("InstantiateGlobalVarAsyObjectObject - OK");
		}
		catch(Exception e){
			System.out.println("InstantiateGlobalVarAsyObjectObject - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testInstantiateGlobalVarAsyObjectStringFileArrayObjectArray() {
		try{ 
			test.instantiateGlobalVarAsy("UserTypes", "UserTypes", UserJar, userParams).get();
			System.out.println("InstantiateGlobalVarAsyObjectStringFileArrayObjectArray - OK");
		}
		catch(Exception e){
			System.out.println("InstantiateGlobalVarAsyObjectStringFileArrayObjectArray - Error");
			//System.out.println("Exception: " + e);
		}
	}

//	@Test
//	public void testInstantiateGlobalVarObjectObjectStringBoolean() {
//		try{ 
//			test.instantiateGlobalVar("UserType1", "UserType1", "UserTypes", true);
//			System.out.println("InstantiateGlobalVarObjectObjectStringBoolean - OK");
//		}
//		catch(Exception e){
//			System.out.println("InstantiateGlobalVarObjectObjectStringBoolean - Error");
//			//System.out.println("Exception: " + e);
//		}
//	}

//	@Test
//	public void testInstantiateGlobalVarAsyObjectObjectStringBoolean() { // Como testar erro?
//		try{ 
//			test.instantiateGlobalVarAsy("GlobalVar4", GlobalVar4, "String", true).get(); // ordem errada?
//			System.out.println("InstantiateGlobalVarAsyObjectObjectStringBoolean - OK");
//		}
//		catch(Exception e){
//			System.out.println("InstantiateGlobalVarAsyObjectObjectStringBoolean - Error");
//			//System.out.println("Exception: " + e);
//		}
//	}

	@Test
	public void testGetInstance() {
		System.out.println("Not yet implemented");
	}

	@Test
	public void testGetDeviceCore() {
		try{ 
			test.getDeviceCore(singleDevice);
			System.out.println("GetDeviceCore - OK");
		}
		catch(Exception e){
			System.out.println("GetDeviceCore - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testGetAllDevicesCores() {
		System.out.println("No parameters");
	}

	@Test
	public void testGetClusterCores() {
		System.out.println("No parameters");
	}

	@Test
	public void testRemoveResult() {
		try{ 
			test.removeResult(jclResult1);
			System.out.println("RemoveResult - OK");
		}
		catch(Exception e){
			System.out.println("RemoveResult - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testGetDeviceTime() {
		System.out.println("No parameters");
	}

	@Test
	public void testGetSuperPeerTime() {
		System.out.println("No parameters");
	}

	@Test
	public void testGetDeviceMetadata() {
		try{ 
			test.getDeviceMetadata(singleDevice);
			System.out.println("GetDeviceMetadata - OK");
		}
		catch(Exception e){
			System.out.println("GetDeviceMetadata - Error");
			//System.out.println("Exception: " + e);
		}
	}

	@Test
	public void testSetDeviceMetadata() {
		try{ 
			test.setDeviceMetadata(singleDevice, null);
			System.out.println("SetDeviceMetadata - OK");
		}
		catch(Exception e){
			System.out.println("SetDeviceMetadata - Error");
			//System.out.println("Exception: " + e);
		}
	}
	

}
