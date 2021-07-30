package org.tikv.jmh;

import com.google.protobuf.ByteString;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.tikv.common.util.FastByteComparisons;

@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class ComparatorBenchmark {

  @Param({"0", "100", "10000"})
  public int commonPrefix;

  @Param({"10", "10000"})
  public int randomSuffix;

  private ByteString bs1, bs2;

  @Setup
  public void setup() {
    byte[] b1 = new byte[commonPrefix + randomSuffix];
    byte[] b2 = new byte[commonPrefix + randomSuffix];
    ThreadLocalRandom trl = ThreadLocalRandom.current();
    for (int i = 0; i < commonPrefix; i++) {
      b1[i] = b2[i] = (byte) trl.nextInt(256);
    }
    for (int i = 0; i < randomSuffix; i++) {
      byte b = (byte) trl.nextInt(256);
      b1[commonPrefix + i] = b;
      b2[commonPrefix + i] = (byte) (b + trl.nextInt(1, 256));
    }

    bs1 = ByteString.copyFrom(b1);
    bs2 = ByteString.copyFrom(b2);
  }

  @Benchmark
  public int toByteArray() {
    return FastByteComparisons.compareTo(bs1.toByteArray(), bs2.toByteArray());
  }

  @Benchmark
  public int asReadonlyBuffer() {
    return bs1.asReadOnlyByteBuffer().compareTo(bs2.asReadOnlyByteBuffer());
  }

  @Benchmark
  public int unsignedLexicographicalComparator() {
    return ByteString.unsignedLexicographicalComparator().compare(bs1, bs2);
  }
}
