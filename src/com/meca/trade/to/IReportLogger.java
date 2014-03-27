package com.meca.trade.to;

public interface IReportLogger extends ILogger{
	
	public void writeLog(String log);
	public String getFileName();
	
}
