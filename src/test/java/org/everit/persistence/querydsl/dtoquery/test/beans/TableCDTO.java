package org.everit.persistence.querydsl.dtoquery.test.beans;

public class TableCDTO {

  public Long tableBId;

  public long tableCId;

  public String tableCVc;

  @Override
  public String toString() {
    return "TableCDTO [tableCId=" + this.tableCId + ", tableCVc=" + this.tableCVc + ", tableBId="
        + this.tableBId
        + "]";
  }

}
