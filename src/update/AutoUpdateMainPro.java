package update;

public class AutoUpdateMainPro {

	private FTPClientUtil ftpClient = null;

	public AutoUpdateMainPro(String hostName, int port, String user, String pwd, String remoteRootPath, String localRootPath) {
		
		ftpClient = new FTPClientUtil(hostName, port, user, pwd, remoteRootPath, localRootPath);
		execute();

	}
	private void execute() {

		ftpClient.FTPClientRun();
		// 释放程序占用的资源
		freeResource();

	}
	private void freeResource() {
		ftpClient.freeFTPClient();
	}


}
