package api.dtos;

import java.math.BigDecimal;

public class CryptoWalletDto {

	private String email;
	private BigDecimal btcAmount;
	private BigDecimal ethAmount;
	private BigDecimal solAmount;
	
	public CryptoWalletDto() {
		
	}
	
	public CryptoWalletDto(String email, BigDecimal btcAmount, BigDecimal ethAmount, BigDecimal solAmount) {
		this.email = email;
		this.btcAmount = btcAmount;
		this.ethAmount = ethAmount;
		this.solAmount = solAmount;
	}

	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public BigDecimal getBtcAmount() {
		return btcAmount;
	}
	
	public void setBtcAmount(BigDecimal btcAmount) {
		this.btcAmount = btcAmount;
	}
	
	public BigDecimal getEthAmount() {
		return ethAmount;
	}
	
	public void setEthAmount(BigDecimal ethAmount) {
		this.ethAmount = ethAmount;
	}
	
	public BigDecimal getSolAmount() {
		return solAmount;
	}
	
	public void setSolAmount(BigDecimal solAmount) {
		this.solAmount = solAmount;
	}
	
}
