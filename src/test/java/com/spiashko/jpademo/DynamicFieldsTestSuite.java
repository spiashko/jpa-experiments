package com.spiashko.jpademo;

import com.spiashko.jpademo.dynamicfields.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.criteria.ListJoin;
import java.util.List;
import java.util.function.Supplier;

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
-- fetch join sql
with ordered_people as (
    SELECT distinct p.id,
                    pf.name,
                    pf.value,
                    row_number() over (order by pf.value) as row
    from person p
             join person_field pf on p.id = pf.fk_person
    where pf.name = 'firstname'
    LIMIT 10 OFFSET 0
)
SELECT distinct op.id,
                pf.name,
                pf.value,
                row
from ordered_people op
     join person_field pf on op.id = pf.fk_person
order by row
     */

    private <T> T doInTransaction(Supplier<T> supplier) {
        return transactionTemplate.execute((status) -> supplier.get());
    }
}
