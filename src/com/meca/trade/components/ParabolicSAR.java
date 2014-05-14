package com.meca.trade.components;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.TradeUtils;

/** Parabolic SAR **/
@ComponentDescription("Parabolic SAR")
@OutPort(value = "SAR", arrayPort = true)
//@OutPort(value = "SAR", description = "Output port", type = Double.class)
/*
 @OutPorts({
	@OutPort(value = "SAR", description = "SAR VALUE", type = Double.class),
	@OutPort(value = "SARCHANGERATE", description = "SAR CHANGE RATE", type = Double.class)
})
*/
@InPorts({
		@InPort(value = "HIGH", description = "High Data", type = Double.class),
		@InPort(value = "LOW", description = "Low Data", type = Double.class),
		@InPort(value = "STEP", description = "Step Size", type = Double.class),
		@InPort(value = "MAXSTEP", description = "Max Step Size", type = Double.class)
})
public class ParabolicSAR extends Indicator {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort stepPort, maxStepPort, highDataPort, lowDataPort;

	//OutputPort outport, changeRatePort;
	
	OutputPort[] outport;
	
	private Double stepSize = null;
	private Double maxStepSize = null;
	
	private Double result = null;
	//private Double changeRate = null;
	
	private Integer previousSARNo = null;
	private Integer currentSARNo = null;
	private Integer initTrendDirection = 1;
	
	private Double tentativeSAR = null;
	private Double calculatedSAR = null;
	
	private Double previousSAR = null;
	private Double currentSAR = null;

	private Double previousEP = null;
	private Double currentEP = null;

	private Double previousAF = null;
	private Double currentAF = null;
	
	private Double twoPreviousHIGHEST = null;
	private Double twoPreviousLOWEST = null;

	private Double previousHIGH = null;
	private Double currentHIGH = null;

	private Double previousLOW = null;
	private Double currentLOW = null;
	
	
	@Override
	protected void execute() {

		Packet highDataPacket = null;
		Packet lowDataPacket = null;
		Packet stepPacket = null;
		Packet maxStepPacket = null;
		
		if (stepSize == null) {
			stepPacket = stepPort.receive();
			Double doubleValue = (Double) stepPacket.getContent();
			stepSize = doubleValue;
			stepPort.close();
			drop(stepPacket);
		}
		
		if (maxStepSize == null) {
			maxStepPacket = maxStepPort.receive();
			Double doubleValue = (Double) maxStepPacket.getContent();
			maxStepSize = doubleValue;
			maxStepPort.close();
			drop(maxStepPacket);
		}
		
		while ((highDataPacket = highDataPort.receive()) != null) {

			lowDataPacket = lowDataPort.receive();
			
			Double hDat = (Double) highDataPacket.getContent();
			Double lDat = (Double) lowDataPacket.getContent();

			if (hDat.isNaN()) {
				result = Double.NaN;
			} else {
				
				currentHIGH = hDat;
				currentLOW = lDat;
				
				if(previousHIGH == null || previousLOW == null) { 
					// ilk data paketi
					
					// cikis
					result = Double.NaN;
					previousHIGH = currentHIGH;
					previousLOW = currentLOW;
				}
				
				else if (previousSARNo == null) {
					// ikinci data paketi 
					
					currentSARNo = (initTrendDirection>0?1:-1);
										
					if(currentSARNo < 0) {
						currentSAR = previousHIGH;
						currentEP = (currentSARNo == -1?currentLOW:Math.min(currentLOW,previousEP));
					} else {
						currentSAR = previousLOW;
						currentEP = (currentSARNo == 1?currentHIGH:Math.max(currentHIGH,previousEP));
					}
					
					if(Math.abs(currentSARNo)==1) 
						currentAF = stepSize;
					else 
						currentAF = (previousEP.compareTo(currentEP) == 0)?previousAF:Math.min(maxStepSize, previousAF + stepSize);
							
		
					// cikis
					
					result = currentSAR;
					twoPreviousHIGHEST = Math.max(previousHIGH, currentHIGH);
					twoPreviousLOWEST = Math.min(previousLOW, currentLOW);
					previousHIGH = currentHIGH;
					previousLOW = currentLOW;
					previousSARNo = currentSARNo;
					previousSAR = currentSAR;
					previousEP = currentEP;
					previousAF = currentAF;
					
				}
				
				else if (previousSARNo != null ){
					// for all the rest packets 
					
					// calculate tentative SAR 
					if(previousSARNo<0)
						tentativeSAR = Math.max(previousSAR+previousAF*(previousEP-previousSAR),twoPreviousHIGHEST);
					else 
						tentativeSAR = Math.min(previousSAR+previousAF*(previousEP-previousSAR), twoPreviousLOWEST);
					
					//calculated SAR
					calculatedSAR =  previousSAR+previousAF*(previousEP-previousSAR);
					
					// SARNo
					if(previousSARNo<0)
						currentSARNo = tentativeSAR<currentHIGH?1:previousSARNo-1; 
					else
						currentSARNo = tentativeSAR>currentLOW?-1:previousSARNo+1;
						
					// SAR
					if(currentSARNo == -1) 
						currentSAR = Math.max(previousEP, currentHIGH);
					else if(currentSARNo == 1)
						currentSAR = Math.min(previousEP, currentLOW);
					else currentSAR = tentativeSAR;
					
					// EP
					if(currentSARNo < 0) 
						currentEP = (currentSARNo == -1) ?  currentLOW : Math.min(previousEP, currentLOW) ; 
					else
						currentEP = (currentSARNo == 1) ?  currentHIGH : Math.max(previousEP, currentHIGH) ; 
					
					
					// AF
					if(Math.abs(currentSARNo)==1) 
						currentAF = stepSize;
					else 
						currentAF = (previousEP.compareTo(currentEP) == 0)?previousAF:Math.min(maxStepSize, previousAF + stepSize);
					
					// cikis
					//changeRate = ((currentSAR-previousSAR)/currentSAR)*10000d;
					result = currentSAR;
					twoPreviousHIGHEST = Math.max(previousHIGH, currentHIGH);
					twoPreviousLOWEST = Math.min(previousLOW, currentLOW);
					previousHIGH = currentHIGH;
					previousLOW = currentLOW;
					previousSARNo = currentSARNo;
					previousSAR = currentSAR;
					previousEP = currentEP;
					previousAF = currentAF;
					
				}
				
			}
			
			try {
				
				if(result != null){
					for(int i=0;i<outport.length;i++){
						
						if (outport[i].isConnected()) {
							outport[i].send(create(result));
						} 
					}
				}
				
				/*
				if(result != null && outport.isConnected() && changeRatePort.isConnected()){
					outport.send(create(result));
					
					
					if(changeRate == null)
						changeRate = Double.NaN;
					
					changeRatePort.send(create(changeRate));
				}*/
				
				
				

			} catch (Exception e) {
				e.printStackTrace();
				
			} finally{
				drop(highDataPacket);
				drop(lowDataPacket);
			}
		}
		
		highDataPort.close();
		lowDataPort.close();
	}

	@Override
	protected void openPorts() {

		maxStepPort = openInput("MAXSTEP");
		stepPort = openInput("STEP");
		highDataPort = openInput("HIGH");
		lowDataPort = openInput("LOW");

		outport = openOutputArray("SAR");
		//changeRatePort = openOutput("SARCHANGERATE");

	}
	
}
