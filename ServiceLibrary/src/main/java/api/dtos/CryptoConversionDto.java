package api.dtos;

import java.math.BigDecimal;

public class CryptoConversionDto {

	private CryptoExchangeDto exchange;
	private BigDecimal quantity;
	private ConversionResault conversionResault;
	
	public CryptoConversionDto() {
		
	}
	
	public CryptoConversionDto(CryptoExchangeDto exchange, BigDecimal quantity) {
		this.exchange = exchange;
		this.quantity = quantity;
		CryptoConversionDto.ConversionResault resault = new CryptoConversionDto.ConversionResault(
				exchange.getTo(), quantity.multiply(exchange.getExchangeRate())); 
		this.conversionResault = resault;
	}
	
	public CryptoExchangeDto getExchange() {
		return exchange;
	}

	public void setExchange(CryptoExchangeDto exchange) {
		this.exchange = exchange;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public ConversionResault getConversionResault() {
		return conversionResault;
	}

	public void setConversionResault(ConversionResault conversionResault) {
		this.conversionResault = conversionResault;
	}

	public class ConversionResault {
		private String to;
		private BigDecimal convertedAmount;
				
		public ConversionResault() {

		}

		public ConversionResault(String to, BigDecimal convertedAmount) {
			this.to = to;
			this.convertedAmount = convertedAmount;
		}

		public String getTo() {
			return to;
		}
		
		public void setTo(String to) {
			this.to = to;
		}
		
		public BigDecimal getConvertedAmount() {
			return convertedAmount;
		}
		
		public void setConvertedAmount(BigDecimal convertedAmount) {
			this.convertedAmount = convertedAmount;
		}
		
	}
	
}
