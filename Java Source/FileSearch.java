import java.awt.FileDialog;
import java.awt.Frame;
import java.io.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;

class FileSearch 
{
	public static ArrayList<String> filePaths = new ArrayList<>();
	
    public void findFile(String name, File file) {
        File[] list = file.listFiles();
        
        if(list!=null) {
	        for (File fil : list) {
	            if (fil.isDirectory()) {
	                findFile(name,fil);
	            }
	            else if (fil.getName().matches(name)) {
	            	filePaths.add(fil.getAbsolutePath());
	            }
	        }
        }
    }
    
    public static ArrayList<String> getPath() throws IOException 
    {
        FileSearch ff = new FileSearch();
        String file = "";
        
        try {
        	FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
            dialog.setMode(FileDialog.LOAD);
            dialog.setVisible(true);
            file = dialog.getDirectory();
            //System.out.println(file + " chosen.");
        }catch(Exception e) {
        	JOptionPane.showMessageDialog(null, "Error: Please choose a valid directory");
        	getPath();
        }
        try {
        	ff.findFile(".*html", new File(file));
        }catch(Exception e) {
        	
        }

		Search.loadSpellChecker();
		Search.loadReducer(filePaths);
        return filePaths;
        
    }
}