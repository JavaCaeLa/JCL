package implementations.sm_kernel;

import android.util.Log;


import com.google.common.primitives.Primitives;

import interfaces.kernel.JCL_orb;
import interfaces.kernel.JCL_result;
import interfaces.kernel.JCL_task;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import commom.JCL_resultImpl;

public class JCL_orbImpl<T extends JCL_result> implements JCL_orb<T> {

    private Map<String, Class<?>> nameMap;
    private Map<Object, Object> globalVars;
    private Map<String, Object> cache1;
    private AtomicLong idClass = new AtomicLong(0);
    private Map<String, Class<?>> gvClasses;
    private Set<Object> locks;
    private long timeOut = 5000L;
    private static JCL_orb instance;
    private static JCL_orb instancePacu;
    private Map<Long, T> results;
    private static AtomicInteger RegisterMsg;


    private JCL_orbImpl() {

        nameMap = new ConcurrentHashMap<String, Class<?>>();
        locks = new ConcurrentSkipListSet<Object>();
        globalVars = new ConcurrentHashMap<Object, Object>();
        cache1 = new ConcurrentHashMap<>();
        gvClasses = new HashMap<>();
    }

    //@Override
    public void execute2(JCL_task task, Map<Long, T> results) {
        try {
            int para;

            if (nameMap.containsKey(task.getObjectName())) {
                T jResult = results.get(task.getTaskID());
                Object instance = cache1.get(task.getObjectName());
                if (task.getMethodParameters() == null)
                    para = 0;
                else
                    para = task.getMethodParameters().length;

                //int type = cache2.get(task.getObjectName() + ":" + task.getObjectMethod() + ":" + para);
                task.setTaskTime(System.nanoTime());
                Object result =  null;//instance.JCLExecPacu(type, task.getMethodParameters());
                task.setTaskTime(System.nanoTime());

                jResult.setTime(task.getTaskTime());

                if (result != null) {
                    jResult.setCorrectResult(result);
                } else {
                    jResult.setCorrectResult("no result");
                }

                synchronized (jResult) {
                    jResult.notifyAll();
                }

            } else {
                Long ini = System.currentTimeMillis();
                boolean ok = true;

                while ((System.currentTimeMillis() - ini) < timeOut) {
                    if (nameMap.containsKey(task.getObjectName())) {

                        T jResult = results.get(task.getTaskID());
                        Object instance = cache1.get(task.getObjectName());
                        if (task.getMethodParameters() == null)
                            para = 0;
                        else
                            para = task.getMethodParameters().length;
                        //int type = cache2.get(task.getObjectName() + ":" + task.getObjectMethod() + ":" + para);

                        task.setTaskTime(System.nanoTime());
                        Object result = null;//instance.JCLExecPacu(type, task.getMethodParameters());
                        task.setTaskTime(System.nanoTime());

                        jResult.setTime(task.getTaskTime());

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
                if (((System.currentTimeMillis() - ini) > timeOut) && (ok)) {
                    System.out.println("Timeout!!");
                    System.out.println("Class: " + task.getObjectName() + "  Register: "
                            + nameMap.containsKey(task.getObjectName()));
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
    public void execute(JCL_task task) {
        try {
            int para;

            if (nameMap.containsKey(task.getObjectName())) {
                T jResult = results.get(task.getTaskID());
                Object instance = cache1.get(task.getObjectName());
                if (task.getMethodParameters() == null)
                    para = 0;
                else
                    para = task.getMethodParameters().length;

                task.setTaskTime(System.nanoTime());
                Object result =  intercept(getMethodNumber(instance, task.getObjectMethod(), para), task.getMethodParameters(), instance);//instance.JCLExecPacu(getMethodNumber(instance, task.getObjectMethod(), para) + "", task.getMethodParameters(), instance);
                task.setTaskTime(System.nanoTime());

                jResult.setTime(task.getTaskTime());
                jResult.setMemorysize(0);

                if (result != null) {
                    jResult.setCorrectResult(result);
                } else {
                    jResult.setCorrectResult("no result");
                }

                synchronized (jResult) {
                    jResult.notifyAll();
                }

            } else {
                Long ini = System.currentTimeMillis();
                boolean ok = true;

                while ((System.currentTimeMillis() - ini) < timeOut) {
                    if (nameMap.containsKey(task.getObjectName())) {

                        T jResult = results.get(task.getTaskID());
                        Object instance = cache1.get(task.getObjectName());
                        if (task.getMethodParameters() == null)
                            para = 0;
                        else
                            para = task.getMethodParameters().length;

                        task.setTaskTime(System.nanoTime());
                        Object result = intercept(getMethodNumber(instance, task.getObjectMethod(), para), task.getMethodParameters(), instance); // instance.JCLExecPacu();
                        task.setTaskTime(System.nanoTime());

                        jResult.setTime(task.getTaskTime());

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
                if (((System.currentTimeMillis() - ini) > timeOut) && (ok)) {
                    System.out.println("Timeout!!");
                    System.out.println("Class: " + task.getObjectName() + "  Register: "
                            + nameMap.containsKey(task.getObjectName()));
                }
            }
        } catch (IllegalArgumentException el) {
            System.err.println("Invalid argument. Method:" + task.getObjectMethod());
            T jResult = results.get(task.getTaskID());
            //	jResult.setTime(task.getTaskTime());
            // jResult.addTime(System.nanoTime());
            jResult.setErrorResult(el);
            el.printStackTrace();

            synchronized (jResult) {
                jResult.notifyAll();
            }

        } catch (NullPointerException en) {

            System.err.println("Method invalid:" + task.getObjectMethod());
            T jResult = results.get(task.getTaskID());
            //jResult.setTime(task.getTaskTime());
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
            //jResult.setTime(task.getTaskTime());
            // jResult.addTime(System.nanoTime());
            jResult.setErrorResult(e);

            synchronized (jResult) {
                jResult.notifyAll();
            }
        }
    }


    public int getMethodNumber(Object clas, String nameM, int quanTParam) {
        //Log.e("Chamando", nameM);
        Method[] m = clas.getClass().getMethods();
        for (int i = 0; i < m.length; i++) {
            if (m[i].getName().equals(nameM) && m[i].getParameterTypes().length == quanTParam)
                return i;
        }
        return -1;
    }




    @Override
    public synchronized boolean register(Class<?> serviceClass, String nickName) {
        try {
            //if (nameMap.containsKey(nickName)) {
                //return false;
            //} else {
                //nameMet.put(nickName, new ConcurrentHashMap<Integer, String>());
                //tyPeMet.put(nickName, new ConcurrentHashMap<String, Class<?>>());

//                File file = JCL_ApplicationContext.getContext().getDir("jcl_user", Context.MODE_PRIVATE);
//                if (!file.isDirectory()) {
//                    throw new IOException("Not a directory: " + file);
//                }
//
//
//                String mainComponentClass = serviceClass.getName();
//
//                ByteBuddy by = new ByteBuddy();
//
//                if (serviceClass.getPackage() != null)
//                    by.makePackage(serviceClass.getPackage().getName());
//
//                Class<?> cc = by.subclass(serviceClass)
//                        .implement(JCL_execute.class)
//                        .method(ElementMatchers.named("JCLExecPacu"))
//                        .intercept(MethodDelegation.to(MyInterceptor.class))
//                        .name(mainComponentClass + idClass.getAndIncrement())
//                        .make()
//                        .load(serviceClass.getClassLoader(), new AndroidClassLoadingStrategy(file)).getLoaded();
//
//                Class<? extends JCL_execute> cla = (Class<? extends JCL_execute>) cc;
                cache1.put(nickName, serviceClass.newInstance());
                nameMap.put(nickName,  serviceClass);

                return true;
            //}

        } catch (Exception e) {
            System.err.println("problem in JCL orb register(Class<?> serviceClass, String nickName)");
            e.printStackTrace();
            return false;
        }

    }
    @Override
    public synchronized boolean registerGV(Class<?> gvClass, String nickName) {
        try {
            Log.e("Registrando", gvClass.getName());
            gvClasses.put(nickName,gvClass);
            return true;
            //}

        } catch (Exception e) {
            System.err.println("problem in JCL orb register(Class<?> serviceClass, String nickName)");
            e.printStackTrace();
            return false;
        }

    }

//	@Override
//	public synchronized boolean register(CtClass cc, String nickName) {
//		// try{
//
//		/*
//		 * if(nameMap.containsKey(nickName)){ return false; }else{ Method[] ms =
//		 * serviceClass.getMethods(); for(int i = 0; i<ms.length ; i++){
//		 * cache2.put(nickName+":"+ms[i].getName()+":"+ms[i].getParameterTypes()
//		 * .length, i); cache1.put(nickName, serviceClass.newInstance()); }
//		 * nameMap.put(nickName, serviceClass);
//		 *
//		 * return true; }
//		 */
//		/*
//		 * } catch (Exception e){ System.err.println(
//		 * "problem in JCL orb register(Class<?> serviceClass, String nickName)"
//		 * ); e.printStackTrace(); return false; }
//		 */
//		try {
//			if (nameMap.containsKey(nickName)) {
//				return false;
//			} else {
//				// String mainComponentClass = serviceClass.getName();
//				ClassPool pool = ClassPool.getDefault();
//				// pool.appendClassPath(new
//				// LoaderClassPath(serviceClass.getClassLoader()));
//				// CtClass cc = pool.get(mainComponentClass);
//				// CtClass cc =
//				// pool.getAndRename(mainComponentClass,mainComponentClass+idClass.getAndIncrement());
//				CtMethod[] ms = cc.getDeclaredMethods();
//				StringBuilder buffer = new StringBuilder();
//				buffer.append("public Object JCLExecPacu(int type,Object[] arg){");
//				buffer.append("switch(type) {");
//				for (int i = 0; i < ms.length; i++) {
//					CtClass[] paType = ms[i].getParameterTypes();
//					cache2.put(nickName + ":" + ms[i].getName() + ":" + paType.length, i);
//					buffer.append("case " + i + ":{");
//					CtClass retur = ms[i].getReturnType();
//					if ((retur.isPrimitive()) && (!retur.getName().equals("void"))) {
//						buffer.append("return new " + ((CtPrimitiveType) retur).getWrapperName() + "(" + ms[i].getName()
//								+ "(");
//					} else if (retur.getName().equals("void")) {
//						buffer.append(ms[i].getName() + "(");
//					} else {
//						buffer.append("return " + ms[i].getName() + "(");
//					}
//					for (int cont = 0; cont < paType.length; cont++) {
//						if (paType[cont].isPrimitive()) {
//							CtPrimitiveType priType = (CtPrimitiveType) paType[cont];
//							buffer.append("((" + priType.getWrapperName() + ")arg[" + cont + "])."
//									+ priType.getGetMethodName() + "()");
//						} else {
//							buffer.append("(" + paType[cont].getName() + ")arg[" + cont + "]");
//						}
//
//						if (cont < (paType.length - 1))
//							buffer.append(",");
//					}
//
//					if ((retur.isPrimitive()) && (!retur.getName().equals("void"))) {
//						buffer.append("));");
//					} else if (retur.getName().equals("void")) {
//						buffer.append("); return null;");
//					} else {
//						buffer.append(");");
//					}
//					buffer.append("}");
//				}
//				buffer.append("}");
//				buffer.append("return null;}");
//				CtClass ccInt = pool.get("interfaces.kernel.JCL_execute");
//				cc.defrost();
//				String oldName = cc.getName();
//				cc.replaceClassName(oldName, oldName + idClass.getAndIncrement());
//				cc.addInterface(ccInt);
//				CtMethod method = CtNewMethod.make(buffer.toString(), cc);
//				cc.addMethod(method);
//				// Loader cl = new Loader(pool);
//				// cl.loadClass("interfaces.kernel.JCL_execute");
//				Class<? extends JCL_execute> cla = cc.toClass();
//				cache1.put(nickName, cla.newInstance());
//				nameMap.put(nickName, cla);
//
//				return true;
//			}
//
//		} catch (Exception e) {
//			System.err.println("problem in JCL orb register(Class<?> serviceClass, String nickName)");
//			e.printStackTrace();
//			return false;
//		}
//
//	}

    @Override
    public boolean register(String mainComponentClass, String nickName) {
        try {
            if (nameMap.containsKey(nickName)) {
                return false;
            } else {
//				ClassPool pool = ClassPool.getDefault();
//				CtClass cc = pool.get(mainComponentClass);
//				CtMethod[] ms = cc.getDeclaredMethods();
//				StringBuilder buffer = new StringBuilder();
//				buffer.append("public Object JCLExecPacu(int type,Object[] arg){");
//				buffer.append("switch(type) {");
//				for (int i = 0; i < ms.length; i++) {
//					CtClass[] paType = ms[i].getParameterTypes();
//					cache2.put(nickName + ":" + ms[i].getName() + ":" + paType.length, i);
//					buffer.append("case " + i + ":{");
//					CtClass retur = ms[i].getReturnType();
//					if ((retur.isPrimitive()) && (!retur.getName().equals("void"))) {
//						buffer.append("return new " + ((CtPrimitiveType) retur).getWrapperName() + "(" + ms[i].getName()
//								+ "(");
//					} else if (retur.getName().equals("void")) {
//						buffer.append(ms[i].getName() + "(");
//					} else {
//						buffer.append("return " + ms[i].getName() + "(");
//					}
//					for (int cont = 0; cont < paType.length; cont++) {
//						if (paType[cont].isPrimitive()) {
//							CtPrimitiveType priType = (CtPrimitiveType) paType[cont];
//							buffer.append("((" + priType.getWrapperName() + ")arg[" + cont + "])."
//									+ priType.getGetMethodName() + "()");
//						} else {
//							buffer.append("(" + paType[cont].getName() + ")arg[" + cont + "]");
//						}
//
//						if (cont < (paType.length - 1))
//							buffer.append(",");
//					}
//
//					if ((retur.isPrimitive()) && (!retur.getName().equals("void"))) {
//						buffer.append("));");
//					} else if (retur.getName().equals("void")) {
//						buffer.append("); return null;");
//					} else {
//						buffer.append(");");
//					}
//					buffer.append("}");
//				}
//				buffer.append("}");
//				buffer.append("return null;}");
//				CtClass ccInt = pool.get("interfaces.kernel.JCL_execute");
//				cc.addInterface(ccInt);
//				CtMethod method = CtNewMethod.make(buffer.toString(), cc);
//				cc.addMethod(method);
//				// cc.replaceClassName(cc.getName(),
//				// cc.getName()+idClass.getAndIncrement());
//				Class<? extends JCL_execute> cla = cc.toClass();
//				cache1.put(nickName, cla.newInstance());
//				nameMap.put(nickName, cla);

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

            if (nameMap.containsKey(nickName)) {
                nameMap.remove(nickName);
                cache1.remove(nickName);
//                for (String key : cache2.keySet()) {
//                    if (key.startsWith(nickName)) {
//                        cache2.remove(key);
//                    }
//                }
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
            e.printStackTrace();
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
                    Object var = gvClasses.get(nickName).newInstance();//Class.forName(nickName).newInstance();
                    globalVars.put(key, var);
                    return true;
                } else {
                    Constructor[] cs = gvClasses.get(nickName).getConstructors();
                    for (Constructor c : cs) {
                        if (c.getParameterTypes() != null) {
                            boolean flag = true;
                            if (c.getParameterTypes().length == defaultVarValue.length) {
                                for (int i = 0; i < c.getParameterTypes().length; i++) {
                                    Class<?> aClass = c.getParameterTypes()[i];
                                    Class<?> compareClass = defaultVarValue[i].getClass();
                                    if (aClass.isPrimitive())
                                        aClass = Primitives.wrap(aClass);
                                        //compareClass= compareClass.getField("TYPE").getDeclaringClass();
                                    //Log.e("name", aClass.getName() + ";" + compareClass.getName());
                                    if (!aClass.equals(compareClass)) {
                                        flag = false;
                                    }

                                }
                            }else
                                flag = false;
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
            e.printStackTrace();
            System.err.println(
                    "problem in JCL orb instantiateGlobalVar(String varName, File[] jars, Object[] defaultVarValue)");

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

//		Class<URLClassLoader> clazz = URLClassLoader.class;
//
//		// Use reflection
//		Method method = clazz.getDeclaredMethod("addURL", new Class[] { URL.class });
//		method.setAccessible(true);
        //method.invoke(classLoader, new Object[] { url });
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

            for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
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

    /*
     * private synchronized Class<?> registerJar(File[] fs, String
     * classToBeExecuted) throws Exception{ if(!new
     * File("../user_jars/").isDirectory()){ new File("../user_jars/").mkdir();
     * } for(File f: fs) copyJarFile(new JarFile(f), new File("../user_jars/"));
     *
     * URL[] urls = new URL[fs.length];
     *
     * for(int i=0; i<fs.length; i++){ urls[i] = new File("../user_jars/" +
     * fs[i].getName()).toURI().toURL(); } JarFile jar = new
     * JarFile("../user_jars/" + fs[0].getName());
     *
     * for (Enumeration<JarEntry> entries = jar.entries() ;
     * entries.hasMoreElements() ;) { JarEntry entry = entries.nextElement();
     * String file = entry.getName();
     *
     * if (file.endsWith(classToBeExecuted+ ".class")) {
     *
     * String classname = file.replace('/', '.').substring(0, file.length() -
     * 6);
     *
     *
     * Enumeration<URL> jars =
     * ClassLoader.getSystemClassLoader().getResources(urls[0].toString());
     *
     * if(jars.hasMoreElements()) System.err.println("used...");
     *
     * URLClassLoader urlCL = new URLClassLoader(urls);
     *
     * Class<?> c = urlCL.loadClass(classname);
     *
     * return c; } }
     *
     * return null;
     *
     * }
     */
	/*
	 * private static void copyJarFile(JarFile jarFile, File destDir) throws
	 * Exception { String fileName = jarFile.getName();
	 * 
	 * String fileNameLastPart = "";
	 * 
	 * if(fileName.lastIndexOf(File.separator)==-1){ fileNameLastPart =
	 * fileName; }else fileNameLastPart =
	 * fileName.substring(fileName.lastIndexOf(File.separator));
	 * 
	 * File destFile = new File(destDir, fileNameLastPart);
	 * 
	 * JarOutputStream jos = new JarOutputStream(new
	 * FileOutputStream(destFile)); Enumeration<JarEntry> entries =
	 * jarFile.entries();
	 * 
	 * while (entries.hasMoreElements()) { JarEntry entry =
	 * entries.nextElement(); InputStream is = jarFile.getInputStream(entry);
	 * 
	 * //jos.putNextEntry(entry); //create a new entry to avoid ZipException:
	 * invalid entry compressed size jos.putNextEntry(new
	 * JarEntry(entry.getName())); byte[] buffer = new byte[4096]; int bytesRead
	 * = is.read(buffer); while (bytesRead!= -1) { jos.write(buffer, 0,
	 * bytesRead); } is.close(); jos.flush(); jos.closeEntry(); } jos.close(); }
	 */
    @Override
    public boolean containsTask(String nickName) {
        if (nickName == null)
            return false;
        return this.nameMap.containsKey(nickName);
    }

    @Override
    public boolean containsGlobalVar(Object key) {
        Log.e("GV", this.globalVars.toString());

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
            Log.e("Clean", "Orb");


            Log.e("Tamanho Antes", globalVars.size()+"");
            Log.e("Itens Antes", globalVars.toString());
            globalVars.clear();
            Log.e("Tamanho", globalVars.size()+"");
            Log.e("Itens", globalVars.toString());
            locks.clear();
            nameMap.clear();
            cache1.clear();
            gvClasses.clear();
            //cache2.clear();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private Object intercept(int index, Object[] arg, Object clas) {
        if (index==-1)
            return null;
        try {
            Method myinvocation2 = clas.getClass().getMethods()[index];
            //Log.e("Method name", myinvocation2.getName());
            return myinvocation2.invoke(clas, arg);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
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
