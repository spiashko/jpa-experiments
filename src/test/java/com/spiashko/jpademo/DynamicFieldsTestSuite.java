package com.spiashko.jpademo;

import com.spiashko.jpademo.dynamicfields.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.criteria.ListJoin;
import java.util.List;
import java.util.function.Supplier;

@Sql(scripts = {"classpath:sql-test-data/dynamic-fields-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DynamicFieldsTestSuite extends AbstractApplicationTest {

    @Autowired
    private PersonRepository repository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    public void complexTest() {

        doInTransaction(() -> {
            List<Person> people = repository.findAll((root, query, builder) -> {

                ListJoin<Person, PersonField> join = root.join(Person_.personFields);
                query
                        .where(builder.equal(join.get(PersonField_.NAME), "firstname"))
                        .orderBy(
                                builder.asc(join.get(PersonField_.VALUE))
                        );

                return query.getRestriction();
            });

            Assertions.assertEquals(3, people.size());

            return people;
        });

    }

    /*
    Select * from participant p left join (select * from participant_field pf where pf.fieldname= ‘dhdhhd’) LPF on p.ID = LPF.participant_id where p.monitoring_id = 5449002 and p.configuration=100 order by LPF.fieldname ;
     */

    /*
    Select *from participant left join participant_field pf on p.Id = pf.participant_id where p.Id In (2224532,234728,6272739)
     */

    private <T> T doInTransaction(Supplier<T> supplier) {
        return transactionTemplate.execute((status) -> supplier.get());
    }
}
