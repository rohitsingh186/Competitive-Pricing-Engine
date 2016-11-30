package com.barclays.price.engine.logic;

public class HighSupplyHighDemandPriceSelection implements ProductFinalPriceSelection {
	
	public double calculateFinalCompetitivePrice(double calculatedCompetitorsPrice) {
		return calculatedCompetitorsPrice;
	}

}
