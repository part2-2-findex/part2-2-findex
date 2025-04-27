package com.part2.findex.indexdata.util;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.part2.findex.indexdata.dto.CsvExportDto;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class Csv {
  public byte[] generateCsvWithOpenCsv(List<CsvExportDto> dataList) throws Exception {
    StringWriter writer = new StringWriter();

    StatefulBeanToCsv<CsvExportDto> beanToCsv = new StatefulBeanToCsvBuilder<CsvExportDto>(writer)
        .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)  // 따옴표 안 붙이게 설정
        .withSeparator(',')                           // 기본 구분자 콤마
        .build();

    writer.append("날짜,시가,종가,고가,저가,거래량,대비,등락률\n");
    beanToCsv.write(dataList);

    return ("\uFEFF" + writer.toString()).getBytes(StandardCharsets.UTF_8);
  }
}
