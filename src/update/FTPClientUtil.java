package update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;

public class FTPClientUtil {
	private static String localRootPath = null;
	private static String remoteRootPath = null;
	private static String hostName = null;
	private static int port = 21;
	private static String user = null;
	private static String pwd = null;
	private static FTPClient ftp = null;
	
	public FTPClientUtil(String hostName,int port,String user,String pwd,String remoteRootPath,String localRootPath) {
		this.localRootPath = localRootPath;
		this.remoteRootPath = remoteRootPath;
		this.hostName = hostName;
		this.port = port;
		this.user = user;
		this.pwd = pwd;
	}
	public void FTPClientRun() {
		getInstance();
		String path = remoteRootPath;
		traverse(hostName,ftp,path);
	}
	public FTPClient getInstance() {
		
		ftp = new FTPClient();

		try {
			ftp = new FTPClient();
			ftp.connect(hostName, port);
			ftp.login(user, pwd);
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");
			FTPClientConfig config = new FTPClientConfig(FTPClientConfig.SYST_NT);
			config.setServerLanguageCode("zh");

		} catch (IOException ex) {
			System.out.println("连接主机:" + hostName + "失败!");
		} catch (SecurityException ex) {
			System.out.println("用户或者密码可能不对，无权限与主机:" + hostName + "连接!");
		}
		return ftp;
	}

	// 遍历ftp站点资源信息,获取FTP服务器上制定目录的应用程序
	public void traverse(String host, FTPClient client, String path) {
		String prefix = "";
		try {
			client.changeWorkingDirectory(path);
			FTPFile[] files = client.listFiles(path);
			for (int i = 0; i < files.length; i++) {
				// 如果是文件夹就递归方法继续遍历
				if (files[i].isDirectory()) {
					/*
					 * 创建新目录时会自动创建两个文件名: . 和 .. 点指当前目录 点点指父目录
					 */
					// 注意这里的判断，否则会出现死循环
					if (!files[i].getName().equals(".") && !files[i].getName().equals("..")) {
						String tempDir = client.printWorkingDirectory() + "/" + files[i].getName();
						File localFile = new File(localRootPath + tempDir.replace(remoteRootPath, "/"));
						if (!localFile.exists()) {
							localFile.mkdirs();
						}
						client.changeWorkingDirectory(tempDir);
						// 是文件夹就递归调用
						traverse(host, client, tempDir);
						prefix += client.printWorkingDirectory();
						client.changeToParentDirectory();
					}
					// 如果是文件就扫描信息
				} else {
					String temp = client.printWorkingDirectory();
					String RemFileName = files[i].getName();
					String tempPath = temp.replaceAll(remoteRootPath, "")+"/" + files[i].getName();
					System.out.println("开始下载 ："+tempPath);
					String filepath = localRootPath + tempPath;
					try {
						File localFile = new File(filepath);
						if (!localFile.exists()) {
							localFile.createNewFile();
						}
						FileOutputStream out = new FileOutputStream(localFile);
						client.retrieveFile(files[i].getName(), out);
						out.flush();
						out.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void freeFTPClient() {
		try {
			if (ftp != null)
				ftp.disconnect();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
