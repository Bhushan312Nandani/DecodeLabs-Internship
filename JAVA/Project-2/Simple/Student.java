import java.util.Stack;
class Student {
    private String name;
    private Stack<Integer> marksStack;

    public Student(String name) {
        this.name = name;
        this.marksStack = new Stack<>();
    }

    public void addMark(int mark) {
        marksStack.push(mark); // Push to the top of the stack
    }

    public Stack<Integer> getMarksStack() {
        return marksStack;
    }

    public String getName() {
        return name;
    }
}