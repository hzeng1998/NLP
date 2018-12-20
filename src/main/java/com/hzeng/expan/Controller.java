package com.hzeng.expan;

import com.hzeng.index.DocSearch;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Controller {

    public static void main(String[] args) throws IOException {

        Set<Entity> initial_set = new HashSet<>();
        //initial
        for (String entity : Setting.getSeed()) {
            initial_set.add(new Entity(entity));
        }
        DocSearch searcher = new DocSearch();
        int count = 0;

        while (count < Setting.getSteps()) {

            count++;
            //get context feature

            HashMap<ContextFeature, Double> contextFeatureIntegerHashMap = new HashMap<>();

            ArrayList<Set<Entity>> unsorted_entities = new ArrayList<>();

            for (Entity entity : initial_set) {
                Set<ContextFeature> contextFeatures =  ContextFeature.getContextFeatures(entity, searcher.search(entity.entity_string));
                for (ContextFeature contextFeature : contextFeatures) {

                    if (contextFeatureIntegerHashMap.containsKey(contextFeature)) {
                        contextFeatureIntegerHashMap.put(contextFeature, contextFeatureIntegerHashMap.get(contextFeature) + 1);
                    } else {
                        contextFeatureIntegerHashMap.put(contextFeature, 1.0);
                    }

                    System.out.println(MessageFormat.format("Entity: {0}",entity.entity_string));
                    System.out.println(MessageFormat.format("Context Feature: {0}_{1} ",contextFeature.left_featurel, contextFeature.right_feature));

                }
            }

            ArrayList<ContextFeature> contextFeatures =  ContextFeature.rankContextFeatures(contextFeatureIntegerHashMap, Setting.getContextFeatureRank());

            for (ContextFeature contextFeature : contextFeatures) {
                unsorted_entities.add(Entity.getEntities(
                        contextFeature,
                        searcher.search(contextFeature.left_featurel + " " + contextFeature.right_feature),
                        initial_set
                ));
            }
            //rank entity
            initial_set.addAll(Entity.rankEntities(unsorted_entities, Setting.getEntityRank()));

            System.out.println(MessageFormat.format("The entity set after {0} times iteration is:", count));

            for (Entity entity : initial_set) {
                System.out.println(entity.entity_string + " ");
            }
        }
    }

}
