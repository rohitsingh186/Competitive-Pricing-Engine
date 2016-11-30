package com.barclays.price.engine.domain;

import java.util.Map;
import java.util.TreeMap;

import com.barclays.price.engine.exception.InvalidMarketConditionStringException;
import com.barclays.price.engine.logic.HighSupplyHighDemandPriceSelection;
import com.barclays.price.engine.logic.HighSupplyLowDemandPriceSelection;
import com.barclays.price.engine.logic.LowSupplyHighDemandPriceSelection;
import com.barclays.price.engine.logic.LowSupplyLowDemandPriceSelection;
import com.barclays.price.engine.logic.ProductFinalPriceSelection;

public class Company {

	private Map<String, Product> products;
	private Map<String, Competitor> competitors;

	public Company() {
		products = new TreeMap<String, Product>();
		competitors = new TreeMap<String, Competitor>();
	}

	public Product getProductByProductNameAndMarketCondition(String productName, String marketCondition) {
		Product product = products.get(productName);
		
		if (product == null) {
			product = createProduct(productName, marketCondition);
		}
		
		return product;
	}

	public Competitor getCompetitorByCompetitorName(String competitorName) {
		Competitor competitor = competitors.get(competitorName);
		
		if (competitor == null) {
			competitor = createCompetitor(competitorName);
		}
		
		return competitor;
	}
	
	public Competitor createCompetitor(String competitorName) {
		return new Competitor(competitorName);
	}

	public Product createProduct(String productName, String marketCondition) {
		ProductFinalPriceSelection finalPriceSelectionCriteria;

		if (marketCondition.equals("H H")) {
			finalPriceSelectionCriteria = new HighSupplyHighDemandPriceSelection();
		}
		else if (marketCondition.equals("H L")) {
			finalPriceSelectionCriteria = new HighSupplyLowDemandPriceSelection();
		}
		else if (marketCondition.equals("L H")) {
			finalPriceSelectionCriteria = new LowSupplyHighDemandPriceSelection();
		}
		else if (marketCondition.equals("L L")) {
			finalPriceSelectionCriteria = new LowSupplyLowDemandPriceSelection();
		}
		else {
			throw new InvalidMarketConditionStringException("Invalid Supply Demand Condition String in given file");
		}
		
		Product product = new Product(productName, finalPriceSelectionCriteria);
		return product;
	}

}
