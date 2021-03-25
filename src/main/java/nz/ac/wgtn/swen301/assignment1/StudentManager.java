package nz.ac.wgtn.swen301.assignment1;

import nz.ac.wgtn.swen301.studentdb.Degree;
import nz.ac.wgtn.swen301.studentdb.NoSuchRecordException;
import nz.ac.wgtn.swen301.studentdb.Student;
import nz.ac.wgtn.swen301.studentdb.StudentDB;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A student managers providing basic CRUD operations for instances of Student, and a read operation for instances of Degree.
 * @author jens dietrich
 */
public class StudentManager {

    static Statement statement = null;  //for executing commands
    static PreparedStatement statementRead = null; //for reading students
    static Connection  connection = null;
    public static HashMap<String, Student> cache  = new HashMap<>(); //The cache to hold students.

    // DO NOT REMOVE THE FOLLOWING -- THIS WILL ENSURE THAT THE DATABASE IS AVAILABLE
    // AND THE APPLICATION CAN CONNECT TO IT WITH JDBC
    static {

        StudentDB.init();
        try {
            connection = DriverManager.getConnection("jdbc:derby:memory:studentdb");
            statement  = connection.createStatement();
            statementRead = connection.prepareStatement("SELECT * FROM STUDENTS WHERE ID = ?");
            cache.clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    // DO NOT REMOVE BLOCK ENDS HERE


    // THE FOLLOWING METHODS MUST IMPLEMENTED :

    /**
     * Return a student instance with values from the row with the respective id in the database.
     * If an instance with this id already exists, return the existing instance and do not create a second one.
     * @param id -- The id of the student.
     * @return the student.
     * @throws NoSuchRecordException if no record with such an id exists in the database
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_readStudent (followed by optional numbers if multiple tests are used)
     */
    public static Student readStudent(String id) throws NoSuchRecordException, SQLException {
        if(cache.containsKey(id)) return cache.get(id);

        statementRead.setString(1, id);             //Search all IDS for THIS id
        ResultSet rs = statementRead.executeQuery();

        while(rs.next()){
            String studentId = rs.getString("id");
            String name = rs.getString("name");
            String firstName = rs.getString("first_name");
            String degree = rs.getString("degree");


            Student student = new Student(studentId, name, firstName, readDegree(degree));

            rs.close();

            cache.put(studentId, student);
            return student;
        }

        rs.close();
        throw new NoSuchRecordException("No such record of student");

    }

    /**
     * Return a degree instance with values from the row with the respective id in the database.
     * If an instance with this id already exists, return the existing instance and do not create a second one.
     * @param id -- The id of the degree.
     * @return the degree.
     * @throws NoSuchRecordException if no record with such an id exists in the database
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_readDegree (followed by optional numbers if multiple tests are used)
     */
    public static Degree readDegree(String id) throws NoSuchRecordException, SQLException {
        ResultSet rs = statement.executeQuery("SELECT * FROM DEGREES WHERE ID = '" + id + "'"); //Search all IDs for THIS id
        while(rs.next()){
            String name = rs.getString("name");
            String degreeID = rs.getString("id");

            rs.close();
            return new Degree(degreeID, name);
        }

        rs.close();
        throw new NoSuchRecordException("No such record of degree");
    }

    /**
     * Delete a student instance from the database.
     * I.e., after this, trying to read a student with this id will result in a NoSuchRecordException.
     * @param student -- The student to be deleted.
     * @throws NoSuchRecordException if no record corresponding to this student instance exists in the database
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_delete
     */
    public static void delete(Student student) throws NoSuchRecordException, SQLException {
        cache.remove(student.getId());
        if(statement.executeUpdate("DELETE FROM STUDENTS WHERE ID = '" + student.getId() + "'") != 1){ //Check that only 1 row is deleted as IDs are unique.
            throw new NoSuchRecordException("No such record of student.");
        }
    }

    /**
     * Update (synchronize) a student instance with the database.
     * The id will not be changed, but the values for first names or degree in the database might be changed by this operation.
     * After executing this command, the attribute values of the object and the respective database value are consistent.
     * Note that names and first names can only be max 10 characters long.
     * There is no special handling required to enforce this, just ensure that tests only use values with < 10 characters.
     * @param student   -- The new student information.
     * @throws NoSuchRecordException if no record corresponding to this student instance exists in the database
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_update (followed by optional numbers if multiple tests are used)
     */
    public static void update(Student student) throws SQLException, NoSuchRecordException {
        readStudent(student.getId());   //check that this student exists

        String sql = "UPDATE STUDENTS SET NAME = '" + student.getName() +       //update the student
                "', FIRST_NAME =  '" + student.getFirstName() +
                "', DEGREE = '" + student.getDegree().getId() + "'" +
                "WHERE ID = '" + student.getId() + "'";


        statement.executeUpdate(sql); //update studentDB with this student information
        cache.put(student.getId(), student);    //update the hashmap
    }


    /**
     * Create a new student with the values provided, and save it to the database.
     * The student must have a new id that is not been used by any other Student instance or STUDENTS record (row).
     * Note that names and first names can only be max 1o characters long.
     * There is no special handling required to enforce this, just ensure that tests only use values with < 10 characters.
     * @param name          -- The name of the student.
     * @param firstName     -- The first name of the student.
     * @param degree        -- The degree the student is taking.
     * @return a freshly created student instance
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_createStudent (followed by optional numbers if multiple tests are used)
     */
    public static Student createStudent(String name,String firstName,Degree degree) throws SQLException, NoSuchRecordException {

        Statement currentStatement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        int max = 0;
        for(String id : getAllStudentIds()){        //Find the max id in the list of students.
            int currentId = Integer.parseInt(id.substring(2));
            if(currentId > max) max = currentId;
        }
        String newId = "id" + (++max);

        ResultSet rs = currentStatement.executeQuery("SELECT * FROM STUDENTS");
        rs.moveToInsertRow();
        rs.updateString("ID",  newId);          
        rs.updateString("name", name);
        rs.updateString("first_name", firstName);
        rs.updateString("degree", degree.getId());
        rs.insertRow();                             //Add a new student.

        rs.close();

        Student newStudent = readStudent(newId);
        cache.put(newStudent.getId(), newStudent);
        return newStudent;                  //Return the new student from the database.

}

    /**
     * Get all student ids currently being used in the database.
     * @return the collection of student ids.
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_getAllStudentIds (followed by optional numbers if multiple tests are used)
     */
    public static Collection<String> getAllStudentIds() throws SQLException, NoSuchRecordException {
        Collection<String> ids = new HashSet<>();

        ResultSet  rs = statement.executeQuery("SELECT ID FROM STUDENTS"); //Find all student ids.
        while(rs.next()){
            ids.add(rs.getString("ID"));
        }
        rs.close();

        if(ids.isEmpty()) {
            throw new NoSuchRecordException();
        }
        return ids;
    }

    /**
     * Get all degree ids currently being used in the database.
     * @return a collection of degree ids.
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_getAllDegreeIds (followed by optional numbers if multiple tests are used)
     */
    public static Iterable<String> getAllDegreeIds() throws SQLException, NoSuchRecordException {
        Collection<String> ids = new HashSet<>();

        ResultSet  rs = statement.executeQuery("SELECT ID FROM DEGREES"); //Find all degree ids.
        while(rs.next()){
            ids.add(rs.getString("ID"));
        }
        rs.close();

        if(ids.isEmpty()) {
            throw new NoSuchRecordException();
        }
        return ids;
    }

}
