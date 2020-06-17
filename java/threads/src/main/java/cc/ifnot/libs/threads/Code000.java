package cc.ifnot.libs.threads;

import java.util.Stack;

public class Code000 {

    public static void main(String[] args) {
        Solution solution = new Solution();
        for (String arg : args) {
            solution.simplifyPath(arg);
        }
    }

    final static class Solution {
        public String simplifyPath(String path) {
            Stack<String> stack = new Stack<>();

            stack.push("/");

            String[] arr = path.split("/");

            for (String s : arr) {

                if (s.equals("..")) {
                    if (!stack.isEmpty()) {
                        stack.pop();
                    }
                } else if (!s.equals("") && !s.equals(".")) {
                    stack.push(s);
                }
            }
            if (stack.isEmpty()) {
                return "/";
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < stack.size(); i++) {
                sb.append("/" + stack.get(i));
            }
            return sb.toString();
        }
    }
}
