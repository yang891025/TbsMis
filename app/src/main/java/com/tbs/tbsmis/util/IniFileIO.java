package com.tbs.tbsmis.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class IniFileIO {
	private final String filename;
	protected HashMap<String, Properties> sections = new HashMap<String, Properties>();
	private transient String section;
	private transient Properties properties;
	
	public IniFileIO(String filename) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(filename));
        this.read(reader);
		reader.close();
		this.filename = filename;
	}
	
	protected void read(BufferedReader reader) throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
            this.parseLine(line);
		}
	}

	private void parseLine(String line) {

		line = line.trim();
		if(line.startsWith(";")){
			return;
		}
		
		if (line.matches("\\[.*\\]") == true) {
            this.section = line.replaceFirst("\\[(.*)\\]", "$1");
            this.properties = new Properties();
            this.sections.put(this.section.toUpperCase(), this.properties);
		} else if (line.matches(".*=.*") == true) {
			if (this.properties != null) {
				int i = line.indexOf('=');
				String name = line.substring(0, i);
				String value = line.substring(i + 1);
				name = name.trim();
				value = value.trim();
                this.properties.setProperty(name.toUpperCase(), value);
			}
		}
	}

	public String getValue(String section, String name) {
		Properties p = this.sections.get(section.toUpperCase());

		if (p == null) {
			return null;
		}

		String value = p.getProperty(name.toUpperCase());
		return value;
	}
	
	public boolean putValue(String section, String name, String value){
		Properties p = this.sections.get(section.toUpperCase());
		if (p == null){
			p = new Properties();
            this.sections.put(section.toUpperCase(), p);
		}
		String val = p.getProperty(name.toUpperCase());
		if(val == null){
		}
		p.setProperty(name.toUpperCase(), value);
		return true;
	}
//	
//	public static boolean SetIniValue(String section,String name,String value){
//		String webRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
//		if (webRoot.endsWith("/") == false) {
//			webRoot += "/";
//		}
//		webRoot = webRoot +constants.SD_CARD_TBSWEB_PATH+ "/"
//				+ constants.WEB_CONFIG_FILE_NAME;
//		try {
//			IniFileIO iniReader = new IniFileIO(webRoot);
//			boolean Var = iniReader.putValue(section, name,value);
//			//iniReader.flush();
//			return Var;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//	
//	}
//	public static String GetIniValue(String section, String name){
//		String webRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
//		if (webRoot.endsWith("/") == false) {
//			webRoot += "/";
//		}
//		webRoot = webRoot +constants.SD_CARD_TBSWEB_PATH+ "/"
//				+ constants.WEB_CONFIG_FILE_NAME;
//		try {
//			IniFileIO iniReader = new IniFileIO(webRoot);
//			String Var = iniReader.getValue(section, name);
//			//iniReader.flush();
//			return Var;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return "error";
//		}
//	}
	
	public boolean flush() throws IOException{
		FileWriter fw = null;
		BufferedWriter bw = null;
		fw = new FileWriter(this.filename);
		bw = new BufferedWriter(fw);
		if(this.sections == null || this.sections.isEmpty()){
			bw.flush();
			bw.close();
			return true;
		}
		
		Set<Map.Entry<String, Properties>> entryset = this.sections.entrySet();
		for(Map.Entry<String, Properties> entry: entryset){
			//Write sections
			String strSection = entry.getKey();
			Properties p = this.sections.get(strSection);
			bw.write("[" +strSection +"]");
			bw.newLine();
			
			if(p == null || p.isEmpty()){
				continue;
			}
			//Write Properties
			for(Object obj: p.keySet()){
				String key = (String)obj;
				bw.write(key +"=");
				String value = p.getProperty(key);
				bw.write(value);
				bw.newLine();
			}
		}
		bw.flush();
		bw.close();
		return true;
	}
}