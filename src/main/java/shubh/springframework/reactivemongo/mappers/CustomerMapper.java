package shubh.springframework.reactivemongo.mappers;

import org.mapstruct.Mapper;
import shubh.springframework.reactivemongo.domain.Customer;
import shubh.springframework.reactivemongo.model.CustomerDTO;

@Mapper
public interface CustomerMapper {
    Customer CustomerDtoToCustomer(CustomerDTO customerDTO);

    CustomerDTO CustomerToCustomerDto(Customer customer);
}
