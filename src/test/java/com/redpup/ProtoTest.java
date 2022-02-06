package com.redpup;

import static com.google.common.truth.Truth.assertThat;

import com.redpup.proto.FooMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class ProtoTest {

  @Test
  public void protoSanityTest() {
    assertThat(FooMessage.newBuilder().setValue("foo").build().getValue())
        .isEqualTo("foo");
  }
}
