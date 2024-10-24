import java.sql.*;
import java.util.*;

public class Main {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "2978";
    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);
            while (true) { 
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1 Add Patient");
                System.out.println("2 View Patients");
                System.out.println("3 View Doctors");
                System.out.println("4 Book Appointment");
                System.out.println("5 Exit");
                System.out.println("Enter your Choice : ");
                int Choice = scanner.nextInt();
                switch (Choice) {
                    case 1 :
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;
                    case 5:
                        System.out.println("THANKYOU FOR USING HOSPITAL MANAGEMENT SYSTEM");
                        return;
                    default:
                        System.out.println("Enter valid choice");
                        break;
                }   
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner){
       System.out.println("Enter Patient Id : ");
       int patientId = scanner.nextInt();
       System.out.println("Enter Doctor Id : ");
       int doctorId = scanner.nextInt();
       System.out.println("Enter appointment Date(YYYY-MM-DD) : ");
       String appointmentDate = scanner.next();
       if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)){
            if(checkDoctorAvaliability(doctorId,appointmentDate,connection)){
                String appointmentQuery = "INSERT INTO appointments(patient_id,doctor_id,appointment_date) VALUES(?,?,?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3,appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected>0){
                        System.out.println("Appointment Booked");
                    }else{
                        System.out.println("Failed to book Appointment");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }else{
            System.out.println("Doctor not Avaliable on this Date");
        }
    }else{
        System.out.println("Either Doctor or Patient doesn't exist!!!");
    }
}

public static boolean checkDoctorAvaliability(int doctorId,String appointmentDate,Connection connection){
    String query = "select COUNT(*) from appointments WHERE doctor_id=? AND appointment_date=?";
    try {
        PreparedStatement preparedStatement = connection.prepareCall(query);
        preparedStatement.setInt(1, doctorId);
        preparedStatement.setString(2, appointmentDate);
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()){
            int count = resultSet.getInt(1);
            if(count==0){
                return true;
            }else{
                return false;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}
}