package com.barclays.price.engine.domain;

import org.junit.Assert;
import org.junit.Test;
import com.barclays.price.engine.domain.Competitor;
import com.barclays.price.engine.domain.Product;
import com.barclays.price.engine.logic.HighSupplyHighDemandPriceSelection;

public class CompetitorTest {
	
	@Test
	public void shouldCreateCompetitorWithGivenNameAndZeroProductPrices() {
		Competitor competitor = new Competitor("Amazon");
		Assert.assertEquals("Amazon", competitor.getName());
		Assert.assertEquals(0, competitor.getProductPrices().size());
	}
	
	@Test
	public void shouldAddProductWithGivenPrice() {
		Competitor competitor = new Competitor("Amazon");
		Product product = new Product("IPhone", new HighSupplyHighDemandPriceSelection());
		competitor.addProductPrice(product, 123.00);
		Assert.assertEquals(1, competitor.getProductPrices().size());
		Assert.assertEquals(123.0, competitor.getProductPrices().get(product), 0.000001);
	}
}
