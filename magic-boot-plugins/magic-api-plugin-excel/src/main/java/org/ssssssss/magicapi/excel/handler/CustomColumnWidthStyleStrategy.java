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
 */
public class CustomColumnWidthStyleStrategy extends AbstractColumnWidthStyleStrategy {

    private static final int MIN_COLUMN_WIDTH = 15;  // 最小列宽（字符数）
    private static final int MAX_COLUMN_WIDTH = 50;  // 最大列宽（字符数）

    private final Map<Integer, Map<Integer, Integer>> cache = new HashMap<>();

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
        columnWidth = Math.max(columnWidth, MIN_COLUMN_WIDTH);

        // 限制最大宽度
        columnWidth = Math.min(columnWidth, MAX_COLUMN_WIDTH);

        Integer maxColumnWidth = maxColumnWidthMap.get(head.getColumnIndex());
        if (maxColumnWidth == null || columnWidth > maxColumnWidth) {
            maxColumnWidthMap.put(head.getColumnIndex(), columnWidth);
            writeSheetHolder.getSheet().setColumnWidth(head.getColumnIndex(), columnWidth * 256);
        }
    }

    /**
     * 计算数据长度
     */
    private Integer dataLength(List<WriteCellData<?>> cellDataList, Cell cell, Boolean isHead) {
        if (isHead) {
            return cell.getStringCellValue().getBytes().length;
        } else {
            WriteCellData<?> cellData = cellDataList.get(0);
            CellDataTypeEnum type = cellData.getType();
            if (type == null) {
                return -1;
            }
            if (type == CellDataTypeEnum.STRING) {
                return cellData.getStringValue().getBytes().length;
            } else if (type == CellDataTypeEnum.BOOLEAN) {
                return cellData.getBooleanValue().toString().getBytes().length;
            } else if (type == CellDataTypeEnum.NUMBER) {
                return cellData.getNumberValue().toString().getBytes().length;
            } else if (type == CellDataTypeEnum.DATE) {
                return 20;  // 日期格式固定宽度
            } else {
                return -1;
            }
        }
    }
}
