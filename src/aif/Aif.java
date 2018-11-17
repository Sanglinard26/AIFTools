package aif;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Kevin
 */
public final class Aif {

	private String name;
	private Traceur traceur;
	private List<Measure> datas;
	private int nbPoints = 0;

	public Aif(String name, Traceur traceur, List<Measure> datas) {

		this.name = name;
		this.traceur = traceur;
		this.datas = datas;

		for (Measure measure : this.datas) {
			this.nbPoints = Math.max(this.nbPoints, measure.getData().size());
		}
	}

	public Aif(File file, boolean removeInvalidPoints) {
		if (file != null) {

			this.name = file.getName().substring(0, file.getName().length() - 4);

			parse(file, removeInvalidPoints);
		}
	}

	private final void parse(File file, boolean removeInvalidPoints) {

		try(BufferedReader bf = new BufferedReader(new FileReader(file)))
		{

			String line, parsedValue;
			String[] splitTab;

			boolean startData = false;
			int cntLine = 0;
			
			Set<Integer> removePoint = new HashSet<Integer>();

			while ((line = bf.readLine()) != null) {

				if (line.startsWith("[Traceur]")) {

					this.traceur = new Traceur();

					while (!(line = bf.readLine()).startsWith("[Donnees]")) {
						splitTab = line.split("\t");
						if (splitTab.length == 3) {
							traceur.addItem(new Item(splitTab[0], splitTab[1], splitTab[2]));
						}
					}
					startData = true;
				}

				if (startData && !line.startsWith("[Donnees]")) {

					splitTab = line.split("\t");

					switch (cntLine) {
					case 0:
						this.datas = new ArrayList<Measure>(splitTab.length);

						for (String nameMeasure : splitTab) {
							this.datas.add(new Measure(nameMeasure));
						}
						break;
					case 1:
						if (splitTab.length == datas.size()) {
							for (int idxCol = 0; idxCol < splitTab.length; idxCol++) {
								this.datas.get(idxCol).setUnit(splitTab[idxCol]);
							}
						}
						break;

					default:
						if (splitTab.length == this.datas.size()) {
							
							for (int idxCol = 0; idxCol < splitTab.length; idxCol++) {
								parsedValue = splitTab[idxCol].trim().replace(',', '.');
								if(parsedValue.matches("[*]+"))
								{
									removePoint.add(this.datas.get(idxCol).getData().size());
									this.datas.get(idxCol).getData().add(String.valueOf(Double.NaN));
								}else{
									this.datas.get(idxCol).getData().add(parsedValue);
								}
								
							}
						}
						break;
					}

					cntLine++;

				}
			}
			
			
			System.out.println(removePoint);
			
			if(removeInvalidPoints)
			{
				final List<Integer> invalidPoints = new ArrayList<Integer>(removePoint);
				Collections.sort(invalidPoints);
				
				for (Measure measure : this.datas) {
					for(int numPoint = invalidPoints.size()-1; numPoint>=0; numPoint--)
					{
						int idxPoint = invalidPoints.get(numPoint).intValue();
						measure.getData().remove(idxPoint);
					}
					
					if(measure.getName().equals("LOGPT"))
					{
						for(int numPoint = 0; numPoint < measure.getData().size(); numPoint++)
						{
							measure.getData().set(numPoint, Integer.toString(numPoint+1));
						}
					}
				}
			}
			
			
			
			for (Measure measure : this.datas) {
				this.nbPoints = Math.max(this.nbPoints, measure.getData().size());
			}

			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public boolean equals(Object aif) {
		return this.name.equals(aif.toString());
	}

	/**
	 * Renvoie trois propri�t�s d'un Aif
	 * 
	 * @return Tableau de String : Nom, Nombre de ligne, Nombre de voie
	 */
	public final String[] getInfos() {
		return new String[] { this.name, Integer.toString(this.nbPoints), Integer.toString(this.datas.size()) };
	}

	public final Traceur getTraceur() {
		return this.traceur;
	}

	public final List<Measure> getMeasures() {
		return this.datas;
	}

	public final int getNbPoints() {
		return this.nbPoints;
	}

	/**
	 * @param listAif : Une liste d'Aif
	 * @return Un nouvel Aif avec la compilation des Aif entrants
	 */
	public static final Aif mergeAif(List<Aif> listAif, boolean alphaOrder) {
		final StringBuilder name = new StringBuilder();

		for (Aif aif : listAif) {
			name.append(aif);
			name.append("_");
		}

		final Traceur traceur = new Traceur();
		traceur.addItem(new Item("NOMS_AIF", "-", listAif.toString()));

		// Check des entetes
		final Map<String, Integer> entetes = new LinkedHashMap<String, Integer>();
		for (Aif aif : listAif) {
			for (Measure measure : aif.getMeasures()) {
				String nameMeasure = measure.getName();
				if (entetes.get(nameMeasure) == null) {
					entetes.put(nameMeasure, 1);
				} else {
					int cnt = entetes.get(nameMeasure).intValue();
					entetes.put(nameMeasure, ++cnt);
				}
			}
		}

		final int nbAif = listAif.size();
		final List<Measure> newMeasures = new ArrayList<Measure>();

		for (Entry<String, Integer> entry : entetes.entrySet()) {
			if (entry.getValue() == nbAif) {
				Measure newMeasure = new Measure(entry.getKey());
				newMeasures.add(newMeasure);
			}
		}

		int cntPoint = 1;
		for (Aif aif : listAif) {
			for (int i = -1; i < aif.getNbPoints(); i++) // -1 pour la boucle sur les unites
			{
				for (Measure measure : newMeasures) {
					int idxMeasure = aif.getMeasures().indexOf(measure);

					if (idxMeasure > -1) {
						Measure oldMeasure = aif.getMeasures().get(idxMeasure);

						if (measure.getUnit().length() == 0) {
							if (idxMeasure > -1) {
								measure.setUnit(oldMeasure.getUnit());
							}
						}

						if (i > -1) {
							if (!measure.getName().equals("LOGPT")) {
								measure.getData().add(oldMeasure.getData().get(i));
							} else {
								measure.getData().add(Integer.toString(cntPoint));
								cntPoint++;
							}
						}
					}
				}
			}
		}

		if (alphaOrder) {
			Collections.sort(newMeasures);
		}

		return new Aif(name.toString(), traceur, newMeasures);
	}

	public static void writeAif(File file, Aif aif) {
		PrintWriter printWriter = null;

		try {
			printWriter = new PrintWriter(file);

			printWriter.println("[Traceur]");
			for (Item item : aif.getTraceur().getListItems()) {
				printWriter.println(item.writeItem());
			}
			printWriter.println("[Donnees]");

			for (Measure measure : aif.getMeasures()) {
				printWriter.print(measure.getName() + "\t");
			}
			printWriter.print("\n");

			for (Measure measure : aif.getMeasures()) {
				printWriter.print(measure.getUnit() + "\t");
			}
			printWriter.print("\n");

			for (int nData = 0; nData < aif.getNbPoints(); nData++) {
				for (Measure measure : aif.getMeasures()) {
					if (nData < measure.getData().size()) {
						printWriter.print(measure.getData().get(nData) + "\t");
					} else {
						printWriter.print("NaN" + "\t");
					}
				}
				printWriter.print("\n");
			}

		} catch (Exception e) {
		} finally {
			if (printWriter != null)
				printWriter.close();
		}
	}

}
