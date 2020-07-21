package cc.ifnot.java.libs;

import org.junit.jupiter.api.Test;

import java.util.Stack;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/7/20 6:33 PM
 * description:
 */
class Code {
    // a -> b -> c
    // 遍历 找到 c 记录 head =c
    // c-> a

    @Test
    Node reverse(Node node) {
        Node newNode = node;
        while (newNode.next != null) {
            newNode = node.next;
        }
        Node ret = newNode;
        Node current = node;
        while (current != ret) {
            newNode.next = current;
            newNode = newNode.next;
            current = current.next;
        }
        return ret;
    }

    @Test
    void test() {
        Node node = null;
        Node head = null;
        for (int i = 0; i < 10; i++) {
            if(node == null) {
                head = node = new Node();
                node.id = i;
            } else {
                node.next = new Node();
                node.next.id = i;
                node = node.next;
            }
        }
        Node current = head;
        while (current != null) {
            Lg.d("%s ", current.id);
            current = current.next;
        }

        final Node nn = reverseLinked(head);

        current = nn;
        while (current != null) {
            Lg.d("%s ", current.id);
            current = current.next;
        }

    }

    Node reverseLinked(Node node) {
        final Stack<Node> stack = new Stack<>();

        Node n = node;
        while (n != null) {
            stack.push(n);
            n = n.next;
        }
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        });

        Node current = stack.pop();
        Node ret = current;
        Node prev = null;
        while (current != null) {
            prev = current;
            current = stack.pop();
            prev.next = current;
        }
        current.next = null;
        return current;
    }

    class Node {
        int id;
        Node next;
    }

}
