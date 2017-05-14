public class ArrayDequeTest {
    public static void add_removeTest(){
        System.out.println("Testing add functions:");
        ArrayDeque<Integer> array = new ArrayDeque<>();
        array.addFirst(3);
        array.addFirst(2);
        array.addFirst(1);
        array.addLast(4);
        array.addLast(5);
        array.addLast(6);
        array.addFirst(0);
        array.addFirst(-1);
        System.out.println("Expected: -1 0 1 2 3 4 5 6");
        System.out.print("Get: ");
        array.printDeque();
        System.out.println("Size of array, expected: 8; get: "+array.size());

        System.out.println("\nTesting remove functions:");
        array.removeFirst();
        array.removeFirst();
        array.removeLast();
        array.removeLast();
        System.out.println("Expected: 1 2 3 4");
        System.out.print("Get: ");
        array.printDeque();
        System.out.println("Size of array, expected: 4; get: "+array.size());

        System.out.println("\nTesting more on remove functions:");
        System.out.println("Expected: 1\n; get: " + array.removeFirst());
        System.out.println("Expected: 4\n; get: " + array.removeLast());
        System.out.println("Size of array, expected: 2; get: "+array.size());

        System.out.println("\nTesting get function:");
        System.out.println("Expected: 2; get: " + array.get(0));
        System.out.println("Expected: 3; get: " + array.get(1));
        System.out.println("Expected: null; get: " + array.get(10));
    }

    public static void testing(){
        ArrayDeque<Integer> ArrayDeque = new ArrayDeque<>();
        ArrayDeque.addFirst(0);
        ArrayDeque.addFirst(1);
        ArrayDeque.addFirst(2);
        ArrayDeque.isEmpty();
        ArrayDeque.addFirst(4);
        ArrayDeque.addFirst(5);
        ArrayDeque.removeLast();
        ArrayDeque.addFirst(7);
    }

    public static void testing2() {
        ArrayDeque<Integer> ArrayDeque = new ArrayDeque<>();
        ArrayDeque.addLast(0);
        ArrayDeque.removeFirst();
        ArrayDeque.addLast(2);
        ArrayDeque.addLast(3);
        ArrayDeque.addLast(4);
        ArrayDeque.addLast(5);
        ArrayDeque.addLast(6);
    }

    public static void testing3(){
        ArrayDeque<Integer> ArrayDeque = new ArrayDeque<>();
        ArrayDeque.addLast(0);
        ArrayDeque.addLast(1);
        ArrayDeque.printDeque();

        ArrayDeque.addLast(2);
        ArrayDeque.addLast(3);
        ArrayDeque.addLast(4);
        ArrayDeque.addLast(5);
        ArrayDeque.addLast(6);
        ArrayDeque.addLast(7);
        ArrayDeque.printDeque();
    }

    public static void main(String[] args){
        add_removeTest();
        testing();
        testing2();
        testing3();
    }
}