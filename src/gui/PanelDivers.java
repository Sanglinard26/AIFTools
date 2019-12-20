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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellEditor;

import aif.Aif;
import aif.Config;
import aif.Measure;
import utils.Utilitaire;

public final class PanelDivers extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final String FOLDER = "/icon_folder_32.png";
    private static final String CORBEILLE = "/icon_corbeille_32.png";
    private static final String SAVE = "/icon_save_32.png";
    private static final String CONFIG = "/icon_config_32.png";

    private static final GridBagConstraints gbc = new GridBagConstraints();

    private final DefaultListModel<Aif> dataModel;
    private MeasureModel measureModel;
    private Config config;

    final JList<Aif> listAif;
    private final JCheckBox checkApply;
    private String measureToRename = null;

    public PanelDivers() {

        setLayout(new GridBagLayout());

        checkApply = new JCheckBox("Appliquer les changements sur tous les AIF");
        checkApply.setHorizontalTextPosition(SwingConstants.LEFT);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 5, 0, 0);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(checkApply, gbc);

        final JCheckBox checkConfig = new JCheckBox("Activation configuration");
        checkConfig.setHorizontalTextPosition(SwingConstants.LEFT);
        checkConfig.setEnabled(false);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 5, 0, 0);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(checkConfig, gbc);
        checkConfig.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (config != null && checkConfig.isSelected()) {
                    markMeasureToWaste(config.getDatasets());
                } else {
                    unMarkMeasureToWaste(null);
                }

            }
        });

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
                final int reponse = fc.showOpenDialog(PanelDivers.this);

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

                    final Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            for (int i = 0; i < selectedFilesToCompil.size(); i++) {
                                tabAif[i] = new Aif(selectedFilesToCompil.get(i), false);
                                dataModel.addElement(tabAif[i]);
                            }
                            if (config != null && config.getDatasets().size() > 0 && checkConfig.isSelected()) {
                                markMeasureToWaste(config.getDatasets());
                            }
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
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 5, 0, 0);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(btOpen, gbc);

        dataModel = new DefaultListModel<Aif>();
        listAif = new JList<Aif>(dataModel);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 4;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 5, 0, 5);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(new JScrollPane(listAif), gbc);
        listAif.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {

                    measureModel.clearList();

                    for (Measure measure : listAif.getSelectedValue().getMeasures()) {
                        measureModel.addElement(measure);
                    }
                }

            }
        });

        final JButton btConfig = new JButton(new ImageIcon(getClass().getResource(CONFIG)));
        btConfig.setToolTipText(
                "<html><p>Fichier *.ini avec la structure suivante :" + "<p>[AIFTOOLS]" + "<p>DATASET1" + "<p>DATASET2" + "<p>DATASET3" + "<p>...");
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 5, 0, 0);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(btConfig, gbc);
        btConfig.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                final JFileChooser fc = new JFileChooser();
                fc.setMultiSelectionEnabled(true);
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fc.setFileFilter(new FileFilter() {

                    @Override
                    public String getDescription() {
                        return "Fichier de config (*.ini)";
                    }

                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory()) {
                            return true;
                        }
                        return f.getName().toLowerCase().endsWith("ini");
                    }
                });

                if (fc.showOpenDialog(PanelDivers.this) == JFileChooser.APPROVE_OPTION) {
                    config = new Config(fc.getSelectedFile());
                    checkConfig.setEnabled(true);
                    if (listAif.getSelectedIndex() > -1) {
                        checkConfig.setSelected(true);
                    }
                }
            }
        });

        final JButton btSave = new JButton(new ImageIcon(getClass().getResource(SAVE)));
        btSave.setToolTipText("Enregistrer les fichiers AIF de la liste");
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 5, 0, 0);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(btSave, gbc);
        btSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                final JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Selection dossier");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                if (chooser.showSaveDialog(PanelDivers.this) == JFileChooser.APPROVE_OPTION) {
                    final Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {

                            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                            for (int i = 0; i < dataModel.size(); i++) {
                                dataModel.get(i).removeWasteMeasure();
                                Aif.writeAif(new File(chooser.getSelectedFile() + "\\" + dataModel.get(i).toString() + "_new.aif"), dataModel.get(i));
                            }

                            setCursor(Cursor.getDefaultCursor());

                            JOptionPane.showMessageDialog(PanelDivers.this, "Enregistrement termine !");
                        }
                    });
                    thread.start();
                }

            }
        });

        final JButton btClear = new JButton(new ImageIcon(getClass().getResource(CORBEILLE)));
        btClear.setToolTipText("Effacer le tableau");
        btClear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dataModel.clear();
            }
        });
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 5, 0, 0);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(btClear, gbc);

        final TableMeasure tabMeasure = new TableMeasure();

        tabMeasure.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tabMeasure.rowAtPoint(e.getPoint());
                int col = tabMeasure.columnAtPoint(e.getPoint());

                if (col == 1) {
                    Measure theMeasure = (Measure) measureModel.getValueAt(row, 0);
                    List<String> dataset = new ArrayList<>();
                    dataset.add(theMeasure.getName());

                    if (theMeasure.getWasted()) {
                        markMeasureToWaste(dataset);
                    } else {
                        unMarkMeasureToWaste(theMeasure);
                    }
                } else {
                    measureToRename = measureModel.getValueAt(row, 0).toString();
                }

            }
        });
        measureModel = (MeasureModel) tabMeasure.getModel();

        final TableCellEditor cellEditor = tabMeasure.getDefaultEditor(String.class);

        cellEditor.addCellEditorListener(new CellEditorListener() {

            @Override
            public void editingStopped(ChangeEvent e) {

                if (listAif.getSelectedIndex() == -1) {
                    return;
                }

                String newName = cellEditor.getCellEditorValue().toString();
                Measure renamedMeasure = new Measure(measureToRename);

                int begin = checkApply.isSelected() ? 0 : listAif.getSelectedIndex();
                int end = checkApply.isSelected() ? dataModel.size() : listAif.getSelectedIndex() + 1;

                for (int i = begin; i < end; i++) {

                    int idx = dataModel.get(i).getMeasures().indexOf(renamedMeasure);
                    if (idx > -1) {
                        dataModel.get(i).getMeasures().get(idx).setName(newName);

                    }
                }
                measureModel.fireTableDataChanged();
                measureToRename = null;

            }

            @Override
            public void editingCanceled(ChangeEvent e) {
                // TODO Auto-generated method stub

            }
        });

        PanRename panRename = new PanRename();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 5, 0, 5);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(panRename, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 1;
        gbc.insets = new Insets(5, 5, 0, 5);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(new JScrollPane(tabMeasure), gbc);

    }

    private final void markMeasureToWaste(List<String> datasets) {

        Measure measureToWaste;

        if (listAif.getSelectedIndex() == -1) {
            return;
        }

        int begin = checkApply.isSelected() ? 0 : listAif.getSelectedIndex();
        int end = checkApply.isSelected() ? dataModel.size() : listAif.getSelectedIndex() + 1;

        for (int i = begin; i < end; i++) {
            for (String dataset : datasets) {
                measureToWaste = new Measure(dataset);
                int idx = dataModel.get(i).getMeasures().indexOf(measureToWaste);
                if (idx > -1) {
                    dataModel.get(i).getMeasures().get(idx).setWasted(true);

                }
            }
        }
        measureModel.fireTableDataChanged();
    }

    private final void unMarkMeasureToWaste(Measure theMeasure) {

        if (listAif.getSelectedIndex() == -1) {
            return;
        }

        int begin = checkApply.isSelected() ? 0 : listAif.getSelectedIndex();
        int end = checkApply.isSelected() ? dataModel.size() : listAif.getSelectedIndex() + 1;

        for (int i = begin; i < end; i++) {
            if (theMeasure != null) {
                int idx = dataModel.get(i).getMeasures().indexOf(theMeasure);
                if (idx > -1) {
                    dataModel.get(i).getMeasures().get(idx).setWasted(false);
                }
            } else {
                for (Measure measure : dataModel.get(i).getMeasures()) {
                    measure.setWasted(false);
                }
            }

        }
        measureModel.fireTableDataChanged();
    }

    private final class PanRename extends JPanel {

        private static final long serialVersionUID = 1L;

        private static final String RENAME = "/icon_rename_32.png";

        final GridBagConstraints gbc = new GridBagConstraints();

        private final JLabel labelSearch;
        private final JLabel labelReplace;
        private final JTextField txtSearch;
        private final JTextField txtReplace;
        private final JButton btRename;

        public PanRename() {

            setLayout(new GridBagLayout());

            labelSearch = new JLabel("Texte à remplacer");
            gbc.fill = GridBagConstraints.NONE;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.weightx = 0;
            gbc.weighty = 0;
            gbc.insets = new Insets(5, 5, 0, 0);
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            add(labelSearch, gbc);

            labelReplace = new JLabel("Texte de remplacement");
            gbc.fill = GridBagConstraints.NONE;
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.weightx = 0;
            gbc.weighty = 0;
            gbc.insets = new Insets(5, 5, 0, 0);
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            add(labelReplace, gbc);

            txtSearch = new JTextField(10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.weightx = 0;
            gbc.weighty = 0;
            gbc.insets = new Insets(5, 5, 0, 0);
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            add(txtSearch, gbc);

            txtReplace = new JTextField(10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.weightx = 0;
            gbc.weighty = 0;
            gbc.insets = new Insets(5, 5, 0, 0);
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            add(txtReplace, gbc);

            btRename = new JButton(new ImageIcon(getClass().getResource(RENAME)));
            btRename.setToolTipText("Lancer le renommage des voies");
            gbc.fill = GridBagConstraints.NONE;
            gbc.gridx = 2;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 2;
            gbc.weightx = 1;
            gbc.weighty = 0;
            gbc.insets = new Insets(5, 5, 0, 0);
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            add(btRename, gbc);
            btRename.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    renameDataset();
                }
            });

        }

        private final void renameDataset() {

            String txtToSearch = txtSearch.getText().trim();

            if (!txtToSearch.isEmpty()) {
                String txtToReplace = txtReplace.getText().trim();

                if (listAif.getSelectedIndex() == -1) {
                    return;
                }

                int begin = checkApply.isSelected() ? 0 : listAif.getSelectedIndex();
                int end = checkApply.isSelected() ? dataModel.size() : listAif.getSelectedIndex() + 1;

                for (int i = begin; i < end; i++) {

                    for (Measure measure : dataModel.get(i).getMeasures()) {
                        String name = measure.getName();
                        if (name.contains(txtToSearch)) {
                            measure.setName(name.replace(txtToSearch, txtToReplace));
                        }
                    }
                }
                measureModel.fireTableDataChanged();
            }
        }

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
