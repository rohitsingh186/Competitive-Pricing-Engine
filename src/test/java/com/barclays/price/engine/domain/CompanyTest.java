package com.barclays.price.engine.domain;

import static org.junit.Assert.*;

import com.barclays.price.engine.logic.LowSupplyLowDemandPriceSelection;
import org.junit.Before;
import org.junit.Test;

import com.barclays.price.engine.logic.HighSupplyHighDemandPriceSelection;
import org.omg.CORBA.DoubleHolder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompanyTest {

	Company company;

	@Before
	public void setUp() {
		company = new Company();
	}

	/**
	 * Initialization related method's tests
	 */

	@Test
	public void shouldCreateCompanyWithDefaultStrategies() {
		assertEquals(4, company.getStrategies().size());
	}

	@Test
	public void shouldCreateCompanyWithZeroProductsAndZeroCompetitors() {
		assertEquals(0, company.getProducts().size());
		assertEquals(0, company.getCompetitors().size());
	}

	/**
	 * Data management related method's tests
	 */

	@Test
	public void shouldGetCatalogueOfProducts() {
		company = new Company();
		Product product = new Product("Moto Phone", new HighSupplyHighDemandPriceSelection());
		company.addProduct(product);
		Map<String, Product> products = company.getProducts();
		assertEquals(1, products.size());
		assertEquals(product, products.get("Moto Phone"));
	}

	@Test
	public void shouldCreateProductWithGivenNameAndMarketConditionString() {
		company = new Company();
		Product product = company.createProduct("Moto Phone", "H H");
		assertEquals("Moto Phone", product.getName());
		assertEquals(true, product.getFinalPriceSelectionCriteria() instanceof HighSupplyHighDemandPriceSelection);
	}

	@Test
	public void shouldAddProductInProductsCatalogue() {
		company = new Company();
		Product product = new Product("Moto Phone", new HighSupplyHighDemandPriceSelection());
		assertEquals(0, company.getProducts().size());
		company.addProduct(product);
		assertEquals(1, company.getProducts().size());
	}

	@Test
	public void shouldGetProductByProductName() {
		company = new Company();
		Product product = new Product("Moto Phone", new HighSupplyHighDemandPriceSelection());
		company.addProduct(product);
		assertEquals(product, company.getProductByProductName("Moto Phone"));
	}

	@Test
	public void shouldGetCatalogueOfCompetitors() {
		company = new Company();
		Competitor competitor = new Competitor("Amazon");
		company.addCompetitor(competitor);
		Map<String, Competitor> competitors = company.getCompetitors();
		assertEquals(1, competitors.size());
		assertEquals(competitor, competitors.get("Amazon"));
	}

	@Test
	public void shouldCreateCompetitorWithGivenCompetitorName() {
		Competitor competitor = company.createCompetitor("Amazon");
		assertEquals("Amazon", competitor.getName());
	}

	@Test
	public void shouldAddCompetitorsInCompetitorsCatalogue() {
		company = new Company();
		Competitor competitor = new Competitor("Amazon");
		assertEquals(0, company.getCompetitors().size());
		company.addCompetitor(competitor);
		assertEquals(1, company.getCompetitors().size());
	}

	@Test
	public void shouldGetCompetitorByCompetitorName() {
		company = new Company();
		Competitor competitor = new Competitor("Amazon");
		company.addCompetitor(competitor);
		assertEquals(competitor, company.getCompetitorByCompetitorName("Amazon"));
	}

	@Test
	public void shouldGetCatalogueOfStrategies() {
		assertEquals(4, company.getStrategies().size());
	}

	/**
	 * Computation related method's tests
	 */

	@Test
	public void shouldCollectCompetitorsPricesForGivenProduct() {
		company = new Company();
		Product product = new Product("Moto Phone", new HighSupplyHighDemandPriceSelection());
		company.addProduct(product);
		Competitor competitor = new Competitor("Amazon");
		competitor.addProductPrice(product, 14999.0);
		company.addCompetitor(competitor);

		List<Double> competitorsPrices = company.collectCompetitorsPriceList(product);
		assertEquals(1, competitorsPrices.size());
		assertEquals(true, competitorsPrices.contains(14999.0));
	}

	@Test
	public void shouldCalculateAverageOfDoubleList() {
		List<Double> competitorsPrices = new ArrayList<Double>();
		competitorsPrices.add(100.0);
		competitorsPrices.add(150.0);
		competitorsPrices.add(200.0);
		competitorsPrices.add(250.0);

		assertEquals(700.0 / 4, company.calculateAverage(competitorsPrices), 0.001);
	}

	@Test
	public void shouldFilterPricesBasedOnAveragePrice() {
		List<Double> competitorsPrices = new ArrayList<Double>();
		competitorsPrices.add(50.0);
		competitorsPrices.add(150.0);
		competitorsPrices.add(200.0);
		competitorsPrices.add(250.0);
		competitorsPrices.add(750.0);

		company.filterPrices(competitorsPrices);
		assertEquals(3, competitorsPrices.size());
		assertEquals(true, competitorsPrices.contains(150.0));
		assertEquals(true, competitorsPrices.contains(200.0));
		assertEquals(true, competitorsPrices.contains(250.0));
	}

	@Test
	public void shouldCalculateModeForGivenPriceListIfSingleModePresent() {
		List<Double> competitorsPrices = new ArrayList<Double>();
		competitorsPrices.add(150.0);
		competitorsPrices.add(250.0);
		competitorsPrices.add(250.0);

		assertEquals(250.0, company.calculateMode(competitorsPrices), 0.001);
	}

	@Test
	public void shouldCalculateModeForGivenPriceListChoosingLeastOneIfMultipleModePresent() {
		List<Double> competitorsPrices = new ArrayList<Double>();
		competitorsPrices.add(150.0);
		competitorsPrices.add(150.0);
		competitorsPrices.add(250.0);
		competitorsPrices.add(250.0);

		assertEquals(150.0, company.calculateMode(competitorsPrices), 0.001);
	}

	@Test
	public void shouldCalculateInitialProductPriceForGivenCompetitorsPrices() {
		List<Double> competitorsPrices = new ArrayList<Double>();
		competitorsPrices.add(50.0);
		competitorsPrices.add(150.0);
		competitorsPrices.add(200.0);
		competitorsPrices.add(250.0);
		competitorsPrices.add(750.0);

		assertEquals(150.0, company.calculateInitialProductPrice(competitorsPrices), 0.001);
	}

	@Test
	public void shouldCalculateFinalPriceBasedOnInitialCalculatedPriceAndProductMarketConditionForGivenProductName() {
		company = new Company();
		Product product = new Product("Moto Phone", new LowSupplyLowDemandPriceSelection());
		company.addProduct(product);

		Competitor competitor = new Competitor("Amazon");
		competitor.addProductPrice(product, 14999.0);
		company.addCompetitor(competitor);

		competitor = new Competitor("Flipkart");
		competitor.addProductPrice(product, 15499.0);
		company.addCompetitor(competitor);

		competitor = new Competitor("Snapdeal");
		competitor.addProductPrice(product, 15499.0);
		company.addCompetitor(competitor);

		assertEquals(15499.0 * 1.1, company.calculatePriceForProduct(product), 0.001);
	}

	// TODO: File operations related method's test

	@Test
	public void shouldGenerateProductsFromFileGivenNumberOfLinesToRead() throws IOException {
		company = new Company();

		FileReader fileReader = new FileReader("test-product-generation.txt");
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		company.generateProducts(bufferedReader, 4);
		bufferedReader.close();

		assertEquals(4, company.getProducts().size());
	}

	@Test
	public void shouldGenerateCompetitorsFromFileGivenNumberOfLinesToRead() throws IOException {
		company = new Company();

		company.addProduct(new Product("ToshibaHardDrive", new HighSupplyHighDemandPriceSelection()));
		company.addProduct(new Product("IPhone6S", new HighSupplyHighDemandPriceSelection()));
		company.addProduct(new Product("Redmi", new HighSupplyHighDemandPriceSelection()));
		company.addProduct(new Product("SamsungPhone", new HighSupplyHighDemandPriceSelection()));

		FileReader fileReader = new FileReader("test-competitor-generation.txt");
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		company.generateCompetitors(bufferedReader, 20);
		bufferedReader.close();

		assertEquals(8, company.getCompetitors().size());
	}

	@Test
	public void shouldGenerateProductsAndCompetitosFromGivenFilepath() throws IOException {
		company = new Company();

		company.createObjectsFromFileData("products.txt");

		assertEquals(4, company.getProducts().size());
		assertEquals(8, company.getCompetitors().size());
	}

}
