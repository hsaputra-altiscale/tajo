/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.tajo.catalog.statistics;

import org.apache.tajo.catalog.Column;
import org.apache.tajo.catalog.json.CatalogGsonHelper;
import org.apache.tajo.common.TajoDataTypes.Type;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestTableStat {
  @Test
  public final void testTableStat() throws CloneNotSupportedException {
    TableStats stat = new TableStats();
    stat.setNumRows(957685);
    stat.setNumBytes(1023234);
    stat.setNumBlocks(3123);
    stat.setNumShuffleOutputs(5);
    stat.setAvgRows(80000);
        
    int numCols = 3;
    ColumnStats[] cols = new ColumnStats[numCols];
    for (int i = 0; i < numCols; i++) {
      cols[i] = new ColumnStats(new Column("col_" + i, Type.INT8));
      cols[i].setNumDistVals(1024 * i);
      cols[i].setNumNulls(100 * i);
      stat.addColumnStat(cols[i]);
    }
    
    assertTrue(957685 == stat.getNumRows());
    assertTrue(1023234 == stat.getNumBytes());
    assertTrue(3123 == stat.getNumBlocks());
    assertTrue(5 == stat.getNumShuffleOutputs());
    assertTrue(80000 == stat.getAvgRows());
    assertEquals(3, stat.getColumnStats().size());
    for (int i = 0; i < numCols; i++) {
      assertEquals(cols[i], stat.getColumnStats().get(i));
    }
    
    TableStats stat2 = new TableStats(stat.getProto());
    tableStatEquals(stat, stat2);
    
    TableStats stat3 = (TableStats) stat.clone();
    tableStatEquals(stat, stat3);

    String json = stat.toJson();
    TableStats fromJson = CatalogGsonHelper.fromJson(json, TableStats.class);
    tableStatEquals(stat, fromJson);
  }
  
  public void tableStatEquals(TableStats s1, TableStats s2) {
    assertEquals(s1.getNumRows(), s2.getNumRows());
    assertEquals(s1.getNumBlocks(), s2.getNumBlocks());
    assertEquals(s1.getNumShuffleOutputs(), s2.getNumShuffleOutputs());
    assertEquals(s1.getAvgRows(), s2.getAvgRows());
    assertEquals(s1.getColumnStats().size(), s2.getColumnStats().size());
    for (int i = 0; i < s1.getColumnStats().size(); i++) {
      assertEquals(s1.getColumnStats().get(i), s2.getColumnStats().get(i));
    }
  }
}
