package com.meca.trade.to;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class FileReportGenerator implements IReportLogger {

	PrintWriter writer = null;
	String fileName = null;
	
	public FileReportGenerator() {
		//for(File file: new File(Constants.OUTPUT_DIRECTORY).listFiles()) file.delete();
	}

	@Override
	public void initializeLogger(String name){
		try {
			writer = new PrintWriter(new File(Constants.OUTPUT_DIRECTORY + File.separator + name));
			fileName = name;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}


	@Override
	public void writeLog(String log){
		writer.println(log);
	}

	@Override
	public void finalizeLogger() {
		writer.close();
	}

}
