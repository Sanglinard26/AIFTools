package gui;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import aif.Aif;
import aif.TableModelAif;

import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;


public final class Ihm extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private static final String FOLDER = "/icon_folder_32.png";
	private static final String CORBEILLE = "/icon_corbeille_32.png";
	private static final String STACKS = "/icon_stack_32.png";
	
	private static final GridBagConstraints gbc = new GridBagConstraints();
	private TableModelAif model;

	public Ihm() {

		super("AIF Tools");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		final Container root = getContentPane();

		root.setLayout(new GridBagLayout());

		final JButton btOpen = new JButton(new ImageIcon(getClass().getResource(FOLDER)));
		btOpen.setToolTipText("Ouvrir fichiers AIF");
		btOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				final JFileChooser fc = new JFileChooser();
				fc.setMultiSelectionEnabled(true);
				fc.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
				fc.setFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						return "Fichier AIF (*.aif)";
					}

					@Override
					public boolean accept(File f) {
						if(f.isDirectory())
						{
							return true;
						}
						return f.getName().toLowerCase().endsWith("aif");
					}
				});
				final int reponse = fc.showOpenDialog(Ihm.this);

				if (reponse == JFileChooser.APPROVE_OPTION) {

					final File[] selectedFiles = fc.getSelectedFiles();
					final Aif[] tabAif = new Aif[selectedFiles.length];

					for(int i = 0; i < selectedFiles.length; i++)
					{
						tabAif[i] = new Aif(selectedFiles[i]);
					}
					model.addAif(tabAif);
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
		root.add(btOpen, gbc);

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
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(5, 5, 0, 0);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		root.add(btClear, gbc);

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

							final Aif newAif = Aif.mergeAif(model.getListAif());
							Aif.writeAif(fileChooser.getSelectedFile(), newAif);

							setCursor(Cursor.getDefaultCursor());

							JOptionPane.showMessageDialog(Ihm.this, "Compilation terminee !");
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
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(5, 5, 0, 0);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		root.add(btCompil, gbc);

		model = new TableModelAif();

		final JTable table = new JTable(model);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.CENTER;
		root.add(new JScrollPane(table), gbc);

		table.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 127)
				{
					model.removeAif(table.getSelectedRows());
				}
			}
		});

		pack();
		setMinimumSize(new Dimension(getWidth(), getHeight()));

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

				final Ihm ihm = new Ihm();
				ihm.setVisible(true);
			}
		});

	}

}
