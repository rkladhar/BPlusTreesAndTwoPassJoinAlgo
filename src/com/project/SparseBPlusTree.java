package com.project;

import java.util.ArrayList;

public class SparseBPlusTree<T, V extends Comparable<V>>
{

    /**
     * Implement B+ tree
     *
     * @param <T> specifies the value type
     * @param <V> uses generics, specifies the index type, and specifies that you must inherit Comparable
     */
    //B+ tree order
    private Integer bTreeOrder;
    // The minimum number of keys for a non-leaf node
    private Integer minKeys;
    //The maximum number of keys for a non-leaf node
    private Integer maxKeys;

    private Node<T, V> root;

    private LeafNode<T, V> leafNode;

    // No parameter construction method, the default order is 3
    public SparseBPlusTree()
    {
        this(3);
    }

    // There is a construction method, you can set the order of the B + tree
    public SparseBPlusTree(Integer bTreeOrder)
    {
        this.bTreeOrder = bTreeOrder;
        this.minKeys = (int) (Math.ceil((bTreeOrder + 1)/ 2.0) - 1.0);
        // Because there may be more than the upper limit during the insertion of the node, so add 1 here
        this.maxKeys = bTreeOrder;
        this.root = new LeafNode<T, V>();
        this.leafNode = null;
    }

    //Search
    public T search(V key)
    {
        System.out.println("Searching key: " + key);
        T t = this.root.search(key);
        if(t == null){
            System.out.println("Key: " + key + " does not exist!");
        }
        return t;
    }

    public T rangeSearch(V key1, V key2)
    {
        System.out.println("Range search between keys: " + key1 + "- " + key2);
        T t = this.root.rangeSearch(key1, key2);
        if(t == null){
            System.out.println("No keys found in the range: " + key1 + "-" + key2);
        }
        else{
            System.out.println("Keys in the range: " + key1 + "-" + key2 + " are as follows:");
            System.out.println(t.toString());
        }
        return t;
    }

    //Insert
    public void insert(T value, V key)
    {
        System.out.println("Inserting key: " + key);
        if(key == null)
            return;
        Node<T, V> t = this.root.insert(value, key);
        if(t != null){
            this.root = t;
        }
        this.leafNode = (LeafNode<T, V>)this.root.refreshLeft();

        System.out.println("Insert completed!");
        System.out.println();
    }

    //Delete
    public void delete(V key)
    {
        System.out.println();
        System.out.println("Deleting key: " + key);
        if(key == null)
            return;
        Node<T, V> t = this.root.delete(key);
        if(t != null){
            this.root = t;
        }
        this.leafNode = (LeafNode<T, V>)this.root.refreshLeft();

        System.out.println("Finished deleting!");
        System.out.println();
    }


    /**
     * Node parent class, because in the B+ tree, non-leaf nodes do not need to store specific data, just need to use the index as a key.
     * So the leaves and non-leaf nodes are not the same, but they share some methods, so use the Node class as the parent class.
     * And because you want to call some public methods to each other, use abstract classes
     *
     * @param <T> with BPlusTree
     * @param <V>
     */
    abstract class Node<T, V extends Comparable<V>>{
        //parent node
        protected Node<T, V> parent;
        // child node
        protected Node<T, V>[] childNodes;
        // number of keys (child nodes)
        protected Integer number;
        //key
        protected Object keys[];

        //Construction method
        public Node(){
            this.keys = new Object[maxKeys];
            this.childNodes = new Node[maxKeys + 2];
            this.number = 0;
            this.parent = null;
        }

        //Search
        abstract T search(V key);

        //Range Search
        abstract T rangeSearch(V key1, V key2);

        //Insert
        abstract Node<T, V> insert(T value, V key);

        //Delete
        abstract Node<T, V> delete(V key);

        abstract LeafNode<T, V> refreshLeft();
    }


    /**
     * Non-leaf node class
     * @param <T>
     * @param <V>
     */

    class NonLeafNode <T, V extends Comparable<V>> extends Node<T, V> {

        public NonLeafNode() {
            super();
        }

        /**
         * Recursive lookup, here is just to determine exactly which value the value is in, the real search the leaf node will check
         *
         * @param key
         * @return
         */
        @Override
        T search(V key) {
            int i = 0;
            while (i < this.number) {
                if (key.compareTo((V) this.keys[i]) < 0) {
                    System.out.println("Non-leaf node: lookup key - " + this.keys[i]);
                    break;
                }
                if (this.number > i + 1) {
                    if ((key.compareTo((V) this.keys[i]) >= 0) && (key.compareTo((V) this.keys[i + 1]) < 0)) {
                        System.out.println("Non-leaf node: lookup key - " + this.keys[i]);
                        i++;
                        break;
                    }
                } else if (this.number == (i + 1) && (key.compareTo((V) this.keys[i]) >= 0)) {
                    System.out.println("Non-leaf node: lookup key - " + this.keys[i]);
                    i++;
                    break;
                }
                i++;
            }
            return this.childNodes[i].search(key);
        }

        @Override
        T rangeSearch(V key1, V key2) {
            int i = 0;
            while (i < this.number) {
                if (key1.compareTo((V) this.keys[i]) < 0) {
                    System.out.println("Non-leaf node: lookup key - " + this.keys[i]);
                    break;
                }
                if (this.number > i + 1) {
                    if ((key1.compareTo((V) this.keys[i]) >= 0) && (key1.compareTo((V) this.keys[i + 1]) < 0)) {
                        System.out.println("Non-leaf node: lookup key - " + this.keys[i]);
                        i++;
                        break;
                    }
                } else if (this.number == (i + 1) && (key1.compareTo((V) this.keys[i]) >= 0)) {
                    System.out.println("Non-leaf node: lookup key - " + this.keys[i]);
                    i++;
                    break;
                }
                i++;
            }
            return this.childNodes[i].rangeSearch(key1, key2);
        }

        /**
         * Recursive insertion, first insert the value into the corresponding leaf node, and finally call the insert class of the leaf node
         *
         * @param value
         * @param key
         */
        @Override
        Node<T, V> insert(T value, V key) {
            int i = 0;
            while (i < this.number) {
                if (key.compareTo((V) this.keys[i]) < 0) {
                    System.out.println("Non-leaf node: lookup key - " + this.keys[i]);
                    break;
                }
                if (this.number > i + 1) {
                    if ((key.compareTo((V) this.keys[i]) >= 0) && (key.compareTo((V) this.keys[i + 1]) < 0)) {
                        System.out.println("Non-leaf node: lookup key - " + this.keys[i]);
                        i++;
                        break;
                    }
                } else if (this.number == (i + 1) && (key.compareTo((V) this.keys[i]) >= 0)) {
                    System.out.println("Non-leaf node: lookup key - " + this.keys[i]);
                    i++;
                    break;
                }
                i++;
            }

            return this.childNodes[i].insert(value, key);
        }

        @Override
        LeafNode<T, V> refreshLeft() {
            return this.childNodes[0].refreshLeft();
        }

        /**
         * When the leaf node inserts successfully completes the decomposition, recursively inserts a new node to the parent node to maintain balance
         *
         * @param node1
         * @param node2
         * @param key
         */
        Node<T, V> insertNode(Node<T, V> node1, Node<T, V> node2, V key, boolean update, V nKey) {

            V oldKey = null;
            V newKey = null;

            // If the original key is null, indicating that this non-node is empty, you can directly put in two nodes
            if ((key == null && !update) || this.number <= 0) {
                if (key != null) {
                    this.keys[0] = key;
                } else {
                    this.keys[0] = node2.keys[0];
                }
                this.childNodes[0] = node1;
                this.childNodes[1] = node2;
                this.number += 1;
                return this;
            }
            if (!update) {
                // The original node is not empty, you should first search the location of the original node, and then insert the new node into the original node
                int i = 0;
                while (i < this.number) {
                    if (key.compareTo((V) this.keys[i]) < 0)
                        break;
                    i++;
                }

                Object tempKeys[] = new Object[maxKeys + 1];
                Object tempChildNodes[] = new Node[maxKeys + 2];

                System.arraycopy(this.keys, 0, tempKeys, 0, i);
                System.arraycopy(this.childNodes, 0, tempChildNodes, 0, i);
                System.arraycopy(this.keys, i, tempKeys, i + 1, this.number - i);
                System.arraycopy(this.childNodes, i + 1, tempChildNodes, i + 2, this.number - i);
                tempKeys[i] = key;
                tempChildNodes[i] = node1;
                tempChildNodes[i + 1] = node2;

                this.number++;

                // Determine whether you need to split
                // If you do not need to split, copy the array back, return directly
                if (this.number <= bTreeOrder) {
                    System.arraycopy(tempKeys, 0, this.keys, 0, this.number);
                    System.arraycopy(tempChildNodes, 0, this.childNodes, 0, this.number + 1);

                    System.out.println("Non-leaf node: insert key - " + key + ", no split required");

                    return null;
                }

                System.out.println("Non-leaf node: insert key - " + key + ", need to split");
                System.out.println("Non-leaf node: before the split - ");
                System.out.print("[");
                for (int j = 0; j < this.keys.length; j++) {
                    if (this.keys[j] != null) {
                        System.out.print(this.keys[j]);
                        System.out.print(" ");
                    }
                }
                System.out.println("]");

//            // If you need to split, and similar to the removal of the leaf node, open from the middle
//            Integer middle = this.number / 2;

                // Create a new non-leaf node, as the right half of the split
                NonLeafNode<T, V> tempNode = new NonLeafNode<T, V>();
                // After the non-leaf node split, the parent node pointer of its child node should be updated to the correct pointer
                tempNode.number = this.number - minKeys - 1;
                tempNode.parent = this.parent;
                //If the parent node is empty, create a new non-leaf node as the parent node, and let the pointers of the two non-leaf nodes that are successfully split point to the parent node.
                if (this.parent == null) {

                    NonLeafNode<T, V> tempNonLeafNode = new NonLeafNode<>();
                    tempNode.parent = tempNonLeafNode;
                    this.parent = tempNonLeafNode;
                    oldKey = null;
                }
                System.arraycopy(tempKeys, minKeys + 1, tempNode.keys, 0, tempNode.number);
                System.arraycopy(tempChildNodes, minKeys + 1, tempNode.childNodes, 0, tempNode.number + 1);
                for (int j = 0; j <= tempNode.number; j++) {
                    tempNode.childNodes[j].parent = tempNode;
                }
                // Let the original non-leaf node as the left node
                this.number = minKeys;
                this.keys = new Object[maxKeys];
                this.childNodes = new Node[maxKeys + 1];
                System.arraycopy(tempKeys, 0, this.keys, 0, minKeys);
                System.arraycopy(tempChildNodes, 0, this.childNodes, 0, minKeys + 1);

                if (this.number > 0) {
                    oldKey = (V) tempKeys[minKeys];
                }

                System.out.println("Non-leaf-node: after the split - ");
                System.out.print("[");
                for (int j = 0; j < this.keys.length; j++) {
                    if (this.keys[j] != null) {
                        System.out.print(this.keys[j]);
                        System.out.print(" ");
                    }
                }
                System.out.print("] ");
                System.out.print("[");
                for (int j = 0; j < tempNode.number; j++) {
                    if (tempNode.keys[j] != null) {
                        System.out.print(tempNode.keys[j]);
                        System.out.print(" ");
                    }
                }
                System.out.println("]");

                //After the leaf node is successfully split, the newly generated node needs to be inserted into the parent node.
                NonLeafNode<T, V> parentNode = (NonLeafNode<T, V>) this.parent;
                return parentNode.insertNode(this, tempNode, oldKey, false, newKey);
            } else {
                // The original node is not empty, you should first search the location of the original node, and then insert the new node into the original node
                int i = 0;
                while (i < this.number) {
                    if (key.compareTo((V) this.keys[i]) == 0)
                        break;
                    i++;
                }

                Object tempKeys[] = new Object[maxKeys];

                System.arraycopy(this.keys, 0, tempKeys, 0, i);
                System.arraycopy(this.keys, i, tempKeys, i + 1, this.number - i - 1);
                tempKeys[i] = nKey;
                System.arraycopy(tempKeys, 0, this.keys, 0, this.number);
                this.childNodes[i + 1] = node2;

                return null;
            }

        }

        @Override
        Node<T, V> delete(V key) {
            int i = 0;
            while (i < this.number) {
                if (key.compareTo((V) this.keys[i]) < 0) {
                    System.out.println("Non-leaf node: lookup key - " + this.keys[i]);
                    break;
                }
                if (this.number > i + 1) {
                    if ((key.compareTo((V) this.keys[i]) >= 0) && (key.compareTo((V) this.keys[i + 1]) < 0)) {
                        System.out.println("Non-leaf node: lookup key - " + this.keys[i]);
                        i++;
                        break;
                    }
                } else if (this.number == (i + 1) && (key.compareTo((V) this.keys[i]) >= 0)) {
                    System.out.println("Non-leaf node: lookup key - " + this.keys[i]);
                    i++;
                    break;
                }
                i++;
            }

            return this.childNodes[i].delete(key);
        }

        Node<T, V> deleteNode(V key) {

            V oldKey = null;
            V newKey = null;

            // The original node is not empty, you should first search the location of the original node, and then insert the new node into the original node
            int i = 0;
            while (i < this.number) {
                if (key.compareTo((V) this.keys[i]) == 0)
                    break;
                i++;
            }
            if(i == this.number){
                int j = 0;
                while (j < this.number) {
                    if (key.compareTo((V) this.keys[j]) < 0)
                        break;
                    j++;
                }
                i=j;
            }
            Object tempKeys[] = new Object[maxKeys];

            System.arraycopy(this.keys, 0, tempKeys, 0, i);
            System.arraycopy(this.keys, i + 1, tempKeys, i, this.number - i - 1);

            this.number--;

            // Determine whether you need to split
            // If you do not need to split, copy the array back, return directly
            if (this.number >= minKeys) {
                System.arraycopy(tempKeys, 0, this.keys, 0, this.number);

                System.out.println("Non-leaf node: delete key - " + key + ", no coalescence required");

                return null;
            }

            System.out.println("Non-leaf node: delete key - " + key + ", need to coalesce");
            System.out.println("Non-leaf node: before the coalescence - ");
            System.out.print("[");
            for(int j=0; j<this.keys.length; j++){
                if (this.keys[j] != null) {
                    System.out.print(this.keys[j]);
                    System.out.print(" ");
                }
            }
            System.out.println("]");
            return null;
        }
    }

    /**
     * Leaf node class
     * @param <T>
     * @param <V>
     */
    class LeafNode <T, V extends Comparable<V>> extends Node<T, V> {

        protected Object values[];
        protected LeafNode left;
        protected LeafNode right;

        public LeafNode(){
            super();
            this.values = new Object[maxKeys];
            this.left = null;
            this.right = null;
        }

        /**
         * search, classic binary search, no more comments
         * @param key
         * @return
         */
        @Override
        T search(V key) {
            if(this.number <=0)
                return null;

            int i = 0;
            while(i < this.number){
                if(key.compareTo((V) this.keys[i]) == 0){
                    System.out.println("Leaf-node: Key found in node - ");
                    System.out.print("[");
                    for(int j=0; j<this.keys.length; j++){
                        if (this.keys[j] != null) {
                            System.out.print(this.keys[j]);
                            System.out.print(" ");
                        }
                    }
                    System.out.println("]");
                    System.out.println();
                    return (T) this.values[i];
                }
                i++;
            }
            return null;
        }

        @Override
        T rangeSearch(V key1, V key2) {
            ArrayList<Object> keyValues = new ArrayList<Object>();
            if(this.number <=0)
                return null;

            if(this.parent == null){
                int i = 0;
                while(i < this.number){
                    if(key1.compareTo((V) this.keys[i]) <= 0){
                        keyValues.add(this.values[i]);
                    }
                    i++;
                }
            }

            else if(this.parent != null){
                int k = 0;
                while(this.parent.childNodes[k] != null){
                    int x = 0;
                    while(x < this.parent.childNodes[k].number){
                        if((key1.compareTo((V) this.parent.childNodes[k].keys[x]) <= 0) && (key2.compareTo((V) this.parent.childNodes[k].keys[x]) >= 0)){
                            keyValues.add(this.parent.childNodes[k].keys[x]);
                        }
                        else{
                            break;
                        }
                        x++;
                    }
                    k++;
                }
            }
            return (T) keyValues;
        }

        /**
         *
         * @param value
         * @param key
         */
        @Override
        Node<T, V> insert(T value, V key) {

//             System.out.println("leaf node, insert key: " + key);

            // Save the key value of the original existence of the parent node
            V oldKey = null;
            V newKey = null;
            // Insert data first
            int i = 0;
            while(i < this.number){
                if(key.compareTo((V) this.keys[i]) < 0)
                {
                    System.out.println("Leaf-node: Insert key, before- " + this.keys[i]);
                    break;
                }
                i++;
            }

            // Copy the array, complete the addition
            Object tempKeys[] = new Object[maxKeys + 1];
            Object tempValues[] = new Object[maxKeys + 1];
            System.arraycopy(this.keys, 0, tempKeys, 0, i);
            System.arraycopy(this.values, 0, tempValues, 0, i);
            System.arraycopy(this.keys, i, tempKeys, i + 1, this.number - i);
            System.arraycopy(this.values, i, tempValues, i + 1, this.number - i);
            tempKeys[i] = key;
            tempValues[i] = value;

            this.number++;

//             System.out.println("Insert completed, current node key is:");
//                        for(int j = 0; j < this.number; j++)
//                            System.out.print(tempKeys[j] + " ");
//                        System.out.println();

            // Determine whether you need to split
            // If you do not need to split to complete the copy and return directly
            if(this.number <= bTreeOrder){
                System.arraycopy(tempKeys, 0, this.keys, 0, this.number);
                System.arraycopy(tempValues, 0, this.values, 0, this.number);

                System.out.println("Leaf-node: insert key - " + key + ", no splitting is required");

                return null;
            }

             System.out.println("Leaf-node: insert key - " + key + ", need to split");
            System.out.println("Leaf-node: before the split - ");
            System.out.print("[");
            for(int j=0; j<this.keys.length; j++){
                if (this.keys[j] != null) {
                    System.out.print(this.keys[j]);
                    System.out.print(" ");
                }
            }
            System.out.println("]");

            // New leaf node, as the right half of the split
            LeafNode<T, V> tempNode = new LeafNode<T, V>();
            tempNode.number = this.number - minKeys;
            tempNode.parent = this.parent;
            //If the parent node is empty, create a new non-leaf node as the parent node, and let the pointers of the two leaf nodes that are successfully split point to the parent node.
            if(this.parent == null) {

                System.out.println("Leaf-node: insert key - " + key + ", parent node is empty, create new parent node");

                NonLeafNode<T, V> tempNonLeafNode = new NonLeafNode<>();
                tempNode.parent = tempNonLeafNode;
                this.parent = tempNonLeafNode;
                oldKey = null;
            }
            System.arraycopy(tempKeys, minKeys, tempNode.keys, 0, tempNode.number);
            System.arraycopy(tempValues, minKeys, tempNode.values, 0, tempNode.number);

            // Let the original leaf node as the left half of the split
            this.number = minKeys;
            this.keys = new Object[maxKeys];
            this.values = new Object[maxKeys];
            System.arraycopy(tempKeys, 0, this.keys, 0, minKeys);
            System.arraycopy(tempValues, 0, this.values, 0, minKeys);

            if(this.right != null){
                LeafNode<T, V> tempRightNode = new LeafNode<T, V>();
                tempRightNode = this.right;
                this.right = tempNode;
                this.right.right = tempRightNode;
            }
            else{
                this.right = tempNode;
            }
            tempNode.left = this;
            if(this.number > 0){
                oldKey = (V) this.right.keys[0];
            }
            System.out.println("Leaf-node: after the split - ");
            System.out.print("[");
            for(int j=0; j<this.keys.length; j++){
                if(this.keys[j] != null){
                    System.out.print(this.keys[j]);
                    System.out.print(" ");
                }
            }
            System.out.print("] ");
            System.out.print("[");
            for(int j=0; j<tempNode.number; j++){
                if(tempNode.keys[j] != null){
                    System.out.print(tempNode.keys[j]);
                    System.out.print(" ");
                }
            }
            System.out.println("]");

            //After the leaf node is successfully split, the newly generated node needs to be inserted into the parent node.
            NonLeafNode<T, V> parentNode = (NonLeafNode<T, V>)this.parent;
            return parentNode.insertNode(this, tempNode, oldKey, false, newKey);
        }

        @Override
        LeafNode<T, V> refreshLeft() {
            if(this.number <= 0)
                return null;
            return this;
        }

        @Override
        Node<T, V> delete(V key) {
            System.out.println("leaf node, delete key: " + key);

            // Save the key value of the original existence of the parent node
            V oldKey = null;
            V newKey = null;
            // Insert data first
            int i = 0;
            while(i < this.number){
                if(key.compareTo((V) this.keys[i]) == 0)
                    break;
                i++;
            }
            if(i >= this.number){
                System.out.println("Key: " + key + " not found");
                return null;
            }

            // Copy the array, complete the addition
            Object tempKeys[] = new Object[maxKeys];
            Object tempValues[] = new Object[maxKeys];
            System.arraycopy(this.keys, 0, tempKeys, 0, i);
            System.arraycopy(this.values, 0, tempValues, 0, i);
            System.arraycopy(this.keys, i+1, tempKeys, i, this.number - i - 1);
            System.arraycopy(this.values, i+1, tempValues, i, this.number - i - 1);

            this.number--;

//            System.out.println("Insert completed, current node key is:");
//            for(int j = 0; j < this.number; j++)
//                System.out.print(tempKeys[j] + " ");
//            System.out.println();

            // Determine whether you need to split
            // If you do not need to split to complete the copy and return directly
            if(this.number >= minKeys){
                System.arraycopy(tempKeys, 0, this.keys, 0, this.number);
                System.arraycopy(tempValues, 0, this.values, 0, this.number);

                System.out.println("Leaf-node: delete key - " + key + ", no re-distribution is required");

                return null;
            }

            System.out.println("Leaf node: delete key - " + key + ", need to re-distribute");
            System.out.println("Leaf-node: before the redistribution - ");
            System.out.print("[");
            for(int j=0; j<this.keys.length; j++){
                if (this.keys[j] != null) {
                    System.out.print(this.keys[j]);
                    System.out.print(" ");
                }
            }
            System.out.println("]");

            // New leaf node, as the right half of the split
            LeafNode<T, V> tempNode = new LeafNode<T, V>();
            tempNode.number = this.right.number - 1;
            tempNode.parent = this.parent;
            int leftNodeNumber = minKeys;
            oldKey = (V) this.right.keys[0];

            if(tempNode.number < minKeys){
                // Add one element in the left node from the right one
                this.keys = new Object[maxKeys];
                this.values = new Object[maxKeys];
                System.arraycopy(tempKeys, 0, this.keys, 0, this.number);
                System.arraycopy(tempValues, 0, this.values, 0, this.number);
                System.arraycopy(this.right.keys, 0, this.keys, this.number, this.right.number);
                System.arraycopy(this.right.values, 0, this.values, this.number, this.right.number);
                this.number = this.number + this.right.number;
                tempNode.left = this;

                System.out.println("Leaf-node: after redistribution - ");
                System.out.print("[");
                for(int j=0; j<this.keys.length; j++){
                    if (this.keys[j] != null) {
                        System.out.print(this.keys[j]);
                        System.out.print(" ");
                    }
                }
                System.out.println("]");

                NonLeafNode<T, V> parentNode = (NonLeafNode<T, V>)this.parent;
                return parentNode.deleteNode(oldKey);
            }
            else{
                // Add one element in the left node from the right one
                this.number = leftNodeNumber;
                System.arraycopy(this.right.keys, 0, tempKeys, this.number - 1, 1);
                System.arraycopy(this.right.values, 0, tempValues, this.number - 1, 1);
                System.arraycopy(tempKeys, 0, this.keys, 0, this.number);
                System.arraycopy(tempValues, 0, this.values, 0, this.number);

                //Update the right leaf node to have one less element
                System.arraycopy(this.right.keys, 1, tempNode.keys, 0, tempNode.number);
                System.arraycopy(this.right.values, 1, tempNode.values, 0, tempNode.number);
                this.right = tempNode;
                tempNode.left = this;
                if(this.number > 0){
                    newKey = (V) this.right.keys[0];
                }
                System.out.println("Leaf-node: after redistribution - ");
                System.out.print("[");
                for(int j=0; j<this.keys.length; j++){
                    if(this.keys[j] != null){
                        System.out.print(this.keys[j]);
                        System.out.print(" ");
                    }
                }
                System.out.print("] ");
                System.out.print("[");
                for(int j=0; j<tempNode.number; j++){
                    if(tempNode.keys[j] != null){
                        System.out.print(tempNode.keys[j]);
                        System.out.print(" ");
                    }
                }
                System.out.println("]");

                //After the leaf node is successfully split, the newly generated node needs to be inserted into the parent node.
                NonLeafNode<T, V> parentNode = (NonLeafNode<T, V>)this.parent;
                return parentNode.insertNode(this, tempNode, oldKey, true, newKey);
            }
        }
    }

}
