
public class ParabolicSAR extends Indicator {

	public ParabolicSAR(Double step, Double maxStep,Integer initTrendDirection) {
		super();
		this.initAF = step;
		this.deltaAF = step;
		this.maxAF = maxStep;
		this.initTrendDirection = initTrendDirection;
		
	}
	
	private Integer windowSize = 2;
	
	private Double initAF = null;
	private Double deltaAF = null;
	private Double maxAF = null;
	
	private Double result = null;
	
	private Integer previousSARNo = null;
	private Integer currentSARNo = null;
	private Integer initTrendDirection = null;
	
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

	
	protected void execute(Double hDat,Double lDat) {

		
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
						currentAF = initAF;
					else 
						currentAF = (previousEP.compareTo(currentEP) == 0)?previousAF:Math.min(maxAF, previousAF + deltaAF);
							
		
					// cikis
					result = Double.NaN;
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
						tentativeSAR = Math.max(TradeUtils.roundUpDigits(previousSAR+previousAF*(previousEP-previousSAR),2),twoPreviousHIGHEST);
					else 
						tentativeSAR = Math.min(TradeUtils.roundUpDigits(previousSAR+previousAF*(previousEP-previousSAR),2), twoPreviousLOWEST);
					
					//calculated SAR
					calculatedSAR =  TradeUtils.roundUpDigits(previousSAR+previousAF*(previousEP-previousSAR),2);
					
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
						currentAF = initAF;
					else 
						currentAF = (previousEP.compareTo(currentEP) == 0)?previousAF:Math.min(maxAF, previousAF + deltaAF);
					
					// cikis
					result = tentativeSAR;
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
				if (result != null ) {
					System.out.println("SAR# : " + currentSARNo + " Calculated SAR : " + calculatedSAR + " Tentative SAR : " + result + " SAR : " + currentSAR + " EP : " + currentEP + " AF : " + currentAF);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}



}
