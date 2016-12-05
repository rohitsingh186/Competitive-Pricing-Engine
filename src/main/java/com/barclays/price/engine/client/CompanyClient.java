package com.barclays.price.engine.client;

import java.io.IOException;
import com.barclays.price.engine.domain.Company;
import com.barclays.price.engine.domain.Product;

public class CompanyClient {

	public static void main(String[] args) throws IOException {
		Company company = new Company();
		company.createObjectsFromFileData("products.txt");
		System.out.println("Number of products generated: " + company.getProducts().size());
		System.out.println("Number of competitors generated: " + company.getCompetitors().size() + "\n");

		for (Product product : company.getProducts().values()) {
			double chosenPrice = company.calculatePriceForProduct(product);
			System.out.println("Chosen price for product " + "'" + product.getName() + "' is: " + chosenPrice);
		}
	}

}
