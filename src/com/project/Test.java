package com.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

public class Test
{
    static void sparseBPlusTreeOperations(int order, int[] arr)
    {
        SparseBPlusTree<BPlusTreeVO, Integer> sparseBPlusTree = new SparseBPlusTree<>(order);
        long time1 = System.nanoTime();

        for (int i = 0; i < arr.length; i++) {
            BPlusTreeVO p = new BPlusTreeVO(arr[i]);
            sparseBPlusTree.insert(p, p.getId());
        }

        long time2 = System.nanoTime();

        System.out.println("Starting B+ Tree Operations");
        //Delete two random keys
        sparseBPlusTree.delete(156680);
        sparseBPlusTree.delete(131133);

        //Five additional deletion/insertion operations
        //Three Insert Operations
        int[] insertNewElements = {140304, 156700, 160022};
        for(int i=0; i < insertNewElements.length; i++){
            BPlusTreeVO p = new BPlusTreeVO(insertNewElements[i]);
            sparseBPlusTree.insert(p, p.getId());
        }

        //Two Delete Operations
        sparseBPlusTree.delete(180976);
        sparseBPlusTree.delete(106289);

        //Search for five random keys
        int[] search = {158644,122427,177197,194358,158181};
        for(int i=0; i < search.length; i++){
            sparseBPlusTree.search(search[i]);
        }

        //Range Search
        sparseBPlusTree.rangeSearch(112000, 113000);

        long time3 = System.nanoTime();

        System.out.println();
        System.out.println("Time taken to insert 10,000 records:" + (time2 - time1));
        System.out.println("Time taken by additional operations:" + (time3 - time2));
    }

    static void denseBPlusTreeOperations(int order, int[] arr)
    {
        DenseBPlusTree<BPlusTreeVO, Integer> denseBPlusTree = new DenseBPlusTree<>(order);
        long time1 = System.nanoTime();

        for (int i = 0; i < arr.length; i++) {
            BPlusTreeVO p = new BPlusTreeVO(arr[i]);
            denseBPlusTree.insert(p, p.getId());
        }

        long time2 = System.nanoTime();

        //Insert two random keys
        int[] insertNewElements = {140250, 150760};
        for(int i=0; i < insertNewElements.length; i++){
            BPlusTreeVO p = new BPlusTreeVO(insertNewElements[i]);
            denseBPlusTree.insert(p, p.getId());
        }

        //Five Additional Insert/Delete Operations
        //Two Insert Operations
        int[] insert = {130250, 150760};
        for(int i=0; i < insert.length; i++){
            BPlusTreeVO p = new BPlusTreeVO(insert[i]);
            denseBPlusTree.insert(p, p.getId());
        }
        //Three Delete Operations
        int[] delete = {180976, 106289, 188295};
        for(int i=0; i < delete.length; i++){
            denseBPlusTree.delete(delete[i]);
        }

        //Search for five random keys
        int[] search = {158644,122427,177197,194358,158181};
        for(int i=0; i < search.length; i++){
            denseBPlusTree.search(search[i]);
        }

        //Range Search
//        denseBPlusTree.rangeSearch(112800, 113000);

        long time3 = System.nanoTime();

        System.out.println("Time taken to insert 10,000 records:" + (time2 - time1));
        System.out.println("Time taken by additional operations:" + (time3 - time2));

    }

    static void twoPassJoinOperations()
    {
        //Test Two-Pass Join Algorithm based on Hashing
        TwoPassJoin twoPassJoin = new TwoPassJoin();
        //Generate the Relation S with 5000 tuples
        twoPassJoin.generateRelationS();

        //Test Relation R with 1000 tuples
        twoPassJoin.generateRelationRWith1000Tuples();
        //Get Final List of tuples based on the following 20 B values
        int[] B = {16090,38306,32232,22194,39808,24356,43929,32480,11486,22635,49067,15756,30340,27683,43306,43300,22238,47388,21043,33405};
        twoPassJoin.getSpecificTuples(B);

        //Test Relation R with 1200 tuples
        twoPassJoin.generateRelationRWith1200Tuples();
    }

    public static void main(String[] args){

        String csvFile = "/Users/rashmeetladhar/Downloads/BPlusTreeArray.csv";
        String line = "";
        String cvsSplitBy = ",";
        int[] arr = {};

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] stringArr = line.split(cvsSplitBy);
                arr = Stream.of(stringArr).mapToInt(Integer::parseInt).toArray();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Test B+ Trees
        sparseBPlusTreeOperations(24, arr);
//        denseBPlusTreeOperations(24, arr);
//        sparseBPlusTreeOperations(13, arr);
//        denseBPlusTreeOperations(13, arr);

        //Test Two Pass Join Algorithm (based on Hashing)
//        twoPassJoinOperations();

    }

}
