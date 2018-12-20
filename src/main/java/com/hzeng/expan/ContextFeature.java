package com.hzeng.expan;

import com.hzeng.StreamUtils;

import java.util.*;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

public class ContextFeature {

    String left_featurel;
    String right_feature;

    ContextFeature(String left_featurel, String right_feature) {
        this.left_featurel = left_featurel;
        this.right_feature = right_feature;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (obj instanceof ContextFeature) {
            ContextFeature contextFeature = (ContextFeature) obj;
            return contextFeature.left_featurel.equals(this.left_featurel) && contextFeature.right_feature.equals(this.right_feature);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return left_featurel.hashCode() & right_feature.hashCode();
    }

    private static String getLeftFeature(String para, int pos) {

        StringBuilder left = new StringBuilder();

        int end = max(0, pos - Setting.getContextWindowSize() + 1);
        for (int index = pos; index >= end; index--) {
            if (Setting.isInStopCharactor(para.charAt(index)))
                break;
            left.append(para.charAt(index));
        }

        return left.reverse().toString();
    }

    private static String getRightFeature(String para, int pos) {

        StringBuilder right = new StringBuilder();

        int end = min(para.length(), pos + Setting.getContextWindowSize());
        for (int index = pos; index < end; index++) {
            if (Setting.isInStopCharactor(para.charAt(index)))
                break;
            right.append(para.charAt(index));
        }

        return right.toString();
    }

    public static Set<ContextFeature> getContextFeatures(Entity entity, ArrayList<String> paras) {

        Set<ContextFeature> contextFeatures = new HashSet<>();

        for (String para : paras) {

            ArrayList<Integer> positions = new ArrayList<>();
            positions.add(-entity.entity_string.length());
            int last = -entity.entity_string.length();

            do {
                positions.add(last = para.indexOf(entity.entity_string, last + entity.entity_string.length()));

                ContextFeature contextFeature = new ContextFeature( getLeftFeature(para, last - 1),
                        getRightFeature(para, last + entity.entity_string.length()));

                if (!(contextFeature.left_featurel.equals(contextFeature.right_feature) && contextFeature.left_featurel.equals("")))
                    contextFeatures.add(contextFeature);

            } while (last != -1);

        }

        return contextFeatures;
    }

    public static ArrayList<ContextFeature> rankContextFeatures(HashMap<ContextFeature, Double> contextFeatureIntegerHashMap, int rank) {

        return StreamUtils.rankHashMap(contextFeatureIntegerHashMap, rank);
    }
}
