package com.example.demo

import com.example.demo.data.Child
import com.example.demo.data.ParentWithList
import com.example.demo.data.ParentWithSet
import com.example.demo.repository.ParentWithListRepository
import com.example.demo.repository.ParentWithSetRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class DemoApplicationTests {

    companion object {
        @Container
        private val postgreSQLContainer = PostgreSQLContainer<Nothing>("postgres:latest")

        @DynamicPropertySource
        @JvmStatic
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgreSQLContainer::getUsername)
            registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
        }
    }

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate;

    @Autowired
    private lateinit var listRepository: ParentWithListRepository;

    @Autowired
    private lateinit var setRepository: ParentWithSetRepository;

    fun createTables() {
        jdbcTemplate.execute(
            """
            CREATE TABLE IF NOT EXISTS parent
            (
                p_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                name VARCHAR
            );
            CREATE TABLE IF NOT EXISTS child
            (
                c_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                name VARCHAR,
                p_id UUID NOT NULL REFERENCES parent (p_id),
                index INTEGER
            );
           
            DELETE FROM child;
            DELETE FROM parent;
        """.trimIndent()
        )
    }

    @Test
    fun `saving a parent with a set of children correctly stores the data`() {
        createTables();
        val parentWithSet = ParentWithSet(null, "Parent With Set", setOf(Child(null, "Child one")))
        setRepository.save(parentWithSet)

        val actual = setRepository.findAll().toList();

        Assertions.assertEquals(actual.size, 1);
        Assertions.assertEquals(actual.first().children.size, 1);
    }

    @Test
    fun `saving a parent with a list of children should store data but crashes`() {
        createTables();
        val parentWithList = ParentWithList(null, "Parent With Set", listOf(Child(null, "Child one")))

        //Crashes with
        //Caused by: org.springframework.jdbc.BadSqlGrammarException: PreparedStatementCallback; bad SQL grammar [INSERT INTO "child" ("c_id", "name", "p_id") VALUES (?, ?, ?)]; nested exception is org.postgresql.util.PSQLException: ERROR: column "c_id" is of type uuid but expression is of type integer
        //Hint: You will need to rewrite or cast the expression.
        listRepository.save(parentWithList);
    }

}
