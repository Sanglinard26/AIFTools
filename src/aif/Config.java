package aif;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Config {
	
	private static final String HEADER = "[AIFTOOLS]";
	
	private final List<String> datasets;
	
	public Config(File file) {
		datasets = new ArrayList<String>();
		parse(file);
	}
	
	private final void parse(File file)
	{
		try (BufferedReader bf = new BufferedReader(new FileReader(file))) {
			
			String line = bf.readLine();
			
			if(!HEADER.equals(line))
			{
				return;
			}
			
			while ((line = bf.readLine()) != null) {
				datasets.add(line.trim());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public final List<String> getDatasets()
	{
		return datasets;
	}

}
