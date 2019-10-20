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
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import aif.Aif;
import aif.Config;
import aif.Measure;
import utils.Utilitaire;

public final class PanelDivers extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final String FOLDER = "/icon_folder_32.png";
	private static final String SAVE = "/icon_save_32.png";
	private static final String CONFIG = "/icon_config_32.png";

	private static final GridBagConstraints gbc = new GridBagConstraints();

	private final DefaultListModel<Aif> dataModel;
	private MeasureModel measureModel;
	private Config config;

	public PanelDivers() {

		setLayout(new GridBagLayout());
		
		final JCheckBox checkConfig = new JCheckBox("Activation configuration");
		checkConfig.setHorizontalTextPosition(SwingConstants.LEFT);
		checkConfig.setEnabled(false);
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(5, 5, 0, 0);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(checkConfig, gbc);
		checkConfig.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if(config != null && checkConfig.isSelected())
				{
					markMeasureToWaste();
				}else{
					unMarkMeasureToWaste();
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
							if(config != null && config.getDatasets().size()>0 && checkConfig.isSelected())
							{
								markMeasureToWaste();
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
		final JList<Aif> listAif = new JList<Aif>(dataModel);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 3;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.insets = new Insets(5, 5, 0, 0);
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
		btConfig.setToolTipText("<html><p>Fichier *.ini avec la structure suivante :"
				+ "<p>[AIFTOOLS]"
				+ "<p>DATASET1"
				+ "<p>DATASET2"
				+ "<p>DATASET3"
				+ "<p>...");
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
					checkConfig.setSelected(true);
					if(dataModel.size()>0)
					{
						markMeasureToWaste();
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
				chooser.setDialogTitle("Sélection dossier");
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

		final TableMeasure tabMeasure = new TableMeasure();
		measureModel = (MeasureModel) tabMeasure.getModel();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(5, 5, 0, 0);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(new JScrollPane(tabMeasure), gbc);

	}

	private final void markMeasureToWaste(){
		Measure measureToWaste;
		for(int i = 0; i < dataModel.size(); i++)
		{
			for(String dataset : config.getDatasets())
			{
				measureToWaste = new Measure(dataset);
				int idx = dataModel.get(i).getMeasures().indexOf(measureToWaste);
				if(idx > -1)
				{
					dataModel.get(i).getMeasures().get(idx).setWasted(true);

				}
			}
		}
		measureModel.fireTableDataChanged();
	}

	private final void unMarkMeasureToWaste(){
		for(int i = 0; i < dataModel.size(); i++)
		{
			for(Measure measure : dataModel.get(i).getMeasures())
			{
				measure.setWasted(false);
			}
		}
		measureModel.fireTableDataChanged();
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
