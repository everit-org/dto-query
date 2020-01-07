package org.everit.persistence.querydsl.dtoquery.test.beans;

import java.util.Collection;
import java.util.Collections;

public class TableADTO {

  public long tableAId;

  public String tableAVc;

  public Collection<TableBDTO> tableBList = Collections.emptyList();

  @Override
  public String toString() {
    return "TableADTO [tableAId=" + this.tableAId + ", tableAVc=" + this.tableAVc + ", tableBList="
        + this.tableBList + "]";
  }

}
