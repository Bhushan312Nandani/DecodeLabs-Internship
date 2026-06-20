class ReportGenerator {
    public void printReport(Student student, GradeCalculator calc) {
        System.out.println("\n=================================");
        System.out.println("   ACADEMIC PERFORMANCE REPORT   ");
        System.out.println("=================================");
        System.out.printf("Student Name:    %s%n", student.getName());
        System.out.printf("Total Marks:     %d%n", calc.getTotalMarks());
        System.out.printf("Average:         %.2f%n", calc.getAveragePercentage());
        System.out.printf("Final Grade:     %s%n", calc.getGrade());
        System.out.println("=================================\n");
    }
}
