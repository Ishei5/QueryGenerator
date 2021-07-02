package com.roadtosenior;

import com.roadtosenior.models.NotAnnotatedTable;
import com.roadtosenior.models.Person;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class QueryGeneratorTest {

    QueryGenerator queryGenerator;

    @Before
    public void before() {
        queryGenerator = new QueryGenerator();
    }

    @Test
    public void testGetAll() {
        String getAllSql = queryGenerator.getAll(Person.class);

        String expectedSql = "SELECT id, person_name, salary FROM persons;";
        assertEquals(expectedSql, getAllSql);
    }

    @Test
    public void testGetById() {
        String getbyIdSql = queryGenerator.getById(Person.class, 1);
        String expectedSql = "SELECT id, person_name, salary FROM persons WHERE id = 1;";
        assertEquals(expectedSql, getbyIdSql);
    }

    @Test
    public void testDelete() {
        String deleteSql = queryGenerator.delete(Person.class, 1);

        String expectedSql = "DELETE FROM persons WHERE id = 1;";
        assertEquals(expectedSql, deleteSql);
    }

    @Test
    public void testInsert() {
        Person personTest = new Person(2, "Nick", 1000.0);
        String insertSql = queryGenerator.insert(personTest);

        String expectedSql = "INSERT INTO persons (id, person_name, salary) VALUES (2, 'Nick', 1000.0);";
        assertEquals(expectedSql, insertSql);
    }

    @Test
    public void testUpdate() {
        Person personTest = new Person(2, "Nick", 1000.0);
        String updateSql = queryGenerator.update(personTest);

        String expectedSql = "UPDATE persons SET (person_name, salary) = ('Nick', 1000.0) WHERE id = 2;";
        assertEquals(expectedSql, updateSql);
    }

    @Test(expected = NumberFormatException.class)
    public void testGetByInvalidId() {
        queryGenerator.getById(Person.class, "id");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testAnAnnotatedTable() {
        String getAllSql = queryGenerator.getAll(NotAnnotatedTable.class);
    }
}
