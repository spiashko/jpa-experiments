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
import java.util.List;
import java.util.Map;

@Sql(scripts = {"classpath:sql-test-data/dynamic-fields-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DynamicFieldsTestSuite extends AbstractApplicationTest {

    private static final String selectSql = """
            select *
            from person p
                     left join person_field pf on p.id = pf.fk_person and pf.name = ?
            order by pf.value
            """;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PersonRepository repository;

    @Test
    public void complexTest() {

        List<Map<String, Object>> peopleFromRawSqlOrderedByFirstname = jdbcTemplate.queryForList(selectSql, "firstname");
        Assertions.assertEquals(3, peopleFromRawSqlOrderedByFirstname.size());

        List<Map<String, Object>> peopleFromRawSqlOrderedByKek = jdbcTemplate.queryForList(selectSql, "kek");
        Assertions.assertEquals(3, peopleFromRawSqlOrderedByKek.size());

        List<Person> peopleFromCriteriaOrderedByFirstname = repository.findAll(prepareSpec("firstname"));
        Assertions.assertEquals(3, peopleFromCriteriaOrderedByFirstname.size());

        List<Person> peopleFromCriteriaOrderedByKek = repository.findAll(prepareSpec("kek"));
        Assertions.assertEquals(3, peopleFromCriteriaOrderedByKek.size());
    }

    private Specification<Person> prepareSpec(String fieldName) {
        return (root, query, builder) -> {
            ListJoin<Person, PersonField> join = root.join(Person_.personFields, JoinType.LEFT);
            join.on(builder.equal(join.get(PersonField_.name), fieldName));
            query
                    .where()
                    .orderBy(
                            builder.asc(join.get(PersonField_.VALUE))
                    );

            return query.getRestriction();
        };

    }
}
