package com.hzeng.expan;

import com.hzeng.StreamUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Entity {

    String entity_string;

    static EntityTag entityTag = new EntityTag();

    Entity(String entity_string) {
        this.entity_string = entity_string;
    }

    static ArrayList<Set<Entity>> tmpArr;

    static HashMap<Entity, Double> ranked_entities;

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (obj instanceof Entity) {
            Entity entity = (Entity) obj;
            return entity.entity_string.equals(this.entity_string);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return entity_string.hashCode();
    }

    public static Set<Entity> getEntities(ContextFeature contextFeature, ArrayList<String> paras, Set<Entity> seed_entities) {

        Set<Entity> entities = new HashSet<>();

        for (String para : paras) {

            String left_pattern, right_pattern;

            if (contextFeature.left_featurel.equals("")) {
                left_pattern = "(^|[^a-z0-9A-Z\u4e00-\u9fa5])";
            } else {
                left_pattern = '(' + contextFeature.left_featurel + ')';
            }

            if (contextFeature.right_feature.equals("")) {
                right_pattern = "($|[^a-z0-9A-Z\u4e00-\u9fa5])";
            } else {
                right_pattern = '(' + contextFeature.right_feature + ')';
            }


            String pattern = left_pattern + "([a-z0-9A-Z\u4e00-\u9fa5]+)" + right_pattern;

            Pattern r = Pattern.compile(pattern);

            Matcher m = r.matcher(para);

            while (m.find()) {

                Entity new_entity = new Entity(m.group(2));

                if (entityTag.tagEntity(new_entity.entity_string)) {
                    if (!seed_entities.contains(new_entity))
                        entities.add(new Entity(m.group(2)));
                }
            }
        }

        return entities;
    }

    public static ArrayList<Entity> rankEntities(ArrayList<Set<Entity>> entities, int rank) {

        ranked_entities = new HashMap<>();
        tmpArr = new ArrayList<>();

        prerankEntities(0, Setting.getContextFeatureEntityPrerank(), entities);

        return StreamUtils.rankHashMap(ranked_entities, rank);
    }

    public static void prerankEntities(int index, int k, ArrayList<Set<Entity>> arrayList) {

        if(k == 1){

            for (int i = index; i < arrayList.size(); i++) {

                tmpArr.add(arrayList.get(i));

                HashMap<Entity, Double> entityIntegerHashMap = new HashMap<>();

                for (Set<Entity> entities : tmpArr) {

                    for (Entity entity : entities) {

                        if (entityIntegerHashMap.containsKey(entity)) {
                            entityIntegerHashMap.put(entity, entityIntegerHashMap.get(entity) + 1);
                        } else {
                            entityIntegerHashMap.put(entity, 1.0);
                        }
                    }
                }

                ArrayList<Entity> preranked_entities =  StreamUtils.rankHashMap(entityIntegerHashMap, Setting.getEntityRank());
                for (int rank = 0; rank < preranked_entities.size(); rank++) {

                    Entity entity = preranked_entities.get(rank);
                    if (ranked_entities.containsKey(entity)) {
                        ranked_entities.put(entity, ranked_entities.get(entity) + 1.0 / (rank + 1));
                    } else {
                        ranked_entities.put(entity, 1.0 / (rank + 1));
                    }
                }

                tmpArr.remove(arrayList.get(i));
            }
        } else if(k > 1) {

            for (int i = index; i <= arrayList.size() - k; i++) {
                tmpArr.add(arrayList.get(i));
                prerankEntities(i + 1,k - 1, arrayList);
                tmpArr.remove(arrayList.get(i));
            }
        }
    }
}
