package update;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AutoUpdateMain {
	
	
	private static String getVersionByFile(String path) {
		String version = null;
		File file = new File(path);
		if(!file.exists()){
			file.getParentFile().mkdirs();
			return null;
		}
		try {
			FileInputStream in = new FileInputStream(file);
			DataInputStream dIn = new DataInputStream(in);
			while (dIn.read() > 0)
				version = dIn.readLine();
			dIn.close();
			in.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return version;
	}
	public static void main(String[] args) {
		System.out.println("自动升级程序启动...");
		// 关闭正在运行的Java应用程序
		HisDataProssorHandler.closeHisDataProssor();
		String oldVersion = getVersionByFile(Const.versionPath);
		String startfile = getVersionByFile(Const.startConfig);
		System.out.print("搜索FTP上版本控制文件:");
		new AutoUpdateMainPro(Const.hostName, Const.port, Const.user, Const.pwd, Const.RemoteVersion, Const.LocalVersion);
		String newVersion = getVersionByFile(Const.versionPath);
		try {
			System.out.println("正在进行版本文件比较...");
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (startfile!=null&&oldVersion != null&&newVersion!=null) {
			if (!newVersion.equals(oldVersion)) {
				//通过指以COMPLETE+日期为指定命令，来更新全部程序
				if(newVersion.toUpperCase().startsWith("complete".toUpperCase())){
					System.out.println("检测到数据采集程序版本有更新，启动自动升级程序");
					try {
						System.out.println("正在进行版本文件比较...");
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					String RemoteUpdatePath = "/update/HisData";
					String LocalUpdateLocal = "../";
					new AutoUpdateMainPro(Const.hostName, Const.port, Const.user, Const.pwd, RemoteUpdatePath, LocalUpdateLocal);
				}else{
					//自动更新程序默认只更新lib文件夹下jar包
					System.out.println("检测到数据采集程序版本有更新，启动自动升级程序");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					String RemoteUpdatePath = "/update/HisData/lib";
					String LocalUpdateLocal = "../lib";
					new AutoUpdateMainPro(Const.hostName, Const.port, Const.user, Const.pwd, RemoteUpdatePath, LocalUpdateLocal);
				}
			} else {
				System.out.println("数据采集程序版本没有更新变动");
			}
		} else {
			//版本控制文件被破坏，自动更新程序下载最新程序，并修复版本控制文件
			System.out.println("本地数据采集程序被破坏，自动下载最新程序，并修复损坏的程序文件");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String RemoteUpdatePath = "/update/HisData";
			String LocalUpdateLocal = "D:\\ftp";
			new AutoUpdateMainPro(Const.hostName, Const.port, Const.user, Const.pwd, RemoteUpdatePath, LocalUpdateLocal);
		}
			try {
				System.out.println("更新运行完毕！启动数据采集程序退出...");
				Thread.sleep(3000);
				// 启动关闭的Java应用程序
				System.out.println("启动关闭的Java应用程序");
				HisDataProssorHandler.openHisDataProssor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
}
