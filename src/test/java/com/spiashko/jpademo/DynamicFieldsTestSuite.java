package com.spiashko.jpademo;

import com.spiashko.jpademo.dynamicfields.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.criteria.ListJoin;
import java.util.List;
import java.util.Map;

@Sql(scripts = {"classpath:sql-test-data/dynamic-fields-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DynamicFieldsTestSuite extends AbstractApplicationTest {

    private static final String selectSql = """
            select *
            from person p
                     left join (select * from person_field pf where pf.name = 'kek') lpf on p.id = lpf.fk_person
            order by lpf.name
            """;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PersonRepository repository;

    @Test
    public void complexTest() {

        List<Map<String, Object>> peopleFromRawSql = jdbcTemplate.queryForList(selectSql);

        Assertions.assertEquals(3, peopleFromRawSql.size());

        List<Person> people = repository.findAll((root, query, builder) -> {

            ListJoin<Person, PersonField> join = root.join(Person_.personFields);
            query
                    .where(builder.equal(join.get(PersonField_.NAME), "kek"))
                    .orderBy(
                            builder.asc(join.get(PersonField_.VALUE))
                    );

            return query.getRestriction();
        });

        Assertions.assertEquals(3, people.size());
    }

    /*sql
    // ;
     */

    /*
    select *from participant left join participant_field pf on p.Id = pf.participant_id where p.Id In (2224532,234728,6272739)
     */
}
