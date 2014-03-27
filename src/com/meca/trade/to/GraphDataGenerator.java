package com.meca.trade.to;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

public class GraphDataGenerator extends FileReportGenerator {

	@Override
	public void writeLog(String log) {
		writer.print(Constants.GRAPH_DATA_JSON_START_STRING);
		writer.print(log);
		writer.print(Constants.GRAPH_DATA_JSON_END_STRING);
		writer.println(Constants.GRAPH_DATA_JSON_SEPARATOR_STRING);
	}

	@Override
	public void initializeLogger(String name) {
		super.initializeLogger(name);
		writer.println("[");
	}

	
	
	
	@Override
	public void finalizeLogger() {
		writer.println("]");
		super.finalizeLogger();
		
		
	}

}
