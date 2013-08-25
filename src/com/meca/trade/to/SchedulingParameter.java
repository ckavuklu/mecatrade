package com.meca.trade.to;

import java.io.Serializable;

public class SchedulingParameter implements Serializable{
	private Integer period;
	private String periodType;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		if (!((((SchedulingParameter) obj).getPeriod().equals(getPeriod())) && (((SchedulingParameter) obj)
				.getPeriodType().equals(getPeriodType()))))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return period.toString().concat(".").concat(periodType);
	}

	public SchedulingParameter(Integer period, String periodType) {
		super();
		this.period = period;
		this.periodType = periodType;
	}

	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}

	public String getPeriodType() {
		return periodType;
	}

	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}
}
