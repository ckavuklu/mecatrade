package com.meca.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class FileSmooth {
	BufferedReader inpReader = null;
	BufferedWriter outWriter = null;
	Date dateIndex = null;

	public FileSmooth(String inputFileName, String outputFileName) throws IOException {
		inpReader = new BufferedReader(new FileReader(inputFileName));
		outWriter = new BufferedWriter(new FileWriter(outputFileName));
	}

	public void populateOutput() throws IOException{
		String currentLine = null;
		String previousLine = null;
		
		while( (currentLine = inpReader.readLine()) != null){
			if(StringUtils.isNotEmpty(previousLine) && StringUtils.isNotEmpty(currentLine)){
				String[] lineDetails = currentLine.split(",");

				
				String lineDate = lineDetails[1];
				String lineTime = lineDetails[2];
				
				
			}
			
			
			previousLine = currentLine;
		}
	}
	
	private void incrementDate(){
		
		Calendar date = Calendar.getInstance();
		date.setTime(dateIndex);
		date.add(Calendar.MINUTE, 2);
		
		dateIndex = date.getTime();
	}
	
	
}
