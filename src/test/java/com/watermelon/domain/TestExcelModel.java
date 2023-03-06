package com.watermelon.domain;

import com.watermelon.domain.utils.ExcelHeader;
import lombok.Data;

@Data
public class TestExcelModel {

    @ExcelHeader(value = "姓名")
    private String name;

    @ExcelHeader(value = "年龄")
    private String age;

    @ExcelHeader(value = "身高", mergeGroup = "test2")
    private String height;

    @ExcelHeader(value = "班级", mergeGroup = "test2")
    private String className;

    @ExcelHeader(value = "地址")
    private String address;

    public TestExcelModel(String name, String age, String height, String className, String address) {
        this.name = name;
        this.age = age;
        this.height = height;
        this.className = className;
        this.address = address;
    }
}
