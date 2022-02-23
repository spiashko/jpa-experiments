package com.spiashko.jpademo;

import com.spiashko.jpademo.dynamicfields.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Sql(scripts = {"classpath:sql-test-data/dynamic-fields-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DynamicFieldsTestSuite extends AbstractApplicationTest {

    private static final String orderSelectSql = """
            select *
            from person p
                     left join person_field pf on p.id = pf.fk_person and pf.name = ?
            order by pf.value
            """;

    private static final String filterSelectSql = """
            select *
            from person p
                     left join person_field pf1 on p.id = pf1.fk_person and pf1.name = 'firstname'
                     left join person_field pf2 on p.id = pf2.fk_person and pf2.name = 'lastname'
            where pf1.value = 'fn person 1'
            and pf2.value = 'ln person 1'
            """;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PersonRepository repository;

    @Test
    public void filteringTest() {

        List<Map<String, Object>> peopleFromRawFilterSql = jdbcTemplate.queryForList(filterSelectSql);
        Assertions.assertEquals(1, peopleFromRawFilterSql.size());

        List<Person> peopleFromFilterCriteria = repository.findAll(
                prepareFilterSpec(
                        new FieldNameValue("firstname", "fn person 1"),
                        new FieldNameValue("lastname", "ln person 1")
                )
        );
        Assertions.assertEquals(1, peopleFromFilterCriteria.size());

    }

    private Specification<Person> prepareFilterSpec(FieldNameValue... fields) {
        return (root, query, builder) -> {
            List<Predicate> allPredicates = new ArrayList<>();

            for (FieldNameValue field : fields) {
                ListJoin<Person, PersonField> join = root.join(Person_.personFields, JoinType.LEFT);
                join.on(builder.equal(join.get(PersonField_.name), field.name()));
                allPredicates.add(builder.equal(join.get(PersonField_.VALUE), field.value()));
            }

            return builder.and(allPredicates.toArray(allPredicates.toArray(new Predicate[0])));
        };

    }

    @Test
    public void complexTest() {

        List<Map<String, Object>> peopleFromRawSqlOrderedByFirstname = jdbcTemplate.queryForList(orderSelectSql, "firstname");
        Assertions.assertEquals(3, peopleFromRawSqlOrderedByFirstname.size());

        List<Map<String, Object>> peopleFromRawSqlOrderedByKek = jdbcTemplate.queryForList(orderSelectSql, "kek");
        Assertions.assertEquals(3, peopleFromRawSqlOrderedByKek.size());

        List<Person> peopleFromCriteriaOrderedByFirstname = repository.findAll(prepareOrderSpec("firstname"));
        Assertions.assertEquals(3, peopleFromCriteriaOrderedByFirstname.size());

        List<Person> peopleFromCriteriaOrderedByKek = repository.findAll(prepareOrderSpec("kek"));
        Assertions.assertEquals(3, peopleFromCriteriaOrderedByKek.size());
    }


    private Specification<Person> prepareOrderSpec(String fieldName) {
        return (root, query, builder) -> {
            ListJoin<Person, PersonField> join = root.join(Person_.personFields, JoinType.LEFT);
            join.on(builder.equal(join.get(PersonField_.name), fieldName));
            query
                    .orderBy(
                            builder.asc(join.get(PersonField_.VALUE))
                    );

            return query.getRestriction();
        };
    }

    private record FieldNameValue(String name, String value) {
    }
}
