## SWEN301 Assignment 1 Template

### Please refer to the assignment brief for details. 

##### 1. How you checked the correctness of dependencies between the UI and the domain layer using the generated jdepend reports.  
    
Running the following command, <code>mvn jdepend:generate</code> will generate a html file which displays the dependencies. 
In FindStudentDetails under "Uses Packages," it depends on the studentdb and domain package. 
It is also shown that the StudentManager package depends on the studentdb package.
In the "Used by Packages" section for StudentManager, it displays that FindStudentDetails uses this.
 

#### 2. How to generate the standalone CLI application with mvn, and how to use.

The code will be compiled using the command in the terminal <code>mvn compile</code> Once the code is compiled,
the application can be created with <code>mvn package</code>

This will create a jar file called "studentfinder-1.0.0.jar" in the located in <code>target</code> folder. 
The original-studentfinder-1.0.0.jar is the same jar file without the studentdb-1.1.1.jar package. 
With studentfinder-1.0.0.jar, the file can be executed. From the home directory, running the command:
<code>java -jar target/studentfinder-1.0.0.jar [argument]</code> will execute the jar file.

    

#### 3. Discuss (less than 100 words) whether your design is prone to memory leaks by interfering with Garbage Collection and how this has been or could be addressed. [3 marks]

Since the cache hashmap is static, it is not affected by the garbage collection. During long periods of being loaded, the 
cache becomes more prone to memory leakage. The memory leakage is due to the lack of cleaning unnecessary resources.
However, this can be addressed. In the <code>delete()</code> function in StudentManager, the cache can delete a student
if the database deletes it from the table. The cache can also be cleared by when it becomes too large.
