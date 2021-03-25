package nz.ac.wgtn.swen301.assignment1.cli;

import nz.ac.wgtn.swen301.assignment1.StudentManager;
import nz.ac.wgtn.swen301.studentdb.NoSuchRecordException;
import nz.ac.wgtn.swen301.studentdb.Student;

import java.sql.SQLException;

public class FindStudentDetails {

    // THE FOLLOWING METHOD MUST IMPLEMENTED
    /**
     * Executable: the user will provide a student id as single argument, and if the details are found,
     * the respective details are printed to the console.
     * E.g. a user could invoke this by running "java -cp <someclasspath> nz.ac.wgtn.swen301.assignment1.cli.FindStudentDetails id42"
     * java -cp nz.ac.wgtn.swen301.assignment1.StudentManager nz.ac.wgtn.swen301.assignment1.cli.FindStudentDetails id42
     * java -cp findStudentDetails.jar nz.ac.wgtn.swen301.assignment1.cli.FindStudentDetails.java id42
     *nz.ac.wgtn.swen301.assignment1.cli.FindStudentDetails
     * C:\Uni\3rd Year\SWEN301\Ass1\target\classes\nz\ac\wgtn\swen301\assignment1\cli\FindStudentDetails.class
     * java -cp target/classes:studentdb nz.ac.wgtn.swen301.assignment1.cli.FindStudentDetails id42
     * @param arg
     */
    public static void main (String[] arg) throws SQLException, NoSuchRecordException {
        if(arg == null || arg.length == 0){
            System.out.println("There was no argument");
            return;
        }
        System.out.println(FindStudentDetails.class.getResource("").getPath());
        StudentManager studentManager = new StudentManager();
        Student student = studentManager.readStudent(arg[0]);
        System.out.println("Id = " + student.getId() + " First Name: "  + student.getFirstName() +  " Last Name:  " + student.getName()  + " Degree: " + student.getDegree());
    }
}
