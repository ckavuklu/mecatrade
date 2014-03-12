package com.meca.trade.to;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

public class GraphDataGenerator extends FileReportGenerator {

	@Override
	public void initializeLogger(String name) {
		super.initializeLogger(name);
		writer.println("[");
	}

	private void generateGraphHTML() {

		Iterator<File> it = FileUtils.iterateFiles(new File(
				Constants.GRAPH_TEMPLATE_DIRECTORY), null, false);

		while (it.hasNext()) {
			File file = it.next();
			try {
				String content = FileUtils.readFileToString(file, "UTF-8");
				content = content.replace("$FILENAME", fileName);
				File tempFile = new File(Constants.OUTPUT_DIRECTORY
						+ File.separator + fileName + " - " + file.getName());
				FileUtils.writeStringToFile(tempFile, content, "UTF-8");
			} catch (IOException e) {
				// Simple exception handling, replace with what's necessary for
				// your use case!
				throw new RuntimeException("Generating file failed", e);
			}
		}

	}
	
	
	@Override
	public void finalizeLogger() {
		writer.println("]");
		super.finalizeLogger();
		
		generateGraphHTML();
		
	}

}
