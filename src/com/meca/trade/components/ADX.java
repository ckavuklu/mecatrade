package com.meca.trade.components;

import java.util.ArrayList;

import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.TradeUtils;

/** Average Directional Index **/
@ComponentDescription("Average Directional Index")
@OutPorts({
		@OutPort(value = "ADX", description = "ADX", type = Double.class),
		@OutPort(value = "PDI", description = "Positive DI", type = Double.class),
		@OutPort(value = "MDI", description = "Negative DI", type = Double.class) })
@InPorts({
		@InPort(value = "ATR", description = "True Range", type = Double.class),
		@InPort(value = "HIGH", description = "High Data", type = Double.class),
		@InPort(value = "LOW", description = "Low Data", type = Double.class),
		@InPort(value = "CLOSE", description = "Close Data", type = Double.class),
		@InPort(value = "WINDOW", description = "Window Size", type = Double.class) })
public class ADX extends Indicator {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort windowPort, closeDataPort, highDataPort, lowDataPort,
			atrDataPort;

	OutputPort adxOutport, mdiOutport, pdiOutport;

	protected ArrayList<Double> trWindow;
	protected ArrayList<Double> pDMWindow;
	protected ArrayList<Double> mDMWindow;

	private Double windowSize = null;

	private Double result = null;

	private Double mDM = null;

	private Double pDM = null;

	private Double avgmDI = Double.NaN;

	private Double avgpDI = Double.NaN;

	private Double TR = null;

	private Double avgTR = null;

	private Double avgpDM = null;

	private Double avgmDM = null;

	private Double DX = null;

	private Double previousAvgTR = null;

	private Double previousAvgpDM = null;

	private Double previousAvgmDM = null;

	private Double previousADX = null;

	private Double ADX = Double.NaN;

	private Double previousHigh = null;

	private Double previousLow = null;

	private boolean firstTREvaluation = true;

	private boolean firstADXEvaluation = true;

	@Override
	protected void execute() {

		Packet highDataPacket = null;
		Packet lowDataPacket = null;
		Packet atrDataPacket = null;
		Packet windowPacket = null;
		Packet closeDataPacket = null;

		if (windowSize == null) {
			windowPacket = windowPort.receive();
			Double doubleValue = (Double) windowPacket.getContent();
			windowSize = doubleValue;
			windowPort.close();
			drop(windowPacket);
			
			
			trWindow = new ArrayList<Double>();
			pDMWindow = new ArrayList<Double>();
			mDMWindow = new ArrayList<Double>();
		}

		while ((atrDataPacket = atrDataPort.receive()) != null) {

			lowDataPacket = lowDataPort.receive();
			closeDataPacket = closeDataPort.receive();
			highDataPacket = highDataPort.receive();

			Double hDat = (Double) highDataPacket.getContent();
			Double lDat = (Double) lowDataPacket.getContent();
			Double cDat = (Double) closeDataPacket.getContent();
			TR = (Double) atrDataPacket.getContent();

			Double upMove = null;
			Double downMove = null;

			if (previousHigh != null && previousLow != null) {
				upMove = hDat - previousHigh;
				downMove = previousLow - lDat;

				if (upMove > downMove && upMove > 0)
					pDM = upMove;
				else
					pDM = 0d;

				if (downMove > upMove && downMove > 0)
					mDM = downMove;
				else
					mDM = 0d;

				if (firstTREvaluation) {
					trWindow.add(TR);
					pDMWindow.add(pDM);
					mDMWindow.add(mDM);

					if (trWindow.size() == windowSize) {

						avgTR = TradeUtils.getSum(trWindow);
						avgpDM = TradeUtils.getSum(pDMWindow);
						avgmDM = TradeUtils.getSum(mDMWindow);

						firstTREvaluation = false;
					}

				} else {

					avgTR = previousAvgTR - (previousAvgTR / windowSize) + TR;
					avgpDM = previousAvgpDM - (previousAvgpDM / windowSize)
							+ pDM;
					avgmDM = previousAvgmDM - (previousAvgmDM / windowSize)
							+ mDM;

				}

				if (avgpDM != null && avgmDM != null) {

					avgpDI = (avgpDM / (avgTR == 0d ? 1d : avgTR)) * 100d;

					avgmDI = (avgmDM / (avgTR == 0d ? 1d : avgTR)) * 100d;

					DX = (Math.abs(avgpDI - avgmDI) / (avgpDI + avgmDI)) * 100d;

					if (firstADXEvaluation) {
						window.add(DX);

						if (window.size() == windowSize) {
							ADX = average();
							firstADXEvaluation = false;
						}

					} else {
						ADX = ((previousADX * (windowSize - 1)) + DX)
								/ windowSize;
					}
				}
}
				
			try {

					if (ADX != null) {
						if (adxOutport.isConnected()) {
							adxOutport.send(create(ADX));
						}
					}
					if (avgmDI != null) {
						if (mdiOutport.isConnected()) {
							mdiOutport.send(create(avgmDI));
						}
					}
					if (avgpDI != null) {
						if (pdiOutport.isConnected()) {
							pdiOutport.send(create(avgpDI));
						}
					}

				} catch (Exception e) {
					e.printStackTrace();

				} finally {
					drop(highDataPacket);
					drop(lowDataPacket);
					drop(closeDataPacket);
					drop(atrDataPacket);
				}
			

			previousHigh = hDat;
			previousLow = lDat;

			previousAvgTR = avgTR;
			previousAvgpDM = avgpDM;
			previousAvgmDM = avgmDM;
			previousADX = ADX;
		}

		highDataPort.close();
		lowDataPort.close();
		closeDataPort.close();
		atrDataPort.close();
	}

	@Override
	protected void openPorts() {

		closeDataPort = openInput("CLOSE");
		atrDataPort = openInput("ATR");
		windowPort = openInput("WINDOW");
		highDataPort = openInput("HIGH");
		lowDataPort = openInput("LOW");

		adxOutport = openOutput("ADX");
		pdiOutport = openOutput("PDI");
		mdiOutport = openOutput("MDI");
	}

}
