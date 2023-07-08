package shubh.springframework.reactivemongo.mappers;

import org.mapstruct.Mapper;
import shubh.springframework.reactivemongo.domain.Customer;
import shubh.springframework.reactivemongo.model.CustomerDTO;

@Mapper
public interface CustomerMapper {
    Customer customerDtoToCustomer(CustomerDTO customerDTO);

    CustomerDTO customerToCustomerDto(Customer customer);
}
