package edu.illinois.cs.cs125.fall2019.oh125;

import com.google.android.gms.tasks.Task;

import java.util.List;

public class CA extends Family125 implements ManageQueue {
    /**
     * Dummy constructor.
     */
    public CA() { }

    /**
     * @param name The name of the person in Family125 instance.
     * @param role The role of the person, either being student, instructor, CA, or TA.
     * @param email The email of the person as a String.
     * @param isAtOfficeHour The boolean expression indicating whether the person is at office hour.
     */
    public CA(String name, String role, String email, boolean isAtOfficeHour) {
        super(name, role, email, isAtOfficeHour);
    }

    /**
     * This method will send request to Firestore to obtain all Student instances in a List.
     * Please resister a listen for callback after the web request is complete.
     *
     * @return an asynchronous operation which obtains Student instances in queue database
     */
    @Override
    public Task<List<Student>> getQueue() {
        return null;
    }

    /**
     * End one student's queue status.
     * Used at the end of one CA session.
     * @param student the Student instance to be kicked out from the queue
     */
    @Override
    public void endQueue(Student student) {

    }
}
