/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panel;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 *
 * @author jerry
 */
public class ColorScrollBarUI extends BasicScrollBarUI {

    /**
     * 滑道背景色
     */
    public Color ChangeBackColor;
    /**
     * 滑道把手色
     */
    public Color ChangeThumbColor;

    /**
     * 把手大小
     */
    public int ThumbSize;

    /**
     * 配置样式
     *
     * @param BackgroundColor 滑道背景色
     * @param ThumbColor 把手颜色
     * @param thumbSize 把手大小
     */
    public ColorScrollBarUI(Color BackgroundColor, Color ThumbColor, int thumbSize) {
        ChangeThumbColor = ThumbColor;
        ChangeBackColor = BackgroundColor;
        ThumbSize = thumbSize;
    }

    @Override
    protected void configureScrollBarColors() {
        scrollbar.setBackground(ChangeBackColor);
        //改变把手的大小
        scrollBarWidth = ThumbSize;
        // 把手
        thumbColor = ChangeThumbColor;
        //      thumbHighlightColor = Color.BLUE;
        //      thumbDarkShadowColor = Color.BLACK;
        //      thumbLightShadowColor = Color.YELLOW;
        // 滑道
        trackColor = ChangeBackColor;
//              trackHighlightColor = Color.GREEN;

    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        JButton button = new JButton();
        button.setVisible(false);
        return button;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        JButton button = new JButton();
        button.setVisible(false);
        return button;
    }
}
