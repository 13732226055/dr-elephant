package com.linkedin.drelephant.analysis.heuristics;

import java.io.IOException;

import com.linkedin.drelephant.analysis.Heuristic;
import com.linkedin.drelephant.analysis.HeuristicResult;
import com.linkedin.drelephant.analysis.Severity;
import com.linkedin.drelephant.hadoop.HadoopCounterHolder;
import com.linkedin.drelephant.hadoop.HadoopJobData;
import com.linkedin.drelephant.hadoop.HadoopTaskData;
import com.linkedin.drelephant.math.Statistics;

import junit.framework.TestCase;


public class ReducerTimeHeuristicTest extends TestCase {
  Heuristic _heuristic = new ReducerTimeHeuristic();
  private static final long MINUTE = Statistics.MINUTE;;

  public void testShortRunetimeCritical() throws IOException {
    assertEquals(Severity.CRITICAL, analyzeJob(1 * MINUTE, 500));
  }

  public void testShortRunetimeSevere() throws IOException {
    assertEquals(Severity.SEVERE, analyzeJob(1 * MINUTE, 200));
  }

  public void testShortRunetimeModerate() throws IOException {
    assertEquals(Severity.MODERATE, analyzeJob(1 * MINUTE, 50));
  }

  public void testShortRunetimeLow() throws IOException {
    assertEquals(Severity.LOW, analyzeJob(1 * MINUTE, 10));
  }

  public void testShortRunetimeNone() throws IOException {
    assertEquals(Severity.NONE, analyzeJob(1 * MINUTE, 2));
  }

  public void testLongRunetimeCritical() throws IOException {
    assertEquals(Severity.CRITICAL, analyzeJob(120 * MINUTE, 10));
  }

  public void testLongRunetimeSevere() throws IOException {
    assertEquals(Severity.SEVERE, analyzeJob(120 * MINUTE, 20));
  }

  public void testLongRunetimeModerate() throws IOException {
    assertEquals(Severity.MODERATE, analyzeJob(120 * MINUTE, 50));
  }

  public void testLongRunetimeLow() throws IOException {
    assertEquals(Severity.LOW, analyzeJob(120 * MINUTE, 100));
  }

  public void testLongRunetimeNone() throws IOException {
    assertEquals(Severity.NONE, analyzeJob(120 * MINUTE, 200));
  }

  private Severity analyzeJob(long runtime, int numTasks) throws IOException {
    HadoopCounterHolder dummyCounter = new HadoopCounterHolder(null);
    HadoopTaskData[] reducers = new HadoopTaskData[numTasks];

    int i = 0;
    for (; i < numTasks; i++) {
      reducers[i] = new HadoopTaskData(dummyCounter, new long[] { 0, runtime, 0, 0 });
    }

    HadoopJobData data = new HadoopJobData().setCounters(dummyCounter).setReducerData(reducers);
    HeuristicResult result = _heuristic.apply(data);
    return result.getSeverity();
  }
}
