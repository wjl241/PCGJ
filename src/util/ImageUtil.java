package util;

import javax.swing.ImageIcon;

import weixin.Main;

public class ImageUtil {
	/**
	 * 根据图片名字获取图片
	 * @param name
	 * @return
	 */
	public static ImageIcon getImageIcon(String url) {
		   ImageIcon icon =
                   new ImageIcon(Main.class.getResource(url));
		return icon;
	}
}
