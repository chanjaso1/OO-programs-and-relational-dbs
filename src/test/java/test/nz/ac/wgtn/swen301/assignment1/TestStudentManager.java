package test.nz.ac.wgtn.swen301.assignment1;

import nz.ac.wgtn.swen301.assignment1.StudentManager;
import nz.ac.wgtn.swen301.studentdb.Degree;
import nz.ac.wgtn.swen301.studentdb.NoSuchRecordException;
import nz.ac.wgtn.swen301.studentdb.Student;
import nz.ac.wgtn.swen301.studentdb.StudentDB;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;


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


}
