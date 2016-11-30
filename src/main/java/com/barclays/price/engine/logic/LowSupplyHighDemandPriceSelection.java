package com.barclays.price.engine.logic;

public class LowSupplyHighDemandPriceSelection implements ProductFinalPriceSelection {

	public double calculateFinalCompetitivePrice(double calculatedCompetitorsPrice) {
		return (calculatedCompetitorsPrice + 0.05 * calculatedCompetitorsPrice);
	}

}
