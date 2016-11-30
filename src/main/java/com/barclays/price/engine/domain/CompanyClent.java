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

public class CompanyClent {

	private Map<String, Product> products;
	private Map<String, Competitor> competitors;

	public CompanyClent() {
		products = new TreeMap<String, Product>();
		competitors = new TreeMap<String, Competitor>();
	}

	public static void main(String[] args) throws IOException {
		CompanyClent company = new CompanyClent();
		company.createObjectsFromFileData("products.txt");
		System.out.println("Number of products generated: " + company.getProducts().size());
		System.out.println("Number of competitors generated: " + company.getCompetitors().size() + "\n");

		for (Product product : company.getProducts().values()) {
			double chosenPrice = company.calculatePriceForProduct(product);
			System.out.println("Chosen price for product " + "'" + product.getName() + "' is: " + chosenPrice);
		}
	}

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
		double averagePrice = calculateAverage(competitorsPrices);
		competitorsPrices.removeIf(price -> (price > (1.5 * averagePrice)) || (price < (0.5 * averagePrice)));
		return calculateMode(competitorsPrices);
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

	public double calculateAverage(List<Double> competitorsPrices) {
		double sum = 0.0;

		for (Double price : competitorsPrices) {
			sum += price;
		}

		return sum / (competitorsPrices.size());
	}

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

	public Map<String, Product> getProducts() {
		return products;
	}

	public Map<String, Competitor> getCompetitors() {
		return competitors;
	}

	/**
	 * HELPER METHODS
	 */
	public void generateProducts(BufferedReader bufferedReader, int numberOfProductLines) throws IOException {
		String currentLine;
		for (int i = 1; i <= numberOfProductLines; i++) {
			currentLine = bufferedReader.readLine();
			String[] productInfo = currentLine.split(" ");
			createProduct(productInfo[0], productInfo[1] + " " + productInfo[2]);
		}
	}

	public void generateCompetitors(BufferedReader bufferedReader, int numberOfCompetitorLines) throws IOException {
		String currentLine;
		for (int i = 1; i <= numberOfCompetitorLines; i++) {
			currentLine = bufferedReader.readLine();
			String[] competitorInfo = currentLine.split(" ");
			Product product = getProductByProductName(competitorInfo[0]);
			Competitor competitor = getOrCreateCompetitorByCompetitorName(competitorInfo[1]);
			double productPrice = Double.parseDouble(competitorInfo[2]);
			competitor.addProductPrice(product, productPrice);
		}
	}

	public Product getProductByProductName(String productName) {
		return products.get(productName);
	}

	public Competitor getOrCreateCompetitorByCompetitorName(String competitorName) {
		Competitor competitor = competitors.get(competitorName);

		if (competitor == null) {
			competitor = createCompetitor(competitorName);
			competitors.put(competitorName, competitor);
		}

		return competitor;
	}

	public Competitor createCompetitor(String competitorName) {
		return new Competitor(competitorName);
	}

	public void createProduct(String productName, String marketCondition) {
		ProductFinalPriceSelection finalPriceSelectionCriteria;

		if (marketCondition.equals("H H")) {
			finalPriceSelectionCriteria = new HighSupplyHighDemandPriceSelection();
		} else if (marketCondition.equals("H L")) {
			finalPriceSelectionCriteria = new HighSupplyLowDemandPriceSelection();
		} else if (marketCondition.equals("L H")) {
			finalPriceSelectionCriteria = new LowSupplyHighDemandPriceSelection();
		} else if (marketCondition.equals("L L")) {
			finalPriceSelectionCriteria = new LowSupplyLowDemandPriceSelection();
		} else {
			throw new InvalidMarketConditionStringException("Invalid Supply Demand Condition String in given file");
		}

		Product product = new Product(productName, finalPriceSelectionCriteria);
		products.put(productName, product);
	}

}
