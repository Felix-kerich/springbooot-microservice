package com.ecommerce.customer;



import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;



@Service
public class UserService {
    private final UserRepo userRepo;
    public UserService(UserRepo userRepo){
        this.userRepo = userRepo;
    }

    public CustomerDTO saveUser(CustomerDTO userDTO){
        Customer userModel = Mapper.mapToUserModel(userDTO);
        Customer savedUser = userRepo.save(userModel);
        return Mapper.mapToUserDTO(savedUser);
    } 

    public CustomerDTO getuserById(Long id){
        Customer userModel = userRepo.findById(id)
        .orElseThrow(()-> new RuntimeException("User does not Exist"));
        return Mapper.mapToUserDTO(userModel);

    }
   public List<CustomerDTO> getAllUsers(){
    List<Customer> allUsers =userRepo.findAll();
    return allUsers.stream()
            .map(Mapper::mapToUserDTO)
            .collect(Collectors.toList());
   }

   public String deleteAllUsers(){
    userRepo.deleteAll();
    return "all users deleted";
   }

   public String deleteById(Long id){
    userRepo.deleteById(id);
    return "user " +id +" "  +"deleted successfully"; 
   }

 

}
