package com.devs.musicalharmonization;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {
        ArrayList<ArrayList<Integer>> arrayLists = new ArrayList<>();
        arrayLists.add(new ArrayList<Integer>( Arrays.asList(1,2,3)));
        arrayLists.add(new ArrayList<Integer>( Arrays.asList(11,22,33)));
        arrayLists.add(new ArrayList<Integer>( Arrays.asList(21,22,23)));
        ArrayList<Integer> result = new ArrayList<>();

        GeneratePermutations(arrayLists,result,3,0);
        System.out.println(result);

        assertEquals(4, 2 + 2);
    }

    void GeneratePermutations(ArrayList<ArrayList<Integer>> Lists, ArrayList<Integer> result, int depth, Integer current)
    {
        if(depth == Lists.size())
        {
            result.add(current);
            return;
        }

        for(int i = 0; i < Lists.get(depth).size(); ++i)
        {
            GeneratePermutations(Lists, result, depth + 1, current + Lists.get(depth).get(i));
        }
    }


}