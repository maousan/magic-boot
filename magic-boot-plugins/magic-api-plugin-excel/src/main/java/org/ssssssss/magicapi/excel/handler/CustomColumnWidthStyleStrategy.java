package org.ssssssss.magicapi.excel.handler;

import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import org.apache.poi.ss.usermodel.Cell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义列宽策略
 * 自动计算列宽，并确保最小宽度
 * 支持中文字符宽度计算（中文算2个宽度单位，ASCII算1个）
 */
public class CustomColumnWidthStyleStrategy extends AbstractColumnWidthStyleStrategy {

    private final int minColumnWidth;  // 最小列宽（字符数）
    private final int maxColumnWidth;  // 最大列宽（字符数）

    private final Map<Integer, Map<Integer, Integer>> cache = new HashMap<>();

    /**
     * 使用默认的最小和最大宽度
     */
    public CustomColumnWidthStyleStrategy() {
        this(15, 50);
    }

    /**
     * 自定义最小和最大宽度
     *
     * @param minColumnWidth 最小列宽（字符数）
     * @param maxColumnWidth 最大列宽（字符数）
     */
    public CustomColumnWidthStyleStrategy(int minColumnWidth, int maxColumnWidth) {
        this.minColumnWidth = minColumnWidth;
        this.maxColumnWidth = maxColumnWidth;
    }

    @Override
    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        boolean needSetWidth = isHead || (cellDataList != null && !cellDataList.isEmpty());
        if (!needSetWidth) {
            return;
        }

        // 获取 sheet 缓存
        Map<Integer, Integer> maxColumnWidthMap = cache.computeIfAbsent(writeSheetHolder.getSheetNo(), k -> new HashMap<>());

        Integer columnWidth = this.dataLength(cellDataList, cell, isHead);
        if (columnWidth < 0) {
            return;
        }

        // 确保最小宽度
        columnWidth = Math.max(columnWidth, minColumnWidth);

        // 限制最大宽度
        columnWidth = Math.min(columnWidth, maxColumnWidth);

        Integer maxColumnWidth = maxColumnWidthMap.get(head.getColumnIndex());
        if (maxColumnWidth == null || columnWidth > maxColumnWidth) {
            maxColumnWidthMap.put(head.getColumnIndex(), columnWidth);
            writeSheetHolder.getSheet().setColumnWidth(head.getColumnIndex(), columnWidth * 256);
        }
    }

    /**
     * 计算数据长度（考虑中文字符宽度）
     * 中文字符算2个宽度单位，ASCII字符算1个宽度单位
     */
    private Integer dataLength(List<WriteCellData<?>> cellDataList, Cell cell, Boolean isHead) {
        if (isHead) {
            return calculateDisplayWidth(cell.getStringCellValue());
        } else {
            WriteCellData<?> cellData = cellDataList.get(0);
            CellDataTypeEnum type = cellData.getType();
            if (type == null) {
                return -1;
            }
            if (type == CellDataTypeEnum.STRING) {
                return calculateDisplayWidth(cellData.getStringValue());
            } else if (type == CellDataTypeEnum.BOOLEAN) {
                return calculateDisplayWidth(cellData.getBooleanValue().toString());
            } else if (type == CellDataTypeEnum.NUMBER) {
                return calculateDisplayWidth(cellData.getNumberValue().toString());
            } else if (type == CellDataTypeEnum.DATE) {
                return 20;  // 日期格式固定宽度
            } else {
                return -1;
            }
        }
    }

    /**
     * 计算字符串的显示宽度
     * 中文字符、全角字符算2个宽度，ASCII字符算1个宽度
     */
    private int calculateDisplayWidth(String str) {
        if (str == null) {
            return 0;
        }

        int width = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            // 判断是否为中文字符或全角字符
            if (isFullWidthCharacter(c)) {
                width += 2;
            } else {
                width += 1;
            }
        }

        return width;
    }

    /**
     * 判断字符是否为全角字符（中文、全角标点、全角字母数字等）
     */
    private boolean isFullWidthCharacter(char c) {
        // 中文字符范围
        if (c >= '\u4E00' && c <= '\u9FA5') {
            return true;
        }
        // 全角标点符号和全角字母数字
        if (c >= '\uFF00' && c <= '\uFFEF') {
            return true;
        }
        // CJK 统一汉字扩展
        if (c >= '\u3400' && c <= '\u4DBF') {
            return true;
        }
        // CJK 兼容汉字
        if (c >= '\uF900' && c <= '\uFAFF') {
            return true;
        }
        return false;
    }
}
