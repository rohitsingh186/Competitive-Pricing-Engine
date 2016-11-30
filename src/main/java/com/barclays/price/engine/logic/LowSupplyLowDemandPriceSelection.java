package com.barclays.price.engine.logic;

public class LowSupplyLowDemandPriceSelection implements ProductFinalPriceSelection {

	public double calculateFinalCompetitivePrice(double calculatedCompetitorsPrice) {
		return (calculatedCompetitorsPrice + 0.1 * calculatedCompetitorsPrice);
	}

}
