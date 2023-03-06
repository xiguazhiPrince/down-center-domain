package com.watermelon.domain.utils;

import java.lang.annotation.*;


/**
 * 标记excel的header
 * {@link HeaderField}
 *
 * @author water
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ExcelHeader {

	/**
	 * 别名值，excel表中的header名称映射
	 *
	 * @return 别名值
	 */
	String value() default "";


	/**
	 * 指定合并单元格的表头，将把相同mergeGroup的列合并, 为空字符串则不会进行合并.
	 * 需要合并的表头需要相邻
	 *
	 * <pre>
	 * 示例
	 * ———————————————————————————————————————————————
	 * |			|	数值列mergeGroup		 |
	 * ———————————————————————————————————————————————
	 * |	名称	|	年龄  |	  	身高cm	|
	 * ———————————————————————————————————————————————
	 * |	water	|	15  |	  	180		|
	 * ———————————————————————————————————————————————
	 * |	green	|	20  |	  	170		|
	 * ———————————————————————————————————————————————
	 *</pre>
	 */
	 String mergeGroup() default "";

}