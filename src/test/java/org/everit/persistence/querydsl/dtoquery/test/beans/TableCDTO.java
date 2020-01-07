/*
 * Copyright © 2011 Everit Kft. (http://www.everit.biz)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
