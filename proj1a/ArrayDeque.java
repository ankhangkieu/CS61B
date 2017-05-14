public class ArrayDeque<Item> {
    private int size;
    private int first;          //index of the first element
    private int maxSize = 8;    //start with the size of ...
    private Item[] items;

    private void resize(int capacity) {
        Item[] newItems = (Item[]) new Object[capacity];
        for (int i = 0; i < size; i++) {     //Copy index to new array
            newItems[i] = get(i);
        }
        items = newItems;
        first = 0;      //Set first back to 0
    }

    public ArrayDeque() {
        size = 0;
        first = 0;
        items = (Item[]) new Object[maxSize];
    }

    public void addFirst(Item item) {
        if (size >= maxSize) {              //Increase size if full
            resize(maxSize * 2);
            maxSize *= 2;
        }
        if (first == 0) {                   //Move first to last ele
            first = maxSize - 1;
        } else {                            //Move first back
            first -= 1;
        }
        items[first] = item;
        size += 1;
    }

    public void addLast(Item item) {
        if (size >= maxSize) {              //Increase size if full
            resize(maxSize * 2);
            maxSize *= 2;
        }
        items[(first + size) % maxSize] = item;
        size += 1;
    }

    public Item removeFirst() {
        if (isEmpty()) {                    //Null if empty
            return null;
        }
        Item item = get(0);
        items[first] = null;
        first = (first + 1) % maxSize;   //If first goes above maxSize then resize it to < maxSize
        size -= 1;
        if (maxSize >= 16 && ((double) size) / ((double) maxSize) < 0.25) {
            resize(maxSize / 2);
            maxSize /= 2;
        }
        return item;
    }

    public Item removeLast() {
        if (isEmpty()) {                    //Null if empty
            return null;
        }
        Item item = get(size - 1);
        items[(first + size - 1) % maxSize] = null;
        size -= 1;
        if (maxSize >= 16 && ((double) size) / ((double) maxSize) < 0.25) {
            resize(maxSize / 2);
            maxSize /= 2;
        }
        return item;
    }

    public Item get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        return items[(first + index) % maxSize];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(get(i) + " ");
        }
        System.out.println();
    }
}
