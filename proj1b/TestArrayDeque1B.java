import static org.junit.Assert.*;
import org.junit.Test;

public class TestArrayDeque1B {
    @Test
    public void testing() {
        StudentArrayDeque<Integer> stuArr = new StudentArrayDeque<Integer>();
        ArrayDequeSolution<Integer> arrDeque = new ArrayDequeSolution<Integer>();
        OperationSequence message = new OperationSequence();

        for (int i = 0; i < 100; i++) {
            double rand = StdRandom.uniform();
            if (rand >= 0.75) {
                stuArr.addFirst(i);
                arrDeque.addFirst(i);
                message.addOperation(new DequeOperation("addFirst", i));
            } else if (rand >= 0.5) {
                stuArr.addLast(i);
                arrDeque.addLast(i);
                message.addOperation(new DequeOperation("addLast", i));
            } else if (rand >= 0.25) {
                message.addOperation(new DequeOperation("removeFirst"));
                assertEquals(message.toString(), arrDeque.removeFirst(), stuArr.removeFirst());
            } else {
                message.addOperation(new DequeOperation("removeLast"));
                assertEquals(message.toString(), arrDeque.removeLast(), stuArr.removeLast());
            }
        }
    }
}
