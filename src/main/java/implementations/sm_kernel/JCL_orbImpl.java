package implementations.sm_kernel;

import interfaces.kernel.JCL_execute;
import interfaces.kernel.JCL_orb;
import interfaces.kernel.JCL_result;
import interfaces.kernel.JCL_task;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import com.google.common.primitives.Primitives;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.CtPrimitiveType;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
import commom.JCL_resultImpl;

public class JCL_orbImpl<T extends JCL_result> implements JCL_orb<T> {

	private Map<String, Class<?>> nameMap;
	private Map<Object, Object> globalVars;
	private static AtomicInteger RegisterMsg;
	private Map<String, JCL_execute> cache1;
	private AtomicLong idClass = new AtomicLong(0);
	private Map<String, Integer> cache2;
	private Set<Object> locks;
	private long timeOut = 3000L;
	private static JCL_orb instance;
	private static JCL_orb instancePacu;
	private Map<Long, T> results;

	private URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

	private JCL_orbImpl() {

		nameMap = new ConcurrentHashMap<String, Class<?>>();
		locks = new ConcurrentSkipListSet<Object>();
		globalVars = new ConcurrentHashMap<Object, Object>();
		cache1 = new ConcurrentHashMap<String, JCL_execute>();
		cache2 = new ConcurrentHashMap<String, Integer>();
		
	}

	@Override
	public void execute(JCL_task task) {
		try {
			int para;
			
			if (nameMap.containsKey(task.getObjectName())){

				T jResult = results.get(task.getTaskID());
				JCL_execute instance = cache1.get(task.getObjectName());
				if (task.getMethodParameters() == null)
					para = 0;
				else
					para = task.getMethodParameters().length;

				int type = cache2.get(task.getObjectName() + ":" + task.getObjectMethod() + ":" + para);
				task.setTaskTime(System.nanoTime());
				Object result = instance.JCLExecPacu(type, task.getMethodParameters());
				task.setTaskTime(System.nanoTime());

				jResult.setTime(task.getTaskTime());
				jResult.setMemorysize(ObjectSizeCalculator.getObjectSize(instance));

				if (result != null) {
					jResult.setCorrectResult(result);
				} else {
					jResult.setCorrectResult("no result");
				}

				synchronized (jResult) {
					jResult.notifyAll();
				}

			} else {
//				if (RegisterMsg.get() > 0){
//				System.out.println("***********************************");
//				System.out.println("*  Registering class in process   *");
//				System.out.println("* Wait to receive all class data  *");
//				System.out.println("* Work id: "+Thread.currentThread().getId()+"                     *");									
//				System.out.println("***********************************");									
//				}else{
//					System.out.println("***************************************");
//					System.out.println("*           Class not found           *");
//					System.out.println("* Wait timeout or register class msg  *");
//					System.out.println("* Work id: "+Thread.currentThread().getId()+"                         *");									
//					System.out.println("***************************************");									
//				}
//				System.out.println("Finding Class in process.");
				Long ini = System.currentTimeMillis();
				boolean ok = true;

				while ((System.currentTimeMillis() - ini) < timeOut) {
					
					if (RegisterMsg.get() > 0){
						ini = System.currentTimeMillis();
					}
										
					if (nameMap.containsKey(task.getObjectName())) {

						T jResult = results.get(task.getTaskID());
						JCL_execute instance = (JCL_execute) cache1.get(task.getObjectName());
						if (task.getMethodParameters() == null)
							para = 0;
						else
							para = task.getMethodParameters().length;
						int type = cache2.get(task.getObjectName() + ":" + task.getObjectMethod() + ":" + para);

						task.setTaskTime(System.nanoTime());
						Object result = instance.JCLExecPacu(type, task.getMethodParameters());
						task.setTaskTime(System.nanoTime());

						jResult.setTime(task.getTaskTime());
						jResult.setMemorysize(ObjectSizeCalculator.getObjectSize(instance));


						if (result != null) {
							jResult.setCorrectResult(result);
						} else {
							jResult.setCorrectResult("no result");
						}

						synchronized (jResult) {
							jResult.notifyAll();
						}

						ok = false;
						break;
						/*
						 * T jResult = results.get(task.getTaskID()); if
						 * (task.getMethodParameters() == null) para = 0; else
						 * para=task.getMethodParameters().length; Method m =
						 * cache2.get(task.getObjectName()+":"+task.
						 * getObjectMethod()+":"+para); Object instance =
						 * cache1.get(task.getObjectName()); Object result =
						 * m.invoke(instance, task.getMethodParameters());
						 * if(result!=null){ jResult.setCorrectResult(result);
						 * }else{ jResult.setCorrectResult("no result"); }
						 * 
						 * synchronized (jResult){ jResult.notifyAll(); } break;
						 */
					}
				}
								
				if (((System.currentTimeMillis() - ini) >= timeOut) && (ok)) {
					System.out.println("Timeout!!");
					System.out.println("Class: " + task.getObjectName() + "  Register: "
							+ nameMap.containsKey(task.getObjectName()));
					T jResult = results.get(task.getTaskID());
					jResult.setTime(task.getTaskTime());
					// jResult.addTime(System.nanoTime());
					jResult.setErrorResult(new Exception("No register class"));
					synchronized (jResult) {
						jResult.notifyAll();
					}
				}
			}
		} catch (IllegalArgumentException el) {
			System.err.println("Invalid argument. Method:" + task.getObjectMethod());
			T jResult = results.get(task.getTaskID());
			jResult.setTime(task.getTaskTime());
			// jResult.addTime(System.nanoTime());
			jResult.setErrorResult(el);

			synchronized (jResult) {
				jResult.notifyAll();
			}

		} catch (NullPointerException en) {

			System.err.println("Method invalid:" + task.getObjectMethod());
			en.printStackTrace();
			T jResult = results.get(task.getTaskID());
			jResult.setTime(task.getTaskTime());
			// jResult.addTime(System.nanoTime());
			jResult.setErrorResult(en);

			synchronized (jResult) {
				jResult.notifyAll();
			}

		} catch (Exception e) {
			System.err.println("problem in JCL orb execute(JCL_task task, Map<String, T> results)");
			System.err.println("Dados");
			System.err.println(task.getObjectName());
			System.err.println(task.getObjectMethod());
			System.out.println(task.getMethodParameters());

			e.printStackTrace();
			T jResult = results.get(task.getTaskID());
			jResult.setTime(task.getTaskTime());
			// jResult.addTime(System.nanoTime());
			jResult.setErrorResult(e);

			synchronized (jResult) {
				jResult.notifyAll();
			}
		}
	}

	@Override
	public synchronized boolean register(Class<?> serviceClass, String nickName) {
		try {
			if (nameMap.containsKey(nickName)) {
				return false;
			} else {
				String mainComponentClass = serviceClass.getName();
				ClassPool pool = ClassPool.getDefault();
				pool.insertClassPath(new ClassClassPath(serviceClass));
				// CtClass cc = pool.get(mainComponentClass);
				CtClass cc = pool.getAndRename(mainComponentClass, mainComponentClass + idClass.getAndIncrement());
				CtMethod[] ms = cc.getDeclaredMethods();
				StringBuilder buffer = new StringBuilder();
				buffer.append("public Object JCLExecPacu(int type,Object[] arg){");
				buffer.append("switch(type) {");
				for (int i = 0; i < ms.length; i++) {
					CtClass[] paType = ms[i].getParameterTypes();
					cache2.put(nickName + ":" + ms[i].getName() + ":" + paType.length, i);
					buffer.append("case " + i + ":{");
					CtClass retur = ms[i].getReturnType();
					if ((retur.isPrimitive()) && (!retur.getName().equals("void"))) {
						buffer.append("return new " + ((CtPrimitiveType) retur).getWrapperName() + "(" + ms[i].getName()
								+ "(");
					} else if (retur.getName().equals("void")) {
						buffer.append(ms[i].getName() + "(");
					} else {
						buffer.append("return " + ms[i].getName() + "(");
					}
					for (int cont = 0; cont < paType.length; cont++) {
						if (paType[cont].isPrimitive()) {
							CtPrimitiveType priType = (CtPrimitiveType) paType[cont];
							buffer.append("((" + priType.getWrapperName() + ")arg[" + cont + "])."
									+ priType.getGetMethodName() + "()");
						} else {
							buffer.append("(" + paType[cont].getName() + ")arg[" + cont + "]");
						}

						if (cont < (paType.length - 1))
							buffer.append(",");
					}

					if ((retur.isPrimitive()) && (!retur.getName().equals("void"))) {
						buffer.append("));");
					} else if (retur.getName().equals("void")) {
						buffer.append("); return null;");
					} else {
						buffer.append(");");
					}
					buffer.append("}");
				}
				buffer.append("}");
				buffer.append("return null;}");
				CtClass ccInt = pool.get("interfaces.kernel.JCL_execute");
				cc.addInterface(ccInt);
				CtMethod method = CtNewMethod.make(buffer.toString(), cc);
				cc.addMethod(method);
				// Loader cl = new Loader(pool);
				// cl.loadClass("interfaces.kernel.JCL_execute");
				Class<? extends JCL_execute> cla = cc.toClass();
				cache1.put(nickName, cla.newInstance());
				nameMap.put(nickName, cla);
				return true;
			}

		} catch (Exception e) {
			System.err.println("problem in JCL orb register(Class<?> serviceClass, String nickName)");
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public synchronized boolean register(CtClass cc, String nickName) {
		// try{

		/*
		 * if(nameMap.containsKey(nickName)){ return false; }else{ Method[] ms =
		 * serviceClass.getMethods(); for(int i = 0; i<ms.length ; i++){
		 * cache2.put(nickName+":"+ms[i].getName()+":"+ms[i].getParameterTypes()
		 * .length, i); cache1.put(nickName, serviceClass.newInstance()); }
		 * nameMap.put(nickName, serviceClass);
		 * 
		 * return true; }
		 */
		/*
		 * } catch (Exception e){ System.err.println(
		 * "problem in JCL orb register(Class<?> serviceClass, String nickName)"
		 * ); e.printStackTrace(); return false; }
		 */
		try {
			if (nameMap.containsKey(nickName)) {
				return false;
			} else {
				// String mainComponentClass = serviceClass.getName();
				ClassPool pool = ClassPool.getDefault();
				// pool.appendClassPath(new
				// LoaderClassPath(serviceClass.getClassLoader()));
				// CtClass cc = pool.get(mainComponentClass);
				// CtClass cc =
				// pool.getAndRename(mainComponentClass,mainComponentClass+idClass.getAndIncrement());
				CtMethod[] ms = cc.getDeclaredMethods();
				StringBuilder buffer = new StringBuilder();
				buffer.append("public Object JCLExecPacu(int type,Object[] arg){");
				buffer.append("switch(type) {");
				for (int i = 0; i < ms.length; i++) {
					CtClass[] paType = ms[i].getParameterTypes();
					cache2.put(nickName + ":" + ms[i].getName() + ":" + paType.length, i);
					buffer.append("case " + i + ":{");
					CtClass retur = ms[i].getReturnType();
					if ((retur.isPrimitive()) && (!retur.getName().equals("void"))) {
						buffer.append("return new " + ((CtPrimitiveType) retur).getWrapperName() + "(" + ms[i].getName()
								+ "(");
					} else if (retur.getName().equals("void")) {
						buffer.append(ms[i].getName() + "(");
					} else {
						buffer.append("return " + ms[i].getName() + "(");
					}
					for (int cont = 0; cont < paType.length; cont++) {
						if (paType[cont].isPrimitive()) {
							CtPrimitiveType priType = (CtPrimitiveType) paType[cont];
							buffer.append("((" + priType.getWrapperName() + ")arg[" + cont + "])."
									+ priType.getGetMethodName() + "()");
						} else {
							buffer.append("(" + paType[cont].getName() + ")arg[" + cont + "]");
						}

						if (cont < (paType.length - 1))
							buffer.append(",");
					}

					if ((retur.isPrimitive()) && (!retur.getName().equals("void"))) {
						buffer.append("));");
					} else if (retur.getName().equals("void")) {
						buffer.append("); return null;");
					} else {
						buffer.append(");");
					}
					buffer.append("}");
				}
				buffer.append("}");
				buffer.append("return null;}");
				CtClass ccInt = pool.get("interfaces.kernel.JCL_execute");
				cc.defrost();
				String oldName = cc.getName();
				cc.replaceClassName(oldName, oldName + idClass.getAndIncrement());
				cc.addInterface(ccInt);
				CtMethod method = CtNewMethod.make(buffer.toString(), cc);
				cc.addMethod(method);
				// Loader cl = new Loader(pool);
				// cl.loadClass("interfaces.kernel.JCL_execute");
				Class<? extends JCL_execute> cla = cc.toClass();
				cache1.put(nickName, cla.newInstance());
				nameMap.put(nickName, cla);

				return true;
			}

		} catch (Exception e) {
			System.err.println("problem in JCL orb register(Class<?> serviceClass, String nickName)");
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public boolean register(String mainComponentClass, String nickName) {
		try {
			if (nameMap.containsKey(nickName)) {
				return false;
			} else {
				ClassPool pool = ClassPool.getDefault();
				CtClass cc = pool.get(mainComponentClass);
				CtMethod[] ms = cc.getDeclaredMethods();
				StringBuilder buffer = new StringBuilder();
				buffer.append("public Object JCLExecPacu(int type,Object[] arg){");
				buffer.append("switch(type) {");
				for (int i = 0; i < ms.length; i++) {
					CtClass[] paType = ms[i].getParameterTypes();
					cache2.put(nickName + ":" + ms[i].getName() + ":" + paType.length, i);
					buffer.append("case " + i + ":{");
					CtClass retur = ms[i].getReturnType();
					if ((retur.isPrimitive()) && (!retur.getName().equals("void"))) {
						buffer.append("return new " + ((CtPrimitiveType) retur).getWrapperName() + "(" + ms[i].getName()
								+ "(");
					} else if (retur.getName().equals("void")) {
						buffer.append(ms[i].getName() + "(");
					} else {
						buffer.append("return " + ms[i].getName() + "(");
					}
					for (int cont = 0; cont < paType.length; cont++) {
						if (paType[cont].isPrimitive()) {
							CtPrimitiveType priType = (CtPrimitiveType) paType[cont];
							buffer.append("((" + priType.getWrapperName() + ")arg[" + cont + "])."
									+ priType.getGetMethodName() + "()");
						} else {
							buffer.append("(" + paType[cont].getName() + ")arg[" + cont + "]");
						}

						if (cont < (paType.length - 1))
							buffer.append(",");
					}

					if ((retur.isPrimitive()) && (!retur.getName().equals("void"))) {
						buffer.append("));");
					} else if (retur.getName().equals("void")) {
						buffer.append("); return null;");
					} else {
						buffer.append(");");
					}
					buffer.append("}");
				}
				buffer.append("}");
				buffer.append("return null;}");
				CtClass ccInt = pool.get("interfaces.kernel.JCL_execute");
				cc.addInterface(ccInt);
				CtMethod method = CtNewMethod.make(buffer.toString(), cc);
				cc.addMethod(method);
				// cc.replaceClassName(cc.getName(),
				// cc.getName()+idClass.getAndIncrement());
				Class<? extends JCL_execute> cla = cc.toClass();
				cache1.put(nickName, cla.newInstance());
				nameMap.put(nickName, cla);

				return true;
			}

		} catch (Exception e) {
			System.err.println("problem in JCL orb register(Class<?> serviceClass, String nickName)");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public synchronized boolean unRegister(String nickName) {
		try {
			
			System.out.println("Unregister");

			if (nameMap.containsKey(nickName)) {
				nameMap.remove(nickName);
				cache1.remove(nickName);
				for (String key : cache2.keySet()) {
					if (key.startsWith(nickName)) {
						cache2.remove(key);
					}
				}
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			System.err.println("problem in JCL orb unRegister(String nickName)");

			return false;
		}
	}

	@Override
	public synchronized boolean destroyGlobalVar(Object key) {
		try {

			if (globalVars.containsKey(key)) {
				globalVars.remove(key);
				return true;
			} else
				return false;

		} catch (Exception e) {
			System.err.println("problem in JCL orb destroyGlobalVar(String varName)");

			return false;
		}
	}

	@Override
	public synchronized boolean instantiateGlobalVar(Object key, Object instance) {
		// TODO Auto-generated method stub
		try {
			if (instance == null) {
				return false;
			}
			if (!globalVars.containsKey(key)) {
				globalVars.put(key, instance);
				return true;
			} else
				return false;

		} catch (Exception e) {
			System.err.println("problem in JCL orb instantiateGlobalVar(String varName, Object instance)");

			return false;
		}

	}

	@Override
	public synchronized boolean instantiateGlobalVar(Object key, String nickName, File[] jars,
			Object[] defaultVarValue) {
		// TODO Auto-generated method stub

		try {

			if (globalVars.containsKey(key)) {
				return false;
			} else {

				for (File f : jars) {
					this.addURL((f.toURI().toURL()));
				}

				if (defaultVarValue == null) {
					Object var = Class.forName(nickName).newInstance();
					globalVars.put(key, var);
					return true;
				} else {
					Constructor[] cs = Class.forName(nickName).getConstructors();
					for (Constructor c : cs) {
						if (c.getParameterTypes() != null) {
							boolean flag = true;
							if (c.getParameterTypes().length == defaultVarValue.length)
								for (int i = 0; i < c.getParameterTypes().length; i++) {
									Class<?> aClass = c.getParameterTypes()[i];
									if (!aClass.equals(defaultVarValue[i].getClass())) {
										flag = false;
									}

								}
							if (flag) {
								Object var = c.newInstance(defaultVarValue);
								globalVars.put(key, var);

								return true;

							}
						}
					}
				}

				return false;
			}

		} catch (Exception e) {
			
			System.err.println(
					"problem in JCL orb instantiateGlobalVar(String varName, File[] jars, Object[] defaultVarValue)");

			return false;
		}
	}

	@Override
	public synchronized boolean instantiateGlobalVar(Object key, String nickName, Object[] defaultVarValue) {
		// TODO Auto-generated method stub

		try {

			if (globalVars.containsKey(key)) {
				return false;
			} else {

				if (defaultVarValue == null) {
					Object var = Class.forName(nickName).newInstance();
					globalVars.put(key, var);
					return true;
				} else {
					Constructor[] cs = Class.forName(nickName).getConstructors();
					for (Constructor c : cs) {
						if (c.getParameterTypes() != null) {
							boolean flag = true;
							if (c.getParameterTypes().length == defaultVarValue.length){
								for (int i = 0; i < c.getParameterTypes().length; i++) {
									Class<?> aClass = c.getParameterTypes()[i];
									if (aClass.isPrimitive()) aClass = Primitives.wrap(aClass);
									if (!aClass.equals(defaultVarValue[i].getClass())) {
										flag = false;
									}

								}
							}else{
								flag = false;
							}
							
							if (flag) {
								Object var = c.newInstance(defaultVarValue);
								globalVars.put(key, var);
								return true;

							}
						}
					}
				}

				return false;
			}

		} catch (Exception e) {
			System.err.println(
					"problem in JCL orb instantiateGlobalVar(String varName, File[] jars, Object[] defaultVarValue)");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean setValue(Object key, Object value) {
		// TODO Auto-generated method stub

		try {
			if (globalVars.containsKey(key)) {
				// no wait and notify
				if (locks.contains(key)) {
					return false;
				} else {
					globalVars.put(key, value);
					return true;
				}
			} else {
				return false;
			}

		} catch (Exception e) {
			System.err.println("problem in JCL orb setValue(String varName, Object value)");

			return false;
		}
	}

	@Override
	public JCL_result getValue(Object key) {
		try {
			Object obj = globalVars.get(key);
			if (obj == null) {
				JCL_result jclr = new JCL_resultImpl();
				jclr.setCorrectResult("No value found!");
				return jclr;
			} else {
				JCL_result jclr = new JCL_resultImpl();
				jclr.setCorrectResult(obj);
				return jclr;
			}
		} catch (Exception e) {
			System.err.println("problem in JCL orb getValue(String varName)");

			JCL_result jclr = new JCL_resultImpl();
			jclr.setErrorResult(e);
			return jclr;
		}
	}

	@Override
	public JCL_result getValueLocking(Object key) {
		// TODO Auto-generated method stub
		try {
			if (globalVars.containsKey(key)) {
				synchronized (locks) {
					// no wait and notify
					if (locks.contains(key))
						return null;

					JCL_result jclr = new JCL_resultImpl();
					jclr.setCorrectResult(globalVars.get(key));
					locks.add(key);
					return jclr;
				}

			} else {

				JCL_result jclr = new JCL_resultImpl();
				jclr.setCorrectResult("No value found!");

				return jclr;
			}
		} catch (Exception e) {
			System.err.println("problem in JCL orb getValueLocking(String varName)");

			JCL_result jclr = new JCL_resultImpl();
			jclr.setErrorResult(e);
			return jclr;
		}
	}

	@Override
	public boolean setValueUnlocking(Object key, Object value) {
		// TODO Auto-generated method stub
		try {
			if (globalVars.containsKey(key)) {
				globalVars.put(key, value);
				locks.remove(key);

				return true;

			} else {
				return false;
			}

		} catch (Exception e) {
			System.err.println("problem in JCL orb setValueUnlocking(String varName)");

			return false;
		}

	}

	/*
	 * @Override public boolean register(File[] f, String classToBeExecuted) {
	 * try{
	 * 
	 * if(containsTask(classToBeExecuted)){ return true; }else { Class<?> c=
	 * registerJar(f, classToBeExecuted); synchronized (c) { if(c!=null){ return
	 * register(c, classToBeExecuted); }else { System.err.println(
	 * " not registered"); return false; } }
	 * 
	 * }
	 * 
	 * }catch(Exception e){
	 * 
	 * System.err.println(
	 * "problem in JCL orb register(File f, String classToBeExecuted)");
	 * e.printStackTrace(); return false; } }
	 */
	private void addURL(URL url) throws Exception {

		Class<URLClassLoader> clazz = URLClassLoader.class;

		// Use reflection
		Method method = clazz.getDeclaredMethod("addURL", new Class[] { URL.class });
		method.setAccessible(true);
		method.invoke(classLoader, new Object[] { url });
	}

	public boolean register(File[] fs, String classToBeExecuted) {
		try {

			// if(!new File("../user_jars/").isDirectory()){
			// new File("../user_jars/").mkdir();
			// }

			for (File f : fs) {
				this.addURL((f.toURI().toURL()));
			}

			JarFile jar = new JarFile(fs[0]);

			for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();) {
				JarEntry entry = entries.nextElement();
				String file = entry.getName();

				if (file.endsWith(classToBeExecuted + ".class")) {
					String classname = file.replace('/', '.').substring(0, file.length() - 6);
					return this.register(classname, classToBeExecuted);

				}
			}

			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean containsTask(String nickName) {
		if (nickName == null)
			return false;
		return this.nameMap.containsKey(nickName);
	}

	@Override
	public boolean containsGlobalVar(Object key) {
		if (key == null)
			return false;
		return this.globalVars.containsKey(key);
	}

	@Override
	public Set<Entry<Object, Object>> getGlobalVarEntrySet() {

		return this.globalVars.entrySet();
	}

	@Override
	public boolean lockGlobalVar(Object key) {
		// TODO Auto-generated method stub
		try {
			if (globalVars.containsKey(key)) {
				if (locks.contains(key)) {
					this.lockGlobalVar(key);
					return false;
				} else {
					synchronized (locks) {
						return locks.add(key);
					}
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			System.err.println("problem in JCL orb lock(Object key)");
			return false;
		}
	}

	@Override
	public boolean unLockGlobalVar(Object key) {
		// TODO Auto-generated method stub
		try {
			if (globalVars.containsKey(key)) {
				synchronized (locks) {
					if (locks.contains(key)) {
						globalVars.remove(key);
						return locks.remove(key);
					}

					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			System.err.println("problem in JCL orb lock(Object key)");
			return false;
		}
	}

	@SuppressWarnings("rawtypes")
	public static JCL_orb getInstance() {
		return Holder.getInstance();
	}

	@SuppressWarnings("rawtypes")
	public static JCL_orb getInstancePacu() {
		return Holder.getInstancePacu();
	}

	private static class Holder {

		protected static JCL_orb getInstance() {
			if (instance == null) {
				instance = new JCL_orbImpl();
			}
			return instance;
		}

		public static JCL_orb getInstancePacu() {
			if (instancePacu == null) {
				instancePacu = new JCL_orbImpl();
			}
			return instancePacu;
		}
	}

	@Override
	public boolean isLock(Object key) {
		if (key == null)
			return false;
		return locks.contains(key);
	}

	@Override
	public boolean cleanEnvironment() {
		try {
			
			globalVars.clear();
			locks.clear();
			nameMap.clear();
			cache1.clear();
			cache2.clear();

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}

	public Map<Long, T> getResults() {
		return results;
	}

	public void setResults(Map<Long, T> results) {
		this.results = results;
	}

	public static AtomicInteger getRegisterMsg() {
		return RegisterMsg;
	}

	public static void setRegisterMsg(AtomicInteger registerMsg) {
		RegisterMsg = registerMsg;
	}
}
