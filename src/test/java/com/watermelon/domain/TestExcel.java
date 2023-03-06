package com.watermelon.domain;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.lang.Tuple;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.watermelon.domain.utils.ExcelHeader;
import com.watermelon.domain.utils.ExcelUtilsX;
import com.watermelon.domain.utils.HeaderField;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class TestExcel {


    @Test
    void testExcel() {
        LinkedHashMap<String, HeaderField> headerAlias = ExcelUtilsX.getHeaderField(TestExcelModel.class, ExcelHeader.class, HeaderField.class);

        String tempPath = "/temp/"+ UUID.randomUUID().toString(true) +".xlsx";
        List<TestExcelModel> testExcelModels;

        for (int j = 0; j < 2; j++) {

            testExcelModels = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                testExcelModels.add(new TestExcelModel(
                        j+"nn"+i,j+"aa"+i, j+"hei"+i,
                        j+"class"+i, j+"address"+i
                ));
            }
            System.out.println(j);
            createExcelFile(testExcelModels, tempPath, headerAlias);
        }
        FileUtil.del(tempPath);

    }

    private static void createExcelFile(List<TestExcelModel> testExcelModels,
                                        String tempFile,
                                        LinkedHashMap<String, HeaderField> headerFields) {


        LinkedHashMap<String, String> headerAlias = headerFields.keySet()
                .stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> headerFields.get(key).getValue(),
                        (a, b) -> b, LinkedHashMap::new
                ));

        //通过工具类创建writer
        try (ExcelWriter writer = ExcelUtil.getWriter(tempFile)) {
            boolean isAppend = writer.getRowCount() != 0;
            if (isAppend) {
                writer.setCurrentRowToEnd();
            } else {
                ExcelUtilsX.handleMerge(headerFields, writer);
                writer.setCurrentRowToEnd();

                writer.setHeaderAlias(headerAlias);
                writer.setOnlyAlias(true);
            }

            writer.write(testExcelModels, isAppend == false);

            writer.flush();
        }
    }

}
