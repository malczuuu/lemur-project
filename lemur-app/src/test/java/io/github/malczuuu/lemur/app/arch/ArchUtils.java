package io.github.malczuuu.lemur.app.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

final class ArchUtils {

  public static JavaClasses getAppClasses() {
    return Holder.CLASSES;
  }

  private ArchUtils() {}

  private static final class Holder {
    private static final JavaClasses CLASSES =
        new ClassFileImporter().importPackages("io.github.malczuuu.lemur.app..");
  }
}
