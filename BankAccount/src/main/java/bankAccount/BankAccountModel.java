package bankAccount;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class BankAccountModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_seq2")
	@SequenceGenerator(name = "my_seq2", sequenceName = "my_seq2", allocationSize = 1, initialValue = 2)
	private int id;
	
	@Column(unique = true)
	private String email;
	
	private BigDecimal usdAmount;
	private BigDecimal eurAmount;
	private BigDecimal rsdAmount;
	private BigDecimal gbpAmount;
	private BigDecimal chfAmount;
	
	public BankAccountModel() {
		
	}
	
	public BankAccountModel(String email, BigDecimal usdAmount, BigDecimal eurAmount, BigDecimal rsdAmount,
			BigDecimal gbpAmount, BigDecimal chfAmount) {
		this.email = email;
		this.usdAmount = usdAmount;
		this.eurAmount = eurAmount;
		this.rsdAmount = rsdAmount;
		this.gbpAmount = gbpAmount;
		this.chfAmount = chfAmount;
	}

	public BankAccountModel(int id, String email, BigDecimal usdAmount, BigDecimal eurAmount, BigDecimal rsdAmount,
			BigDecimal gbpAmount, BigDecimal chfAmount) {
		this.id = id;
		this.email = email;
		this.usdAmount = usdAmount;
		this.eurAmount = eurAmount;
		this.rsdAmount = rsdAmount;
		this.gbpAmount = gbpAmount;
		this.chfAmount = chfAmount;
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
	
	public BigDecimal getUsdAmount() {
		return usdAmount;
	}
	
	public void setUsdAmount(BigDecimal usdAmount) {
		this.usdAmount = usdAmount;
	}
	
	public BigDecimal getEurAmount() {
		return eurAmount;
	}
	
	public void setEurAmount(BigDecimal eurAmount) {
		this.eurAmount = eurAmount;
	}
	
	public BigDecimal getRsdAmount() {
		return rsdAmount;
	}
	
	public void setRsdAmount(BigDecimal rsdAmount) {
		this.rsdAmount = rsdAmount;
	}
	
	public BigDecimal getGbpAmount() {
		return gbpAmount;
	}
	
	public void setGbpAmount(BigDecimal gbpAmount) {
		this.gbpAmount = gbpAmount;
	}
	
	public BigDecimal getChfAmount() {
		return chfAmount;
	}
	
	public void setChfAmount(BigDecimal chfAmount) {
		this.chfAmount = chfAmount;
	}

}
