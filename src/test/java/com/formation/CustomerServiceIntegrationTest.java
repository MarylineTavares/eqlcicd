package com.formation;

import com.formation.service.CustomerService;
import com.formation.web.error.ConflictException;
import com.formation.web.error.NotFoundException;
import com.formation.web.model.Customer;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class CustomerServiceIntegrationTest {

    @Autowired
    CustomerService customerService;

    @Test
    void getAllCustomer(){
        List<Customer> customers = customerService.getAllCustomers();
        assertEquals(5, customers.size());
    }

    @Test
    void getCustomer(){

        Customer customer = customerService.getCustomer("054b145c-ddbc-4136-a2bd-7bf45ed1bef7");
        assertNotNull(customer);
        assertEquals("Cally", customer.getFirstName());
    }

    @Test
    void getCustomer_NotFound(){
        assertThrows(NotFoundException.class, () -> customerService.getCustomer("d972b30f-21cc-411f-b374-685ce23cd317"), "should have thrown an exception");
    }

    @Test
    void addCustomer(){
        Customer customer = new Customer("", "John", "Doe", "jdoe@test.com", "555-515-1234",
                "1234 Main Street; Anytown, KS 66110");
        customer = customerService.addCustomer(customer);
        assertTrue(StringUtils.isNotBlank(customer.getCustomerId()));
        assertEquals("John", customer.getFirstName());
        customerService.deleteCustomer(customer.getCustomerId());
    }

    @Test
    void addCustomer_alreadyExists(){
        Customer customer = new Customer("", "John", "Doe", "penatibus.et@lectusa.com", "555-515-1234", "1234 Main Street; Anytown, KS 66110");
        assertThrows(ConflictException.class, () -> customerService.addCustomer(customer));
    }

    @Test
    void updateCustomer(){
        Customer customer = new Customer("", "John", "Doe", "jdoe@test.com",
                "555-515-1234", "1234 Main Street; Anytown, KS 66110");
        customer = customerService.addCustomer(customer);
        customer.setFirstName("Jane");
        customer = customerService.updateCustomer(customer);
        assertEquals("Jane", customer.getFirstName());
        customerService.deleteCustomer(customer.getCustomerId());
    }



}
