public class Parser {
    public Parser() {
    }

    public int eval1(String str) {

        int answer = 0;

        int i = 0;

        str.replaceAll("\\s+", "");

        System.out.print(str);

        char[] c_a = str.toCharArray();

        while (i < c_a.length) {

            if (c_a[i] == '+') { // start of handling additions.

                if (c_a[i + 1] == '-') {
                    answer = answer - c_a[i + 2];
                    i = i + 2;

                } else {
                    answer = answer + c_a[i];

                }

            } else if (c_a[i] == '*') {

            }
            if (c_a[i] == '-') { // what about two negative numbers multiplied?
                answer = -(answer * c_a[i + 2]);
                i = i + 2;
            }

            i++;

            // 8 +2 = 10 8 + 7 * 7 = 8 + -8 + 7 * -7 + 8=

        }

        return 0;
    }

    public int eval2(String str) {
        return 0;
    }
}
