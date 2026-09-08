public class Example {
    public static void main(String[] args) {
        if (Runtime.version().feature() != 25) {
            throw new AssertionError("Unexpected runtime: " + Runtime.version());
        }
        if (!new KotlinExample().message().equals("hello")) {
            throw new AssertionError("Kotlin code did not execute correctly");
        }
    }
}
