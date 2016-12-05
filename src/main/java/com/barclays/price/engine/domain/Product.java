package com.barclays.price.engine.domain;

import com.barclays.price.engine.logic.ProductFinalPriceSelection;

public class Product implements Comparable {

	private String name;
	private ProductFinalPriceSelection finalPriceSelectionCriteria;

	public Product(String name, ProductFinalPriceSelection finalPriceSelectionCriteria) {
		this.name = name;
		this.finalPriceSelectionCriteria = finalPriceSelectionCriteria;
	}
	
	public ProductFinalPriceSelection getFinalPriceSelectionCriteria() {
		return finalPriceSelectionCriteria;
	}

	public void setFinalPriceSelectionCriteria(ProductFinalPriceSelection finalPriceSelectionCriteria) {
		this.finalPriceSelectionCriteria = finalPriceSelectionCriteria;
	}

	public String getName() {
		return name;
	}

	@Override
	public int compareTo(Object o) {
		return this.name.compareTo(((Product) o).getName());
	}
	
}
