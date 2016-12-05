package com.barclays.price.engine.domain;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	private Map<String, ProductFinalPriceSelection> strategies;

	public Company() {
		products = new TreeMap<String, Product>();
		competitors = new TreeMap<String, Competitor>();
		strategies = new TreeMap<String, ProductFinalPriceSelection>();
		initializeStrategies(strategies);
	}
	
	public void initializeStrategies(Map<String, ProductFinalPriceSelection> strategies) {
		strategies.put("H H", new HighSupplyHighDemandPriceSelection());
		strategies.put("H L", new HighSupplyLowDemandPriceSelection());
		strategies.put("L H", new LowSupplyHighDemandPriceSelection());
		strategies.put("L L", new LowSupplyLowDemandPriceSelection());
	}

	/**
	 * Data management related methods
	 */

	public Map<String, Product> getProducts() {
		return products;
	}

	public Product createProduct(String productName, String marketCondition) {
		ProductFinalPriceSelection finalPriceSelectionCriteria = strategies.get(marketCondition);

		if (finalPriceSelectionCriteria == null) {
			throw new InvalidMarketConditionStringException("Invalid Supply Demand Condition String in given file");
		}

		Product product = new Product(productName, finalPriceSelectionCriteria);
		return product;
	}

	public void addProduct(Product product) {
		this.products.put(product.getName(), product);
	}

	public Product getProductByProductName(String productName) {
		return products.get(productName);
	}

	public Map<String, Competitor> getCompetitors() {
		return competitors;
	}

	public void addCompetitor(Competitor competitor) {
		this.competitors.put(competitor.getName(), competitor);
	}

	public Competitor createCompetitor(String competitorName) {
		return new Competitor(competitorName);
	}

	public Competitor getCompetitorByCompetitorName(String competitorName) {
		return this.competitors.get(competitorName);
	}

	public Map<String, ProductFinalPriceSelection> getStrategies() {
		return strategies;
	}


	/**
	 * Computation related methods
	 */

	public double calculatePriceForProduct(Product product) {
		List<Double> competitorsPrices = collectCompetitorsPriceList(product);
		double initialProductPrice = calculateInitialProductPrice(competitorsPrices);
		double chosenProductPrice = product.getFinalPriceSelectionCriteria()
				.calculateFinalCompetitivePrice(initialProductPrice);
		return chosenProductPrice;
	}

	public List<Double> collectCompetitorsPriceList(Product product) {
		List<Double> competitorsPrices = new ArrayList<Double>();

		Double price;
		for (Competitor competitor : competitors.values()) {
			price = competitor.getProductPrices().get(product);
			if (price != null) {
				competitorsPrices.add(price);
			}
		}

		return competitorsPrices;
	}

	public double calculateInitialProductPrice(List<Double> competitorsPrices) {
		filterPrices(competitorsPrices);
		return calculateMode(competitorsPrices);
	}

	public void filterPrices(List<Double> competitorsPrices) {
		double averagePrice = calculateAverage(competitorsPrices);
		competitorsPrices.removeIf(price -> (price > (1.5 * averagePrice)) || (price < (0.5 * averagePrice)));
	}

	public double calculateAverage(List<Double> competitorsPrices) {
		double sum = 0.0;

		for (Double price : competitorsPrices) {
			sum += price;
		}

		return sum / (competitorsPrices.size());
	}

	public double calculateMode(List<Double> competitorsPrices) {
		HashMap<Double, Integer> priceCountMap = new HashMap<Double, Integer>();
		int max = -1;
		double frequentlyOccuringMinPrice = -1;
		
		for (double price: competitorsPrices) {
			if (priceCountMap.get(price) != null) {
				int count = priceCountMap.get(price);
				count = count + 1;
				priceCountMap.put(price, count);
				if (count > max) {
					max = count;
					frequentlyOccuringMinPrice = price;
				}
				else if ((count == max) && (price < frequentlyOccuringMinPrice)) {
					frequentlyOccuringMinPrice = price;
				}
			} else {
				priceCountMap.put(price, 1);
				
				if (frequentlyOccuringMinPrice == -1) {
					max = 1;
					frequentlyOccuringMinPrice = price;
				}
			}
		}
		
		return frequentlyOccuringMinPrice;
	}

	/**
	 * File Operations related methods
	 */

	public void createObjectsFromFileData(String filePath) throws IOException {
		FileReader fileReader = new FileReader(filePath);
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		String currentLine = bufferedReader.readLine();
		int numberOfProductLines = Integer.parseInt(currentLine);
		generateProducts(bufferedReader, numberOfProductLines);

		currentLine = bufferedReader.readLine();
		int numberOfCompetitorLines = Integer.parseInt(currentLine);
		generateCompetitors(bufferedReader, numberOfCompetitorLines);

		bufferedReader.close();
	}

	public void generateProducts(BufferedReader bufferedReader, int numberOfProductLines) throws IOException {
		String currentLine;
		for (int i = 1; i <= numberOfProductLines; i++) {
			currentLine = bufferedReader.readLine();
			String[] productInfo = currentLine.split(" ");
			Product product = createProduct(productInfo[0], productInfo[1] + " " + productInfo[2]);
			addProduct(product);
		}
	}

	public void generateCompetitors(BufferedReader bufferedReader, int numberOfCompetitorLines) throws IOException {
		String currentLine;
		for (int i = 1; i <= numberOfCompetitorLines; i++) {
			currentLine = bufferedReader.readLine();
			String[] competitorInfo = currentLine.split(" ");
			Product product = getProductByProductName(competitorInfo[0]);
			Competitor competitor = getCompetitorByCompetitorName(competitorInfo[1]);
			if (competitor == null) {
				competitor = createCompetitor(competitorInfo[1]);
				addCompetitor(competitor);
			}
			double productPrice = Double.parseDouble(competitorInfo[2]);
			competitor.addProductPrice(product, productPrice);
		}
	}

}
