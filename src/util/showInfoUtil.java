package util;

import showDialog.InfoDialog;

public class showInfoUtil {
	/**
	 * 提示框，不能超过19个中文字
	 * @param info
	 */
	public static void showInfoDialog(String info) {
		InfoDialog infoDialog = new InfoDialog(info);
		infoDialog.setVisible(true);
	}
}
