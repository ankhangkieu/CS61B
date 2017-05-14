public class LinkedListDeque<Item> {
    private class Node {
        private Item data;
        private Node next;
        private Node prev;

        private Node(Item data, Node next, Node prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    private int size;
    private Node sentinel;

    private Item getRecursiveHelper(Node curNode, int index) {
        if (index == 0) {
            return curNode.data;
        }
        return getRecursiveHelper(curNode.next, index - 1);
    }

    public LinkedListDeque() {
        size = 0;
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
    }

    public void addFirst(Item item) {
        Node newItem = new Node(item , sentinel.next, sentinel);
        sentinel.next.prev = newItem;
        sentinel.next = newItem;
        size += 1;
    }

    public void addLast(Item item) {
        Node newItem = new Node(item, sentinel, sentinel.prev);
        sentinel.prev.next = newItem;
        sentinel.prev = newItem;
        size += 1;
    }

    public Item getRecursive(int index) {
        if (index >= size) {
            return null;
        }
        return getRecursiveHelper(sentinel.next, index);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node cur = sentinel.next;
        for (int i = 0; i < size; i++, cur = cur.next) {
            System.out.print(cur.data + " ");
        }
        System.out.println();
    }

    public Item removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Node first = sentinel.next;
        sentinel.next = first.next;
        first.next.prev = sentinel;
        size -= 1;
        return first.data;
    }

    public Item removeLast() {
        if (isEmpty()) {
            return null;
        }
        Node last = sentinel.prev;
        sentinel.prev = last.prev;
        last.prev.next = sentinel;
        size -= 1;
        return last.data;
    }

    public Item get(int index) {
        if (index >= size) {
            return null;
        }
        Node run = sentinel.next;
        while (index != 0) {
            run = run.next;
            index -= 1;
        }
        return run.data;
    }
}
