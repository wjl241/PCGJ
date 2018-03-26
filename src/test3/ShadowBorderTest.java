package test3;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Arrays;

public class ShadowBorderTest extends JPanel {

  public static void main(String[] args) {

    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {

        JFrame frame = new JFrame("Test ShadowBorder");

        frame.setContentPane(new ShadowBorderTest());

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
      }
    });
  }

  ShadowBorderTest() {

    super(new GridLayout(2, 2, 20, 20));

    super.setBackground(Color.GRAY.brighter());

    JPanel p1 = new JPanel();
    p1.setBorder(BorderFactory.createCompoundBorder(

            ShadowBorder.newInstance(),
            BorderFactory.createLineBorder(Color.WHITE)
    ));

    JPanel p2 = new JPanel();
    p2.setBorder(BorderFactory.createCompoundBorder(

            ShadowBorder.newBuilder().shadowSize(3).center().build(),
            BorderFactory.createLineBorder(Color.WHITE)
    ));

    JPanel p3 = new JPanel();
    p3.setBorder(BorderFactory.createCompoundBorder(

            ShadowBorder.newBuilder().shadowColor(Color.ORANGE).build(),
            BorderFactory.createLineBorder(Color.WHITE)
    ));

    JPanel p4 = new JPanel();
    p4.setBorder(BorderFactory.createCompoundBorder(

            ShadowBorder.newBuilder().shadowAlpha(0.7f).top().build(),
            BorderFactory.createLineBorder(Color.WHITE)
    ));

    Dimension d = new Dimension(150, 150);
    for (JPanel p : Arrays.asList(p1, p2, p3, p4)) {

      p.setPreferredSize(d);
      add(p);
    }

    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
  }
}
