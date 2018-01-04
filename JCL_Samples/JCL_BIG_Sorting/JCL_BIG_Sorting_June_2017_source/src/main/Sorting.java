package main;

import implementations.collections.JCLHashMap;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_map;
import interfaces.kernel.JCL_facade;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Sorting {
    public void bugFix() {}
    
    public Integer getCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    public List<String> phase1(int id, String name, int numJCLThreads) {
        Int2LongMap values = new Int2LongOpenHashMap(1000000);
        long totalF = 0;
        System.err.println("file: " + name);
        try {
            File f = new File("../" + name + "/" + name + ".bin");
            InputStream in = new BufferedInputStream(new FileInputStream(f));
            FastBufferedInputStream fb = new FastBufferedInputStream(in);
            byte[] i = new byte[4];
            while (fb.read(i) == 4) {
                int k = java.nio.ByteBuffer.wrap(i).getInt();
                if (!values.containsKey(k))
                    values.put(k, 1);
                else {
                    long aux = values.get(k);
                    aux++;
                    values.put(k, aux);
                }
                totalF++;
            }
            fb.close();
            in.close();
            
            // primeira modificacao
            //for (long v : values.values())
            //    totalF += v;
            
            IntSet sorted = new IntAVLTreeSet(values.keySet());
            long acumula = 0;
            int b = 0;
            List<String> result = new LinkedList<>();
            long blinha = 0; 
            int last = 0;
            for (int ac : sorted) {
                blinha = values.get(ac);
                acumula += blinha;
                if (acumula > (totalF / (numJCLThreads))) {
                    b = ac;
                    result.add(b + ":" + acumula);
                    acumula = 0;
                }
                last = ac;
            }
            
            // segunda modificacao
            if(acumula != 0) result.add(last + ":" + acumula);
            
            JCL_facade jcl = JCL_FacadeImpl.getInstanceLambari();
            jcl.instantiateGlobalVar(id, values);
            sorted.clear();
            sorted = null;
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void phase2(int id, int numJCLThreads, String schema) {
        JCL_facade jcl = JCL_FacadeImpl.getInstanceLambari();
        Int2LongMap sorted = (Int2LongMap) jcl.getValue(id).getCorrectResult();
        jcl.deleteGlobalVar(id);
        String[] chunks = schema.split(":");
        int i = 0;
        Int2LongMap[] finais = new Int2LongMap[numJCLThreads];
        for (int r = 0; r < numJCLThreads; r++)
            finais[r] = new Int2LongOpenHashMap();
        for (int ii : sorted.keySet()) {
            i = 0;
            if (ii >= Integer.parseInt(chunks[chunks.length - 1]))
                finais[numJCLThreads - 1].put(ii, sorted.get(ii));
            else {
                if (ii < Integer.parseInt(chunks[0]))
                    finais[0].put(ii, sorted.get(ii));
                else {
                    tag: {
                        for (int k = 1; k < chunks.length; k++) {
                            if (Integer.parseInt(chunks[i]) <= ii && ii < Integer.parseInt(chunks[k])) {
                                finais[i + 1].put(ii, sorted.get(ii));
                                break tag;
                            }
                            i++;
                        }
                    }
                }
            }
        }

        for (int r = 0; r < numJCLThreads; r++) {
            if(!finais[r].isEmpty()) {
                JCL_map<Integer, Int2LongMap> h = new JCLHashMap<>(String.valueOf(r));
                h.put(id, finais[r]);
                long fT = 0;
                for (long kk : finais[r].values())
                    fT += kk;
                System.err.println(finais[r].size() + ":" + fT + " phase 2 putting in jcl - thread id " + r);
                finais[r].clear();
                finais[r] = null;
            }
        }
        sorted.clear();
        sorted = null;
        chunks = null;
    }

    public void phase3(int id) {
        Map<Integer, Int2LongMap> h = JCL_FacadeImpl.GetHashMap(String.valueOf(id));
        Int2LongMap result = new Int2LongOpenHashMap();
        for (Int2LongMap m : h.values()) {
            for (int i : m.keySet()) {
                if (result.containsKey(i)) {
                    long j = m.get(i) + result.get(i);
                    result.put(i, j);
                } else {
                    result.put(i, m.get(i));
                }
            }
            m.clear();
        }
        long freqT = 0;
        for (Long v : result.values())
            freqT += v;
        System.err.println("ID: " + id + " size: " + result.size() + " freqT: " + freqT);

        h.clear();
        h.put(id, result);
        // result.clear();
        // result = null;
    }

    public int phase4(String name, int JCLNumThreads) {
        try {
            IntSet values = new IntOpenHashSet();
            IntSet exist = new IntOpenHashSet();
            int numerrors = 0;
            try {
                for (int id = 0; id < JCLNumThreads; id++) {
                    File f = new File("../" + name + id + "/" + name + id + ".bin");
                    InputStream in = new BufferedInputStream(new FileInputStream(f));
                    byte[] i = new byte[4];

                    while (in.read(i) == 4) {
                        int k = java.nio.ByteBuffer.wrap(i).getInt();
                        values.add(k);
                    }
                    in.close();
                }

                for (int id = 0; id < JCLNumThreads; id++) {
                    Map<Integer, Int2LongMap> h = JCL_FacadeImpl.GetHashMap(String.valueOf(id));
                    for (Int2LongMap m : h.values()) {
                        exist.addAll(m.keySet());
                    }
                }

                for (int j : values)
                    if (!exist.contains(j))
                        numerrors++;

            } catch (Exception e) {
            }

            values.clear();
            exist.clear();
            values = null;
            exist = null;

            // return 0;
            return numerrors;
        } finally {
            for (int id = 0; id < JCLNumThreads; id++) {
                File f = new File("../" + name + id + "/");
                removeDirs(f);
            }
        }
    }
    
    public void clean(String name, Integer JCLNumThreads) {
        System.out.println("Cleaning data");
        for (int id = 0; id < JCLNumThreads; id++) {
            File f = new File("../" + name + id + "/");
            removeDirs(f);
        }
    }

    private boolean removeDirs(File directory) {

        if (directory == null)
            return false;
        if (!directory.exists())
            return true;
        if (!directory.isDirectory())
            return false;

        String[] list = directory.list();

        // Some JVMs return null for File.list() when the
        // directory is empty.
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                File entry = new File(directory, list[i]);

                if (entry.isDirectory()) {
                    if (!removeDirs(entry))
                        return false;
                } else {
                    if (!entry.delete())
                        return false;
                }
            }
        }

        return directory.delete();
    }

}
