/*
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

package org.apache.hadoop.hive.ql.exec.vector.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.hadoop.hive.common.type.HiveChar;
import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluator;
import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluatorFactory;
import org.apache.hadoop.hive.ql.exec.vector.VectorExtractRow;
import org.apache.hadoop.hive.ql.exec.vector.VectorRandomBatchSource;
import org.apache.hadoop.hive.ql.exec.vector.VectorRandomRowSource;
import org.apache.hadoop.hive.ql.exec.vector.VectorizationContext;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedBatchUtil;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatchCtx;
import org.apache.hadoop.hive.ql.exec.vector.VectorRandomRowSource.GenerationSpec;
import org.apache.hadoop.hive.ql.exec.vector.expressions.IdentityExpression;
import org.apache.hadoop.hive.ql.exec.vector.expressions.VectorExpression;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.metadata.VirtualColumn;
import org.apache.hadoop.hive.ql.plan.ExprNodeColumnDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeConstantDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeGenericFuncDesc;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFIf;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFWhen;
import org.apache.hadoop.hive.serde2.io.ByteWritable;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.hive.serde2.io.HiveCharWritable;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.serde2.io.HiveIntervalDayTimeWritable;
import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;
import org.apache.hadoop.hive.serde2.io.HiveVarcharWritable;
import org.apache.hadoop.hive.serde2.io.ShortWritable;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils.ObjectInspectorCopyOption;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory;
import org.apache.hadoop.hive.serde2.typeinfo.CharTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.apache.hadoop.hive.serde2.typeinfo.VarcharTypeInfo;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import junit.framework.Assert;

import org.apache.hadoop.io.Writable;
import org.junit.Ignore;
import org.junit.Test;

public class TestVectorCastStatement {

  @Test
  public void testBoolean() throws Exception {
    Random random = new Random(12882);

    doIfTests(random, "boolean");
  }

  @Test
  public void testTinyInt() throws Exception {
    Random random = new Random(5371);

    doIfTests(random, "tinyint");
  }

  @Test
  public void testSmallInt() throws Exception {
    Random random = new Random(2772);

    doIfTests(random, "smallint");
  }

  @Test
  public void testInt() throws Exception {
    Random random = new Random(12882);

    doIfTests(random, "int");
  }

  @Test
  public void testBigInt() throws Exception {
    Random random = new Random(12882);

    doIfTests(random, "bigint");
  }

  @Test
  public void testString() throws Exception {
    Random random = new Random(12882);

    doIfTests(random, "string");
  }

  @Test
  public void testTimestamp() throws Exception {
    Random random = new Random(12882);

    doIfTests(random, "timestamp");
  }

  @Test
  public void testDate() throws Exception {
    Random random = new Random(12882);

    doIfTests(random, "date");
  }

  @Test
  public void testFloat() throws Exception {
    Random random = new Random(7322);

    doIfTests(random, "float");
  }

  @Test
  public void testDouble() throws Exception {
    Random random = new Random(12882);

    doIfTests(random, "double");
  }

  @Test
  public void testChar() throws Exception {
    Random random = new Random(12882);

    doIfTests(random, "char(10)");
  }

  @Test
  public void testVarchar() throws Exception {
    Random random = new Random(12882);

    doIfTests(random, "varchar(15)");
  }

  @Test
  public void testBinary() throws Exception {
    Random random = new Random(12882);

    doIfTests(random, "binary");
  }

  @Test
  public void testDecimal() throws Exception {
    Random random = new Random(9300);

    doIfTests(random, "decimal(38,18)");
    doIfTests(random, "decimal(38,0)");
    doIfTests(random, "decimal(20,8)");
    doIfTests(random, "decimal(10,4)");
  }

  public enum CastStmtTestMode {
    ROW_MODE,
    ADAPTOR,
    VECTOR_EXPRESSION;

    static final int count = values().length;
  }

  private void doIfTests(Random random, String typeName)
          throws Exception {

    TypeInfo typeInfo = TypeInfoUtils.getTypeInfoFromTypeString(typeName);
    PrimitiveCategory primitiveCategory = ((PrimitiveTypeInfo) typeInfo).getPrimitiveCategory();

    for (PrimitiveCategory targetPrimitiveCategory : PrimitiveCategory.values()) {

      if (targetPrimitiveCategory == PrimitiveCategory.VOID ||
          targetPrimitiveCategory == PrimitiveCategory.INTERVAL_YEAR_MONTH ||
          targetPrimitiveCategory == PrimitiveCategory.INTERVAL_DAY_TIME ||
          targetPrimitiveCategory == PrimitiveCategory.UNKNOWN) {
        continue;
      }

      // BINARY conversions supported by GenericUDFDecimal, GenericUDFTimestamp.
      if (primitiveCategory == PrimitiveCategory.BINARY) {
        if (targetPrimitiveCategory == PrimitiveCategory.DECIMAL ||
            targetPrimitiveCategory == PrimitiveCategory.TIMESTAMP) {
          continue;
        }
      }

      // DATE conversions supported by GenericUDFDecimal.
      if (primitiveCategory == PrimitiveCategory.DATE) {
        if (targetPrimitiveCategory == PrimitiveCategory.DECIMAL) {
          continue;
        }
      }

      if (primitiveCategory == targetPrimitiveCategory) {
        if (primitiveCategory != PrimitiveCategory.CHAR &&
            primitiveCategory != PrimitiveCategory.VARCHAR &&
            primitiveCategory != PrimitiveCategory.DECIMAL) {
          continue;
        }
      }

      doIfTestOneCast(random, typeName, targetPrimitiveCategory);
    }
  }

  private boolean needsValidDataTypeData(TypeInfo typeInfo) {
    PrimitiveCategory primitiveCategory = ((PrimitiveTypeInfo) typeInfo).getPrimitiveCategory();
    if (primitiveCategory == PrimitiveCategory.STRING ||
        primitiveCategory == PrimitiveCategory.CHAR ||
        primitiveCategory == PrimitiveCategory.VARCHAR ||
        primitiveCategory == PrimitiveCategory.BINARY) {
      return false;
    }
    return true;
  }

  private void doIfTestOneCast(Random random, String typeName,
      PrimitiveCategory targetPrimitiveCategory)
          throws Exception {

    TypeInfo typeInfo = TypeInfoUtils.getTypeInfoFromTypeString(typeName);
    PrimitiveCategory primitiveCategory = ((PrimitiveTypeInfo) typeInfo).getPrimitiveCategory();

    //----------------------------------------------------------------------------------------------

    String targetTypeName;
    if (targetPrimitiveCategory == PrimitiveCategory.BYTE) {
      targetTypeName = "tinyint";
    } else if (targetPrimitiveCategory == PrimitiveCategory.SHORT) {
      targetTypeName = "smallint";
    } else if (targetPrimitiveCategory == PrimitiveCategory.LONG) {
      targetTypeName = "bigint";
    } else {
      targetTypeName = targetPrimitiveCategory.name().toLowerCase();
    }
    targetTypeName = VectorRandomRowSource.getDecoratedTypeName(random, targetTypeName);
    TypeInfo targetTypeInfo = TypeInfoUtils.getTypeInfoFromTypeString(targetTypeName);

    //----------------------------------------------------------------------------------------------

    GenerationSpec generationSpec;
    if (needsValidDataTypeData(targetTypeInfo) &&
        (primitiveCategory == PrimitiveCategory.STRING ||
         primitiveCategory == PrimitiveCategory.CHAR ||
         primitiveCategory == PrimitiveCategory.VARCHAR)) {
      generationSpec = GenerationSpec.createStringFamilyOtherTypeValue(typeInfo, targetTypeInfo);
    } else {
      generationSpec = GenerationSpec.createSameType(typeInfo);
    }

    List<GenerationSpec> generationSpecList = new ArrayList<GenerationSpec>();
    generationSpecList.add(generationSpec);

    VectorRandomRowSource rowSource = new VectorRandomRowSource();

    rowSource.initGenerationSpecSchema(
        random, generationSpecList, /* maxComplexDepth */ 0, /* allowNull */ true);

    List<String> columns = new ArrayList<String>();
    columns.add("col0");
    ExprNodeColumnDesc col1Expr = new ExprNodeColumnDesc(typeInfo, "col0", "table", false);

    List<ExprNodeDesc> children = new ArrayList<ExprNodeDesc>();
    children.add(col1Expr);

    String[] columnNames = columns.toArray(new String[0]);

    Object[][] randomRows = rowSource.randomRows(100000);

    VectorRandomBatchSource batchSource =
        VectorRandomBatchSource.createInterestingBatches(
            random,
            rowSource,
            randomRows,
            null);

    final int rowCount = randomRows.length;
    Object[][] resultObjectsArray = new Object[CastStmtTestMode.count][];
    for (int i = 0; i < CastStmtTestMode.count; i++) {

      Object[] resultObjects = new Object[rowCount];
      resultObjectsArray[i] = resultObjects;

      CastStmtTestMode ifStmtTestMode = CastStmtTestMode.values()[i];
      switch (ifStmtTestMode) {
      case ROW_MODE:
        if (!doRowCastTest(
              typeInfo,
              targetTypeInfo,
              columns,
              children,
              randomRows,
              rowSource.rowStructObjectInspector(),
              resultObjects)) {
          return;
        }
        break;
      case ADAPTOR:
      case VECTOR_EXPRESSION:
        if (!doVectorCastTest(
              typeInfo,
              targetTypeInfo,
              columns,
              columnNames,
              rowSource.typeInfos(),
              children,
              ifStmtTestMode,
              batchSource,
              resultObjects)) {
          return;
        }
        break;
      default:
        throw new RuntimeException("Unexpected IF statement test mode " + ifStmtTestMode);
      }
    }

    for (int i = 0; i < rowCount; i++) {
      // Row-mode is the expected value.
      Object expectedResult = resultObjectsArray[0][i];

      for (int v = 1; v < CastStmtTestMode.count; v++) {
        Object vectorResult = resultObjectsArray[v][i];
        if (expectedResult == null || vectorResult == null) {
          if (expectedResult != null || vectorResult != null) {
            Assert.fail(
                "Row " + i +
                " sourceTypeName " + typeName +
                " targetTypeName " + targetTypeName +
                " " + CastStmtTestMode.values()[v] +
                " result is NULL " + (vectorResult == null ? "YES" : "NO") +
                " does not match row-mode expected result is NULL " +
                (expectedResult == null ? "YES" : "NO"));
          }
        } else {

          if (!expectedResult.equals(vectorResult)) {
            Assert.fail(
                "Row " + i +
                " sourceTypeName " + typeName +
                " targetTypeName " + targetTypeName +
                " " + CastStmtTestMode.values()[v] +
                " result " + vectorResult.toString() +
                " (" + vectorResult.getClass().getSimpleName() + ")" +
                " does not match row-mode expected result " + expectedResult.toString() +
                " (" + expectedResult.getClass().getSimpleName() + ")");
          }
        }
      }
    }
  }

  private boolean doRowCastTest(TypeInfo typeInfo, TypeInfo targetTypeInfo,
      List<String> columns, List<ExprNodeDesc> children,
      Object[][] randomRows, ObjectInspector rowInspector, Object[] resultObjects)
          throws Exception {

    GenericUDF udf;
    try {
      udf = VectorizationContext.getGenericUDFForCast(targetTypeInfo);
    } catch (HiveException e) {
      return false;
    }

    ExprNodeGenericFuncDesc exprDesc =
        new ExprNodeGenericFuncDesc(targetTypeInfo, udf, children);

    /*
    System.out.println(
        "*DEBUG* typeInfo " + typeInfo.toString() +
        " targetTypeInfo " + targetTypeInfo +
        " castStmtTestMode ROW_MODE" +
        " exprDesc " + exprDesc.toString());
    */

    ExprNodeEvaluator evaluator =
        ExprNodeEvaluatorFactory.get(exprDesc);
    try {
        evaluator.initialize(rowInspector);
    } catch (HiveException e) {
      return false;
    }

    ObjectInspector objectInspector = TypeInfoUtils
        .getStandardWritableObjectInspectorFromTypeInfo(targetTypeInfo);

    final int rowCount = randomRows.length;
    for (int i = 0; i < rowCount; i++) {
      Object[] row = randomRows[i];
      Object result = evaluator.evaluate(row);
      Object copyResult =
          ObjectInspectorUtils.copyToStandardObject(
              result, objectInspector, ObjectInspectorCopyOption.WRITABLE);
      resultObjects[i] = copyResult;
    }

    return true;
  }

  private void extractResultObjects(VectorizedRowBatch batch, int rowIndex,
      VectorExtractRow resultVectorExtractRow, Object[] scrqtchRow, Object[] resultObjects,
      TypeInfo targetTypeInfo, int outputColIndex) {

    boolean selectedInUse = batch.selectedInUse;
    int[] selected = batch.selected;
    for (int logicalIndex = 0; logicalIndex < batch.size; logicalIndex++) {
      final int batchIndex = (selectedInUse ? selected[logicalIndex] : logicalIndex);
      resultVectorExtractRow.extractRow(batch, batchIndex, scrqtchRow);
      resultObjects[rowIndex++] = getCopyOf(scrqtchRow[0], targetTypeInfo);
    }
  }

  private Object getCopyOf(Object o, TypeInfo info) {
    if (info instanceof PrimitiveTypeInfo) {
      if (o == null) {
        return o;
      }
      PrimitiveCategory category = ((PrimitiveTypeInfo) info).getPrimitiveCategory();
      switch (category) {
      case VOID:
        return null;
      case BOOLEAN:
        return new BooleanWritable(((BooleanWritable) o).get());
      case BYTE:
        return new ByteWritable(((ByteWritable) o).get());
      case SHORT:
        return new ShortWritable(((ShortWritable) o).get());
      case INT:
        return new IntWritable(((IntWritable) o).get());
      case LONG:
        return new LongWritable(((LongWritable) o).get());
      case TIMESTAMP:
        return new TimestampWritable(((TimestampWritable) o));
      case DATE:
        return new DateWritable(((DateWritable) o).get());
      case FLOAT:
        return new FloatWritable(((FloatWritable) o).get());
      case DOUBLE:
        return new DoubleWritable(((DoubleWritable) o).get());
      case BINARY:
        return new BytesWritable(((BytesWritable) o).copyBytes());
      case STRING:
        Text ret = new Text();
        ret.set(((Text) o).getBytes(), 0, ((Text) o).getLength());
        return ret;
      case VARCHAR:
        return new HiveVarcharWritable(((HiveVarcharWritable) o));
      case CHAR:
        return new HiveCharWritable(((HiveCharWritable) o));
      case DECIMAL:
        return new HiveDecimalWritable(((HiveDecimalWritable) o));
      case INTERVAL_YEAR_MONTH:
        return new HiveIntervalYearMonthWritable(((HiveIntervalYearMonthWritable) o));
      case INTERVAL_DAY_TIME:
        return new HiveIntervalDayTimeWritable(((HiveIntervalDayTimeWritable) o));
      default:
        throw new RuntimeException("Unexpected type found" + info.getTypeName());
      }
    }
    throw new RuntimeException("Unexpected type found" + info.getTypeName());
  }

  private boolean doVectorCastTest(TypeInfo typeInfo, TypeInfo targetTypeInfo,
      List<String> columns, String[] columnNames,
      TypeInfo[] typeInfos,
      List<ExprNodeDesc> children,
      CastStmtTestMode castStmtTestMode,
      VectorRandomBatchSource batchSource,
      Object[] resultObjects)
          throws Exception {

    GenericUDF udf;
    try {
      udf = VectorizationContext.getGenericUDFForCast(targetTypeInfo);
    } catch (HiveException e) {
      return false;
    }

    ExprNodeGenericFuncDesc exprDesc =
        new ExprNodeGenericFuncDesc(targetTypeInfo, udf, children);

    HiveConf hiveConf = new HiveConf();
    if (castStmtTestMode == CastStmtTestMode.ADAPTOR) {
      hiveConf.setBoolVar(HiveConf.ConfVars.HIVE_TEST_VECTOR_ADAPTOR_OVERRIDE, true);
    }

    VectorizationContext vectorizationContext =
        new VectorizationContext(
            "name",
            columns,
            hiveConf);
    VectorExpression vectorExpression = vectorizationContext.getVectorExpression(exprDesc);

    /*
    System.out.println(
        "*DEBUG* typeInfo " + typeInfo.toString() +
        " targetTypeInfo " + targetTypeInfo +
        " castStmtTestMode " + castStmtTestMode +
        " vectorExpression " + vectorExpression.toString());
    */

    VectorRandomRowSource rowSource = batchSource.getRowSource();
    VectorizedRowBatchCtx batchContext =
        new VectorizedRowBatchCtx(
            columnNames,
            rowSource.typeInfos(),
            /* dataColumnNums */ null,
            /* partitionColumnCount */ 0,
            vectorizationContext.getScratchColumnTypeNames());

    VectorizedRowBatch batch = batchContext.createVectorizedRowBatch();

    VectorExtractRow resultVectorExtractRow = new VectorExtractRow();

    resultVectorExtractRow
        .init(new TypeInfo[] { targetTypeInfo }, new int[] { vectorExpression.getOutputColumn() });
    Object[] scrqtchRow = new Object[1];

    batchSource.resetBatchIteration();
    int rowIndex = 0;
    while (true) {
      if (!batchSource.fillNextBatch(batch)) {
        break;
      }
      vectorExpression.evaluate(batch);
      extractResultObjects(batch, rowIndex, resultVectorExtractRow, scrqtchRow, resultObjects,
          targetTypeInfo, vectorExpression.getOutputColumn());
      rowIndex += batch.size;
    }

    return true;
  }
}
