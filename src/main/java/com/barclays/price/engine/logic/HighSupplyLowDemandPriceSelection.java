package com.barclays.price.engine.logic;

public class HighSupplyLowDemandPriceSelection implements ProductFinalPriceSelection {

	public double calculateFinalCompetitivePrice(double calculatedCompetitorsPrice) {
		return (calculatedCompetitorsPrice - 0.05 * calculatedCompetitorsPrice);
	}

}
