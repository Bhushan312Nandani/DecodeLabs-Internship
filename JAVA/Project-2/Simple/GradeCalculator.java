import java.util.Stack;
class GradeCalculator {
    private int totalMarks = 0;
    private double averagePercentage = 0.0;
    private String grade = "";

    // The "Stack of Calculation"
    public void processStudentData(Student student) {
        Stack<Integer> marks = student.getMarksStack();
        int subjectCount = marks.size(); 
        
        if (subjectCount == 0) return;

        // Process: Accumulation via DSA Stack Popping
        while (!marks.isEmpty()) {
            totalMarks += marks.pop(); // LIFO extraction
        }

        // Process: Calculation (Preventing Java Integer Truncation Trap)
        averagePercentage = (double) totalMarks / subjectCount;

        // Process: Classification (The Logic Ladder)
        assignGrade();
    }

    private void assignGrade() {
        if (averagePercentage >= 90) {
            grade = "A";
        } else if (averagePercentage >= 80) {
            grade = "B";
        } else if (averagePercentage >= 70) {
            grade = "C";
        } else if (averagePercentage >= 60) {
            grade = "D";
        } else {
            grade = "F";
        }
    }

    // Getters for the View Layer
    public int getTotalMarks() { return totalMarks; }
    public double getAveragePercentage() { return averagePercentage; }
    public String getGrade() { return grade; }
}