package lab8;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private class Node {
        K key;
        V value;
        Node left = null;
        Node right = null;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        Node(K key, V value, Node left, Node right) {
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
        }
    }

    private int size;
    private Node root;

    public BSTMap() {
        size = 0;
    }

    @Override
    public void clear() {
        this.size = 0;
        this.root = null;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKeyHelper(key, this.root);
    }

    private boolean containsKeyHelper(K key, Node node) {
        if (node == null) {
            return false;
        }
        int comparing = key.compareTo(node.key);
        if (comparing == 0) {
            return true;
        } else if (comparing > 0) {
            return containsKeyHelper(key, node.right);
        }
        return containsKeyHelper(key, node.left);
    }

    @Override
    public V get(K key) {
        return getHelper(key, this.root);
    }

    private V getHelper(K key, Node node) {
        if (node == null) {
            return null;
        }
        int comparing = key.compareTo(node.key);
        if (comparing == 0) {
            return node.value;
        } else if (comparing > 0) {
            return getHelper(key, node.right);
        }
        return getHelper(key, node.left);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        this.root = putHelper(key, value, this.root);
    }

    private Node putHelper(K key, V value, Node node) {
        if (node == null) {
            this.size += 1;
            return new Node(key, value);
        }
        int comparing = key.compareTo(node.key);
        if (comparing == 0) {
            node.value = value;
        } else if (comparing > 0) {
            node.right = putHelper(key, value, node.right);
        } else {
            node.left = putHelper(key, value, node.left);
        }
        return node;
    }

    @Override
    public Set<K> keySet() {
        return new HashSet<K>(getKeys(this.root));
    }

    private ArrayList<K> getKeys(Node node) {
        if (node == null) {
            return new ArrayList<K>();
        }
        ArrayList<K> keys = new ArrayList<K>();
        keys.add(node.key);
        ArrayList<K> leftKeys = getKeys(node.left);
        ArrayList<K> rightKeys = getKeys(node.right);
        keys.addAll(leftKeys);
        keys.addAll(rightKeys);
        return keys;
    }

    @Override
    public V remove(K key) {
        return removeHelper(key, this.root, null);
    }

    private V removeHelper(K key, Node node, Node parent) {
        if (node == null) {
            return null;
        }
        int comparing = key.compareTo(node.key);
        if (comparing > 0) {
            return removeHelper(key, node.right, node);
        } else if (comparing < 0) {
            return removeHelper(key, node.left, node);
        }
        Node maxLeft = removeMaxLeft(node.left, node);
        if (parent == null) {
            maxLeft.left = node.left;
            maxLeft.right = node.right;
            this.root = maxLeft;
        } else {
            boolean leftOfParent = node.key.compareTo(parent.key) < 0;
            if (maxLeft == null) {
                if (leftOfParent) {
                    parent.left = node.right;
                } else {
                    parent.right = node.right;
                }
            } else {
                if (leftOfParent) {
                    parent.left = maxLeft;
                } else {
                    parent.right = maxLeft;
                }
                maxLeft.left = node.left;
                maxLeft.right = node.right;
            }
        }
        return node.value;
    }


    private Node removeMaxLeft(Node node, Node parent) {
        if (node == null || parent == null) {
            return null;
        }
        if (node.right == null) {
            boolean leftOfParent = node.key.compareTo(parent.key) < 0;
            if (leftOfParent) {
                parent.left = node.left;
            } else {
                parent.right = node.left;
            }
            return node;
        }
        return removeMaxLeft(node.right, node);
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
