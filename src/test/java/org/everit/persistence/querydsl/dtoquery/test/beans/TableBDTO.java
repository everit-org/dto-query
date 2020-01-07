package org.everit.persistence.querydsl.dtoquery.test.beans;

import java.util.Collection;
import java.util.Collections;

public class TableBDTO {

  public Long tableAId;

  public long tableBId;

  public String tableBVc;

  public Collection<TableCDTO> tableCList = Collections.emptyList();

  @Override
  public String toString() {
    return "TableBDTO [tableBId=" + this.tableBId + ", tableBVc=" + this.tableBVc + ", tableAId="
        + this.tableAId + ", tableCList=" + this.tableCList + "]";
  }

}
