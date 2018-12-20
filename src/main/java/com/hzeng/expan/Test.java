
package com.hzeng.expan;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

import java.util.List;

public class Test {


    public static void main(String[] args) {

        List<Term> tagged =  HanLP.segment("皮损数量");

        for (Term term : tagged) {
            System.out.println(term.nature.toString());
        }
    }
}
