module de.moritzpetersen.photocopy {
  exports de.moritzpetersen.photocopy;
  exports de.moritzpetersen.photocopy.app.javafx;
  exports de.moritzpetersen.photocopy.config;

  requires javafx.graphics;
  requires static lombok;
  requires factory;
  requires javafx.controls;
  requires metadata.extractor;
  requires com.fasterxml.jackson.annotation;
  requires com.fasterxml.jackson.databind;
  requires java.xml;
  requires org.slf4j;
  requires javax.inject;
}
