package implementations.util;

import java.io.File;

public class DirCreation {
	
	public static void createDirs(String dir){
		try{
		  File directory = new File(dir);
		  removeDirs(directory);
		  directory.mkdir();	
		}catch (Exception e){
			System.err.println("Cannot create dir with path: " + dir);
		}
	}
	
	private static boolean removeDirs(File directory) {

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

		      if (entry.isDirectory()){
		        if (!removeDirs(entry))
		          return false;
		      }else{
		        if (!entry.delete())
		          return false;
		      }
		    }
		  }

		  return directory.delete();
	}

}
