package com.ecommerce.customer;

public class Mapper {
    public static Customer mapToUserModel(CustomerDTO userDTO){
        Customer userModel = new Customer(
            userDTO.getId(),
            userDTO.getFirstName(),
            userDTO.getLastName(),
            userDTO.getUserName(),
            userDTO.getEmail(), null 
            
        );
        return userModel;
    } 

    public static CustomerDTO mapToUserDTO(Customer userModel){
        CustomerDTO userDTO = new CustomerDTO(
            userModel.getId(),
            userModel.getFirstName(),
            userModel.getLastName(),
            userModel.getUserName(),
            userModel.getEmail()
        );
        return userDTO;
    }

}
