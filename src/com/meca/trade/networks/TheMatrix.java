package com.meca.trade.networks;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.meca.trade.to.Constants;
import com.meca.trade.to.MarketDataGenerator;

public class TheMatrix {
	private HashMap<String, Parameter> runtimeParamMap;
	
	private MarketDataGenerator generator = null;
	private NewOptimizer optimizer = null;
	
	private Long runNumber = 0l;
	
	public void createRuntime() {

		SAXBuilder builder = new SAXBuilder();

		try {
			runtimeParamMap = new HashMap<String,Parameter>();
			
			Document runtimeDoc = builder.build(Constants.INPUT_DIRECTORY + File.separator + "MatrixRuntime.xml");

			Element configuration = runtimeDoc.getRootElement()
					.getChildren("configuration").get(0);
			Iterator itr = configuration.getChildren().iterator();

			while (itr.hasNext()) {
				Element elem = (Element) itr.next();
				String paramName = elem.getAttribute("name").getValue();
				runtimeParamMap.put(paramName,
						new Parameter(paramName, elem.getAttribute("type")
								.getValue(), elem.getAttribute("value")
								.getValue()));
			}

		} catch (Exception e) {

		}
	}
	
	private void createMarketDataGenerator(String sourceFileName, Date periodStart, Date periodEnd, Integer schedulePeriod, String schedule) throws IOException {
		
		/*
		sourceFileName = "ORG_" + (String)parameterMap.get("INPUT_MARKET_DATA_FILE_NAME").getValue();
		periodStart = (Date)parameterMap.get("PERIOD_START").getValue();
		periodEnd = (Date)parameterMap.get("PERIOD_END").getValue();
		schedulePeriod = (Integer)parameterMap.get("PERIOD_STEP_SIZE").getValue();
		schedule = (String)parameterMap.get("PERIOD_TYPE").getValue();
		*/
		
		generator = new MarketDataGenerator(runtimeParamMap);
	}
	
	public void optimize() throws Exception{
		
		optimizer = new NewOptimizer(runtimeParamMap);
		
		optimizer.output(optimizer.iterate());
		
		optimizer.reRunBestIndividuals();
		
		createRunReports();
		
	}
	
	
	private void createRunReports(){
		FileFilter filter = FileFilterUtils.notFileFilter(new NameFileFilter("Networks.xml"));
		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy-HHmmss-SSS");
		File newFile = new File( Constants.OUTPUT_DIRECTORY + File.separator + "Run-" + dateFormat.format( new Date() ) );
		newFile.mkdirs();

		File inputDirectory = new File(Constants.INPUT_DIRECTORY);
		File outputDirectory = new File(Constants.OUTPUT_DIRECTORY);
		
		try {
		    FileUtils.copyDirectory(inputDirectory, newFile, filter);
		  
		    for(File file:outputDirectory.listFiles()){
		    	if(!file.isDirectory())
		    		FileUtils.moveFileToDirectory(file, newFile, false);
		    }
		    
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		
		
	}

	public TheMatrix() {
		
		
		

	}
}
