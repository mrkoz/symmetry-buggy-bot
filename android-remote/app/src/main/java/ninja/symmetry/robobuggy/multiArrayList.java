package ninja.symmetry.robobuggy;

import android.util.SparseArray;

import java.util.ArrayList;

public class multiArrayList {
    SparseArray<ArrayList<Integer>> lists;
    public int listCount = 0;
    public int listSize = 0;

    public multiArrayList(int numberOfLists, int size) {
        lists = new SparseArray<ArrayList<Integer>>();
        listCount = numberOfLists;
        listSize = size;

        for (int i =0; i < numberOfLists; i++){
            lists.put(i, new ArrayList<Integer>(size));
        }
    }

    public multiArrayList() {
        lists = new SparseArray<ArrayList<Integer>>();
    }

    public void createDummyData() {
        for (int i = 0; i < listCount; i++) {
            for (int num = 0; num < listSize; num++) {
                this.getListAt(i).add(num, i + num);
            }
        }
    }

    public ArrayList<Integer> getListAt(int index) {
        if (lists.size() > index) {
            return lists.get(index);
        }
        return null;
    }

    public Integer getValueAt(int list, int item) {
        return this.getListAt(list).get(item);
    }

    public void setValueAt(int list, int item, int value) {
        this.getListAt(list).set(item, value);
    }

    public void duplicateListFromTo(int from, int to) {
        for (int item = 0; item < listSize; item++) {
            setValueAt(to, item,  getListAt(from).get(item));
        }
    }
}
