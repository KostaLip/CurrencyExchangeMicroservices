package cryptoWallet;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class CryptoWalletModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_seq3")
	@SequenceGenerator(name = "my_seq3", sequenceName = "my_seq3", allocationSize = 1, initialValue = 2)
	private int id;
	
	@Column(unique = true)
	private String email;
	
	private BigDecimal btcAmount;
	private BigDecimal ethAmount;
	private BigDecimal solAmount;
	
	public CryptoWalletModel() {
		
	}
	
	public CryptoWalletModel(String email, BigDecimal btcAmount, BigDecimal ethAmount, BigDecimal solAmount) {
		this.email = email;
		this.btcAmount = btcAmount;
		this.ethAmount = ethAmount;
		this.solAmount = solAmount;
	}

	public CryptoWalletModel(int id, String email, BigDecimal btcAmount, BigDecimal ethAmount, BigDecimal solAmount) {
		this.id = id;
		this.email = email;
		this.btcAmount = btcAmount;
		this.ethAmount = ethAmount;
		this.solAmount = solAmount;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
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
