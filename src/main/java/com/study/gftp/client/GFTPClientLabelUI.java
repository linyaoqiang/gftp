package com.study.gftp.client;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicLabelUI;

/**
 * 服务器文件信息展示的LabelUI修饰器
 */
public class GFTPClientLabelUI extends BasicLabelUI {
	@Override
	public void paint(Graphics g, JComponent c) {
		g.setColor(Color.LIGHT_GRAY);
		int y = (int) c.getSize().getHeight();
		int x = (int) c.getSize().getWidth();
		g.drawLine(0, 0, 0, y-1);
		g.drawLine(0, 0, x-1,0);
		g.drawLine(0, y - 1, x - 1, y - 1);
		g.drawLine(x - 1, 0, x - 1, y - 1);
		super.paint(g, c);
	}
}
