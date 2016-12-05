package com.barclays.price.engine.domain;

import java.util.Map;
import java.util.TreeMap;

public class Competitor {
	
	private String name;
	private Map<Product, Double> productPrices;
	
	public Competitor(String name) {
		this.name = name;
		productPrices = new TreeMap<Product, Double>();
	}
	
	public void addProductPrice(Product product, double price) {
		productPrices.put(product, price);
	}

	public Map<Product, Double> getProductPrices() {
		return productPrices;
	}

	public String getName() {
		return name;
	}
	
}
