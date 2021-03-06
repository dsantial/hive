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

package org.apache.hadoop.hive.ql.plan;

import java.io.Serializable;
import java.util.Map;

import org.apache.hadoop.fs.Path;

/**
 * LockTableDesc.
 *
 */
@Explain(displayName = "Lock Table")
public class LockTableDesc extends DDLDesc implements Serializable {
  private static final long serialVersionUID = 1L;

  private String tableName;
  private String mode;
  private Map<String, String> partSpec;
  private String queryId;

  public LockTableDesc() {
  }

  public LockTableDesc(String tableName, String mode, Map<String, String> partSpec, String queryId) {
    this.tableName = tableName;
    this.mode      = mode;
    this.partSpec  = partSpec;
    this.queryId   = queryId;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public String getMode() {
    return mode;
  }

  public Map<String, String> getPartSpec() {
    return partSpec;
  }

  public void setPartSpec(Map<String, String> partSpec) {
    this.partSpec = partSpec;
  }

  public String getQueryId() {
    return queryId;
  }

  public void setQueryId(String queryId) {
    this.queryId = queryId;
  }
}
