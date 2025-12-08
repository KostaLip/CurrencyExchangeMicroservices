package userService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.UserDto;
import api.proxies.BankAccountProxy;
import api.proxies.CryptoWalletProxy;
import api.services.UserService;
import util.exceptions.AdminUpdateException;

@RestController
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository repo;
	
	@Autowired
	private BankAccountProxy proxy;
	
	@Autowired
	private CryptoWalletProxy cryptoWallet;
	
	@Override
	public List<UserDto> getUsers(String role) {
		List<UserModel> models = repo.findAll();
		List<UserDto> dtos = new ArrayList<UserDto>();
		if(role.equalsIgnoreCase("OWNER")) {
			for(UserModel model : models) {
				dtos.add(convertUserModelToDto(model));
			}
			return dtos;
		} else if(role.equalsIgnoreCase("ADMIN")) {
			for(UserModel model : models) {
				if(model.getRole().equalsIgnoreCase("USER")) {
					dtos.add(convertUserModelToDto(model));
				}
			}
			return dtos;
		}
		return null;
		
	}

	@Override
	public UserDto getUserByEmail(String email) {
		return convertUserModelToDto(repo.findByEmail(email));
	}

	@Override
	public ResponseEntity<?> createAdmin(UserDto dto) {
		if(repo.findByEmail(dto.getEmail()) == null) {
			dto.setRole("ADMIN");
			UserModel model = converUserDtoToModel(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(model));
		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Admin with passed email already exists");
		}
	}

	@Override
	public ResponseEntity<?> createUser(UserDto dto) {
		if(repo.findByEmail(dto.getEmail()) == null) {
			dto.setRole("USER");
			UserModel model = converUserDtoToModel(dto);
			proxy.createBankAccount(model.getEmail());
			cryptoWallet.createCryptoWallet(model.getEmail());
			return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(model));
		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User with passed email already exists");
		}
	}

	@Override
	public ResponseEntity<?> updateUser(UserDto dto, String role) {
		UserModel user = repo.findByEmail(dto.getEmail());
		if(user == null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User with passed email does not exist");
		}
		if(role.equalsIgnoreCase("ADMIN") && !user.getRole().equalsIgnoreCase("USER")) {
			throw new AdminUpdateException("ADMIN users can not update non USER users");
		}
		if(role.equalsIgnoreCase("ADMIN") && dto.getRole().equalsIgnoreCase("OWNER")) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("There is already OWNER user. System can have only one OWNER user");
		}
		if(role.equalsIgnoreCase("ADMIN") && dto.getRole().equalsIgnoreCase("ADMIN") && user.getRole().equalsIgnoreCase("USER")) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("You can not promote USER user to ADMIN user");
		}
		if(role.equalsIgnoreCase("OWNER")) {
			if(user.getRole().equalsIgnoreCase("OWNER") && !dto.getRole().equalsIgnoreCase("OWNER")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("You can not chane role of OWNER user. System can have only one OWNER user");
			} else if(!user.getRole().equalsIgnoreCase("OWNER") && dto.getRole().equalsIgnoreCase("OWNER")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("There is already OWNER user. System can have only one OWNER user");
			}
		}
		repo.updateUser(dto.getEmail(), dto.getPassword(), dto.getRole());
		//proxy.updateEmailBankAccount(role, role);
		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}
	
	@Override
	public ResponseEntity<?> deleteUser(String email) {
		UserModel user = repo.findByEmail(email);
		if(user != null) {
			if(user.getRole().equalsIgnoreCase("OWNER")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("You can not delete OWNER user. Because system can have only one OWNER user");
			}
			repo.delete(user);
			proxy.deleteBankAccount(email);
			cryptoWallet.deleteCryptoWallet(email);
			return ResponseEntity.status(HttpStatus.OK).body(String.format(
					"User with email: %s, has been successfully deleted", email));
		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User with passed email does not exist");
		}
	}
	
	public UserDto convertUserModelToDto(UserModel model) {
		return new UserDto(model.getEmail(), model.getPassword(), model.getRole());
	}
	
	public UserModel converUserDtoToModel(UserDto dto) {
		return new UserModel(dto.getEmail(), dto.getPassword(), dto.getRole());
	}

}
