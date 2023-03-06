package com.watermelon.domain.utils;


import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 公共方法
 *
 * @author watermelon on 2021/11/21
 */
public class ExcelUtilsX {


    //    /**
    //     * 导出工具
    //     */
    //    public static LinkedHashMap<String, String> getHeaderAlias(
    //            Class<?> modelWithExcelAnnotations,
    //            String... ignoreFieldNames) {
    //
    //        ArrayList<String> annotationFiledNameForHeaderAlia = new ArrayList<>();
    //
    //        annotationFiledNameForHeaderAlia.add(AnnotationsPropertyNameUtil.fieldToStr(ExcelHeader::value));
    //
    //        return getHeaderAlias(
    //                modelWithExcelAnnotations,
    //                ExcelHeader.class, annotationFiledNameForHeaderAlia,
    //                ignoreFieldNames
    //        );
    //    }
    //
    //    public static <A extends Annotation, T> LinkedHashMap<String, String> getHeaderAlias(
    //            Class<?> modelClass,
    //            Class<A> headerAliasAnnotationClass,
    //            List<String> annotationMethodNameForHeaderAlia,
    //            String... ignoreFieldNames) {
    //
    //        Assert.notNull(modelClass);
    //        Assert.notNull(headerAliasAnnotationClass);
    //        Assert.notNull(annotationMethodNameForHeaderAlia);
    //
    //        // 获取model的所有字段,并配置headerAlias
    //        LinkedHashMap<String, String> headerAlias = new LinkedHashMap<>();
    //        for (Field headerAliasField : ReflectUtil.getFields(modelClass)) {
    //
    //            String fieldName = headerAliasField.getName();
    //
    //            if (ArrayUtil.contains(ignoreFieldNames, fieldName)){
    //                continue;
    //            }
    //
    //            if (AnnotationUtil.hasAnnotation(headerAliasField, headerAliasAnnotationClass) == false){
    //                continue;
    //            }
    //
    //            // 从传入的属性中获取第一个不为blank的值赋给fieldAlias
    //            String fieldAlias = "";
    //            for (String annotationMethodName : annotationMethodNameForHeaderAlia) {
    //                // Class<?> annotationReturnType = ReflectUtil.getMethodByNameIgnoreCase(headerAliasAnnotationClass, annotationMethodName).getReturnType();
    //                Object annotationValue = AnnotationUtil.getAnnotationValue(
    //                        headerAliasField, headerAliasAnnotationClass, annotationMethodName
    //                );
    //                if (annotationValue != null && annotationValue instanceof String){
    //                    String annotationValueStr = (String) annotationValue;
    //                    if (StrUtil.isNotBlank(annotationValueStr)){
    //                        fieldAlias = annotationValueStr;
    //                        break;
    //                    }
    //                }
    //            }
    //
    //            if (StrUtil.isBlank(fieldAlias)){
    //                fieldAlias = fieldName;
    //            }
    //
    //            headerAlias.put(fieldName, fieldAlias);
    //        }
    //        return headerAlias;
    //    }


    /**
     * 获取实体类标注的自定义注解ExcelHeader属性
     * @param entityClass 实体类的class
     * @param headerAliasAnnotationClass 注解的class
     * @param headerToBeanClass 注解属性要转换成bean的Class
     * @return 实体类的class的field的属性对象，以定义顺序排序
     */
    public static <A extends Annotation, T> LinkedHashMap<String, T> getHeaderField(
            Class<?> entityClass,
            Class<A> headerAliasAnnotationClass,
            Class<T> headerToBeanClass) {

        Assert.notNull(entityClass);
        Assert.notNull(headerAliasAnnotationClass);

        Field[] annotationFields = ReflectUtil.getFields(headerAliasAnnotationClass);
        Field[] entityFields = ReflectUtil.getFields(entityClass);

        checkBeanHasContainAnnotationField(annotationFields, ReflectUtil.getFields(headerToBeanClass));

        // 获取model的所有字段,并配置headerAlias
        LinkedHashMap<String, T> headerAlias = new LinkedHashMap<>();
        Arrays.stream(entityFields)
                .filter(field -> AnnotationUtil.hasAnnotation(field, headerAliasAnnotationClass))
                .forEach(field -> {
                    Map<String, Object> annotationValueMap = AnnotationUtil.getAnnotationValueMap(field, headerAliasAnnotationClass);
                    headerAlias.put(field.getName(), BeanUtil.mapToBean(annotationValueMap, headerToBeanClass, true, null));
                });


        return headerAlias;
    }


    /**
     * bean要和注解的属性匹配
     */
    private static void checkBeanHasContainAnnotationField(Field[] annotationFields, Field[] entityFields) {
        Set<String> entityFieldsToCheck = Arrays.stream(entityFields)
                .map(Field::getName)
                .collect(Collectors.toSet());
        Arrays.stream(annotationFields)
                .map(field -> entityFieldsToCheck.contains(field.getName()))
                .forEach(Assert::isTrue);
    }


    public static void createExcelFile(List<?> testExcelModels,
                                       String destFilePath,
                                       LinkedHashMap<String, HeaderField> headerFields) {

        LinkedHashMap<String, String> headerAlias = headerFields.keySet()
                .stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> headerFields.get(key).getValue(),
                        (a, b) -> b, LinkedHashMap::new
                ));

        try (ExcelWriter writer = ExcelUtil.getWriter(destFilePath)) {

            boolean isAppend = writer.getRowCount() != 0;

            if (isAppend) {
                writer.setCurrentRowToEnd();
            } else {
                handleMerge(headerFields, writer);
                writer.setCurrentRowToEnd();

                writer.setHeaderAlias(headerAlias);
                writer.setOnlyAlias(true);
            }

            writer.write(testExcelModels);

            writer.flush();
        }
    }


    /**
     * 处理单元格合并
     */
    public static void handleMerge(LinkedHashMap<String, HeaderField> headerFields, ExcelWriter writer){

        // mergeGroup -> 拥有相同mergeGroup的count
        Map<String, Long> groupAndMergeLength = headerFields
                .values()
                .stream()
                .filter(headerField -> StrUtil.isNotEmpty(headerField.getMergeGroup()))
                .collect(Collectors.groupingBy(HeaderField::getMergeGroup, Collectors.counting()));

        // 转换成：field -> 拥有相同mergeGroup的count
        Map<String, Long> fieldAndMergeLength = headerFields
                .entrySet()
                .stream()
                .filter(entry -> groupAndMergeLength.containsKey(entry.getValue().getMergeGroup()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> groupAndMergeLength.get(entry.getValue().getMergeGroup()),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        Set<String> handledGroup = new HashSet<>();
        int index = 0;
        for (Map.Entry<String, HeaderField> entry : headerFields.entrySet()) {
            String key = entry.getKey();
            HeaderField value = entry.getValue();
            Long mergeLength = fieldAndMergeLength.get(key);

            if (!handledGroup.contains(value.getMergeGroup()) && fieldAndMergeLength.containsKey(key)){

                Assert.isTrue(mergeLength != 1, "请检查field:{}, 合并单元格的长度必须大于1", key);

                // merge
                writer.merge(
                        0, 0, index, index + mergeLength.intValue() - 1,
                        value.getMergeGroup(), false
                );
                handledGroup.add(value.getMergeGroup());
            }
            index++;
        }
    }






    static class AnnotationsPropertyNameUtil {

        private AnnotationsPropertyNameUtil() {
            // Prevent Instantiation of Static Class
        }

        /**
         * 返回对应的字段名
         */
        public static <T, R> String fieldToStr(Func1<T, R> method) {
            // 获取方法名称
            return LambdaUtil.getMethodName(method);
        }

    }


}
