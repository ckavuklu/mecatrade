package com.meca.trade.components;
import java.util.ArrayList;
import java.util.Collections;

import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;

@ComponentDescription("Stochastic Oscillator")
@OutPorts({
	@OutPort(value = "KLINE", description = "KLINE", type = Double.class),
	@OutPort(value = "DLINE", description = "DLINE", type = Double.class)})

@InPorts({
		@InPort(value = "N_WINDOW", description = "N window", type = Double.class),
		@InPort(value = "K_WINDOW", description = "Fast K smoothed with K-period SMA", type = Double.class),
		@InPort(value = "D_WINDOW", description = "D Period SMA of Full K", type = Double.class),
		@InPort(value = "HIGH", description = "Data", type = Double.class),
		@InPort(value = "LOW", description = "Data", type = Double.class),
		@InPort(value = "CLOSE", description = "Data", type = Double.class),
		
		
})
public class StochasticOscillator extends Indicator {

	static final String copyright = "";

	InputPort nWindowPort, kWindowPort,dWindowPort,highPort,lowPort,closePort;

	OutputPort outportKLine, outportDLine;

	private Integer nWindowSize = null;
	private Integer kWindowSize = null;
	private Integer dWindowSize = null;
	private Double kSmaResult = null;
	private Double dSmaResult = null;
	
	private ArrayList<Double> lWindow = null;
	private ArrayList<Double> kWindow = null;
	private ArrayList<Double> dWindow = null;

	
	@Override
	protected void execute() {

		Packet hDataPacket = null;
		Packet lDataPacket = null;
		Packet cDataPacket = null;
		Packet nWindowPacket = null;
		Packet kWindowPacket = null;
		Packet dWindowPacket = null;
		
	
		if (nWindowSize == null) {
			nWindowPacket = nWindowPort.receive();
			Double doubleValue = (Double) nWindowPacket.getContent();
			nWindowSize = new Integer(doubleValue.intValue());
			nWindowPort.close();
			drop(nWindowPacket);
		}
		
		if (kWindowSize == null) {
			kWindowPacket = kWindowPort.receive();
			Double doubleValue = (Double) kWindowPacket.getContent();
			kWindowSize = new Integer(doubleValue.intValue());
			lWindow = new ArrayList<Double>();
			kWindow = new ArrayList<Double>();
			dWindow = new ArrayList<Double>();
			kWindowPort.close();
			drop(kWindowPacket);
		}
		
		if (dWindowSize == null) {
			dWindowPacket = dWindowPort.receive();
			Double doubleValue = (Double) dWindowPacket.getContent();
			dWindowSize = new Integer(doubleValue.intValue());
			dWindowPort.close();
			drop(dWindowPacket);
		}

		while (((hDataPacket = highPort.receive()) != null) &&
				((lDataPacket = lowPort.receive()) != null) &&
				((cDataPacket = closePort.receive()) != null)) {

			Double hDat = (Double) hDataPacket.getContent();
			Double lDat = (Double) lDataPacket.getContent();
			Double cDat = (Double) cDataPacket.getContent();

			window.add(window.size(),hDat);
			lWindow.add(lWindow.size(),lDat);
			
			if(window.size() >= nWindowSize){
				Double low = Collections.min(lWindow);
				Double high = Collections.max(window);
				
				kWindow.add(kWindow.size(),100d * ((cDat - low)/(high - low)));
				
				if(kWindow.size() >= kWindowSize){
					kSmaResult = average(kWindow);
					kWindow.remove(0);
				}else{
					kSmaResult = Double.NaN;
				}
				
				dWindow.add(dWindow.size(),kSmaResult);
				
				if(dWindow.size() >= dWindowSize){
					dSmaResult = average(dWindow);
					dWindow.remove(0);
				}else{
					dSmaResult = Double.NaN;
				}
				
				window.remove(0);
				lWindow.remove(0);
				
				
			} else{
				dSmaResult = Double.NaN; 
				kSmaResult = Double.NaN;
			} 
			
			try {
				
				if(kSmaResult != null && outportKLine.isConnected()){
					outportKLine.send(create(kSmaResult));
				}
				

				if(dSmaResult != null && outportDLine.isConnected()){
					outportDLine.send(create(dSmaResult));
				}
				
				drop(hDataPacket);
				drop(lDataPacket);
				drop(cDataPacket);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		highPort.close();
		lowPort.close();
		closePort.close();
		
	}

	@Override
	protected void openPorts() {

		nWindowPort = openInput("N_WINDOW");
		kWindowPort = openInput("K_WINDOW");
		dWindowPort = openInput("D_WINDOW");
		
		highPort = openInput("HIGH");
		lowPort = openInput("LOW");
		closePort = openInput("CLOSE");
		
		outportKLine = openOutput("KLINE");
		outportDLine = openOutput("DLINE");

	}
	
	private Double average(ArrayList<Double> window){
		Double result = 0d;
		
		
		for(Double data:window){
			result += Double.valueOf(data); 
		} 
		
		return result/window.size();
	}
}
