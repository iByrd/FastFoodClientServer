package fastfood;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OrderForm {
	private int burgerQuantity;
	private int friesQuantity;
	private int shakeQuantity;
	private int currentOrderNumber;
	private BigDecimal totalAmount;
	private BigDecimal totalBeforeTax;
	private BigDecimal totalTax;
	public static final double BURGER_PRICE = 5.00;
	public static final double FRIES_PRICE = 2.00;
	public static final double SHAKE_PRICE = 3.50;
	public static final double SALES_TAX = 0.06;

	public OrderForm(int burgerQuantity, int friesQuantity, int shakeQuantity, int currentOrderNumber) {
		this.burgerQuantity = burgerQuantity;
		this.friesQuantity = friesQuantity;
		this.shakeQuantity = shakeQuantity;
		this.currentOrderNumber = currentOrderNumber;
	}

	public int getCurrentOrderNumber() {
		return currentOrderNumber;
	}

	public void calculateTotalAmount() {
		BigDecimal totalBeforeTax = new BigDecimal(burgerQuantity * BURGER_PRICE + friesQuantity * FRIES_PRICE + shakeQuantity * SHAKE_PRICE);
		BigDecimal totalTax = new BigDecimal((burgerQuantity * BURGER_PRICE + friesQuantity * FRIES_PRICE + shakeQuantity * SHAKE_PRICE)
				* SALES_TAX);
		BigDecimal totalAmount = totalBeforeTax.add(totalTax);
		this.totalAmount = totalAmount;
		
	}

	public BigDecimal getTotalAmount() {
		BigDecimal formatTotal = totalAmount.setScale(2, RoundingMode.HALF_UP);
		return formatTotal;
	}

	public BigDecimal getTotalBeforeTax() {
		return totalBeforeTax;
	}

	public BigDecimal getTotalTax() {
		return totalTax;
	}

	public int getBurgerQuantity() {
		return burgerQuantity;
	}

	public int getFriesQuantity() {
		return friesQuantity;
	}

	public int getShakeQuantity() {
		return shakeQuantity;
	}
	
	@Override
	public String toString() {
		return "order number: " + this.currentOrderNumber + "\n" + "total: " + getTotalAmount();
	}

}
