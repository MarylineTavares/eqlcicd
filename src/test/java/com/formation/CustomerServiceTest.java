package com.formation;

import com.formation.data.entity.CustomerEntity;
import com.formation.data.repository.CustomerRepository;
import com.formation.service.CustomerService;
import com.formation.web.error.ConflictException;
import com.formation.web.error.NotFoundException;
import com.formation.web.model.Customer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @InjectMocks
    CustomerService customerService;

    @Mock
    CustomerRepository customerRepository;

    @Test
    void getAllCustomers(){

        //Given
        Mockito.doReturn(getMockCustomers(2)).when(customerRepository).findAll();

        //When
        List<Customer> customers = customerService.getAllCustomers();

        //Then
        Assertions.assertEquals(2, customers.size());
    }

    @Test
    void getCustomer() {
        CustomerEntity entity = getMockCustomerEntity();
        Optional<CustomerEntity> optional = Optional.of(entity); //optional to not deal with null
        Mockito.doReturn(optional).when(customerRepository).findById(entity.getCustomerId());
        Customer customer = customerService.getCustomer(entity.getCustomerId().toString());
        Assertions.assertNotNull(customer);
        Assertions.assertEquals("testFirst", customer.getFirstName());
    }

    @Test
    void getCustomer_notExists(){
        CustomerEntity entity = getMockCustomerEntity();
        Optional<CustomerEntity> optional = Optional.empty();
        Mockito.doReturn(optional).when(customerRepository).findById(entity.getCustomerId());
        Assertions.assertThrows(NotFoundException.class, () -> customerService.getCustomer(entity.getCustomerId().toString()),
                "Exception not throw as expected");
    }

    @Test // 21 : Teste la méthode findByEmailAddress
    void findByEmailAddress() {
        CustomerEntity entity = getMockCustomerEntity();
        Mockito.doReturn(entity).when(customerRepository).findByEmailAddress(entity.getEmailAddress());
        Customer customer = customerService.findByEmailAddress(entity.getEmailAddress());
        Assertions.assertNotNull(customer);
        Assertions.assertEquals("testFirst", customer.getFirstName());
    }

    @Test
    void addCustomer(){
        CustomerEntity entity = getMockCustomerEntity();
        when(customerRepository.findByEmailAddress(entity.getEmailAddress())).thenReturn(null);
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(entity);
        Customer customer = new Customer (entity.getCustomerId().toString(), entity.getFirstName(),
                entity.getLastName(), entity.getEmailAddress(), entity.getPhoneNumber(), entity.getAddress());
        customer = customerService.addCustomer(customer);
        Assertions.assertNotNull(customer);
        Assertions.assertEquals("testLast", customer.getLastName());
    }

    @Test
    void addCustomer_existing(){
        CustomerEntity entity = getMockCustomerEntity();
        when(customerRepository.findByEmailAddress(entity.getEmailAddress())).thenReturn(entity);
        Customer customer = new Customer(entity.getCustomerId().toString(), entity.getFirstName(), entity.getLastName(),
                entity.getEmailAddress(), entity.getPhoneNumber(), entity.getAddress());
        Assertions.assertThrows(ConflictException.class, () -> customerService.addCustomer(customer),
                "would have thrown conflict exception");
    }

    @Test
    void updateCustomer() {
        CustomerEntity entity = getMockCustomerEntity();
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(entity);
        Customer customer = new Customer(entity.getCustomerId().toString(), entity.getFirstName(), entity.getLastName(),
                entity.getEmailAddress(), entity.getPhoneNumber(), entity.getAddress());
        customer = customerService.updateCustomer(customer);
        Assertions.assertNotNull(customer);
        Assertions.assertEquals("testLast", customer.getLastName());
    }

    @Test
    void deleteCustomer(){
        UUID id = UUID.randomUUID();
        doNothing().when(customerRepository).deleteById(id);
        customerService.deleteCustomer(id.toString());
    }

    private Iterable<CustomerEntity> getMockCustomers(int size){
        List<CustomerEntity> customers = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            customers.add(new CustomerEntity(UUID.randomUUID(), "firstname " + i, "lastname " + i,
                    "email " + i, "phone " + i, "address " + i));
        }
        return customers;
    }

    private CustomerEntity getMockCustomerEntity(){
        return new CustomerEntity(UUID.randomUUID(), "testFirst", "testLast",
                "testemail@test.com", "555-515-1234", "1234 Test Street");
    }
}
