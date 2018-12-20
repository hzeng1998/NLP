package com.hzeng.expan;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

import java.util.List;

public class EntityTag {

    public boolean tagEntity(String args) {

        List<Term> tagged =  HanLP.segment(args);

        if (tagged.size() == 1 && tagged.get(0).nature.startsWith("n"))
            return true;

        for (Term term : tagged) {
            if (! term.nature.startsWith("n") || term.nature.toString().equals("ng")) {
                return false;
            }
        }
        return true;
    }
}
