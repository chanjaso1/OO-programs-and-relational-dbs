package test.nz.ac.wgtn.swen301.assignment1;

import nz.ac.wgtn.swen301.assignment1.StudentManager;
import nz.ac.wgtn.swen301.studentdb.Degree;
import nz.ac.wgtn.swen301.studentdb.NoSuchRecordException;
import nz.ac.wgtn.swen301.studentdb.Student;
import nz.ac.wgtn.swen301.studentdb.StudentDB;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Collection;
import java.util.regex.Pattern;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class TestStudentManager {

    // DO NOT REMOVE THE FOLLOWING -- THIS WILL ENSURE THAT THE DATABASE IS AVAILABLE
    // AND IN ITS INITIAL STATE BEFORE EACH TEST RUNS
    @Before
    public void init() {
        StudentDB.init();
    }
    // DO NOT REMOVE BLOCK ENDS HERE

    /**
     * Check there is a student when the correct id is read.
     * @throws Exception if there it is null.
     */
    @Test
    public void test_readStudent() throws Exception {
        Student student = new StudentManager().readStudent("id42");
        // THIS WILL INITIALLY FAIL !!
        assertNotNull(student);
    }

    /**
     * Check there is no student when an incorrect id is read.
     */
    @Test
    public void test_readStudent2() {
        try {
            new StudentManager().readStudent("42");
        } catch (NoSuchRecordException | SQLException e) {
            //if caught, this test passes.
        }
    }

    /**
     * Check there is a degree when the correct id is read.
     *
     * @throws Exception if it is null.
     */
    @Test
    public void test_readDegree() throws Exception {
        Degree degree = new StudentManager().readDegree("deg3");
        assertNotNull(degree);
    }

    /**
     * Check there is no degree when an incorrect id is read.
     *
     * @throws Exception if it has failed.
     */
    @Test
    public void test_readDegree2() throws Exception {
        try {
            new StudentManager().readDegree("id2");
            throw new Exception("Failed to find an incorrect degree");
        } catch (NoSuchRecordException e) {
            //If caught, this test passes
        }
    }

    /**
     * Check that deleting works.
     * The database will be read again to validate if the deletion was done.
     *
     * @throws Exception if there was no deletion.
     */
    @Test
    public void test_delete() throws Exception {
        StudentManager studentManager = new StudentManager();
        Student student = studentManager.readStudent("id34");
        studentManager.delete(student);

        try {
            studentManager.readStudent(student.getId());
            throw new Exception("Failed to delete a student");
        } catch (NoSuchRecordException | SQLException e) {
            //If caught, this test passes
        }
    }

    /**
     * Check that deleting does not work.
     * The database will be read again to validate if the deletion was done.
     *
     * @throws Exception if there was a deletion.
     */
    @Test
    public void test_delete2() throws Exception {

        try {
            new StudentManager().delete(new Student());
            throw new Exception("Failed to check for an incorrect tuple of student.");
        } catch (NoSuchRecordException e) {
            //If caught, this test passes
        }
    }

    /**
     * Check that the update is working by seeing if the student information and the
     * database is properly synchronized.
     * @throws Exception if there was no update.
     */
    @Test
    public void test_update() throws Exception {
        StudentManager studentManager = new StudentManager();
        Student student = studentManager.readStudent("id192"); //old student
        student.setFirstName("Fish");
        student.setName("and Chips");

        studentManager.update(student);
        student = studentManager.readStudent(student.getId());
        if(!(student.getFirstName().equals("Fish") && student.getName().equals("and Chips"))){
            throw new Exception("No update during testing");
        }
    }

    /**
     * Check that the update is not working by seeing if the string length of the first and
     * last name are more than 10 characters.
     * @throws Exception if the update was valid.
     */
    @Test
    public void test_update2() throws Exception {
        StudentManager studentManager = new StudentManager();
        Student student = studentManager.readStudent("id192"); //old student
        student.setFirstName("Prince William");
        student.setName("Michael Jackson");

        try {
            studentManager.update(student);
            throw new Exception("Failed to check for string length");
        }catch(SQLException e){
            //if caught, then this test passes
        }
    }


    /**
     * Create two unique students.
     * @throws Exception if these students were not found in the database.
     */
    @Test
    public void test_createStudent() throws Exception {
        StudentManager studentManager = new StudentManager();

        Student student = studentManager.createStudent("Jake", "Bobber", studentManager.readDegree("deg9"));
        studentManager.readStudent(student.getId());

        student = studentManager.createStudent("boober", "Shobber", studentManager.readDegree("deg2"));
        studentManager.readStudent(student.getId());

    }

    /**
     * Create two unique students and then delete to see if the id is still unique.
     * @throws Exception if the ids were not unique.
     */
    @Test
    public void test_createStudent2() throws Exception {
        StudentManager studentManager = new StudentManager();

        Student student = studentManager.createStudent("Jake", "Bobber", studentManager.readDegree("deg9"));
        assertTrue(student.getId().equals("id10000"));
        studentManager.readStudent(student.getId());

        studentManager.delete(student);

        student = studentManager.createStudent("boober", "Shobber", studentManager.readDegree("deg2"));
        studentManager.readStudent(student.getId());
        assertTrue(student.getId().equals("id10000"));
    }



    /**
     * Check that the list of student ids are not empty.
     * @throws Exception if it is empty.
     */
    @Test
    public void test_getAllStudentIds() throws Exception{
        StudentManager studentManager = new StudentManager();
        Collection<String> ids = studentManager.getAllStudentIds();
        if(ids.isEmpty()) throw new Exception("Student IDs should not be empty");

    }

    /**
     * Check that all student ids follow the same string format.
     * @throws Exception if any id does not follow the format.
     */
    @Test
    public void test_getAllStudentIds2() throws Exception{
        StudentManager studentManager = new StudentManager();
        Collection<String> ids = studentManager.getAllStudentIds();
        for(String id : ids) {
            if(!(id.substring(0,2).equals("id") && id.length() > 2)){
                throw new Exception("Some id/s were not formatted properly");
            }
        }

    }

    /**
     * Check that the list of degree ids are not empty.
     * @throws Exception if it is empty.
     */
    @Test
    public void test_getAllDegreeIds() throws Exception{
        StudentManager studentManager = new StudentManager();
        Collection<String> ids = (Collection<String>) studentManager.getAllDegreeIds();
        if(ids.isEmpty()) throw new Exception("Degree IDs should not be empty");

    }

    /**
     * Check that all degree ids follow the same string format.
     * @throws Exception if any id does not follow the format.
     */
    @Test
    public void test_getAllDegreeIds2() throws Exception{
        StudentManager studentManager = new StudentManager();
        Collection<String> ids = (Collection<String>) studentManager.getAllDegreeIds();
        for(String id : ids) {
            if(!(id.substring(0,3).equals("deg") && id.length() > 3)){
                throw new Exception("Some id/s were not formatted properly");
            }
        }

    }


}
