package gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

public final class Ihm extends JFrame {

    private static final long serialVersionUID = 1L;

    public Ihm() {

        super("AIF Tools");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Compilation", new PanelCompil());
        tabbedPane.addTab("Divers", new PanelDivers());

        add(tabbedPane);

        pack();
        setMinimumSize(new Dimension(getWidth(), getHeight()));
        setResizable(false);
        setVisible(true);
        setLocationRelativeTo(null);

    }

    public static void main(String[] args) {

        try {

            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    try {
                        UIManager.setLookAndFeel(info.getClassName());
                    } catch (ClassNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (InstantiationException e1) {
                        e1.printStackTrace();
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    } catch (UnsupportedLookAndFeelException e1) {
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                new Ihm();
            }
        });

    }

}
