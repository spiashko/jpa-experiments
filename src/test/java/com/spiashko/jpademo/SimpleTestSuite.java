package com.spiashko.jpademo;

import com.spiashko.jpademo.simple.Company;
import com.spiashko.jpademo.simple.CompanyRepository;
import com.spiashko.jpademo.simple.Employee;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

public class SimpleTestSuite extends AbstractApplicationTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

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

        doInTransaction(() -> companyRepository.save(google));
        Assertions.assertEquals(1, companyRepository.findAll().size());

        doInTransaction(() -> companyRepository.save(facebook));
        Assertions.assertEquals(2, companyRepository.findAll().size());

        Assertions.assertEquals(facebook.getName(), companyRepository.findById(facebook.getId()).get().getName());

        doInTransaction(() -> companyRepository.delete(facebook));
        Assertions.assertEquals(1, companyRepository.findAll().size());

        Assertions.assertNull(companyRepository.findById(facebook.getId()).orElse(null));

        doInTransaction(() -> companyRepository.deleteAll());
        Assertions.assertEquals(0, companyRepository.findAll().size());
    }

    private void doInTransaction(Runnable runnable) {
        transactionTemplate.execute((status) -> {
            runnable.run();
            return null;
        });
    }
}
