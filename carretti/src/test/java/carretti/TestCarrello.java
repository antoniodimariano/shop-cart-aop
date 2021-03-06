package carretti;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import valueobject.Prodotto;
import valueobject.ProdottoCarrello;

public class TestCarrello {
	private Shop shop;
	private Carrello carrello;
	private static Prodotto P1, P2, P3, P4, P5;
	static {
		P1 = new Prodotto("FAKE", "NAME", 10F);
		P2 = new Prodotto("A01", "Bottiglietta Ferrarelle", .5F);
		P3 = new Prodotto("A02", "Computer asus", 1000F);
		P4 = new Prodotto("A03", "Cuffie cellulare", 20.5F);
		P5 = new Prodotto("A04", "Mouse TRUST", 12F);
	}
	
	@Before
	public void initShop() {
		shop = Shop.getInstance();
		carrello = new Carrello();
	}
	
	@After
	public void destroyShop() {
		shop = null;
		carrello = null;
	}
	
	private final Set<Prodotto> initProducts(final Prodotto ...products) {
		Set<Prodotto> prod = new HashSet<Prodotto>();
		for(final Prodotto p : products)
			prod.add(p);
		return prod;
	}
	
	/**
	 * tenta di rimuovere un prodotto da un carrello vuoto
	 * @throws Exception 
	 */
	@Test
	public void testRemoveProdEmptyCart() {
		try {
			carrello.removeByCodice("TEST_1", 1);
			fail("removing not possible");
		} catch(Exception e) {
			
		}
	}
	
	/**
	 * prova ad inserire un prodotto che non esiste nello Shop
	 * @throws Exception 
	 */
	@Test
	public void testAddProductNotExist() {
		try {
			carrello.addByCodice(P1.getCodice(), 10);
			fail("il prodotto aggiunto non esiste");
		} catch (Exception e) {
			//intentionally empty
		}
	}
	
	/**
	 * prova ad inserire prodotti presenti e prodotti che non
	 * sono presenti nello shop
	 * 
	 */
	@Test
	public void testAddProducts() {
		int quantity = 10;
		Set<Prodotto> prods = initProducts(P1,P2,P3,P4,P5);
		for(Prodotto p : prods)
			try {
				carrello.addByCodice(p.getCodice(), quantity);
			} catch (Exception e) {
				//intentionally empty
			}
		
		//controllo se effettivamente ha inserita 4 prodotti nel carrello
		assertEquals("la dimensione non corrisponde", 4, carrello.getListaProdotti().size());
		
		assertFalse(carrello.getListaProdotti().containsKey(P1.getCodice()));
		assertTrue(carrello.getListaProdotti().containsKey(P2.getCodice()));
		assertTrue(carrello.getListaProdotti().containsKey(P3.getCodice()));
		assertTrue(carrello.getListaProdotti().containsKey(P4.getCodice()));
		assertTrue(carrello.getListaProdotti().containsKey(P5.getCodice()));
	}
	
	/**
	 * aggiunge una quantit� ad un prodotto gi� inserito nel carrello
	 * @throws Exception 
	 */
	@Test
	public void testAddQuantitySameProduct() throws Exception {
		carrello.addByCodice(P2.getCodice(), 10);
		carrello.addByCodice(P3.getCodice(), 1);
		carrello.addByCodice(P2.getCodice(), 13);
		int expected = 23;
		int actual = carrello.getListaProdotti().get(P2.getCodice()).getQuantity().intValue();
		assertSame("quantities not equals", expected, actual);
	}
	
	/**
	 * rimuove un prodotto dal carrello
	 */
	@Test
	public void testDeleteProduct() throws Exception {
		int quantity = 10;
		carrello.addByCodice(P2.getCodice(), quantity);
		carrello.addByCodice(P3.getCodice(), quantity);
		carrello.removeByCodice(P2.getCodice(), quantity);
		assertSame("Product not deleted", 1, carrello.getListaProdotti().size());
		assertFalse(carrello.getListaProdotti().containsKey(P2.getCodice()));
		assertTrue(carrello.getListaProdotti().containsKey(P3.getCodice()));
	}
	
	/**
	 * rimuove una quantit� superiore a quella contenuta nel carrello
	 * 
	 */
	@Test
	public void testDeleteMoreQuantity() throws Exception {
		carrello.addByCodice(P2.getCodice(), 10);
		carrello.removeByCodice(P2.getCodice(), 20);
		assertTrue(carrello.getListaProdotti().isEmpty());
	}
	
	/**
	 * rimuove un prodotto che non � stato inserito nel carrello
	 * 
	 */
	@Test
	public void testDeleteProductNotExist() {
		try{
			carrello.addByCodice(P2.getCodice(), 10);
			carrello.removeByCodice(P1.getCodice(), 10);
			fail("Product added");
		} catch (Exception e) {
			//intentionally empty
		}
		
	}
	
	/**
	 * rimuove una quantit� passando un numero negativo
	 * @throws Exception 
	 */
	@Test
	public void testDeleteNegativeQuantity() throws Exception {
		carrello.addByCodice(P2.getCodice(), 10);
		carrello.removeByCodice(P2.getCodice(), -2);
		int actual = carrello.getListaProdotti().get(P2.getCodice()).getQuantity().intValue();
		assertSame("Quantity is not equals", 8, actual);
	}
}
