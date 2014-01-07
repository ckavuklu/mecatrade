package com.meca.trade.to;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class FileGraphDataGenerator implements IReportLogger {

	PrintWriter writer = null;
	
	public FileGraphDataGenerator() {
		for(File file: new File("output").listFiles()) file.delete();
	}

	@Override
	public void initializeLogger(String name){
		try {
			writer = new PrintWriter(new File("output/" + name));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void writeGraphLog(PriceData priceData,Double equity, Double margin,Double freeMargin,Double marginLevel,Double openPL) {
		
		StringBuilder log = new StringBuilder();
		
		log.append(TradeUtils.convertStringDate(priceData.getTime()));
		log.append(Constants.SEPARATOR);
		log.append(TradeUtils.convertStringTime(priceData.getTime()));
		log.append(Constants.SEPARATOR);
		log.append(priceData.getOpen().toString());
		log.append(Constants.SEPARATOR);
		log.append(priceData.getHigh().toString());
		log.append(Constants.SEPARATOR);
		log.append(priceData.getLow().toString());
		log.append(Constants.SEPARATOR);
		log.append(priceData.getClose().toString());
		log.append(Constants.SEPARATOR);
		log.append(equity.toString());
	
		writer.println(log);

	}

	@Override
	public void finalizeLogger() {
		writer.close();
	}

	@Override
	public void writeStringLog(String log){
		writer.println(log);
	}
}
