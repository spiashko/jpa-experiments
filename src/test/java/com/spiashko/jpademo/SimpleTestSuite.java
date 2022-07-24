package com.spiashko.jpademo;

import com.spiashko.jpademo.simple.Company;
import com.spiashko.jpademo.simple.CompanyRepository;
import com.spiashko.jpademo.simple.Employee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Slf4j
public class SimpleTestSuite extends AbstractApplicationTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private TransactionTemplate template;

    @Test
    public void complexTest() {

        Company google = Company.builder()
                .name("Google")
                .build();

        Company facebook = Company.builder()
                .name("Facebook")
                .build();

        Employee bob = Employee.builder()
                .name("Bob")
                .build();

        google.addEmployee(bob);

        companyRepository.save(google);
        Assertions.assertEquals(1, companyRepository.findAll().size());

        companyRepository.save(facebook);
        Assertions.assertEquals(2, companyRepository.findAll().size());

        Assertions.assertEquals(facebook.getName(), companyRepository.findById(facebook.getId()).get().getName());

        companyRepository.delete(facebook);
        Assertions.assertEquals(1, companyRepository.findAll().size());

        Assertions.assertNull(companyRepository.findById(facebook.getId()).orElse(null));

        companyRepository.deleteAll();
        Assertions.assertEquals(0, companyRepository.findAll().size());
    }

    @Test
    public void removeAndInsertTheSameEmployeeTest() {

        Company google = Company.builder()
                .name("Google")
                .build();

        Employee bob = Employee.builder()
                .name("Bob")
                .build();

        Employee alice = Employee.builder()
                .name("Alice")
                .build();


        google.addEmployee(bob);
        google.addEmployee(alice);

        companyRepository.save(google);

        template.executeWithoutResult(s -> {
            List<Company> companies = companyRepository.findAll();
            Assertions.assertEquals(1, companies.size());
            Assertions.assertEquals(2, companies.get(0).getEmployees().size());
        });

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            template.executeWithoutResult(s -> {
                Company googleInDb = companyRepository.findAll().get(0);

                boolean aliceWasRemoved = googleInDb.getEmployees().removeIf(e -> e.getName().equals("Alice"));
                Assertions.assertTrue(aliceWasRemoved, "alice should be removed from list");
                //companyRepository.flush(); // flush will make it work again

                Employee aliceOnceAgain = Employee.builder()
                        .name("Alice")
                        .build();
                googleInDb.addEmployee(aliceOnceAgain);
            });
        });
    }


}
