package uk.ac.ic.doc.blocc;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;

class MainTest {

  @Test
  void add() {
    assertThat(Main.add(1, 2)).isEqualTo(3);
  }

  @Test
  void mainPrintsHelloWorld() {
    ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStreamCaptor));

    Main.main(null);

    assertThat(outputStreamCaptor.toString()).isEqualTo("Hello world!\n");
  }
}
