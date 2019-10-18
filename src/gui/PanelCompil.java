/*
 * Creation : 18 oct. 2019
 */
package gui;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import aif.Aif;
import aif.TableModelAif;
import utils.Utilitaire;

public final class PanelCompil extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final String FOLDER = "/icon_folder_32.png";
    private static final String CORBEILLE = "/icon_corbeille_32.png";
    private static final String STACKS = "/icon_stack_32.png";

    private static final GridBagConstraints gbc = new GridBagConstraints();

    private JCheckBox chkOrdreAlpha, chkInvalidMeasure;
    private TableModelAif model;

    public PanelCompil() {

        setLayout(new GridBagLayout());

        final JButton btOpen = new JButton(new ImageIcon(getClass().getResource(FOLDER)));
        btOpen.setToolTipText("Ouvrir fichiers AIF");
        btOpen.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                final JFileChooser fc = new JFileChooser();
                fc.setMultiSelectionEnabled(true);
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fc.setFileFilter(new FileFilter() {

                    @Override
                    public String getDescription() {
                        return "Fichier AIF (*.aif)";
                    }

                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory()) {
                            return true;
                        }
                        return f.getName().toLowerCase().endsWith("aif");
                    }
                });
                final int reponse = fc.showOpenDialog(PanelCompil.this);

                if (reponse == JFileChooser.APPROVE_OPTION) {

                    final Finder finder = new Finder();
                    final List<File> selectedFilesToCompil = new ArrayList<File>();

                    for (File selFile : fc.getSelectedFiles()) {
                        if (selFile.isDirectory()) {
                            try {
                                Files.walkFileTree(selFile.toPath(), finder);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            selectedFilesToCompil.add(selFile);
                        }
                    }

                    selectedFilesToCompil.addAll(finder.getFilesToCompil());

                    final Aif[] tabAif = new Aif[selectedFilesToCompil.size()];
                    final boolean removeInvalidPoint = chkInvalidMeasure.isSelected();

                    final Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            for (int i = 0; i < selectedFilesToCompil.size(); i++) {
                                tabAif[i] = new Aif(selectedFilesToCompil.get(i), removeInvalidPoint);
                            }
                            model.addAif(tabAif);
                        }
                    });

                    thread.start();

                }
            }
        });
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 5, 0, 0);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(btOpen, gbc);

        final JButton btClear = new JButton(new ImageIcon(getClass().getResource(CORBEILLE)));
        btClear.setToolTipText("Effacer le tableau");
        btClear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                model.clearTable();
            }
        });
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 5, 0, 0);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(btClear, gbc);

        final JButton btCompil = new JButton(new ImageIcon(getClass().getResource(STACKS)));
        btCompil.setToolTipText("Compiler les fichiers");
        btCompil.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                final JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Enregistement du fichier");
                fileChooser.setFileFilter(new FileNameExtensionFilter("Fichier AIF (*.aif)", "aif"));
                fileChooser.setSelectedFile(new File(".aif"));

                final int rep = fileChooser.showSaveDialog(null);

                if (rep == JFileChooser.APPROVE_OPTION) {

                    final Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {

                            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                            final Aif newAif = Aif.mergeAif(model.getListAif(), chkOrdreAlpha.isSelected());
                            Aif.writeAif(fileChooser.getSelectedFile(), newAif);

                            setCursor(Cursor.getDefaultCursor());

                            JOptionPane.showMessageDialog(PanelCompil.this, "Compilation terminee !");
                        }
                    });
                    thread.start();
                }
            }
        });
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 5, 0, 0);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(btCompil, gbc);

        chkOrdreAlpha = new JCheckBox("Trier les voies par ordre alphabetique", false);
        chkOrdreAlpha.setToolTipText("Option decochee on garde l'ordre du premier fichier");
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 5, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        add(chkOrdreAlpha, gbc);

        chkInvalidMeasure = new JCheckBox("Supprimer les points invalides", false);
        chkInvalidMeasure.setToolTipText("Fonction utilisee pendant l'import de fichier");
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 5, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        add(chkInvalidMeasure, gbc);

        model = new TableModelAif();

        final JTable table = new JTable(model);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        add(new JScrollPane(table), gbc);

        table.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 127) {
                    model.removeAif(table.getSelectedRows());
                }
            }
        });

    }

    private static class Finder extends SimpleFileVisitor<Path> {

        private final List<File> filesToCompil;

        public Finder() {
            filesToCompil = new ArrayList<File>();
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (Utilitaire.getExtension(file.toFile()).equals("aif")) {
                filesToCompil.add(file.toFile());
            }
            return FileVisitResult.CONTINUE;
        }

        private final List<File> getFilesToCompil() {
            return filesToCompil;
        }
    }

}
