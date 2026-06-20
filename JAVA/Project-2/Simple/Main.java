import java.util.Scanner;
import java.util.Stack;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Initializing DecodeLabs Grade System...");

        System.out.print("Enter Student Name: ");
        String name = sc.nextLine();
        Student student = new Student(name);

        System.out.print("Enter the number of subjects: ");
        // Mitigating the Scanner Buffer Trap using Integer.parseInt
        int numSubjects = Integer.parseInt(sc.nextLine());

        // Input Phase with Defensive Programming
        for (int i = 1; i <= numSubjects; i++) {
            while (true) {
                System.out.print("Enter marks for Subject " + i + " (0-100): ");
                int mark = Integer.parseInt(sc.nextLine());

                // Validating Edge Cases before processing
                if (mark >= 0 && mark <= 100) {
                    student.addMark(mark);
                    break; // Valid input, break the while loop
                } else {
                    System.out.println("[ERROR] Invalid input. Marks must be between 0 and 100.");
                }
            }
        }

        // Execution Core
        GradeCalculator calculator = new GradeCalculator();
        calculator.processStudentData(student);

        ReportGenerator report = new ReportGenerator();
        report.printReport(student, calculator);

        sc.close();
    }
}