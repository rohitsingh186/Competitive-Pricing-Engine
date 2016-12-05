package com.barclays.price.engine.domain;

import static org.junit.Assert.*;
import org.junit.Test;

import com.barclays.price.engine.logic.HighSupplyHighDemandPriceSelection;
import com.barclays.price.engine.logic.LowSupplyHighDemandPriceSelection;
import com.barclays.price.engine.logic.ProductFinalPriceSelection;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;

public class ProductTest {

	@Test
	public void shouldCreateProductWithGivenNameAndPriceSelectionStrategy() {
		ProductFinalPriceSelection pricingSelectionStrategy = new HighSupplyHighDemandPriceSelection(); 
		Product product = new Product("IPhone", pricingSelectionStrategy);
		assertEquals("IPhone", product.getName());
		assertEquals(HighSupplyHighDemandPriceSelection.class, product.getFinalPriceSelectionCriteria().getClass());
	}
	
	@Test
	public void shouldBeAbleToModifyPriceSelectionStrategy() {
		ProductFinalPriceSelection pricingSelectionStrategy = new HighSupplyHighDemandPriceSelection(); 
		Product product = new Product("IPhone", pricingSelectionStrategy);
		assertEquals(HighSupplyHighDemandPriceSelection.class, product.getFinalPriceSelectionCriteria().getClass());
		
		pricingSelectionStrategy = new LowSupplyHighDemandPriceSelection();
		product.setFinalPriceSelectionCriteria(pricingSelectionStrategy);
		assertEquals(LowSupplyHighDemandPriceSelection.class, product.getFinalPriceSelectionCriteria().getClass());
	}
	
	@Test
	public void shouldCompareProductObjectsBasedOnProductName() {
		Product product1 = new Product("IPhone", new HighSupplyHighDemandPriceSelection());
		Product product2 = new Product("HardDisk", new HighSupplyHighDemandPriceSelection());
		assertEquals(true, ((product1.compareTo(product2)) > 0));
	}

}
