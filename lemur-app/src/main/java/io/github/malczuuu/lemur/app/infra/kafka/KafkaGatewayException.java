package io.github.malczuuu.lemur.app.infra.kafka;

public class KafkaGatewayException extends RuntimeException {

  public KafkaGatewayException(String message, Throwable cause) {
    super(message, cause);
  }
}
