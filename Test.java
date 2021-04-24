import java.util.function.Function;

public class Test {
    public static void main(String[] args) {
        Parser parser = new Parser();
        // ändra till false om du bara vill se de som misslyckas
        Tester.setVerbose(false);

        Tester.setPart(1, parser);
        Tester.assertEqual("3", 3);
        Tester.assertEqual("3 + 4", 7);
        Tester.assertEqual("3 * 4", 12);
        Tester.assertEqual("3 * 4 * 5", 60);
        Tester.assertEqual("3 * 4 + 5 * 3", 51);
        Tester.assertEqual("3 + 4 + 5 + 3", 15);
        Tester.assertEqual("3 + 4 + -5 + 3", 5);
        Tester.assertEqual("3 + 4 + -5 * -5", -10);
        Tester.assertEqual("3 * 4 * 5 * 3 + 7 * 6 + 9 * 100 + 3 * 89", 10066167);
        Tester.assertEqual("3 * 4 *   5     * 3 + 7    * 6 + 9 * 100 + 3 *     89 * 0", 0);
        Tester.assertError("");
        Tester.assertError("3 3 + 3");
        Tester.assertError("testing something weird");

        Tester.setPart(2, parser);
        Tester.assertEqual("3", 3);
        Tester.assertEqual("3 + 4", 7);
        Tester.assertEqual("3 + 5 * 3", 18);
        Tester.assertEqual("( 3 + 5 ) * 3", 24);
        Tester.assertEqual("( 3 + 5 ) * 3 + 7 * 3", 45);
        Tester.assertEqual("( 3 + 5 ) * 3 + 7 * ( 3 + 3 )", 66);
        Tester.assertEqual("( 3 + 5 ) * 3 + 7 + ( 3 + 3 )", 37);
        Tester.assertEqual("( 3 + 5 ) * 3 + 7 + ( 3 + 3 ) * 5", 61);
        Tester.assertEqual("( 3 + 5 ) * 3 + 7 + ( 3 + 3 ) * ( 5 + 9 )", 115);
        Tester.assertEqual("( 3 + 5 ) * 3 + 7 + ( 3 + 3 ) * ( 5 + 9 * 9 )", 547);
        Tester.assertEqual("( 3 + 5 ) * 3 + 7 + ( 3 + 3 * 7 ) * ( 5 * 9 + 4 * 4 )", 1495);
        Tester.assertError("testing something weird");

        Tester.display();
    }
}

class TestPass {
    public int passed = 0;
    public int failed = 0;
}

class FunctionReturnType {
    public Integer value;
    public Exception exception;

    public FunctionReturnType(Integer value, Exception exception) {
        this.value = value;
        this.exception = exception;
    }
}

class Tester {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    static TestPass part1 = new TestPass();
    static TestPass part2 = new TestPass();
    static Function<String, FunctionReturnType> partFunc;
    static int id = 1;
    static boolean isPart1 = true;
    static boolean verbose = true;

    public static void setVerbose(boolean v) {
        verbose = v;
    }

    public static void display() {
        printLine("_", 40);
        String p = String.format("\n%s", ANSI_GREEN + (part1.passed + part2.passed) + " PASSED" + ANSI_RESET);
        if (part1.failed > 0 || part2.failed > 0) {
            p += String.format(" %s", ANSI_RED + (part1.failed + part2.failed) + " FAILED" + ANSI_RESET);
        }

        System.out.println(p);
    }

    private static void printLine(String str, int count) {
        for (int i = 0; i < count; i++) {
            System.out.print(str);
        }
        System.out.println();
    }

    public static void setPart(int part, Parser parser) {
        isPart1 = part == 1;

        partFunc = isPart1 ? t -> {
            try {
                return new FunctionReturnType(parser.eval1(t), null);
            } catch (Exception e) {
                return new FunctionReturnType(0, e);
            }
        } : t -> {
            try {
                return new FunctionReturnType(parser.eval2(t), null);
            } catch (Exception e) {
                return new FunctionReturnType(0, e);
            }
        };

        if (verbose) {
            printLine("_", 40);
            System.out.println("\n" + (isPart1 ? "PART 1" : "PART 2"));
        }
    }

    public static void assertEqual(String obj1, Integer obj2) {
        FunctionReturnType res = partFunc.apply(obj1);

        if (res.exception == null) {
            assertTest(res.value.equals(obj2), res.value, obj2, obj1);
        } else {
            assertTest(false, res.exception, obj2, obj1);
            res.exception.printStackTrace();
        }
    }

    public static void assertError(String obj1) {
        FunctionReturnType res = partFunc.apply(obj1);

        if (res.exception == null) {
            assertTest(false, res.value, "Exception", obj1);
        } else {
            boolean passed = res.exception.getClass() == InvalidParserException.class;
            assertTest(passed, res.exception.getClass().getTypeName(), InvalidParserException.class.getTypeName(),
                    obj1);
        }
    }

    private static <E, T> void assertTest(boolean passed, E obj1, T obj2, String parseStr) {

        if (isPart1) {
            if (passed)
                part1.passed += 1;
            else
                part1.failed += 1;
        } else {
            if (passed)
                part2.passed += 1;
            else
                part2.failed += 1;
        }

        if (passed && verbose) {
            String output = String.format("%-65s", parseStr + " == " + obj2);
            String check = String.format("%s", ANSI_GREEN + "PASSED" + ANSI_RESET);
            System.out.println(output + check);
        }

        if (!passed) {
            String output = String.format("%-65s", parseStr + " == " + obj2);
            String check = String.format("%s", ANSI_RED + "FAILED" + ANSI_RESET);
            String f = String.format("\tExpected %s but got %s", ANSI_GREEN + obj2 + ANSI_RESET,
                    ANSI_RED + obj1 + ANSI_RESET);
            System.out.println(output + check);
            System.out.println(f);
        }

        id += 1;
    }
}