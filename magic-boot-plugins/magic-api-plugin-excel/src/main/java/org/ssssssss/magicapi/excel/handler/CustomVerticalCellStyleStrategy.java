package org.ssssssss.magicapi.excel.handler;

import com.alibaba.excel.metadata.data.DataFormatData;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.AbstractVerticalCellStyleStrategy;
import org.apache.poi.ss.usermodel.*;

/**
 * 3.0版本的easyexcel, 自定义表头和内容的样式
 */
public class CustomVerticalCellStyleStrategy extends AbstractVerticalCellStyleStrategy {

    private boolean errorExcel = false;

    public CustomVerticalCellStyleStrategy() {}

    public CustomVerticalCellStyleStrategy(boolean errorExcel) {
        this.errorExcel = errorExcel;
    }

    @Override
    protected WriteCellStyle headCellStyle(CellWriteHandlerContext context) {

        Cell cell = context.getCell();
        WriteCellStyle headCellStyle = new WriteCellStyle();

        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setBold(true);
        headWriteFont.setFontName("等线");
        headWriteFont.setFontHeightInPoints((short)11);
        headWriteFont.setColor(IndexedColors.WHITE.getIndex());

        headCellStyle.setFillForegroundColor(IndexedColors.BLUE1.getIndex());
        headCellStyle.setWriteFont(headWriteFont);

        headCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);

        // 设置表头的样式
        if (0 == cell.getRowIndex()) {
            headCellStyle.setWrapped(true);
            headCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        } else if (1 == cell.getRowIndex()){
            headCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
            headCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        }
        return headCellStyle;
    }

    @Override
    protected WriteCellStyle contentCellStyle(CellWriteHandlerContext context) {
        Cell cell = context.getCell();
        // 第三列设置为文本格式
        int contentColumnIndex = cell.getColumnIndex();

        WriteCellStyle contentCellStyle = new WriteCellStyle();
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setBold(false);
        headWriteFont.setFontName("等线");
        contentCellStyle.setWriteFont(headWriteFont);

        contentCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        CellType type = cell.getCellType();
        switch (type) {
            case STRING:
                contentCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
                break;
            case BOOLEAN:
                contentCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
                break;
            case NUMERIC:
                contentCellStyle.setHorizontalAlignment(HorizontalAlignment.RIGHT);
                break;
            default:{
                contentCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
                break;
            }
        }

        if (1 == contentColumnIndex) {
            DataFormatData dataFormatData = new DataFormatData();
            dataFormatData.setIndex((short)49);
            contentCellStyle.setDataFormatData(dataFormatData);
        }
        return contentCellStyle;
    }
}
