package update;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class HisDataProssorHandler {
	
	public static void closeHisDataProssor(){
		Process process = null;
		String cmdStr = "taskkill /f /im java(hzcy).exe";
		try {
			process = Runtime.getRuntime().exec(cmdStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void openHisDataProssor(){
		String startCmd = null;
		String root = null;
		try {
			File file = new File(Const.startConfig);
			root = file.getParentFile().getAbsolutePath();
			DataInputStream dIn = new DataInputStream(new FileInputStream(file));
				startCmd = dIn.readLine();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Process process = null;
		String cmdStr[] = startCmd.split(";");
		
		try {
			
			for(String cmd:cmdStr){
				System.out.println("启动程序"+cmd);
				//进入root目录
				int last = cmd.lastIndexOf("/");
				String runStr = "start /min "+root+cmd.substring(0,last);
				File workFile = new File(root+"/"+cmd.substring(0,last));
				String[] cmds = new String[5];
				cmds[0] = "cmd.exe";
				cmds[1] = "/c";
				cmds[2] = "start";
				cmds[3] = "/min";
				cmds[4] = cmd.substring(last+1);
				process = Runtime.getRuntime().exec(cmds,null,workFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		closeHisDataProssor();
	}
}
